import { ResponseContext, RequestContext, HttpFile, HttpInfo } from '../http/http.js';
import { Configuration} from '../configuration.js'

import { AgentCreateDTO } from '../models/AgentCreateDTO.js';
import { AgentDetailsDTO } from '../models/AgentDetailsDTO.js';
import { AgentItemForNameDTO } from '../models/AgentItemForNameDTO.js';
import { AgentQueryDTO } from '../models/AgentQueryDTO.js';
import { AgentQueryWhere } from '../models/AgentQueryWhere.js';
import { AgentSummaryDTO } from '../models/AgentSummaryDTO.js';
import { AgentSummaryStatsDTO } from '../models/AgentSummaryStatsDTO.js';
import { AgentUpdateDTO } from '../models/AgentUpdateDTO.js';
import { AiApiKeyCreateDTO } from '../models/AiApiKeyCreateDTO.js';
import { AiApiKeyInfoDTO } from '../models/AiApiKeyInfoDTO.js';
import { AiModelInfoDTO } from '../models/AiModelInfoDTO.js';
import { ApiTokenInfoDTO } from '../models/ApiTokenInfoDTO.js';
import { AppConfigInfoDTO } from '../models/AppConfigInfoDTO.js';
import { AppMetaDTO } from '../models/AppMetaDTO.js';
import { CharacterBackendDTO } from '../models/CharacterBackendDTO.js';
import { CharacterBackendDetailsDTO } from '../models/CharacterBackendDetailsDTO.js';
import { CharacterCreateDTO } from '../models/CharacterCreateDTO.js';
import { CharacterDetailsDTO } from '../models/CharacterDetailsDTO.js';
import { CharacterItemForNameDTO } from '../models/CharacterItemForNameDTO.js';
import { CharacterQueryDTO } from '../models/CharacterQueryDTO.js';
import { CharacterQueryWhere } from '../models/CharacterQueryWhere.js';
import { CharacterSummaryDTO } from '../models/CharacterSummaryDTO.js';
import { CharacterSummaryStatsDTO } from '../models/CharacterSummaryStatsDTO.js';
import { CharacterUpdateDTO } from '../models/CharacterUpdateDTO.js';
import { ChatContentDTO } from '../models/ChatContentDTO.js';
import { ChatContextDTO } from '../models/ChatContextDTO.js';
import { ChatCreateDTO } from '../models/ChatCreateDTO.js';
import { ChatMessageDTO } from '../models/ChatMessageDTO.js';
import { ChatMessageRecordDTO } from '../models/ChatMessageRecordDTO.js';
import { ChatPromptContentDTO } from '../models/ChatPromptContentDTO.js';
import { ChatSessionDTO } from '../models/ChatSessionDTO.js';
import { ChatToolCallDTO } from '../models/ChatToolCallDTO.js';
import { ChatUpdateDTO } from '../models/ChatUpdateDTO.js';
import { HotTagDTO } from '../models/HotTagDTO.js';
import { InteractiveStatsDTO } from '../models/InteractiveStatsDTO.js';
import { LlmResultDTO } from '../models/LlmResultDTO.js';
import { MemoryUsageDTO } from '../models/MemoryUsageDTO.js';
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
import { RagTaskDTO } from '../models/RagTaskDTO.js';
import { RagTaskDetailsDTO } from '../models/RagTaskDetailsDTO.js';
import { SseEmitter } from '../models/SseEmitter.js';
import { TokenUsageDTO } from '../models/TokenUsageDTO.js';
import { UserBasicInfoDTO } from '../models/UserBasicInfoDTO.js';
import { UserDetailsDTO } from '../models/UserDetailsDTO.js';
import { UserFullDetailsDTO } from '../models/UserFullDetailsDTO.js';
import { ObservableAIServiceApi } from './ObservableAPI.js';

