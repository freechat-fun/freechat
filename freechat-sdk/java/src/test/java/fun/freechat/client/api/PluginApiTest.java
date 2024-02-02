/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.15
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.PluginCreateDTO;
import fun.freechat.client.model.PluginDetailsDTO;
import fun.freechat.client.model.PluginQueryDTO;
import fun.freechat.client.model.PluginSummaryDTO;
import fun.freechat.client.model.PluginUpdateDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PluginApi
 */
@Disabled
public class PluginApiTest {

    private final PluginApi api = new PluginApi();

    /**
     * Batch Search Plugin Details
     *
     * Batch call shortcut for /api/v1/plugin/details/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPluginDetailsTest() throws ApiException {
        List<PluginQueryDTO> pluginQueryDTO = null;
        List<List<PluginDetailsDTO>> response = api.batchSearchPluginDetails(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Plugin Summaries
     *
     * Batch call shortcut for /api/v1/plugin/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPluginSummaryTest() throws ApiException {
        List<PluginQueryDTO> pluginQueryDTO = null;
        List<List<PluginSummaryDTO>> response = api.batchSearchPluginSummary(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Calculate Number of Plugins
     *
     * Calculate the number of plugins according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countPluginsTest() throws ApiException {
        PluginQueryDTO pluginQueryDTO = null;
        Long response = api.countPlugins(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Create Plugin
     *
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPluginTest() throws ApiException {
        PluginCreateDTO pluginCreateDTO = null;
        String response = api.createPlugin(pluginCreateDTO);
        // TODO: test validations
    }

    /**
     * Batch Create Plugins
     *
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPluginsTest() throws ApiException {
        List<PluginCreateDTO> pluginCreateDTO = null;
        List<String> response = api.createPlugins(pluginCreateDTO);
        // TODO: test validations
    }

    /**
     * Delete Plugin
     *
     * Delete plugin. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePluginTest() throws ApiException {
        String pluginId = null;
        Boolean response = api.deletePlugin(pluginId);
        // TODO: test validations
    }

    /**
     * Batch Delete Plugins
     *
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePluginsTest() throws ApiException {
        List<String> requestBody = null;
        List<String> response = api.deletePlugins(requestBody);
        // TODO: test validations
    }

    /**
     * Get Plugin Details
     *
     * Get plugin detailed information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPluginDetailsTest() throws ApiException {
        String pluginId = null;
        PluginDetailsDTO response = api.getPluginDetails(pluginId);
        // TODO: test validations
    }

    /**
     * Get Plugin Summary
     *
     * Get plugin summary information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPluginSummaryTest() throws ApiException {
        String pluginId = null;
        PluginSummaryDTO response = api.getPluginSummary(pluginId);
        // TODO: test validations
    }

    /**
     * Refresh Plugin Information
     *
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void refreshPluginInfoTest() throws ApiException {
        String pluginId = null;
        api.refreshPluginInfo(pluginId);
        // TODO: test validations
    }

    /**
     * Search Plugin Details
     *
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPluginDetailsTest() throws ApiException {
        PluginQueryDTO pluginQueryDTO = null;
        List<PluginDetailsDTO> response = api.searchPluginDetails(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Plugin Summary
     *
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPluginSummaryTest() throws ApiException {
        PluginQueryDTO pluginQueryDTO = null;
        List<PluginSummaryDTO> response = api.searchPluginSummary(pluginQueryDTO);
        // TODO: test validations
    }

    /**
     * Update Plugin
     *
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updatePluginTest() throws ApiException {
        String pluginId = null;
        PluginUpdateDTO pluginUpdateDTO = null;
        Boolean response = api.updatePlugin(pluginId, pluginUpdateDTO);
        // TODO: test validations
    }

}
