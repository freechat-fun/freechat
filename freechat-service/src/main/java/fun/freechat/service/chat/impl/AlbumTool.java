package fun.freechat.service.chat.impl;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.observability.AgentRequest;
import dev.langchain4j.agentic.observability.AgentResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.Result;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.service.util.StoreUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static fun.freechat.service.util.StoreUtils.PUBLIC_DIR;

@Slf4j
public class AlbumTool {
    private static final String DESCRIPTION_EN = """
            Fetch real-time, context-specific images of a character. This tool MUST be invoked whenever the user requests to see, generate, or obtain a photo, image, or specific scene of a character.
            [Core Behavior Guidelines]
            1. Dynamic Generation Principle: This tool dynamically returns a brand-new, authentic image URL that perfectly matches the specific scene, action, clothing, or mood described by the user in the current turn.
            2. Strictly Prohibit URL Reuse: Even if an image URL for the same character was fetched previously in the conversation history, you are ABSOLUTELY FORBIDDEN from reusing that historical URL. Every single request is independent and context-dependent.
            3. Anti-Hallucination Requirement: Never fabricate, hallucinate, guess, or piece together any image URLs. You must use the exact, real URL returned by the tool in the current invocation.
            4. Embedding Format: If a valid image URL is returned, you must embed it into your final response using Markdown format: ![img](url).""";
    private static final String DESCRIPTION_CN = """
            获取角色的实时场景图片。当用户请求查看、生成或获取某个角色的照片、图片、或特定场景下的画面时，必须调用此工具。
            【核心行为准则】
            1. 动态生成原则：该工具会根据用户当前轮次描述的具体场景、动作、服装或情绪，动态返回一张全新的、匹配当前最新语境的真实图片 URL。
            2. 严禁复用历史链接：即便此前在对话历史中已经获取过该角色的照片，也绝对禁止直接复用旧的图片 URL。因为每一次请求的画面内容都是完全独立且即时生成的。
            3. 真实性要求：绝对禁止伪造、捏造、猜测或盲目拼凑任何图片 URL。必须使用工具在当前轮次实际返回的真实 URL。
            4. 嵌入格式：如果返回了有效的图片 URL，您必须使用 Markdown 格式将其嵌入到最终响应中：![img](url)。""";
    private static final String IMAGE_EDIT_AGENT_DESCRIPTION_EN = "Generate a new image based on an input image.";
    private static final String IMAGE_EDIT_AGENT_DESCRIPTION_CN = "根据输入的图片生成一张新图片。";
    private static final String IMAGE_EDIT_AGENT_PROMPT_EN = """
            Use the person from the input image to generate a new image. Keep the person's face unchanged.
            Image style: photorealistic.
            Image description: {{description}}
            """;
    private static final String IMAGE_EDIT_AGENT_PROMPT_CN = """
            使用输入图片中的人物，生成一张新的图片。保持人物（脸）不变。
            图片风格：写实。
            图片描述：{{description}}
            """;
    private static final String IMAGE_GENERATE_AGENT_DESCRIPTION_EN = "Generate a image of a person.";
    private static final String IMAGE_GENERATE_AGENT_DESCRIPTION_CN = "生成一张人物图片。";
    private static final String IMAGE_GENERATE_AGENT_PROMPT_EN = """
            Generate a image of a person.
            Image style: photorealistic.
            Image description: {{description}}
            """;
    private static final String IMAGE_GENERATE_AGENT_PROMPT_CN = """
            生成一张人物图片。
            图片风格：写实。
            图片描述：{{description}}
            """;

    private final String homeUrl;
    private final CharacterService characterService;
    private final ChatContextService chatContextService;
    private final ShortLinkService shortLinkService;
    private final ChatSessionService chatSessionService;
    private final String lang;

