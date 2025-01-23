// TODO: better import syntax?
import {BaseAPIRequestFactory, RequiredError, COLLECTION_FORMATS} from './baseapi.js';
import {Configuration} from '../configuration.js';
import {RequestContext, HttpMethod, ResponseContext, HttpFile, HttpInfo} from '../http/http.js';
import {ObjectSerializer} from '../models/ObjectSerializer.js';
import {ApiException} from './exception.js';
import {canConsumeForm, isCodeInRange} from '../util.js';
import {SecurityAuthentication} from '../auth/auth.js';



/**
 * no description
 */
export class TagManagerForBizAdminApiRequestFactory extends BaseAPIRequestFactory {

    /**
     * Create a tag, tags created by the administrator cannot be deleted by ordinary users.
     * Create Tag
     * @param referType Tag type (prompt, agent, plugin...)
     * @param referId Resource identifier of the tag
     * @param tag Tag content
     */
    public async createTag(referType: string, referId: string, tag: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'referType' is not null or undefined
        if (referType === null || referType === undefined) {
            throw new RequiredError("TagManagerForBizAdminApi", "createTag", "referType");
        }


        // verify required parameter 'referId' is not null or undefined
        if (referId === null || referId === undefined) {
            throw new RequiredError("TagManagerForBizAdminApi", "createTag", "referId");
        }


        // verify required parameter 'tag' is not null or undefined
        if (tag === null || tag === undefined) {
            throw new RequiredError("TagManagerForBizAdminApi", "createTag", "tag");
        }


        // Path Params
        const localVarPath = '/api/v2/biz/admin/tag/{referType}/{referId}/{tag}'
            .replace('{' + 'referType' + '}', encodeURIComponent(String(referType)))
            .replace('{' + 'referId' + '}', encodeURIComponent(String(referId)))
            .replace('{' + 'tag' + '}', encodeURIComponent(String(tag)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.POST);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _options?.authMethods?.default || this.configuration?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

    /**
     * Delete a tag, any tag created by anyone can be deleted.
     * Delete Tag
     * @param referType Tag type (prompt, agent, plugin...)
     * @param referId Resource identifier of the tag
     * @param tag Tag content
     */
    public async deleteTag(referType: string, referId: string, tag: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'referType' is not null or undefined
        if (referType === null || referType === undefined) {
            throw new RequiredError("TagManagerForBizAdminApi", "deleteTag", "referType");
        }


        // verify required parameter 'referId' is not null or undefined
        if (referId === null || referId === undefined) {
            throw new RequiredError("TagManagerForBizAdminApi", "deleteTag", "referId");
        }


        // verify required parameter 'tag' is not null or undefined
        if (tag === null || tag === undefined) {
            throw new RequiredError("TagManagerForBizAdminApi", "deleteTag", "tag");
        }


        // Path Params
        const localVarPath = '/api/v2/biz/admin/tag/{referType}/{referId}/{tag}'
            .replace('{' + 'referType' + '}', encodeURIComponent(String(referType)))
            .replace('{' + 'referId' + '}', encodeURIComponent(String(referId)))
            .replace('{' + 'tag' + '}', encodeURIComponent(String(tag)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.DELETE);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")


        let authMethod: SecurityAuthentication | undefined;
        // Apply auth methods
        authMethod = _config.authMethods["bearerAuth"]
        if (authMethod?.applySecurityAuthentication) {
            await authMethod?.applySecurityAuthentication(requestContext);
        }
        
        const defaultAuth: SecurityAuthentication | undefined = _options?.authMethods?.default || this.configuration?.authMethods?.default
        if (defaultAuth?.applySecurityAuthentication) {
            await defaultAuth?.applySecurityAuthentication(requestContext);
        }

        return requestContext;
    }

}

export class TagManagerForBizAdminApiResponseProcessor {

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to createTag
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async createTagWithHttpInfo(response: ResponseContext): Promise<HttpInfo<boolean >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: boolean = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "boolean", ""
            ) as boolean;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: boolean = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "boolean", ""
            ) as boolean;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to deleteTag
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async deleteTagWithHttpInfo(response: ResponseContext): Promise<HttpInfo<boolean >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: boolean = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "boolean", ""
            ) as boolean;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: boolean = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "boolean", ""
            ) as boolean;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

}
