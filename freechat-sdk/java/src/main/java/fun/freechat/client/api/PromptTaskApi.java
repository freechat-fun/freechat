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

import fun.freechat.client.ApiCallback;
import fun.freechat.client.ApiClient;
import fun.freechat.client.ApiException;
import fun.freechat.client.ApiResponse;
import fun.freechat.client.Configuration;
import fun.freechat.client.Pair;
import fun.freechat.client.ProgressRequestBody;
import fun.freechat.client.ProgressResponseBody;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;


import fun.freechat.client.model.PromptTaskDTO;
import fun.freechat.client.model.PromptTaskDetailsDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromptTaskApi {
    private ApiClient localVarApiClient;
    private int localHostIndex;
    private String localCustomBaseUrl;

    public PromptTaskApi() {
        this(Configuration.getDefaultApiClient());
    }

    public PromptTaskApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public int getHostIndex() {
        return localHostIndex;
    }

    public void setHostIndex(int hostIndex) {
        this.localHostIndex = hostIndex;
    }

    public String getCustomBaseUrl() {
        return localCustomBaseUrl;
    }

    public void setCustomBaseUrl(String customBaseUrl) {
        this.localCustomBaseUrl = customBaseUrl;
    }

    /**
     * Build call for createPromptTask
     * @param promptTaskDTO The prompt task to be added (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call createPromptTaskCall(PromptTaskDTO promptTaskDTO, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = promptTaskDTO;

        // create path and map variables
        String localVarPath = "/api/v1/prompt/task";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "text/plain"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call createPromptTaskValidateBeforeCall(PromptTaskDTO promptTaskDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'promptTaskDTO' is set
        if (promptTaskDTO == null) {
            throw new ApiException("Missing the required parameter 'promptTaskDTO' when calling createPromptTask(Async)");
        }

        return createPromptTaskCall(promptTaskDTO, _callback);

    }

    /**
     * Add Prompt Task
     * Add a prompt task.
     * @param promptTaskDTO The prompt task to be added (required)
     * @return String
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public String createPromptTask(PromptTaskDTO promptTaskDTO) throws ApiException {
        ApiResponse<String> localVarResp = createPromptTaskWithHttpInfo(promptTaskDTO);
        return localVarResp.getData();
    }

    /**
     * Add Prompt Task
     * Add a prompt task.
     * @param promptTaskDTO The prompt task to be added (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<String> createPromptTaskWithHttpInfo(PromptTaskDTO promptTaskDTO) throws ApiException {
        okhttp3.Call localVarCall = createPromptTaskValidateBeforeCall(promptTaskDTO, null);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Add Prompt Task (asynchronously)
     * Add a prompt task.
     * @param promptTaskDTO The prompt task to be added (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call createPromptTaskAsync(PromptTaskDTO promptTaskDTO, final ApiCallback<String> _callback) throws ApiException {

        okhttp3.Call localVarCall = createPromptTaskValidateBeforeCall(promptTaskDTO, _callback);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for deletePromptTask
     * @param promptTaskId The promptTaskId to be deleted (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deletePromptTaskCall(String promptTaskId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/prompt/task/{promptTaskId}"
            .replace("{" + "promptTaskId" + "}", localVarApiClient.escapeString(promptTaskId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "DELETE", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call deletePromptTaskValidateBeforeCall(String promptTaskId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'promptTaskId' is set
        if (promptTaskId == null) {
            throw new ApiException("Missing the required parameter 'promptTaskId' when calling deletePromptTask(Async)");
        }

        return deletePromptTaskCall(promptTaskId, _callback);

    }

    /**
     * Delete Prompt Task
     * Delete a prompt task.
     * @param promptTaskId The promptTaskId to be deleted (required)
     * @return Boolean
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Boolean deletePromptTask(String promptTaskId) throws ApiException {
        ApiResponse<Boolean> localVarResp = deletePromptTaskWithHttpInfo(promptTaskId);
        return localVarResp.getData();
    }

    /**
     * Delete Prompt Task
     * Delete a prompt task.
     * @param promptTaskId The promptTaskId to be deleted (required)
     * @return ApiResponse&lt;Boolean&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Boolean> deletePromptTaskWithHttpInfo(String promptTaskId) throws ApiException {
        okhttp3.Call localVarCall = deletePromptTaskValidateBeforeCall(promptTaskId, null);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Delete Prompt Task (asynchronously)
     * Delete a prompt task.
     * @param promptTaskId The promptTaskId to be deleted (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call deletePromptTaskAsync(String promptTaskId, final ApiCallback<Boolean> _callback) throws ApiException {

        okhttp3.Call localVarCall = deletePromptTaskValidateBeforeCall(promptTaskId, _callback);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getPromptTask
     * @param promptTaskId The promptTaskId to be queried (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getPromptTaskCall(String promptTaskId, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/api/v1/prompt/task/{promptTaskId}"
            .replace("{" + "promptTaskId" + "}", localVarApiClient.escapeString(promptTaskId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call getPromptTaskValidateBeforeCall(String promptTaskId, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'promptTaskId' is set
        if (promptTaskId == null) {
            throw new ApiException("Missing the required parameter 'promptTaskId' when calling getPromptTask(Async)");
        }

        return getPromptTaskCall(promptTaskId, _callback);

    }

    /**
     * Get Prompt Task
     * Get the prompt task details.
     * @param promptTaskId The promptTaskId to be queried (required)
     * @return PromptTaskDetailsDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public PromptTaskDetailsDTO getPromptTask(String promptTaskId) throws ApiException {
        ApiResponse<PromptTaskDetailsDTO> localVarResp = getPromptTaskWithHttpInfo(promptTaskId);
        return localVarResp.getData();
    }

    /**
     * Get Prompt Task
     * Get the prompt task details.
     * @param promptTaskId The promptTaskId to be queried (required)
     * @return ApiResponse&lt;PromptTaskDetailsDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<PromptTaskDetailsDTO> getPromptTaskWithHttpInfo(String promptTaskId) throws ApiException {
        okhttp3.Call localVarCall = getPromptTaskValidateBeforeCall(promptTaskId, null);
        Type localVarReturnType = new TypeToken<PromptTaskDetailsDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Prompt Task (asynchronously)
     * Get the prompt task details.
     * @param promptTaskId The promptTaskId to be queried (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getPromptTaskAsync(String promptTaskId, final ApiCallback<PromptTaskDetailsDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = getPromptTaskValidateBeforeCall(promptTaskId, _callback);
        Type localVarReturnType = new TypeToken<PromptTaskDetailsDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for updatePromptTask
     * @param promptTaskId The promptTaskId to be updated (required)
     * @param promptTaskDTO The prompt task info to be updated (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call updatePromptTaskCall(String promptTaskId, PromptTaskDTO promptTaskDTO, final ApiCallback _callback) throws ApiException {
        String basePath = null;
        // Operation Servers
        String[] localBasePaths = new String[] {  };

        // Determine Base Path to Use
        if (localCustomBaseUrl != null){
            basePath = localCustomBaseUrl;
        } else if ( localBasePaths.length > 0 ) {
            basePath = localBasePaths[localHostIndex];
        } else {
            basePath = null;
        }

        Object localVarPostBody = promptTaskDTO;

        // create path and map variables
        String localVarPath = "/api/v1/prompt/task/{promptTaskId}"
            .replace("{" + "promptTaskId" + "}", localVarApiClient.escapeString(promptTaskId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
            "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
            "application/json"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        if (localVarContentType != null) {
            localVarHeaderParams.put("Content-Type", localVarContentType);
        }

        String[] localVarAuthNames = new String[] { "bearerAuth" };
        return localVarApiClient.buildCall(basePath, localVarPath, "PUT", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call updatePromptTaskValidateBeforeCall(String promptTaskId, PromptTaskDTO promptTaskDTO, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'promptTaskId' is set
        if (promptTaskId == null) {
            throw new ApiException("Missing the required parameter 'promptTaskId' when calling updatePromptTask(Async)");
        }

        // verify the required parameter 'promptTaskDTO' is set
        if (promptTaskDTO == null) {
            throw new ApiException("Missing the required parameter 'promptTaskDTO' when calling updatePromptTask(Async)");
        }

        return updatePromptTaskCall(promptTaskId, promptTaskDTO, _callback);

    }

    /**
     * Update Prompt Task
     * Update a prompt task.
     * @param promptTaskId The promptTaskId to be updated (required)
     * @param promptTaskDTO The prompt task info to be updated (required)
     * @return Boolean
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public Boolean updatePromptTask(String promptTaskId, PromptTaskDTO promptTaskDTO) throws ApiException {
        ApiResponse<Boolean> localVarResp = updatePromptTaskWithHttpInfo(promptTaskId, promptTaskDTO);
        return localVarResp.getData();
    }

    /**
     * Update Prompt Task
     * Update a prompt task.
     * @param promptTaskId The promptTaskId to be updated (required)
     * @param promptTaskDTO The prompt task info to be updated (required)
     * @return ApiResponse&lt;Boolean&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<Boolean> updatePromptTaskWithHttpInfo(String promptTaskId, PromptTaskDTO promptTaskDTO) throws ApiException {
        okhttp3.Call localVarCall = updatePromptTaskValidateBeforeCall(promptTaskId, promptTaskDTO, null);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Update Prompt Task (asynchronously)
     * Update a prompt task.
     * @param promptTaskId The promptTaskId to be updated (required)
     * @param promptTaskDTO The prompt task info to be updated (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call updatePromptTaskAsync(String promptTaskId, PromptTaskDTO promptTaskDTO, final ApiCallback<Boolean> _callback) throws ApiException {

        okhttp3.Call localVarCall = updatePromptTaskValidateBeforeCall(promptTaskId, promptTaskDTO, _callback);
        Type localVarReturnType = new TypeToken<Boolean>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}