    @Builder
    public AlbumTool(
            String homeUrl,
            CharacterService characterService,
            ChatContextService chatContextService,
            ShortLinkService shortLinkService,
            ChatSessionService chatSessionService,
            String lang) {
        this.homeUrl = ensureNotNull(homeUrl, "homeUrl");
        this.characterService = ensureNotNull(characterService, "characterService");
        this.chatContextService = ensureNotNull(chatContextService, "chatContextService");
        this.shortLinkService = ensureNotNull(shortLinkService, "shortLinkService");
        this.chatSessionService = ensureNotNull(chatSessionService, "chatSessionService");
        this.lang = ensureNotNull(lang, "lang");
    }

    @Tool
    public Content findAnImage(@ToolMemoryId Object memoryId, String description) {
        String originImageUrl = loadLocalImage(memoryId);

        ChatModel imageChatModel = Optional.ofNullable(memoryId)
                .map(ChatService::asChatId)
                .filter(StringUtils::isNotBlank)
                .map(chatSessionService::get)
                .map(ChatSession::getImageChatModel)
                .orElse(null);

        if (imageChatModel == null) {
            return TextContent.from(originImageUrl);
        }

        ImageContentParser resultParser = new ImageContentParser();

        UntypedAgent imageCreator;
        Map<String, Object> input;
        if (originImageUrl == null) {
            ImageGenerateAgent generateAgent = AgenticServices.agentBuilder(ImageGenerateAgent.class)
                    .description(imageGenerateAgentDescription())
                    .chatModel(imageChatModel)
                    .outputKey("generatedImage")
                    .listener(new AgentListener() {
                        @Override
                        public void beforeAgentInvocation(AgentRequest request) {
                            log.info("[Input Description]: {}", request.inputs().get("description"));
                        }

                        @Override
                        public void afterAgentInvocation(AgentResponse response) {
                            if (response.output() instanceof Result<?> result
                                    && result.content() instanceof AiMessage message) {
                                log.info(
                                        "[Output Description {}]: {}",
                                        result.finalResponse().metadata().id(),
                                        message.text());
                            }
                        }
                    })
                    .build();

            imageCreator = AgenticServices.sequenceBuilder()
                    .subAgents(generateAgent, resultParser)
                    .outputKey("imageResult")
                    .build();

            input = Map.of(
                    "chatId",
                    ChatService.asChatId(memoryId),
                    "homeUrl",
                    homeUrl,
                    "shortLinkService",
                    shortLinkService,
                    "prompt",
                    imageGenerateAgentPrompt(),
                    "description",
                    description);
        } else {
            ImageEditAgent editAgent = AgenticServices.agentBuilder(ImageEditAgent.class)
                    .description(imageEditAgentDescription())
                    .chatModel(imageChatModel)
                    .outputKey("generatedImage")
                    .listener(new AgentListener() {
                        @Override
                        public void beforeAgentInvocation(AgentRequest request) {
                            log.info("[Input Description]: {}", request.inputs().get("description"));
                        }

                        @Override
                        public void afterAgentInvocation(AgentResponse response) {
                            if (response.output() instanceof Result<?> result
                                    && result.content() instanceof AiMessage message) {
                                log.info(
                                        "[Output Description {}]: {}",
                                        result.finalResponse().metadata().id(),
                                        message.text());
                            }
                        }
                    })
                    .build();

            imageCreator = AgenticServices.sequenceBuilder()
                    .subAgents(editAgent, resultParser)
                    .outputKey("imageResult")
                    .build();

            input = Map.of(
                    "chatId",
                    ChatService.asChatId(memoryId),
                    "homeUrl",
                    homeUrl,
                    "shortLinkService",
                    shortLinkService,
                    "prompt",
                    imageEditAgentPrompt(),
                    "originImage",
                    ImageContent.from(originImageUrl, ImageContent.DetailLevel.HIGH),
                    "description",
                    description);
        }

        return (Content) imageCreator.invoke(input);
    }

    public String description() {
        return "zh".equalsIgnoreCase(lang) ? DESCRIPTION_CN : DESCRIPTION_EN;
    }

    private String imageEditAgentDescription() {
        return "zh".equalsIgnoreCase(lang) ? IMAGE_EDIT_AGENT_DESCRIPTION_CN : IMAGE_EDIT_AGENT_DESCRIPTION_EN;
    }

