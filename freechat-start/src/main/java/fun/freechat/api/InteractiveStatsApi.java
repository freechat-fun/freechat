package fun.freechat.api;

import fun.freechat.api.dto.*;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.InteractiveStatsScoreDetails;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.common.TagService;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.flow.FlowService;
import fun.freechat.service.plugin.PluginService;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.stats.InteractiveStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@Tag(name = "Interactive Statistics")
@RequestMapping("/api/v1")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class InteractiveStatsApi {
    @Autowired
    private InteractiveStatsService interactiveStatsService;

    @Autowired
    private PromptService promptService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private CharacterService characterService;

    @Autowired
    private TagService tagService;

    @Operation(
            operationId = "addStatistic",
            summary = "Add Statistics",
            description = "Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics."
    )
    @PostMapping("/stats/{infoType}/{infoId}/{statsType}/{delta}")
    public Long add(
            @Parameter(description = "Info type: prompt | flow | plugin | character") @PathVariable("infoType") @NotBlank String infoType,
            @Parameter(description = "Unique resource identifier") @PathVariable("infoId") @NotBlank String infoId,
            @Parameter(description = "Statistics type: view_count | refer_count | recommend_count | score") @PathVariable("statsType") @NotBlank String statsType,
            @Parameter(description = "Delta in statistical value") @PathVariable("delta") @NotEmpty Long delta) {
        StatsType statsTypeEnum = StatsType.of(statsType);
        if (statsTypeEnum == StatsType.SCORE) {
            delta = Math.max(0L, delta);
            delta = Math.min(10L, delta);
        }
        return interactiveStatsService.add(AccountUtils.currentUser().getUserId(),
                InfoType.of(infoType), infoId,
                StatsType.of(statsType), Objects.isNull(delta) ? 0L : delta);
    }

    @Operation(
            operationId = "increaseStatistic",
            summary = "Increase Statistics",
            description = "Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics."
    )
    @PostMapping("/stats/{infoType}/{infoId}/{statsType}")
    public Long increase(
            @Parameter(description = "Info type: prompt | flow | plugin | character") @PathVariable("infoType") @NotBlank String infoType,
            @Parameter(description = "Unique resource identifier") @PathVariable("infoId") @NotBlank String infoId,
            @Parameter(description = "Statistics type: view_count | refer_count | recommend_count | score") @PathVariable("statsType") @NotBlank String statsType) {
        return add(infoType, infoId, statsType, 1L);
    }

    @Operation(
            operationId = "getStatistic",
            summary = "Get Statistics",
            description = "Get the statistics of the corresponding metrics of the corresponding resources."
    )
    @GetMapping("/stats/{infoType}/{infoId}/{statsType}")
    public Long get(
            @Parameter(description = "Info type: prompt | flow | plugin | character") @PathVariable("infoType") @NotBlank String infoType,
            @Parameter(description = "Unique resource identifier") @PathVariable("infoId") @NotBlank String infoId,
            @Parameter(description = "Statistics type: view_count | refer_count | recommend_count | score") @PathVariable("statsType") @NotBlank String statsType) {
        return add(infoType, infoId, statsType, 0L);
    }

    @Operation(
            operationId = "getStatistics",
            summary = "Get All Statistics",
            description = "Get all statistics of the corresponding resources."
    )
    @GetMapping("/stats/{infoType}/{infoId}")
    public InteractiveStatsDTO get(
            @Parameter(description = "Info type: prompt | flow | plugin | character") @PathVariable("infoType") @NotBlank String infoType,
            @Parameter(description = "Unique resource identifier") @PathVariable("infoId") @NotBlank String infoId) {
        return InteractiveStatsDTO.from(
                interactiveStatsService.get(InfoType.of(infoType), infoId));
    }

    @Operation(
            operationId = "getScore",
            summary = "Get Score for Resource",
            description = "Get the current user's score for the corresponding resource."
    )
    @GetMapping("/score/{infoType}/{infoId}")
    public Long getScore(
            @Parameter(description = "Info type: prompt | flow | plugin | character") @PathVariable("infoType") @NotBlank String infoType,
            @Parameter(description = "Unique resource identifier") @PathVariable("infoId") @NotBlank String infoId) {
        return Optional.ofNullable(interactiveStatsService.getScore(
                    AccountUtils.currentUser().getUserId(), InfoType.of(infoType), infoId))
                .map(InteractiveStatsScoreDetails::getScore)
                .orElse(-1L);
    }

    @Operation(
            operationId = "listPromptsByStatistic",
            summary = "List Prompts by Statistics",
            description = "List prompts based on statistics, including interactive statistical data."
    )
    @GetMapping(value = {
            "/stats/prompts/by/{statsType}/{pageSize}/{pageNum}",
            "/stats/prompts/by/{statsType}/{pageSize}",
            "/stats/prompts/by/{statsType}"})
    public List<PromptSummaryStatsDTO> listPrompts(
            @Parameter(description = "Statistics type: view_count | refer_count | recommend_count | score") @PathVariable("statsType") @NotBlank String statsType,
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Optional<Long> pageSize,
            @Parameter(description = "Current page number") @PathVariable("pageNum") Optional<Long> pageNum,
            @Parameter(description = "Default is descending order, set asc=1 for ascending order") @RequestParam("asc") @Nullable String asc) {
        boolean desc = !"1".equals(asc);
        long limit = pageSize.filter(size -> size > 0).orElse(10L);
        long offset = pageNum.filter(num -> num >= 0).orElse(0L) * limit;
        List<InteractiveStats> statsList = interactiveStatsService.list(
                InfoType.PROMPT, StatsType.of(statsType), limit, offset, desc);
        HashMap<String, InteractiveStats> statsMap = new HashMap<>(statsList.size());
        for (InteractiveStats stats : statsList) {
            statsMap.put(stats.getReferId(), stats);
        }
        var summaries = promptService.summary(statsMap.keySet(), AccountUtils.currentUser());
        List<PromptSummaryStatsDTO> dtoList = new ArrayList<>(summaries.size());
        for (var summary : summaries) {
            InteractiveStats stats = statsMap.get(summary.getLeft().getPromptId());
            PromptSummaryStatsDTO dto = PromptSummaryStatsDTO.from(summary, stats);
            if (Objects.nonNull(dto)) {
                dtoList.add(dto);
            }
        }
        dtoList.sort((r1, r2) -> (int)(r2.getGmtModified().getTime() - r1.getGmtModified().getTime()));
        return dtoList;
    }

    @Operation(
            operationId = "listFlowsByStatistic",
            summary = "List Flows by Statistics",
            description = "List flows based on statistics, including interactive statistical data."
    )
    @GetMapping(value = {
            "/stats/flows/by/{statsType}/{pageSize}/{pageNum}",
            "/stats/flows/by/{statsType}/{pageSize}",
            "/stats/flows/by/{statsType}"})
    public List<FlowSummaryStatsDTO> listFlows(
            @Parameter(description = "Statistics type: view_count | refer_count | recommend_count | score") @PathVariable("statsType") @NotBlank String statsType,
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Optional<Long> pageSize,
            @Parameter(description = "Current page number") @PathVariable("pageNum") Optional<Long> pageNum,
            @Parameter(description = "Default is descending order, set asc=1 for ascending order") @RequestParam("asc") @Nullable String asc) {
        boolean desc = !"1".equals(asc);
        long limit = pageSize.filter(size -> size > 0).orElse(10L);
        long offset = pageNum.filter(num -> num >= 0).orElse(0L) * limit;
        List<InteractiveStats> statsList = interactiveStatsService.list(
                InfoType.FLOW, StatsType.of(statsType), limit, offset, desc);
        HashMap<String, InteractiveStats> statsMap = new HashMap<>(statsList.size());
        for (InteractiveStats stats : statsList) {
            statsMap.put(stats.getReferId(), stats);
        }
        var summaries = flowService.summary(statsMap.keySet(), AccountUtils.currentUser());
        List<FlowSummaryStatsDTO> dtoList = new ArrayList<>(summaries.size());
        for (var summary : summaries) {
            InteractiveStats stats = statsMap.get(summary.getLeft().getFlowId());
            FlowSummaryStatsDTO dto = FlowSummaryStatsDTO.from(summary, stats);
            if (Objects.nonNull(dto)) {
                dtoList.add(dto);
            }
        }
        dtoList.sort((r1, r2) -> (int)(r2.getGmtModified().getTime() - r1.getGmtModified().getTime()));
        return dtoList;
    }

    @Operation(
            operationId = "listPluginsByStatistic",
            summary = "List Plugins by Statistics",
            description = "List plugins based on statistics, including interactive statistical data."
    )
    @GetMapping(value = {
            "/stats/plugins/by/{statsType}/{pageSize}/{pageNum}",
            "/stats/plugins/by/{statsType}/{pageSize}",
            "/stats/plugins/by/{statsType}"})
    public List<PluginSummaryStatsDTO> listPlugins(
            @Parameter(description = "Statistics type: view_count | refer_count | recommend_count | score") @PathVariable("statsType") @NotBlank String statsType,
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Optional<Long> pageSize,
            @Parameter(description = "Current page number") @PathVariable("pageNum") Optional<Long> pageNum,
            @Parameter(description = "Default is descending order, set asc=1 for ascending order") @RequestParam("asc") @Nullable String asc) {
        boolean desc = !"1".equals(asc);
        long limit = pageSize.filter(size -> size > 0).orElse(10L);
        long offset = pageNum.filter(num -> num >= 0).orElse(0L) * limit;
        List<InteractiveStats> statsList = interactiveStatsService.list(
                InfoType.PLUGIN, StatsType.of(statsType), limit, offset, desc);
        HashMap<String, InteractiveStats> statsMap = new HashMap<>(statsList.size());
        for (InteractiveStats stats : statsList) {
            statsMap.put(stats.getReferId(), stats);
        }
        var summaries = pluginService.summary(statsMap.keySet(), AccountUtils.currentUser());
        List<PluginSummaryStatsDTO> dtoList = new ArrayList<>(summaries.size());
        for (var summary : summaries) {
            InteractiveStats stats = statsMap.get(summary.getLeft().getPluginId());
            PluginSummaryStatsDTO dto = PluginSummaryStatsDTO.from(summary, stats);
            if (Objects.nonNull(dto)) {
                dtoList.add(dto);
            }
        }
        dtoList.sort((r1, r2) -> (int)(r2.getGmtModified().getTime() - r1.getGmtModified().getTime()));
        return dtoList;
    }

    @Operation(
            operationId = "listCharactersByStatistic",
            summary = "List Characters by Statistics",
            description = "List characters based on statistics, including interactive statistical data."
    )
    @GetMapping(value = {
            "/stats/characters/by/{statsType}/{pageSize}/{pageNum}",
            "/stats/characters/by/{statsType}/{pageSize}",
            "/stats/characters/by/{statsType}"})
    public List<CharacterSummaryStatsDTO> listCharacters(
            @Parameter(description = "Statistics type: view_count | refer_count | recommend_count | score") @PathVariable("statsType") @NotBlank String statsType,
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Optional<Long> pageSize,
            @Parameter(description = "Current page number") @PathVariable("pageNum") Optional<Long> pageNum,
            @Parameter(description = "Default is descending order, set asc=1 for ascending order") @RequestParam("asc") @Nullable String asc) {
        boolean desc = !"1".equals(asc);
        long limit = pageSize.filter(size -> size > 0).orElse(10L);
        long offset = pageNum.filter(num -> num >= 0).orElse(0L) * limit;
        List<InteractiveStats> statsList = interactiveStatsService.list(
                InfoType.CHARACTER, StatsType.of(statsType), limit, offset, desc);
        HashMap<String, InteractiveStats> statsMap = new HashMap<>(statsList.size());
        for (InteractiveStats stats : statsList) {
            statsMap.put(stats.getReferId(), stats);
        }
        var summaries = characterService.summary(statsMap.keySet(), AccountUtils.currentUser());
        List<CharacterSummaryStatsDTO> dtoList = new ArrayList<>(summaries.size());
        for (var summary : summaries) {
            InteractiveStats stats = statsMap.get(summary.getLeft().getCharacterId());
            CharacterSummaryStatsDTO dto = CharacterSummaryStatsDTO.from(summary, stats);
            if (Objects.nonNull(dto)) {
                dtoList.add(dto);
            }
        }
        dtoList.sort((r1, r2) -> (int)(r2.getGmtModified().getTime() - r1.getGmtModified().getTime()));
        return dtoList;
    }

    @Operation(
            operationId = "listHotTags",
            summary = "Hot Tags",
            description = "Get popular tags for a specified info type."
    )
    @GetMapping("/tags/hot/{infoType}/{pageSize}")
    public List<HotTagDTO> getHotTags(
            @Parameter(description = "Info type: prompt | flow | plugin | character") @PathVariable("infoType") @NotBlank String infoType,
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Long pageSize,
            @Parameter(description = "Key word") @RequestParam("text") Optional<String> text) {
        return tagService.listHot(InfoType.of(infoType),  text.orElse(null), pageSize)
                .stream()
                .map(HotTagDTO::fromHotTag)
                .toList();
    }
}
