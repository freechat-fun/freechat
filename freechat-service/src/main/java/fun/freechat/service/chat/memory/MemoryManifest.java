package fun.freechat.service.chat.memory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record MemoryManifest(List<Entry> entries) {
    public MemoryManifest {
        entries = List.copyOf(entries);
        if (entries.isEmpty() || entries.size() > MemoryBounds.MAX_MANIFEST_RECORDS) {
            throw new IllegalArgumentException("Invalid memory manifest size");
        }
        Set<String> ids = new HashSet<>();
        for (Entry entry : entries) {
            if (!ids.add(entry.id())) {
                throw new IllegalArgumentException("Duplicate memory manifest identifier");
            }
        }
    }

    public static MemoryManifest of(List<MemoryDocument> documents, MemoryDocumentCodec codec) {
        return new MemoryManifest(documents.stream()
                .map(document -> new Entry(
                        document.id(),
                        document.kind(),
                        document.archive(),
                        MemoryDocumentCodec.hash(codec.encode(document).text())))
                .toList());
    }

    public Optional<Entry> find(String id) {
        return entries.stream().filter(entry -> entry.id().equals(id)).findFirst();
    }

    public List<String> ids() {
        return entries.stream().map(Entry::id).toList();
    }

    public record Entry(String id, MemoryDocument.Kind kind, boolean archive, String hash) {
        public Entry {
            if (!MemoryDocumentCodec.canonicalUuid(id)
                    || kind == null
                    || hash == null
                    || !hash.matches("[0-9a-f]{64}")
                    || archive && kind != MemoryDocument.Kind.WINDOW_SUMMARY) {
                throw new IllegalArgumentException("Invalid memory manifest entry");
            }
        }

        public boolean discoverable() {
            return kind == MemoryDocument.Kind.EPISODE_SUMMARY || kind == MemoryDocument.Kind.WINDOW_SUMMARY && archive;
        }
    }
}
