/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.5.0
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


import fun.freechat.client.model.AppMetaDTO;
import fun.freechat.client.model.LlmResultDTO;
import fun.freechat.client.model.OpenAiParamDTO;
import fun.freechat.client.model.QwenParamDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppMetaForAdminApi {
    private ApiClient localVarApiClient;
    private int localHostIndex;
    private String localCustomBaseUrl;

    public AppMetaForAdminApi() {
        this(Configuration.getDefaultApiClient());
    }

    public AppMetaForAdminApi(ApiClient apiClient) {
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
     * Build call for expose
     * @param openAiParam  (required)
     * @param qwenParam  (required)
     * @param aiForPromptResult  (required)
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call exposeCall(OpenAiParamDTO openAiParam, QwenParamDTO qwenParam, LlmResultDTO aiForPromptResult, final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v1/admin/app/expose";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        if (openAiParam != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("openAiParam", openAiParam));
        }

        if (qwenParam != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("qwenParam", qwenParam));
        }

        if (aiForPromptResult != null) {
            localVarQueryParams.addAll(localVarApiClient.parameterToPair("aiForPromptResult", aiForPromptResult));
        }

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
    private okhttp3.Call exposeValidateBeforeCall(OpenAiParamDTO openAiParam, QwenParamDTO qwenParam, LlmResultDTO aiForPromptResult, final ApiCallback _callback) throws ApiException {
        // verify the required parameter 'openAiParam' is set
        if (openAiParam == null) {
            throw new ApiException("Missing the required parameter 'openAiParam' when calling expose(Async)");
        }

        // verify the required parameter 'qwenParam' is set
        if (qwenParam == null) {
            throw new ApiException("Missing the required parameter 'qwenParam' when calling expose(Async)");
        }

        // verify the required parameter 'aiForPromptResult' is set
        if (aiForPromptResult == null) {
            throw new ApiException("Missing the required parameter 'aiForPromptResult' when calling expose(Async)");
        }

        return exposeCall(openAiParam, qwenParam, aiForPromptResult, _callback);

    }

    /**
     * Expose DTO definitions
     * This method does nothing.
     * @param openAiParam  (required)
     * @param qwenParam  (required)
     * @param aiForPromptResult  (required)
     * @return String
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public String expose(OpenAiParamDTO openAiParam, QwenParamDTO qwenParam, LlmResultDTO aiForPromptResult) throws ApiException {
        ApiResponse<String> localVarResp = exposeWithHttpInfo(openAiParam, qwenParam, aiForPromptResult);
        return localVarResp.getData();
    }

    /**
     * Expose DTO definitions
     * This method does nothing.
     * @param openAiParam  (required)
     * @param qwenParam  (required)
     * @param aiForPromptResult  (required)
     * @return ApiResponse&lt;String&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<String> exposeWithHttpInfo(OpenAiParamDTO openAiParam, QwenParamDTO qwenParam, LlmResultDTO aiForPromptResult) throws ApiException {
        okhttp3.Call localVarCall = exposeValidateBeforeCall(openAiParam, qwenParam, aiForPromptResult, null);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Expose DTO definitions (asynchronously)
     * This method does nothing.
     * @param openAiParam  (required)
     * @param qwenParam  (required)
     * @param aiForPromptResult  (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call exposeAsync(OpenAiParamDTO openAiParam, QwenParamDTO qwenParam, LlmResultDTO aiForPromptResult, final ApiCallback<String> _callback) throws ApiException {

        okhttp3.Call localVarCall = exposeValidateBeforeCall(openAiParam, qwenParam, aiForPromptResult, _callback);
        Type localVarReturnType = new TypeToken<String>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
    /**
     * Build call for getAppMeta
     * @param _callback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getAppMetaCall(final ApiCallback _callback) throws ApiException {
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
        String localVarPath = "/api/v1/admin/app/meta";

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
    private okhttp3.Call getAppMetaValidateBeforeCall(final ApiCallback _callback) throws ApiException {
        return getAppMetaCall(_callback);

    }

    /**
     * Get Application Information
     * Get application information to accurately locate the corresponding project version.
     * @return AppMetaDTO
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public AppMetaDTO getAppMeta() throws ApiException {
        ApiResponse<AppMetaDTO> localVarResp = getAppMetaWithHttpInfo();
        return localVarResp.getData();
    }

    /**
     * Get Application Information
     * Get application information to accurately locate the corresponding project version.
     * @return ApiResponse&lt;AppMetaDTO&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public ApiResponse<AppMetaDTO> getAppMetaWithHttpInfo() throws ApiException {
        okhttp3.Call localVarCall = getAppMetaValidateBeforeCall(null);
        Type localVarReturnType = new TypeToken<AppMetaDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Get Application Information (asynchronously)
     * Get application information to accurately locate the corresponding project version.
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
     <table summary="Response Details" border="1">
        <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
        <tr><td> 200 </td><td> OK </td><td>  -  </td></tr>
     </table>
     */
    public okhttp3.Call getAppMetaAsync(final ApiCallback<AppMetaDTO> _callback) throws ApiException {

        okhttp3.Call localVarCall = getAppMetaValidateBeforeCall(_callback);
        Type localVarReturnType = new TypeToken<AppMetaDTO>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}
