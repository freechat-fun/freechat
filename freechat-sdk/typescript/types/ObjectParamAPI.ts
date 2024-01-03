import { ResponseContext, RequestContext, HttpFile, HttpInfo } from '../http/http.js';
import { Configuration} from '../configuration.js'

import { AiApiKeyCreateDTO } from '../models/AiApiKeyCreateDTO.js';
import { AiApiKeyInfoDTO } from '../models/AiApiKeyInfoDTO.js';
import { AiModelInfoDTO } from '../models/AiModelInfoDTO.js';
import { ApiTokenInfoDTO } from '../models/ApiTokenInfoDTO.js';
import { AppConfigCreateDTO } from '../models/AppConfigCreateDTO.js';
import { AppConfigInfoDTO } from '../models/AppConfigInfoDTO.js';
import { AppMetaDTO } from '../models/AppMetaDTO.js';
import { CharacterBackendDTO } from '../models/CharacterBackendDTO.js';
import { CharacterBackendDetailsDTO } from '../models/CharacterBackendDetailsDTO.js';
import { CharacterCreateDTO } from '../models/CharacterCreateDTO.js';
import { CharacterDetailsDTO } from '../models/CharacterDetailsDTO.js';
import { CharacterInfoDraftDTO } from '../models/CharacterInfoDraftDTO.js';
import { CharacterItemForNameDTO } from '../models/CharacterItemForNameDTO.js';
import { CharacterQueryDTO } from '../models/CharacterQueryDTO.js';
import { CharacterQueryWhere } from '../models/CharacterQueryWhere.js';
import { CharacterSummaryDTO } from '../models/CharacterSummaryDTO.js';
import { CharacterSummaryStatsDTO } from '../models/CharacterSummaryStatsDTO.js';
import { CharacterUpdateDTO } from '../models/CharacterUpdateDTO.js';
import { ChatContentDTO } from '../models/ChatContentDTO.js';
import { ChatCreateDTO } from '../models/ChatCreateDTO.js';
import { ChatMessageDTO } from '../models/ChatMessageDTO.js';
import { ChatPromptContentDTO } from '../models/ChatPromptContentDTO.js';
import { ChatToolCallDTO } from '../models/ChatToolCallDTO.js';
import { FlowCreateDTO } from '../models/FlowCreateDTO.js';
import { FlowDetailsDTO } from '../models/FlowDetailsDTO.js';
import { FlowItemForNameDTO } from '../models/FlowItemForNameDTO.js';
import { FlowQueryDTO } from '../models/FlowQueryDTO.js';
import { FlowQueryWhere } from '../models/FlowQueryWhere.js';
import { FlowSummaryDTO } from '../models/FlowSummaryDTO.js';
import { FlowSummaryStatsDTO } from '../models/FlowSummaryStatsDTO.js';
import { FlowUpdateDTO } from '../models/FlowUpdateDTO.js';
import { InteractiveStatsDTO } from '../models/InteractiveStatsDTO.js';
import { LlmResultDTO } from '../models/LlmResultDTO.js';
import { LlmTokenUsageDTO } from '../models/LlmTokenUsageDTO.js';
import { OpenAiParamDTO } from '../models/OpenAiParamDTO.js';
import { PluginCreateDTO } from '../models/PluginCreateDTO.js';
import { PluginDetailsDTO } from '../models/PluginDetailsDTO.js';
import { PluginQueryDTO } from '../models/PluginQueryDTO.js';
import { PluginQueryWhere } from '../models/PluginQueryWhere.js';
import { PluginSummaryDTO } from '../models/PluginSummaryDTO.js';
import { PluginSummaryStatsDTO } from '../models/PluginSummaryStatsDTO.js';
import { PluginUpdateDTO } from '../models/PluginUpdateDTO.js';
import { PromptAiParamDTO } from '../models/PromptAiParamDTO.js';
import { PromptCreateDTO } from '../models/PromptCreateDTO.js';
import { PromptDetailsDTO } from '../models/PromptDetailsDTO.js';
import { PromptItemForNameDTO } from '../models/PromptItemForNameDTO.js';
import { PromptQueryDTO } from '../models/PromptQueryDTO.js';
import { PromptQueryWhere } from '../models/PromptQueryWhere.js';
import { PromptRefDTO } from '../models/PromptRefDTO.js';
import { PromptSummaryDTO } from '../models/PromptSummaryDTO.js';
import { PromptSummaryStatsDTO } from '../models/PromptSummaryStatsDTO.js';
import { PromptTaskDTO } from '../models/PromptTaskDTO.js';
import { PromptTaskDetailsDTO } from '../models/PromptTaskDetailsDTO.js';
import { PromptTemplateDTO } from '../models/PromptTemplateDTO.js';
import { PromptUpdateDTO } from '../models/PromptUpdateDTO.js';
import { QwenParamDTO } from '../models/QwenParamDTO.js';
import { SseEmitter } from '../models/SseEmitter.js';
import { UserBasicInfoDTO } from '../models/UserBasicInfoDTO.js';
import { UserDetailsDTO } from '../models/UserDetailsDTO.js';
import { UserFullDetailsDTO } from '../models/UserFullDetailsDTO.js';

import { ObservableAIServiceApi } from "./ObservableAPI.js";
import { AIServiceApiRequestFactory, AIServiceApiResponseProcessor} from "../apis/AIServiceApi.js";

export interface AIServiceApiAddAiApiKeyRequest {
    /**
     * Model call credential information
     * @type AiApiKeyCreateDTO
     * @memberof AIServiceApiaddAiApiKey
     */
    aiApiKeyCreateDTO: AiApiKeyCreateDTO
}

export interface AIServiceApiDeleteAiApiKeyRequest {
    /**
     * Credential identifier
     * @type number
     * @memberof AIServiceApideleteAiApiKey
     */
    id: number
}

export interface AIServiceApiDisableAiApiKeyRequest {
    /**
     * Credential identifier
     * @type number
     * @memberof AIServiceApidisableAiApiKey
     */
    id: number
}

export interface AIServiceApiEnableAiApiKeyRequest {
    /**
     * Credential identifier
     * @type number
     * @memberof AIServiceApienableAiApiKey
     */
    id: number
}

export interface AIServiceApiGetAiApiKeyRequest {
    /**
     * Credential identifier
     * @type number
     * @memberof AIServiceApigetAiApiKey
     */
    id: number
}

export interface AIServiceApiGetAiModelInfoRequest {
    /**
     * Model identifier
     * @type string
     * @memberof AIServiceApigetAiModelInfo
     */
    modelId: string
}

export interface AIServiceApiListAiApiKeysRequest {
    /**
     * Model provider
     * @type string
     * @memberof AIServiceApilistAiApiKeys
     */
    provider: string
}

export interface AIServiceApiListAiModelInfoRequest {
    /**
     * Maximum quantity
     * @type number
     * @memberof AIServiceApilistAiModelInfo
     */
    pageSize: number
}

export interface AIServiceApiListAiModelInfo1Request {
}

export interface AIServiceApiListAiModelInfo2Request {
    /**
     * Maximum quantity
     * @type number
     * @memberof AIServiceApilistAiModelInfo2
     */
    pageSize: number
    /**
     * Current page number
     * @type number
     * @memberof AIServiceApilistAiModelInfo2
     */
    pageNum: number
}

export class ObjectAIServiceApi {
    private api: ObservableAIServiceApi

