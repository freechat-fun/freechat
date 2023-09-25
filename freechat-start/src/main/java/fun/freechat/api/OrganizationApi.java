package fun.freechat.api;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.User;
import fun.freechat.service.account.SysAuthorityService;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.organization.OrgService;
import fun.freechat.util.AuthorityUtils;
import fun.freechat.util.graph.Graph;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Controller
@Tag(name = "Organization")
@RequestMapping("/api/v1/org")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class OrganizationApi {
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysAuthorityService authorityService;

    @Autowired
    private OrgService orgService;

    private String getSubordinateId(String username) {
        return getSubordinateId(username, null);
    }

    private String getSubordinateId(String username, Graph<String> subordinates) {
        String userId = AccountUtils.userNameToId(username);
        if (Objects.isNull(subordinates)) {
            subordinates = orgService.getSubordinates(AccountUtils.currentUser().getUserId());
        }
        if (subordinates.contains(userId) ||
                AuthorityUtils.adminRole().equals(AccountUtils.currentRole())) {
            return userId;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to manage user " + username);
        }
    }

    private void checkAuthorities(Collection<String> authorities) {
        String currentAuthority = AuthorityUtils.fromRole(AccountUtils.currentRole());
        int currentPriority = AuthorityUtils.getPriority(currentAuthority);
        for (String authority : authorities) {
            if (currentPriority <= AuthorityUtils.getPriority(authority)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to set authority " + authority);
            }
        }
    }

    @Operation(
            operationId = "getOwners",
            summary = "Get My Superior Relationship",
            description = "Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship."
    )
    @GetMapping("/owners")
    public List<String> getOwners(
            @Parameter(description = "Whether to return virtual reported owners") @RequestParam("all") @Nullable String all) {
        boolean includeVirtual = "1".equalsIgnoreCase(all);
        return orgService.getOwners(AccountUtils.currentUser().getUserId(), includeVirtual)
                .stream()
                .map(AccountUtils::userIdToName)
                .toList();
    }

    @Operation(
            operationId = "getOwnersDot",
            summary = "Get DOT of Superior Relationship",
            description = "Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)"
    )
    @GetMapping("/owners/dot")
    public String getOwnersDot(
            @Parameter(description = "Whether to return virtual reported owners") @RequestParam("all") @Nullable String all) {
        boolean includeVirtual = "1".equalsIgnoreCase(all);
        return orgService.getOwners(AccountUtils.currentUser().getUserId(), includeVirtual)
                .toDot(AccountUtils::userIdToName);
    }

    @Operation(
            operationId = "getSubordinates",
            summary = "Get My Subordinate Relationship",
            description = "Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship."
    )
    @GetMapping("/subordinates")
    public List<String> getSubordinates(
            @Parameter(description = "Whether to return virtual managed subordinates") @RequestParam("all") @Nullable String all) {
        boolean includeVirtual = "1".equalsIgnoreCase(all);
        return orgService.getSubordinates(AccountUtils.currentUser().getUserId(), includeVirtual)
                .stream()
                .map(AccountUtils::userIdToName)
                .toList();
    }

    @Operation(
            operationId = "getSubordinatesDot",
            summary = "Get DOT of Subordinate Relationship",
            description = "Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)"
    )
    @GetMapping("/subordinates/dot")
    public String getSubordinatesDot(
            @Parameter(description = "Whether to return virtual managed subordinates") @RequestParam("all") @Nullable String all) {
        boolean includeVirtual = "1".equalsIgnoreCase(all);
        return orgService.getSubordinates(AccountUtils.currentUser().getUserId(), includeVirtual)
                .toDot(AccountUtils::userIdToName);
    }

    @Operation(
            operationId = "getSubordinateOwners",
            summary = "Get Superior Relationship",
            description = """
                    Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/>
                    By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
                    """
    )
    @GetMapping("/manage/{username}/owners")
    public List<String> getSubordinateOwners(
            @Parameter(description = "The account being queried, must be a subordinate account of the current account")
            @PathVariable("username")
            @NotBlank
            String username,
            @Parameter(description = "Whether to return virtual reported owners")
            @RequestParam("all")
            @Nullable
            String all) {
        String userId = getSubordinateId(username);
        boolean includeVirtual = "1".equalsIgnoreCase(all);
        return orgService.getOwners(userId, includeVirtual)
                .stream()
                .map(AccountUtils::userIdToName)
                .toList();
    }

    @Operation(
            operationId = "getSubordinateSubordinates",
            summary = "Get Subordinate Relationship",
            description = "Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship."
    )
    @GetMapping("/manage/{username}/subordinates")
    public List<String> getSubordinateSubordinates(
            @Parameter(description = "The account being queried, must be a subordinate account of the current account") @PathVariable("username") @NotBlank
            String username,
            @Parameter(description = "Whether to return virtual managed subordinates") @RequestParam("all") @Nullable
            String all) {
        String userId = getSubordinateId(username);
        boolean includeVirtual = "1".equalsIgnoreCase(all);
        return orgService.getSubordinates(userId, includeVirtual)
                .stream()
                .map(AccountUtils::userIdToName)
                .toList();
    }

    @Operation(
            operationId = "updateSubordinateOwners",
            summary = "Update Superior Relationship",
            description = "Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship."
    )
    @PutMapping("/manage/{username}/owners")
    public Boolean updateSubordinateOwners(
            @Parameter(description = "The account being operated, must be a subordinate account of the current account")
            @PathVariable("username")
            @NotBlank
            String username,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account"
            )
            @RequestBody
            @NotEmpty
            List<String> owners) {
        Graph<String> mySubordinates = orgService.getSubordinates(AccountUtils.currentUser().getUserId());
        String userId = getSubordinateId(username, mySubordinates);
        List<String> ownerIds = new ArrayList<>(owners.size());
        for (String owner : owners) {
            String ownerId = getSubordinateId(owner, mySubordinates);
            ownerIds.add(ownerId);
        }
        return orgService.updateOwners(userId, ownerIds);
    }

    @Operation(
            operationId = "updateSubordinateSubordinates",
            summary = "Update Subordinate Relationship",
            description = "Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship."
    )
    @PutMapping("/manage/{username}/subordinates")
    public Boolean updateSubordinateSubordinates(
            @Parameter(description = "The account being operated, must be a subordinate account of the current account") @PathVariable("username") @NotBlank
            String username,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account"
            )
            @RequestBody
            @NotEmpty
            List<String> subordinates) {
        Graph<String> mySubordinates = orgService.getSubordinates(AccountUtils.currentUser().getUserId());
        String userId = getSubordinateId(username, mySubordinates);
        List<String> subordinateIds = new ArrayList<>(subordinates.size());
        for (String subordinate : subordinates) {
            String subordinateId = getSubordinateId(subordinate, mySubordinates);
            subordinateIds.add(subordinateId);
        }
        return orgService.updateSubordinates(userId, subordinateIds);
    }

    @Operation(
            operationId = "removeSubordinateSubordinatesTree",
            summary = "Clear Subordinate Relationship",
            description = "Fully delete the direct subordinate relationship of the subordinate account."
    )
    @DeleteMapping("/manage/{username}/subordinates")
    public Boolean removeSubordinateSubordinatesTree(
            @Parameter(description = "The account being operated, must be a subordinate account of the current account")
            @PathVariable("username")
            @NotBlank
            String username) {
        String userId = getSubordinateId(username);
        return orgService.removeSubordinatesTree(userId);
    }

    @Operation(
            operationId = "listSubordinateAuthorities",
            summary = "List Subordinate Permissions",
            description = "List the permission list of the subordinate account."
    )
    @GetMapping("/authority/{username}")
    public Set<String> listSubordinateAuthorities(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username) {
        getSubordinateId(username);
        User user = userService.loadByUsername(username);
        if (Objects.isNull(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find user " + username);
        }
        return authorityService.list(user);
    }

    @Operation(
            operationId = "updateSubordinateAuthorities",
            summary = "Update Subordinate Permissions",
            description = "Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account."
    )
    @PutMapping("/authority/{username}")
    public Boolean updateSubordinateAuthorities(
            @Parameter(description = "Username") @PathVariable("username") @NotBlank
            String username,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Permission list") @RequestBody @NotNull
            Set<String> authorities) {
        getSubordinateId(username);
        checkAuthorities(authorities);
        User user = userService.loadByUsername(username);
        if (Objects.isNull(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find user " + username);
        }
        return authorityService.update(user, authorities);
    }
}
