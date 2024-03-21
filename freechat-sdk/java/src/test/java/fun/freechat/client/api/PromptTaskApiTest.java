/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.6.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.PromptTaskDTO;
import fun.freechat.client.model.PromptTaskDetailsDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PromptTaskApi
 */
@Disabled
public class PromptTaskApiTest {

    private final PromptTaskApi api = new PromptTaskApi();

    /**
     * Add Prompt Task
     *
     * Add a prompt task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createPromptTaskTest() throws ApiException {
        PromptTaskDTO promptTaskDTO = null;
        String response = api.createPromptTask(promptTaskDTO);
        // TODO: test validations
    }

    /**
     * Delete Prompt Task
     *
     * Delete a prompt task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deletePromptTaskTest() throws ApiException {
        String promptTaskId = null;
        Boolean response = api.deletePromptTask(promptTaskId);
        // TODO: test validations
    }

    /**
     * Get Prompt Task
     *
     * Get the prompt task details.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getPromptTaskTest() throws ApiException {
        String promptTaskId = null;
        PromptTaskDetailsDTO response = api.getPromptTask(promptTaskId);
        // TODO: test validations
    }

    /**
     * Update Prompt Task
     *
     * Update a prompt task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updatePromptTaskTest() throws ApiException {
        String promptTaskId = null;
        PromptTaskDTO promptTaskDTO = null;
        Boolean response = api.updatePromptTask(promptTaskId, promptTaskDTO);
        // TODO: test validations
    }

}
