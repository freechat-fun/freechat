package fun.freechat.service.chat.impl;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.common.ShortLinkService;
import lombok.Builder;

public class ZhAlbumTool extends AlbumTool {
    @Builder
    public ZhAlbumTool(String homeUrl,
                       CharacterService characterService,
                       ChatContextService chatContextService,
                       ShortLinkService shortLinkService) {
        super(homeUrl, characterService, chatContextService, shortLinkService);
    }

    @Override
    @Tool("""
            这个工具用于查找与角色相关的图片。当你判断出用户想要查看角色的照片时，\
            应该调用此工具以获取角色的图片，使用 Markdown 标签 "![img](图片的 URL)" \
            并将其放置在响应内容的适当位置。你可以从对话中提取要查找的图片的描述（或标签），\
            并将它们传递给此工具，以更准确地找到用户想要查看的图片，但这不是必需的。
            注意：
            - 此工具返回图片的 URL。你需要使用 Markdown 标签 "![img](图片的 URL)" 将其封装，\
            并放置在响应内容的适当位置。
            - 此工具可能找不到任何图片，在这种情况下，它将返回特殊内容 "<NOT_FOUND>"。你需要忽略此工具的结果，\
            并向用户解释你目前没有可以提供的照片。""")
    public String findAnImage(@ToolMemoryId Object memoryId,
                              @P(value = "需要检索的图片的描述。", required = false) String description) {
        return super.findAnImage(memoryId, description);
    }
}
