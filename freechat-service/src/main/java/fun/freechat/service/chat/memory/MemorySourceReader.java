package fun.freechat.service.chat.memory;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import fun.freechat.model.ChatHistory;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MemorySourceReader {
    private final MemoryTurnRepository turns;

    public MemorySourceReader(MemoryTurnRepository turns) {
        this.turns = turns;
    }

    public Optional<Turn> nextTurn(MemoryScope scope, long afterId, long throughId) {
        List<ChatHistory> terminals = turns.finalizedPage(scope, afterId, throughId, 1);
        if (terminals.isEmpty()) {
            return Optional.empty();
        }
        ChatHistory terminal = terminals.getFirst();
        require(MemoryDocumentCodec.canonicalUuid(terminal.getTurnId()));
        long cursor = afterId;
        ChatHistory start;
        do {
            List<ChatHistory> starts = turns.sourcePage(scope, cursor, terminal.getId(), 1);
            require(!starts.isEmpty());
            start = starts.getFirst();
            cursor = start.getId();
        } while (trustedPrefix(start));
        boolean singleAbort = start.getId().equals(terminal.getId())
                && "turn-abort".equals(start.getRecordKind())
                && start.getMessage() != null;
        require(("turn-start".equals(start.getRecordKind()) || singleAbort)
                && terminal.getTurnId().equals(start.getTurnId())
                && Objects.equals(terminal.getEpisode(), start.getEpisode()));
        return Optional.of(new Turn(
                afterId,
                terminal.getId(),
                terminal.getTurnId(),
                terminal.getEpisode(),
                terminal.getGmtCreate().toInstant(ZoneOffset.UTC),
                "turn-abort".equals(terminal.getRecordKind())));
    }

    public Fragment nextFragment(MemoryScope scope, Turn turn, Position position, MemoryBounds bounds, int tokenLimit) {
        require(position.rowId() >= turn.afterId() && position.rowId() <= turn.throughId() && position.offset() >= 0);
        if (turn.aborted()) {
            return new Fragment(null, new Position(turn.throughId(), 0), true);
        }
        long after = position.offset() == 0 ? position.rowId() : position.rowId() - 1;
        int offset = position.offset();
        boolean prefix = position.rowId() == turn.afterId() && offset == 0;
        while (after < turn.throughId()) {
            List<ChatHistory> rows = turns.sourcePage(scope, after, turn.throughId(), 1);
            require(!rows.isEmpty());
            ChatHistory row = rows.getFirst();
            if (prefix && trustedPrefix(row)) {
                after = row.getId();
                continue;
            }
            prefix = false;
            require(turn.turnId().equals(row.getTurnId()) && Objects.equals(turn.episode(), row.getEpisode()));
            require(offset == 0 || row.getId() == position.rowId());
            if ("turn-complete".equals(row.getRecordKind())) {
                require(row.getId() == turn.throughId());
                if (row.getMessage() == null) {
                    require(offset == 0);
                    return new Fragment(null, new Position(turn.throughId(), 0), true);
                }
            } else if (!"message".equals(row.getRecordKind())) {
                require("turn-start".equals(row.getRecordKind()));
                if (row.getMessage() == null) {
                    require(offset == 0);
                    after = row.getId();
                    continue;
                }
            }
            String text = evidenceText(row);
            if (text == null || text.isBlank()) {
                require(offset == 0);
                after = row.getId();
                continue;
            }
            for (int index = 0; index < text.length(); index++) {
                char value = text.charAt(index);
                if (Character.isHighSurrogate(value)) {
                    require(++index < text.length() && Character.isLowSurrogate(text.charAt(index)));
                } else {
                    require(!Character.isLowSurrogate(value));
                }
            }
            MemoryBounds.Page page = bounds.fragment(text, offset, tokenLimit);
            require(!page.text().isEmpty());
            Evidence evidence = new Evidence(
                    row.getId(),
                    row.getMessageOrigin(),
                    row.getSourceMessage() != null,
                    row.getGmtCreate().toInstant(ZoneOffset.UTC),
                    offset,
                    page.text());
            Position next = new Position(row.getId(), page.nextOffset() == null ? 0 : page.nextOffset());
            return new Fragment(evidence, next, false);
        }
        return new Fragment(null, new Position(turn.throughId(), 0), true);
    }

    private static String evidenceText(ChatHistory row) {
        try {
            String origin = row.getMessageOrigin();
            if (MemoryTurnRepository.Origin.SYSTEM.text().equals(origin)
                    || MemoryTurnRepository.Origin.TEMPLATE_EXAMPLE.text().equals(origin)) {
                return null;
            }
            ChatMessage message = ChatMessageDeserializer.messageFromJson(
                    row.getSourceMessage() == null ? row.getMessage() : row.getSourceMessage());
            if ("turn-start".equals(row.getRecordKind())) {
                require(MemoryTurnRepository.Origin.USER_INPUT.text().equals(origin) && message instanceof UserMessage);
            } else if ("turn-complete".equals(row.getRecordKind())) {
                require(MemoryTurnRepository.Origin.ASSISTANT_OUTPUT.text().equals(origin)
                        && message instanceof AiMessage ai
                        && !ai.hasToolExecutionRequests());
            }
            if (MemoryTurnRepository.Origin.USER_INPUT.text().equals(origin) && message instanceof UserMessage user) {
                return user.contents().stream()
                        .filter(TextContent.class::isInstance)
                        .map(TextContent.class::cast)
                        .map(TextContent::text)
                        .collect(Collectors.joining("\n"));
            }
            if (MemoryTurnRepository.Origin.ASSISTANT_OUTPUT.text().equals(origin) && message instanceof AiMessage ai) {
                return ai.text();
            }
            if (MemoryTurnRepository.Origin.TOOL.text().equals(origin)) {
                if (message instanceof ToolExecutionResultMessage result) {
                    return result.text();
                }
                if (message instanceof AiMessage ai && ai.hasToolExecutionRequests()) {
                    return null;
                }
            }
            throw invalid();
        } catch (RuntimeException ignored) {
            throw invalid();
        }
    }

    private static boolean trustedPrefix(ChatHistory row) {
        return row.getTurnId() == null
                && "message".equals(row.getRecordKind())
                && (MemoryTurnRepository.Origin.SYSTEM.text().equals(row.getMessageOrigin())
                        || MemoryTurnRepository.Origin.TEMPLATE_EXAMPLE.text().equals(row.getMessageOrigin()));
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw invalid();
        }
    }

    private static IllegalStateException invalid() {
        return new IllegalStateException("Invalid finalized memory source");
    }

    public record Turn(
            long afterId, long throughId, String turnId, long episode, Instant observedAt, boolean aborted) {}

    public record Position(long rowId, int offset) {}

    public record Evidence(
            long sourceId, String origin, boolean originalInput, Instant observedAt, int offset, String text) {}

    public record Fragment(Evidence evidence, Position next, boolean complete) {}
}
