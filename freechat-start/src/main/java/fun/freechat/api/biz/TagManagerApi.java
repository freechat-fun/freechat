package fun.freechat.api.biz;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.common.TagService;
import fun.freechat.service.enums.InfoType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

@Service
@Tag(name = "Tag Manager (for biz, admin)", description = "Manage tags, callable only by super administrators and business administrators.")
@RequestMapping("/api/v1/biz/admin/tag")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class TagManagerApi {
    @Autowired
    private TagService tagService;

    @Operation(
            operationId = "createTag",
            summary = "Create Tag",
            description = "Create a tag, tags created by the administrator cannot be deleted by ordinary users."
    )
    @PostMapping("/{referType}/{referId}/{tag}")
    public Boolean create(
            @Parameter(description = "Tag type (prompt, agent, plugin...)") @PathVariable("referType") @NotBlank
            String referType,
            @Parameter(description = "Resource identifier of the tag") @PathVariable("referId") @NotBlank
            String referId,
            @Parameter(description = "Tag content") @PathVariable("tag") @NotBlank
            String tag) {
        return tagService.create(AccountUtils.currentUser(),
                InfoType.of(referType), referId, tag);
    }

    @Operation(
            operationId = "deleteTag",
            summary = "Delete Tag",
            description = "Delete a tag, any tag created by anyone can be deleted."
    )
    @DeleteMapping("/{referType}/{referId}/{tag}")
    public Boolean delete(
            @Parameter(description = "Tag type (prompt, agent, plugin...)") @PathVariable("referType") @NotBlank
            String referType,
            @Parameter(description = "Resource identifier of the tag") @PathVariable("referId") @NotBlank
            String referId,
            @Parameter(description = "Tag content") @PathVariable("tag") @NotBlank
            String tag) {
        return tagService.delete(AccountUtils.currentUser(),
                InfoType.of(referType), referId, tag);
    }
}
