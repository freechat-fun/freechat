// TODO: better import syntax?
import {BaseAPIRequestFactory, RequiredError, COLLECTION_FORMATS} from './baseapi.js';
import {Configuration} from '../configuration.js';
import {RequestContext, HttpMethod, ResponseContext, HttpFile, HttpInfo} from '../http/http.js';
import {ObjectSerializer} from '../models/ObjectSerializer.js';
import {ApiException} from './exception.js';
import {canConsumeForm, isCodeInRange} from '../util.js';
import {SecurityAuthentication} from '../auth/auth.js';


import { TgMessageDTO } from '../models/TgMessageDTO.js';

/**
 * no description
 */
export class TelegramManagerForAdminApiRequestFactory extends BaseAPIRequestFactory {

    /**
     * Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.
     * Find Telegram Chat
     * @param backendId Character backend identifier
     * @param tgChatId Telegram chat id
     */
    public async findTelegramChat(backendId: string, tgChatId: number, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'backendId' is not null or undefined
        if (backendId === null || backendId === undefined) {
            throw new RequiredError("TelegramManagerForAdminApi", "findTelegramChat", "backendId");
        }


        // verify required parameter 'tgChatId' is not null or undefined
        if (tgChatId === null || tgChatId === undefined) {
            throw new RequiredError("TelegramManagerForAdminApi", "findTelegramChat", "tgChatId");
        }


        // Path Params
        const localVarPath = '/api/v2/admin/chat/tg/{backendId}/{tgChatId}'
            .replace('{' + 'backendId' + '}', encodeURIComponent(String(backendId)))
            .replace('{' + 'tgChatId' + '}', encodeURIComponent(String(tgChatId)));

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
     * List Telegram messages recorded against the given tg_chat.chat_id, newest first.
     * List Telegram Messages
     * @param chatId tg_chat.chat_id
     * @param limit Max rows to return (default 100)
     * @param offset Row offset (default 0)
     */
    public async listTelegramMessages(chatId: string, limit?: number, offset?: number, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'chatId' is not null or undefined
        if (chatId === null || chatId === undefined) {
            throw new RequiredError("TelegramManagerForAdminApi", "listTelegramMessages", "chatId");
        }




        // Path Params
        const localVarPath = '/api/v2/admin/chat/tg/messages/{chatId}'
            .replace('{' + 'chatId' + '}', encodeURIComponent(String(chatId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (limit !== undefined) {
            requestContext.setQueryParam("limit", ObjectSerializer.serialize(limit, "number", "int32"));
        }

        // Query Params
        if (offset !== undefined) {
            requestContext.setQueryParam("offset", ObjectSerializer.serialize(offset, "number", "int32"));
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

}

export class TelegramManagerForAdminApiResponseProcessor {

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to findTelegramChat
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async findTelegramChatWithHttpInfo(response: ResponseContext): Promise<HttpInfo<string >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: string = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "string", ""
            ) as string;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: string = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "string", ""
            ) as string;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listTelegramMessages
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listTelegramMessagesWithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<TgMessageDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<TgMessageDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<TgMessageDTO>", ""
            ) as Array<TgMessageDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<TgMessageDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<TgMessageDTO>", ""
            ) as Array<TgMessageDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

}
