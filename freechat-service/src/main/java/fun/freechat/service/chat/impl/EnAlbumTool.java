package fun.freechat.service.chat.impl;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.common.ShortLinkService;
import lombok.Builder;

public class EnAlbumTool extends AlbumTool {

    @Builder
    public EnAlbumTool(String homeUrl,
                       CharacterService characterService,
                       ChatContextService chatContextService,
                       ShortLinkService shortLinkService) {
        super(homeUrl, characterService, chatContextService, shortLinkService);
    }

    @Override
    @Tool("""
            This tool is used to find an image related to character. \
            When you determine that the user wants to see the character's photo, \
            you should call this tool to get a picture of the character, \
            use the markdown tag "![img](the image url)" and \
            place it in the appropriate position in the response content. \
            You can extract the description (or tags) of the image you want to find \
            from the conversation and pass them to this tool to more accurately find \
            the image the user wants to see, but this is not required.
            Note:
            - This tool returns the URL of the image. You need to use the markdown tag \
            "![img](the image url)" to wrap it and place it in the appropriate position \
            in the response content.
            - This tool may not find any images, in which case it will return the special content \
            "<NOT_FOUND>". You need to ignore the result of this tool and explain to the user \
            that you currently have no photos to provide.""")
    public String findAnImage(@ToolMemoryId Object memoryId,
                              @P(value = "A description of the picture being searched for.", required = false) String description) {
        return super.findAnImage(memoryId, description);
    }
}