    public constructor(configuration: Configuration, requestFactory?: AIServiceApiRequestFactory, responseProcessor?: AIServiceApiResponseProcessor) {
        this.api = new ObservableAIServiceApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Add a credential for the model service.
     * Add Model Provider Credential
     * @param param the request object
     */
    public addAiApiKeyWithHttpInfo(param: AIServiceApiAddAiApiKeyRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.addAiApiKeyWithHttpInfo(param.aiApiKeyCreateDTO,  options).toPromise();
    }

    /**
     * Add a credential for the model service.
     * Add Model Provider Credential
     * @param param the request object
     */
    public addAiApiKey(param: AIServiceApiAddAiApiKeyRequest, options?: Configuration): Promise<number> {
        return this.api.addAiApiKey(param.aiApiKeyCreateDTO,  options).toPromise();
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param param the request object
     */
    public deleteAiApiKeyWithHttpInfo(param: AIServiceApiDeleteAiApiKeyRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deleteAiApiKeyWithHttpInfo(param.id,  options).toPromise();
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param param the request object
     */
    public deleteAiApiKey(param: AIServiceApiDeleteAiApiKeyRequest, options?: Configuration): Promise<boolean> {
        return this.api.deleteAiApiKey(param.id,  options).toPromise();
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param param the request object
     */
    public disableAiApiKeyWithHttpInfo(param: AIServiceApiDisableAiApiKeyRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.disableAiApiKeyWithHttpInfo(param.id,  options).toPromise();
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param param the request object
     */
    public disableAiApiKey(param: AIServiceApiDisableAiApiKeyRequest, options?: Configuration): Promise<boolean> {
        return this.api.disableAiApiKey(param.id,  options).toPromise();
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param param the request object
     */
    public enableAiApiKeyWithHttpInfo(param: AIServiceApiEnableAiApiKeyRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.enableAiApiKeyWithHttpInfo(param.id,  options).toPromise();
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param param the request object
     */
    public enableAiApiKey(param: AIServiceApiEnableAiApiKeyRequest, options?: Configuration): Promise<boolean> {
        return this.api.enableAiApiKey(param.id,  options).toPromise();
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param param the request object
     */
    public getAiApiKeyWithHttpInfo(param: AIServiceApiGetAiApiKeyRequest, options?: Configuration): Promise<HttpInfo<AiApiKeyInfoDTO>> {
        return this.api.getAiApiKeyWithHttpInfo(param.id,  options).toPromise();
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param param the request object
     */
    public getAiApiKey(param: AIServiceApiGetAiApiKeyRequest, options?: Configuration): Promise<AiApiKeyInfoDTO> {
        return this.api.getAiApiKey(param.id,  options).toPromise();
    }

    /**
     * Return specific model information.
     * Get Model Information
     * @param param the request object
     */
    public getAiModelInfoWithHttpInfo(param: AIServiceApiGetAiModelInfoRequest, options?: Configuration): Promise<HttpInfo<AiModelInfoDTO>> {
        return this.api.getAiModelInfoWithHttpInfo(param.modelId,  options).toPromise();
    }

    /**
     * Return specific model information.
     * Get Model Information
     * @param param the request object
     */
    public getAiModelInfo(param: AIServiceApiGetAiModelInfoRequest, options?: Configuration): Promise<AiModelInfoDTO> {
        return this.api.getAiModelInfo(param.modelId,  options).toPromise();
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param param the request object
     */
    public listAiApiKeysWithHttpInfo(param: AIServiceApiListAiApiKeysRequest, options?: Configuration): Promise<HttpInfo<Array<AiApiKeyInfoDTO>>> {
        return this.api.listAiApiKeysWithHttpInfo(param.provider,  options).toPromise();
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param param the request object
     */
    public listAiApiKeys(param: AIServiceApiListAiApiKeysRequest, options?: Configuration): Promise<Array<AiApiKeyInfoDTO>> {
        return this.api.listAiApiKeys(param.provider,  options).toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param param the request object
     */
    public listAiModelInfoWithHttpInfo(param: AIServiceApiListAiModelInfoRequest, options?: Configuration): Promise<HttpInfo<Array<AiModelInfoDTO>>> {
        return this.api.listAiModelInfoWithHttpInfo(param.pageSize,  options).toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param param the request object
     */
    public listAiModelInfo(param: AIServiceApiListAiModelInfoRequest, options?: Configuration): Promise<Array<AiModelInfoDTO>> {
        return this.api.listAiModelInfo(param.pageSize,  options).toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param param the request object
     */
    public listAiModelInfo1WithHttpInfo(param: AIServiceApiListAiModelInfo1Request = {}, options?: Configuration): Promise<HttpInfo<Array<AiModelInfoDTO>>> {
        return this.api.listAiModelInfo1WithHttpInfo( options).toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param param the request object
     */
    public listAiModelInfo1(param: AIServiceApiListAiModelInfo1Request = {}, options?: Configuration): Promise<Array<AiModelInfoDTO>> {
        return this.api.listAiModelInfo1( options).toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param param the request object
     */
    public listAiModelInfo2WithHttpInfo(param: AIServiceApiListAiModelInfo2Request, options?: Configuration): Promise<HttpInfo<Array<AiModelInfoDTO>>> {
        return this.api.listAiModelInfo2WithHttpInfo(param.pageSize, param.pageNum,  options).toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param param the request object
     */
    public listAiModelInfo2(param: AIServiceApiListAiModelInfo2Request, options?: Configuration): Promise<Array<AiModelInfoDTO>> {
        return this.api.listAiModelInfo2(param.pageSize, param.pageNum,  options).toPromise();
    }

}

import { ObservableAccountApi } from "./ObservableAPI.js";
import { AccountApiRequestFactory, AccountApiResponseProcessor} from "../apis/AccountApi.js";

export interface AccountApiCreateTokenRequest {
}

export interface AccountApiCreateToken1Request {
    /**
     * Token validity duration (seconds)
     * @type number
     * @memberof AccountApicreateToken1
     */
    duration: number
}

export interface AccountApiDeleteTokenRequest {
    /**
     * Token content
     * @type string
     * @memberof AccountApideleteToken
     */
    token: string
}

export interface AccountApiDeleteTokenByIdRequest {
    /**
     * Token id
     * @type number
     * @memberof AccountApideleteTokenById
     */
    id: number
}

export interface AccountApiDisableTokenRequest {
    /**
     * Token content
     * @type string
     * @memberof AccountApidisableToken
     */
    token: string
}

export interface AccountApiDisableTokenByIdRequest {
    /**
     * Token id
     * @type number
     * @memberof AccountApidisableTokenById
     */
    id: number
}

export interface AccountApiGetTokenByIdRequest {
    /**
     * Token id
     * @type number
     * @memberof AccountApigetTokenById
     */
    id: number
}

export interface AccountApiGetUserBasicRequest {
    /**
     * Username
     * @type string
     * @memberof AccountApigetUserBasic
     */
    username: string
}

export interface AccountApiGetUserDetailsRequest {
}

export interface AccountApiListTokensRequest {
}

export interface AccountApiUpdateUserInfoRequest {
    /**
     * User information
     * @type UserDetailsDTO
     * @memberof AccountApiupdateUserInfo
     */
    userDetailsDTO: UserDetailsDTO
}

export interface AccountApiUploadUserPictureRequest {
    /**
     * User picture
     * @type HttpFile
     * @memberof AccountApiuploadUserPicture
     */
    file: HttpFile
}

export class ObjectAccountApi {
    private api: ObservableAccountApi

    public constructor(configuration: Configuration, requestFactory?: AccountApiRequestFactory, responseProcessor?: AccountApiResponseProcessor) {
        this.api = new ObservableAccountApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param param the request object
     */
    public createTokenWithHttpInfo(param: AccountApiCreateTokenRequest = {}, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createTokenWithHttpInfo( options).toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param param the request object
     */
    public createToken(param: AccountApiCreateTokenRequest = {}, options?: Configuration): Promise<string> {
        return this.api.createToken( options).toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param param the request object
     */
    public createToken1WithHttpInfo(param: AccountApiCreateToken1Request, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createToken1WithHttpInfo(param.duration,  options).toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param param the request object
     */
    public createToken1(param: AccountApiCreateToken1Request, options?: Configuration): Promise<string> {
        return this.api.createToken1(param.duration,  options).toPromise();
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param param the request object
     */
    public deleteTokenWithHttpInfo(param: AccountApiDeleteTokenRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.deleteTokenWithHttpInfo(param.token,  options).toPromise();
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param param the request object
     */
    public deleteToken(param: AccountApiDeleteTokenRequest, options?: Configuration): Promise<string> {
        return this.api.deleteToken(param.token,  options).toPromise();
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param param the request object
     */
    public deleteTokenByIdWithHttpInfo(param: AccountApiDeleteTokenByIdRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deleteTokenByIdWithHttpInfo(param.id,  options).toPromise();
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param param the request object
     */
    public deleteTokenById(param: AccountApiDeleteTokenByIdRequest, options?: Configuration): Promise<boolean> {
        return this.api.deleteTokenById(param.id,  options).toPromise();
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param param the request object
     */
    public disableTokenWithHttpInfo(param: AccountApiDisableTokenRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.disableTokenWithHttpInfo(param.token,  options).toPromise();
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param param the request object
     */
    public disableToken(param: AccountApiDisableTokenRequest, options?: Configuration): Promise<string> {
        return this.api.disableToken(param.token,  options).toPromise();
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param param the request object
     */
    public disableTokenByIdWithHttpInfo(param: AccountApiDisableTokenByIdRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.disableTokenByIdWithHttpInfo(param.id,  options).toPromise();
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param param the request object
     */
    public disableTokenById(param: AccountApiDisableTokenByIdRequest, options?: Configuration): Promise<boolean> {
        return this.api.disableTokenById(param.id,  options).toPromise();
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param param the request object
     */
    public getTokenByIdWithHttpInfo(param: AccountApiGetTokenByIdRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.getTokenByIdWithHttpInfo(param.id,  options).toPromise();
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param param the request object
     */
    public getTokenById(param: AccountApiGetTokenByIdRequest, options?: Configuration): Promise<string> {
        return this.api.getTokenById(param.id,  options).toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param param the request object
     */
    public getUserBasicWithHttpInfo(param: AccountApiGetUserBasicRequest, options?: Configuration): Promise<HttpInfo<UserBasicInfoDTO>> {
        return this.api.getUserBasicWithHttpInfo(param.username,  options).toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param param the request object
     */
    public getUserBasic(param: AccountApiGetUserBasicRequest, options?: Configuration): Promise<UserBasicInfoDTO> {
        return this.api.getUserBasic(param.username,  options).toPromise();
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     * @param param the request object
     */
    public getUserDetailsWithHttpInfo(param: AccountApiGetUserDetailsRequest = {}, options?: Configuration): Promise<HttpInfo<UserDetailsDTO>> {
        return this.api.getUserDetailsWithHttpInfo( options).toPromise();
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     * @param param the request object
     */
    public getUserDetails(param: AccountApiGetUserDetailsRequest = {}, options?: Configuration): Promise<UserDetailsDTO> {
        return this.api.getUserDetails( options).toPromise();
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     * @param param the request object
     */
    public listTokensWithHttpInfo(param: AccountApiListTokensRequest = {}, options?: Configuration): Promise<HttpInfo<Array<ApiTokenInfoDTO>>> {
        return this.api.listTokensWithHttpInfo( options).toPromise();
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     * @param param the request object
     */
    public listTokens(param: AccountApiListTokensRequest = {}, options?: Configuration): Promise<Array<ApiTokenInfoDTO>> {
        return this.api.listTokens( options).toPromise();
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param param the request object
     */
    public updateUserInfoWithHttpInfo(param: AccountApiUpdateUserInfoRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateUserInfoWithHttpInfo(param.userDetailsDTO,  options).toPromise();
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param param the request object
     */
    public updateUserInfo(param: AccountApiUpdateUserInfoRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateUserInfo(param.userDetailsDTO,  options).toPromise();
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param param the request object
     */
    public uploadUserPictureWithHttpInfo(param: AccountApiUploadUserPictureRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.uploadUserPictureWithHttpInfo(param.file,  options).toPromise();
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param param the request object
     */
    public uploadUserPicture(param: AccountApiUploadUserPictureRequest, options?: Configuration): Promise<string> {
        return this.api.uploadUserPicture(param.file,  options).toPromise();
    }

}

import { ObservableAccountManagerForAdminApi } from "./ObservableAPI.js";
import { AccountManagerForAdminApiRequestFactory, AccountManagerForAdminApiResponseProcessor} from "../apis/AccountManagerForAdminApi.js";

export interface AccountManagerForAdminApiCreateTokenForUserRequest {
    /**
     * Username
     * @type string
     * @memberof AccountManagerForAdminApicreateTokenForUser
     */
    username: string
    /**
     * Validity period (seconds)
     * @type number
     * @memberof AccountManagerForAdminApicreateTokenForUser
     */
    duration: number
}

export interface AccountManagerForAdminApiCreateUserRequest {
    /**
     * User information
     * @type UserFullDetailsDTO
     * @memberof AccountManagerForAdminApicreateUser
     */
    userFullDetailsDTO: UserFullDetailsDTO
}

export interface AccountManagerForAdminApiDeleteTokenForUserRequest {
    /**
     * API Token
     * @type string
     * @memberof AccountManagerForAdminApideleteTokenForUser
     */
    token: string
}

export interface AccountManagerForAdminApiDeleteUserRequest {
    /**
     * Username
     * @type string
     * @memberof AccountManagerForAdminApideleteUser
     */
    username: string
}

export interface AccountManagerForAdminApiDisableTokenForUserRequest {
    /**
     * API Token
     * @type string
     * @memberof AccountManagerForAdminApidisableTokenForUser
     */
    token: string
}

export interface AccountManagerForAdminApiGetDetailsOfUserRequest {
    /**
     * Username
     * @type string
     * @memberof AccountManagerForAdminApigetDetailsOfUser
     */
    username: string
}

export interface AccountManagerForAdminApiGetUserByTokenRequest {
    /**
     * API Token
     * @type string
     * @memberof AccountManagerForAdminApigetUserByToken
     */
    token: string
}

export interface AccountManagerForAdminApiListAuthoritiesOfUserRequest {
    /**
     * Username
     * @type string
     * @memberof AccountManagerForAdminApilistAuthoritiesOfUser
     */
    username: string
}

export interface AccountManagerForAdminApiListTokensOfUserRequest {
    /**
     * Username
     * @type string
     * @memberof AccountManagerForAdminApilistTokensOfUser
     */
    username: string
}

export interface AccountManagerForAdminApiListUsersRequest {
    /**
     * Maximum quantity
     * @type number
     * @memberof AccountManagerForAdminApilistUsers
     */
    pageSize: number
    /**
     * Current page number
     * @type number
     * @memberof AccountManagerForAdminApilistUsers
     */
    pageNum: number
}

export interface AccountManagerForAdminApiListUsers1Request {
    /**
     * Maximum quantity
     * @type number
     * @memberof AccountManagerForAdminApilistUsers1
     */
    pageSize: number
}

export interface AccountManagerForAdminApiListUsers2Request {
}

export interface AccountManagerForAdminApiUpdateAuthoritiesOfUserRequest {
    /**
     * Username
     * @type string
     * @memberof AccountManagerForAdminApiupdateAuthoritiesOfUser
     */
    username: string
    /**
     * Permission list
     * @type Set&lt;string&gt;
     * @memberof AccountManagerForAdminApiupdateAuthoritiesOfUser
     */
    requestBody: Set<string>
}

export interface AccountManagerForAdminApiUpdateUserRequest {
    /**
     * User information
     * @type UserFullDetailsDTO
     * @memberof AccountManagerForAdminApiupdateUser
     */
    userFullDetailsDTO: UserFullDetailsDTO
}

export class ObjectAccountManagerForAdminApi {
    private api: ObservableAccountManagerForAdminApi

    public constructor(configuration: Configuration, requestFactory?: AccountManagerForAdminApiRequestFactory, responseProcessor?: AccountManagerForAdminApiResponseProcessor) {
        this.api = new ObservableAccountManagerForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create an API Token for a specified user, valid for duration seconds.
     * Create API Token for User.
     * @param param the request object
     */
    public createTokenForUserWithHttpInfo(param: AccountManagerForAdminApiCreateTokenForUserRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createTokenForUserWithHttpInfo(param.username, param.duration,  options).toPromise();
    }

    /**
     * Create an API Token for a specified user, valid for duration seconds.
     * Create API Token for User.
     * @param param the request object
     */
    public createTokenForUser(param: AccountManagerForAdminApiCreateTokenForUserRequest, options?: Configuration): Promise<string> {
        return this.api.createTokenForUser(param.username, param.duration,  options).toPromise();
    }

    /**
     * Create user.
     * Create User
     * @param param the request object
     */
    public createUserWithHttpInfo(param: AccountManagerForAdminApiCreateUserRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.createUserWithHttpInfo(param.userFullDetailsDTO,  options).toPromise();
    }

    /**
     * Create user.
     * Create User
     * @param param the request object
     */
    public createUser(param: AccountManagerForAdminApiCreateUserRequest, options?: Configuration): Promise<boolean> {
        return this.api.createUser(param.userFullDetailsDTO,  options).toPromise();
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param param the request object
     */
    public deleteTokenForUserWithHttpInfo(param: AccountManagerForAdminApiDeleteTokenForUserRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deleteTokenForUserWithHttpInfo(param.token,  options).toPromise();
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param param the request object
     */
    public deleteTokenForUser(param: AccountManagerForAdminApiDeleteTokenForUserRequest, options?: Configuration): Promise<boolean> {
        return this.api.deleteTokenForUser(param.token,  options).toPromise();
    }

    /**
     * Delete user by username.
     * Delete User
     * @param param the request object
     */
    public deleteUserWithHttpInfo(param: AccountManagerForAdminApiDeleteUserRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deleteUserWithHttpInfo(param.username,  options).toPromise();
    }

    /**
     * Delete user by username.
     * Delete User
     * @param param the request object
     */
    public deleteUser(param: AccountManagerForAdminApiDeleteUserRequest, options?: Configuration): Promise<boolean> {
        return this.api.deleteUser(param.username,  options).toPromise();
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param param the request object
     */
    public disableTokenForUserWithHttpInfo(param: AccountManagerForAdminApiDisableTokenForUserRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.disableTokenForUserWithHttpInfo(param.token,  options).toPromise();
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param param the request object
     */
    public disableTokenForUser(param: AccountManagerForAdminApiDisableTokenForUserRequest, options?: Configuration): Promise<boolean> {
        return this.api.disableTokenForUser(param.token,  options).toPromise();
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param param the request object
     */
    public getDetailsOfUserWithHttpInfo(param: AccountManagerForAdminApiGetDetailsOfUserRequest, options?: Configuration): Promise<HttpInfo<UserDetailsDTO>> {
        return this.api.getDetailsOfUserWithHttpInfo(param.username,  options).toPromise();
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param param the request object
     */
    public getDetailsOfUser(param: AccountManagerForAdminApiGetDetailsOfUserRequest, options?: Configuration): Promise<UserDetailsDTO> {
        return this.api.getDetailsOfUser(param.username,  options).toPromise();
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param param the request object
     */
    public getUserByTokenWithHttpInfo(param: AccountManagerForAdminApiGetUserByTokenRequest, options?: Configuration): Promise<HttpInfo<UserDetailsDTO>> {
        return this.api.getUserByTokenWithHttpInfo(param.token,  options).toPromise();
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param param the request object
     */
    public getUserByToken(param: AccountManagerForAdminApiGetUserByTokenRequest, options?: Configuration): Promise<UserDetailsDTO> {
        return this.api.getUserByToken(param.token,  options).toPromise();
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param param the request object
     */
    public listAuthoritiesOfUserWithHttpInfo(param: AccountManagerForAdminApiListAuthoritiesOfUserRequest, options?: Configuration): Promise<HttpInfo<Set<string>>> {
        return this.api.listAuthoritiesOfUserWithHttpInfo(param.username,  options).toPromise();
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param param the request object
     */
    public listAuthoritiesOfUser(param: AccountManagerForAdminApiListAuthoritiesOfUserRequest, options?: Configuration): Promise<Set<string>> {
        return this.api.listAuthoritiesOfUser(param.username,  options).toPromise();
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param param the request object
     */
    public listTokensOfUserWithHttpInfo(param: AccountManagerForAdminApiListTokensOfUserRequest, options?: Configuration): Promise<HttpInfo<Array<ApiTokenInfoDTO>>> {
        return this.api.listTokensOfUserWithHttpInfo(param.username,  options).toPromise();
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param param the request object
     */
    public listTokensOfUser(param: AccountManagerForAdminApiListTokensOfUserRequest, options?: Configuration): Promise<Array<ApiTokenInfoDTO>> {
        return this.api.listTokensOfUser(param.username,  options).toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param param the request object
     */
    public listUsersWithHttpInfo(param: AccountManagerForAdminApiListUsersRequest, options?: Configuration): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        return this.api.listUsersWithHttpInfo(param.pageSize, param.pageNum,  options).toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param param the request object
     */
    public listUsers(param: AccountManagerForAdminApiListUsersRequest, options?: Configuration): Promise<Array<UserBasicInfoDTO>> {
        return this.api.listUsers(param.pageSize, param.pageNum,  options).toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param param the request object
     */
    public listUsers1WithHttpInfo(param: AccountManagerForAdminApiListUsers1Request, options?: Configuration): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        return this.api.listUsers1WithHttpInfo(param.pageSize,  options).toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param param the request object
     */
    public listUsers1(param: AccountManagerForAdminApiListUsers1Request, options?: Configuration): Promise<Array<UserBasicInfoDTO>> {
        return this.api.listUsers1(param.pageSize,  options).toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param param the request object
     */
    public listUsers2WithHttpInfo(param: AccountManagerForAdminApiListUsers2Request = {}, options?: Configuration): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        return this.api.listUsers2WithHttpInfo( options).toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param param the request object
     */
    public listUsers2(param: AccountManagerForAdminApiListUsers2Request = {}, options?: Configuration): Promise<Array<UserBasicInfoDTO>> {
        return this.api.listUsers2( options).toPromise();
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param param the request object
     */
    public updateAuthoritiesOfUserWithHttpInfo(param: AccountManagerForAdminApiUpdateAuthoritiesOfUserRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateAuthoritiesOfUserWithHttpInfo(param.username, param.requestBody,  options).toPromise();
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param param the request object
     */
    public updateAuthoritiesOfUser(param: AccountManagerForAdminApiUpdateAuthoritiesOfUserRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateAuthoritiesOfUser(param.username, param.requestBody,  options).toPromise();
    }

    /**
     * Update user information.
     * Update User
     * @param param the request object
     */
    public updateUserWithHttpInfo(param: AccountManagerForAdminApiUpdateUserRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateUserWithHttpInfo(param.userFullDetailsDTO,  options).toPromise();
    }

    /**
     * Update user information.
     * Update User
     * @param param the request object
     */
    public updateUser(param: AccountManagerForAdminApiUpdateUserRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateUser(param.userFullDetailsDTO,  options).toPromise();
    }

}

import { ObservableAppConfigForAdminApi } from "./ObservableAPI.js";
import { AppConfigForAdminApiRequestFactory, AppConfigForAdminApiResponseProcessor} from "../apis/AppConfigForAdminApi.js";

export interface AppConfigForAdminApiGetAppConfigRequest {
    /**
     * Configuration name
     * @type string
     * @memberof AppConfigForAdminApigetAppConfig
     */
    name: string
}

export interface AppConfigForAdminApiGetAppConfigByVersionRequest {
    /**
     * Configuration name
     * @type string
     * @memberof AppConfigForAdminApigetAppConfigByVersion
     */
    name: string
    /**
     * Configuration version
     * @type number
     * @memberof AppConfigForAdminApigetAppConfigByVersion
     */
    version: number
}

export interface AppConfigForAdminApiListAppConfigNamesRequest {
}

export interface AppConfigForAdminApiPublishAppConfigRequest {
    /**
     * Configuration information
     * @type AppConfigCreateDTO
     * @memberof AppConfigForAdminApipublishAppConfig
     */
    appConfigCreateDTO: AppConfigCreateDTO
}

export class ObjectAppConfigForAdminApi {
    private api: ObservableAppConfigForAdminApi

    public constructor(configuration: Configuration, requestFactory?: AppConfigForAdminApiRequestFactory, responseProcessor?: AppConfigForAdminApiResponseProcessor) {
        this.api = new ObservableAppConfigForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Get the latest configuration information of the application by name.
     * Get Configuration
     * @param param the request object
     */
    public getAppConfigWithHttpInfo(param: AppConfigForAdminApiGetAppConfigRequest, options?: Configuration): Promise<HttpInfo<AppConfigInfoDTO>> {
        return this.api.getAppConfigWithHttpInfo(param.name,  options).toPromise();
    }

    /**
     * Get the latest configuration information of the application by name.
     * Get Configuration
     * @param param the request object
     */
    public getAppConfig(param: AppConfigForAdminApiGetAppConfigRequest, options?: Configuration): Promise<AppConfigInfoDTO> {
        return this.api.getAppConfig(param.name,  options).toPromise();
    }

    /**
     * Get the configuration information of the application by name and version.
     * Get Specified Version of Configuration
     * @param param the request object
     */
    public getAppConfigByVersionWithHttpInfo(param: AppConfigForAdminApiGetAppConfigByVersionRequest, options?: Configuration): Promise<HttpInfo<AppConfigInfoDTO>> {
        return this.api.getAppConfigByVersionWithHttpInfo(param.name, param.version,  options).toPromise();
    }

    /**
     * Get the configuration information of the application by name and version.
     * Get Specified Version of Configuration
     * @param param the request object
     */
    public getAppConfigByVersion(param: AppConfigForAdminApiGetAppConfigByVersionRequest, options?: Configuration): Promise<AppConfigInfoDTO> {
        return this.api.getAppConfigByVersion(param.name, param.version,  options).toPromise();
    }

    /**
     * List all application configuration names.
     * List Configuration Names
     * @param param the request object
     */
    public listAppConfigNamesWithHttpInfo(param: AppConfigForAdminApiListAppConfigNamesRequest = {}, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.listAppConfigNamesWithHttpInfo( options).toPromise();
    }

    /**
     * List all application configuration names.
     * List Configuration Names
     * @param param the request object
     */
    public listAppConfigNames(param: AppConfigForAdminApiListAppConfigNamesRequest = {}, options?: Configuration): Promise<Array<string>> {
        return this.api.listAppConfigNames( options).toPromise();
    }

    /**
     * Publish application configuration, return configuration version.
     * Publish Configuration
     * @param param the request object
     */
    public publishAppConfigWithHttpInfo(param: AppConfigForAdminApiPublishAppConfigRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.publishAppConfigWithHttpInfo(param.appConfigCreateDTO,  options).toPromise();
    }

    /**
     * Publish application configuration, return configuration version.
     * Publish Configuration
     * @param param the request object
     */
    public publishAppConfig(param: AppConfigForAdminApiPublishAppConfigRequest, options?: Configuration): Promise<number> {
        return this.api.publishAppConfig(param.appConfigCreateDTO,  options).toPromise();
    }

}

import { ObservableAppMetaForAdminApi } from "./ObservableAPI.js";
import { AppMetaForAdminApiRequestFactory, AppMetaForAdminApiResponseProcessor} from "../apis/AppMetaForAdminApi.js";

export interface AppMetaForAdminApiExposeRequest {
    /**
     * 
     * @type OpenAiParamDTO
     * @memberof AppMetaForAdminApiexpose
     */
    openAiParam: OpenAiParamDTO
    /**
     * 
     * @type QwenParamDTO
     * @memberof AppMetaForAdminApiexpose
     */
    qwenParam: QwenParamDTO
    /**
     * 
     * @type LlmResultDTO
     * @memberof AppMetaForAdminApiexpose
     */
    aiForPromptResult: LlmResultDTO
}

export interface AppMetaForAdminApiGetAppMetaRequest {
}

export class ObjectAppMetaForAdminApi {
    private api: ObservableAppMetaForAdminApi

    public constructor(configuration: Configuration, requestFactory?: AppMetaForAdminApiRequestFactory, responseProcessor?: AppMetaForAdminApiResponseProcessor) {
        this.api = new ObservableAppMetaForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * This method does nothing.
     * Expose DTO definitions
     * @param param the request object
     */
    public exposeWithHttpInfo(param: AppMetaForAdminApiExposeRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.exposeWithHttpInfo(param.openAiParam, param.qwenParam, param.aiForPromptResult,  options).toPromise();
    }

    /**
     * This method does nothing.
     * Expose DTO definitions
     * @param param the request object
     */
    public expose(param: AppMetaForAdminApiExposeRequest, options?: Configuration): Promise<string> {
        return this.api.expose(param.openAiParam, param.qwenParam, param.aiForPromptResult,  options).toPromise();
    }

    /**
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     * @param param the request object
     */
    public getAppMetaWithHttpInfo(param: AppMetaForAdminApiGetAppMetaRequest = {}, options?: Configuration): Promise<HttpInfo<AppMetaDTO>> {
        return this.api.getAppMetaWithHttpInfo( options).toPromise();
    }

    /**
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     * @param param the request object
     */
    public getAppMeta(param: AppMetaForAdminApiGetAppMetaRequest = {}, options?: Configuration): Promise<AppMetaDTO> {
        return this.api.getAppMeta( options).toPromise();
    }

}

import { ObservableCharacterApi } from "./ObservableAPI.js";
import { CharacterApiRequestFactory, CharacterApiResponseProcessor} from "../apis/CharacterApi.js";

export interface CharacterApiAddCharacterBackendRequest {
    /**
     * The characterId to be added a backend
     * @type string
     * @memberof CharacterApiaddCharacterBackend
     */
    characterId: string
    /**
     * The character backend to be added
     * @type CharacterBackendDTO
     * @memberof CharacterApiaddCharacterBackend
     */
    characterBackendDTO: CharacterBackendDTO
}

export interface CharacterApiBatchSearchCharacterDetailsRequest {
    /**
     * Query conditions
     * @type Array&lt;CharacterQueryDTO&gt;
     * @memberof CharacterApibatchSearchCharacterDetails
     */
    characterQueryDTO: Array<CharacterQueryDTO>
}

export interface CharacterApiBatchSearchCharacterSummaryRequest {
    /**
     * Query conditions
     * @type Array&lt;CharacterQueryDTO&gt;
     * @memberof CharacterApibatchSearchCharacterSummary
     */
    characterQueryDTO: Array<CharacterQueryDTO>
}

export interface CharacterApiCloneCharacterRequest {
    /**
     * The referenced characterId
     * @type string
     * @memberof CharacterApicloneCharacter
     */
    characterId: string
}

export interface CharacterApiCountCharactersRequest {
    /**
     * Query conditions
     * @type CharacterQueryDTO
     * @memberof CharacterApicountCharacters
     */
    characterQueryDTO: CharacterQueryDTO
}

export interface CharacterApiCreateCharacterRequest {
    /**
     * Information of the character to be created
     * @type CharacterCreateDTO
     * @memberof CharacterApicreateCharacter
     */
    characterCreateDTO: CharacterCreateDTO
}

export interface CharacterApiDeleteCharacterRequest {
    /**
     * The characterId to be deleted
     * @type string
     * @memberof CharacterApideleteCharacter
     */
    characterId: string
}

export interface CharacterApiDeleteChatRequest {
    /**
     * Chat session identifier
     * @type string
     * @memberof CharacterApideleteChat
     */
    chatId: string
}

export interface CharacterApiGetCharacterDetailsRequest {
    /**
     * CharacterId to be obtained
     * @type string
     * @memberof CharacterApigetCharacterDetails
     */
    characterId: string
}

export interface CharacterApiGetCharacterLatestIdByNameRequest {
    /**
     * Character name
     * @type string
     * @memberof CharacterApigetCharacterLatestIdByName
     */
    name: string
}

export interface CharacterApiGetCharacterSummaryRequest {
    /**
     * CharacterId to be obtained
     * @type string
     * @memberof CharacterApigetCharacterSummary
     */
    characterId: string
}

export interface CharacterApiGetDefaultCharacterBackendRequest {
    /**
     * The characterId to be queried
     * @type string
     * @memberof CharacterApigetDefaultCharacterBackend
     */
    characterId: string
}

export interface CharacterApiListCharacterBackendIdsRequest {
    /**
     * The characterId to be queried
     * @type string
     * @memberof CharacterApilistCharacterBackendIds
     */
    characterId: string
}

export interface CharacterApiListCharacterVersionsByNameRequest {
    /**
     * Character name
     * @type string
     * @memberof CharacterApilistCharacterVersionsByName
     */
    name: string
}

export interface CharacterApiListMessagesRequest {
    /**
     * Chat session identifier
     * @type string
     * @memberof CharacterApilistMessages
     */
    chatId: string
    /**
     * Messages limit
     * @type number
     * @memberof CharacterApilistMessages
     */
    limit: number
}

export interface CharacterApiListMessages1Request {
    /**
     * Chat session identifier
     * @type string
     * @memberof CharacterApilistMessages1
     */
    chatId: string
    /**
     * Messages limit
     * @type number
     * @memberof CharacterApilistMessages1
     */
    limit: number
    /**
     * Messages offset (from new to old)
     * @type number
     * @memberof CharacterApilistMessages1
     */
    offset: number
}

export interface CharacterApiListMessages2Request {
    /**
     * Chat session identifier
     * @type string
     * @memberof CharacterApilistMessages2
     */
    chatId: string
}

export interface CharacterApiPublishCharacterRequest {
    /**
     * The characterId to be published
     * @type string
     * @memberof CharacterApipublishCharacter
     */
    characterId: string
}

export interface CharacterApiPublishCharacter1Request {
    /**
     * The characterId to be published
     * @type string
     * @memberof CharacterApipublishCharacter1
     */
    characterId: string
    /**
     * Visibility: public | private | ...
     * @type string
     * @memberof CharacterApipublishCharacter1
     */
    visibility: string
}

export interface CharacterApiRemoveCharacterBackendRequest {
    /**
     * The characterBackendId to be removed
     * @type string
     * @memberof CharacterApiremoveCharacterBackend
     */
    characterBackendId: string
}

export interface CharacterApiSearchCharacterDetailsRequest {
    /**
     * Query conditions
     * @type CharacterQueryDTO
     * @memberof CharacterApisearchCharacterDetails
     */
    characterQueryDTO: CharacterQueryDTO
}

export interface CharacterApiSearchCharacterSummaryRequest {
    /**
     * Query conditions
     * @type CharacterQueryDTO
     * @memberof CharacterApisearchCharacterSummary
     */
    characterQueryDTO: CharacterQueryDTO
}

export interface CharacterApiSendMessageRequest {
    /**
     * Chat session identifier
     * @type string
     * @memberof CharacterApisendMessage
     */
    chatId: string
    /**
     * Chat content
     * @type ChatContentDTO
     * @memberof CharacterApisendMessage
     */
    chatContentDTO: ChatContentDTO
}

export interface CharacterApiSetDefaultCharacterBackendRequest {
    /**
     * The characterBackendId to be set to default
     * @type string
     * @memberof CharacterApisetDefaultCharacterBackend
     */
    characterBackendId: string
}

export interface CharacterApiStartChatRequest {
    /**
     * Parameters for starting a chat session
     * @type ChatCreateDTO
     * @memberof CharacterApistartChat
     */
    chatCreateDTO: ChatCreateDTO
}

export interface CharacterApiStreamSendMessageRequest {
    /**
     * Chat session identifier
     * @type string
     * @memberof CharacterApistreamSendMessage
     */
    chatId: string
    /**
     * Chat content
     * @type ChatContentDTO
     * @memberof CharacterApistreamSendMessage
     */
    chatContentDTO: ChatContentDTO
}

export interface CharacterApiUpdateCharacterRequest {
    /**
     * The characterId to be updated
     * @type string
     * @memberof CharacterApiupdateCharacter
     */
    characterId: string
    /**
     * The character information to be updated
     * @type CharacterUpdateDTO
     * @memberof CharacterApiupdateCharacter
     */
    characterUpdateDTO: CharacterUpdateDTO
}

export interface CharacterApiUpdateCharacterBackendRequest {
    /**
     * The characterBackendId to be updated
     * @type string
     * @memberof CharacterApiupdateCharacterBackend
     */
    characterBackendId: string
    /**
     * The character backend configuration to be updated
     * @type CharacterBackendDTO
     * @memberof CharacterApiupdateCharacterBackend
     */
    characterBackendDTO: CharacterBackendDTO
}

export interface CharacterApiUploadCharacterAvatarRequest {
    /**
     * Character avatar
     * @type HttpFile
     * @memberof CharacterApiuploadCharacterAvatar
     */
    file: HttpFile
}

export interface CharacterApiUploadCharacterPictureRequest {
    /**
     * Character picture
     * @type HttpFile
     * @memberof CharacterApiuploadCharacterPicture
     */
    file: HttpFile
}

export class ObjectCharacterApi {
    private api: ObservableCharacterApi

    public constructor(configuration: Configuration, requestFactory?: CharacterApiRequestFactory, responseProcessor?: CharacterApiResponseProcessor) {
        this.api = new ObservableCharacterApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Add a backend configuration for a character.
     * Add Character Backend
     * @param param the request object
     */
    public addCharacterBackendWithHttpInfo(param: CharacterApiAddCharacterBackendRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.addCharacterBackendWithHttpInfo(param.characterId, param.characterBackendDTO,  options).toPromise();
    }

    /**
     * Add a backend configuration for a character.
     * Add Character Backend
     * @param param the request object
     */
    public addCharacterBackend(param: CharacterApiAddCharacterBackendRequest, options?: Configuration): Promise<string> {
        return this.api.addCharacterBackend(param.characterId, param.characterBackendDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/details/search.
     * Batch Search Character Details
     * @param param the request object
     */
    public batchSearchCharacterDetailsWithHttpInfo(param: CharacterApiBatchSearchCharacterDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<Array<CharacterDetailsDTO>>>> {
        return this.api.batchSearchCharacterDetailsWithHttpInfo(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/details/search.
     * Batch Search Character Details
     * @param param the request object
     */
    public batchSearchCharacterDetails(param: CharacterApiBatchSearchCharacterDetailsRequest, options?: Configuration): Promise<Array<Array<CharacterDetailsDTO>>> {
        return this.api.batchSearchCharacterDetails(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/search.
     * Batch Search Character Summaries
     * @param param the request object
     */
    public batchSearchCharacterSummaryWithHttpInfo(param: CharacterApiBatchSearchCharacterSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<Array<CharacterSummaryDTO>>>> {
        return this.api.batchSearchCharacterSummaryWithHttpInfo(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/search.
     * Batch Search Character Summaries
     * @param param the request object
     */
    public batchSearchCharacterSummary(param: CharacterApiBatchSearchCharacterSummaryRequest, options?: Configuration): Promise<Array<Array<CharacterSummaryDTO>>> {
        return this.api.batchSearchCharacterSummary(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param param the request object
     */
    public cloneCharacterWithHttpInfo(param: CharacterApiCloneCharacterRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.cloneCharacterWithHttpInfo(param.characterId,  options).toPromise();
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param param the request object
     */
    public cloneCharacter(param: CharacterApiCloneCharacterRequest, options?: Configuration): Promise<string> {
        return this.api.cloneCharacter(param.characterId,  options).toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param param the request object
     */
    public countCharactersWithHttpInfo(param: CharacterApiCountCharactersRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.countCharactersWithHttpInfo(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param param the request object
     */
    public countCharacters(param: CharacterApiCountCharactersRequest, options?: Configuration): Promise<number> {
        return this.api.countCharacters(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Create a character.
     * Create Character
     * @param param the request object
     */
    public createCharacterWithHttpInfo(param: CharacterApiCreateCharacterRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createCharacterWithHttpInfo(param.characterCreateDTO,  options).toPromise();
    }

    /**
     * Create a character.
     * Create Character
     * @param param the request object
     */
    public createCharacter(param: CharacterApiCreateCharacterRequest, options?: Configuration): Promise<string> {
        return this.api.createCharacter(param.characterCreateDTO,  options).toPromise();
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param param the request object
     */
    public deleteCharacterWithHttpInfo(param: CharacterApiDeleteCharacterRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deleteCharacterWithHttpInfo(param.characterId,  options).toPromise();
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param param the request object
     */
    public deleteCharacter(param: CharacterApiDeleteCharacterRequest, options?: Configuration): Promise<boolean> {
        return this.api.deleteCharacter(param.characterId,  options).toPromise();
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param param the request object
     */
    public deleteChatWithHttpInfo(param: CharacterApiDeleteChatRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deleteChatWithHttpInfo(param.chatId,  options).toPromise();
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param param the request object
     */
    public deleteChat(param: CharacterApiDeleteChatRequest, options?: Configuration): Promise<boolean> {
        return this.api.deleteChat(param.chatId,  options).toPromise();
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param param the request object
     */
    public getCharacterDetailsWithHttpInfo(param: CharacterApiGetCharacterDetailsRequest, options?: Configuration): Promise<HttpInfo<CharacterDetailsDTO>> {
        return this.api.getCharacterDetailsWithHttpInfo(param.characterId,  options).toPromise();
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param param the request object
     */
    public getCharacterDetails(param: CharacterApiGetCharacterDetailsRequest, options?: Configuration): Promise<CharacterDetailsDTO> {
        return this.api.getCharacterDetails(param.characterId,  options).toPromise();
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param param the request object
     */
    public getCharacterLatestIdByNameWithHttpInfo(param: CharacterApiGetCharacterLatestIdByNameRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.getCharacterLatestIdByNameWithHttpInfo(param.name,  options).toPromise();
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param param the request object
     */
    public getCharacterLatestIdByName(param: CharacterApiGetCharacterLatestIdByNameRequest, options?: Configuration): Promise<string> {
        return this.api.getCharacterLatestIdByName(param.name,  options).toPromise();
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param param the request object
     */
    public getCharacterSummaryWithHttpInfo(param: CharacterApiGetCharacterSummaryRequest, options?: Configuration): Promise<HttpInfo<CharacterSummaryDTO>> {
        return this.api.getCharacterSummaryWithHttpInfo(param.characterId,  options).toPromise();
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param param the request object
     */
    public getCharacterSummary(param: CharacterApiGetCharacterSummaryRequest, options?: Configuration): Promise<CharacterSummaryDTO> {
        return this.api.getCharacterSummary(param.characterId,  options).toPromise();
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param param the request object
     */
    public getDefaultCharacterBackendWithHttpInfo(param: CharacterApiGetDefaultCharacterBackendRequest, options?: Configuration): Promise<HttpInfo<CharacterBackendDetailsDTO>> {
        return this.api.getDefaultCharacterBackendWithHttpInfo(param.characterId,  options).toPromise();
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param param the request object
     */
    public getDefaultCharacterBackend(param: CharacterApiGetDefaultCharacterBackendRequest, options?: Configuration): Promise<CharacterBackendDetailsDTO> {
        return this.api.getDefaultCharacterBackend(param.characterId,  options).toPromise();
    }

    /**
     * List Character Backend identifiers.
     * List Character Backend ids
     * @param param the request object
     */
    public listCharacterBackendIdsWithHttpInfo(param: CharacterApiListCharacterBackendIdsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.listCharacterBackendIdsWithHttpInfo(param.characterId,  options).toPromise();
    }

    /**
     * List Character Backend identifiers.
     * List Character Backend ids
     * @param param the request object
     */
    public listCharacterBackendIds(param: CharacterApiListCharacterBackendIdsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.listCharacterBackendIds(param.characterId,  options).toPromise();
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param param the request object
     */
    public listCharacterVersionsByNameWithHttpInfo(param: CharacterApiListCharacterVersionsByNameRequest, options?: Configuration): Promise<HttpInfo<Array<CharacterItemForNameDTO>>> {
        return this.api.listCharacterVersionsByNameWithHttpInfo(param.name,  options).toPromise();
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param param the request object
     */
    public listCharacterVersionsByName(param: CharacterApiListCharacterVersionsByNameRequest, options?: Configuration): Promise<Array<CharacterItemForNameDTO>> {
        return this.api.listCharacterVersionsByName(param.name,  options).toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param param the request object
     */
    public listMessagesWithHttpInfo(param: CharacterApiListMessagesRequest, options?: Configuration): Promise<HttpInfo<Array<ChatMessageDTO>>> {
        return this.api.listMessagesWithHttpInfo(param.chatId, param.limit,  options).toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param param the request object
     */
    public listMessages(param: CharacterApiListMessagesRequest, options?: Configuration): Promise<Array<ChatMessageDTO>> {
        return this.api.listMessages(param.chatId, param.limit,  options).toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param param the request object
     */
    public listMessages1WithHttpInfo(param: CharacterApiListMessages1Request, options?: Configuration): Promise<HttpInfo<Array<ChatMessageDTO>>> {
        return this.api.listMessages1WithHttpInfo(param.chatId, param.limit, param.offset,  options).toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param param the request object
     */
    public listMessages1(param: CharacterApiListMessages1Request, options?: Configuration): Promise<Array<ChatMessageDTO>> {
        return this.api.listMessages1(param.chatId, param.limit, param.offset,  options).toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param param the request object
     */
    public listMessages2WithHttpInfo(param: CharacterApiListMessages2Request, options?: Configuration): Promise<HttpInfo<Array<ChatMessageDTO>>> {
        return this.api.listMessages2WithHttpInfo(param.chatId,  options).toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param param the request object
     */
    public listMessages2(param: CharacterApiListMessages2Request, options?: Configuration): Promise<Array<ChatMessageDTO>> {
        return this.api.listMessages2(param.chatId,  options).toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param param the request object
     */
    public publishCharacterWithHttpInfo(param: CharacterApiPublishCharacterRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.publishCharacterWithHttpInfo(param.characterId,  options).toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param param the request object
     */
    public publishCharacter(param: CharacterApiPublishCharacterRequest, options?: Configuration): Promise<string> {
        return this.api.publishCharacter(param.characterId,  options).toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param param the request object
     */
    public publishCharacter1WithHttpInfo(param: CharacterApiPublishCharacter1Request, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.publishCharacter1WithHttpInfo(param.characterId, param.visibility,  options).toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param param the request object
     */
    public publishCharacter1(param: CharacterApiPublishCharacter1Request, options?: Configuration): Promise<string> {
        return this.api.publishCharacter1(param.characterId, param.visibility,  options).toPromise();
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param param the request object
     */
    public removeCharacterBackendWithHttpInfo(param: CharacterApiRemoveCharacterBackendRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.removeCharacterBackendWithHttpInfo(param.characterBackendId,  options).toPromise();
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param param the request object
     */
    public removeCharacterBackend(param: CharacterApiRemoveCharacterBackendRequest, options?: Configuration): Promise<boolean> {
        return this.api.removeCharacterBackend(param.characterBackendId,  options).toPromise();
    }

    /**
     * Same as /api/v1/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param param the request object
     */
    public searchCharacterDetailsWithHttpInfo(param: CharacterApiSearchCharacterDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<CharacterDetailsDTO>>> {
        return this.api.searchCharacterDetailsWithHttpInfo(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Same as /api/v1/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param param the request object
     */
    public searchCharacterDetails(param: CharacterApiSearchCharacterDetailsRequest, options?: Configuration): Promise<Array<CharacterDetailsDTO>> {
        return this.api.searchCharacterDetails(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param param the request object
     */
    public searchCharacterSummaryWithHttpInfo(param: CharacterApiSearchCharacterSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryDTO>>> {
        return this.api.searchCharacterSummaryWithHttpInfo(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param param the request object
     */
    public searchCharacterSummary(param: CharacterApiSearchCharacterSummaryRequest, options?: Configuration): Promise<Array<CharacterSummaryDTO>> {
        return this.api.searchCharacterSummary(param.characterQueryDTO,  options).toPromise();
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param param the request object
     */
    public sendMessageWithHttpInfo(param: CharacterApiSendMessageRequest, options?: Configuration): Promise<HttpInfo<LlmResultDTO>> {
        return this.api.sendMessageWithHttpInfo(param.chatId, param.chatContentDTO,  options).toPromise();
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param param the request object
     */
    public sendMessage(param: CharacterApiSendMessageRequest, options?: Configuration): Promise<LlmResultDTO> {
        return this.api.sendMessage(param.chatId, param.chatContentDTO,  options).toPromise();
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param param the request object
     */
    public setDefaultCharacterBackendWithHttpInfo(param: CharacterApiSetDefaultCharacterBackendRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.setDefaultCharacterBackendWithHttpInfo(param.characterBackendId,  options).toPromise();
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param param the request object
     */
    public setDefaultCharacterBackend(param: CharacterApiSetDefaultCharacterBackendRequest, options?: Configuration): Promise<boolean> {
        return this.api.setDefaultCharacterBackend(param.characterBackendId,  options).toPromise();
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param param the request object
     */
    public startChatWithHttpInfo(param: CharacterApiStartChatRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.startChatWithHttpInfo(param.chatCreateDTO,  options).toPromise();
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param param the request object
     */
    public startChat(param: CharacterApiStartChatRequest, options?: Configuration): Promise<string> {
        return this.api.startChat(param.chatCreateDTO,  options).toPromise();
    }

    /**
     * Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param param the request object
     */
    public streamSendMessageWithHttpInfo(param: CharacterApiStreamSendMessageRequest, options?: Configuration): Promise<HttpInfo<SseEmitter>> {
        return this.api.streamSendMessageWithHttpInfo(param.chatId, param.chatContentDTO,  options).toPromise();
    }

    /**
     * Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param param the request object
     */
    public streamSendMessage(param: CharacterApiStreamSendMessageRequest, options?: Configuration): Promise<SseEmitter> {
        return this.api.streamSendMessage(param.chatId, param.chatContentDTO,  options).toPromise();
    }

    /**
     * Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param param the request object
     */
    public updateCharacterWithHttpInfo(param: CharacterApiUpdateCharacterRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateCharacterWithHttpInfo(param.characterId, param.characterUpdateDTO,  options).toPromise();
    }

    /**
     * Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param param the request object
     */
    public updateCharacter(param: CharacterApiUpdateCharacterRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateCharacter(param.characterId, param.characterUpdateDTO,  options).toPromise();
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param param the request object
     */
    public updateCharacterBackendWithHttpInfo(param: CharacterApiUpdateCharacterBackendRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateCharacterBackendWithHttpInfo(param.characterBackendId, param.characterBackendDTO,  options).toPromise();
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param param the request object
     */
    public updateCharacterBackend(param: CharacterApiUpdateCharacterBackendRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateCharacterBackend(param.characterBackendId, param.characterBackendDTO,  options).toPromise();
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param param the request object
     */
    public uploadCharacterAvatarWithHttpInfo(param: CharacterApiUploadCharacterAvatarRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.uploadCharacterAvatarWithHttpInfo(param.file,  options).toPromise();
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param param the request object
     */
    public uploadCharacterAvatar(param: CharacterApiUploadCharacterAvatarRequest, options?: Configuration): Promise<string> {
        return this.api.uploadCharacterAvatar(param.file,  options).toPromise();
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param param the request object
     */
    public uploadCharacterPictureWithHttpInfo(param: CharacterApiUploadCharacterPictureRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.uploadCharacterPictureWithHttpInfo(param.file,  options).toPromise();
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param param the request object
     */
    public uploadCharacterPicture(param: CharacterApiUploadCharacterPictureRequest, options?: Configuration): Promise<string> {
        return this.api.uploadCharacterPicture(param.file,  options).toPromise();
    }

}

import { ObservableEncryptionManagerForAdminApi } from "./ObservableAPI.js";
import { EncryptionManagerForAdminApiRequestFactory, EncryptionManagerForAdminApiResponseProcessor} from "../apis/EncryptionManagerForAdminApi.js";

export interface EncryptionManagerForAdminApiEncryptTextRequest {
    /**
     * Text to be encrypted
     * @type string
     * @memberof EncryptionManagerForAdminApiencryptText
     */
    text: string
}

export class ObjectEncryptionManagerForAdminApi {
    private api: ObservableEncryptionManagerForAdminApi

    public constructor(configuration: Configuration, requestFactory?: EncryptionManagerForAdminApiRequestFactory, responseProcessor?: EncryptionManagerForAdminApiResponseProcessor) {
        this.api = new ObservableEncryptionManagerForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Encrypt a piece of text with the built-in key.
     * Encrypt Text
     * @param param the request object
     */
    public encryptTextWithHttpInfo(param: EncryptionManagerForAdminApiEncryptTextRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.encryptTextWithHttpInfo(param.text,  options).toPromise();
    }

    /**
     * Encrypt a piece of text with the built-in key.
     * Encrypt Text
     * @param param the request object
     */
    public encryptText(param: EncryptionManagerForAdminApiEncryptTextRequest, options?: Configuration): Promise<string> {
        return this.api.encryptText(param.text,  options).toPromise();
    }

}

import { ObservableFlowApi } from "./ObservableAPI.js";
import { FlowApiRequestFactory, FlowApiResponseProcessor} from "../apis/FlowApi.js";

export interface FlowApiBatchSearchFlowDetailsRequest {
    /**
     * Query conditions
     * @type Array&lt;FlowQueryDTO&gt;
     * @memberof FlowApibatchSearchFlowDetails
     */
    flowQueryDTO: Array<FlowQueryDTO>
}

export interface FlowApiBatchSearchFlowSummaryRequest {
    /**
     * Query conditions
     * @type Array&lt;FlowQueryDTO&gt;
     * @memberof FlowApibatchSearchFlowSummary
     */
    flowQueryDTO: Array<FlowQueryDTO>
}

export interface FlowApiCloneFlowRequest {
    /**
     * The referenced flowId
     * @type string
     * @memberof FlowApicloneFlow
     */
    flowId: string
}

export interface FlowApiCloneFlowsRequest {
    /**
     * List of flow information to be created
     * @type Array&lt;string&gt;
     * @memberof FlowApicloneFlows
     */
    requestBody: Array<string>
}

export interface FlowApiCountFlowsRequest {
    /**
     * Query conditions
     * @type FlowQueryDTO
     * @memberof FlowApicountFlows
     */
    flowQueryDTO: FlowQueryDTO
}

export interface FlowApiCreateFlowRequest {
    /**
     * Information of the flow to be created
     * @type FlowCreateDTO
     * @memberof FlowApicreateFlow
     */
    flowCreateDTO: FlowCreateDTO
}

export interface FlowApiCreateFlowsRequest {
    /**
     * List of flow information to be created
     * @type Array&lt;FlowCreateDTO&gt;
     * @memberof FlowApicreateFlows
     */
    flowCreateDTO: Array<FlowCreateDTO>
}

export interface FlowApiDeleteFlowRequest {
    /**
     * FlowId to be deleted
     * @type string
     * @memberof FlowApideleteFlow
     */
    flowId: string
}

export interface FlowApiDeleteFlowsRequest {
    /**
     * List of flowId to be deleted
     * @type Array&lt;string&gt;
     * @memberof FlowApideleteFlows
     */
    requestBody: Array<string>
}

export interface FlowApiGetFlowDetailsRequest {
    /**
     * FlowId to be obtained
     * @type string
     * @memberof FlowApigetFlowDetails
     */
    flowId: string
}

export interface FlowApiGetFlowSummaryRequest {
    /**
     * flowId to be obtained
     * @type string
     * @memberof FlowApigetFlowSummary
     */
    flowId: string
}

export interface FlowApiListFlowVersionsByNameRequest {
    /**
     * Flow name
     * @type string
     * @memberof FlowApilistFlowVersionsByName
     */
    name: string
}

export interface FlowApiPublishFlowRequest {
    /**
     * The flowId to be published
     * @type string
     * @memberof FlowApipublishFlow
     */
    flowId: string
    /**
     * Visibility: public | private | ...
     * @type string
     * @memberof FlowApipublishFlow
     */
    visibility: string
}

export interface FlowApiSearchFlowDetailsRequest {
    /**
     * Query conditions
     * @type FlowQueryDTO
     * @memberof FlowApisearchFlowDetails
     */
    flowQueryDTO: FlowQueryDTO
}

export interface FlowApiSearchFlowSummaryRequest {
    /**
     * Query conditions
     * @type FlowQueryDTO
     * @memberof FlowApisearchFlowSummary
     */
    flowQueryDTO: FlowQueryDTO
}

export interface FlowApiUpdateFlowRequest {
    /**
     * FlowId to be updated
     * @type string
     * @memberof FlowApiupdateFlow
     */
    flowId: string
    /**
     * Flow information to be updated
     * @type FlowUpdateDTO
     * @memberof FlowApiupdateFlow
     */
    flowUpdateDTO: FlowUpdateDTO
}

export class ObjectFlowApi {
    private api: ObservableFlowApi

    public constructor(configuration: Configuration, requestFactory?: FlowApiRequestFactory, responseProcessor?: FlowApiResponseProcessor) {
        this.api = new ObservableFlowApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Batch call shortcut for /api/v1/flow/details/search.
     * Batch Search Flow Details
     * @param param the request object
     */
    public batchSearchFlowDetailsWithHttpInfo(param: FlowApiBatchSearchFlowDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<Array<FlowDetailsDTO>>>> {
        return this.api.batchSearchFlowDetailsWithHttpInfo(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/flow/details/search.
     * Batch Search Flow Details
     * @param param the request object
     */
    public batchSearchFlowDetails(param: FlowApiBatchSearchFlowDetailsRequest, options?: Configuration): Promise<Array<Array<FlowDetailsDTO>>> {
        return this.api.batchSearchFlowDetails(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/flow/search.
     * Batch Search Flow Summaries
     * @param param the request object
     */
    public batchSearchFlowSummaryWithHttpInfo(param: FlowApiBatchSearchFlowSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<Array<FlowSummaryDTO>>>> {
        return this.api.batchSearchFlowSummaryWithHttpInfo(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/flow/search.
     * Batch Search Flow Summaries
     * @param param the request object
     */
    public batchSearchFlowSummary(param: FlowApiBatchSearchFlowSummaryRequest, options?: Configuration): Promise<Array<Array<FlowSummaryDTO>>> {
        return this.api.batchSearchFlowSummary(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 
     * Clone Flow
     * @param param the request object
     */
    public cloneFlowWithHttpInfo(param: FlowApiCloneFlowRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.cloneFlowWithHttpInfo(param.flowId,  options).toPromise();
    }

    /**
     * Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 
     * Clone Flow
     * @param param the request object
     */
    public cloneFlow(param: FlowApiCloneFlowRequest, options?: Configuration): Promise<string> {
        return this.api.cloneFlow(param.flowId,  options).toPromise();
    }

    /**
     * Batch clone multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Clone Flows
     * @param param the request object
     */
    public cloneFlowsWithHttpInfo(param: FlowApiCloneFlowsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.cloneFlowsWithHttpInfo(param.requestBody,  options).toPromise();
    }

    /**
     * Batch clone multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Clone Flows
     * @param param the request object
     */
    public cloneFlows(param: FlowApiCloneFlowsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.cloneFlows(param.requestBody,  options).toPromise();
    }

    /**
     * Calculate the number of flows according to the specified query conditions.
     * Calculate Number of Flows
     * @param param the request object
     */
    public countFlowsWithHttpInfo(param: FlowApiCountFlowsRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.countFlowsWithHttpInfo(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Calculate the number of flows according to the specified query conditions.
     * Calculate Number of Flows
     * @param param the request object
     */
    public countFlows(param: FlowApiCountFlowsRequest, options?: Configuration): Promise<number> {
        return this.api.countFlows(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Flow
     * @param param the request object
     */
    public createFlowWithHttpInfo(param: FlowApiCreateFlowRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createFlowWithHttpInfo(param.flowCreateDTO,  options).toPromise();
    }

    /**
     * Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Flow
     * @param param the request object
     */
    public createFlow(param: FlowApiCreateFlowRequest, options?: Configuration): Promise<string> {
        return this.api.createFlow(param.flowCreateDTO,  options).toPromise();
    }

    /**
     * Batch create multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Create Flows
     * @param param the request object
     */
    public createFlowsWithHttpInfo(param: FlowApiCreateFlowsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.createFlowsWithHttpInfo(param.flowCreateDTO,  options).toPromise();
    }

    /**
     * Batch create multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Create Flows
     * @param param the request object
     */
    public createFlows(param: FlowApiCreateFlowsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.createFlows(param.flowCreateDTO,  options).toPromise();
    }

    /**
     * Delete flow. Return success or failure.
     * Delete Flow
     * @param param the request object
     */
    public deleteFlowWithHttpInfo(param: FlowApiDeleteFlowRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deleteFlowWithHttpInfo(param.flowId,  options).toPromise();
    }

    /**
     * Delete flow. Return success or failure.
     * Delete Flow
     * @param param the request object
     */
    public deleteFlow(param: FlowApiDeleteFlowRequest, options?: Configuration): Promise<boolean> {
        return this.api.deleteFlow(param.flowId,  options).toPromise();
    }

    /**
     * Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.
     * Batch Delete Flows
     * @param param the request object
     */
    public deleteFlowsWithHttpInfo(param: FlowApiDeleteFlowsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.deleteFlowsWithHttpInfo(param.requestBody,  options).toPromise();
    }

    /**
     * Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.
     * Batch Delete Flows
     * @param param the request object
     */
    public deleteFlows(param: FlowApiDeleteFlowsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.deleteFlows(param.requestBody,  options).toPromise();
    }

    /**
     * Get flow detailed information.
     * Get Flow Details
     * @param param the request object
     */
    public getFlowDetailsWithHttpInfo(param: FlowApiGetFlowDetailsRequest, options?: Configuration): Promise<HttpInfo<FlowDetailsDTO>> {
        return this.api.getFlowDetailsWithHttpInfo(param.flowId,  options).toPromise();
    }

    /**
     * Get flow detailed information.
     * Get Flow Details
     * @param param the request object
     */
    public getFlowDetails(param: FlowApiGetFlowDetailsRequest, options?: Configuration): Promise<FlowDetailsDTO> {
        return this.api.getFlowDetails(param.flowId,  options).toPromise();
    }

    /**
     * Get flow summary information.
     * Get Flow Summary
     * @param param the request object
     */
    public getFlowSummaryWithHttpInfo(param: FlowApiGetFlowSummaryRequest, options?: Configuration): Promise<HttpInfo<FlowSummaryDTO>> {
        return this.api.getFlowSummaryWithHttpInfo(param.flowId,  options).toPromise();
    }

    /**
     * Get flow summary information.
     * Get Flow Summary
     * @param param the request object
     */
    public getFlowSummary(param: FlowApiGetFlowSummaryRequest, options?: Configuration): Promise<FlowSummaryDTO> {
        return this.api.getFlowSummary(param.flowId,  options).toPromise();
    }

    /**
     * List the versions and corresponding flowIds by flow name.
     * List Versions by Flow Name
     * @param param the request object
     */
    public listFlowVersionsByNameWithHttpInfo(param: FlowApiListFlowVersionsByNameRequest, options?: Configuration): Promise<HttpInfo<Array<FlowItemForNameDTO>>> {
        return this.api.listFlowVersionsByNameWithHttpInfo(param.name,  options).toPromise();
    }

    /**
     * List the versions and corresponding flowIds by flow name.
     * List Versions by Flow Name
     * @param param the request object
     */
    public listFlowVersionsByName(param: FlowApiListFlowVersionsByNameRequest, options?: Configuration): Promise<Array<FlowItemForNameDTO>> {
        return this.api.listFlowVersionsByName(param.name,  options).toPromise();
    }

    /**
     * Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.
     * Publish Flow
     * @param param the request object
     */
    public publishFlowWithHttpInfo(param: FlowApiPublishFlowRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.publishFlowWithHttpInfo(param.flowId, param.visibility,  options).toPromise();
    }

    /**
     * Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.
     * Publish Flow
     * @param param the request object
     */
    public publishFlow(param: FlowApiPublishFlowRequest, options?: Configuration): Promise<string> {
        return this.api.publishFlow(param.flowId, param.visibility,  options).toPromise();
    }

    /**
     * Same as /api/v1/flow/search, but returns detailed information of the flow.
     * Search Flow Details
     * @param param the request object
     */
    public searchFlowDetailsWithHttpInfo(param: FlowApiSearchFlowDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<FlowDetailsDTO>>> {
        return this.api.searchFlowDetailsWithHttpInfo(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Same as /api/v1/flow/search, but returns detailed information of the flow.
     * Search Flow Details
     * @param param the request object
     */
    public searchFlowDetails(param: FlowApiSearchFlowDetailsRequest, options?: Configuration): Promise<Array<FlowDetailsDTO>> {
        return this.api.searchFlowDetails(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 
     * Search Flow Summary
     * @param param the request object
     */
    public searchFlowSummaryWithHttpInfo(param: FlowApiSearchFlowSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<FlowSummaryDTO>>> {
        return this.api.searchFlowSummaryWithHttpInfo(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 
     * Search Flow Summary
     * @param param the request object
     */
    public searchFlowSummary(param: FlowApiSearchFlowSummaryRequest, options?: Configuration): Promise<Array<FlowSummaryDTO>> {
        return this.api.searchFlowSummary(param.flowQueryDTO,  options).toPromise();
    }

    /**
     * Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.
     * Update Flow
     * @param param the request object
     */
    public updateFlowWithHttpInfo(param: FlowApiUpdateFlowRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateFlowWithHttpInfo(param.flowId, param.flowUpdateDTO,  options).toPromise();
    }

    /**
     * Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.
     * Update Flow
     * @param param the request object
     */
    public updateFlow(param: FlowApiUpdateFlowRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateFlow(param.flowId, param.flowUpdateDTO,  options).toPromise();
    }

}

import { ObservableInteractiveStatisticsApi } from "./ObservableAPI.js";
import { InteractiveStatisticsApiRequestFactory, InteractiveStatisticsApiResponseProcessor} from "../apis/InteractiveStatisticsApi.js";

export interface InteractiveStatisticsApiAddStatisticRequest {
    /**
     * Resource type: prompt | flow | plugin
     * @type string
     * @memberof InteractiveStatisticsApiaddStatistic
     */
    infoType: string
    /**
     * Unique resource identifier
     * @type string
     * @memberof InteractiveStatisticsApiaddStatistic
     */
    infoId: string
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApiaddStatistic
     */
    statsType: string
    /**
     * Delta in statistical value
     * @type number
     * @memberof InteractiveStatisticsApiaddStatistic
     */
    delta: number
}

export interface InteractiveStatisticsApiGetScoreRequest {
    /**
     * Resource type: prompt | flow | plugin
     * @type string
     * @memberof InteractiveStatisticsApigetScore
     */
    infoType: string
    /**
     * Unique resource identifier
     * @type string
     * @memberof InteractiveStatisticsApigetScore
     */
    infoId: string
}

export interface InteractiveStatisticsApiGetStatisticRequest {
    /**
     * Resource type: prompt | flow | plugin
     * @type string
     * @memberof InteractiveStatisticsApigetStatistic
     */
    infoType: string
    /**
     * Unique resource identifier
     * @type string
     * @memberof InteractiveStatisticsApigetStatistic
     */
    infoId: string
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApigetStatistic
     */
    statsType: string
}

export interface InteractiveStatisticsApiGetStatisticsRequest {
    /**
     * Resource type: prompt | flow | plugin
     * @type string
     * @memberof InteractiveStatisticsApigetStatistics
     */
    infoType: string
    /**
     * Unique resource identifier
     * @type string
     * @memberof InteractiveStatisticsApigetStatistics
     */
    infoId: string
}

export interface InteractiveStatisticsApiIncreaseStatisticRequest {
    /**
     * Resource type: prompt | flow | plugin
     * @type string
     * @memberof InteractiveStatisticsApiincreaseStatistic
     */
    infoType: string
    /**
     * Unique resource identifier
     * @type string
     * @memberof InteractiveStatisticsApiincreaseStatistic
     */
    infoId: string
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApiincreaseStatistic
     */
    statsType: string
}

export interface InteractiveStatisticsApiListCharactersByStatisticRequest {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistCharactersByStatistic
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistCharactersByStatistic
     */
    pageSize: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistCharactersByStatistic
     */
    asc?: string
}

export interface InteractiveStatisticsApiListCharactersByStatistic1Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistCharactersByStatistic1
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistCharactersByStatistic1
     */
    pageSize: number
    /**
     * Current page number
     * @type number
     * @memberof InteractiveStatisticsApilistCharactersByStatistic1
     */
    pageNum: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistCharactersByStatistic1
     */
    asc?: string
}

export interface InteractiveStatisticsApiListCharactersByStatistic2Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistCharactersByStatistic2
     */
    statsType: string
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistCharactersByStatistic2
     */
    asc?: string
}

export interface InteractiveStatisticsApiListFlowsByStatisticRequest {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistFlowsByStatistic
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistFlowsByStatistic
     */
    pageSize: number
    /**
     * Current page number
     * @type number
     * @memberof InteractiveStatisticsApilistFlowsByStatistic
     */
    pageNum: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistFlowsByStatistic
     */
    asc?: string
}

export interface InteractiveStatisticsApiListFlowsByStatistic1Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistFlowsByStatistic1
     */
    statsType: string
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistFlowsByStatistic1
     */
    asc?: string
}

export interface InteractiveStatisticsApiListFlowsByStatistic2Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistFlowsByStatistic2
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistFlowsByStatistic2
     */
    pageSize: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistFlowsByStatistic2
     */
    asc?: string
}

export interface InteractiveStatisticsApiListPluginsByStatisticRequest {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistPluginsByStatistic
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistPluginsByStatistic
     */
    pageSize: number
    /**
     * Current page number
     * @type number
     * @memberof InteractiveStatisticsApilistPluginsByStatistic
     */
    pageNum: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistPluginsByStatistic
     */
    asc?: string
}

export interface InteractiveStatisticsApiListPluginsByStatistic1Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistPluginsByStatistic1
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistPluginsByStatistic1
     */
    pageSize: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistPluginsByStatistic1
     */
    asc?: string
}

export interface InteractiveStatisticsApiListPluginsByStatistic2Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistPluginsByStatistic2
     */
    statsType: string
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistPluginsByStatistic2
     */
    asc?: string
}

export interface InteractiveStatisticsApiListPromptsByStatisticRequest {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistPromptsByStatistic
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistPromptsByStatistic
     */
    pageSize: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistPromptsByStatistic
     */
    asc?: string
}

export interface InteractiveStatisticsApiListPromptsByStatistic1Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistPromptsByStatistic1
     */
    statsType: string
    /**
     * Maximum quantity
     * @type number
     * @memberof InteractiveStatisticsApilistPromptsByStatistic1
     */
    pageSize: number
    /**
     * Current page number
     * @type number
     * @memberof InteractiveStatisticsApilistPromptsByStatistic1
     */
    pageNum: number
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistPromptsByStatistic1
     */
    asc?: string
}

export interface InteractiveStatisticsApiListPromptsByStatistic2Request {
    /**
     * Statistics type: view_count | refer_count | recommend_count | score
     * @type string
     * @memberof InteractiveStatisticsApilistPromptsByStatistic2
     */
    statsType: string
    /**
     * Default is descending order, set asc&#x3D;1 for ascending order
     * @type string
     * @memberof InteractiveStatisticsApilistPromptsByStatistic2
     */
    asc?: string
}

export class ObjectInteractiveStatisticsApi {
    private api: ObservableInteractiveStatisticsApi

    public constructor(configuration: Configuration, requestFactory?: InteractiveStatisticsApiRequestFactory, responseProcessor?: InteractiveStatisticsApiResponseProcessor) {
        this.api = new ObservableInteractiveStatisticsApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * Add Statistics
     * @param param the request object
     */
    public addStatisticWithHttpInfo(param: InteractiveStatisticsApiAddStatisticRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.addStatisticWithHttpInfo(param.infoType, param.infoId, param.statsType, param.delta,  options).toPromise();
    }

    /**
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * Add Statistics
     * @param param the request object
     */
    public addStatistic(param: InteractiveStatisticsApiAddStatisticRequest, options?: Configuration): Promise<number> {
        return this.api.addStatistic(param.infoType, param.infoId, param.statsType, param.delta,  options).toPromise();
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param param the request object
     */
    public getScoreWithHttpInfo(param: InteractiveStatisticsApiGetScoreRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.getScoreWithHttpInfo(param.infoType, param.infoId,  options).toPromise();
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param param the request object
     */
    public getScore(param: InteractiveStatisticsApiGetScoreRequest, options?: Configuration): Promise<number> {
        return this.api.getScore(param.infoType, param.infoId,  options).toPromise();
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param param the request object
     */
    public getStatisticWithHttpInfo(param: InteractiveStatisticsApiGetStatisticRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.getStatisticWithHttpInfo(param.infoType, param.infoId, param.statsType,  options).toPromise();
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param param the request object
     */
    public getStatistic(param: InteractiveStatisticsApiGetStatisticRequest, options?: Configuration): Promise<number> {
        return this.api.getStatistic(param.infoType, param.infoId, param.statsType,  options).toPromise();
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param param the request object
     */
    public getStatisticsWithHttpInfo(param: InteractiveStatisticsApiGetStatisticsRequest, options?: Configuration): Promise<HttpInfo<InteractiveStatsDTO>> {
        return this.api.getStatisticsWithHttpInfo(param.infoType, param.infoId,  options).toPromise();
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param param the request object
     */
    public getStatistics(param: InteractiveStatisticsApiGetStatisticsRequest, options?: Configuration): Promise<InteractiveStatsDTO> {
        return this.api.getStatistics(param.infoType, param.infoId,  options).toPromise();
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param param the request object
     */
    public increaseStatisticWithHttpInfo(param: InteractiveStatisticsApiIncreaseStatisticRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.increaseStatisticWithHttpInfo(param.infoType, param.infoId, param.statsType,  options).toPromise();
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param param the request object
     */
    public increaseStatistic(param: InteractiveStatisticsApiIncreaseStatisticRequest, options?: Configuration): Promise<number> {
        return this.api.increaseStatistic(param.infoType, param.infoId, param.statsType,  options).toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param param the request object
     */
    public listCharactersByStatisticWithHttpInfo(param: InteractiveStatisticsApiListCharactersByStatisticRequest, options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        return this.api.listCharactersByStatisticWithHttpInfo(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param param the request object
     */
    public listCharactersByStatistic(param: InteractiveStatisticsApiListCharactersByStatisticRequest, options?: Configuration): Promise<Array<CharacterSummaryStatsDTO>> {
        return this.api.listCharactersByStatistic(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param param the request object
     */
    public listCharactersByStatistic1WithHttpInfo(param: InteractiveStatisticsApiListCharactersByStatistic1Request, options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        return this.api.listCharactersByStatistic1WithHttpInfo(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param param the request object
     */
    public listCharactersByStatistic1(param: InteractiveStatisticsApiListCharactersByStatistic1Request, options?: Configuration): Promise<Array<CharacterSummaryStatsDTO>> {
        return this.api.listCharactersByStatistic1(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param param the request object
     */
    public listCharactersByStatistic2WithHttpInfo(param: InteractiveStatisticsApiListCharactersByStatistic2Request, options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        return this.api.listCharactersByStatistic2WithHttpInfo(param.statsType, param.asc,  options).toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param param the request object
     */
    public listCharactersByStatistic2(param: InteractiveStatisticsApiListCharactersByStatistic2Request, options?: Configuration): Promise<Array<CharacterSummaryStatsDTO>> {
        return this.api.listCharactersByStatistic2(param.statsType, param.asc,  options).toPromise();
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param param the request object
     */
    public listFlowsByStatisticWithHttpInfo(param: InteractiveStatisticsApiListFlowsByStatisticRequest, options?: Configuration): Promise<HttpInfo<Array<FlowSummaryStatsDTO>>> {
        return this.api.listFlowsByStatisticWithHttpInfo(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param param the request object
     */
    public listFlowsByStatistic(param: InteractiveStatisticsApiListFlowsByStatisticRequest, options?: Configuration): Promise<Array<FlowSummaryStatsDTO>> {
        return this.api.listFlowsByStatistic(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param param the request object
     */
    public listFlowsByStatistic1WithHttpInfo(param: InteractiveStatisticsApiListFlowsByStatistic1Request, options?: Configuration): Promise<HttpInfo<Array<FlowSummaryStatsDTO>>> {
        return this.api.listFlowsByStatistic1WithHttpInfo(param.statsType, param.asc,  options).toPromise();
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param param the request object
     */
    public listFlowsByStatistic1(param: InteractiveStatisticsApiListFlowsByStatistic1Request, options?: Configuration): Promise<Array<FlowSummaryStatsDTO>> {
        return this.api.listFlowsByStatistic1(param.statsType, param.asc,  options).toPromise();
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param param the request object
     */
    public listFlowsByStatistic2WithHttpInfo(param: InteractiveStatisticsApiListFlowsByStatistic2Request, options?: Configuration): Promise<HttpInfo<Array<FlowSummaryStatsDTO>>> {
        return this.api.listFlowsByStatistic2WithHttpInfo(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param param the request object
     */
    public listFlowsByStatistic2(param: InteractiveStatisticsApiListFlowsByStatistic2Request, options?: Configuration): Promise<Array<FlowSummaryStatsDTO>> {
        return this.api.listFlowsByStatistic2(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param param the request object
     */
    public listPluginsByStatisticWithHttpInfo(param: InteractiveStatisticsApiListPluginsByStatisticRequest, options?: Configuration): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        return this.api.listPluginsByStatisticWithHttpInfo(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param param the request object
     */
    public listPluginsByStatistic(param: InteractiveStatisticsApiListPluginsByStatisticRequest, options?: Configuration): Promise<Array<PluginSummaryStatsDTO>> {
        return this.api.listPluginsByStatistic(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param param the request object
     */
    public listPluginsByStatistic1WithHttpInfo(param: InteractiveStatisticsApiListPluginsByStatistic1Request, options?: Configuration): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        return this.api.listPluginsByStatistic1WithHttpInfo(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param param the request object
     */
    public listPluginsByStatistic1(param: InteractiveStatisticsApiListPluginsByStatistic1Request, options?: Configuration): Promise<Array<PluginSummaryStatsDTO>> {
        return this.api.listPluginsByStatistic1(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param param the request object
     */
    public listPluginsByStatistic2WithHttpInfo(param: InteractiveStatisticsApiListPluginsByStatistic2Request, options?: Configuration): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        return this.api.listPluginsByStatistic2WithHttpInfo(param.statsType, param.asc,  options).toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param param the request object
     */
    public listPluginsByStatistic2(param: InteractiveStatisticsApiListPluginsByStatistic2Request, options?: Configuration): Promise<Array<PluginSummaryStatsDTO>> {
        return this.api.listPluginsByStatistic2(param.statsType, param.asc,  options).toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param param the request object
     */
    public listPromptsByStatisticWithHttpInfo(param: InteractiveStatisticsApiListPromptsByStatisticRequest, options?: Configuration): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        return this.api.listPromptsByStatisticWithHttpInfo(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param param the request object
     */
    public listPromptsByStatistic(param: InteractiveStatisticsApiListPromptsByStatisticRequest, options?: Configuration): Promise<Array<PromptSummaryStatsDTO>> {
        return this.api.listPromptsByStatistic(param.statsType, param.pageSize, param.asc,  options).toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param param the request object
     */
    public listPromptsByStatistic1WithHttpInfo(param: InteractiveStatisticsApiListPromptsByStatistic1Request, options?: Configuration): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        return this.api.listPromptsByStatistic1WithHttpInfo(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param param the request object
     */
    public listPromptsByStatistic1(param: InteractiveStatisticsApiListPromptsByStatistic1Request, options?: Configuration): Promise<Array<PromptSummaryStatsDTO>> {
        return this.api.listPromptsByStatistic1(param.statsType, param.pageSize, param.pageNum, param.asc,  options).toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param param the request object
     */
    public listPromptsByStatistic2WithHttpInfo(param: InteractiveStatisticsApiListPromptsByStatistic2Request, options?: Configuration): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        return this.api.listPromptsByStatistic2WithHttpInfo(param.statsType, param.asc,  options).toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param param the request object
     */
    public listPromptsByStatistic2(param: InteractiveStatisticsApiListPromptsByStatistic2Request, options?: Configuration): Promise<Array<PromptSummaryStatsDTO>> {
        return this.api.listPromptsByStatistic2(param.statsType, param.asc,  options).toPromise();
    }

}

import { ObservableOrganizationApi } from "./ObservableAPI.js";
import { OrganizationApiRequestFactory, OrganizationApiResponseProcessor} from "../apis/OrganizationApi.js";

export interface OrganizationApiGetOwnersRequest {
    /**
     * Whether to return virtual reported owners
     * @type string
     * @memberof OrganizationApigetOwners
     */
    all?: string
}

export interface OrganizationApiGetOwnersDotRequest {
    /**
     * Whether to return virtual reported owners
     * @type string
     * @memberof OrganizationApigetOwnersDot
     */
    all?: string
}

export interface OrganizationApiGetSubordinateOwnersRequest {
    /**
     * The account being queried, must be a subordinate account of the current account
     * @type string
     * @memberof OrganizationApigetSubordinateOwners
     */
    username: string
    /**
     * Whether to return virtual reported owners
     * @type string
     * @memberof OrganizationApigetSubordinateOwners
     */
    all?: string
}

export interface OrganizationApiGetSubordinateSubordinatesRequest {
    /**
     * The account being queried, must be a subordinate account of the current account
     * @type string
     * @memberof OrganizationApigetSubordinateSubordinates
     */
    username: string
    /**
     * Whether to return virtual managed subordinates
     * @type string
     * @memberof OrganizationApigetSubordinateSubordinates
     */
    all?: string
}

export interface OrganizationApiGetSubordinatesRequest {
    /**
     * Whether to return virtual managed subordinates
     * @type string
     * @memberof OrganizationApigetSubordinates
     */
    all?: string
}

export interface OrganizationApiGetSubordinatesDotRequest {
    /**
     * Whether to return virtual managed subordinates
     * @type string
     * @memberof OrganizationApigetSubordinatesDot
     */
    all?: string
}

export interface OrganizationApiListSubordinateAuthoritiesRequest {
    /**
     * Username
     * @type string
     * @memberof OrganizationApilistSubordinateAuthorities
     */
    username: string
}

export interface OrganizationApiRemoveSubordinateSubordinatesTreeRequest {
    /**
     * The account being operated, must be a subordinate account of the current account
     * @type string
     * @memberof OrganizationApiremoveSubordinateSubordinatesTree
     */
    username: string
}

export interface OrganizationApiUpdateSubordinateAuthoritiesRequest {
    /**
     * Username
     * @type string
     * @memberof OrganizationApiupdateSubordinateAuthorities
     */
    username: string
    /**
     * Permission list
     * @type Set&lt;string&gt;
     * @memberof OrganizationApiupdateSubordinateAuthorities
     */
    requestBody: Set<string>
}

export interface OrganizationApiUpdateSubordinateOwnersRequest {
    /**
     * The account being operated, must be a subordinate account of the current account
     * @type string
     * @memberof OrganizationApiupdateSubordinateOwners
     */
    username: string
    /**
     * The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
     * @type Array&lt;string&gt;
     * @memberof OrganizationApiupdateSubordinateOwners
     */
    requestBody: Array<string>
}

export interface OrganizationApiUpdateSubordinateSubordinatesRequest {
    /**
     * The account being operated, must be a subordinate account of the current account
     * @type string
     * @memberof OrganizationApiupdateSubordinateSubordinates
     */
    username: string
    /**
     * The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
     * @type Array&lt;string&gt;
     * @memberof OrganizationApiupdateSubordinateSubordinates
     */
    requestBody: Array<string>
}

export class ObjectOrganizationApi {
    private api: ObservableOrganizationApi

    public constructor(configuration: Configuration, requestFactory?: OrganizationApiRequestFactory, responseProcessor?: OrganizationApiResponseProcessor) {
        this.api = new ObservableOrganizationApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     * Get My Superior Relationship
     * @param param the request object
     */
    public getOwnersWithHttpInfo(param: OrganizationApiGetOwnersRequest = {}, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.getOwnersWithHttpInfo(param.all,  options).toPromise();
    }

    /**
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     * Get My Superior Relationship
     * @param param the request object
     */
    public getOwners(param: OrganizationApiGetOwnersRequest = {}, options?: Configuration): Promise<Array<string>> {
        return this.api.getOwners(param.all,  options).toPromise();
    }

    /**
     * Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param param the request object
     */
    public getOwnersDotWithHttpInfo(param: OrganizationApiGetOwnersDotRequest = {}, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.getOwnersDotWithHttpInfo(param.all,  options).toPromise();
    }

    /**
     * Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param param the request object
     */
    public getOwnersDot(param: OrganizationApiGetOwnersDotRequest = {}, options?: Configuration): Promise<string> {
        return this.api.getOwnersDot(param.all,  options).toPromise();
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param param the request object
     */
    public getSubordinateOwnersWithHttpInfo(param: OrganizationApiGetSubordinateOwnersRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.getSubordinateOwnersWithHttpInfo(param.username, param.all,  options).toPromise();
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param param the request object
     */
    public getSubordinateOwners(param: OrganizationApiGetSubordinateOwnersRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.getSubordinateOwners(param.username, param.all,  options).toPromise();
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param param the request object
     */
    public getSubordinateSubordinatesWithHttpInfo(param: OrganizationApiGetSubordinateSubordinatesRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.getSubordinateSubordinatesWithHttpInfo(param.username, param.all,  options).toPromise();
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param param the request object
     */
    public getSubordinateSubordinates(param: OrganizationApiGetSubordinateSubordinatesRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.getSubordinateSubordinates(param.username, param.all,  options).toPromise();
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param param the request object
     */
    public getSubordinatesWithHttpInfo(param: OrganizationApiGetSubordinatesRequest = {}, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.getSubordinatesWithHttpInfo(param.all,  options).toPromise();
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param param the request object
     */
    public getSubordinates(param: OrganizationApiGetSubordinatesRequest = {}, options?: Configuration): Promise<Array<string>> {
        return this.api.getSubordinates(param.all,  options).toPromise();
    }

    /**
     * Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param param the request object
     */
    public getSubordinatesDotWithHttpInfo(param: OrganizationApiGetSubordinatesDotRequest = {}, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.getSubordinatesDotWithHttpInfo(param.all,  options).toPromise();
    }

    /**
     * Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param param the request object
     */
    public getSubordinatesDot(param: OrganizationApiGetSubordinatesDotRequest = {}, options?: Configuration): Promise<string> {
        return this.api.getSubordinatesDot(param.all,  options).toPromise();
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param param the request object
     */
    public listSubordinateAuthoritiesWithHttpInfo(param: OrganizationApiListSubordinateAuthoritiesRequest, options?: Configuration): Promise<HttpInfo<Set<string>>> {
        return this.api.listSubordinateAuthoritiesWithHttpInfo(param.username,  options).toPromise();
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param param the request object
     */
    public listSubordinateAuthorities(param: OrganizationApiListSubordinateAuthoritiesRequest, options?: Configuration): Promise<Set<string>> {
        return this.api.listSubordinateAuthorities(param.username,  options).toPromise();
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param param the request object
     */
    public removeSubordinateSubordinatesTreeWithHttpInfo(param: OrganizationApiRemoveSubordinateSubordinatesTreeRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.removeSubordinateSubordinatesTreeWithHttpInfo(param.username,  options).toPromise();
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param param the request object
     */
    public removeSubordinateSubordinatesTree(param: OrganizationApiRemoveSubordinateSubordinatesTreeRequest, options?: Configuration): Promise<boolean> {
        return this.api.removeSubordinateSubordinatesTree(param.username,  options).toPromise();
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param param the request object
     */
    public updateSubordinateAuthoritiesWithHttpInfo(param: OrganizationApiUpdateSubordinateAuthoritiesRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateSubordinateAuthoritiesWithHttpInfo(param.username, param.requestBody,  options).toPromise();
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param param the request object
     */
    public updateSubordinateAuthorities(param: OrganizationApiUpdateSubordinateAuthoritiesRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateSubordinateAuthorities(param.username, param.requestBody,  options).toPromise();
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param param the request object
     */
    public updateSubordinateOwnersWithHttpInfo(param: OrganizationApiUpdateSubordinateOwnersRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateSubordinateOwnersWithHttpInfo(param.username, param.requestBody,  options).toPromise();
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param param the request object
     */
    public updateSubordinateOwners(param: OrganizationApiUpdateSubordinateOwnersRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateSubordinateOwners(param.username, param.requestBody,  options).toPromise();
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param param the request object
     */
    public updateSubordinateSubordinatesWithHttpInfo(param: OrganizationApiUpdateSubordinateSubordinatesRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updateSubordinateSubordinatesWithHttpInfo(param.username, param.requestBody,  options).toPromise();
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param param the request object
     */
    public updateSubordinateSubordinates(param: OrganizationApiUpdateSubordinateSubordinatesRequest, options?: Configuration): Promise<boolean> {
        return this.api.updateSubordinateSubordinates(param.username, param.requestBody,  options).toPromise();
    }

}

import { ObservablePluginApi } from "./ObservableAPI.js";
import { PluginApiRequestFactory, PluginApiResponseProcessor} from "../apis/PluginApi.js";

export interface PluginApiBatchSearchPluginDetailsRequest {
    /**
     * Query conditions
     * @type Array&lt;PluginQueryDTO&gt;
     * @memberof PluginApibatchSearchPluginDetails
     */
    pluginQueryDTO: Array<PluginQueryDTO>
}

export interface PluginApiBatchSearchPluginSummaryRequest {
    /**
     * Query conditions
     * @type Array&lt;PluginQueryDTO&gt;
     * @memberof PluginApibatchSearchPluginSummary
     */
    pluginQueryDTO: Array<PluginQueryDTO>
}

export interface PluginApiCountPluginsRequest {
    /**
     * Query conditions
     * @type PluginQueryDTO
     * @memberof PluginApicountPlugins
     */
    pluginQueryDTO: PluginQueryDTO
}

export interface PluginApiCreatePluginRequest {
    /**
     * Information of the plugin to be created
     * @type PluginCreateDTO
     * @memberof PluginApicreatePlugin
     */
    pluginCreateDTO: PluginCreateDTO
}

export interface PluginApiCreatePluginsRequest {
    /**
     * List of plugin information to be created
     * @type Array&lt;PluginCreateDTO&gt;
     * @memberof PluginApicreatePlugins
     */
    pluginCreateDTO: Array<PluginCreateDTO>
}

export interface PluginApiDeletePluginRequest {
    /**
     * The pluginId to be deleted
     * @type string
     * @memberof PluginApideletePlugin
     */
    pluginId: string
}

export interface PluginApiDeletePluginsRequest {
    /**
     * List of pluginIds to be deleted
     * @type Array&lt;string&gt;
     * @memberof PluginApideletePlugins
     */
    requestBody: Array<string>
}

export interface PluginApiGetPluginDetailsRequest {
    /**
     * PluginId to be obtained
     * @type string
     * @memberof PluginApigetPluginDetails
     */
    pluginId: string
}

export interface PluginApiGetPluginSummaryRequest {
    /**
     * PluginId to be obtained
     * @type string
     * @memberof PluginApigetPluginSummary
     */
    pluginId: string
}

export interface PluginApiRefreshPluginInfoRequest {
    /**
     * The pluginId to be fetched
     * @type string
     * @memberof PluginApirefreshPluginInfo
     */
    pluginId: string
}

export interface PluginApiSearchPluginDetailsRequest {
    /**
     * Query conditions
     * @type PluginQueryDTO
     * @memberof PluginApisearchPluginDetails
     */
    pluginQueryDTO: PluginQueryDTO
}

export interface PluginApiSearchPluginSummaryRequest {
    /**
     * Query conditions
     * @type PluginQueryDTO
     * @memberof PluginApisearchPluginSummary
     */
    pluginQueryDTO: PluginQueryDTO
}

export interface PluginApiUpdatePluginRequest {
    /**
     * The pluginId to be updated
     * @type string
     * @memberof PluginApiupdatePlugin
     */
    pluginId: string
    /**
     * The plugin information to be updated
     * @type PluginUpdateDTO
     * @memberof PluginApiupdatePlugin
     */
    pluginUpdateDTO: PluginUpdateDTO
}

export class ObjectPluginApi {
    private api: ObservablePluginApi

    public constructor(configuration: Configuration, requestFactory?: PluginApiRequestFactory, responseProcessor?: PluginApiResponseProcessor) {
        this.api = new ObservablePluginApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Batch call shortcut for /api/v1/plugin/details/search.
     * Batch Search Plugin Details
     * @param param the request object
     */
    public batchSearchPluginDetailsWithHttpInfo(param: PluginApiBatchSearchPluginDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<Array<PluginDetailsDTO>>>> {
        return this.api.batchSearchPluginDetailsWithHttpInfo(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/plugin/details/search.
     * Batch Search Plugin Details
     * @param param the request object
     */
    public batchSearchPluginDetails(param: PluginApiBatchSearchPluginDetailsRequest, options?: Configuration): Promise<Array<Array<PluginDetailsDTO>>> {
        return this.api.batchSearchPluginDetails(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/plugin/search.
     * Batch Search Plugin Summaries
     * @param param the request object
     */
    public batchSearchPluginSummaryWithHttpInfo(param: PluginApiBatchSearchPluginSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<Array<PluginSummaryDTO>>>> {
        return this.api.batchSearchPluginSummaryWithHttpInfo(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/plugin/search.
     * Batch Search Plugin Summaries
     * @param param the request object
     */
    public batchSearchPluginSummary(param: PluginApiBatchSearchPluginSummaryRequest, options?: Configuration): Promise<Array<Array<PluginSummaryDTO>>> {
        return this.api.batchSearchPluginSummary(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param param the request object
     */
    public countPluginsWithHttpInfo(param: PluginApiCountPluginsRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.countPluginsWithHttpInfo(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param param the request object
     */
    public countPlugins(param: PluginApiCountPluginsRequest, options?: Configuration): Promise<number> {
        return this.api.countPlugins(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param param the request object
     */
    public createPluginWithHttpInfo(param: PluginApiCreatePluginRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createPluginWithHttpInfo(param.pluginCreateDTO,  options).toPromise();
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param param the request object
     */
    public createPlugin(param: PluginApiCreatePluginRequest, options?: Configuration): Promise<string> {
        return this.api.createPlugin(param.pluginCreateDTO,  options).toPromise();
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param param the request object
     */
    public createPluginsWithHttpInfo(param: PluginApiCreatePluginsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.createPluginsWithHttpInfo(param.pluginCreateDTO,  options).toPromise();
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param param the request object
     */
    public createPlugins(param: PluginApiCreatePluginsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.createPlugins(param.pluginCreateDTO,  options).toPromise();
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param param the request object
     */
    public deletePluginWithHttpInfo(param: PluginApiDeletePluginRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deletePluginWithHttpInfo(param.pluginId,  options).toPromise();
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param param the request object
     */
    public deletePlugin(param: PluginApiDeletePluginRequest, options?: Configuration): Promise<boolean> {
        return this.api.deletePlugin(param.pluginId,  options).toPromise();
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param param the request object
     */
    public deletePluginsWithHttpInfo(param: PluginApiDeletePluginsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.deletePluginsWithHttpInfo(param.requestBody,  options).toPromise();
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param param the request object
     */
    public deletePlugins(param: PluginApiDeletePluginsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.deletePlugins(param.requestBody,  options).toPromise();
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param param the request object
     */
    public getPluginDetailsWithHttpInfo(param: PluginApiGetPluginDetailsRequest, options?: Configuration): Promise<HttpInfo<PluginDetailsDTO>> {
        return this.api.getPluginDetailsWithHttpInfo(param.pluginId,  options).toPromise();
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param param the request object
     */
    public getPluginDetails(param: PluginApiGetPluginDetailsRequest, options?: Configuration): Promise<PluginDetailsDTO> {
        return this.api.getPluginDetails(param.pluginId,  options).toPromise();
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param param the request object
     */
    public getPluginSummaryWithHttpInfo(param: PluginApiGetPluginSummaryRequest, options?: Configuration): Promise<HttpInfo<PluginSummaryDTO>> {
        return this.api.getPluginSummaryWithHttpInfo(param.pluginId,  options).toPromise();
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param param the request object
     */
    public getPluginSummary(param: PluginApiGetPluginSummaryRequest, options?: Configuration): Promise<PluginSummaryDTO> {
        return this.api.getPluginSummary(param.pluginId,  options).toPromise();
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param param the request object
     */
    public refreshPluginInfoWithHttpInfo(param: PluginApiRefreshPluginInfoRequest, options?: Configuration): Promise<HttpInfo<void>> {
        return this.api.refreshPluginInfoWithHttpInfo(param.pluginId,  options).toPromise();
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param param the request object
     */
    public refreshPluginInfo(param: PluginApiRefreshPluginInfoRequest, options?: Configuration): Promise<void> {
        return this.api.refreshPluginInfo(param.pluginId,  options).toPromise();
    }

    /**
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param param the request object
     */
    public searchPluginDetailsWithHttpInfo(param: PluginApiSearchPluginDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<PluginDetailsDTO>>> {
        return this.api.searchPluginDetailsWithHttpInfo(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param param the request object
     */
    public searchPluginDetails(param: PluginApiSearchPluginDetailsRequest, options?: Configuration): Promise<Array<PluginDetailsDTO>> {
        return this.api.searchPluginDetails(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param param the request object
     */
    public searchPluginSummaryWithHttpInfo(param: PluginApiSearchPluginSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<PluginSummaryDTO>>> {
        return this.api.searchPluginSummaryWithHttpInfo(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param param the request object
     */
    public searchPluginSummary(param: PluginApiSearchPluginSummaryRequest, options?: Configuration): Promise<Array<PluginSummaryDTO>> {
        return this.api.searchPluginSummary(param.pluginQueryDTO,  options).toPromise();
    }

    /**
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param param the request object
     */
    public updatePluginWithHttpInfo(param: PluginApiUpdatePluginRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updatePluginWithHttpInfo(param.pluginId, param.pluginUpdateDTO,  options).toPromise();
    }

    /**
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param param the request object
     */
    public updatePlugin(param: PluginApiUpdatePluginRequest, options?: Configuration): Promise<boolean> {
        return this.api.updatePlugin(param.pluginId, param.pluginUpdateDTO,  options).toPromise();
    }

}

import { ObservablePromptApi } from "./ObservableAPI.js";
import { PromptApiRequestFactory, PromptApiResponseProcessor} from "../apis/PromptApi.js";

export interface PromptApiApplyStringPromptRefRequest {
    /**
     * Prompt record
     * @type PromptRefDTO
     * @memberof PromptApiapplyStringPromptRef
     */
    promptRefDTO: PromptRefDTO
}

export interface PromptApiApplyStringPromptTemplateRequest {
    /**
     * String type prompt template
     * @type PromptTemplateDTO
     * @memberof PromptApiapplyStringPromptTemplate
     */
    promptTemplateDTO: PromptTemplateDTO
}

export interface PromptApiBatchSearchPromptDetailsRequest {
    /**
     * Query conditions
     * @type Array&lt;PromptQueryDTO&gt;
     * @memberof PromptApibatchSearchPromptDetails
     */
    promptQueryDTO: Array<PromptQueryDTO>
}

export interface PromptApiBatchSearchPromptSummaryRequest {
    /**
     * Query conditions
     * @type Array&lt;PromptQueryDTO&gt;
     * @memberof PromptApibatchSearchPromptSummary
     */
    promptQueryDTO: Array<PromptQueryDTO>
}

export interface PromptApiClonePromptRequest {
    /**
     * The referenced promptId
     * @type string
     * @memberof PromptApiclonePrompt
     */
    promptId: string
}

export interface PromptApiClonePromptsRequest {
    /**
     * List of prompt information to be created
     * @type Array&lt;string&gt;
     * @memberof PromptApiclonePrompts
     */
    requestBody: Array<string>
}

export interface PromptApiCountPromptsRequest {
    /**
     * Query conditions
     * @type PromptQueryDTO
     * @memberof PromptApicountPrompts
     */
    promptQueryDTO: PromptQueryDTO
}

export interface PromptApiCreatePromptRequest {
    /**
     * Information of the prompt to be created
     * @type PromptCreateDTO
     * @memberof PromptApicreatePrompt
     */
    promptCreateDTO: PromptCreateDTO
}

export interface PromptApiCreatePromptsRequest {
    /**
     * List of prompt information to be created
     * @type Array&lt;PromptCreateDTO&gt;
     * @memberof PromptApicreatePrompts
     */
    promptCreateDTO: Array<PromptCreateDTO>
}

export interface PromptApiDeletePromptRequest {
    /**
     * The promptId to be deleted
     * @type string
     * @memberof PromptApideletePrompt
     */
    promptId: string
}

export interface PromptApiDeletePromptsRequest {
    /**
     * List of promptIds to be deleted
     * @type Array&lt;string&gt;
     * @memberof PromptApideletePrompts
     */
    requestBody: Array<string>
}

export interface PromptApiExistsNameRequest {
    /**
     * Name
     * @type string
     * @memberof PromptApiexistsName
     */
    name: string
}

export interface PromptApiGetPromptDetailsRequest {
    /**
     * PromptId to be obtained
     * @type string
     * @memberof PromptApigetPromptDetails
     */
    promptId: string
}

export interface PromptApiGetPromptSummaryRequest {
    /**
     * PromptId to be obtained
     * @type string
     * @memberof PromptApigetPromptSummary
     */
    promptId: string
}

export interface PromptApiListPromptVersionsByNameRequest {
    /**
     * Prompt name
     * @type string
     * @memberof PromptApilistPromptVersionsByName
     */
    name: string
}

export interface PromptApiPublishPromptRequest {
    /**
     * The promptId to be published
     * @type string
     * @memberof PromptApipublishPrompt
     */
    promptId: string
    /**
     * Visibility: public | private | ...
     * @type string
     * @memberof PromptApipublishPrompt
     */
    visibility: string
}

export interface PromptApiSearchPromptDetailsRequest {
    /**
     * Query conditions
     * @type PromptQueryDTO
     * @memberof PromptApisearchPromptDetails
     */
    promptQueryDTO: PromptQueryDTO
}

export interface PromptApiSearchPromptSummaryRequest {
    /**
     * Query conditions
     * @type PromptQueryDTO
     * @memberof PromptApisearchPromptSummary
     */
    promptQueryDTO: PromptQueryDTO
}

export interface PromptApiSendPromptRequest {
    /**
     * Call parameters
     * @type PromptAiParamDTO
     * @memberof PromptApisendPrompt
     */
    promptAiParamDTO: PromptAiParamDTO
}

export interface PromptApiStreamSendPromptRequest {
    /**
     * Call parameters
     * @type PromptAiParamDTO
     * @memberof PromptApistreamSendPrompt
     */
    promptAiParamDTO: PromptAiParamDTO
}

export interface PromptApiUpdatePromptRequest {
    /**
     * The promptId to be updated
     * @type string
     * @memberof PromptApiupdatePrompt
     */
    promptId: string
    /**
     * The prompt information to be updated
     * @type PromptUpdateDTO
     * @memberof PromptApiupdatePrompt
     */
    promptUpdateDTO: PromptUpdateDTO
}

export class ObjectPromptApi {
    private api: ObservablePromptApi

    public constructor(configuration: Configuration, requestFactory?: PromptApiRequestFactory, responseProcessor?: PromptApiResponseProcessor) {
        this.api = new ObservablePromptApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Apply parameters to prompt record.
     * Apply Parameters to Prompt Record
     * @param param the request object
     */
    public applyStringPromptRefWithHttpInfo(param: PromptApiApplyStringPromptRefRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.applyStringPromptRefWithHttpInfo(param.promptRefDTO,  options).toPromise();
    }

    /**
     * Apply parameters to prompt record.
     * Apply Parameters to Prompt Record
     * @param param the request object
     */
    public applyStringPromptRef(param: PromptApiApplyStringPromptRefRequest, options?: Configuration): Promise<string> {
        return this.api.applyStringPromptRef(param.promptRefDTO,  options).toPromise();
    }

    /**
     * Apply parameters to string type prompt template.
     * Apply Parameters to String Prompt Template
     * @param param the request object
     */
    public applyStringPromptTemplateWithHttpInfo(param: PromptApiApplyStringPromptTemplateRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.applyStringPromptTemplateWithHttpInfo(param.promptTemplateDTO,  options).toPromise();
    }

    /**
     * Apply parameters to string type prompt template.
     * Apply Parameters to String Prompt Template
     * @param param the request object
     */
    public applyStringPromptTemplate(param: PromptApiApplyStringPromptTemplateRequest, options?: Configuration): Promise<string> {
        return this.api.applyStringPromptTemplate(param.promptTemplateDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/details/search.
     * Batch Search Prompt Details
     * @param param the request object
     */
    public batchSearchPromptDetailsWithHttpInfo(param: PromptApiBatchSearchPromptDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<Array<PromptDetailsDTO>>>> {
        return this.api.batchSearchPromptDetailsWithHttpInfo(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/details/search.
     * Batch Search Prompt Details
     * @param param the request object
     */
    public batchSearchPromptDetails(param: PromptApiBatchSearchPromptDetailsRequest, options?: Configuration): Promise<Array<Array<PromptDetailsDTO>>> {
        return this.api.batchSearchPromptDetails(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/search.
     * Batch Search Prompt Summaries
     * @param param the request object
     */
    public batchSearchPromptSummaryWithHttpInfo(param: PromptApiBatchSearchPromptSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<Array<PromptSummaryDTO>>>> {
        return this.api.batchSearchPromptSummaryWithHttpInfo(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/search.
     * Batch Search Prompt Summaries
     * @param param the request object
     */
    public batchSearchPromptSummary(param: PromptApiBatchSearchPromptSummaryRequest, options?: Configuration): Promise<Array<Array<PromptSummaryDTO>>> {
        return this.api.batchSearchPromptSummary(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param param the request object
     */
    public clonePromptWithHttpInfo(param: PromptApiClonePromptRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.clonePromptWithHttpInfo(param.promptId,  options).toPromise();
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param param the request object
     */
    public clonePrompt(param: PromptApiClonePromptRequest, options?: Configuration): Promise<string> {
        return this.api.clonePrompt(param.promptId,  options).toPromise();
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param param the request object
     */
    public clonePromptsWithHttpInfo(param: PromptApiClonePromptsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.clonePromptsWithHttpInfo(param.requestBody,  options).toPromise();
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param param the request object
     */
    public clonePrompts(param: PromptApiClonePromptsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.clonePrompts(param.requestBody,  options).toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param param the request object
     */
    public countPromptsWithHttpInfo(param: PromptApiCountPromptsRequest, options?: Configuration): Promise<HttpInfo<number>> {
        return this.api.countPromptsWithHttpInfo(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param param the request object
     */
    public countPrompts(param: PromptApiCountPromptsRequest, options?: Configuration): Promise<number> {
        return this.api.countPrompts(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param param the request object
     */
    public createPromptWithHttpInfo(param: PromptApiCreatePromptRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createPromptWithHttpInfo(param.promptCreateDTO,  options).toPromise();
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param param the request object
     */
    public createPrompt(param: PromptApiCreatePromptRequest, options?: Configuration): Promise<string> {
        return this.api.createPrompt(param.promptCreateDTO,  options).toPromise();
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param param the request object
     */
    public createPromptsWithHttpInfo(param: PromptApiCreatePromptsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.createPromptsWithHttpInfo(param.promptCreateDTO,  options).toPromise();
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param param the request object
     */
    public createPrompts(param: PromptApiCreatePromptsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.createPrompts(param.promptCreateDTO,  options).toPromise();
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param param the request object
     */
    public deletePromptWithHttpInfo(param: PromptApiDeletePromptRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deletePromptWithHttpInfo(param.promptId,  options).toPromise();
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param param the request object
     */
    public deletePrompt(param: PromptApiDeletePromptRequest, options?: Configuration): Promise<boolean> {
        return this.api.deletePrompt(param.promptId,  options).toPromise();
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param param the request object
     */
    public deletePromptsWithHttpInfo(param: PromptApiDeletePromptsRequest, options?: Configuration): Promise<HttpInfo<Array<string>>> {
        return this.api.deletePromptsWithHttpInfo(param.requestBody,  options).toPromise();
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param param the request object
     */
    public deletePrompts(param: PromptApiDeletePromptsRequest, options?: Configuration): Promise<Array<string>> {
        return this.api.deletePrompts(param.requestBody,  options).toPromise();
    }

    /**
     * Check if the name already exists.
     * Check If Name Exists
     * @param param the request object
     */
    public existsNameWithHttpInfo(param: PromptApiExistsNameRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.existsNameWithHttpInfo(param.name,  options).toPromise();
    }

    /**
     * Check if the name already exists.
     * Check If Name Exists
     * @param param the request object
     */
    public existsName(param: PromptApiExistsNameRequest, options?: Configuration): Promise<boolean> {
        return this.api.existsName(param.name,  options).toPromise();
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param param the request object
     */
    public getPromptDetailsWithHttpInfo(param: PromptApiGetPromptDetailsRequest, options?: Configuration): Promise<HttpInfo<PromptDetailsDTO>> {
        return this.api.getPromptDetailsWithHttpInfo(param.promptId,  options).toPromise();
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param param the request object
     */
    public getPromptDetails(param: PromptApiGetPromptDetailsRequest, options?: Configuration): Promise<PromptDetailsDTO> {
        return this.api.getPromptDetails(param.promptId,  options).toPromise();
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param param the request object
     */
    public getPromptSummaryWithHttpInfo(param: PromptApiGetPromptSummaryRequest, options?: Configuration): Promise<HttpInfo<PromptSummaryDTO>> {
        return this.api.getPromptSummaryWithHttpInfo(param.promptId,  options).toPromise();
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param param the request object
     */
    public getPromptSummary(param: PromptApiGetPromptSummaryRequest, options?: Configuration): Promise<PromptSummaryDTO> {
        return this.api.getPromptSummary(param.promptId,  options).toPromise();
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param param the request object
     */
    public listPromptVersionsByNameWithHttpInfo(param: PromptApiListPromptVersionsByNameRequest, options?: Configuration): Promise<HttpInfo<Array<PromptItemForNameDTO>>> {
        return this.api.listPromptVersionsByNameWithHttpInfo(param.name,  options).toPromise();
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param param the request object
     */
    public listPromptVersionsByName(param: PromptApiListPromptVersionsByNameRequest, options?: Configuration): Promise<Array<PromptItemForNameDTO>> {
        return this.api.listPromptVersionsByName(param.name,  options).toPromise();
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param param the request object
     */
    public publishPromptWithHttpInfo(param: PromptApiPublishPromptRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.publishPromptWithHttpInfo(param.promptId, param.visibility,  options).toPromise();
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param param the request object
     */
    public publishPrompt(param: PromptApiPublishPromptRequest, options?: Configuration): Promise<string> {
        return this.api.publishPrompt(param.promptId, param.visibility,  options).toPromise();
    }

    /**
     * Same as /api/v1/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param param the request object
     */
    public searchPromptDetailsWithHttpInfo(param: PromptApiSearchPromptDetailsRequest, options?: Configuration): Promise<HttpInfo<Array<PromptDetailsDTO>>> {
        return this.api.searchPromptDetailsWithHttpInfo(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Same as /api/v1/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param param the request object
     */
    public searchPromptDetails(param: PromptApiSearchPromptDetailsRequest, options?: Configuration): Promise<Array<PromptDetailsDTO>> {
        return this.api.searchPromptDetails(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param param the request object
     */
    public searchPromptSummaryWithHttpInfo(param: PromptApiSearchPromptSummaryRequest, options?: Configuration): Promise<HttpInfo<Array<PromptSummaryDTO>>> {
        return this.api.searchPromptSummaryWithHttpInfo(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param param the request object
     */
    public searchPromptSummary(param: PromptApiSearchPromptSummaryRequest, options?: Configuration): Promise<Array<PromptSummaryDTO>> {
        return this.api.searchPromptSummary(param.promptQueryDTO,  options).toPromise();
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param param the request object
     */
    public sendPromptWithHttpInfo(param: PromptApiSendPromptRequest, options?: Configuration): Promise<HttpInfo<LlmResultDTO>> {
        return this.api.sendPromptWithHttpInfo(param.promptAiParamDTO,  options).toPromise();
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param param the request object
     */
    public sendPrompt(param: PromptApiSendPromptRequest, options?: Configuration): Promise<LlmResultDTO> {
        return this.api.sendPrompt(param.promptAiParamDTO,  options).toPromise();
    }

    /**
     * Refer to /api/v1/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param param the request object
     */
    public streamSendPromptWithHttpInfo(param: PromptApiStreamSendPromptRequest, options?: Configuration): Promise<HttpInfo<SseEmitter>> {
        return this.api.streamSendPromptWithHttpInfo(param.promptAiParamDTO,  options).toPromise();
    }

    /**
     * Refer to /api/v1/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param param the request object
     */
    public streamSendPrompt(param: PromptApiStreamSendPromptRequest, options?: Configuration): Promise<SseEmitter> {
        return this.api.streamSendPrompt(param.promptAiParamDTO,  options).toPromise();
    }

    /**
     * Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param param the request object
     */
    public updatePromptWithHttpInfo(param: PromptApiUpdatePromptRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updatePromptWithHttpInfo(param.promptId, param.promptUpdateDTO,  options).toPromise();
    }

    /**
     * Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param param the request object
     */
    public updatePrompt(param: PromptApiUpdatePromptRequest, options?: Configuration): Promise<boolean> {
        return this.api.updatePrompt(param.promptId, param.promptUpdateDTO,  options).toPromise();
    }

}

import { ObservablePromptTaskApi } from "./ObservableAPI.js";
import { PromptTaskApiRequestFactory, PromptTaskApiResponseProcessor} from "../apis/PromptTaskApi.js";

export interface PromptTaskApiCreatePromptTaskRequest {
    /**
     * The prompt task to be added
     * @type PromptTaskDTO
     * @memberof PromptTaskApicreatePromptTask
     */
    promptTaskDTO: PromptTaskDTO
}

export interface PromptTaskApiDeletePromptTaskRequest {
    /**
     * The promptTaskId to be deleted
     * @type string
     * @memberof PromptTaskApideletePromptTask
     */
    promptTaskId: string
}

export interface PromptTaskApiGetPromptTaskRequest {
    /**
     * The promptTaskId to be queried
     * @type string
     * @memberof PromptTaskApigetPromptTask
     */
    promptTaskId: string
}

export interface PromptTaskApiUpdatePromptTaskRequest {
    /**
     * The promptTaskId to be updated
     * @type string
     * @memberof PromptTaskApiupdatePromptTask
     */
    promptTaskId: string
    /**
     * The prompt task info to be updated
     * @type PromptTaskDTO
     * @memberof PromptTaskApiupdatePromptTask
     */
    promptTaskDTO: PromptTaskDTO
}

export class ObjectPromptTaskApi {
    private api: ObservablePromptTaskApi

    public constructor(configuration: Configuration, requestFactory?: PromptTaskApiRequestFactory, responseProcessor?: PromptTaskApiResponseProcessor) {
        this.api = new ObservablePromptTaskApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Add a prompt task.
     * Add Prompt Task
     * @param param the request object
     */
    public createPromptTaskWithHttpInfo(param: PromptTaskApiCreatePromptTaskRequest, options?: Configuration): Promise<HttpInfo<string>> {
        return this.api.createPromptTaskWithHttpInfo(param.promptTaskDTO,  options).toPromise();
    }

    /**
     * Add a prompt task.
     * Add Prompt Task
     * @param param the request object
     */
    public createPromptTask(param: PromptTaskApiCreatePromptTaskRequest, options?: Configuration): Promise<string> {
        return this.api.createPromptTask(param.promptTaskDTO,  options).toPromise();
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param param the request object
     */
    public deletePromptTaskWithHttpInfo(param: PromptTaskApiDeletePromptTaskRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.deletePromptTaskWithHttpInfo(param.promptTaskId,  options).toPromise();
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param param the request object
     */
    public deletePromptTask(param: PromptTaskApiDeletePromptTaskRequest, options?: Configuration): Promise<boolean> {
        return this.api.deletePromptTask(param.promptTaskId,  options).toPromise();
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param param the request object
     */
    public getPromptTaskWithHttpInfo(param: PromptTaskApiGetPromptTaskRequest, options?: Configuration): Promise<HttpInfo<PromptTaskDetailsDTO>> {
        return this.api.getPromptTaskWithHttpInfo(param.promptTaskId,  options).toPromise();
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param param the request object
     */
    public getPromptTask(param: PromptTaskApiGetPromptTaskRequest, options?: Configuration): Promise<PromptTaskDetailsDTO> {
        return this.api.getPromptTask(param.promptTaskId,  options).toPromise();
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param param the request object
     */
    public updatePromptTaskWithHttpInfo(param: PromptTaskApiUpdatePromptTaskRequest, options?: Configuration): Promise<HttpInfo<boolean>> {
        return this.api.updatePromptTaskWithHttpInfo(param.promptTaskId, param.promptTaskDTO,  options).toPromise();
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param param the request object
     */
    public updatePromptTask(param: PromptTaskApiUpdatePromptTaskRequest, options?: Configuration): Promise<boolean> {
        return this.api.updatePromptTask(param.promptTaskId, param.promptTaskDTO,  options).toPromise();
    }

}
