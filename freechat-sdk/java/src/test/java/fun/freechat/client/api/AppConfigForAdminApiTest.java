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
import fun.freechat.client.model.AppConfigCreateDTO;
import fun.freechat.client.model.AppConfigInfoDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AppConfigForAdminApi
 */
@Disabled
public class AppConfigForAdminApiTest {

    private final AppConfigForAdminApi api = new AppConfigForAdminApi();

    /**
     * Get Configuration
     *
     * Get the latest configuration information of the application by name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getAppConfigTest() throws ApiException {
        String name = null;
        AppConfigInfoDTO response = api.getAppConfig(name);
        // TODO: test validations
    }

    /**
     * Get Specified Version of Configuration
     *
     * Get the configuration information of the application by name and version.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getAppConfigByVersionTest() throws ApiException {
        String name = null;
        Integer version = null;
        AppConfigInfoDTO response = api.getAppConfigByVersion(name, version);
        // TODO: test validations
    }

    /**
     * List Configuration Names
     *
     * List all application configuration names.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAppConfigNamesTest() throws ApiException {
        List<String> response = api.listAppConfigNames();
        // TODO: test validations
    }

    /**
     * Publish Configuration
     *
     * Publish application configuration, return configuration version.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void publishAppConfigTest() throws ApiException {
        AppConfigCreateDTO appConfigCreateDTO = null;
        Integer response = api.publishAppConfig(appConfigCreateDTO);
        // TODO: test validations
    }

}
