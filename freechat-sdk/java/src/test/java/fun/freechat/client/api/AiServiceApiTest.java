/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.AiApiKeyCreateDTO;
import fun.freechat.client.model.AiApiKeyInfoDTO;
import fun.freechat.client.model.AiModelInfoDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AiServiceApi
 */
@Disabled
public class AiServiceApiTest {

    private final AiServiceApi api = new AiServiceApi();

    /**
     * Add Model Provider Credential
     *
     * Add a credential for the model service.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void addAiApiKeyTest() throws ApiException {
        AiApiKeyCreateDTO aiApiKeyCreateDTO = null;
        Long response = api.addAiApiKey(aiApiKeyCreateDTO);
        // TODO: test validations
    }

    /**
     * Delete Credential of Model Provider
     *
     * Delete the credential information of the model provider.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteAiApiKeyTest() throws ApiException {
        Long id = null;
        Boolean response = api.deleteAiApiKey(id);
        // TODO: test validations
    }

    /**
     * Disable Model Provider Credential
     *
     * Disable the credential information of the model provider.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void disableAiApiKeyTest() throws ApiException {
        Long id = null;
        Boolean response = api.disableAiApiKey(id);
        // TODO: test validations
    }

    /**
     * Enable Model Provider Credential
     *
     * Enable the credential information of the model provider.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void enableAiApiKeyTest() throws ApiException {
        Long id = null;
        Boolean response = api.enableAiApiKey(id);
        // TODO: test validations
    }

    /**
     * Get credential of Model Provider
     *
     * Get the credential information of the model provider.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getAiApiKeyTest() throws ApiException {
        Long id = null;
        AiApiKeyInfoDTO response = api.getAiApiKey(id);
        // TODO: test validations
    }

    /**
     * Get Model Information
     *
     * Return specific model information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getAiModelInfoTest() throws ApiException {
        String modelId = null;
        AiModelInfoDTO response = api.getAiModelInfo(modelId);
        // TODO: test validations
    }

    /**
     * List Credentials of Model Provider
     *
     * List all credential information of the model provider.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAiApiKeysTest() throws ApiException {
        String provider = null;
        List<AiApiKeyInfoDTO> response = api.listAiApiKeys(provider);
        // TODO: test validations
    }

    /**
     * List Models
     *
     * Return model information by page, return the pageNum page, up to pageSize model information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAiModelInfoTest() throws ApiException {
        Long pageSize = null;
        List<AiModelInfoDTO> response = api.listAiModelInfo(pageSize);
        // TODO: test validations
    }

    /**
     * List Models
     *
     * Return model information by page, return the pageNum page, up to pageSize model information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAiModelInfo1Test() throws ApiException {
        List<AiModelInfoDTO> response = api.listAiModelInfo1();
        // TODO: test validations
    }

    /**
     * List Models
     *
     * Return model information by page, return the pageNum page, up to pageSize model information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listAiModelInfo2Test() throws ApiException {
        Long pageSize = null;
        Long pageNum = null;
        List<AiModelInfoDTO> response = api.listAiModelInfo2(pageSize, pageNum);
        // TODO: test validations
    }

}
