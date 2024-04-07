/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.7.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.AgentSummaryStatsDTO;
import fun.freechat.client.model.CharacterSummaryStatsDTO;
import fun.freechat.client.model.HotTagDTO;
import fun.freechat.client.model.InteractiveStatsDTO;
import fun.freechat.client.model.PluginSummaryStatsDTO;
import fun.freechat.client.model.PromptSummaryStatsDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for InteractiveStatisticsApi
 */
@Disabled
public class InteractiveStatisticsApiTest {

    private final InteractiveStatisticsApi api = new InteractiveStatisticsApi();

    /**
     * Add Statistics
     *
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void addStatisticTest() throws ApiException {
        String infoType = null;
        String infoId = null;
        String statsType = null;
        Long delta = null;
        Long response = api.addStatistic(infoType, infoId, statsType, delta);
        // TODO: test validations
    }

    /**
     * Get Score for Resource
     *
     * Get the current user&#39;s score for the corresponding resource.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getScoreTest() throws ApiException {
        String infoType = null;
        String infoId = null;
        Long response = api.getScore(infoType, infoId);
        // TODO: test validations
    }

    /**
     * Get Statistics
     *
     * Get the statistics of the corresponding metrics of the corresponding resources.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getStatisticTest() throws ApiException {
        String infoType = null;
        String infoId = null;
        String statsType = null;
        Long response = api.getStatistic(infoType, infoId, statsType);
        // TODO: test validations
    }

    /**
     * Get All Statistics
     *
     * Get all statistics of the corresponding resources.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getStatisticsTest() throws ApiException {
        String infoType = null;
        String infoId = null;
        InteractiveStatsDTO response = api.getStatistics(infoType, infoId);
        // TODO: test validations
    }

    /**
     * Increase Statistics
     *
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void increaseStatisticTest() throws ApiException {
        String infoType = null;
        String infoId = null;
        String statsType = null;
        Long response = api.increaseStatistic(infoType, infoId, statsType);
        // TODO: test validations
    }

    /**
     * List Agents by Statistics
     *
     * List agents based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAgentsByStatisticTest() throws ApiException {
        String statsType = null;
        String asc = null;
        List<AgentSummaryStatsDTO> response = api.listAgentsByStatistic(statsType, asc);
        // TODO: test validations
    }

    /**
     * List Agents by Statistics
     *
     * List agents based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAgentsByStatistic1Test() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        String asc = null;
        List<AgentSummaryStatsDTO> response = api.listAgentsByStatistic1(statsType, pageSize, asc);
        // TODO: test validations
    }

    /**
     * List Agents by Statistics
     *
     * List agents based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAgentsByStatistic2Test() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        Long pageNum = null;
        String asc = null;
        List<AgentSummaryStatsDTO> response = api.listAgentsByStatistic2(statsType, pageSize, pageNum, asc);
        // TODO: test validations
    }

    /**
     * List Characters by Statistics
     *
     * List characters based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharactersByStatisticTest() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        String asc = null;
        List<CharacterSummaryStatsDTO> response = api.listCharactersByStatistic(statsType, pageSize, asc);
        // TODO: test validations
    }

    /**
     * List Characters by Statistics
     *
     * List characters based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharactersByStatistic1Test() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        Long pageNum = null;
        String asc = null;
        List<CharacterSummaryStatsDTO> response = api.listCharactersByStatistic1(statsType, pageSize, pageNum, asc);
        // TODO: test validations
    }

    /**
     * List Characters by Statistics
     *
     * List characters based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listCharactersByStatistic2Test() throws ApiException {
        String statsType = null;
        String asc = null;
        List<CharacterSummaryStatsDTO> response = api.listCharactersByStatistic2(statsType, asc);
        // TODO: test validations
    }

    /**
     * Hot Tags
     *
     * Get popular tags for a specified info type.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listHotTagsTest() throws ApiException {
        String infoType = null;
        Long pageSize = null;
        String text = null;
        List<HotTagDTO> response = api.listHotTags(infoType, pageSize, text);
        // TODO: test validations
    }

    /**
     * List Plugins by Statistics
     *
     * List plugins based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPluginsByStatisticTest() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        Long pageNum = null;
        String asc = null;
        List<PluginSummaryStatsDTO> response = api.listPluginsByStatistic(statsType, pageSize, pageNum, asc);
        // TODO: test validations
    }

    /**
     * List Plugins by Statistics
     *
     * List plugins based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPluginsByStatistic1Test() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        String asc = null;
        List<PluginSummaryStatsDTO> response = api.listPluginsByStatistic1(statsType, pageSize, asc);
        // TODO: test validations
    }

    /**
     * List Plugins by Statistics
     *
     * List plugins based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPluginsByStatistic2Test() throws ApiException {
        String statsType = null;
        String asc = null;
        List<PluginSummaryStatsDTO> response = api.listPluginsByStatistic2(statsType, asc);
        // TODO: test validations
    }

    /**
     * List Prompts by Statistics
     *
     * List prompts based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPromptsByStatisticTest() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        String asc = null;
        List<PromptSummaryStatsDTO> response = api.listPromptsByStatistic(statsType, pageSize, asc);
        // TODO: test validations
    }

    /**
     * List Prompts by Statistics
     *
     * List prompts based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPromptsByStatistic1Test() throws ApiException {
        String statsType = null;
        Long pageSize = null;
        Long pageNum = null;
        String asc = null;
        List<PromptSummaryStatsDTO> response = api.listPromptsByStatistic1(statsType, pageSize, pageNum, asc);
        // TODO: test validations
    }

    /**
     * List Prompts by Statistics
     *
     * List prompts based on statistics, including interactive statistical data.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPromptsByStatistic2Test() throws ApiException {
        String statsType = null;
        String asc = null;
        List<PromptSummaryStatsDTO> response = api.listPromptsByStatistic2(statsType, asc);
        // TODO: test validations
    }

}
