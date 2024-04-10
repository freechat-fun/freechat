/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.8.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import fun.freechat.client.model.RagTaskDTO;
import fun.freechat.client.model.RagTaskDetailsDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for RagApi
 */
@Disabled
public class RagApiTest {

    private final RagApi api = new RagApi();

    /**
     * Cancel RAG Task
     *
     * Cancel a RAG task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void cancelRagTaskTest() throws ApiException {
        Long taskId = null;
        Boolean response = api.cancelRagTask(taskId);
        // TODO: test validations
    }

    /**
     * Create RAG Task
     *
     * Create a RAG task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void createRagTaskTest() throws ApiException {
        Long characterId = null;
        RagTaskDTO ragTaskDTO = null;
        Long response = api.createRagTask(characterId, ragTaskDTO);
        // TODO: test validations
    }

    /**
     * Delete RAG Task
     *
     * Delete a RAG task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void deleteRagTaskTest() throws ApiException {
        Long taskId = null;
        Boolean response = api.deleteRagTask(taskId);
        // TODO: test validations
    }

    /**
     * Get RAG Task
     *
     * Get the RAG task details.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getRagTaskTest() throws ApiException {
        Long taskId = null;
        RagTaskDetailsDTO response = api.getRagTask(taskId);
        // TODO: test validations
    }

    /**
     * Get RAG Task Status
     *
     * Get the RAG task execution status: pending | running | succeeded | failed | canceled.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getRagTaskStatusTest() throws ApiException {
        Long taskId = null;
        String response = api.getRagTaskStatus(taskId);
        // TODO: test validations
    }

    /**
     * List RAG Tasks
     *
     * List the RAG tasks by characterId.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listRagTasksTest() throws ApiException {
        Long characterId = null;
        List<RagTaskDetailsDTO> response = api.listRagTasks(characterId);
        // TODO: test validations
    }

    /**
     * Start RAG Task
     *
     * Start a RAG task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void startRagTaskTest() throws ApiException {
        Long taskId = null;
        Boolean response = api.startRagTask(taskId);
        // TODO: test validations
    }

    /**
     * Update RAG Task
     *
     * Update a RAG task.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateRagTaskTest() throws ApiException {
        Long taskId = null;
        RagTaskDTO ragTaskDTO = null;
        Boolean response = api.updateRagTask(taskId, ragTaskDTO);
        // TODO: test validations
    }

}