    private String imageEditAgentPrompt() {
        return "zh".equalsIgnoreCase(lang) ? IMAGE_EDIT_AGENT_PROMPT_CN : IMAGE_EDIT_AGENT_PROMPT_EN;
    }

    private String imageGenerateAgentDescription() {
        return "zh".equalsIgnoreCase(lang) ? IMAGE_GENERATE_AGENT_DESCRIPTION_CN : IMAGE_GENERATE_AGENT_DESCRIPTION_EN;
    }

    private String imageGenerateAgentPrompt() {
        return "zh".equalsIgnoreCase(lang) ? IMAGE_GENERATE_AGENT_PROMPT_CN : IMAGE_GENERATE_AGENT_PROMPT_EN;
    }

    protected String loadLocalImage(Object memoryId) {
        CharacterInfo characterInfo = Optional.ofNullable(memoryId)
                .map(ChatService::asChatId)
                .filter(StringUtils::isNotBlank)
                .map(chatContextService::getCharacterUid)
                .filter(StringUtils::isNotBlank)
                .map(characterService::summaryByUid)
                .orElse(null);

        if (characterInfo == null) {
            return null;
        }

        String characterPicture = characterInfo.getPicture();
        String characterAvatar = characterInfo.getAvatar();

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir =
                    PUBLIC_DIR + characterInfo.getUserId() + "/character/picture/" + characterInfo.getCharacterUid();
            List<String> pictures = fileStore.list(dstDir, null, false).stream()
                    // order by last modified time
                    .sorted(Comparator.comparing(
                            path -> {
                                try {
                                    return fileStore.getLastModifiedTime(path);
                                } catch (IOException e) {
                                    return Long.MAX_VALUE;
                                }
                            },
                            Long::compareTo))
                    .toList();

            if (pictures.isEmpty()) {
                return defaultPicture(characterAvatar, characterPicture);
            }

            String picturePath = pictures.getFirst();
            String pictureUrl = fileStore.getShareUrl(picturePath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(pictureUrl)) {
                String subPath = picturePath.substring(PUBLIC_DIR.length());
                String key = Base64.getUrlEncoder().encodeToString(subPath.getBytes(StandardCharsets.UTF_8));
                String targetPath = "/public/image/" + key;
                try {
                    // use short link to reduce token usage
                    targetPath = shortLinkService.shorten("/public/image/" + key);
                    pictureUrl = "%s/s/%s".formatted(homeUrl, targetPath);
                } catch (Exception e) {
                    log.warn("Failed to shorten path: {}", targetPath);
                    pictureUrl = homeUrl + targetPath;
                }
            }

            if (StringUtils.isNotBlank(picturePath)) {
                // touch file so that it is at the end of list next time
                fileStore.setLastModifiedTime(picturePath, System.currentTimeMillis());
            }

            log.info(
                    "Found a picture for character {}, path: {}, url: {}",
                    characterInfo.getName(),
                    picturePath,
                    pictureUrl);

            return pictureUrl;
        } catch (IOException e) {
            log.warn(
                    "Failed to list album of character: {} ({})",
                    characterInfo.getName(),
                    characterInfo.getCharacterUid());
            return defaultPicture(characterAvatar, characterPicture);
        }
    }

    private String defaultPicture(String avatar, String picture) {
        return StringUtils.isNotBlank(avatar)
                ? shortenPictureUrl(avatar)
                : StringUtils.isNotBlank(picture) ? shortenPictureUrl(picture) : null;
    }

    private String shortenPictureUrl(String pictureUrl) {
        if (!pictureUrl.startsWith(homeUrl)) {
            return pictureUrl;
        }
        try {
            String urlPath = pictureUrl.substring(homeUrl.length());
            String shortPath = shortLinkService.shorten(urlPath);
            return "%s/s/%s".formatted(homeUrl, shortPath);
        } catch (Exception e) {
            log.warn("Failed to shorten picture url: {}", pictureUrl);
            return pictureUrl;
        }
    }
}
