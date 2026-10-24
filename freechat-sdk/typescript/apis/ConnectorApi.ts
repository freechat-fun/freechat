// TODO: better import syntax?
import {BaseAPIRequestFactory, RequiredError, COLLECTION_FORMATS} from './baseapi.js';
import {Configuration} from '../configuration.js';
import {RequestContext, HttpMethod, ResponseContext, HttpFile, HttpInfo} from '../http/http.js';
import {ObjectSerializer} from '../models/ObjectSerializer.js';
import {ApiException} from './exception.js';
import {canConsumeForm, isCodeInRange} from '../util.js';
import {SecurityAuthentication} from '../auth/auth.js';


import { ConnectorDetailsDTO } from '../models/ConnectorDetailsDTO.js';
import { ConnectorTestDTO } from '../models/ConnectorTestDTO.js';
import { ConnectorWriteDTO } from '../models/ConnectorWriteDTO.js';

/**
 * no description
 */
export class ConnectorApiRequestFactory extends BaseAPIRequestFactory {

    /**
     * Validates syntax and stores encrypted header values without contacting the endpoint. Public address checks run before network use.
     * Create My Connector
     * @param connectorWriteDTO
     */
    public async createConnector(connectorWriteDTO: ConnectorWriteDTO, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'connectorWriteDTO' is not null or undefined
        if (connectorWriteDTO === null || connectorWriteDTO === undefined) {
            throw new RequiredError("ConnectorApi", "createConnector", "connectorWriteDTO");
        }


        // Path Params
        const localVarPath = '/api/v2/connector';

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.POST);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        // Body Params
        const contentType = ObjectSerializer.getPreferredMediaType([
            "application/json"
        ]);
        requestContext.setHeaderParam("Content-Type", contentType);
        const serializedBody = ObjectSerializer.stringify(
            ObjectSerializer.serialize(connectorWriteDTO, "ConnectorWriteDTO", ""),
            contentType
        );
        requestContext.setBody(serializedBody);

        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }

        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Delete My Connector
     * @param connectorId
     * @param expectedRevision
     */
    public async deleteConnector(connectorId: string, expectedRevision: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'connectorId' is not null or undefined
        if (connectorId === null || connectorId === undefined) {
            throw new RequiredError("ConnectorApi", "deleteConnector", "connectorId");
        }


        // verify required parameter 'expectedRevision' is not null or undefined
        if (expectedRevision === null || expectedRevision === undefined) {
            throw new RequiredError("ConnectorApi", "deleteConnector", "expectedRevision");
        }


        // Path Params
        const localVarPath = '/api/v2/connector/{connectorId}'
            .replace('{' + 'connectorId' + '}', encodeURIComponent(String(connectorId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.DELETE);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (expectedRevision !== undefined) {
            requestContext.setQueryParam("expectedRevision", ObjectSerializer.serialize(expectedRevision, "string", ""));
        }


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }

        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Returns metadata and header names, never header values.
     * Get My Connector
     * @param connectorId
     */
    public async getConnector(connectorId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'connectorId' is not null or undefined
        if (connectorId === null || connectorId === undefined) {
            throw new RequiredError("ConnectorApi", "getConnector", "connectorId");
        }


        // Path Params
        const localVarPath = '/api/v2/connector/{connectorId}'
            .replace('{' + 'connectorId' + '}', encodeURIComponent(String(connectorId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }

        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * List My Connectors
     */
    public async listConnectors(_options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // Path Params
        const localVarPath = '/api/v2/connector';

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }

        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Initialize an owned HTTP or SSE MCP server and list its tools without executing any tool. Private addresses and redirects are rejected. The stored configuration revision must match.
     * Test My Connector
     * @param connectorId
     * @param expectedRevision
     */
    public async testConnector(connectorId: string, expectedRevision: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'connectorId' is not null or undefined
        if (connectorId === null || connectorId === undefined) {
            throw new RequiredError("ConnectorApi", "testConnector", "connectorId");
        }


        // verify required parameter 'expectedRevision' is not null or undefined
        if (expectedRevision === null || expectedRevision === undefined) {
            throw new RequiredError("ConnectorApi", "testConnector", "expectedRevision");
        }


        // Path Params
        const localVarPath = '/api/v2/connector/{connectorId}/test'
            .replace('{' + 'connectorId' + '}', encodeURIComponent(String(connectorId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.POST);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (expectedRevision !== undefined) {
            requestContext.setQueryParam("expectedRevision", ObjectSerializer.serialize(expectedRevision, "string", ""));
        }


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }

        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Replaces an expected revision. Explicitly retain unchanged header names; retained credentials cannot move to another origin.
     * Update My Connector
     * @param connectorId
     * @param connectorWriteDTO
     */
    public async updateConnector(connectorId: string, connectorWriteDTO: ConnectorWriteDTO, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'connectorId' is not null or undefined
        if (connectorId === null || connectorId === undefined) {
            throw new RequiredError("ConnectorApi", "updateConnector", "connectorId");
        }


        // verify required parameter 'connectorWriteDTO' is not null or undefined
        if (connectorWriteDTO === null || connectorWriteDTO === undefined) {
            throw new RequiredError("ConnectorApi", "updateConnector", "connectorWriteDTO");
        }


        // Path Params
        const localVarPath = '/api/v2/connector/{connectorId}'
            .replace('{' + 'connectorId' + '}', encodeURIComponent(String(connectorId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.PUT);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        // Body Params
        const contentType = ObjectSerializer.getPreferredMediaType([
            "application/json"
        ]);
        requestContext.setHeaderParam("Content-Type", contentType);
        const serializedBody = ObjectSerializer.stringify(
            ObjectSerializer.serialize(connectorWriteDTO, "ConnectorWriteDTO", ""),
            contentType
        );
        requestContext.setBody(serializedBody);

        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }

        const defaultAuth: SecurityAuthentication | undefined = _config?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

}

export class ConnectorApiResponseProcessor {

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to createConnector
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async createConnectorWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ConnectorDetailsDTO >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ConnectorDetailsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorDetailsDTO", ""
            ) as ConnectorDetailsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ConnectorDetailsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorDetailsDTO", ""
            ) as ConnectorDetailsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to deleteConnector
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async deleteConnectorWithHttpInfo(response: ResponseContext): Promise<HttpInfo<void >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("204", response.httpStatusCode)) {
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, undefined);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: void = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "void", ""
            ) as void;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getConnector
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getConnectorWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ConnectorDetailsDTO >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ConnectorDetailsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorDetailsDTO", ""
            ) as ConnectorDetailsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ConnectorDetailsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorDetailsDTO", ""
            ) as ConnectorDetailsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listConnectors
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listConnectorsWithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<ConnectorDetailsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<ConnectorDetailsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<ConnectorDetailsDTO>", ""
            ) as Array<ConnectorDetailsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<ConnectorDetailsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<ConnectorDetailsDTO>", ""
            ) as Array<ConnectorDetailsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to testConnector
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async testConnectorWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ConnectorTestDTO >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ConnectorTestDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorTestDTO", ""
            ) as ConnectorTestDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ConnectorTestDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorTestDTO", ""
            ) as ConnectorTestDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to updateConnector
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async updateConnectorWithHttpInfo(response: ResponseContext): Promise<HttpInfo<ConnectorDetailsDTO >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: ConnectorDetailsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorDetailsDTO", ""
            ) as ConnectorDetailsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: ConnectorDetailsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "ConnectorDetailsDTO", ""
            ) as ConnectorDetailsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

}
