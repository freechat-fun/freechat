package fun.freechat.api;

import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.skills.SkillException;
import fun.freechat.service.skills.SkillService;
import fun.freechat.service.tools.network.SkillSourceFetcher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/skill")
@Tag(name = "Skill", description = "Private, user-owned prompt-only skills")
@Validated
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class SkillApi {
    private final SkillService skills;
    private final SkillSourceFetcher sources;

    @GetMapping
    @Operation(
            operationId = "listSkills",
            summary = "List My Skills",
            description = "List only the authenticated user's installed skills.")
    public List<SkillSummaryDTO> list() {
        return skills.list(AccountUtils.currentUser().getUserId()).stream()
                .map(SkillSummaryDTO::from)
                .toList();
    }

    @GetMapping("/{skillId}")
    @PreAuthorize("hasPermission(#p0, 'skillDefaultOp')")
    @Operation(
            operationId = "getSkill",
            summary = "Get My Skill",
            description = "Read a complete skill document set owned by the authenticated user.")
    public SkillDetailsDTO get(@PathVariable @NotBlank String skillId) {
        return SkillDetailsDTO.from(skills.get(AccountUtils.currentUser().getUserId(), skillId));
    }

    @PostMapping("/install")
    @Operation(
            operationId = "prepareSkill",
            summary = "Prepare Skill Installation",
            description =
                    "Validate and review pasted/uploaded text documents or a raw document from an operator-approved HTTPS host. Publication requires explicit confirmation of the returned digest.")
    public SkillDraftDTO prepare(@RequestBody @Valid SkillInstallDTO request) {
        if ((request.getFiles() == null) == (request.getSourceUrl() == null)) {
            throw new SkillException(SkillException.Code.SKILL_INVALID_FORMAT);
        }
        String owner = AccountUtils.currentUser().getUserId();
        Map<String, String> files = request.getFiles() != null
                ? request.getFiles()
                : Map.of("SKILL.md", sources.fetch(owner, request.getSourceUrl()));
        return SkillDraftDTO.from(skills.prepare(owner, null, null, files));
    }

    @PostMapping("/install/{draftId}")
    @Operation(
            operationId = "confirmSkill",
            summary = "Confirm Skill Installation",
            description =
                    "Publish an unexpired reviewed draft belonging to the authenticated user. Confirmation is single-use and bound to its exact content digest.")
    public SkillDetailsDTO confirm(
            @PathVariable @NotBlank String draftId, @RequestBody @Valid SkillConfirmDTO request) {
        return SkillDetailsDTO.from(
                skills.confirm(AccountUtils.currentUser().getUserId(), draftId, request.getDigest()));
    }

    @PutMapping("/{skillId}")
    @PreAuthorize("hasPermission(#p0, 'skillDefaultOp')")
    @Operation(
            operationId = "updateSkill",
            summary = "Update My Skill",
            description =
                    "Rebuild, validate and review the complete document set before atomically replacing the expected revision. Rejection leaves the installed revision unchanged.")
    public SkillDetailsDTO update(@PathVariable @NotBlank String skillId, @RequestBody @Valid SkillUpdateDTO request) {
        return SkillDetailsDTO.from(skills.update(
                AccountUtils.currentUser().getUserId(), skillId, request.getExpectedRevision(), request.getFiles()));
    }

    @DeleteMapping("/{skillId}")
    @PreAuthorize("hasPermission(#p0, 'skillDefaultOp')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            operationId = "deleteSkill",
            summary = "Delete My Skill",
            description = "Revoke an owned skill at its expected revision; characters cannot continue loading it.")
    public void delete(@PathVariable @NotBlank String skillId, @RequestParam @NotBlank String expectedRevision) {
        skills.delete(AccountUtils.currentUser().getUserId(), skillId, expectedRevision);
    }
}
