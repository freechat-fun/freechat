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
import fun.freechat.client.model.LlmResultDTO;
import fun.freechat.client.model.PromptAiParamDTO;
import fun.freechat.client.model.PromptCreateDTO;
import fun.freechat.client.model.PromptDetailsDTO;
import fun.freechat.client.model.PromptItemForNameDTO;
import fun.freechat.client.model.PromptQueryDTO;
import fun.freechat.client.model.PromptRefDTO;
import fun.freechat.client.model.PromptSummaryDTO;
import fun.freechat.client.model.PromptTemplateDTO;
import fun.freechat.client.model.PromptUpdateDTO;
import fun.freechat.client.model.SseEmitter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PromptApi
 */
@Disabled
public class PromptApiTest {

    private final PromptApi api = new PromptApi();

    /**
     * Apply Parameters to Prompt Record
     *
     * Apply parameters to prompt record.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void applyPromptRefTest() throws ApiException {
        PromptRefDTO promptRefDTO = null;
        String response = api.applyPromptRef(promptRefDTO);
        // TODO: test validations
    }

    /**
     * Apply Parameters to Prompt Template
     *
     * Apply parameters to prompt template.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void applyPromptTemplateTest() throws ApiException {
        PromptTemplateDTO promptTemplateDTO = null;
        String response = api.applyPromptTemplate(promptTemplateDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Prompt Details
     *
     * Batch call shortcut for /api/v1/prompt/details/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPromptDetailsTest() throws ApiException {
        List<PromptQueryDTO> promptQueryDTO = null;
        List<List<PromptDetailsDTO>> response = api.batchSearchPromptDetails(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Batch Search Prompt Summaries
     *
     * Batch call shortcut for /api/v1/prompt/search.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void batchSearchPromptSummaryTest() throws ApiException {
        List<PromptQueryDTO> promptQueryDTO = null;
        List<List<PromptSummaryDTO>> response = api.batchSearchPromptSummary(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Clone Prompt
     *
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void clonePromptTest() throws ApiException {
        String promptId = null;
        String response = api.clonePrompt(promptId);
        // TODO: test validations
    }

    /**
     * Batch Clone Prompts
     *
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void clonePromptsTest() throws ApiException {
        List<String> requestBody = null;
        List<String> response = api.clonePrompts(requestBody);
        // TODO: test validations
    }

    /**
     * Calculate Number of Prompts
     *
     * Calculate the number of prompts according to the specified query conditions.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void countPromptsTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        Long response = api.countPrompts(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Create Prompt
     *
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPromptTest() throws ApiException {
        PromptCreateDTO promptCreateDTO = null;
        String response = api.createPrompt(promptCreateDTO);
        // TODO: test validations
    }

    /**
     * Batch Create Prompts
     *
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPromptsTest() throws ApiException {
        List<PromptCreateDTO> promptCreateDTO = null;
        List<String> response = api.createPrompts(promptCreateDTO);
        // TODO: test validations
    }

    /**
     * Delete Prompt
     *
     * Delete prompt. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePromptTest() throws ApiException {
        String promptId = null;
        Boolean response = api.deletePrompt(promptId);
        // TODO: test validations
    }

    /**
     * Delete Prompt by Name
     *
     * Delete prompt by name. return the list of successfully deleted promptIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePromptByNameTest() throws ApiException {
        String name = null;
        List<String> response = api.deletePromptByName(name);
        // TODO: test validations
    }

    /**
     * Batch Delete Prompts
     *
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePromptsTest() throws ApiException {
        List<String> requestBody = null;
        List<String> response = api.deletePrompts(requestBody);
        // TODO: test validations
    }

    /**
     * Check If Prompt Name Exists
     *
     * Check if the prompt name already exists.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void existsPromptNameTest() throws ApiException {
        String name = null;
        Boolean response = api.existsPromptName(name);
        // TODO: test validations
    }

    /**
     * Get Prompt Details
     *
     * Get prompt detailed information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPromptDetailsTest() throws ApiException {
        String promptId = null;
        PromptDetailsDTO response = api.getPromptDetails(promptId);
        // TODO: test validations
    }

    /**
     * Get Prompt Summary
     *
     * Get prompt summary information.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPromptSummaryTest() throws ApiException {
        String promptId = null;
        PromptSummaryDTO response = api.getPromptSummary(promptId);
        // TODO: test validations
    }

    /**
     * List Versions by Prompt Name
     *
     * List the versions and corresponding promptIds by prompt name.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listPromptVersionsByNameTest() throws ApiException {
        String name = null;
        List<PromptItemForNameDTO> response = api.listPromptVersionsByName(name);
        // TODO: test validations
    }

    /**
     * Publish Prompt
     *
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void publishPromptTest() throws ApiException {
        String promptId = null;
        String visibility = null;
        String response = api.publishPrompt(promptId, visibility);
        // TODO: test validations
    }

    /**
     * Search Prompt Details
     *
     * Same as /api/v1/prompt/search, but returns detailed information of the prompt.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPromptDetailsTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        List<PromptDetailsDTO> response = api.searchPromptDetails(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Search Prompt Summary
     *
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user&#39;s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void searchPromptSummaryTest() throws ApiException {
        PromptQueryDTO promptQueryDTO = null;
        List<PromptSummaryDTO> response = api.searchPromptSummary(promptQueryDTO);
        // TODO: test validations
    }

    /**
     * Send Prompt
     *
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void sendPromptTest() throws ApiException {
        PromptAiParamDTO promptAiParamDTO = null;
        LlmResultDTO response = api.sendPrompt(promptAiParamDTO);
        // TODO: test validations
    }

    /**
     * Send Prompt by Streaming Back
     *
     * Refer to /api/v1/prompt/send, stream back chunks of the response.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void streamSendPromptTest() throws ApiException {
        PromptAiParamDTO promptAiParamDTO = null;
        SseEmitter response = api.streamSendPrompt(promptAiParamDTO);
        // TODO: test validations
    }

    /**
     * Update Prompt
     *
     * Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updatePromptTest() throws ApiException {
        String promptId = null;
        PromptUpdateDTO promptUpdateDTO = null;
        Boolean response = api.updatePrompt(promptId, promptUpdateDTO);
        // TODO: test validations
    }

}
