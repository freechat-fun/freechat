import { ResponseContext, RequestContext, HttpFile, HttpInfo } from '../http/http.js';
import { Configuration, PromiseConfigurationOptions, wrapOptions } from '../configuration.js'
import { PromiseMiddleware, Middleware, PromiseMiddlewareWrapper } from '../middleware.js';

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
import { RagTaskDTO } from '../models/RagTaskDTO.js';
import { RagTaskDetailsDTO } from '../models/RagTaskDetailsDTO.js';
import { SseEmitter } from '../models/SseEmitter.js';
import { TgMessageDTO } from '../models/TgMessageDTO.js';
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
    public addAiApiKeyWithHttpInfo(aiApiKeyCreateDTO: AiApiKeyCreateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.addAiApiKeyWithHttpInfo(aiApiKeyCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Add a credential for the model service.
     * Add Model Provider Credential
     * @param aiApiKeyCreateDTO Model call credential information
     */
    public addAiApiKey(aiApiKeyCreateDTO: AiApiKeyCreateDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.addAiApiKey(aiApiKeyCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param id Credential identifier
     */
    public deleteAiApiKeyWithHttpInfo(id: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteAiApiKeyWithHttpInfo(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param id Credential identifier
     */
    public deleteAiApiKey(id: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteAiApiKey(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param id Credential identifier
     */
    public disableAiApiKeyWithHttpInfo(id: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableAiApiKeyWithHttpInfo(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param id Credential identifier
     */
    public disableAiApiKey(id: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableAiApiKey(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param id Credential identifier
     */
    public enableAiApiKeyWithHttpInfo(id: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.enableAiApiKeyWithHttpInfo(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param id Credential identifier
     */
    public enableAiApiKey(id: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.enableAiApiKey(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param id Credential identifier
     */
    public getAiApiKeyWithHttpInfo(id: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<AiApiKeyInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAiApiKeyWithHttpInfo(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param id Credential identifier
     */
    public getAiApiKey(id: number, _options?: PromiseConfigurationOptions): Promise<AiApiKeyInfoDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAiApiKey(id, observableOptions);
        return result.toPromise();
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param provider Model provider
     */
    public listAiApiKeysWithHttpInfo(provider: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<AiApiKeyInfoDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAiApiKeysWithHttpInfo(provider, observableOptions);
        return result.toPromise();
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param provider Model provider
     */
    public listAiApiKeys(provider: string, _options?: PromiseConfigurationOptions): Promise<Array<AiApiKeyInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAiApiKeys(provider, observableOptions);
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
     * @param duration Token validity duration (seconds)
     */
    public createTokenWithHttpInfo(duration: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createTokenWithHttpInfo(duration, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param duration Token validity duration (seconds)
     */
    public createToken(duration: number, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createToken(duration, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     */
    public createToken1WithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createToken1WithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     */
    public createToken1(_options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createToken1(observableOptions);
        return result.toPromise();
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param token Token content
     */
    public deleteTokenWithHttpInfo(token: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteTokenWithHttpInfo(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param token Token content
     */
    public deleteToken(token: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteToken(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param id Token id
     */
    public deleteTokenByIdWithHttpInfo(id: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteTokenByIdWithHttpInfo(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param id Token id
     */
    public deleteTokenById(id: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteTokenById(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param token Token content
     */
    public disableTokenWithHttpInfo(token: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableTokenWithHttpInfo(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param token Token content
     */
    public disableToken(token: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableToken(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param id Token id
     */
    public disableTokenByIdWithHttpInfo(id: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableTokenByIdWithHttpInfo(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param id Token id
     */
    public disableTokenById(id: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableTokenById(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param id Token id
     */
    public getTokenByIdWithHttpInfo(id: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getTokenByIdWithHttpInfo(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param id Token id
     */
    public getTokenById(id: number, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getTokenById(id, observableOptions);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param username Username
     */
    public getUserBasicWithHttpInfo(username: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<UserBasicInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserBasicWithHttpInfo(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param username Username
     */
    public getUserBasic(username: string, _options?: PromiseConfigurationOptions): Promise<UserBasicInfoDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserBasic(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     */
    public getUserBasic1WithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<UserBasicInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserBasic1WithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     */
    public getUserBasic1(_options?: PromiseConfigurationOptions): Promise<UserBasicInfoDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserBasic1(observableOptions);
        return result.toPromise();
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     */
    public getUserDetailsWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<UserDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserDetailsWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     */
    public getUserDetails(_options?: PromiseConfigurationOptions): Promise<UserDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserDetails(observableOptions);
        return result.toPromise();
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     */
    public listTokensWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ApiTokenInfoDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTokensWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     */
    public listTokens(_options?: PromiseConfigurationOptions): Promise<Array<ApiTokenInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTokens(observableOptions);
        return result.toPromise();
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param userDetailsDTO User information
     */
    public updateUserInfoWithHttpInfo(userDetailsDTO: UserDetailsDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateUserInfoWithHttpInfo(userDetailsDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param userDetailsDTO User information
     */
    public updateUserInfo(userDetailsDTO: UserDetailsDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateUserInfo(userDetailsDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param file User picture
     */
    public uploadUserPictureWithHttpInfo(file: HttpFile, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadUserPictureWithHttpInfo(file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param file User picture
     */
    public uploadUserPicture(file: HttpFile, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadUserPicture(file, observableOptions);
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
    public createTokenForUserWithHttpInfo(username: string, duration: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createTokenForUserWithHttpInfo(username, duration, observableOptions);
        return result.toPromise();
    }

    /**
     * Create an API Token for a specified user, valid for duration seconds.
     * Create API Token for User.
     * @param username Username
     * @param duration Validity period (seconds)
     */
    public createTokenForUser(username: string, duration: number, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createTokenForUser(username, duration, observableOptions);
        return result.toPromise();
    }

    /**
     * Create user.
     * Create User
     * @param userFullDetailsDTO User information
     */
    public createUserWithHttpInfo(userFullDetailsDTO: UserFullDetailsDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createUserWithHttpInfo(userFullDetailsDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create user.
     * Create User
     * @param userFullDetailsDTO User information
     */
    public createUser(userFullDetailsDTO: UserFullDetailsDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createUser(userFullDetailsDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param token API Token
     */
    public deleteTokenForUserWithHttpInfo(token: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteTokenForUserWithHttpInfo(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param token API Token
     */
    public deleteTokenForUser(token: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteTokenForUser(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete user by username.
     * Delete User
     * @param username Username
     */
    public deleteUserWithHttpInfo(username: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteUserWithHttpInfo(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete user by username.
     * Delete User
     * @param username Username
     */
    public deleteUser(username: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteUser(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param token API Token
     */
    public disableTokenForUserWithHttpInfo(token: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableTokenForUserWithHttpInfo(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param token API Token
     */
    public disableTokenForUser(token: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.disableTokenForUser(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param username Username
     */
    public getDetailsOfUserWithHttpInfo(username: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<UserDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDetailsOfUserWithHttpInfo(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param username Username
     */
    public getDetailsOfUser(username: string, _options?: PromiseConfigurationOptions): Promise<UserDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDetailsOfUser(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param token API Token
     */
    public getUserByTokenWithHttpInfo(token: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<UserDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserByTokenWithHttpInfo(token, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param token API Token
     */
    public getUserByToken(token: string, _options?: PromiseConfigurationOptions): Promise<UserDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getUserByToken(token, observableOptions);
        return result.toPromise();
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param username Username
     */
    public listAuthoritiesOfUserWithHttpInfo(username: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Set<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAuthoritiesOfUserWithHttpInfo(username, observableOptions);
        return result.toPromise();
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param username Username
     */
    public listAuthoritiesOfUser(username: string, _options?: PromiseConfigurationOptions): Promise<Set<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAuthoritiesOfUser(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param username Username
     */
    public listTokensOfUserWithHttpInfo(username: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ApiTokenInfoDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTokensOfUserWithHttpInfo(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param username Username
     */
    public listTokensOfUser(username: string, _options?: PromiseConfigurationOptions): Promise<Array<ApiTokenInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTokensOfUser(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listUsersWithHttpInfo(pageSize: number, pageNum: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listUsersWithHttpInfo(pageSize, pageNum, observableOptions);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listUsers(pageSize: number, pageNum: number, _options?: PromiseConfigurationOptions): Promise<Array<UserBasicInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listUsers(pageSize, pageNum, observableOptions);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     */
    public listUsers1WithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listUsers1WithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     */
    public listUsers1(_options?: PromiseConfigurationOptions): Promise<Array<UserBasicInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listUsers1(observableOptions);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     */
    public listUsers2WithHttpInfo(pageSize: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<UserBasicInfoDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listUsers2WithHttpInfo(pageSize, observableOptions);
        return result.toPromise();
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     */
    public listUsers2(pageSize: number, _options?: PromiseConfigurationOptions): Promise<Array<UserBasicInfoDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listUsers2(pageSize, observableOptions);
        return result.toPromise();
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateAuthoritiesOfUserWithHttpInfo(username: string, requestBody: Set<string>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateAuthoritiesOfUserWithHttpInfo(username, requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateAuthoritiesOfUser(username: string, requestBody: Set<string>, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateAuthoritiesOfUser(username, requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Update user information.
     * Update User
     * @param userFullDetailsDTO User information
     */
    public updateUserWithHttpInfo(userFullDetailsDTO: UserFullDetailsDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateUserWithHttpInfo(userFullDetailsDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update user information.
     * Update User
     * @param userFullDetailsDTO User information
     */
    public updateUser(userFullDetailsDTO: UserFullDetailsDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateUser(userFullDetailsDTO, observableOptions);
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
     * Batch call shortcut for /api/v2/agent/details/search.
     * Batch Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentDetailsWithHttpInfo(agentQueryDTO: Array<AgentQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<AgentDetailsDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchAgentDetailsWithHttpInfo(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/agent/details/search.
     * Batch Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentDetails(agentQueryDTO: Array<AgentQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<AgentDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchAgentDetails(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/agent/search.
     * Batch Search Agent Summaries
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentSummaryWithHttpInfo(agentQueryDTO: Array<AgentQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<AgentSummaryDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchAgentSummaryWithHttpInfo(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/agent/search.
     * Batch Search Agent Summaries
     * @param agentQueryDTO Query conditions
     */
    public batchSearchAgentSummary(agentQueryDTO: Array<AgentQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<AgentSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchAgentSummary(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 
     * Clone Agent
     * @param agentId The referenced agentId
     */
    public cloneAgentWithHttpInfo(agentId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cloneAgentWithHttpInfo(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Enter the agentId, generate a new record, the content is basically the same as the original agent, but the following fields are different: - Version number is 1 - Visibility is private - The parent agent is the source agentId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new agentId. 
     * Clone Agent
     * @param agentId The referenced agentId
     */
    public cloneAgent(agentId: number, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cloneAgent(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch clone multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Clone Agents
     * @param requestBody List of agent information to be created
     */
    public cloneAgentsWithHttpInfo(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cloneAgentsWithHttpInfo(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch clone multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Clone Agents
     * @param requestBody List of agent information to be created
     */
    public cloneAgents(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cloneAgents(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of agents according to the specified query conditions.
     * Calculate Number of Agents
     * @param agentQueryDTO Query conditions
     */
    public countAgentsWithHttpInfo(agentQueryDTO: AgentQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countAgentsWithHttpInfo(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of agents according to the specified query conditions.
     * Calculate Number of Agents
     * @param agentQueryDTO Query conditions
     */
    public countAgents(agentQueryDTO: AgentQueryDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countAgents(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Agent
     * @param agentCreateDTO Information of the agent to be created
     */
    public createAgentWithHttpInfo(agentCreateDTO: AgentCreateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createAgentWithHttpInfo(agentCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a agent, ignore required fields: - Agent name - Agent configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Agent
     * @param agentCreateDTO Information of the agent to be created
     */
    public createAgent(agentCreateDTO: AgentCreateDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createAgent(agentCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch create multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Create Agents
     * @param agentCreateDTO List of agent information to be created
     */
    public createAgentsWithHttpInfo(agentCreateDTO: Array<AgentCreateDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createAgentsWithHttpInfo(agentCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch create multiple agents. Ensure transactionality, return the agentId list after success.
     * Batch Create Agents
     * @param agentCreateDTO List of agent information to be created
     */
    public createAgents(agentCreateDTO: Array<AgentCreateDTO>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createAgents(agentCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete agent. Return success or failure.
     * Delete Agent
     * @param agentId AgentId to be deleted
     */
    public deleteAgentWithHttpInfo(agentId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteAgentWithHttpInfo(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete agent. Return success or failure.
     * Delete Agent
     * @param agentId AgentId to be deleted
     */
    public deleteAgent(agentId: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteAgent(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.
     * Batch Delete Agents
     * @param requestBody List of agentId to be deleted
     */
    public deleteAgentsWithHttpInfo(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteAgentsWithHttpInfo(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete multiple agents. Ensure transactionality, return the list of successfully deleted agentId.
     * Batch Delete Agents
     * @param requestBody List of agentId to be deleted
     */
    public deleteAgents(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteAgents(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Get agent detailed information.
     * Get Agent Details
     * @param agentId AgentId to be obtained
     */
    public getAgentDetailsWithHttpInfo(agentId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<AgentDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAgentDetailsWithHttpInfo(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get agent detailed information.
     * Get Agent Details
     * @param agentId AgentId to be obtained
     */
    public getAgentDetails(agentId: number, _options?: PromiseConfigurationOptions): Promise<AgentDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAgentDetails(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get agent summary information.
     * Get Agent Summary
     * @param agentId agentId to be obtained
     */
    public getAgentSummaryWithHttpInfo(agentId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<AgentSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAgentSummaryWithHttpInfo(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get agent summary information.
     * Get Agent Summary
     * @param agentId agentId to be obtained
     */
    public getAgentSummary(agentId: number, _options?: PromiseConfigurationOptions): Promise<AgentSummaryDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAgentSummary(agentId, observableOptions);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding agentIds by agent name.
     * List Versions by Agent Name
     * @param name Agent name
     */
    public listAgentVersionsByNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<AgentItemForNameDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentVersionsByNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding agentIds by agent name.
     * List Versions by Agent Name
     * @param name Agent name
     */
    public listAgentVersionsByName(name: string, _options?: PromiseConfigurationOptions): Promise<Array<AgentItemForNameDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentVersionsByName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.
     * Publish Agent
     * @param agentId The agentId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishAgentWithHttpInfo(agentId: number, visibility: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishAgentWithHttpInfo(agentId, visibility, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish agent, draft content becomes formal content, version number increases by 1. After successful publication, a new agentId will be generated and returned. You need to specify the visibility for publication.
     * Publish Agent
     * @param agentId The agentId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishAgent(agentId: number, visibility: string, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishAgent(agentId, visibility, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/agent/search, but returns detailed information of the agent.
     * Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public searchAgentDetailsWithHttpInfo(agentQueryDTO: AgentQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<AgentDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchAgentDetailsWithHttpInfo(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/agent/search, but returns detailed information of the agent.
     * Search Agent Details
     * @param agentQueryDTO Query conditions
     */
    public searchAgentDetails(agentQueryDTO: AgentQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<AgentDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchAgentDetails(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 
     * Search Agent Summary
     * @param agentQueryDTO Query conditions
     */
    public searchAgentSummaryWithHttpInfo(agentQueryDTO: AgentQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<AgentSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchAgentSummaryWithHttpInfo(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search agents: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the agent summary content. - Support pagination. 
     * Search Agent Summary
     * @param agentQueryDTO Query conditions
     */
    public searchAgentSummary(agentQueryDTO: AgentQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<AgentSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchAgentSummary(agentQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update agent, refer to /api/v2/agent/create, required field: agentId. Return success or failure.
     * Update Agent
     * @param agentId AgentId to be updated
     * @param agentUpdateDTO Agent information to be updated
     */
    public updateAgentWithHttpInfo(agentId: number, agentUpdateDTO: AgentUpdateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateAgentWithHttpInfo(agentId, agentUpdateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update agent, refer to /api/v2/agent/create, required field: agentId. Return success or failure.
     * Update Agent
     * @param agentId AgentId to be updated
     * @param agentUpdateDTO Agent information to be updated
     */
    public updateAgent(agentId: number, agentUpdateDTO: AgentUpdateDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateAgent(agentId, agentUpdateDTO, observableOptions);
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
     * Get default configuration information of the application.
     * Get Default Config
     */
    public getDefaultConfigWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDefaultConfigWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Get default configuration information of the application.
     * Get Default Config
     */
    public getDefaultConfig(_options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDefaultConfig(observableOptions);
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
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     */
    public getAppMetaWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<AppMetaDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAppMetaWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     */
    public getAppMeta(_options?: PromiseConfigurationOptions): Promise<AppMetaDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getAppMeta(observableOptions);
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
     * @param characterUid The characterUid to be added a backend
     * @param characterBackendDTO The character backend to be added
     */
    public addCharacterBackendWithHttpInfo(characterUid: string, characterBackendDTO: CharacterBackendDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.addCharacterBackendWithHttpInfo(characterUid, characterBackendDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Add a backend configuration for a character.
     * Add Character Backend
     * @param characterUid The characterUid to be added a backend
     * @param characterBackendDTO The character backend to be added
     */
    public addCharacterBackend(characterUid: string, characterBackendDTO: CharacterBackendDTO, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.addCharacterBackend(characterUid, characterBackendDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/character/details/search.
     * Batch Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterDetailsWithHttpInfo(characterQueryDTO: Array<CharacterQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<CharacterDetailsDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchCharacterDetailsWithHttpInfo(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/character/details/search.
     * Batch Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterDetails(characterQueryDTO: Array<CharacterQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<CharacterDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchCharacterDetails(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/character/search.
     * Batch Search Character Summaries
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterSummaryWithHttpInfo(characterQueryDTO: Array<CharacterQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<CharacterSummaryDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchCharacterSummaryWithHttpInfo(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/character/search.
     * Batch Search Character Summaries
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterSummary(characterQueryDTO: Array<CharacterQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<CharacterSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchCharacterSummary(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param characterId The referenced characterId
     */
    public cloneCharacterWithHttpInfo(characterId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cloneCharacterWithHttpInfo(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param characterId The referenced characterId
     */
    public cloneCharacter(characterId: number, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cloneCharacter(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param characterQueryDTO Query conditions
     */
    public countCharactersWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countCharactersWithHttpInfo(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param characterQueryDTO Query conditions
     */
    public countCharacters(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countCharacters(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Public Characters
     * @param characterQueryDTO Query conditions
     */
    public countPublicCharactersWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPublicCharactersWithHttpInfo(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Public Characters
     * @param characterQueryDTO Query conditions
     */
    public countPublicCharacters(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPublicCharacters(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a character.
     * Create Character
     * @param characterCreateDTO Information of the character to be created
     */
    public createCharacterWithHttpInfo(characterCreateDTO: CharacterCreateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createCharacterWithHttpInfo(characterCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a character.
     * Create Character
     * @param characterCreateDTO Information of the character to be created
     */
    public createCharacter(characterCreateDTO: CharacterCreateDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createCharacter(characterCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param characterId The characterId to be deleted
     */
    public deleteCharacterWithHttpInfo(characterId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterWithHttpInfo(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param characterId The characterId to be deleted
     */
    public deleteCharacter(characterId: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacter(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete character by name. return the list of successfully deleted characterIds.
     * Delete Character by Name
     * @param name The character name to be deleted
     */
    public deleteCharacterByNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterByNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete character by name. return the list of successfully deleted characterIds.
     * Delete Character by Name
     * @param name The character name to be deleted
     */
    public deleteCharacterByName(name: string, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterByName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete character by uid. return the list of successfully deleted characterIds.
     * Delete Character by Uid
     * @param characterUid The character uid to be deleted
     */
    public deleteCharacterByUidWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterByUidWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete character by uid. return the list of successfully deleted characterIds.
     * Delete Character by Uid
     * @param characterUid The character uid to be deleted
     */
    public deleteCharacterByUid(characterUid: string, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterByUid(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a document of the character by key.
     * Delete Character Document
     * @param key Document key
     */
    public deleteCharacterDocumentWithHttpInfo(key: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterDocumentWithHttpInfo(key, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a document of the character by key.
     * Delete Character Document
     * @param key Document key
     */
    public deleteCharacterDocument(key: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterDocument(key, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a picture of the character by key.
     * Delete Character Picture
     * @param key Image key
     */
    public deleteCharacterPictureWithHttpInfo(key: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterPictureWithHttpInfo(key, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a picture of the character by key.
     * Delete Character Picture
     * @param key Image key
     */
    public deleteCharacterPicture(key: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterPicture(key, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a video of the character by key.
     * Delete Character Video
     * @param key Video key
     */
    public deleteCharacterVideoWithHttpInfo(key: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterVideoWithHttpInfo(key, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a video of the character by key.
     * Delete Character Video
     * @param key Video key
     */
    public deleteCharacterVideo(key: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterVideo(key, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a voice of the character by key.
     * Delete Character Voice
     * @param characterBackendId The characterBackendId
     * @param key Voice key
     */
    public deleteCharacterVoiceWithHttpInfo(characterBackendId: string, key: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterVoiceWithHttpInfo(characterBackendId, key, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a voice of the character by key.
     * Delete Character Voice
     * @param characterBackendId The characterBackendId
     * @param key Voice key
     */
    public deleteCharacterVoice(characterBackendId: string, key: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteCharacterVoice(characterBackendId, key, observableOptions);
        return result.toPromise();
    }

    /**
     * Check if the character name already exists.
     * Check If Character Name Exists
     * @param name Name
     */
    public existsCharacterNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.existsCharacterNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Check if the character name already exists.
     * Check If Character Name Exists
     * @param name Name
     */
    public existsCharacterName(name: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.existsCharacterName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Export character configuration in tar.gz format, including settings, documents and pictures.
     * Export Character Configuration
     * @param characterId Character identifier
     */
    public exportCharacterWithHttpInfo(characterId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<void>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.exportCharacterWithHttpInfo(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Export character configuration in tar.gz format, including settings, documents and pictures.
     * Export Character Configuration
     * @param characterId Character identifier
     */
    public exportCharacter(characterId: number, _options?: PromiseConfigurationOptions): Promise<void> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.exportCharacter(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param characterId CharacterId to be obtained
     */
    public getCharacterDetailsWithHttpInfo(characterId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<CharacterDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCharacterDetailsWithHttpInfo(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param characterId CharacterId to be obtained
     */
    public getCharacterDetails(characterId: number, _options?: PromiseConfigurationOptions): Promise<CharacterDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCharacterDetails(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param name Character name
     */
    public getCharacterLatestIdByNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCharacterLatestIdByNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param name Character name
     */
    public getCharacterLatestIdByName(name: string, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCharacterLatestIdByName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param characterId CharacterId to be obtained
     */
    public getCharacterSummaryWithHttpInfo(characterId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<CharacterSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCharacterSummaryWithHttpInfo(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param characterId CharacterId to be obtained
     */
    public getCharacterSummary(characterId: number, _options?: PromiseConfigurationOptions): Promise<CharacterSummaryDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getCharacterSummary(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param characterUid The characterUid to be queried
     */
    public getDefaultCharacterBackendWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<CharacterBackendDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDefaultCharacterBackendWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param characterUid The characterUid to be queried
     */
    public getDefaultCharacterBackend(characterUid: string, _options?: PromiseConfigurationOptions): Promise<CharacterBackendDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDefaultCharacterBackend(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Import character configuration from a tar.gz file.
     * Import Character Configuration
     * @param file Character avatar
     */
    public importCharacterWithHttpInfo(file: HttpFile, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.importCharacterWithHttpInfo(file, observableOptions);
        return result.toPromise();
    }

    /**
     * Import character configuration from a tar.gz file.
     * Import Character Configuration
     * @param file Character avatar
     */
    public importCharacter(file: HttpFile, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.importCharacter(file, observableOptions);
        return result.toPromise();
    }

    /**
     * List character backend identifiers.
     * List Character Backend ids
     * @param characterUid The characterUid to be queried
     */
    public listCharacterBackendIdsWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterBackendIdsWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List character backend identifiers.
     * List Character Backend ids
     * @param characterUid The characterUid to be queried
     */
    public listCharacterBackendIds(characterUid: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterBackendIds(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List character backends.
     * List Character Backends
     * @param characterUid The characterUid to be queried
     */
    public listCharacterBackendsWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterBackendDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterBackendsWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List character backends.
     * List Character Backends
     * @param characterUid The characterUid to be queried
     */
    public listCharacterBackends(characterUid: string, _options?: PromiseConfigurationOptions): Promise<Array<CharacterBackendDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterBackends(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List documents of the character.
     * List Character Documents
     * @param characterUid Character unique identifier
     */
    public listCharacterDocumentsWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterDocumentsWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List documents of the character.
     * List Character Documents
     * @param characterUid Character unique identifier
     */
    public listCharacterDocuments(characterUid: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterDocuments(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List pictures of the character.
     * List Character Pictures
     * @param characterUid Character unique identifier
     */
    public listCharacterPicturesWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterPicturesWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List pictures of the character.
     * List Character Pictures
     * @param characterUid Character unique identifier
     */
    public listCharacterPictures(characterUid: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterPictures(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param name Character name
     */
    public listCharacterVersionsByNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterItemForNameDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterVersionsByNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param name Character name
     */
    public listCharacterVersionsByName(name: string, _options?: PromiseConfigurationOptions): Promise<Array<CharacterItemForNameDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterVersionsByName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * List videos of the character.
     * List Character Videos
     * @param characterUid Character unique identifier
     */
    public listCharacterVideosWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterVideosWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List videos of the character.
     * List Character Videos
     * @param characterUid Character unique identifier
     */
    public listCharacterVideos(characterUid: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterVideos(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List voices of the character.
     * List Character Voices
     * @param characterBackendId The characterBackendId
     */
    public listCharacterVoicesWithHttpInfo(characterBackendId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterVoicesWithHttpInfo(characterBackendId, observableOptions);
        return result.toPromise();
    }

    /**
     * List voices of the character.
     * List Character Voices
     * @param characterBackendId The characterBackendId
     */
    public listCharacterVoices(characterBackendId: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharacterVoices(characterBackendId, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a new character name starting with a desired name.
     * Create New Character Name
     * @param desired Desired name
     */
    public newCharacterNameWithHttpInfo(desired: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.newCharacterNameWithHttpInfo(desired, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a new character name starting with a desired name.
     * Create New Character Name
     * @param desired Desired name
     */
    public newCharacterName(desired: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.newCharacterName(desired, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishCharacterWithHttpInfo(characterId: number, visibility: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishCharacterWithHttpInfo(characterId, visibility, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishCharacter(characterId: number, visibility: string, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishCharacter(characterId, visibility, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     */
    public publishCharacter1WithHttpInfo(characterId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishCharacter1WithHttpInfo(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     */
    public publishCharacter1(characterId: number, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishCharacter1(characterId, observableOptions);
        return result.toPromise();
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param characterBackendId The characterBackendId to be removed
     */
    public removeCharacterBackendWithHttpInfo(characterBackendId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.removeCharacterBackendWithHttpInfo(characterBackendId, observableOptions);
        return result.toPromise();
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param characterBackendId The characterBackendId to be removed
     */
    public removeCharacterBackend(characterBackendId: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.removeCharacterBackend(characterBackendId, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterDetailsWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchCharacterDetailsWithHttpInfo(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterDetails(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<CharacterDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchCharacterDetails(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterSummaryWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchCharacterSummaryWithHttpInfo(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterSummary(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<CharacterSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchCharacterSummary(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Public Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchPublicCharacterSummaryWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPublicCharacterSummaryWithHttpInfo(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Public Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchPublicCharacterSummary(characterQueryDTO: CharacterQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<CharacterSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPublicCharacterSummary(characterQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param characterBackendId The characterBackendId to be set to default
     */
    public setDefaultCharacterBackendWithHttpInfo(characterBackendId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.setDefaultCharacterBackendWithHttpInfo(characterBackendId, observableOptions);
        return result.toPromise();
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param characterBackendId The characterBackendId to be set to default
     */
    public setDefaultCharacterBackend(characterBackendId: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.setDefaultCharacterBackend(characterBackendId, observableOptions);
        return result.toPromise();
    }

    /**
     * Update character, refer to /api/v2/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param characterId The characterId to be updated
     * @param characterUpdateDTO The character information to be updated
     */
    public updateCharacterWithHttpInfo(characterId: number, characterUpdateDTO: CharacterUpdateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateCharacterWithHttpInfo(characterId, characterUpdateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update character, refer to /api/v2/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param characterId The characterId to be updated
     * @param characterUpdateDTO The character information to be updated
     */
    public updateCharacter(characterId: number, characterUpdateDTO: CharacterUpdateDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateCharacter(characterId, characterUpdateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param characterBackendId The characterBackendId to be updated
     * @param characterBackendDTO The character backend configuration to be updated
     */
    public updateCharacterBackendWithHttpInfo(characterBackendId: string, characterBackendDTO: CharacterBackendDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateCharacterBackendWithHttpInfo(characterBackendId, characterBackendDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param characterBackendId The characterBackendId to be updated
     * @param characterBackendDTO The character backend configuration to be updated
     */
    public updateCharacterBackend(characterBackendId: string, characterBackendDTO: CharacterBackendDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateCharacterBackend(characterBackendId, characterBackendDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param characterUid Character unique identifier
     * @param file Character avatar
     */
    public uploadCharacterAvatarWithHttpInfo(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterAvatarWithHttpInfo(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param characterUid Character unique identifier
     * @param file Character avatar
     */
    public uploadCharacterAvatar(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterAvatar(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a document of the character.
     * Upload Character Document
     * @param characterUid Character unique identifier
     * @param file Character document
     */
    public uploadCharacterDocumentWithHttpInfo(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterDocumentWithHttpInfo(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a document of the character.
     * Upload Character Document
     * @param characterUid Character unique identifier
     * @param file Character document
     */
    public uploadCharacterDocument(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterDocument(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param characterUid Character unique identifier
     * @param file Character picture
     */
    public uploadCharacterPictureWithHttpInfo(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterPictureWithHttpInfo(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param characterUid Character unique identifier
     * @param file Character picture
     */
    public uploadCharacterPicture(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterPicture(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a video of the character.
     * Upload Character Video
     * @param characterUid Character unique identifier
     * @param file Character video
     */
    public uploadCharacterVideoWithHttpInfo(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterVideoWithHttpInfo(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a video of the character.
     * Upload Character Video
     * @param characterUid Character unique identifier
     * @param file Character video
     */
    public uploadCharacterVideo(characterUid: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterVideo(characterUid, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a voice of the character.
     * Upload Character Voice
     * @param characterBackendId The characterBackendId
     * @param file Character voice
     */
    public uploadCharacterVoiceWithHttpInfo(characterBackendId: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterVoiceWithHttpInfo(characterBackendId, file, observableOptions);
        return result.toPromise();
    }

    /**
     * Upload a voice of the character.
     * Upload Character Voice
     * @param characterBackendId The characterBackendId
     * @param file Character voice
     */
    public uploadCharacterVoice(characterBackendId: string, file: HttpFile, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.uploadCharacterVoice(characterBackendId, file, observableOptions);
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
    public clearMemoryWithHttpInfo(chatId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.clearMemoryWithHttpInfo(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * Clear memory of the chat session.
     * Clear Memory
     * @param chatId Chat session identifier
     */
    public clearMemory(chatId: string, _options?: PromiseConfigurationOptions): Promise<Array<ChatMessageRecordDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.clearMemory(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param chatId Chat session identifier
     */
    public deleteChatWithHttpInfo(chatId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteChatWithHttpInfo(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param chatId Chat session identifier
     */
    public deleteChat(chatId: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteChat(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get default chat id of current user and the character.
     * Get Default Chat
     * @param characterUid Character uid
     */
    public getDefaultChatIdWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDefaultChatIdWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Get default chat id of current user and the character.
     * Get Default Chat
     * @param characterUid Character uid
     */
    public getDefaultChatId(characterUid: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getDefaultChatId(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Get memory usage of a chat.
     * Get Memory Usage
     * @param chatId Chat session identifier
     */
    public getMemoryUsageWithHttpInfo(chatId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<MemoryUsageDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getMemoryUsageWithHttpInfo(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get memory usage of a chat.
     * Get Memory Usage
     * @param chatId Chat session identifier
     */
    public getMemoryUsage(chatId: string, _options?: PromiseConfigurationOptions): Promise<MemoryUsageDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getMemoryUsage(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * List chats of current user.
     * List Chats
     */
    public listChatsWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatSessionDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listChatsWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * List chats of current user.
     * List Chats
     */
    public listChats(_options?: PromiseConfigurationOptions): Promise<Array<ChatSessionDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listChats(observableOptions);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listDebugMessagesWithHttpInfo(chatId: string, limit: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listDebugMessagesWithHttpInfo(chatId, limit, observableOptions);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listDebugMessages(chatId: string, limit: number, _options?: PromiseConfigurationOptions): Promise<Array<ChatMessageRecordDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listDebugMessages(chatId, limit, observableOptions);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listDebugMessages1WithHttpInfo(chatId: string, limit: number, offset: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listDebugMessages1WithHttpInfo(chatId, limit, offset, observableOptions);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listDebugMessages1(chatId: string, limit: number, offset: number, _options?: PromiseConfigurationOptions): Promise<Array<ChatMessageRecordDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listDebugMessages1(chatId, limit, offset, observableOptions);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     */
    public listDebugMessages2WithHttpInfo(chatId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listDebugMessages2WithHttpInfo(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * List debug messages of a chat.
     * List Chat Debug Messages
     * @param chatId Chat session identifier
     */
    public listDebugMessages2(chatId: string, _options?: PromiseConfigurationOptions): Promise<Array<ChatMessageRecordDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listDebugMessages2(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listMessagesWithHttpInfo(chatId: string, limit: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listMessagesWithHttpInfo(chatId, limit, observableOptions);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listMessages(chatId: string, limit: number, _options?: PromiseConfigurationOptions): Promise<Array<ChatMessageRecordDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listMessages(chatId, limit, observableOptions);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listMessages1WithHttpInfo(chatId: string, limit: number, offset: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listMessages1WithHttpInfo(chatId, limit, offset, observableOptions);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listMessages1(chatId: string, limit: number, offset: number, _options?: PromiseConfigurationOptions): Promise<Array<ChatMessageRecordDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listMessages1(chatId, limit, offset, observableOptions);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     */
    public listMessages2WithHttpInfo(chatId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<ChatMessageRecordDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listMessages2WithHttpInfo(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     */
    public listMessages2(chatId: string, _options?: PromiseConfigurationOptions): Promise<Array<ChatMessageRecordDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listMessages2(chatId, observableOptions);
        return result.toPromise();
    }

    /**
     * Rollback messages of a chat.
     * Rollback Chat Messages
     * @param chatId Chat session identifier
     * @param count Message count to be rolled back
     */
    public rollbackMessagesWithHttpInfo(chatId: string, count: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.rollbackMessagesWithHttpInfo(chatId, count, observableOptions);
        return result.toPromise();
    }

    /**
     * Rollback messages of a chat.
     * Rollback Chat Messages
     * @param chatId Chat session identifier
     * @param count Message count to be rolled back
     */
    public rollbackMessages(chatId: string, count: number, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.rollbackMessages(chatId, count, observableOptions);
        return result.toPromise();
    }

    /**
     * Send a message to assistant for a new chat message.
     * Send Assistant for Chat Message
     * @param chatId Chat session identifier
     * @param assistantUid Assistant uid
     */
    public sendAssistantWithHttpInfo(chatId: string, assistantUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<LlmResultDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.sendAssistantWithHttpInfo(chatId, assistantUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Send a message to assistant for a new chat message.
     * Send Assistant for Chat Message
     * @param chatId Chat session identifier
     * @param assistantUid Assistant uid
     */
    public sendAssistant(chatId: string, assistantUid: string, _options?: PromiseConfigurationOptions): Promise<LlmResultDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.sendAssistant(chatId, assistantUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public sendMessageWithHttpInfo(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<LlmResultDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.sendMessageWithHttpInfo(chatId, chatMessageDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public sendMessage(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: PromiseConfigurationOptions): Promise<LlmResultDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.sendMessage(chatId, chatMessageDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param chatCreateDTO Parameters for starting a chat session
     */
    public startChatWithHttpInfo(chatCreateDTO: ChatCreateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.startChatWithHttpInfo(chatCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param chatCreateDTO Parameters for starting a chat session
     */
    public startChat(chatCreateDTO: ChatCreateDTO, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.startChat(chatCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Refer to /api/v2/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.
     * Send Assistant for Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param assistantUid Assistant uid
     */
    public streamSendAssistantWithHttpInfo(chatId: string, assistantUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<SseEmitter>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.streamSendAssistantWithHttpInfo(chatId, assistantUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Refer to /api/v2/chat/send/assistant/{chatId}/{assistantUid}, stream back chunks of the response.
     * Send Assistant for Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param assistantUid Assistant uid
     */
    public streamSendAssistant(chatId: string, assistantUid: string, _options?: PromiseConfigurationOptions): Promise<SseEmitter> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.streamSendAssistant(chatId, assistantUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Refer to /api/v2/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public streamSendMessageWithHttpInfo(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<SseEmitter>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.streamSendMessageWithHttpInfo(chatId, chatMessageDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Refer to /api/v2/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param chatMessageDTO Chat message
     */
    public streamSendMessage(chatId: string, chatMessageDTO: ChatMessageDTO, _options?: PromiseConfigurationOptions): Promise<SseEmitter> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.streamSendMessage(chatId, chatMessageDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update the chat session.
     * Update Chat Session
     * @param chatId Chat session identifier
     * @param chatUpdateDTO The chat session information to be updated
     */
    public updateChatWithHttpInfo(chatId: string, chatUpdateDTO: ChatUpdateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateChatWithHttpInfo(chatId, chatUpdateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update the chat session.
     * Update Chat Session
     * @param chatId Chat session identifier
     * @param chatUpdateDTO The chat session information to be updated
     */
    public updateChat(chatId: string, chatUpdateDTO: ChatUpdateDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateChat(chatId, chatUpdateDTO, observableOptions);
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
    public encryptTextWithHttpInfo(text: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.encryptTextWithHttpInfo(text, observableOptions);
        return result.toPromise();
    }

    /**
     * Encrypt a piece of text with the built-in key.
     * Encrypt Text
     * @param text Text to be encrypted
     */
    public encryptText(text: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.encryptText(text, observableOptions);
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
    public addStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, delta: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.addStatisticWithHttpInfo(infoType, infoId, statsType, delta, observableOptions);
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
    public addStatistic(infoType: string, infoId: string, statsType: string, delta: number, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.addStatistic(infoType, infoId, statsType, delta, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getScoreWithHttpInfo(infoType: string, infoId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getScoreWithHttpInfo(infoType, infoId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getScore(infoType: string, infoId: string, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getScore(infoType, infoId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public getStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getStatisticWithHttpInfo(infoType, infoId, statsType, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public getStatistic(infoType: string, infoId: string, statsType: string, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getStatistic(infoType, infoId, statsType, observableOptions);
        return result.toPromise();
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getStatisticsWithHttpInfo(infoType: string, infoId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<InteractiveStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getStatisticsWithHttpInfo(infoType, infoId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     */
    public getStatistics(infoType: string, infoId: string, _options?: PromiseConfigurationOptions): Promise<InteractiveStatsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getStatistics(infoType, infoId, observableOptions);
        return result.toPromise();
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public increaseStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.increaseStatisticWithHttpInfo(infoType, infoId, statsType, observableOptions);
        return result.toPromise();
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param infoType Info type: prompt | agent | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public increaseStatistic(infoType: string, infoId: string, statsType: string, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.increaseStatistic(infoType, infoId, statsType, observableOptions);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatisticWithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<AgentSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentsByStatisticWithHttpInfo(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<AgentSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentsByStatistic(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic1WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<AgentSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentsByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<AgentSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentsByStatistic1(statsType, pageSize, pageNum, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic2WithHttpInfo(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<AgentSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentsByStatistic2WithHttpInfo(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List agents based on statistics, including interactive statistical data.
     * List Agents by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listAgentsByStatistic2(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<AgentSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listAgentsByStatistic2(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatisticWithHttpInfo(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharactersByStatisticWithHttpInfo(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<CharacterSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharactersByStatistic(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic1WithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharactersByStatistic1WithHttpInfo(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic1(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<CharacterSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharactersByStatistic1(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic2WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharactersByStatistic2WithHttpInfo(statsType, pageSize, pageNum, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic2(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<CharacterSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listCharactersByStatistic2(statsType, pageSize, pageNum, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * Get popular tags for a specified info type.
     * Hot Tags
     * @param infoType Info type: prompt | agent | plugin | character
     * @param pageSize Maximum quantity
     * @param [text] Key word
     */
    public listHotTagsWithHttpInfo(infoType: string, pageSize: number, text?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<HotTagDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listHotTagsWithHttpInfo(infoType, pageSize, text, observableOptions);
        return result.toPromise();
    }

    /**
     * Get popular tags for a specified info type.
     * Hot Tags
     * @param infoType Info type: prompt | agent | plugin | character
     * @param pageSize Maximum quantity
     * @param [text] Key word
     */
    public listHotTags(infoType: string, pageSize: number, text?: string, _options?: PromiseConfigurationOptions): Promise<Array<HotTagDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listHotTags(infoType, pageSize, text, observableOptions);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatisticWithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPluginsByStatisticWithHttpInfo(statsType, pageSize, pageNum, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<PluginSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPluginsByStatistic(statsType, pageSize, pageNum, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic1WithHttpInfo(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPluginsByStatistic1WithHttpInfo(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic1(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<PluginSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPluginsByStatistic1(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic2WithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPluginsByStatistic2WithHttpInfo(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic2(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<PluginSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPluginsByStatistic2(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatisticWithHttpInfo(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptsByStatisticWithHttpInfo(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic(statsType: string, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<PromptSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptsByStatistic(statsType, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic1WithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptsByStatistic1WithHttpInfo(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic1(statsType: string, pageSize: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<PromptSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptsByStatistic1(statsType, pageSize, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic2WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptsByStatistic2WithHttpInfo(statsType, pageSize, pageNum, asc, observableOptions);
        return result.toPromise();
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param [asc] Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic2(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: PromiseConfigurationOptions): Promise<Array<PromptSummaryStatsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptsByStatistic2(statsType, pageSize, pageNum, asc, observableOptions);
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
     * @param [all] Whether to return virtual reported owners
     */
    public getOwnersWithHttpInfo(all?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getOwnersWithHttpInfo(all, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     * Get My Superior Relationship
     * @param [all] Whether to return virtual reported owners
     */
    public getOwners(all?: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getOwners(all, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param [all] Whether to return virtual reported owners
     */
    public getOwnersDotWithHttpInfo(all?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getOwnersDotWithHttpInfo(all, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param [all] Whether to return virtual reported owners
     */
    public getOwnersDot(all?: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getOwnersDot(all, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param [all] Whether to return virtual reported owners
     */
    public getSubordinateOwnersWithHttpInfo(username: string, all?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinateOwnersWithHttpInfo(username, all, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param [all] Whether to return virtual reported owners
     */
    public getSubordinateOwners(username: string, all?: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinateOwners(username, all, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param [all] Whether to return virtual managed subordinates
     */
    public getSubordinateSubordinatesWithHttpInfo(username: string, all?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinateSubordinatesWithHttpInfo(username, all, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param [all] Whether to return virtual managed subordinates
     */
    public getSubordinateSubordinates(username: string, all?: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinateSubordinates(username, all, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param [all] Whether to return virtual managed subordinates
     */
    public getSubordinatesWithHttpInfo(all?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinatesWithHttpInfo(all, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param [all] Whether to return virtual managed subordinates
     */
    public getSubordinates(all?: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinates(all, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param [all] Whether to return virtual managed subordinates
     */
    public getSubordinatesDotWithHttpInfo(all?: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinatesDotWithHttpInfo(all, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param [all] Whether to return virtual managed subordinates
     */
    public getSubordinatesDot(all?: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getSubordinatesDot(all, observableOptions);
        return result.toPromise();
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param username Username
     */
    public listSubordinateAuthoritiesWithHttpInfo(username: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listSubordinateAuthoritiesWithHttpInfo(username, observableOptions);
        return result.toPromise();
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param username Username
     */
    public listSubordinateAuthorities(username: string, _options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listSubordinateAuthorities(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     */
    public removeSubordinateSubordinatesTreeWithHttpInfo(username: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.removeSubordinateSubordinatesTreeWithHttpInfo(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     */
    public removeSubordinateSubordinatesTree(username: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.removeSubordinateSubordinatesTree(username, observableOptions);
        return result.toPromise();
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateSubordinateAuthoritiesWithHttpInfo(username: string, requestBody: Set<string>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateSubordinateAuthoritiesWithHttpInfo(username, requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateSubordinateAuthorities(username: string, requestBody: Set<string>, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateSubordinateAuthorities(username, requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateOwnersWithHttpInfo(username: string, requestBody: Array<string>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateSubordinateOwnersWithHttpInfo(username, requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateOwners(username: string, requestBody: Array<string>, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateSubordinateOwners(username, requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateSubordinatesWithHttpInfo(username: string, requestBody: Array<string>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateSubordinateSubordinatesWithHttpInfo(username, requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateSubordinates(username: string, requestBody: Array<string>, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateSubordinateSubordinates(username, requestBody, observableOptions);
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
     * Batch call shortcut for /api/v2/plugin/details/search.
     * Batch Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginDetailsWithHttpInfo(pluginQueryDTO: Array<PluginQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<PluginDetailsDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPluginDetailsWithHttpInfo(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/plugin/details/search.
     * Batch Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginDetails(pluginQueryDTO: Array<PluginQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<PluginDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPluginDetails(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/plugin/search.
     * Batch Search Plugin Summaries
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginSummaryWithHttpInfo(pluginQueryDTO: Array<PluginQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<PluginSummaryDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPluginSummaryWithHttpInfo(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/plugin/search.
     * Batch Search Plugin Summaries
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginSummary(pluginQueryDTO: Array<PluginQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<PluginSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPluginSummary(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param pluginQueryDTO Query conditions
     */
    public countPluginsWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPluginsWithHttpInfo(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param pluginQueryDTO Query conditions
     */
    public countPlugins(pluginQueryDTO: PluginQueryDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPlugins(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param pluginCreateDTO Information of the plugin to be created
     */
    public createPluginWithHttpInfo(pluginCreateDTO: PluginCreateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPluginWithHttpInfo(pluginCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param pluginCreateDTO Information of the plugin to be created
     */
    public createPlugin(pluginCreateDTO: PluginCreateDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPlugin(pluginCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param pluginCreateDTO List of plugin information to be created
     */
    public createPluginsWithHttpInfo(pluginCreateDTO: Array<PluginCreateDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPluginsWithHttpInfo(pluginCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param pluginCreateDTO List of plugin information to be created
     */
    public createPlugins(pluginCreateDTO: Array<PluginCreateDTO>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPlugins(pluginCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param pluginId The pluginId to be deleted
     */
    public deletePluginWithHttpInfo(pluginId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePluginWithHttpInfo(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param pluginId The pluginId to be deleted
     */
    public deletePlugin(pluginId: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePlugin(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param requestBody List of pluginIds to be deleted
     */
    public deletePluginsWithHttpInfo(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePluginsWithHttpInfo(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param requestBody List of pluginIds to be deleted
     */
    public deletePlugins(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePlugins(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param pluginId PluginId to be obtained
     */
    public getPluginDetailsWithHttpInfo(pluginId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<PluginDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPluginDetailsWithHttpInfo(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param pluginId PluginId to be obtained
     */
    public getPluginDetails(pluginId: number, _options?: PromiseConfigurationOptions): Promise<PluginDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPluginDetails(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param pluginId PluginId to be obtained
     */
    public getPluginSummaryWithHttpInfo(pluginId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<PluginSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPluginSummaryWithHttpInfo(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param pluginId PluginId to be obtained
     */
    public getPluginSummary(pluginId: number, _options?: PromiseConfigurationOptions): Promise<PluginSummaryDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPluginSummary(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param pluginId The pluginId to be fetched
     */
    public refreshPluginInfoWithHttpInfo(pluginId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<void>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.refreshPluginInfoWithHttpInfo(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param pluginId The pluginId to be fetched
     */
    public refreshPluginInfo(pluginId: number, _options?: PromiseConfigurationOptions): Promise<void> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.refreshPluginInfo(pluginId, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginDetailsWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PluginDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPluginDetailsWithHttpInfo(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginDetails(pluginQueryDTO: PluginQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<PluginDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPluginDetails(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginSummaryWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PluginSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPluginSummaryWithHttpInfo(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginSummary(pluginQueryDTO: PluginQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<PluginSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPluginSummary(pluginQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update plugin, refer to /api/v2/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param pluginId The pluginId to be updated
     * @param pluginUpdateDTO The plugin information to be updated
     */
    public updatePluginWithHttpInfo(pluginId: number, pluginUpdateDTO: PluginUpdateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updatePluginWithHttpInfo(pluginId, pluginUpdateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update plugin, refer to /api/v2/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param pluginId The pluginId to be updated
     * @param pluginUpdateDTO The plugin information to be updated
     */
    public updatePlugin(pluginId: number, pluginUpdateDTO: PluginUpdateDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updatePlugin(pluginId, pluginUpdateDTO, observableOptions);
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
    public applyPromptRefWithHttpInfo(promptRefDTO: PromptRefDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.applyPromptRefWithHttpInfo(promptRefDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Apply parameters to prompt record.
     * Apply Parameters to Prompt Record
     * @param promptRefDTO Prompt record
     */
    public applyPromptRef(promptRefDTO: PromptRefDTO, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.applyPromptRef(promptRefDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Apply parameters to prompt template.
     * Apply Parameters to Prompt Template
     * @param promptTemplateDTO String type prompt template
     */
    public applyPromptTemplateWithHttpInfo(promptTemplateDTO: PromptTemplateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.applyPromptTemplateWithHttpInfo(promptTemplateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Apply parameters to prompt template.
     * Apply Parameters to Prompt Template
     * @param promptTemplateDTO String type prompt template
     */
    public applyPromptTemplate(promptTemplateDTO: PromptTemplateDTO, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.applyPromptTemplate(promptTemplateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/prompt/details/search.
     * Batch Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptDetailsWithHttpInfo(promptQueryDTO: Array<PromptQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<PromptDetailsDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPromptDetailsWithHttpInfo(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/prompt/details/search.
     * Batch Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptDetails(promptQueryDTO: Array<PromptQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<PromptDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPromptDetails(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/prompt/search.
     * Batch Search Prompt Summaries
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptSummaryWithHttpInfo(promptQueryDTO: Array<PromptQueryDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<Array<PromptSummaryDTO>>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPromptSummaryWithHttpInfo(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch call shortcut for /api/v2/prompt/search.
     * Batch Search Prompt Summaries
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptSummary(promptQueryDTO: Array<PromptQueryDTO>, _options?: PromiseConfigurationOptions): Promise<Array<Array<PromptSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.batchSearchPromptSummary(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param promptId The referenced promptId
     */
    public clonePromptWithHttpInfo(promptId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.clonePromptWithHttpInfo(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param promptId The referenced promptId
     */
    public clonePrompt(promptId: number, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.clonePrompt(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param requestBody List of prompt information to be created
     */
    public clonePromptsWithHttpInfo(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.clonePromptsWithHttpInfo(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param requestBody List of prompt information to be created
     */
    public clonePrompts(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.clonePrompts(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPromptsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPromptsWithHttpInfo(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPrompts(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPrompts(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Public Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPublicPromptsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPublicPromptsWithHttpInfo(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Public Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPublicPrompts(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.countPublicPrompts(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param promptCreateDTO Information of the prompt to be created
     */
    public createPromptWithHttpInfo(promptCreateDTO: PromptCreateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPromptWithHttpInfo(promptCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param promptCreateDTO Information of the prompt to be created
     */
    public createPrompt(promptCreateDTO: PromptCreateDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPrompt(promptCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param promptCreateDTO List of prompt information to be created
     */
    public createPromptsWithHttpInfo(promptCreateDTO: Array<PromptCreateDTO>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPromptsWithHttpInfo(promptCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param promptCreateDTO List of prompt information to be created
     */
    public createPrompts(promptCreateDTO: Array<PromptCreateDTO>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPrompts(promptCreateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param promptId The promptId to be deleted
     */
    public deletePromptWithHttpInfo(promptId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePromptWithHttpInfo(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param promptId The promptId to be deleted
     */
    public deletePrompt(promptId: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePrompt(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete prompt by name. return the list of successfully deleted promptIds.
     * Delete Prompt by Name
     * @param name The prompt name to be deleted
     */
    public deletePromptByNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePromptByNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete prompt by name. return the list of successfully deleted promptIds.
     * Delete Prompt by Name
     * @param name The prompt name to be deleted
     */
    public deletePromptByName(name: string, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePromptByName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param requestBody List of promptIds to be deleted
     */
    public deletePromptsWithHttpInfo(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<number>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePromptsWithHttpInfo(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param requestBody List of promptIds to be deleted
     */
    public deletePrompts(requestBody: Array<number>, _options?: PromiseConfigurationOptions): Promise<Array<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePrompts(requestBody, observableOptions);
        return result.toPromise();
    }

    /**
     * Check if the prompt name already exists.
     * Check If Prompt Name Exists
     * @param name Name
     */
    public existsPromptNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.existsPromptNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Check if the prompt name already exists.
     * Check If Prompt Name Exists
     * @param name Name
     */
    public existsPromptName(name: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.existsPromptName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param promptId PromptId to be obtained
     */
    public getPromptDetailsWithHttpInfo(promptId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<PromptDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPromptDetailsWithHttpInfo(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param promptId PromptId to be obtained
     */
    public getPromptDetails(promptId: number, _options?: PromiseConfigurationOptions): Promise<PromptDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPromptDetails(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param promptId PromptId to be obtained
     */
    public getPromptSummaryWithHttpInfo(promptId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<PromptSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPromptSummaryWithHttpInfo(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param promptId PromptId to be obtained
     */
    public getPromptSummary(promptId: number, _options?: PromiseConfigurationOptions): Promise<PromptSummaryDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPromptSummary(promptId, observableOptions);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param name Prompt name
     */
    public listPromptVersionsByNameWithHttpInfo(name: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PromptItemForNameDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptVersionsByNameWithHttpInfo(name, observableOptions);
        return result.toPromise();
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param name Prompt name
     */
    public listPromptVersionsByName(name: string, _options?: PromiseConfigurationOptions): Promise<Array<PromptItemForNameDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listPromptVersionsByName(name, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a new prompt name starting with a desired name.
     * Create New Prompt Name
     * @param desired Desired name
     */
    public newPromptNameWithHttpInfo(desired: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.newPromptNameWithHttpInfo(desired, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a new prompt name starting with a desired name.
     * Create New Prompt Name
     * @param desired Desired name
     */
    public newPromptName(desired: string, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.newPromptName(desired, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param promptId The promptId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishPromptWithHttpInfo(promptId: number, visibility: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishPromptWithHttpInfo(promptId, visibility, observableOptions);
        return result.toPromise();
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param promptId The promptId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishPrompt(promptId: number, visibility: string, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.publishPrompt(promptId, visibility, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public searchPromptDetailsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PromptDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPromptDetailsWithHttpInfo(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Same as /api/v2/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public searchPromptDetails(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<PromptDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPromptDetails(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPromptSummaryWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PromptSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPromptSummaryWithHttpInfo(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPromptSummary(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<PromptSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPromptSummary(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Public Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPublicPromptSummaryWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<PromptSummaryDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPublicPromptSummaryWithHttpInfo(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: public(fixed).   - Username: exact match. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Public Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPublicPromptSummary(promptQueryDTO: PromptQueryDTO, _options?: PromiseConfigurationOptions): Promise<Array<PromptSummaryDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.searchPublicPromptSummary(promptQueryDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param promptAiParamDTO Call parameters
     */
    public sendPromptWithHttpInfo(promptAiParamDTO: PromptAiParamDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<LlmResultDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.sendPromptWithHttpInfo(promptAiParamDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param promptAiParamDTO Call parameters
     */
    public sendPrompt(promptAiParamDTO: PromptAiParamDTO, _options?: PromiseConfigurationOptions): Promise<LlmResultDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.sendPrompt(promptAiParamDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Refer to /api/v2/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param promptAiParamDTO Call parameters
     */
    public streamSendPromptWithHttpInfo(promptAiParamDTO: PromptAiParamDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<SseEmitter>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.streamSendPromptWithHttpInfo(promptAiParamDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Refer to /api/v2/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param promptAiParamDTO Call parameters
     */
    public streamSendPrompt(promptAiParamDTO: PromptAiParamDTO, _options?: PromiseConfigurationOptions): Promise<SseEmitter> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.streamSendPrompt(promptAiParamDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update prompt, refer to /api/v2/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param promptId The promptId to be updated
     * @param promptUpdateDTO The prompt information to be updated
     */
    public updatePromptWithHttpInfo(promptId: number, promptUpdateDTO: PromptUpdateDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updatePromptWithHttpInfo(promptId, promptUpdateDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update prompt, refer to /api/v2/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param promptId The promptId to be updated
     * @param promptUpdateDTO The prompt information to be updated
     */
    public updatePrompt(promptId: number, promptUpdateDTO: PromptUpdateDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updatePrompt(promptId, promptUpdateDTO, observableOptions);
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
    public createPromptTaskWithHttpInfo(promptTaskDTO: PromptTaskDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPromptTaskWithHttpInfo(promptTaskDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a prompt task.
     * Create Prompt Task
     * @param promptTaskDTO The prompt task to be added
     */
    public createPromptTask(promptTaskDTO: PromptTaskDTO, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createPromptTask(promptTaskDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param promptTaskId The promptTaskId to be deleted
     */
    public deletePromptTaskWithHttpInfo(promptTaskId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePromptTaskWithHttpInfo(promptTaskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param promptTaskId The promptTaskId to be deleted
     */
    public deletePromptTask(promptTaskId: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deletePromptTask(promptTaskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param promptTaskId The promptTaskId to be queried
     */
    public getPromptTaskWithHttpInfo(promptTaskId: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<PromptTaskDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPromptTaskWithHttpInfo(promptTaskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param promptTaskId The promptTaskId to be queried
     */
    public getPromptTask(promptTaskId: string, _options?: PromiseConfigurationOptions): Promise<PromptTaskDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getPromptTask(promptTaskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param promptTaskId The promptTaskId to be updated
     * @param promptTaskDTO The prompt task info to be updated
     */
    public updatePromptTaskWithHttpInfo(promptTaskId: string, promptTaskDTO: PromptTaskDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updatePromptTaskWithHttpInfo(promptTaskId, promptTaskDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param promptTaskId The promptTaskId to be updated
     * @param promptTaskDTO The prompt task info to be updated
     */
    public updatePromptTask(promptTaskId: string, promptTaskDTO: PromptTaskDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updatePromptTask(promptTaskId, promptTaskDTO, observableOptions);
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
    public cancelRagTaskWithHttpInfo(taskId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cancelRagTaskWithHttpInfo(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Cancel a RAG task.
     * Cancel RAG Task
     * @param taskId The taskId to be canceled
     */
    public cancelRagTask(taskId: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.cancelRagTask(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a RAG task.
     * Create RAG Task
     * @param characterUid The characterUid to be added a RAG task
     * @param ragTaskDTO The RAG task to be added
     */
    public createRagTaskWithHttpInfo(characterUid: string, ragTaskDTO: RagTaskDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<number>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createRagTaskWithHttpInfo(characterUid, ragTaskDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a RAG task.
     * Create RAG Task
     * @param characterUid The characterUid to be added a RAG task
     * @param ragTaskDTO The RAG task to be added
     */
    public createRagTask(characterUid: string, ragTaskDTO: RagTaskDTO, _options?: PromiseConfigurationOptions): Promise<number> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createRagTask(characterUid, ragTaskDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a RAG task.
     * Delete RAG Task
     * @param taskId The taskId to be deleted
     */
    public deleteRagTaskWithHttpInfo(taskId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteRagTaskWithHttpInfo(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a RAG task.
     * Delete RAG Task
     * @param taskId The taskId to be deleted
     */
    public deleteRagTask(taskId: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteRagTask(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the RAG task details.
     * Get RAG Task
     * @param taskId The taskId to be queried
     */
    public getRagTaskWithHttpInfo(taskId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<RagTaskDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRagTaskWithHttpInfo(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the RAG task details.
     * Get RAG Task
     * @param taskId The taskId to be queried
     */
    public getRagTask(taskId: number, _options?: PromiseConfigurationOptions): Promise<RagTaskDetailsDTO> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRagTask(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the RAG task execution status: pending | running | succeeded | failed | canceled.
     * Get RAG Task Status
     * @param taskId The taskId to be queried status
     */
    public getRagTaskStatusWithHttpInfo(taskId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRagTaskStatusWithHttpInfo(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Get the RAG task execution status: pending | running | succeeded | failed | canceled.
     * Get RAG Task Status
     * @param taskId The taskId to be queried status
     */
    public getRagTaskStatus(taskId: number, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.getRagTaskStatus(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * List the RAG tasks by characterId.
     * List RAG Tasks
     * @param characterUid The characterUid to be queried
     */
    public listRagTasksWithHttpInfo(characterUid: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<RagTaskDetailsDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listRagTasksWithHttpInfo(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * List the RAG tasks by characterId.
     * List RAG Tasks
     * @param characterUid The characterUid to be queried
     */
    public listRagTasks(characterUid: string, _options?: PromiseConfigurationOptions): Promise<Array<RagTaskDetailsDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listRagTasks(characterUid, observableOptions);
        return result.toPromise();
    }

    /**
     * Start a RAG task.
     * Start RAG Task
     * @param taskId The taskId to be started
     */
    public startRagTaskWithHttpInfo(taskId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.startRagTaskWithHttpInfo(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Start a RAG task.
     * Start RAG Task
     * @param taskId The taskId to be started
     */
    public startRagTask(taskId: number, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.startRagTask(taskId, observableOptions);
        return result.toPromise();
    }

    /**
     * Update a RAG task.
     * Update RAG Task
     * @param taskId The taskId to be updated
     * @param ragTaskDTO The prompt task info to be updated
     */
    public updateRagTaskWithHttpInfo(taskId: number, ragTaskDTO: RagTaskDTO, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateRagTaskWithHttpInfo(taskId, ragTaskDTO, observableOptions);
        return result.toPromise();
    }

    /**
     * Update a RAG task.
     * Update RAG Task
     * @param taskId The taskId to be updated
     * @param ragTaskDTO The prompt task info to be updated
     */
    public updateRagTask(taskId: number, ragTaskDTO: RagTaskDTO, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.updateRagTask(taskId, ragTaskDTO, observableOptions);
        return result.toPromise();
    }


}



import { ObservableTTSServiceApi } from './ObservableAPI.js';

import { TTSServiceApiRequestFactory, TTSServiceApiResponseProcessor} from "../apis/TTSServiceApi.js";
export class PromiseTTSServiceApi {
    private api: ObservableTTSServiceApi

    public constructor(
        configuration: Configuration,
        requestFactory?: TTSServiceApiRequestFactory,
        responseProcessor?: TTSServiceApiResponseProcessor
    ) {
        this.api = new ObservableTTSServiceApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Return builtin TTS speakers.
     * List Builtin Speakers
     */
    public listTtsBuiltinSpeakersWithHttpInfo(_options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<string>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTtsBuiltinSpeakersWithHttpInfo(observableOptions);
        return result.toPromise();
    }

    /**
     * Return builtin TTS speakers.
     * List Builtin Speakers
     */
    public listTtsBuiltinSpeakers(_options?: PromiseConfigurationOptions): Promise<Array<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTtsBuiltinSpeakers(observableOptions);
        return result.toPromise();
    }

    /**
     * Play TTS sample audio of the builtin/custom speaker.
     * Play Sample Audio
     * @param speakerType The speaker type
     * @param speaker The speaker
     */
    public playSampleWithHttpInfo(speakerType: string, speaker: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<any>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.playSampleWithHttpInfo(speakerType, speaker, observableOptions);
        return result.toPromise();
    }

    /**
     * Play TTS sample audio of the builtin/custom speaker.
     * Play Sample Audio
     * @param speakerType The speaker type
     * @param speaker The speaker
     */
    public playSample(speakerType: string, speaker: string, _options?: PromiseConfigurationOptions): Promise<any> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.playSample(speakerType, speaker, observableOptions);
        return result.toPromise();
    }

    /**
     * Read out the message.
     * Speak Message
     * @param messageId The message id
     */
    public speakMessageWithHttpInfo(messageId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<any>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.speakMessageWithHttpInfo(messageId, observableOptions);
        return result.toPromise();
    }

    /**
     * Read out the message.
     * Speak Message
     * @param messageId The message id
     */
    public speakMessage(messageId: number, _options?: PromiseConfigurationOptions): Promise<any> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.speakMessage(messageId, observableOptions);
        return result.toPromise();
    }


}



import { ObservableTagManagerForBizAdminApi } from './ObservableAPI.js';

import { TagManagerForBizAdminApiRequestFactory, TagManagerForBizAdminApiResponseProcessor} from "../apis/TagManagerForBizAdminApi.js";
export class PromiseTagManagerForBizAdminApi {
    private api: ObservableTagManagerForBizAdminApi

    public constructor(
        configuration: Configuration,
        requestFactory?: TagManagerForBizAdminApiRequestFactory,
        responseProcessor?: TagManagerForBizAdminApiResponseProcessor
    ) {
        this.api = new ObservableTagManagerForBizAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Create a tag, tags created by the administrator cannot be deleted by ordinary users.
     * Create Tag
     * @param referType Tag type (prompt, agent, plugin...)
     * @param referId Resource identifier of the tag
     * @param tag Tag content
     */
    public createTagWithHttpInfo(referType: string, referId: string, tag: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createTagWithHttpInfo(referType, referId, tag, observableOptions);
        return result.toPromise();
    }

    /**
     * Create a tag, tags created by the administrator cannot be deleted by ordinary users.
     * Create Tag
     * @param referType Tag type (prompt, agent, plugin...)
     * @param referId Resource identifier of the tag
     * @param tag Tag content
     */
    public createTag(referType: string, referId: string, tag: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.createTag(referType, referId, tag, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a tag, any tag created by anyone can be deleted.
     * Delete Tag
     * @param referType Tag type (prompt, agent, plugin...)
     * @param referId Resource identifier of the tag
     * @param tag Tag content
     */
    public deleteTagWithHttpInfo(referType: string, referId: string, tag: string, _options?: PromiseConfigurationOptions): Promise<HttpInfo<boolean>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteTagWithHttpInfo(referType, referId, tag, observableOptions);
        return result.toPromise();
    }

    /**
     * Delete a tag, any tag created by anyone can be deleted.
     * Delete Tag
     * @param referType Tag type (prompt, agent, plugin...)
     * @param referId Resource identifier of the tag
     * @param tag Tag content
     */
    public deleteTag(referType: string, referId: string, tag: string, _options?: PromiseConfigurationOptions): Promise<boolean> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.deleteTag(referType, referId, tag, observableOptions);
        return result.toPromise();
    }


}



import { ObservableTelegramManagerForAdminApi } from './ObservableAPI.js';

import { TelegramManagerForAdminApiRequestFactory, TelegramManagerForAdminApiResponseProcessor} from "../apis/TelegramManagerForAdminApi.js";
export class PromiseTelegramManagerForAdminApi {
    private api: ObservableTelegramManagerForAdminApi

    public constructor(
        configuration: Configuration,
        requestFactory?: TelegramManagerForAdminApiRequestFactory,
        responseProcessor?: TelegramManagerForAdminApiResponseProcessor
    ) {
        this.api = new ObservableTelegramManagerForAdminApi(configuration, requestFactory, responseProcessor);
    }

    /**
     * Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.
     * Find Telegram Chat
     * @param backendId Character backend identifier
     * @param tgChatId Telegram chat id
     */
    public findTelegramChatWithHttpInfo(backendId: string, tgChatId: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<string>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.findTelegramChatWithHttpInfo(backendId, tgChatId, observableOptions);
        return result.toPromise();
    }

    /**
     * Look up the FreeChat chat_id bound to a Telegram (backend, tg_chat_id) pair.
     * Find Telegram Chat
     * @param backendId Character backend identifier
     * @param tgChatId Telegram chat id
     */
    public findTelegramChat(backendId: string, tgChatId: number, _options?: PromiseConfigurationOptions): Promise<string> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.findTelegramChat(backendId, tgChatId, observableOptions);
        return result.toPromise();
    }

    /**
     * List Telegram messages recorded against the given tg_chat.chat_id, newest first.
     * List Telegram Messages
     * @param chatId tg_chat.chat_id
     * @param [limit] Max rows to return (default 100)
     * @param [offset] Row offset (default 0)
     */
    public listTelegramMessagesWithHttpInfo(chatId: string, limit?: number, offset?: number, _options?: PromiseConfigurationOptions): Promise<HttpInfo<Array<TgMessageDTO>>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTelegramMessagesWithHttpInfo(chatId, limit, offset, observableOptions);
        return result.toPromise();
    }

    /**
     * List Telegram messages recorded against the given tg_chat.chat_id, newest first.
     * List Telegram Messages
     * @param chatId tg_chat.chat_id
     * @param [limit] Max rows to return (default 100)
     * @param [offset] Row offset (default 0)
     */
    public listTelegramMessages(chatId: string, limit?: number, offset?: number, _options?: PromiseConfigurationOptions): Promise<Array<TgMessageDTO>> {
        const observableOptions = wrapOptions(_options);
        const result = this.api.listTelegramMessages(chatId, limit, offset, observableOptions);
        return result.toPromise();
    }


}