import { AIServiceApiRequestFactory, AIServiceApiResponseProcessor} from "../apis/AIServiceApi.js";
export class PromiseAIServiceApi {
    private api: ObservableAIServiceApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AIServiceApiRequestFactory,
        responseProcessor?: AIServiceApiResponseProcessor
    ) {
        this.api = new ObservableAIServiceApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Add a credential for the model service.
     * Add Model Provider Credential
     * @param aiApiKeyCreateDTO Model call credential information
     */
    public addAiApiKeyWithHttpInfo(aiApiKeyCreateDTO: AiApiKeyCreateDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.addAiApiKeyWithHttpInfo(aiApiKeyCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Add a credential for the model service.
     * Add Model Provider Credential
     * @param aiApiKeyCreateDTO Model call credential information
     */
    public addAiApiKey(aiApiKeyCreateDTO: AiApiKeyCreateDTO, _options?: Configuration): Promise<number> {
        const result = this.api.addAiApiKey(aiApiKeyCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param id Credential identifier
     */
    public deleteAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteAiApiKeyWithHttpInfo(id, _options);
        return result.toPromise();
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param id Credential identifier
     */
    public deleteAiApiKey(id: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteAiApiKey(id, _options);
        return result.toPromise();
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param id Credential identifier
     */
    public disableAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.disableAiApiKeyWithHttpInfo(id, _options);
        return result.toPromise();
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param id Credential identifier
     */
    public disableAiApiKey(id: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.disableAiApiKey(id, _options);
        return result.toPromise();
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param id Credential identifier
     */
    public enableAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.enableAiApiKeyWithHttpInfo(id, _options);
        return result.toPromise();
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param id Credential identifier
     */
    public enableAiApiKey(id: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.enableAiApiKey(id, _options);
        return result.toPromise();
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param id Credential identifier
     */
    public getAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Promise<HttpInfo<AiApiKeyInfoDTO>> {
        const result = this.api.getAiApiKeyWithHttpInfo(id, _options);
        return result.toPromise();
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param id Credential identifier
     */
    public getAiApiKey(id: number, _options?: Configuration): Promise<AiApiKeyInfoDTO> {
        const result = this.api.getAiApiKey(id, _options);
        return result.toPromise();
    }

    /**
     * Return specific model information.
     * Get Model Information
     * @param modelId Model identifier
     */
    public getAiModelInfoWithHttpInfo(modelId: string, _options?: Configuration): Promise<HttpInfo<AiModelInfoDTO>> {
        const result = this.api.getAiModelInfoWithHttpInfo(modelId, _options);
        return result.toPromise();
    }

    /**
     * Return specific model information.
     * Get Model Information
     * @param modelId Model identifier
     */
    public getAiModelInfo(modelId: string, _options?: Configuration): Promise<AiModelInfoDTO> {
        const result = this.api.getAiModelInfo(modelId, _options);
        return result.toPromise();
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param provider Model provider
     */
    public listAiApiKeysWithHttpInfo(provider: string, _options?: Configuration): Promise<HttpInfo<Array<AiApiKeyInfoDTO>>> {
        const result = this.api.listAiApiKeysWithHttpInfo(provider, _options);
        return result.toPromise();
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param provider Model provider
     */
    public listAiApiKeys(provider: string, _options?: Configuration): Promise<Array<AiApiKeyInfoDTO>> {
        const result = this.api.listAiApiKeys(provider, _options);
        return result.toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     */
    public listAiModelInfoWithHttpInfo(pageSize: number, _options?: Configuration): Promise<HttpInfo<Array<AiModelInfoDTO>>> {
        const result = this.api.listAiModelInfoWithHttpInfo(pageSize, _options);
        return result.toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     */
    public listAiModelInfo(pageSize: number, _options?: Configuration): Promise<Array<AiModelInfoDTO>> {
        const result = this.api.listAiModelInfo(pageSize, _options);
        return result.toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     */
    public listAiModelInfo1WithHttpInfo(_options?: Configuration): Promise<HttpInfo<Array<AiModelInfoDTO>>> {
        const result = this.api.listAiModelInfo1WithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     */
    public listAiModelInfo1(_options?: Configuration): Promise<Array<AiModelInfoDTO>> {
        const result = this.api.listAiModelInfo1(_options);
        return result.toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listAiModelInfo2WithHttpInfo(pageSize: number, pageNum: number, _options?: Configuration): Promise<HttpInfo<Array<AiModelInfoDTO>>> {
        const result = this.api.listAiModelInfo2WithHttpInfo(pageSize, pageNum, _options);
        return result.toPromise();
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listAiModelInfo2(pageSize: number, pageNum: number, _options?: Configuration): Promise<Array<AiModelInfoDTO>> {
        const result = this.api.listAiModelInfo2(pageSize, pageNum, _options);
        return result.toPromise();
    }


}



import { ObservableAccountApi } from './ObservableAPI.js';

import { AccountApiRequestFactory, AccountApiResponseProcessor} from "../apis/AccountApi.js";
export class PromiseAccountApi {
    private api: ObservableAccountApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AccountApiRequestFactory,
        responseProcessor?: AccountApiResponseProcessor
    ) {
        this.api = new ObservableAccountApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     */
    public createTokenWithHttpInfo(_options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.createTokenWithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     */
    public createToken(_options?: Configuration): Promise<string> {
        const result = this.api.createToken(_options);
        return result.toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param duration Token validity duration (seconds)
     */
    public createToken1WithHttpInfo(duration: number, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.createToken1WithHttpInfo(duration, _options);
        return result.toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param duration Token validity duration (seconds)
     */
    public createToken1(duration: number, _options?: Configuration): Promise<string> {
        const result = this.api.createToken1(duration, _options);
        return result.toPromise();
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param token Token content
     */
    public deleteTokenWithHttpInfo(token: string, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.deleteTokenWithHttpInfo(token, _options);
        return result.toPromise();
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param token Token content
     */
    public deleteToken(token: string, _options?: Configuration): Promise<string> {
        const result = this.api.deleteToken(token, _options);
        return result.toPromise();
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param id Token id
     */
    public deleteTokenByIdWithHttpInfo(id: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteTokenByIdWithHttpInfo(id, _options);
        return result.toPromise();
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param id Token id
     */
    public deleteTokenById(id: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteTokenById(id, _options);
        return result.toPromise();
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param token Token content
     */
    public disableTokenWithHttpInfo(token: string, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.disableTokenWithHttpInfo(token, _options);
        return result.toPromise();
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param token Token content
     */
    public disableToken(token: string, _options?: Configuration): Promise<string> {
        const result = this.api.disableToken(token, _options);
        return result.toPromise();
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param id Token id
     */
    public disableTokenByIdWithHttpInfo(id: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.disableTokenByIdWithHttpInfo(id, _options);
        return result.toPromise();
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param id Token id
     */
    public disableTokenById(id: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.disableTokenById(id, _options);
        return result.toPromise();
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param id Token id
     */
    public getTokenByIdWithHttpInfo(id: number, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.getTokenByIdWithHttpInfo(id, _options);
        return result.toPromise();
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param id Token id
     */
    public getTokenById(id: number, _options?: Configuration): Promise<string> {
        const result = this.api.getTokenById(id, _options);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     */
    public getUserBasicWithHttpInfo(_options?: Configuration): Promise<HttpInfo<UserBasicInfoDTO>> {
        const result = this.api.getUserBasicWithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     */
    public getUserBasic(_options?: Configuration): Promise<UserBasicInfoDTO> {
        const result = this.api.getUserBasic(_options);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param username Username
     */
    public getUserBasic1WithHttpInfo(username: string, _options?: Configuration): Promise<HttpInfo<UserBasicInfoDTO>> {
        const result = this.api.getUserBasic1WithHttpInfo(username, _options);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param username Username
     */
    public getUserBasic1(username: string, _options?: Configuration): Promise<UserBasicInfoDTO> {
        const result = this.api.getUserBasic1(username, _options);
        return result.toPromise();
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     */
    public getUserDetailsWithHttpInfo(_options?: Configuration): Promise<HttpInfo<UserDetailsDTO>> {
        const result = this.api.getUserDetailsWithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     */
    public getUserDetails(_options?: Configuration): Promise<UserDetailsDTO> {
        const result = this.api.getUserDetails(_options);
        return result.toPromise();
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     */
    public listTokensWithHttpInfo(_options?: Configuration): Promise<HttpInfo<Array<ApiTokenInfoDTO>>> {
        const result = this.api.listTokensWithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     */
    public listTokens(_options?: Configuration): Promise<Array<ApiTokenInfoDTO>> {
        const result = this.api.listTokens(_options);
        return result.toPromise();
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param userDetailsDTO User information
     */
    public updateUserInfoWithHttpInfo(userDetailsDTO: UserDetailsDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateUserInfoWithHttpInfo(userDetailsDTO, _options);
        return result.toPromise();
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param userDetailsDTO User information
     */
    public updateUserInfo(userDetailsDTO: UserDetailsDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateUserInfo(userDetailsDTO, _options);
        return result.toPromise();
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param file User picture
     */
    public uploadUserPictureWithHttpInfo(file: HttpFile, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.uploadUserPictureWithHttpInfo(file, _options);
        return result.toPromise();
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param file User picture
     */
    public uploadUserPicture(file: HttpFile, _options?: Configuration): Promise<string> {
        const result = this.api.uploadUserPicture(file, _options);
        return result.toPromise();
    }


}



import { ObservableAccountManagerForAdminApi } from './ObservableAPI.js';

import { AccountManagerForAdminApiRequestFactory, AccountManagerForAdminApiResponseProcessor} from "../apis/AccountManagerForAdminApi.js";
export class PromiseAccountManagerForAdminApi {
    private api: ObservableAccountManagerForAdminApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AccountManagerForAdminApiRequestFactory,
        responseProcessor?: AccountManagerForAdminApiResponseProcessor
    ) {
        this.api = new ObservableAccountManagerForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create an API Token for a specified user, valid for duration seconds.
     * Create API Token for User.
     * @param username Username
     * @param duration Validity period (seconds)
     */
    public createTokenForUserWithHttpInfo(username: string, duration: number, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.createTokenForUserWithHttpInfo(username, duration, _options);
        return result.toPromise();
    }

    /**
     * Create an API Token for a specified user, valid for duration seconds.
     * Create API Token for User.
     * @param username Username
     * @param duration Validity period (seconds)
     */
    public createTokenForUser(username: string, duration: number, _options?: Configuration): Promise<string> {
        const result = this.api.createTokenForUser(username, duration, _options);
        return result.toPromise();
    }

    /**
     * Create user.
     * Create User
     * @param userFullDetailsDTO User information
     */
    public createUserWithHttpInfo(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.createUserWithHttpInfo(userFullDetailsDTO, _options);
        return result.toPromise();
    }

    /**
     * Create user.
     * Create User
     * @param userFullDetailsDTO User information
     */
    public createUser(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.createUser(userFullDetailsDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param token API Token
     */
    public deleteTokenForUserWithHttpInfo(token: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteTokenForUserWithHttpInfo(token, _options);
        return result.toPromise();
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param token API Token
     */
    public deleteTokenForUser(token: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteTokenForUser(token, _options);
        return result.toPromise();
    }

    /**
     * Delete user by username.
     * Delete User
     * @param username Username
     */
    public deleteUserWithHttpInfo(username: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteUserWithHttpInfo(username, _options);
        return result.toPromise();
    }

    /**
     * Delete user by username.
     * Delete User
     * @param username Username
     */
    public deleteUser(username: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteUser(username, _options);
        return result.toPromise();
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param token API Token
     */
    public disableTokenForUserWithHttpInfo(token: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.disableTokenForUserWithHttpInfo(token, _options);
        return result.toPromise();
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param token API Token
     */
    public disableTokenForUser(token: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.disableTokenForUser(token, _options);
        return result.toPromise();
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param username Username
     */
    public getDetailsOfUserWithHttpInfo(username: string, _options?: Configuration): Promise<HttpInfo<UserDetailsDTO>> {
        const result = this.api.getDetailsOfUserWithHttpInfo(username, _options);
        return result.toPromise();
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param username Username
     */
    public getDetailsOfUser(username: string, _options?: Configuration): Promise<UserDetailsDTO> {
        const result = this.api.getDetailsOfUser(username, _options);
        return result.toPromise();
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param token API Token
     */
    public getUserByTokenWithHttpInfo(token: string, _options?: Configuration): Promise<HttpInfo<UserDetailsDTO>> {
        const result = this.api.getUserByTokenWithHttpInfo(token, _options);
        return result.toPromise();
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param token API Token
     */
    public getUserByToken(token: string, _options?: Configuration): Promise<UserDetailsDTO> {
        const result = this.api.getUserByToken(token, _options);
        return result.toPromise();
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param username Username
     */
    public listAuthoritiesOfUserWithHttpInfo(username: string, _options?: Configuration): Promise<HttpInfo<Set<string>>> {
        const result = this.api.listAuthoritiesOfUserWithHttpInfo(username, _options);
        return result.toPromise();
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param username Username
     */
    public listAuthoritiesOfUser(username: string, _options?: Configuration): Promise<Set<string>> {
        const result = this.api.listAuthoritiesOfUser(username, _options);
        return result.toPromise();
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param username Username
     */
    public listTokensOfUserWithHttpInfo(username: string, _options?: Configuration): Promise<HttpInfo<Array<ApiTokenInfoDTO>>> {
        const result = this.api.listTokensOfUserWithHttpInfo(username, _options);
        return result.toPromise();
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param username Username
     */
    public listTokensOfUser(username: string, _options?: Configuration): Promise<Array<ApiTokenInfoDTO>> {
        const result = this.api.listTokensOfUser(username, _options);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listUsersWithHttpInfo(pageSize: number, pageNum: number, _options?: Configuration): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        const result = this.api.listUsersWithHttpInfo(pageSize, pageNum, _options);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listUsers(pageSize: number, pageNum: number, _options?: Configuration): Promise<Array<UserBasicInfoDTO>> {
        const result = this.api.listUsers(pageSize, pageNum, _options);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     */
    public listUsers1WithHttpInfo(pageSize: number, _options?: Configuration): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        const result = this.api.listUsers1WithHttpInfo(pageSize, _options);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     */
    public listUsers1(pageSize: number, _options?: Configuration): Promise<Array<UserBasicInfoDTO>> {
        const result = this.api.listUsers1(pageSize, _options);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     */
    public listUsers2WithHttpInfo(_options?: Configuration): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        const result = this.api.listUsers2WithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     */
    public listUsers2(_options?: Configuration): Promise<Array<UserBasicInfoDTO>> {
        const result = this.api.listUsers2(_options);
        return result.toPromise();
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateAuthoritiesOfUserWithHttpInfo(username: string, requestBody: Set<string>, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateAuthoritiesOfUserWithHttpInfo(username, requestBody, _options);
        return result.toPromise();
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateAuthoritiesOfUser(username: string, requestBody: Set<string>, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateAuthoritiesOfUser(username, requestBody, _options);
        return result.toPromise();
    }

    /**
     * Update user information.
     * Update User
     * @param userFullDetailsDTO User information
     */
    public updateUserWithHttpInfo(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateUserWithHttpInfo(userFullDetailsDTO, _options);
        return result.toPromise();
    }

    /**
     * Update user information.
     * Update User
     * @param userFullDetailsDTO User information
     */
    public updateUser(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateUser(userFullDetailsDTO, _options);
        return result.toPromise();
    }


}



import { ObservableAgentApi } from './ObservableAPI.js';

import { AgentApiRequestFactory, AgentApiResponseProcessor} from "../apis/AgentApi.js";
export class PromiseAgentApi {
    private api: ObservableAgentApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AgentApiRequestFactory,
        responseProcessor?: AgentApiResponseProcessor
    ) {
        this.api = new ObservableAgentApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Batch call shortcut for /api/v1/agent/details/search.
     * Batch Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentDetailsWithHttpInfo(agentQueryDTO: Array<AgentQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<AgentDetailsDTO>>>> {
        const result = this.api.batchSearchAgentDetailsWithHttpInfo(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/agent/details/search.
     * Batch Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentDetails(agentQueryDTO: Array<AgentQueryDTO>, _options?: Configuration): Promise<Array<Array<AgentDetailsDTO>>> {
        const result = this.api.batchSearchAgentDetails(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/agent/search.
     * Batch Search Agent Summaries
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentSummaryWithHttpInfo(agentQueryDTO: Array<AgentQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<AgentSummaryDTO>>>> {
        const result = this.api.batchSearchAgentSummaryWithHttpInfo(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/agent/search.
     * Batch Search Agent Summaries
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentSummary(agentQueryDTO: Array<AgentQueryDTO>, _options?: Configuration): Promise<Array<Array<AgentSummaryDTO>>> {
        const result = this.api.batchSearchAgentSummary(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 
     * Clone Agent
     * @param agentId The referenced agentId
     */
    public cloneAgentWithHttpInfo(agentId: number, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.cloneAgentWithHttpInfo(agentId, _options);
        return result.toPromise();
    }

    /**
     * Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 
     * Clone Agent
     * @param agentId The referenced agentId
     */
    public cloneAgent(agentId: number, _options?: Configuration): Promise<number> {
        const result = this.api.cloneAgent(agentId, _options);
        return result.toPromise();
    }

    /**
     * Batch clone multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Clone Agents
     * @param requestBody List of agent information to be created
     */
    public cloneAgentsWithHttpInfo(requestBody: Array<number>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.cloneAgentsWithHttpInfo(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Batch clone multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Clone Agents
     * @param requestBody List of agent information to be created
     */
    public cloneAgents(requestBody: Array<number>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.cloneAgents(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of agents according to the specified query conditions.
     * Calculate Number of Agents
     * @param agentQueryDTO Query conditions
     */
    public countAgentsWithHttpInfo(agentQueryDTO: AgentQueryDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.countAgentsWithHttpInfo(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of agents according to the specified query conditions.
     * Calculate Number of Agents
     * @param agentQueryDTO Query conditions
     */
    public countAgents(agentQueryDTO: AgentQueryDTO, _options?: Configuration): Promise<number> {
        const result = this.api.countAgents(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Agent
     * @param agentCreateDTO Information of the agent to be created
     */
    public createAgentWithHttpInfo(agentCreateDTO: AgentCreateDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.createAgentWithHttpInfo(agentCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Agent
     * @param agentCreateDTO Information of the agent to be created
     */
    public createAgent(agentCreateDTO: AgentCreateDTO, _options?: Configuration): Promise<number> {
        const result = this.api.createAgent(agentCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch create multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Create Agents
     * @param agentCreateDTO List of agent information to be created
     */
    public createAgentsWithHttpInfo(agentCreateDTO: Array<AgentCreateDTO>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.createAgentsWithHttpInfo(agentCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch create multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Create Agents
     * @param agentCreateDTO List of agent information to be created
     */
    public createAgents(agentCreateDTO: Array<AgentCreateDTO>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.createAgents(agentCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete agent. Return success or failure.
     * Delete Agent
     * @param agentId AgentId to be deleted
     */
    public deleteAgentWithHttpInfo(agentId: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteAgentWithHttpInfo(agentId, _options);
        return result.toPromise();
    }

    /**
     * Delete agent. Return success or failure.
     * Delete Agent
     * @param agentId AgentId to be deleted
     */
    public deleteAgent(agentId: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteAgent(agentId, _options);
        return result.toPromise();
    }

    /**
     * Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.
     * Batch Delete Agents
     * @param requestBody List of agentId to be deleted
     */
    public deleteAgentsWithHttpInfo(requestBody: Array<number>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.deleteAgentsWithHttpInfo(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.
     * Batch Delete Agents
     * @param requestBody List of agentId to be deleted
     */
    public deleteAgents(requestBody: Array<number>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.deleteAgents(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Get agent detailed information.
     * Get Agent Details
     * @param agentId AgentId to be obtained
     */
    public getAgentDetailsWithHttpInfo(agentId: number, _options?: Configuration): Promise<HttpInfo<AgentDetailsDTO>> {
        const result = this.api.getAgentDetailsWithHttpInfo(agentId, _options);
        return result.toPromise();
    }

    /**
     * Get agent detailed information.
     * Get Agent Details
     * @param agentId AgentId to be obtained
     */
    public getAgentDetails(agentId: number, _options?: Configuration): Promise<AgentDetailsDTO> {
        const result = this.api.getAgentDetails(agentId, _options);
        return result.toPromise();
    }

    /**
     * Get agent summary information.
     * Get Agent Summary
     * @param agentId agentId to be obtained
     */
    public getAgentSummaryWithHttpInfo(agentId: number, _options?: Configuration): Promise<HttpInfo<AgentSummaryDTO>> {
        const result = this.api.getAgentSummaryWithHttpInfo(agentId, _options);
        return result.toPromise();
    }

    /**
     * Get agent summary information.
     * Get Agent Summary
     * @param agentId agentId to be obtained
     */
    public getAgentSummary(agentId: number, _options?: Configuration): Promise<AgentSummaryDTO> {
        const result = this.api.getAgentSummary(agentId, _options);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding agentIds by agent name.
     * List Versions by Agent Name
     * @param name Agent name
     */
    public listAgentVersionsByNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<Array<AgentItemForNameDTO>>> {
        const result = this.api.listAgentVersionsByNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding agentIds by agent name.
     * List Versions by Agent Name
     * @param name Agent name
     */
    public listAgentVersionsByName(name: string, _options?: Configuration): Promise<Array<AgentItemForNameDTO>> {
        const result = this.api.listAgentVersionsByName(name, _options);
        return result.toPromise();
    }

    /**
     * Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.
     * Publish Agent
     * @param agentId The agentId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishAgentWithHttpInfo(agentId: number, visibility: string, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.publishAgentWithHttpInfo(agentId, visibility, _options);
        return result.toPromise();
    }

    /**
     * Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.
     * Publish Agent
     * @param agentId The agentId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishAgent(agentId: number, visibility: string, _options?: Configuration): Promise<number> {
        const result = this.api.publishAgent(agentId, visibility, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/agent/search, but returns detailed information of the agent.
     * Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public searchAgentDetailsWithHttpInfo(agentQueryDTO: AgentQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<AgentDetailsDTO>>> {
        const result = this.api.searchAgentDetailsWithHttpInfo(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/agent/search, but returns detailed information of the agent.
     * Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public searchAgentDetails(agentQueryDTO: AgentQueryDTO, _options?: Configuration): Promise<Array<AgentDetailsDTO>> {
        const result = this.api.searchAgentDetails(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 
     * Search Agent Summary
     * @param agentQueryDTO Query conditions
     */
    public searchAgentSummaryWithHttpInfo(agentQueryDTO: AgentQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<AgentSummaryDTO>>> {
        const result = this.api.searchAgentSummaryWithHttpInfo(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 
     * Search Agent Summary
     * @param agentQueryDTO Query conditions
     */
    public searchAgentSummary(agentQueryDTO: AgentQueryDTO, _options?: Configuration): Promise<Array<AgentSummaryDTO>> {
        const result = this.api.searchAgentSummary(agentQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Update agent, refer to /api/v1/agent/create, required field: agentId. Return success or failure.
     * Update Agent
     * @param agentId AgentId to be updated
     * @param agentUpdateDTO Agent information to be updated
     */
    public updateAgentWithHttpInfo(agentId: number, agentUpdateDTO: AgentUpdateDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateAgentWithHttpInfo(agentId, agentUpdateDTO, _options);
        return result.toPromise();
    }

    /**
     * Update agent, refer to /api/v1/agent/create, required field: agentId. Return success or failure.
     * Update Agent
     * @param agentId AgentId to be updated
     * @param agentUpdateDTO Agent information to be updated
     */
    public updateAgent(agentId: number, agentUpdateDTO: AgentUpdateDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateAgent(agentId, agentUpdateDTO, _options);
        return result.toPromise();
    }


}



import { ObservableAppConfigForAdminApi } from './ObservableAPI.js';

import { AppConfigForAdminApiRequestFactory, AppConfigForAdminApiResponseProcessor} from "../apis/AppConfigForAdminApi.js";
export class PromiseAppConfigForAdminApi {
    private api: ObservableAppConfigForAdminApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AppConfigForAdminApiRequestFactory,
        responseProcessor?: AppConfigForAdminApiResponseProcessor
    ) {
        this.api = new ObservableAppConfigForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Get all configuration information of the application.
     * Get Configurations
     */
    public getAppConfigsWithHttpInfo(_options?: Configuration): Promise<HttpInfo<AppConfigInfoDTO>> {
        const result = this.api.getAppConfigsWithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * Get all configuration information of the application.
     * Get Configurations
     */
    public getAppConfigs(_options?: Configuration): Promise<AppConfigInfoDTO> {
        const result = this.api.getAppConfigs(_options);
        return result.toPromise();
    }


}



import { ObservableAppMetaForAdminApi } from './ObservableAPI.js';

import { AppMetaForAdminApiRequestFactory, AppMetaForAdminApiResponseProcessor} from "../apis/AppMetaForAdminApi.js";
export class PromiseAppMetaForAdminApi {
    private api: ObservableAppMetaForAdminApi

    public constructor(
        configuration: Configuration,
        requestFactory?: AppMetaForAdminApiRequestFactory,
        responseProcessor?: AppMetaForAdminApiResponseProcessor
    ) {
        this.api = new ObservableAppMetaForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * This method does nothing.
     * Expose DTO definitions
     * @param openAiParam 
     * @param qwenParam 
     */
    public exposeWithHttpInfo(openAiParam: OpenAiParamDTO, qwenParam: QwenParamDTO, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.exposeWithHttpInfo(openAiParam, qwenParam, _options);
        return result.toPromise();
    }

    /**
     * This method does nothing.
     * Expose DTO definitions
     * @param openAiParam 
     * @param qwenParam 
     */
    public expose(openAiParam: OpenAiParamDTO, qwenParam: QwenParamDTO, _options?: Configuration): Promise<string> {
        const result = this.api.expose(openAiParam, qwenParam, _options);
        return result.toPromise();
    }

    /**
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     */
    public getAppMetaWithHttpInfo(_options?: Configuration): Promise<HttpInfo<AppMetaDTO>> {
        const result = this.api.getAppMetaWithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     */
    public getAppMeta(_options?: Configuration): Promise<AppMetaDTO> {
        const result = this.api.getAppMeta(_options);
        return result.toPromise();
    }


}



import { ObservableCharacterApi } from './ObservableAPI.js';

import { CharacterApiRequestFactory, CharacterApiResponseProcessor} from "../apis/CharacterApi.js";
export class PromiseCharacterApi {
    private api: ObservableCharacterApi

    public constructor(
        configuration: Configuration,
        requestFactory?: CharacterApiRequestFactory,
        responseProcessor?: CharacterApiResponseProcessor
    ) {
        this.api = new ObservableCharacterApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Add a backend configuration for a character.
     * Add Character Backend
     * @param characterId The characterId to be added a backend
     * @param characterBackendDTO The character backend to be added
     */
    public addCharacterBackendWithHttpInfo(characterId: number, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.addCharacterBackendWithHttpInfo(characterId, characterBackendDTO, _options);
        return result.toPromise();
    }

    /**
     * Add a backend configuration for a character.
     * Add Character Backend
     * @param characterId The characterId to be added a backend
     * @param characterBackendDTO The character backend to be added
     */
    public addCharacterBackend(characterId: number, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Promise<string> {
        const result = this.api.addCharacterBackend(characterId, characterBackendDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/details/search.
     * Batch Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterDetailsWithHttpInfo(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<CharacterDetailsDTO>>>> {
        const result = this.api.batchSearchCharacterDetailsWithHttpInfo(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/details/search.
     * Batch Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterDetails(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Promise<Array<Array<CharacterDetailsDTO>>> {
        const result = this.api.batchSearchCharacterDetails(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/search.
     * Batch Search Character Summaries
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterSummaryWithHttpInfo(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<CharacterSummaryDTO>>>> {
        const result = this.api.batchSearchCharacterSummaryWithHttpInfo(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/character/search.
     * Batch Search Character Summaries
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterSummary(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Promise<Array<Array<CharacterSummaryDTO>>> {
        const result = this.api.batchSearchCharacterSummary(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param characterId The referenced characterId
     */
    public cloneCharacterWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.cloneCharacterWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param characterId The referenced characterId
     */
    public cloneCharacter(characterId: number, _options?: Configuration): Promise<number> {
        const result = this.api.cloneCharacter(characterId, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param characterQueryDTO Query conditions
     */
    public countCharactersWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.countCharactersWithHttpInfo(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param characterQueryDTO Query conditions
     */
    public countCharacters(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<number> {
        const result = this.api.countCharacters(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Public Characters
     * @param characterQueryDTO Query conditions
     */
    public countPublicCharactersWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.countPublicCharactersWithHttpInfo(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Public Characters
     * @param characterQueryDTO Query conditions
     */
    public countPublicCharacters(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<number> {
        const result = this.api.countPublicCharacters(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a character.
     * Create Character
     * @param characterCreateDTO Information of the character to be created
     */
    public createCharacterWithHttpInfo(characterCreateDTO: CharacterCreateDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.createCharacterWithHttpInfo(characterCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a character.
     * Create Character
     * @param characterCreateDTO Information of the character to be created
     */
    public createCharacter(characterCreateDTO: CharacterCreateDTO, _options?: Configuration): Promise<number> {
        const result = this.api.createCharacter(characterCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param characterId The characterId to be deleted
     */
    public deleteCharacterWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteCharacterWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param characterId The characterId to be deleted
     */
    public deleteCharacter(characterId: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteCharacter(characterId, _options);
        return result.toPromise();
    }

    /**
     * Delete character by name. return the list of successfully deleted characterIds.
     * Delete Character by Name
     * @param name The character name to be deleted
     */
    public deleteCharacterByNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.deleteCharacterByNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * Delete character by name. return the list of successfully deleted characterIds.
     * Delete Character by Name
     * @param name The character name to be deleted
     */
    public deleteCharacterByName(name: string, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.deleteCharacterByName(name, _options);
        return result.toPromise();
    }

    /**
     * Delete a document of the character by key.
     * Delete Character Document
     * @param key Document key
     */
    public deleteCharacterDocumentWithHttpInfo(key: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteCharacterDocumentWithHttpInfo(key, _options);
        return result.toPromise();
    }

    /**
     * Delete a document of the character by key.
     * Delete Character Document
     * @param key Document key
     */
    public deleteCharacterDocument(key: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteCharacterDocument(key, _options);
        return result.toPromise();
    }

    /**
     * Delete a picture of the character by key.
     * Delete Character Picture
     * @param key Image key
     */
    public deleteCharacterPictureWithHttpInfo(key: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteCharacterPictureWithHttpInfo(key, _options);
        return result.toPromise();
    }

    /**
     * Delete a picture of the character by key.
     * Delete Character Picture
     * @param key Image key
     */
    public deleteCharacterPicture(key: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteCharacterPicture(key, _options);
        return result.toPromise();
    }

    /**
     * Check if the character name already exists.
     * Check If Character Name Exists
     * @param name Name
     */
    public existsCharacterNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.existsCharacterNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * Check if the character name already exists.
     * Check If Character Name Exists
     * @param name Name
     */
    public existsCharacterName(name: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.existsCharacterName(name, _options);
        return result.toPromise();
    }

    /**
     * Export character configuration in tar.gz format, including settings, documents and pictures.
     * Export Character Configuration
     * @param characterId Character identifier
     */
    public exportCharacterWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<void>> {
        const result = this.api.exportCharacterWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Export character configuration in tar.gz format, including settings, documents and pictures.
     * Export Character Configuration
     * @param characterId Character identifier
     */
    public exportCharacter(characterId: number, _options?: Configuration): Promise<void> {
        const result = this.api.exportCharacter(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param characterId CharacterId to be obtained
     */
    public getCharacterDetailsWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<CharacterDetailsDTO>> {
        const result = this.api.getCharacterDetailsWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param characterId CharacterId to be obtained
     */
    public getCharacterDetails(characterId: number, _options?: Configuration): Promise<CharacterDetailsDTO> {
        const result = this.api.getCharacterDetails(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param name Character name
     */
    public getCharacterLatestIdByNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.getCharacterLatestIdByNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param name Character name
     */
    public getCharacterLatestIdByName(name: string, _options?: Configuration): Promise<number> {
        const result = this.api.getCharacterLatestIdByName(name, _options);
        return result.toPromise();
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param characterId CharacterId to be obtained
     */
    public getCharacterSummaryWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<CharacterSummaryDTO>> {
        const result = this.api.getCharacterSummaryWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param characterId CharacterId to be obtained
     */
    public getCharacterSummary(characterId: number, _options?: Configuration): Promise<CharacterSummaryDTO> {
        const result = this.api.getCharacterSummary(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param characterId The characterId to be queried
     */
    public getDefaultCharacterBackendWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<CharacterBackendDetailsDTO>> {
        const result = this.api.getDefaultCharacterBackendWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param characterId The characterId to be queried
     */
    public getDefaultCharacterBackend(characterId: number, _options?: Configuration): Promise<CharacterBackendDetailsDTO> {
        const result = this.api.getDefaultCharacterBackend(characterId, _options);
        return result.toPromise();
    }

    /**
     * Import character configuration from a tar.gz file.
     * Import Character Configuration
     * @param file Character avatar
     */
    public importCharacterWithHttpInfo(file: HttpFile, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.importCharacterWithHttpInfo(file, _options);
        return result.toPromise();
    }

    /**
     * Import character configuration from a tar.gz file.
     * Import Character Configuration
     * @param file Character avatar
     */
    public importCharacter(file: HttpFile, _options?: Configuration): Promise<number> {
        const result = this.api.importCharacter(file, _options);
        return result.toPromise();
    }

    /**
     * List character backend identifiers.
     * List Character Backend ids
     * @param characterId The characterId to be queried
     */
    public listCharacterBackendIdsWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.listCharacterBackendIdsWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * List character backend identifiers.
     * List Character Backend ids
     * @param characterId The characterId to be queried
     */
    public listCharacterBackendIds(characterId: number, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.listCharacterBackendIds(characterId, _options);
        return result.toPromise();
    }

    /**
     * List character backends.
     * List Character Backends
     * @param characterId The characterId to be queried
     */
    public listCharacterBackendsWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<Array<CharacterBackendDetailsDTO>>> {
        const result = this.api.listCharacterBackendsWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * List character backends.
     * List Character Backends
     * @param characterId The characterId to be queried
     */
    public listCharacterBackends(characterId: number, _options?: Configuration): Promise<Array<CharacterBackendDetailsDTO>> {
        const result = this.api.listCharacterBackends(characterId, _options);
        return result.toPromise();
    }

    /**
     * List documents of the character.
     * List Character Documents
     * @param characterId Character identifier
     */
    public listCharacterDocumentsWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.listCharacterDocumentsWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * List documents of the character.
     * List Character Documents
     * @param characterId Character identifier
     */
    public listCharacterDocuments(characterId: number, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.listCharacterDocuments(characterId, _options);
        return result.toPromise();
    }

    /**
     * List pictures of the character.
     * List Character Pictures
     * @param characterId Character identifier
     */
    public listCharacterPicturesWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.listCharacterPicturesWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * List pictures of the character.
     * List Character Pictures
     * @param characterId Character identifier
     */
    public listCharacterPictures(characterId: number, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.listCharacterPictures(characterId, _options);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param name Character name
     */
    public listCharacterVersionsByNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<Array<CharacterItemForNameDTO>>> {
        const result = this.api.listCharacterVersionsByNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param name Character name
     */
    public listCharacterVersionsByName(name: string, _options?: Configuration): Promise<Array<CharacterItemForNameDTO>> {
        const result = this.api.listCharacterVersionsByName(name, _options);
        return result.toPromise();
    }

    /**
     * Create a new character name starting with a desired name.
     * Create New Character Name
     * @param desired Desired name
     */
    public newCharacterNameWithHttpInfo(desired: string, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.newCharacterNameWithHttpInfo(desired, _options);
        return result.toPromise();
    }

    /**
     * Create a new character name starting with a desired name.
     * Create New Character Name
     * @param desired Desired name
     */
    public newCharacterName(desired: string, _options?: Configuration): Promise<string> {
        const result = this.api.newCharacterName(desired, _options);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     */
    public publishCharacterWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.publishCharacterWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     */
    public publishCharacter(characterId: number, _options?: Configuration): Promise<number> {
        const result = this.api.publishCharacter(characterId, _options);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishCharacter1WithHttpInfo(characterId: number, visibility: string, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.publishCharacter1WithHttpInfo(characterId, visibility, _options);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishCharacter1(characterId: number, visibility: string, _options?: Configuration): Promise<number> {
        const result = this.api.publishCharacter1(characterId, visibility, _options);
        return result.toPromise();
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param characterBackendId The characterBackendId to be removed
     */
    public removeCharacterBackendWithHttpInfo(characterBackendId: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.removeCharacterBackendWithHttpInfo(characterBackendId, _options);
        return result.toPromise();
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param characterBackendId The characterBackendId to be removed
     */
    public removeCharacterBackend(characterBackendId: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.removeCharacterBackend(characterBackendId, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterDetailsWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<CharacterDetailsDTO>>> {
        const result = this.api.searchCharacterDetailsWithHttpInfo(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterDetails(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<Array<CharacterDetailsDTO>> {
        const result = this.api.searchCharacterDetails(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterSummaryWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryDTO>>> {
        const result = this.api.searchCharacterSummaryWithHttpInfo(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterSummary(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<Array<CharacterSummaryDTO>> {
        const result = this.api.searchCharacterSummary(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Public Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchPublicCharacterSummaryWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryDTO>>> {
        const result = this.api.searchPublicCharacterSummaryWithHttpInfo(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Public Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchPublicCharacterSummary(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Promise<Array<CharacterSummaryDTO>> {
        const result = this.api.searchPublicCharacterSummary(characterQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param characterBackendId The characterBackendId to be set to default
     */
    public setDefaultCharacterBackendWithHttpInfo(characterBackendId: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.setDefaultCharacterBackendWithHttpInfo(characterBackendId, _options);
        return result.toPromise();
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param characterBackendId The characterBackendId to be set to default
     */
    public setDefaultCharacterBackend(characterBackendId: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.setDefaultCharacterBackend(characterBackendId, _options);
        return result.toPromise();
    }

    /**
     * Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param characterId The characterId to be updated
     * @param characterUpdateDTO The character information to be updated
     */
    public updateCharacterWithHttpInfo(characterId: number, characterUpdateDTO: CharacterUpdateDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateCharacterWithHttpInfo(characterId, characterUpdateDTO, _options);
        return result.toPromise();
    }

    /**
     * Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param characterId The characterId to be updated
     * @param characterUpdateDTO The character information to be updated
     */
    public updateCharacter(characterId: number, characterUpdateDTO: CharacterUpdateDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateCharacter(characterId, characterUpdateDTO, _options);
        return result.toPromise();
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param characterBackendId The characterBackendId to be updated
     * @param characterBackendDTO The character backend configuration to be updated
     */
    public updateCharacterBackendWithHttpInfo(characterBackendId: string, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateCharacterBackendWithHttpInfo(characterBackendId, characterBackendDTO, _options);
        return result.toPromise();
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param characterBackendId The characterBackendId to be updated
     * @param characterBackendDTO The character backend configuration to be updated
     */
    public updateCharacterBackend(characterBackendId: string, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateCharacterBackend(characterBackendId, characterBackendDTO, _options);
        return result.toPromise();
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param characterId Character identifier
     * @param file Character avatar
     */
    public uploadCharacterAvatarWithHttpInfo(characterId: number, file: HttpFile, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.uploadCharacterAvatarWithHttpInfo(characterId, file, _options);
        return result.toPromise();
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param characterId Character identifier
     * @param file Character avatar
     */
    public uploadCharacterAvatar(characterId: number, file: HttpFile, _options?: Configuration): Promise<string> {
        const result = this.api.uploadCharacterAvatar(characterId, file, _options);
        return result.toPromise();
    }

    /**
     * Upload a document of the character.
     * Upload Character Document
     * @param characterId Character identifier
     * @param file Character document
     */
    public uploadCharacterDocumentWithHttpInfo(characterId: number, file: HttpFile, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.uploadCharacterDocumentWithHttpInfo(characterId, file, _options);
        return result.toPromise();
    }

    /**
     * Upload a document of the character.
     * Upload Character Document
     * @param characterId Character identifier
     * @param file Character document
     */
    public uploadCharacterDocument(characterId: number, file: HttpFile, _options?: Configuration): Promise<string> {
        const result = this.api.uploadCharacterDocument(characterId, file, _options);
        return result.toPromise();
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param characterId Character identifier
     * @param file Character picture
     */
    public uploadCharacterPictureWithHttpInfo(characterId: number, file: HttpFile, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.uploadCharacterPictureWithHttpInfo(characterId, file, _options);
        return result.toPromise();
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param characterId Character identifier
     * @param file Character picture
     */
    public uploadCharacterPicture(characterId: number, file: HttpFile, _options?: Configuration): Promise<string> {
        const result = this.api.uploadCharacterPicture(characterId, file, _options);
        return result.toPromise();
    }


}



import { ObservableChatApi } from './ObservableAPI.js';

import { ChatApiRequestFactory, ChatApiResponseProcessor} from "../apis/ChatApi.js";
export class PromiseChatApi {
    private api: ObservableChatApi

    public constructor(
        configuration: Configuration,
        requestFactory?: ChatApiRequestFactory,
        responseProcessor?: ChatApiResponseProcessor
    ) {
        this.api = new ObservableChatApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Clear memory of the chat session.
     * Clear Memory
     * @param chatId Chat session identifier
     */
    public clearMemoryWithHttpInfo(chatId: string, _options?: Configuration): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const result = this.api.clearMemoryWithHttpInfo(chatId, _options);
        return result.toPromise();
    }

    /**
     * Clear memory of the chat session.
     * Clear Memory
     * @param chatId Chat session identifier
     */
    public clearMemory(chatId: string, _options?: Configuration): Promise<Array<ChatMessageRecordDTO>> {
        const result = this.api.clearMemory(chatId, _options);
        return result.toPromise();
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param chatId Chat session identifier
     */
    public deleteChatWithHttpInfo(chatId: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteChatWithHttpInfo(chatId, _options);
        return result.toPromise();
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param chatId Chat session identifier
     */
    public deleteChat(chatId: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteChat(chatId, _options);
        return result.toPromise();
    }

    /**
     * Get default chat id of current user and the character.
     * Get Default Chat
     * @param characterId Character identifier
     */
    public getDefaultChatIdWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.getDefaultChatIdWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get default chat id of current user and the character.
     * Get Default Chat
     * @param characterId Character identifier
     */
    public getDefaultChatId(characterId: number, _options?: Configuration): Promise<string> {
        const result = this.api.getDefaultChatId(characterId, _options);
        return result.toPromise();
    }

    /**
     * Get memory usage of a chat.
     * Get Memory Usage
     * @param chatId Chat session identifier
     */
    public getMemoryUsageWithHttpInfo(chatId: string, _options?: Configuration): Promise<HttpInfo<MemoryUsageDTO>> {
        const result = this.api.getMemoryUsageWithHttpInfo(chatId, _options);
        return result.toPromise();
    }

    /**
     * Get memory usage of a chat.
     * Get Memory Usage
     * @param chatId Chat session identifier
     */
    public getMemoryUsage(chatId: string, _options?: Configuration): Promise<MemoryUsageDTO> {
        const result = this.api.getMemoryUsage(chatId, _options);
        return result.toPromise();
    }

    /**
     * List chats of current user.
     * List Chats
     */
    public listChatsWithHttpInfo(_options?: Configuration): Promise<HttpInfo<Array<ChatSessionDTO>>> {
        const result = this.api.listChatsWithHttpInfo(_options);
        return result.toPromise();
    }

    /**
     * List chats of current user.
     * List Chats
     */
    public listChats(_options?: Configuration): Promise<Array<ChatSessionDTO>> {
        const result = this.api.listChats(_options);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listDebugMessagesWithHttpInfo(chatId: string, limit: number, _options?: Configuration): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const result = this.api.listDebugMessagesWithHttpInfo(chatId, limit, _options);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listDebugMessages(chatId: string, limit: number, _options?: Configuration): Promise<Array<ChatMessageRecordDTO>> {
        const result = this.api.listDebugMessages(chatId, limit, _options);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listDebugMessages1WithHttpInfo(chatId: string, limit: number, offset: number, _options?: Configuration): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const result = this.api.listDebugMessages1WithHttpInfo(chatId, limit, offset, _options);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listDebugMessages1(chatId: string, limit: number, offset: number, _options?: Configuration): Promise<Array<ChatMessageRecordDTO>> {
        const result = this.api.listDebugMessages1(chatId, limit, offset, _options);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     */
    public listDebugMessages2WithHttpInfo(chatId: string, _options?: Configuration): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const result = this.api.listDebugMessages2WithHttpInfo(chatId, _options);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     */
    public listDebugMessages2(chatId: string, _options?: Configuration): Promise<Array<ChatMessageRecordDTO>> {
        const result = this.api.listDebugMessages2(chatId, _options);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     */
    public listMessagesWithHttpInfo(chatId: string, _options?: Configuration): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const result = this.api.listMessagesWithHttpInfo(chatId, _options);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     */
    public listMessages(chatId: string, _options?: Configuration): Promise<Array<ChatMessageRecordDTO>> {
        const result = this.api.listMessages(chatId, _options);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listMessages1WithHttpInfo(chatId: string, limit: number, offset: number, _options?: Configuration): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const result = this.api.listMessages1WithHttpInfo(chatId, limit, offset, _options);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listMessages1(chatId: string, limit: number, offset: number, _options?: Configuration): Promise<Array<ChatMessageRecordDTO>> {
        const result = this.api.listMessages1(chatId, limit, offset, _options);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listMessages2WithHttpInfo(chatId: string, limit: number, _options?: Configuration): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const result = this.api.listMessages2WithHttpInfo(chatId, limit, _options);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listMessages2(chatId: string, limit: number, _options?: Configuration): Promise<Array<ChatMessageRecordDTO>> {
        const result = this.api.listMessages2(chatId, limit, _options);
        return result.toPromise();
    }

    /**
     * Rollback messages of a chat.
     * Rollback Chat Messages
     * @param chatId Chat session identifier
     * @param count Message count to be rolled back
     */
    public rollbackMessagesWithHttpInfo(chatId: string, count: number, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.rollbackMessagesWithHttpInfo(chatId, count, _options);
        return result.toPromise();
    }

    /**
     * Rollback messages of a chat.
     * Rollback Chat Messages
     * @param chatId Chat session identifier
     * @param count Message count to be rolled back
     */
    public rollbackMessages(chatId: string, count: number, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.rollbackMessages(chatId, count, _options);
        return result.toPromise();
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public sendMessageWithHttpInfo(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: Configuration): Promise<HttpInfo<LlmResultDTO>> {
        const result = this.api.sendMessageWithHttpInfo(chatId, chatMessageDTO, _options);
        return result.toPromise();
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public sendMessage(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: Configuration): Promise<LlmResultDTO> {
        const result = this.api.sendMessage(chatId, chatMessageDTO, _options);
        return result.toPromise();
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param chatCreateDTO Parameters for starting a chat session
     */
    public startChatWithHttpInfo(chatCreateDTO: ChatCreateDTO, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.startChatWithHttpInfo(chatCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param chatCreateDTO Parameters for starting a chat session
     */
    public startChat(chatCreateDTO: ChatCreateDTO, _options?: Configuration): Promise<string> {
        const result = this.api.startChat(chatCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public streamSendMessageWithHttpInfo(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: Configuration): Promise<HttpInfo<SseEmitter>> {
        const result = this.api.streamSendMessageWithHttpInfo(chatId, chatMessageDTO, _options);
        return result.toPromise();
    }

    /**
     * Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public streamSendMessage(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: Configuration): Promise<SseEmitter> {
        const result = this.api.streamSendMessage(chatId, chatMessageDTO, _options);
        return result.toPromise();
    }

    /**
     * Update the chat session.
     * Update Chat Session
     * @param chatId Chat session identifier
     * @param chatUpdateDTO The chat session information to be updated
     */
    public updateChatWithHttpInfo(chatId: string, chatUpdateDTO: ChatUpdateDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateChatWithHttpInfo(chatId, chatUpdateDTO, _options);
        return result.toPromise();
    }

    /**
     * Update the chat session.
     * Update Chat Session
     * @param chatId Chat session identifier
     * @param chatUpdateDTO The chat session information to be updated
     */
    public updateChat(chatId: string, chatUpdateDTO: ChatUpdateDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateChat(chatId, chatUpdateDTO, _options);
        return result.toPromise();
    }


}



import { ObservableEncryptionManagerForAdminApi } from './ObservableAPI.js';

import { EncryptionManagerForAdminApiRequestFactory, EncryptionManagerForAdminApiResponseProcessor} from "../apis/EncryptionManagerForAdminApi.js";
export class PromiseEncryptionManagerForAdminApi {
    private api: ObservableEncryptionManagerForAdminApi

    public constructor(
        configuration: Configuration,
        requestFactory?: EncryptionManagerForAdminApiRequestFactory,
        responseProcessor?: EncryptionManagerForAdminApiResponseProcessor
    ) {
        this.api = new ObservableEncryptionManagerForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Encrypt a piece of text with the built-in key.
     * Encrypt Text
     * @param text Text to be encrypted
     */
    public encryptTextWithHttpInfo(text: string, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.encryptTextWithHttpInfo(text, _options);
        return result.toPromise();
    }

    /**
     * Encrypt a piece of text with the built-in key.
     * Encrypt Text
     * @param text Text to be encrypted
     */
    public encryptText(text: string, _options?: Configuration): Promise<string> {
        const result = this.api.encryptText(text, _options);
        return result.toPromise();
    }


}



import { ObservableInteractiveStatisticsApi } from './ObservableAPI.js';

import { InteractiveStatisticsApiRequestFactory, InteractiveStatisticsApiResponseProcessor} from "../apis/InteractiveStatisticsApi.js";
export class PromiseInteractiveStatisticsApi {
    private api: ObservableInteractiveStatisticsApi

    public constructor(
        configuration: Configuration,
        requestFactory?: InteractiveStatisticsApiRequestFactory,
        responseProcessor?: InteractiveStatisticsApiResponseProcessor
    ) {
        this.api = new ObservableInteractiveStatisticsApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * Add Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param delta Delta in statistical value
     */
    public addStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, delta: number, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.addStatisticWithHttpInfo(infoType, infoId, statsType, delta, _options);
        return result.toPromise();
    }

    /**
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * Add Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param delta Delta in statistical value
     */
    public addStatistic(infoType: string, infoId: string, statsType: string, delta: number, _options?: Configuration): Promise<number> {
        const result = this.api.addStatistic(infoType, infoId, statsType, delta, _options);
        return result.toPromise();
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getScoreWithHttpInfo(infoType: string, infoId: string, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.getScoreWithHttpInfo(infoType, infoId, _options);
        return result.toPromise();
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getScore(infoType: string, infoId: string, _options?: Configuration): Promise<number> {
        const result = this.api.getScore(infoType, infoId, _options);
        return result.toPromise();
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public getStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.getStatisticWithHttpInfo(infoType, infoId, statsType, _options);
        return result.toPromise();
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public getStatistic(infoType: string, infoId: string, statsType: string, _options?: Configuration): Promise<number> {
        const result = this.api.getStatistic(infoType, infoId, statsType, _options);
        return result.toPromise();
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getStatisticsWithHttpInfo(infoType: string, infoId: string, _options?: Configuration): Promise<HttpInfo<InteractiveStatsDTO>> {
        const result = this.api.getStatisticsWithHttpInfo(infoType, infoId, _options);
        return result.toPromise();
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getStatistics(infoType: string, infoId: string, _options?: Configuration): Promise<InteractiveStatsDTO> {
        const result = this.api.getStatistics(infoType, infoId, _options);
        return result.toPromise();
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public increaseStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.increaseStatisticWithHttpInfo(infoType, infoId, statsType, _options);
        return result.toPromise();
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public increaseStatistic(infoType: string, infoId: string, statsType: string, _options?: Configuration): Promise<number> {
        const result = this.api.increaseStatistic(infoType, infoId, statsType, _options);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatisticWithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<AgentSummaryStatsDTO>>> {
        const result = this.api.listAgentsByStatisticWithHttpInfo(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<Array<AgentSummaryStatsDTO>> {
        const result = this.api.listAgentsByStatistic(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic1WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<AgentSummaryStatsDTO>>> {
        const result = this.api.listAgentsByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<Array<AgentSummaryStatsDTO>> {
        const result = this.api.listAgentsByStatistic1(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic2WithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<AgentSummaryStatsDTO>>> {
        const result = this.api.listAgentsByStatistic2WithHttpInfo(statsType, asc, _options);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic2(statsType: string, asc?: string, _options?: Configuration): Promise<Array<AgentSummaryStatsDTO>> {
        const result = this.api.listAgentsByStatistic2(statsType, asc, _options);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatisticWithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const result = this.api.listCharactersByStatisticWithHttpInfo(statsType, asc, _options);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic(statsType: string, asc?: string, _options?: Configuration): Promise<Array<CharacterSummaryStatsDTO>> {
        const result = this.api.listCharactersByStatistic(statsType, asc, _options);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic1WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const result = this.api.listCharactersByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<Array<CharacterSummaryStatsDTO>> {
        const result = this.api.listCharactersByStatistic1(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic2WithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const result = this.api.listCharactersByStatistic2WithHttpInfo(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic2(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<Array<CharacterSummaryStatsDTO>> {
        const result = this.api.listCharactersByStatistic2(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * Get popular tags for a specified info type.
     * Hot Tags
     * @param infoType Info type: prompt | agent | plugin | character
     * @param pageSize Maximum quantity
     * @param text Key word
     */
    public listHotTagsWithHttpInfo(infoType: string, pageSize: number, text?: string, _options?: Configuration): Promise<HttpInfo<Array<HotTagDTO>>> {
        const result = this.api.listHotTagsWithHttpInfo(infoType, pageSize, text, _options);
        return result.toPromise();
    }

    /**
     * Get popular tags for a specified info type.
     * Hot Tags
     * @param infoType Info type: prompt | agent | plugin | character
     * @param pageSize Maximum quantity
     * @param text Key word
     */
    public listHotTags(infoType: string, pageSize: number, text?: string, _options?: Configuration): Promise<Array<HotTagDTO>> {
        const result = this.api.listHotTags(infoType, pageSize, text, _options);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatisticWithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const result = this.api.listPluginsByStatisticWithHttpInfo(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<Array<PluginSummaryStatsDTO>> {
        const result = this.api.listPluginsByStatistic(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic1WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const result = this.api.listPluginsByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<Array<PluginSummaryStatsDTO>> {
        const result = this.api.listPluginsByStatistic1(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic2WithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const result = this.api.listPluginsByStatistic2WithHttpInfo(statsType, asc, _options);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic2(statsType: string, asc?: string, _options?: Configuration): Promise<Array<PluginSummaryStatsDTO>> {
        const result = this.api.listPluginsByStatistic2(statsType, asc, _options);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatisticWithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const result = this.api.listPromptsByStatisticWithHttpInfo(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<Array<PromptSummaryStatsDTO>> {
        const result = this.api.listPromptsByStatistic(statsType, pageSize, asc, _options);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic1WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const result = this.api.listPromptsByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<Array<PromptSummaryStatsDTO>> {
        const result = this.api.listPromptsByStatistic1(statsType, pageSize, pageNum, asc, _options);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic2WithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const result = this.api.listPromptsByStatistic2WithHttpInfo(statsType, asc, _options);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic2(statsType: string, asc?: string, _options?: Configuration): Promise<Array<PromptSummaryStatsDTO>> {
        const result = this.api.listPromptsByStatistic2(statsType, asc, _options);
        return result.toPromise();
    }


}



import { ObservableOrganizationApi } from './ObservableAPI.js';

import { OrganizationApiRequestFactory, OrganizationApiResponseProcessor} from "../apis/OrganizationApi.js";
export class PromiseOrganizationApi {
    private api: ObservableOrganizationApi

    public constructor(
        configuration: Configuration,
        requestFactory?: OrganizationApiRequestFactory,
        responseProcessor?: OrganizationApiResponseProcessor
    ) {
        this.api = new ObservableOrganizationApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     * Get My Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwnersWithHttpInfo(all?: string, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.getOwnersWithHttpInfo(all, _options);
        return result.toPromise();
    }

    /**
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     * Get My Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwners(all?: string, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.getOwners(all, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwnersDotWithHttpInfo(all?: string, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.getOwnersDotWithHttpInfo(all, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwnersDot(all?: string, _options?: Configuration): Promise<string> {
        const result = this.api.getOwnersDot(all, _options);
        return result.toPromise();
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual reported owners
     */
    public getSubordinateOwnersWithHttpInfo(username: string, all?: string, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.getSubordinateOwnersWithHttpInfo(username, all, _options);
        return result.toPromise();
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual reported owners
     */
    public getSubordinateOwners(username: string, all?: string, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.getSubordinateOwners(username, all, _options);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinateSubordinatesWithHttpInfo(username: string, all?: string, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.getSubordinateSubordinatesWithHttpInfo(username, all, _options);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinateSubordinates(username: string, all?: string, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.getSubordinateSubordinates(username, all, _options);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinatesWithHttpInfo(all?: string, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.getSubordinatesWithHttpInfo(all, _options);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinates(all?: string, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.getSubordinates(all, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinatesDotWithHttpInfo(all?: string, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.getSubordinatesDotWithHttpInfo(all, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinatesDot(all?: string, _options?: Configuration): Promise<string> {
        const result = this.api.getSubordinatesDot(all, _options);
        return result.toPromise();
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param username Username
     */
    public listSubordinateAuthoritiesWithHttpInfo(username: string, _options?: Configuration): Promise<HttpInfo<Array<string>>> {
        const result = this.api.listSubordinateAuthoritiesWithHttpInfo(username, _options);
        return result.toPromise();
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param username Username
     */
    public listSubordinateAuthorities(username: string, _options?: Configuration): Promise<Array<string>> {
        const result = this.api.listSubordinateAuthorities(username, _options);
        return result.toPromise();
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     */
    public removeSubordinateSubordinatesTreeWithHttpInfo(username: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.removeSubordinateSubordinatesTreeWithHttpInfo(username, _options);
        return result.toPromise();
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     */
    public removeSubordinateSubordinatesTree(username: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.removeSubordinateSubordinatesTree(username, _options);
        return result.toPromise();
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateSubordinateAuthoritiesWithHttpInfo(username: string, requestBody: Set<string>, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateSubordinateAuthoritiesWithHttpInfo(username, requestBody, _options);
        return result.toPromise();
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateSubordinateAuthorities(username: string, requestBody: Set<string>, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateSubordinateAuthorities(username, requestBody, _options);
        return result.toPromise();
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateOwnersWithHttpInfo(username: string, requestBody: Array<string>, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateSubordinateOwnersWithHttpInfo(username, requestBody, _options);
        return result.toPromise();
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateOwners(username: string, requestBody: Array<string>, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateSubordinateOwners(username, requestBody, _options);
        return result.toPromise();
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateSubordinatesWithHttpInfo(username: string, requestBody: Array<string>, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateSubordinateSubordinatesWithHttpInfo(username, requestBody, _options);
        return result.toPromise();
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateSubordinates(username: string, requestBody: Array<string>, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateSubordinateSubordinates(username, requestBody, _options);
        return result.toPromise();
    }


}



import { ObservablePluginApi } from './ObservableAPI.js';

import { PluginApiRequestFactory, PluginApiResponseProcessor} from "../apis/PluginApi.js";
export class PromisePluginApi {
    private api: ObservablePluginApi

    public constructor(
        configuration: Configuration,
        requestFactory?: PluginApiRequestFactory,
        responseProcessor?: PluginApiResponseProcessor
    ) {
        this.api = new ObservablePluginApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Batch call shortcut for /api/v1/plugin/details/search.
     * Batch Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginDetailsWithHttpInfo(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<PluginDetailsDTO>>>> {
        const result = this.api.batchSearchPluginDetailsWithHttpInfo(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/plugin/details/search.
     * Batch Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginDetails(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Promise<Array<Array<PluginDetailsDTO>>> {
        const result = this.api.batchSearchPluginDetails(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/plugin/search.
     * Batch Search Plugin Summaries
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginSummaryWithHttpInfo(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<PluginSummaryDTO>>>> {
        const result = this.api.batchSearchPluginSummaryWithHttpInfo(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/plugin/search.
     * Batch Search Plugin Summaries
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginSummary(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Promise<Array<Array<PluginSummaryDTO>>> {
        const result = this.api.batchSearchPluginSummary(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param pluginQueryDTO Query conditions
     */
    public countPluginsWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.countPluginsWithHttpInfo(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param pluginQueryDTO Query conditions
     */
    public countPlugins(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Promise<number> {
        const result = this.api.countPlugins(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param pluginCreateDTO Information of the plugin to be created
     */
    public createPluginWithHttpInfo(pluginCreateDTO: PluginCreateDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.createPluginWithHttpInfo(pluginCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param pluginCreateDTO Information of the plugin to be created
     */
    public createPlugin(pluginCreateDTO: PluginCreateDTO, _options?: Configuration): Promise<number> {
        const result = this.api.createPlugin(pluginCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param pluginCreateDTO List of plugin information to be created
     */
    public createPluginsWithHttpInfo(pluginCreateDTO: Array<PluginCreateDTO>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.createPluginsWithHttpInfo(pluginCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param pluginCreateDTO List of plugin information to be created
     */
    public createPlugins(pluginCreateDTO: Array<PluginCreateDTO>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.createPlugins(pluginCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param pluginId The pluginId to be deleted
     */
    public deletePluginWithHttpInfo(pluginId: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deletePluginWithHttpInfo(pluginId, _options);
        return result.toPromise();
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param pluginId The pluginId to be deleted
     */
    public deletePlugin(pluginId: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.deletePlugin(pluginId, _options);
        return result.toPromise();
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param requestBody List of pluginIds to be deleted
     */
    public deletePluginsWithHttpInfo(requestBody: Array<number>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.deletePluginsWithHttpInfo(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param requestBody List of pluginIds to be deleted
     */
    public deletePlugins(requestBody: Array<number>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.deletePlugins(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param pluginId PluginId to be obtained
     */
    public getPluginDetailsWithHttpInfo(pluginId: number, _options?: Configuration): Promise<HttpInfo<PluginDetailsDTO>> {
        const result = this.api.getPluginDetailsWithHttpInfo(pluginId, _options);
        return result.toPromise();
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param pluginId PluginId to be obtained
     */
    public getPluginDetails(pluginId: number, _options?: Configuration): Promise<PluginDetailsDTO> {
        const result = this.api.getPluginDetails(pluginId, _options);
        return result.toPromise();
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param pluginId PluginId to be obtained
     */
    public getPluginSummaryWithHttpInfo(pluginId: number, _options?: Configuration): Promise<HttpInfo<PluginSummaryDTO>> {
        const result = this.api.getPluginSummaryWithHttpInfo(pluginId, _options);
        return result.toPromise();
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param pluginId PluginId to be obtained
     */
    public getPluginSummary(pluginId: number, _options?: Configuration): Promise<PluginSummaryDTO> {
        const result = this.api.getPluginSummary(pluginId, _options);
        return result.toPromise();
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param pluginId The pluginId to be fetched
     */
    public refreshPluginInfoWithHttpInfo(pluginId: number, _options?: Configuration): Promise<HttpInfo<void>> {
        const result = this.api.refreshPluginInfoWithHttpInfo(pluginId, _options);
        return result.toPromise();
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param pluginId The pluginId to be fetched
     */
    public refreshPluginInfo(pluginId: number, _options?: Configuration): Promise<void> {
        const result = this.api.refreshPluginInfo(pluginId, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginDetailsWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<PluginDetailsDTO>>> {
        const result = this.api.searchPluginDetailsWithHttpInfo(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginDetails(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Promise<Array<PluginDetailsDTO>> {
        const result = this.api.searchPluginDetails(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginSummaryWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<PluginSummaryDTO>>> {
        const result = this.api.searchPluginSummaryWithHttpInfo(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginSummary(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Promise<Array<PluginSummaryDTO>> {
        const result = this.api.searchPluginSummary(pluginQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param pluginId The pluginId to be updated
     * @param pluginUpdateDTO The plugin information to be updated
     */
    public updatePluginWithHttpInfo(pluginId: number, pluginUpdateDTO: PluginUpdateDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updatePluginWithHttpInfo(pluginId, pluginUpdateDTO, _options);
        return result.toPromise();
    }

    /**
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param pluginId The pluginId to be updated
     * @param pluginUpdateDTO The plugin information to be updated
     */
    public updatePlugin(pluginId: number, pluginUpdateDTO: PluginUpdateDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updatePlugin(pluginId, pluginUpdateDTO, _options);
        return result.toPromise();
    }


}



import { ObservablePromptApi } from './ObservableAPI.js';

import { PromptApiRequestFactory, PromptApiResponseProcessor} from "../apis/PromptApi.js";
export class PromisePromptApi {
    private api: ObservablePromptApi

    public constructor(
        configuration: Configuration,
        requestFactory?: PromptApiRequestFactory,
        responseProcessor?: PromptApiResponseProcessor
    ) {
        this.api = new ObservablePromptApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Apply parameters to prompt record.
     * Apply Parameters to Prompt Record
     * @param promptRefDTO Prompt record
     */
    public applyPromptRefWithHttpInfo(promptRefDTO: PromptRefDTO, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.applyPromptRefWithHttpInfo(promptRefDTO, _options);
        return result.toPromise();
    }

    /**
     * Apply parameters to prompt record.
     * Apply Parameters to Prompt Record
     * @param promptRefDTO Prompt record
     */
    public applyPromptRef(promptRefDTO: PromptRefDTO, _options?: Configuration): Promise<string> {
        const result = this.api.applyPromptRef(promptRefDTO, _options);
        return result.toPromise();
    }

    /**
     * Apply parameters to prompt template.
     * Apply Parameters to Prompt Template
     * @param promptTemplateDTO String type prompt template
     */
    public applyPromptTemplateWithHttpInfo(promptTemplateDTO: PromptTemplateDTO, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.applyPromptTemplateWithHttpInfo(promptTemplateDTO, _options);
        return result.toPromise();
    }

    /**
     * Apply parameters to prompt template.
     * Apply Parameters to Prompt Template
     * @param promptTemplateDTO String type prompt template
     */
    public applyPromptTemplate(promptTemplateDTO: PromptTemplateDTO, _options?: Configuration): Promise<string> {
        const result = this.api.applyPromptTemplate(promptTemplateDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/details/search.
     * Batch Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptDetailsWithHttpInfo(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<PromptDetailsDTO>>>> {
        const result = this.api.batchSearchPromptDetailsWithHttpInfo(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/details/search.
     * Batch Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptDetails(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Promise<Array<Array<PromptDetailsDTO>>> {
        const result = this.api.batchSearchPromptDetails(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/search.
     * Batch Search Prompt Summaries
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptSummaryWithHttpInfo(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Promise<HttpInfo<Array<Array<PromptSummaryDTO>>>> {
        const result = this.api.batchSearchPromptSummaryWithHttpInfo(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v1/prompt/search.
     * Batch Search Prompt Summaries
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptSummary(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Promise<Array<Array<PromptSummaryDTO>>> {
        const result = this.api.batchSearchPromptSummary(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param promptId The referenced promptId
     */
    public clonePromptWithHttpInfo(promptId: number, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.clonePromptWithHttpInfo(promptId, _options);
        return result.toPromise();
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param promptId The referenced promptId
     */
    public clonePrompt(promptId: number, _options?: Configuration): Promise<number> {
        const result = this.api.clonePrompt(promptId, _options);
        return result.toPromise();
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param requestBody List of prompt information to be created
     */
    public clonePromptsWithHttpInfo(requestBody: Array<number>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.clonePromptsWithHttpInfo(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param requestBody List of prompt information to be created
     */
    public clonePrompts(requestBody: Array<number>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.clonePrompts(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPromptsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.countPromptsWithHttpInfo(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPrompts(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<number> {
        const result = this.api.countPrompts(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Public Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPublicPromptsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.countPublicPromptsWithHttpInfo(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Public Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPublicPrompts(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<number> {
        const result = this.api.countPublicPrompts(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param promptCreateDTO Information of the prompt to be created
     */
    public createPromptWithHttpInfo(promptCreateDTO: PromptCreateDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.createPromptWithHttpInfo(promptCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param promptCreateDTO Information of the prompt to be created
     */
    public createPrompt(promptCreateDTO: PromptCreateDTO, _options?: Configuration): Promise<number> {
        const result = this.api.createPrompt(promptCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param promptCreateDTO List of prompt information to be created
     */
    public createPromptsWithHttpInfo(promptCreateDTO: Array<PromptCreateDTO>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.createPromptsWithHttpInfo(promptCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param promptCreateDTO List of prompt information to be created
     */
    public createPrompts(promptCreateDTO: Array<PromptCreateDTO>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.createPrompts(promptCreateDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param promptId The promptId to be deleted
     */
    public deletePromptWithHttpInfo(promptId: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deletePromptWithHttpInfo(promptId, _options);
        return result.toPromise();
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param promptId The promptId to be deleted
     */
    public deletePrompt(promptId: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.deletePrompt(promptId, _options);
        return result.toPromise();
    }

    /**
     * Delete prompt by name. return the list of successfully deleted promptIds.
     * Delete Prompt by Name
     * @param name The prompt name to be deleted
     */
    public deletePromptByNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.deletePromptByNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * Delete prompt by name. return the list of successfully deleted promptIds.
     * Delete Prompt by Name
     * @param name The prompt name to be deleted
     */
    public deletePromptByName(name: string, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.deletePromptByName(name, _options);
        return result.toPromise();
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param requestBody List of promptIds to be deleted
     */
    public deletePromptsWithHttpInfo(requestBody: Array<number>, _options?: Configuration): Promise<HttpInfo<Array<number>>> {
        const result = this.api.deletePromptsWithHttpInfo(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param requestBody List of promptIds to be deleted
     */
    public deletePrompts(requestBody: Array<number>, _options?: Configuration): Promise<Array<number>> {
        const result = this.api.deletePrompts(requestBody, _options);
        return result.toPromise();
    }

    /**
     * Check if the prompt name already exists.
     * Check If Prompt Name Exists
     * @param name Name
     */
    public existsPromptNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.existsPromptNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * Check if the prompt name already exists.
     * Check If Prompt Name Exists
     * @param name Name
     */
    public existsPromptName(name: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.existsPromptName(name, _options);
        return result.toPromise();
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param promptId PromptId to be obtained
     */
    public getPromptDetailsWithHttpInfo(promptId: number, _options?: Configuration): Promise<HttpInfo<PromptDetailsDTO>> {
        const result = this.api.getPromptDetailsWithHttpInfo(promptId, _options);
        return result.toPromise();
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param promptId PromptId to be obtained
     */
    public getPromptDetails(promptId: number, _options?: Configuration): Promise<PromptDetailsDTO> {
        const result = this.api.getPromptDetails(promptId, _options);
        return result.toPromise();
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param promptId PromptId to be obtained
     */
    public getPromptSummaryWithHttpInfo(promptId: number, _options?: Configuration): Promise<HttpInfo<PromptSummaryDTO>> {
        const result = this.api.getPromptSummaryWithHttpInfo(promptId, _options);
        return result.toPromise();
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param promptId PromptId to be obtained
     */
    public getPromptSummary(promptId: number, _options?: Configuration): Promise<PromptSummaryDTO> {
        const result = this.api.getPromptSummary(promptId, _options);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param name Prompt name
     */
    public listPromptVersionsByNameWithHttpInfo(name: string, _options?: Configuration): Promise<HttpInfo<Array<PromptItemForNameDTO>>> {
        const result = this.api.listPromptVersionsByNameWithHttpInfo(name, _options);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param name Prompt name
     */
    public listPromptVersionsByName(name: string, _options?: Configuration): Promise<Array<PromptItemForNameDTO>> {
        const result = this.api.listPromptVersionsByName(name, _options);
        return result.toPromise();
    }

    /**
     * Create a new prompt name starting with a desired name.
     * Create New Prompt Name
     * @param desired Desired name
     */
    public newPromptNameWithHttpInfo(desired: string, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.newPromptNameWithHttpInfo(desired, _options);
        return result.toPromise();
    }

    /**
     * Create a new prompt name starting with a desired name.
     * Create New Prompt Name
     * @param desired Desired name
     */
    public newPromptName(desired: string, _options?: Configuration): Promise<string> {
        const result = this.api.newPromptName(desired, _options);
        return result.toPromise();
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param promptId The promptId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishPromptWithHttpInfo(promptId: number, visibility: string, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.publishPromptWithHttpInfo(promptId, visibility, _options);
        return result.toPromise();
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param promptId The promptId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishPrompt(promptId: number, visibility: string, _options?: Configuration): Promise<number> {
        const result = this.api.publishPrompt(promptId, visibility, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public searchPromptDetailsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<PromptDetailsDTO>>> {
        const result = this.api.searchPromptDetailsWithHttpInfo(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Same as /api/v1/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public searchPromptDetails(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<Array<PromptDetailsDTO>> {
        const result = this.api.searchPromptDetails(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPromptSummaryWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<PromptSummaryDTO>>> {
        const result = this.api.searchPromptSummaryWithHttpInfo(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPromptSummary(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<Array<PromptSummaryDTO>> {
        const result = this.api.searchPromptSummary(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Public Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPublicPromptSummaryWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<HttpInfo<Array<PromptSummaryDTO>>> {
        const result = this.api.searchPublicPromptSummaryWithHttpInfo(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Public Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPublicPromptSummary(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Promise<Array<PromptSummaryDTO>> {
        const result = this.api.searchPublicPromptSummary(promptQueryDTO, _options);
        return result.toPromise();
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param promptAiParamDTO Call parameters
     */
    public sendPromptWithHttpInfo(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Promise<HttpInfo<LlmResultDTO>> {
        const result = this.api.sendPromptWithHttpInfo(promptAiParamDTO, _options);
        return result.toPromise();
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param promptAiParamDTO Call parameters
     */
    public sendPrompt(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Promise<LlmResultDTO> {
        const result = this.api.sendPrompt(promptAiParamDTO, _options);
        return result.toPromise();
    }

    /**
     * Refer to /api/v1/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param promptAiParamDTO Call parameters
     */
    public streamSendPromptWithHttpInfo(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Promise<HttpInfo<SseEmitter>> {
        const result = this.api.streamSendPromptWithHttpInfo(promptAiParamDTO, _options);
        return result.toPromise();
    }

    /**
     * Refer to /api/v1/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param promptAiParamDTO Call parameters
     */
    public streamSendPrompt(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Promise<SseEmitter> {
        const result = this.api.streamSendPrompt(promptAiParamDTO, _options);
        return result.toPromise();
    }

    /**
     * Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param promptId The promptId to be updated
     * @param promptUpdateDTO The prompt information to be updated
     */
    public updatePromptWithHttpInfo(promptId: number, promptUpdateDTO: PromptUpdateDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updatePromptWithHttpInfo(promptId, promptUpdateDTO, _options);
        return result.toPromise();
    }

    /**
     * Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param promptId The promptId to be updated
     * @param promptUpdateDTO The prompt information to be updated
     */
    public updatePrompt(promptId: number, promptUpdateDTO: PromptUpdateDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updatePrompt(promptId, promptUpdateDTO, _options);
        return result.toPromise();
    }


}



import { ObservablePromptTaskApi } from './ObservableAPI.js';

import { PromptTaskApiRequestFactory, PromptTaskApiResponseProcessor} from "../apis/PromptTaskApi.js";
export class PromisePromptTaskApi {
    private api: ObservablePromptTaskApi

    public constructor(
        configuration: Configuration,
        requestFactory?: PromptTaskApiRequestFactory,
        responseProcessor?: PromptTaskApiResponseProcessor
    ) {
        this.api = new ObservablePromptTaskApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create a prompt task.
     * Create Prompt Task
     * @param promptTaskDTO The prompt task to be added
     */
    public createPromptTaskWithHttpInfo(promptTaskDTO: PromptTaskDTO, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.createPromptTaskWithHttpInfo(promptTaskDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a prompt task.
     * Create Prompt Task
     * @param promptTaskDTO The prompt task to be added
     */
    public createPromptTask(promptTaskDTO: PromptTaskDTO, _options?: Configuration): Promise<string> {
        const result = this.api.createPromptTask(promptTaskDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param promptTaskId The promptTaskId to be deleted
     */
    public deletePromptTaskWithHttpInfo(promptTaskId: string, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deletePromptTaskWithHttpInfo(promptTaskId, _options);
        return result.toPromise();
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param promptTaskId The promptTaskId to be deleted
     */
    public deletePromptTask(promptTaskId: string, _options?: Configuration): Promise<boolean> {
        const result = this.api.deletePromptTask(promptTaskId, _options);
        return result.toPromise();
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param promptTaskId The promptTaskId to be queried
     */
    public getPromptTaskWithHttpInfo(promptTaskId: string, _options?: Configuration): Promise<HttpInfo<PromptTaskDetailsDTO>> {
        const result = this.api.getPromptTaskWithHttpInfo(promptTaskId, _options);
        return result.toPromise();
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param promptTaskId The promptTaskId to be queried
     */
    public getPromptTask(promptTaskId: string, _options?: Configuration): Promise<PromptTaskDetailsDTO> {
        const result = this.api.getPromptTask(promptTaskId, _options);
        return result.toPromise();
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param promptTaskId The promptTaskId to be updated
     * @param promptTaskDTO The prompt task info to be updated
     */
    public updatePromptTaskWithHttpInfo(promptTaskId: string, promptTaskDTO: PromptTaskDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updatePromptTaskWithHttpInfo(promptTaskId, promptTaskDTO, _options);
        return result.toPromise();
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param promptTaskId The promptTaskId to be updated
     * @param promptTaskDTO The prompt task info to be updated
     */
    public updatePromptTask(promptTaskId: string, promptTaskDTO: PromptTaskDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updatePromptTask(promptTaskId, promptTaskDTO, _options);
        return result.toPromise();
    }


}



import { ObservableRagApi } from './ObservableAPI.js';

import { RagApiRequestFactory, RagApiResponseProcessor} from "../apis/RagApi.js";
export class PromiseRagApi {
    private api: ObservableRagApi

    public constructor(
        configuration: Configuration,
        requestFactory?: RagApiRequestFactory,
        responseProcessor?: RagApiResponseProcessor
    ) {
        this.api = new ObservableRagApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Cancel a RAG task.
     * Cancel RAG Task
     * @param taskId The taskId to be canceled
     */
    public cancelRagTaskWithHttpInfo(taskId: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.cancelRagTaskWithHttpInfo(taskId, _options);
        return result.toPromise();
    }

    /**
     * Cancel a RAG task.
     * Cancel RAG Task
     * @param taskId The taskId to be canceled
     */
    public cancelRagTask(taskId: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.cancelRagTask(taskId, _options);
        return result.toPromise();
    }

    /**
     * Create a RAG task.
     * Create RAG Task
     * @param characterId The characterId to be added a RAG task
     * @param ragTaskDTO The RAG task to be added
     */
    public createRagTaskWithHttpInfo(characterId: number, ragTaskDTO: RagTaskDTO, _options?: Configuration): Promise<HttpInfo<number>> {
        const result = this.api.createRagTaskWithHttpInfo(characterId, ragTaskDTO, _options);
        return result.toPromise();
    }

    /**
     * Create a RAG task.
     * Create RAG Task
     * @param characterId The characterId to be added a RAG task
     * @param ragTaskDTO The RAG task to be added
     */
    public createRagTask(characterId: number, ragTaskDTO: RagTaskDTO, _options?: Configuration): Promise<number> {
        const result = this.api.createRagTask(characterId, ragTaskDTO, _options);
        return result.toPromise();
    }

    /**
     * Delete a RAG task.
     * Delete RAG Task
     * @param taskId The taskId to be deleted
     */
    public deleteRagTaskWithHttpInfo(taskId: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.deleteRagTaskWithHttpInfo(taskId, _options);
        return result.toPromise();
    }

    /**
     * Delete a RAG task.
     * Delete RAG Task
     * @param taskId The taskId to be deleted
     */
    public deleteRagTask(taskId: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.deleteRagTask(taskId, _options);
        return result.toPromise();
    }

    /**
     * Get the RAG task details.
     * Get RAG Task
     * @param taskId The taskId to be queried
     */
    public getRagTaskWithHttpInfo(taskId: number, _options?: Configuration): Promise<HttpInfo<RagTaskDetailsDTO>> {
        const result = this.api.getRagTaskWithHttpInfo(taskId, _options);
        return result.toPromise();
    }

    /**
     * Get the RAG task details.
     * Get RAG Task
     * @param taskId The taskId to be queried
     */
    public getRagTask(taskId: number, _options?: Configuration): Promise<RagTaskDetailsDTO> {
        const result = this.api.getRagTask(taskId, _options);
        return result.toPromise();
    }

    /**
     * Get the RAG task execution status: pending | running | succeeded | failed | canceled.
     * Get RAG Task Status
     * @param taskId The taskId to be queried status
     */
    public getRagTaskStatusWithHttpInfo(taskId: number, _options?: Configuration): Promise<HttpInfo<string>> {
        const result = this.api.getRagTaskStatusWithHttpInfo(taskId, _options);
        return result.toPromise();
    }

    /**
     * Get the RAG task execution status: pending | running | succeeded | failed | canceled.
     * Get RAG Task Status
     * @param taskId The taskId to be queried status
     */
    public getRagTaskStatus(taskId: number, _options?: Configuration): Promise<string> {
        const result = this.api.getRagTaskStatus(taskId, _options);
        return result.toPromise();
    }

    /**
     * List the RAG tasks by characterId.
     * List RAG Tasks
     * @param characterId The characterId to be queried
     */
    public listRagTasksWithHttpInfo(characterId: number, _options?: Configuration): Promise<HttpInfo<Array<RagTaskDetailsDTO>>> {
        const result = this.api.listRagTasksWithHttpInfo(characterId, _options);
        return result.toPromise();
    }

    /**
     * List the RAG tasks by characterId.
     * List RAG Tasks
     * @param characterId The characterId to be queried
     */
    public listRagTasks(characterId: number, _options?: Configuration): Promise<Array<RagTaskDetailsDTO>> {
        const result = this.api.listRagTasks(characterId, _options);
        return result.toPromise();
    }

    /**
     * Start a RAG task.
     * Start RAG Task
     * @param taskId The taskId to be started
     */
    public startRagTaskWithHttpInfo(taskId: number, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.startRagTaskWithHttpInfo(taskId, _options);
        return result.toPromise();
    }

    /**
     * Start a RAG task.
     * Start RAG Task
     * @param taskId The taskId to be started
     */
    public startRagTask(taskId: number, _options?: Configuration): Promise<boolean> {
        const result = this.api.startRagTask(taskId, _options);
        return result.toPromise();
    }

    /**
     * Update a RAG task.
     * Update RAG Task
     * @param taskId The taskId to be updated
     * @param ragTaskDTO The prompt task info to be updated
     */
    public updateRagTaskWithHttpInfo(taskId: number, ragTaskDTO: RagTaskDTO, _options?: Configuration): Promise<HttpInfo<boolean>> {
        const result = this.api.updateRagTaskWithHttpInfo(taskId, ragTaskDTO, _options);
        return result.toPromise();
    }

    /**
     * Update a RAG task.
     * Update RAG Task
     * @param taskId The taskId to be updated
     * @param ragTaskDTO The prompt task info to be updated
     */
    public updateRagTask(taskId: number, ragTaskDTO: RagTaskDTO, _options?: Configuration): Promise<boolean> {
        const result = this.api.updateRagTask(taskId, ragTaskDTO, _options);
        return result.toPromise();
    }


}



