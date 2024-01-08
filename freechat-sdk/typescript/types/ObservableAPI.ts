import { ResponseContext, RequestContext, HttpFile, HttpInfo } from '../http/http.js';
import { Configuration} from '../configuration.js'
import { Observable, of, from } from '../rxjsStub.js';
import {mergeMap, map} from  '../rxjsStub.js';
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

import { AIServiceApiRequestFactory, AIServiceApiResponseProcessor} from "../apis/AIServiceApi.js";
export class ObservableAIServiceApi {
    private requestFactory: AIServiceApiRequestFactory;
    private responseProcessor: AIServiceApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: AIServiceApiRequestFactory,
        responseProcessor?: AIServiceApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new AIServiceApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new AIServiceApiResponseProcessor();
    }

    /**
     * Add a credential for the model service.
     * Add Model Provider Credential
     * @param aiApiKeyCreateDTO Model call credential information
     */
    public addAiApiKeyWithHttpInfo(aiApiKeyCreateDTO: AiApiKeyCreateDTO, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.addAiApiKey(aiApiKeyCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.addAiApiKeyWithHttpInfo(rsp)));
            }));
    }

    /**
     * Add a credential for the model service.
     * Add Model Provider Credential
     * @param aiApiKeyCreateDTO Model call credential information
     */
    public addAiApiKey(aiApiKeyCreateDTO: AiApiKeyCreateDTO, _options?: Configuration): Observable<number> {
        return this.addAiApiKeyWithHttpInfo(aiApiKeyCreateDTO, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param id Credential identifier
     */
    public deleteAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deleteAiApiKey(id, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteAiApiKeyWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete the credential information of the model provider.
     * Delete Credential of Model Provider
     * @param id Credential identifier
     */
    public deleteAiApiKey(id: number, _options?: Configuration): Observable<boolean> {
        return this.deleteAiApiKeyWithHttpInfo(id, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param id Credential identifier
     */
    public disableAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.disableAiApiKey(id, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.disableAiApiKeyWithHttpInfo(rsp)));
            }));
    }

    /**
     * Disable the credential information of the model provider.
     * Disable Model Provider Credential
     * @param id Credential identifier
     */
    public disableAiApiKey(id: number, _options?: Configuration): Observable<boolean> {
        return this.disableAiApiKeyWithHttpInfo(id, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param id Credential identifier
     */
    public enableAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.enableAiApiKey(id, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.enableAiApiKeyWithHttpInfo(rsp)));
            }));
    }

    /**
     * Enable the credential information of the model provider.
     * Enable Model Provider Credential
     * @param id Credential identifier
     */
    public enableAiApiKey(id: number, _options?: Configuration): Observable<boolean> {
        return this.enableAiApiKeyWithHttpInfo(id, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param id Credential identifier
     */
    public getAiApiKeyWithHttpInfo(id: number, _options?: Configuration): Observable<HttpInfo<AiApiKeyInfoDTO>> {
        const requestContextPromise = this.requestFactory.getAiApiKey(id, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAiApiKeyWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the credential information of the model provider.
     * Get credential of Model Provider
     * @param id Credential identifier
     */
    public getAiApiKey(id: number, _options?: Configuration): Observable<AiApiKeyInfoDTO> {
        return this.getAiApiKeyWithHttpInfo(id, _options).pipe(map((apiResponse: HttpInfo<AiApiKeyInfoDTO>) => apiResponse.data));
    }

    /**
     * Return specific model information.
     * Get Model Information
     * @param modelId Model identifier
     */
    public getAiModelInfoWithHttpInfo(modelId: string, _options?: Configuration): Observable<HttpInfo<AiModelInfoDTO>> {
        const requestContextPromise = this.requestFactory.getAiModelInfo(modelId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAiModelInfoWithHttpInfo(rsp)));
            }));
    }

    /**
     * Return specific model information.
     * Get Model Information
     * @param modelId Model identifier
     */
    public getAiModelInfo(modelId: string, _options?: Configuration): Observable<AiModelInfoDTO> {
        return this.getAiModelInfoWithHttpInfo(modelId, _options).pipe(map((apiResponse: HttpInfo<AiModelInfoDTO>) => apiResponse.data));
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param provider Model provider
     */
    public listAiApiKeysWithHttpInfo(provider: string, _options?: Configuration): Observable<HttpInfo<Array<AiApiKeyInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listAiApiKeys(provider, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listAiApiKeysWithHttpInfo(rsp)));
            }));
    }

    /**
     * List all credential information of the model provider.
     * List Credentials of Model Provider
     * @param provider Model provider
     */
    public listAiApiKeys(provider: string, _options?: Configuration): Observable<Array<AiApiKeyInfoDTO>> {
        return this.listAiApiKeysWithHttpInfo(provider, _options).pipe(map((apiResponse: HttpInfo<Array<AiApiKeyInfoDTO>>) => apiResponse.data));
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     */
    public listAiModelInfoWithHttpInfo(pageSize: number, _options?: Configuration): Observable<HttpInfo<Array<AiModelInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listAiModelInfo(pageSize, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listAiModelInfoWithHttpInfo(rsp)));
            }));
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     */
    public listAiModelInfo(pageSize: number, _options?: Configuration): Observable<Array<AiModelInfoDTO>> {
        return this.listAiModelInfoWithHttpInfo(pageSize, _options).pipe(map((apiResponse: HttpInfo<Array<AiModelInfoDTO>>) => apiResponse.data));
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     */
    public listAiModelInfo1WithHttpInfo(_options?: Configuration): Observable<HttpInfo<Array<AiModelInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listAiModelInfo1(_options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listAiModelInfo1WithHttpInfo(rsp)));
            }));
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     */
    public listAiModelInfo1(_options?: Configuration): Observable<Array<AiModelInfoDTO>> {
        return this.listAiModelInfo1WithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<Array<AiModelInfoDTO>>) => apiResponse.data));
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listAiModelInfo2WithHttpInfo(pageSize: number, pageNum: number, _options?: Configuration): Observable<HttpInfo<Array<AiModelInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listAiModelInfo2(pageSize, pageNum, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listAiModelInfo2WithHttpInfo(rsp)));
            }));
    }

    /**
     * Return model information by page, return the pageNum page, up to pageSize model information.
     * List Models
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listAiModelInfo2(pageSize: number, pageNum: number, _options?: Configuration): Observable<Array<AiModelInfoDTO>> {
        return this.listAiModelInfo2WithHttpInfo(pageSize, pageNum, _options).pipe(map((apiResponse: HttpInfo<Array<AiModelInfoDTO>>) => apiResponse.data));
    }

}

import { AccountApiRequestFactory, AccountApiResponseProcessor} from "../apis/AccountApi.js";
export class ObservableAccountApi {
    private requestFactory: AccountApiRequestFactory;
    private responseProcessor: AccountApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: AccountApiRequestFactory,
        responseProcessor?: AccountApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new AccountApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new AccountApiResponseProcessor();
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     */
    public createTokenWithHttpInfo(_options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createToken(_options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createTokenWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     */
    public createToken(_options?: Configuration): Observable<string> {
        return this.createTokenWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param duration Token validity duration (seconds)
     */
    public createToken1WithHttpInfo(duration: number, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createToken1(duration, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createToken1WithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a timed API Token, valid for {duration} seconds.
     * Create API Token
     * @param duration Token validity duration (seconds)
     */
    public createToken1(duration: number, _options?: Configuration): Observable<string> {
        return this.createToken1WithHttpInfo(duration, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param token Token content
     */
    public deleteTokenWithHttpInfo(token: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.deleteToken(token, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteTokenWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete an API Token.
     * Delete API Token
     * @param token Token content
     */
    public deleteToken(token: string, _options?: Configuration): Observable<string> {
        return this.deleteTokenWithHttpInfo(token, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param id Token id
     */
    public deleteTokenByIdWithHttpInfo(id: number, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deleteTokenById(id, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteTokenByIdWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete the API token by id.
     * Delete API Token by Id
     * @param id Token id
     */
    public deleteTokenById(id: number, _options?: Configuration): Observable<boolean> {
        return this.deleteTokenByIdWithHttpInfo(id, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param token Token content
     */
    public disableTokenWithHttpInfo(token: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.disableToken(token, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.disableTokenWithHttpInfo(rsp)));
            }));
    }

    /**
     * Disable an API Token, the token is not deleted.
     * Disable API Token
     * @param token Token content
     */
    public disableToken(token: string, _options?: Configuration): Observable<string> {
        return this.disableTokenWithHttpInfo(token, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param id Token id
     */
    public disableTokenByIdWithHttpInfo(id: number, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.disableTokenById(id, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.disableTokenByIdWithHttpInfo(rsp)));
            }));
    }

    /**
     * Disable the API token by id.
     * Disable API Token by Id
     * @param id Token id
     */
    public disableTokenById(id: number, _options?: Configuration): Observable<boolean> {
        return this.disableTokenByIdWithHttpInfo(id, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param id Token id
     */
    public getTokenByIdWithHttpInfo(id: number, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.getTokenById(id, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getTokenByIdWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the API token by id.
     * Get API Token by Id
     * @param id Token id
     */
    public getTokenById(id: number, _options?: Configuration): Observable<string> {
        return this.getTokenByIdWithHttpInfo(id, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param username Username
     */
    public getUserBasicWithHttpInfo(username: string, _options?: Configuration): Observable<HttpInfo<UserBasicInfoDTO>> {
        const requestContextPromise = this.requestFactory.getUserBasic(username, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getUserBasicWithHttpInfo(rsp)));
            }));
    }

    /**
     * Return user basic information, including: username, nickname, avatar link.
     * Get User Basic Information
     * @param username Username
     */
    public getUserBasic(username: string, _options?: Configuration): Observable<UserBasicInfoDTO> {
        return this.getUserBasicWithHttpInfo(username, _options).pipe(map((apiResponse: HttpInfo<UserBasicInfoDTO>) => apiResponse.data));
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     */
    public getUserDetailsWithHttpInfo(_options?: Configuration): Observable<HttpInfo<UserDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getUserDetails(_options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getUserDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Return the detailed user information of the current account, the fields refer to the [standard claims](https://openid.net/specs/openid-connect-core-1_0.html#Claims) of OpenID Connect (OIDC).
     * Get User Details
     */
    public getUserDetails(_options?: Configuration): Observable<UserDetailsDTO> {
        return this.getUserDetailsWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<UserDetailsDTO>) => apiResponse.data));
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     */
    public listTokensWithHttpInfo(_options?: Configuration): Observable<HttpInfo<Array<ApiTokenInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listTokens(_options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listTokensWithHttpInfo(rsp)));
            }));
    }

    /**
     * List currently valid tokens.
     * List API Tokens
     */
    public listTokens(_options?: Configuration): Observable<Array<ApiTokenInfoDTO>> {
        return this.listTokensWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<Array<ApiTokenInfoDTO>>) => apiResponse.data));
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param userDetailsDTO User information
     */
    public updateUserInfoWithHttpInfo(userDetailsDTO: UserDetailsDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateUserInfo(userDetailsDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateUserInfoWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update the detailed user information of the current account.
     * Update User Details
     * @param userDetailsDTO User information
     */
    public updateUserInfo(userDetailsDTO: UserDetailsDTO, _options?: Configuration): Observable<boolean> {
        return this.updateUserInfoWithHttpInfo(userDetailsDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param file User picture
     */
    public uploadUserPictureWithHttpInfo(file: HttpFile, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.uploadUserPicture(file, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.uploadUserPictureWithHttpInfo(rsp)));
            }));
    }

    /**
     * Upload a picture of the user.
     * Upload User Picture
     * @param file User picture
     */
    public uploadUserPicture(file: HttpFile, _options?: Configuration): Observable<string> {
        return this.uploadUserPictureWithHttpInfo(file, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

}

import { AccountManagerForAdminApiRequestFactory, AccountManagerForAdminApiResponseProcessor} from "../apis/AccountManagerForAdminApi.js";
export class ObservableAccountManagerForAdminApi {
    private requestFactory: AccountManagerForAdminApiRequestFactory;
    private responseProcessor: AccountManagerForAdminApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: AccountManagerForAdminApiRequestFactory,
        responseProcessor?: AccountManagerForAdminApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new AccountManagerForAdminApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new AccountManagerForAdminApiResponseProcessor();
    }

    /**
     * Create an API Token for a specified user, valid for duration seconds.
     * Create API Token for User.
     * @param username Username
     * @param duration Validity period (seconds)
     */
    public createTokenForUserWithHttpInfo(username: string, duration: number, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createTokenForUser(username, duration, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createTokenForUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create an API Token for a specified user, valid for duration seconds.
     * Create API Token for User.
     * @param username Username
     * @param duration Validity period (seconds)
     */
    public createTokenForUser(username: string, duration: number, _options?: Configuration): Observable<string> {
        return this.createTokenForUserWithHttpInfo(username, duration, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Create user.
     * Create User
     * @param userFullDetailsDTO User information
     */
    public createUserWithHttpInfo(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.createUser(userFullDetailsDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create user.
     * Create User
     * @param userFullDetailsDTO User information
     */
    public createUser(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Observable<boolean> {
        return this.createUserWithHttpInfo(userFullDetailsDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param token API Token
     */
    public deleteTokenForUserWithHttpInfo(token: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deleteTokenForUser(token, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteTokenForUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete the specified API Token.
     * Delete API Token
     * @param token API Token
     */
    public deleteTokenForUser(token: string, _options?: Configuration): Observable<boolean> {
        return this.deleteTokenForUserWithHttpInfo(token, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Delete user by username.
     * Delete User
     * @param username Username
     */
    public deleteUserWithHttpInfo(username: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deleteUser(username, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete user by username.
     * Delete User
     * @param username Username
     */
    public deleteUser(username: string, _options?: Configuration): Observable<boolean> {
        return this.deleteUserWithHttpInfo(username, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param token API Token
     */
    public disableTokenForUserWithHttpInfo(token: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.disableTokenForUser(token, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.disableTokenForUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Disable the specified API Token.
     * Disable API Token
     * @param token API Token
     */
    public disableTokenForUser(token: string, _options?: Configuration): Observable<boolean> {
        return this.disableTokenForUserWithHttpInfo(token, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param username Username
     */
    public getDetailsOfUserWithHttpInfo(username: string, _options?: Configuration): Observable<HttpInfo<UserDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getDetailsOfUser(username, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getDetailsOfUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Return detailed user information.
     * Get User Details
     * @param username Username
     */
    public getDetailsOfUser(username: string, _options?: Configuration): Observable<UserDetailsDTO> {
        return this.getDetailsOfUserWithHttpInfo(username, _options).pipe(map((apiResponse: HttpInfo<UserDetailsDTO>) => apiResponse.data));
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param token API Token
     */
    public getUserByTokenWithHttpInfo(token: string, _options?: Configuration): Observable<HttpInfo<UserDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getUserByToken(token, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getUserByTokenWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the detailed user information corresponding to the API Token.
     * Get User by API Token
     * @param token API Token
     */
    public getUserByToken(token: string, _options?: Configuration): Observable<UserDetailsDTO> {
        return this.getUserByTokenWithHttpInfo(token, _options).pipe(map((apiResponse: HttpInfo<UserDetailsDTO>) => apiResponse.data));
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param username Username
     */
    public listAuthoritiesOfUserWithHttpInfo(username: string, _options?: Configuration): Observable<HttpInfo<Set<string>>> {
        const requestContextPromise = this.requestFactory.listAuthoritiesOfUser(username, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listAuthoritiesOfUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * List the user\'s permissions.
     * List User Permissions
     * @param username Username
     */
    public listAuthoritiesOfUser(username: string, _options?: Configuration): Observable<Set<string>> {
        return this.listAuthoritiesOfUserWithHttpInfo(username, _options).pipe(map((apiResponse: HttpInfo<Set<string>>) => apiResponse.data));
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param username Username
     */
    public listTokensOfUserWithHttpInfo(username: string, _options?: Configuration): Observable<HttpInfo<Array<ApiTokenInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listTokensOfUser(username, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listTokensOfUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the list of API Tokens of the user.
     * Get API Token of User
     * @param username Username
     */
    public listTokensOfUser(username: string, _options?: Configuration): Observable<Array<ApiTokenInfoDTO>> {
        return this.listTokensOfUserWithHttpInfo(username, _options).pipe(map((apiResponse: HttpInfo<Array<ApiTokenInfoDTO>>) => apiResponse.data));
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listUsersWithHttpInfo(pageSize: number, pageNum: number, _options?: Configuration): Observable<HttpInfo<Array<UserBasicInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listUsers(pageSize, pageNum, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listUsersWithHttpInfo(rsp)));
            }));
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     */
    public listUsers(pageSize: number, pageNum: number, _options?: Configuration): Observable<Array<UserBasicInfoDTO>> {
        return this.listUsersWithHttpInfo(pageSize, pageNum, _options).pipe(map((apiResponse: HttpInfo<Array<UserBasicInfoDTO>>) => apiResponse.data));
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     */
    public listUsers1WithHttpInfo(pageSize: number, _options?: Configuration): Observable<HttpInfo<Array<UserBasicInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listUsers1(pageSize, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listUsers1WithHttpInfo(rsp)));
            }));
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     * @param pageSize Maximum quantity
     */
    public listUsers1(pageSize: number, _options?: Configuration): Observable<Array<UserBasicInfoDTO>> {
        return this.listUsers1WithHttpInfo(pageSize, _options).pipe(map((apiResponse: HttpInfo<Array<UserBasicInfoDTO>>) => apiResponse.data));
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     */
    public listUsers2WithHttpInfo(_options?: Configuration): Observable<HttpInfo<Array<UserBasicInfoDTO>>> {
        const requestContextPromise = this.requestFactory.listUsers2(_options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listUsers2WithHttpInfo(rsp)));
            }));
    }

    /**
     * Return user information by page, return the pageNum page, up to pageSize user information.
     * List User Information
     */
    public listUsers2(_options?: Configuration): Observable<Array<UserBasicInfoDTO>> {
        return this.listUsers2WithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<Array<UserBasicInfoDTO>>) => apiResponse.data));
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateAuthoritiesOfUserWithHttpInfo(username: string, requestBody: Set<string>, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateAuthoritiesOfUser(username, requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateAuthoritiesOfUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update the user\'s permission list.
     * Update User Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateAuthoritiesOfUser(username: string, requestBody: Set<string>, _options?: Configuration): Observable<boolean> {
        return this.updateAuthoritiesOfUserWithHttpInfo(username, requestBody, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Update user information.
     * Update User
     * @param userFullDetailsDTO User information
     */
    public updateUserWithHttpInfo(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateUser(userFullDetailsDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateUserWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update user information.
     * Update User
     * @param userFullDetailsDTO User information
     */
    public updateUser(userFullDetailsDTO: UserFullDetailsDTO, _options?: Configuration): Observable<boolean> {
        return this.updateUserWithHttpInfo(userFullDetailsDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

}

import { AppConfigForAdminApiRequestFactory, AppConfigForAdminApiResponseProcessor} from "../apis/AppConfigForAdminApi.js";
export class ObservableAppConfigForAdminApi {
    private requestFactory: AppConfigForAdminApiRequestFactory;
    private responseProcessor: AppConfigForAdminApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: AppConfigForAdminApiRequestFactory,
        responseProcessor?: AppConfigForAdminApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new AppConfigForAdminApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new AppConfigForAdminApiResponseProcessor();
    }

    /**
     * Get the latest configuration information of the application by name.
     * Get Configuration
     * @param name Configuration name
     */
    public getAppConfigWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<AppConfigInfoDTO>> {
        const requestContextPromise = this.requestFactory.getAppConfig(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAppConfigWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the latest configuration information of the application by name.
     * Get Configuration
     * @param name Configuration name
     */
    public getAppConfig(name: string, _options?: Configuration): Observable<AppConfigInfoDTO> {
        return this.getAppConfigWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<AppConfigInfoDTO>) => apiResponse.data));
    }

    /**
     * Get the configuration information of the application by name and version.
     * Get Specified Version of Configuration
     * @param name Configuration name
     * @param version Configuration version
     */
    public getAppConfigByVersionWithHttpInfo(name: string, version: number, _options?: Configuration): Observable<HttpInfo<AppConfigInfoDTO>> {
        const requestContextPromise = this.requestFactory.getAppConfigByVersion(name, version, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAppConfigByVersionWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the configuration information of the application by name and version.
     * Get Specified Version of Configuration
     * @param name Configuration name
     * @param version Configuration version
     */
    public getAppConfigByVersion(name: string, version: number, _options?: Configuration): Observable<AppConfigInfoDTO> {
        return this.getAppConfigByVersionWithHttpInfo(name, version, _options).pipe(map((apiResponse: HttpInfo<AppConfigInfoDTO>) => apiResponse.data));
    }

    /**
     * List all application configuration names.
     * List Configuration Names
     */
    public listAppConfigNamesWithHttpInfo(_options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.listAppConfigNames(_options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listAppConfigNamesWithHttpInfo(rsp)));
            }));
    }

    /**
     * List all application configuration names.
     * List Configuration Names
     */
    public listAppConfigNames(_options?: Configuration): Observable<Array<string>> {
        return this.listAppConfigNamesWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Publish application configuration, return configuration version.
     * Publish Configuration
     * @param appConfigCreateDTO Configuration information
     */
    public publishAppConfigWithHttpInfo(appConfigCreateDTO: AppConfigCreateDTO, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.publishAppConfig(appConfigCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.publishAppConfigWithHttpInfo(rsp)));
            }));
    }

    /**
     * Publish application configuration, return configuration version.
     * Publish Configuration
     * @param appConfigCreateDTO Configuration information
     */
    public publishAppConfig(appConfigCreateDTO: AppConfigCreateDTO, _options?: Configuration): Observable<number> {
        return this.publishAppConfigWithHttpInfo(appConfigCreateDTO, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

}

import { AppMetaForAdminApiRequestFactory, AppMetaForAdminApiResponseProcessor} from "../apis/AppMetaForAdminApi.js";
export class ObservableAppMetaForAdminApi {
    private requestFactory: AppMetaForAdminApiRequestFactory;
    private responseProcessor: AppMetaForAdminApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: AppMetaForAdminApiRequestFactory,
        responseProcessor?: AppMetaForAdminApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new AppMetaForAdminApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new AppMetaForAdminApiResponseProcessor();
    }

    /**
     * This method does nothing.
     * Expose DTO definitions
     * @param openAiParam 
     * @param qwenParam 
     * @param aiForPromptResult 
     */
    public exposeWithHttpInfo(openAiParam: OpenAiParamDTO, qwenParam: QwenParamDTO, aiForPromptResult: LlmResultDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.expose(openAiParam, qwenParam, aiForPromptResult, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.exposeWithHttpInfo(rsp)));
            }));
    }

    /**
     * This method does nothing.
     * Expose DTO definitions
     * @param openAiParam 
     * @param qwenParam 
     * @param aiForPromptResult 
     */
    public expose(openAiParam: OpenAiParamDTO, qwenParam: QwenParamDTO, aiForPromptResult: LlmResultDTO, _options?: Configuration): Observable<string> {
        return this.exposeWithHttpInfo(openAiParam, qwenParam, aiForPromptResult, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     */
    public getAppMetaWithHttpInfo(_options?: Configuration): Observable<HttpInfo<AppMetaDTO>> {
        const requestContextPromise = this.requestFactory.getAppMeta(_options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getAppMetaWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get application information to accurately locate the corresponding project version.
     * Get Application Information
     */
    public getAppMeta(_options?: Configuration): Observable<AppMetaDTO> {
        return this.getAppMetaWithHttpInfo(_options).pipe(map((apiResponse: HttpInfo<AppMetaDTO>) => apiResponse.data));
    }

}

import { CharacterApiRequestFactory, CharacterApiResponseProcessor} from "../apis/CharacterApi.js";
export class ObservableCharacterApi {
    private requestFactory: CharacterApiRequestFactory;
    private responseProcessor: CharacterApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: CharacterApiRequestFactory,
        responseProcessor?: CharacterApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new CharacterApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new CharacterApiResponseProcessor();
    }

    /**
     * Add a backend configuration for a character.
     * Add Character Backend
     * @param characterId The characterId to be added a backend
     * @param characterBackendDTO The character backend to be added
     */
    public addCharacterBackendWithHttpInfo(characterId: string, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.addCharacterBackend(characterId, characterBackendDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.addCharacterBackendWithHttpInfo(rsp)));
            }));
    }

    /**
     * Add a backend configuration for a character.
     * Add Character Backend
     * @param characterId The characterId to be added a backend
     * @param characterBackendDTO The character backend to be added
     */
    public addCharacterBackend(characterId: string, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Observable<string> {
        return this.addCharacterBackendWithHttpInfo(characterId, characterBackendDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Batch call shortcut for /api/v1/character/details/search.
     * Batch Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterDetailsWithHttpInfo(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<CharacterDetailsDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchCharacterDetails(characterQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchCharacterDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/character/details/search.
     * Batch Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterDetails(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Observable<Array<Array<CharacterDetailsDTO>>> {
        return this.batchSearchCharacterDetailsWithHttpInfo(characterQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<CharacterDetailsDTO>>>) => apiResponse.data));
    }

    /**
     * Batch call shortcut for /api/v1/character/search.
     * Batch Search Character Summaries
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterSummaryWithHttpInfo(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<CharacterSummaryDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchCharacterSummary(characterQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchCharacterSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/character/search.
     * Batch Search Character Summaries
     * @param characterQueryDTO Query conditions
     */
    public batchSearchCharacterSummary(characterQueryDTO: Array<CharacterQueryDTO>, _options?: Configuration): Observable<Array<Array<CharacterSummaryDTO>>> {
        return this.batchSearchCharacterSummaryWithHttpInfo(characterQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<CharacterSummaryDTO>>>) => apiResponse.data));
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param characterId The referenced characterId
     */
    public cloneCharacterWithHttpInfo(characterId: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.cloneCharacter(characterId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.cloneCharacterWithHttpInfo(rsp)));
            }));
    }

    /**
     * Enter the characterId, generate a new record, the content is basically the same as the original character, but the following fields are different: - Version number is 1 - Visibility is private - The parent character is the source characterId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new characterId. 
     * Clone Character
     * @param characterId The referenced characterId
     */
    public cloneCharacter(characterId: string, _options?: Configuration): Observable<string> {
        return this.cloneCharacterWithHttpInfo(characterId, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param characterQueryDTO Query conditions
     */
    public countCharactersWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.countCharacters(characterQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.countCharactersWithHttpInfo(rsp)));
            }));
    }

    /**
     * Calculate the number of characters according to the specified query conditions.
     * Calculate Number of Characters
     * @param characterQueryDTO Query conditions
     */
    public countCharacters(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Observable<number> {
        return this.countCharactersWithHttpInfo(characterQueryDTO, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Create a character.
     * Create Character
     * @param characterCreateDTO Information of the character to be created
     */
    public createCharacterWithHttpInfo(characterCreateDTO: CharacterCreateDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createCharacter(characterCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createCharacterWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a character.
     * Create Character
     * @param characterCreateDTO Information of the character to be created
     */
    public createCharacter(characterCreateDTO: CharacterCreateDTO, _options?: Configuration): Observable<string> {
        return this.createCharacterWithHttpInfo(characterCreateDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param characterId The characterId to be deleted
     */
    public deleteCharacterWithHttpInfo(characterId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deleteCharacter(characterId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteCharacterWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete character. Returns success or failure.
     * Delete Character
     * @param characterId The characterId to be deleted
     */
    public deleteCharacter(characterId: string, _options?: Configuration): Observable<boolean> {
        return this.deleteCharacterWithHttpInfo(characterId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Delete character by name. return the list of successfully deleted characterIds.
     * Delete Character by Name
     * @param name The character name to be deleted
     */
    public deleteCharacterByNameWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.deleteCharacterByName(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteCharacterByNameWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete character by name. return the list of successfully deleted characterIds.
     * Delete Character by Name
     * @param name The character name to be deleted
     */
    public deleteCharacterByName(name: string, _options?: Configuration): Observable<Array<string>> {
        return this.deleteCharacterByNameWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param chatId Chat session identifier
     */
    public deleteChatWithHttpInfo(chatId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deleteChat(chatId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteChatWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete the chat session.
     * Delete Chat Session
     * @param chatId Chat session identifier
     */
    public deleteChat(chatId: string, _options?: Configuration): Observable<boolean> {
        return this.deleteChatWithHttpInfo(chatId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param characterId CharacterId to be obtained
     */
    public getCharacterDetailsWithHttpInfo(characterId: string, _options?: Configuration): Observable<HttpInfo<CharacterDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getCharacterDetails(characterId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getCharacterDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get character detailed information.
     * Get Character Details
     * @param characterId CharacterId to be obtained
     */
    public getCharacterDetails(characterId: string, _options?: Configuration): Observable<CharacterDetailsDTO> {
        return this.getCharacterDetailsWithHttpInfo(characterId, _options).pipe(map((apiResponse: HttpInfo<CharacterDetailsDTO>) => apiResponse.data));
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param name Character name
     */
    public getCharacterLatestIdByNameWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.getCharacterLatestIdByName(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getCharacterLatestIdByNameWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get latest characterId by character name.
     * Get Latest Character Id by Name
     * @param name Character name
     */
    public getCharacterLatestIdByName(name: string, _options?: Configuration): Observable<string> {
        return this.getCharacterLatestIdByNameWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param characterId CharacterId to be obtained
     */
    public getCharacterSummaryWithHttpInfo(characterId: string, _options?: Configuration): Observable<HttpInfo<CharacterSummaryDTO>> {
        const requestContextPromise = this.requestFactory.getCharacterSummary(characterId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getCharacterSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get character summary information.
     * Get Character Summary
     * @param characterId CharacterId to be obtained
     */
    public getCharacterSummary(characterId: string, _options?: Configuration): Observable<CharacterSummaryDTO> {
        return this.getCharacterSummaryWithHttpInfo(characterId, _options).pipe(map((apiResponse: HttpInfo<CharacterSummaryDTO>) => apiResponse.data));
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param characterId The characterId to be queried
     */
    public getDefaultCharacterBackendWithHttpInfo(characterId: string, _options?: Configuration): Observable<HttpInfo<CharacterBackendDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getDefaultCharacterBackend(characterId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getDefaultCharacterBackendWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the default backend configuration.
     * Get Default Character Backend
     * @param characterId The characterId to be queried
     */
    public getDefaultCharacterBackend(characterId: string, _options?: Configuration): Observable<CharacterBackendDetailsDTO> {
        return this.getDefaultCharacterBackendWithHttpInfo(characterId, _options).pipe(map((apiResponse: HttpInfo<CharacterBackendDetailsDTO>) => apiResponse.data));
    }

    /**
     * List Character Backend identifiers.
     * List Character Backend ids
     * @param characterId The characterId to be queried
     */
    public listCharacterBackendIdsWithHttpInfo(characterId: string, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.listCharacterBackendIds(characterId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listCharacterBackendIdsWithHttpInfo(rsp)));
            }));
    }

    /**
     * List Character Backend identifiers.
     * List Character Backend ids
     * @param characterId The characterId to be queried
     */
    public listCharacterBackendIds(characterId: string, _options?: Configuration): Observable<Array<string>> {
        return this.listCharacterBackendIdsWithHttpInfo(characterId, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param name Character name
     */
    public listCharacterVersionsByNameWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<Array<CharacterItemForNameDTO>>> {
        const requestContextPromise = this.requestFactory.listCharacterVersionsByName(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listCharacterVersionsByNameWithHttpInfo(rsp)));
            }));
    }

    /**
     * List the versions and corresponding characterIds by character name.
     * List Versions by Character Name
     * @param name Character name
     */
    public listCharacterVersionsByName(name: string, _options?: Configuration): Observable<Array<CharacterItemForNameDTO>> {
        return this.listCharacterVersionsByNameWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<Array<CharacterItemForNameDTO>>) => apiResponse.data));
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listMessagesWithHttpInfo(chatId: string, limit: number, _options?: Configuration): Observable<HttpInfo<Array<ChatMessageDTO>>> {
        const requestContextPromise = this.requestFactory.listMessages(chatId, limit, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listMessagesWithHttpInfo(rsp)));
            }));
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     */
    public listMessages(chatId: string, limit: number, _options?: Configuration): Observable<Array<ChatMessageDTO>> {
        return this.listMessagesWithHttpInfo(chatId, limit, _options).pipe(map((apiResponse: HttpInfo<Array<ChatMessageDTO>>) => apiResponse.data));
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listMessages1WithHttpInfo(chatId: string, limit: number, offset: number, _options?: Configuration): Observable<HttpInfo<Array<ChatMessageDTO>>> {
        const requestContextPromise = this.requestFactory.listMessages1(chatId, limit, offset, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listMessages1WithHttpInfo(rsp)));
            }));
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     * @param limit Messages limit
     * @param offset Messages offset (from new to old)
     */
    public listMessages1(chatId: string, limit: number, offset: number, _options?: Configuration): Observable<Array<ChatMessageDTO>> {
        return this.listMessages1WithHttpInfo(chatId, limit, offset, _options).pipe(map((apiResponse: HttpInfo<Array<ChatMessageDTO>>) => apiResponse.data));
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     */
    public listMessages2WithHttpInfo(chatId: string, _options?: Configuration): Observable<HttpInfo<Array<ChatMessageDTO>>> {
        const requestContextPromise = this.requestFactory.listMessages2(chatId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listMessages2WithHttpInfo(rsp)));
            }));
    }

    /**
     * List messages of a chat.
     * List Chat Messages
     * @param chatId Chat session identifier
     */
    public listMessages2(chatId: string, _options?: Configuration): Observable<Array<ChatMessageDTO>> {
        return this.listMessages2WithHttpInfo(chatId, _options).pipe(map((apiResponse: HttpInfo<Array<ChatMessageDTO>>) => apiResponse.data));
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     */
    public publishCharacterWithHttpInfo(characterId: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.publishCharacter(characterId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.publishCharacterWithHttpInfo(rsp)));
            }));
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     */
    public publishCharacter(characterId: string, _options?: Configuration): Observable<string> {
        return this.publishCharacterWithHttpInfo(characterId, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishCharacter1WithHttpInfo(characterId: string, visibility: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.publishCharacter1(characterId, visibility, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.publishCharacter1WithHttpInfo(rsp)));
            }));
    }

    /**
     * Publish character, draft content becomes formal content, version number increases by 1. After successful publication, a new characterId will be generated and returned. You need to specify the visibility for publication.
     * Publish Character
     * @param characterId The characterId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishCharacter1(characterId: string, visibility: string, _options?: Configuration): Observable<string> {
        return this.publishCharacter1WithHttpInfo(characterId, visibility, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param characterBackendId The characterBackendId to be removed
     */
    public removeCharacterBackendWithHttpInfo(characterBackendId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.removeCharacterBackend(characterBackendId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.removeCharacterBackendWithHttpInfo(rsp)));
            }));
    }

    /**
     * Remove a backend configuration.
     * Remove Character Backend
     * @param characterBackendId The characterBackendId to be removed
     */
    public removeCharacterBackend(characterBackendId: string, _options?: Configuration): Observable<boolean> {
        return this.removeCharacterBackendWithHttpInfo(characterBackendId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Same as /api/v1/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterDetailsWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<CharacterDetailsDTO>>> {
        const requestContextPromise = this.requestFactory.searchCharacterDetails(characterQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchCharacterDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Same as /api/v1/character/search, but returns detailed information of the character.
     * Search Character Details
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterDetails(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Observable<Array<CharacterDetailsDTO>> {
        return this.searchCharacterDetailsWithHttpInfo(characterQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<CharacterDetailsDTO>>) => apiResponse.data));
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterSummaryWithHttpInfo(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<CharacterSummaryDTO>>> {
        const requestContextPromise = this.requestFactory.searchCharacterSummary(characterQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchCharacterSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Search characters: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Name: left match.   - Language, exact match.   - General: name, description, profile, chat style, experience, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the character summary content. - Support pagination. 
     * Search Character Summary
     * @param characterQueryDTO Query conditions
     */
    public searchCharacterSummary(characterQueryDTO: CharacterQueryDTO, _options?: Configuration): Observable<Array<CharacterSummaryDTO>> {
        return this.searchCharacterSummaryWithHttpInfo(characterQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<CharacterSummaryDTO>>) => apiResponse.data));
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param chatId Chat session identifier
     * @param chatContentDTO Chat content
     */
    public sendMessageWithHttpInfo(chatId: string, chatContentDTO: ChatContentDTO, _options?: Configuration): Observable<HttpInfo<LlmResultDTO>> {
        const requestContextPromise = this.requestFactory.sendMessage(chatId, chatContentDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.sendMessageWithHttpInfo(rsp)));
            }));
    }

    /**
     * Send a chat message to character.
     * Send Chat Message
     * @param chatId Chat session identifier
     * @param chatContentDTO Chat content
     */
    public sendMessage(chatId: string, chatContentDTO: ChatContentDTO, _options?: Configuration): Observable<LlmResultDTO> {
        return this.sendMessageWithHttpInfo(chatId, chatContentDTO, _options).pipe(map((apiResponse: HttpInfo<LlmResultDTO>) => apiResponse.data));
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param characterBackendId The characterBackendId to be set to default
     */
    public setDefaultCharacterBackendWithHttpInfo(characterBackendId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.setDefaultCharacterBackend(characterBackendId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.setDefaultCharacterBackendWithHttpInfo(rsp)));
            }));
    }

    /**
     * Set the default backend configuration.
     * Set Default Character Backend
     * @param characterBackendId The characterBackendId to be set to default
     */
    public setDefaultCharacterBackend(characterBackendId: string, _options?: Configuration): Observable<boolean> {
        return this.setDefaultCharacterBackendWithHttpInfo(characterBackendId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param chatCreateDTO Parameters for starting a chat session
     */
    public startChatWithHttpInfo(chatCreateDTO: ChatCreateDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.startChat(chatCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.startChatWithHttpInfo(rsp)));
            }));
    }

    /**
     * Start a chat session.
     * Start Chat Session
     * @param chatCreateDTO Parameters for starting a chat session
     */
    public startChat(chatCreateDTO: ChatCreateDTO, _options?: Configuration): Observable<string> {
        return this.startChatWithHttpInfo(chatCreateDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param chatContentDTO Chat content
     */
    public streamSendMessageWithHttpInfo(chatId: string, chatContentDTO: ChatContentDTO, _options?: Configuration): Observable<HttpInfo<SseEmitter>> {
        const requestContextPromise = this.requestFactory.streamSendMessage(chatId, chatContentDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.streamSendMessageWithHttpInfo(rsp)));
            }));
    }

    /**
     * Refer to /api/v1/chat/send/{chatId}, stream back chunks of the response.
     * Send Chat Message by Streaming Back
     * @param chatId Chat session identifier
     * @param chatContentDTO Chat content
     */
    public streamSendMessage(chatId: string, chatContentDTO: ChatContentDTO, _options?: Configuration): Observable<SseEmitter> {
        return this.streamSendMessageWithHttpInfo(chatId, chatContentDTO, _options).pipe(map((apiResponse: HttpInfo<SseEmitter>) => apiResponse.data));
    }

    /**
     * Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param characterId The characterId to be updated
     * @param characterUpdateDTO The character information to be updated
     */
    public updateCharacterWithHttpInfo(characterId: string, characterUpdateDTO: CharacterUpdateDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateCharacter(characterId, characterUpdateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateCharacterWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update character, refer to /api/v1/character/create, required field: characterId. Returns success or failure.
     * Update Character
     * @param characterId The characterId to be updated
     * @param characterUpdateDTO The character information to be updated
     */
    public updateCharacter(characterId: string, characterUpdateDTO: CharacterUpdateDTO, _options?: Configuration): Observable<boolean> {
        return this.updateCharacterWithHttpInfo(characterId, characterUpdateDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param characterBackendId The characterBackendId to be updated
     * @param characterBackendDTO The character backend configuration to be updated
     */
    public updateCharacterBackendWithHttpInfo(characterBackendId: string, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateCharacterBackend(characterBackendId, characterBackendDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateCharacterBackendWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update a backend configuration.
     * Update Character Backend
     * @param characterBackendId The characterBackendId to be updated
     * @param characterBackendDTO The character backend configuration to be updated
     */
    public updateCharacterBackend(characterBackendId: string, characterBackendDTO: CharacterBackendDTO, _options?: Configuration): Observable<boolean> {
        return this.updateCharacterBackendWithHttpInfo(characterBackendId, characterBackendDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param file Character avatar
     */
    public uploadCharacterAvatarWithHttpInfo(file: HttpFile, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.uploadCharacterAvatar(file, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.uploadCharacterAvatarWithHttpInfo(rsp)));
            }));
    }

    /**
     * Upload an avatar of the character.
     * Upload Character Avatar
     * @param file Character avatar
     */
    public uploadCharacterAvatar(file: HttpFile, _options?: Configuration): Observable<string> {
        return this.uploadCharacterAvatarWithHttpInfo(file, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param file Character picture
     */
    public uploadCharacterPictureWithHttpInfo(file: HttpFile, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.uploadCharacterPicture(file, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.uploadCharacterPictureWithHttpInfo(rsp)));
            }));
    }

    /**
     * Upload a picture of the character.
     * Upload Character Picture
     * @param file Character picture
     */
    public uploadCharacterPicture(file: HttpFile, _options?: Configuration): Observable<string> {
        return this.uploadCharacterPictureWithHttpInfo(file, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

}

import { EncryptionManagerForAdminApiRequestFactory, EncryptionManagerForAdminApiResponseProcessor} from "../apis/EncryptionManagerForAdminApi.js";
export class ObservableEncryptionManagerForAdminApi {
    private requestFactory: EncryptionManagerForAdminApiRequestFactory;
    private responseProcessor: EncryptionManagerForAdminApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: EncryptionManagerForAdminApiRequestFactory,
        responseProcessor?: EncryptionManagerForAdminApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new EncryptionManagerForAdminApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new EncryptionManagerForAdminApiResponseProcessor();
    }

    /**
     * Encrypt a piece of text with the built-in key.
     * Encrypt Text
     * @param text Text to be encrypted
     */
    public encryptTextWithHttpInfo(text: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.encryptText(text, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.encryptTextWithHttpInfo(rsp)));
            }));
    }

    /**
     * Encrypt a piece of text with the built-in key.
     * Encrypt Text
     * @param text Text to be encrypted
     */
    public encryptText(text: string, _options?: Configuration): Observable<string> {
        return this.encryptTextWithHttpInfo(text, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

}

import { FlowApiRequestFactory, FlowApiResponseProcessor} from "../apis/FlowApi.js";
export class ObservableFlowApi {
    private requestFactory: FlowApiRequestFactory;
    private responseProcessor: FlowApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: FlowApiRequestFactory,
        responseProcessor?: FlowApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new FlowApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new FlowApiResponseProcessor();
    }

    /**
     * Batch call shortcut for /api/v1/flow/details/search.
     * Batch Search Flow Details
     * @param flowQueryDTO Query conditions
     */
    public batchSearchFlowDetailsWithHttpInfo(flowQueryDTO: Array<FlowQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<FlowDetailsDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchFlowDetails(flowQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchFlowDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/flow/details/search.
     * Batch Search Flow Details
     * @param flowQueryDTO Query conditions
     */
    public batchSearchFlowDetails(flowQueryDTO: Array<FlowQueryDTO>, _options?: Configuration): Observable<Array<Array<FlowDetailsDTO>>> {
        return this.batchSearchFlowDetailsWithHttpInfo(flowQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<FlowDetailsDTO>>>) => apiResponse.data));
    }

    /**
     * Batch call shortcut for /api/v1/flow/search.
     * Batch Search Flow Summaries
     * @param flowQueryDTO Query conditions
     */
    public batchSearchFlowSummaryWithHttpInfo(flowQueryDTO: Array<FlowQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<FlowSummaryDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchFlowSummary(flowQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchFlowSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/flow/search.
     * Batch Search Flow Summaries
     * @param flowQueryDTO Query conditions
     */
    public batchSearchFlowSummary(flowQueryDTO: Array<FlowQueryDTO>, _options?: Configuration): Observable<Array<Array<FlowSummaryDTO>>> {
        return this.batchSearchFlowSummaryWithHttpInfo(flowQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<FlowSummaryDTO>>>) => apiResponse.data));
    }

    /**
     * Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 
     * Clone Flow
     * @param flowId The referenced flowId
     */
    public cloneFlowWithHttpInfo(flowId: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.cloneFlow(flowId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.cloneFlowWithHttpInfo(rsp)));
            }));
    }

    /**
     * Enter the flowId, generate a new record, the content is basically the same as the original flow, but the following fields are different: - Version number is 1 - Visibility is private - The parent flow is the source flowId - The creation time is the current moment.  - All statistical indicators are zeroed.  Return the new flowId. 
     * Clone Flow
     * @param flowId The referenced flowId
     */
    public cloneFlow(flowId: string, _options?: Configuration): Observable<string> {
        return this.cloneFlowWithHttpInfo(flowId, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Batch clone multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Clone Flows
     * @param requestBody List of flow information to be created
     */
    public cloneFlowsWithHttpInfo(requestBody: Array<string>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.cloneFlows(requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.cloneFlowsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch clone multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Clone Flows
     * @param requestBody List of flow information to be created
     */
    public cloneFlows(requestBody: Array<string>, _options?: Configuration): Observable<Array<string>> {
        return this.cloneFlowsWithHttpInfo(requestBody, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Calculate the number of flows according to the specified query conditions.
     * Calculate Number of Flows
     * @param flowQueryDTO Query conditions
     */
    public countFlowsWithHttpInfo(flowQueryDTO: FlowQueryDTO, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.countFlows(flowQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.countFlowsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Calculate the number of flows according to the specified query conditions.
     * Calculate Number of Flows
     * @param flowQueryDTO Query conditions
     */
    public countFlows(flowQueryDTO: FlowQueryDTO, _options?: Configuration): Observable<number> {
        return this.countFlowsWithHttpInfo(flowQueryDTO, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Flow
     * @param flowCreateDTO Information of the flow to be created
     */
    public createFlowWithHttpInfo(flowCreateDTO: FlowCreateDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createFlow(flowCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createFlowWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a flow, ignore required fields: - Flow name - Flow configuration  Limitations: - Description: 300 characters - Configuration: 2000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Flow
     * @param flowCreateDTO Information of the flow to be created
     */
    public createFlow(flowCreateDTO: FlowCreateDTO, _options?: Configuration): Observable<string> {
        return this.createFlowWithHttpInfo(flowCreateDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Batch create multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Create Flows
     * @param flowCreateDTO List of flow information to be created
     */
    public createFlowsWithHttpInfo(flowCreateDTO: Array<FlowCreateDTO>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.createFlows(flowCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createFlowsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch create multiple flows. Ensure transactionality, return the flowId list after success.
     * Batch Create Flows
     * @param flowCreateDTO List of flow information to be created
     */
    public createFlows(flowCreateDTO: Array<FlowCreateDTO>, _options?: Configuration): Observable<Array<string>> {
        return this.createFlowsWithHttpInfo(flowCreateDTO, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Delete flow. Return success or failure.
     * Delete Flow
     * @param flowId FlowId to be deleted
     */
    public deleteFlowWithHttpInfo(flowId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deleteFlow(flowId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteFlowWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete flow. Return success or failure.
     * Delete Flow
     * @param flowId FlowId to be deleted
     */
    public deleteFlow(flowId: string, _options?: Configuration): Observable<boolean> {
        return this.deleteFlowWithHttpInfo(flowId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.
     * Batch Delete Flows
     * @param requestBody List of flowId to be deleted
     */
    public deleteFlowsWithHttpInfo(requestBody: Array<string>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.deleteFlows(requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deleteFlowsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete multiple flows. Ensure transactionality, return the list of successfully deleted flowId.
     * Batch Delete Flows
     * @param requestBody List of flowId to be deleted
     */
    public deleteFlows(requestBody: Array<string>, _options?: Configuration): Observable<Array<string>> {
        return this.deleteFlowsWithHttpInfo(requestBody, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Get flow detailed information.
     * Get Flow Details
     * @param flowId FlowId to be obtained
     */
    public getFlowDetailsWithHttpInfo(flowId: string, _options?: Configuration): Observable<HttpInfo<FlowDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getFlowDetails(flowId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getFlowDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get flow detailed information.
     * Get Flow Details
     * @param flowId FlowId to be obtained
     */
    public getFlowDetails(flowId: string, _options?: Configuration): Observable<FlowDetailsDTO> {
        return this.getFlowDetailsWithHttpInfo(flowId, _options).pipe(map((apiResponse: HttpInfo<FlowDetailsDTO>) => apiResponse.data));
    }

    /**
     * Get flow summary information.
     * Get Flow Summary
     * @param flowId flowId to be obtained
     */
    public getFlowSummaryWithHttpInfo(flowId: string, _options?: Configuration): Observable<HttpInfo<FlowSummaryDTO>> {
        const requestContextPromise = this.requestFactory.getFlowSummary(flowId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getFlowSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get flow summary information.
     * Get Flow Summary
     * @param flowId flowId to be obtained
     */
    public getFlowSummary(flowId: string, _options?: Configuration): Observable<FlowSummaryDTO> {
        return this.getFlowSummaryWithHttpInfo(flowId, _options).pipe(map((apiResponse: HttpInfo<FlowSummaryDTO>) => apiResponse.data));
    }

    /**
     * List the versions and corresponding flowIds by flow name.
     * List Versions by Flow Name
     * @param name Flow name
     */
    public listFlowVersionsByNameWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<Array<FlowItemForNameDTO>>> {
        const requestContextPromise = this.requestFactory.listFlowVersionsByName(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listFlowVersionsByNameWithHttpInfo(rsp)));
            }));
    }

    /**
     * List the versions and corresponding flowIds by flow name.
     * List Versions by Flow Name
     * @param name Flow name
     */
    public listFlowVersionsByName(name: string, _options?: Configuration): Observable<Array<FlowItemForNameDTO>> {
        return this.listFlowVersionsByNameWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<Array<FlowItemForNameDTO>>) => apiResponse.data));
    }

    /**
     * Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.
     * Publish Flow
     * @param flowId The flowId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishFlowWithHttpInfo(flowId: string, visibility: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.publishFlow(flowId, visibility, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.publishFlowWithHttpInfo(rsp)));
            }));
    }

    /**
     * Publish flow, draft content becomes formal content, version number increases by 1. After successful publication, a new flowId will be generated and returned. You need to specify the visibility for publication.
     * Publish Flow
     * @param flowId The flowId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishFlow(flowId: string, visibility: string, _options?: Configuration): Observable<string> {
        return this.publishFlowWithHttpInfo(flowId, visibility, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Same as /api/v1/flow/search, but returns detailed information of the flow.
     * Search Flow Details
     * @param flowQueryDTO Query conditions
     */
    public searchFlowDetailsWithHttpInfo(flowQueryDTO: FlowQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<FlowDetailsDTO>>> {
        const requestContextPromise = this.requestFactory.searchFlowDetails(flowQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchFlowDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Same as /api/v1/flow/search, but returns detailed information of the flow.
     * Search Flow Details
     * @param flowQueryDTO Query conditions
     */
    public searchFlowDetails(flowQueryDTO: FlowQueryDTO, _options?: Configuration): Observable<Array<FlowDetailsDTO>> {
        return this.searchFlowDetailsWithHttpInfo(flowQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<FlowDetailsDTO>>) => apiResponse.data));
    }

    /**
     * Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 
     * Search Flow Summary
     * @param flowQueryDTO Query conditions
     */
    public searchFlowSummaryWithHttpInfo(flowQueryDTO: FlowQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<FlowSummaryDTO>>> {
        const requestContextPromise = this.requestFactory.searchFlowSummary(flowQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchFlowSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Search flows: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Format: exact match, currently supported: langflow   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - General: name, description, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the flow summary content. - Support pagination. 
     * Search Flow Summary
     * @param flowQueryDTO Query conditions
     */
    public searchFlowSummary(flowQueryDTO: FlowQueryDTO, _options?: Configuration): Observable<Array<FlowSummaryDTO>> {
        return this.searchFlowSummaryWithHttpInfo(flowQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<FlowSummaryDTO>>) => apiResponse.data));
    }

    /**
     * Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.
     * Update Flow
     * @param flowId FlowId to be updated
     * @param flowUpdateDTO Flow information to be updated
     */
    public updateFlowWithHttpInfo(flowId: string, flowUpdateDTO: FlowUpdateDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateFlow(flowId, flowUpdateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateFlowWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update flow, refer to /api/v1/flow/create, required field: flowId. Return success or failure.
     * Update Flow
     * @param flowId FlowId to be updated
     * @param flowUpdateDTO Flow information to be updated
     */
    public updateFlow(flowId: string, flowUpdateDTO: FlowUpdateDTO, _options?: Configuration): Observable<boolean> {
        return this.updateFlowWithHttpInfo(flowId, flowUpdateDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

}

import { InteractiveStatisticsApiRequestFactory, InteractiveStatisticsApiResponseProcessor} from "../apis/InteractiveStatisticsApi.js";
export class ObservableInteractiveStatisticsApi {
    private requestFactory: InteractiveStatisticsApiRequestFactory;
    private responseProcessor: InteractiveStatisticsApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: InteractiveStatisticsApiRequestFactory,
        responseProcessor?: InteractiveStatisticsApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new InteractiveStatisticsApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new InteractiveStatisticsApiResponseProcessor();
    }

    /**
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * Add Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param delta Delta in statistical value
     */
    public addStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, delta: number, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.addStatistic(infoType, infoId, statsType, delta, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.addStatisticWithHttpInfo(rsp)));
            }));
    }

    /**
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * Add Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param delta Delta in statistical value
     */
    public addStatistic(infoType: string, infoId: string, statsType: string, delta: number, _options?: Configuration): Observable<number> {
        return this.addStatisticWithHttpInfo(infoType, infoId, statsType, delta, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     */
    public getScoreWithHttpInfo(infoType: string, infoId: string, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.getScore(infoType, infoId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getScoreWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     */
    public getScore(infoType: string, infoId: string, _options?: Configuration): Observable<number> {
        return this.getScoreWithHttpInfo(infoType, infoId, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public getStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.getStatistic(infoType, infoId, statsType, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getStatisticWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public getStatistic(infoType: string, infoId: string, statsType: string, _options?: Configuration): Observable<number> {
        return this.getStatisticWithHttpInfo(infoType, infoId, statsType, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     */
    public getStatisticsWithHttpInfo(infoType: string, infoId: string, _options?: Configuration): Observable<HttpInfo<InteractiveStatsDTO>> {
        const requestContextPromise = this.requestFactory.getStatistics(infoType, infoId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getStatisticsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     */
    public getStatistics(infoType: string, infoId: string, _options?: Configuration): Observable<InteractiveStatsDTO> {
        return this.getStatisticsWithHttpInfo(infoType, infoId, _options).pipe(map((apiResponse: HttpInfo<InteractiveStatsDTO>) => apiResponse.data));
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public increaseStatisticWithHttpInfo(infoType: string, infoId: string, statsType: string, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.increaseStatistic(infoType, infoId, statsType, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.increaseStatisticWithHttpInfo(rsp)));
            }));
    }

    /**
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param infoType Resource type: prompt | flow | plugin
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public increaseStatistic(infoType: string, infoId: string, statsType: string, _options?: Configuration): Observable<number> {
        return this.increaseStatisticWithHttpInfo(infoType, infoId, statsType, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatisticWithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listCharactersByStatistic(statsType, pageSize, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listCharactersByStatisticWithHttpInfo(rsp)));
            }));
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<Array<CharacterSummaryStatsDTO>> {
        return this.listCharactersByStatisticWithHttpInfo(statsType, pageSize, asc, _options).pipe(map((apiResponse: HttpInfo<Array<CharacterSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic1WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listCharactersByStatistic1(statsType, pageSize, pageNum, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listCharactersByStatistic1WithHttpInfo(rsp)));
            }));
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<Array<CharacterSummaryStatsDTO>> {
        return this.listCharactersByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc, _options).pipe(map((apiResponse: HttpInfo<Array<CharacterSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic2WithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<CharacterSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listCharactersByStatistic2(statsType, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listCharactersByStatistic2WithHttpInfo(rsp)));
            }));
    }

    /**
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listCharactersByStatistic2(statsType: string, asc?: string, _options?: Configuration): Observable<Array<CharacterSummaryStatsDTO>> {
        return this.listCharactersByStatistic2WithHttpInfo(statsType, asc, _options).pipe(map((apiResponse: HttpInfo<Array<CharacterSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listFlowsByStatisticWithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<FlowSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listFlowsByStatistic(statsType, pageSize, pageNum, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listFlowsByStatisticWithHttpInfo(rsp)));
            }));
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listFlowsByStatistic(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<Array<FlowSummaryStatsDTO>> {
        return this.listFlowsByStatisticWithHttpInfo(statsType, pageSize, pageNum, asc, _options).pipe(map((apiResponse: HttpInfo<Array<FlowSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listFlowsByStatistic1WithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<FlowSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listFlowsByStatistic1(statsType, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listFlowsByStatistic1WithHttpInfo(rsp)));
            }));
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listFlowsByStatistic1(statsType: string, asc?: string, _options?: Configuration): Observable<Array<FlowSummaryStatsDTO>> {
        return this.listFlowsByStatistic1WithHttpInfo(statsType, asc, _options).pipe(map((apiResponse: HttpInfo<Array<FlowSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listFlowsByStatistic2WithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<FlowSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listFlowsByStatistic2(statsType, pageSize, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listFlowsByStatistic2WithHttpInfo(rsp)));
            }));
    }

    /**
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listFlowsByStatistic2(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<Array<FlowSummaryStatsDTO>> {
        return this.listFlowsByStatistic2WithHttpInfo(statsType, pageSize, asc, _options).pipe(map((apiResponse: HttpInfo<Array<FlowSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatisticWithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listPluginsByStatistic(statsType, pageSize, pageNum, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listPluginsByStatisticWithHttpInfo(rsp)));
            }));
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<Array<PluginSummaryStatsDTO>> {
        return this.listPluginsByStatisticWithHttpInfo(statsType, pageSize, pageNum, asc, _options).pipe(map((apiResponse: HttpInfo<Array<PluginSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic1WithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listPluginsByStatistic1(statsType, pageSize, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listPluginsByStatistic1WithHttpInfo(rsp)));
            }));
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic1(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<Array<PluginSummaryStatsDTO>> {
        return this.listPluginsByStatistic1WithHttpInfo(statsType, pageSize, asc, _options).pipe(map((apiResponse: HttpInfo<Array<PluginSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic2WithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<PluginSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listPluginsByStatistic2(statsType, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listPluginsByStatistic2WithHttpInfo(rsp)));
            }));
    }

    /**
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPluginsByStatistic2(statsType: string, asc?: string, _options?: Configuration): Observable<Array<PluginSummaryStatsDTO>> {
        return this.listPluginsByStatistic2WithHttpInfo(statsType, asc, _options).pipe(map((apiResponse: HttpInfo<Array<PluginSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatisticWithHttpInfo(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listPromptsByStatistic(statsType, pageSize, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listPromptsByStatisticWithHttpInfo(rsp)));
            }));
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Observable<Array<PromptSummaryStatsDTO>> {
        return this.listPromptsByStatisticWithHttpInfo(statsType, pageSize, asc, _options).pipe(map((apiResponse: HttpInfo<Array<PromptSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic1WithHttpInfo(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listPromptsByStatistic1(statsType, pageSize, pageNum, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listPromptsByStatistic1WithHttpInfo(rsp)));
            }));
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Observable<Array<PromptSummaryStatsDTO>> {
        return this.listPromptsByStatistic1WithHttpInfo(statsType, pageSize, pageNum, asc, _options).pipe(map((apiResponse: HttpInfo<Array<PromptSummaryStatsDTO>>) => apiResponse.data));
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic2WithHttpInfo(statsType: string, asc?: string, _options?: Configuration): Observable<HttpInfo<Array<PromptSummaryStatsDTO>>> {
        const requestContextPromise = this.requestFactory.listPromptsByStatistic2(statsType, asc, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listPromptsByStatistic2WithHttpInfo(rsp)));
            }));
    }

    /**
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public listPromptsByStatistic2(statsType: string, asc?: string, _options?: Configuration): Observable<Array<PromptSummaryStatsDTO>> {
        return this.listPromptsByStatistic2WithHttpInfo(statsType, asc, _options).pipe(map((apiResponse: HttpInfo<Array<PromptSummaryStatsDTO>>) => apiResponse.data));
    }

}

import { OrganizationApiRequestFactory, OrganizationApiResponseProcessor} from "../apis/OrganizationApi.js";
export class ObservableOrganizationApi {
    private requestFactory: OrganizationApiRequestFactory;
    private responseProcessor: OrganizationApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: OrganizationApiRequestFactory,
        responseProcessor?: OrganizationApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new OrganizationApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new OrganizationApiResponseProcessor();
    }

    /**
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     * Get My Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwnersWithHttpInfo(all?: string, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.getOwners(all, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getOwnersWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.<br/>By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     * Get My Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwners(all?: string, _options?: Configuration): Observable<Array<string>> {
        return this.getOwnersWithHttpInfo(all, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwnersDotWithHttpInfo(all?: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.getOwnersDot(all, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getOwnersDotWithHttpInfo(rsp)));
            }));
    }

    /**
     * Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Superior Relationship
     * @param all Whether to return virtual reported owners
     */
    public getOwnersDot(all?: string, _options?: Configuration): Observable<string> {
        return this.getOwnersDotWithHttpInfo(all, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual reported owners
     */
    public getSubordinateOwnersWithHttpInfo(username: string, all?: string, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.getSubordinateOwners(username, all, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getSubordinateOwnersWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.<br/> By specifying all=1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     * Get Superior Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual reported owners
     */
    public getSubordinateOwners(username: string, all?: string, _options?: Configuration): Observable<Array<string>> {
        return this.getSubordinateOwnersWithHttpInfo(username, all, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinateSubordinatesWithHttpInfo(username: string, all?: string, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.getSubordinateSubordinates(username, all, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getSubordinateSubordinatesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get Subordinate Relationship
     * @param username The account being queried, must be a subordinate account of the current account
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinateSubordinates(username: string, all?: string, _options?: Configuration): Observable<Array<string>> {
        return this.getSubordinateSubordinatesWithHttpInfo(username, all, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinatesWithHttpInfo(all?: string, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.getSubordinates(all, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getSubordinatesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.<br/>By specifying all=1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     * Get My Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinates(all?: string, _options?: Configuration): Observable<Array<string>> {
        return this.getSubordinatesWithHttpInfo(all, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinatesDotWithHttpInfo(all?: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.getSubordinatesDot(all, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getSubordinatesDotWithHttpInfo(rsp)));
            }));
    }

    /**
     * Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     * Get DOT of Subordinate Relationship
     * @param all Whether to return virtual managed subordinates
     */
    public getSubordinatesDot(all?: string, _options?: Configuration): Observable<string> {
        return this.getSubordinatesDotWithHttpInfo(all, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param username Username
     */
    public listSubordinateAuthoritiesWithHttpInfo(username: string, _options?: Configuration): Observable<HttpInfo<Set<string>>> {
        const requestContextPromise = this.requestFactory.listSubordinateAuthorities(username, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listSubordinateAuthoritiesWithHttpInfo(rsp)));
            }));
    }

    /**
     * List the permission list of the subordinate account.
     * List Subordinate Permissions
     * @param username Username
     */
    public listSubordinateAuthorities(username: string, _options?: Configuration): Observable<Set<string>> {
        return this.listSubordinateAuthoritiesWithHttpInfo(username, _options).pipe(map((apiResponse: HttpInfo<Set<string>>) => apiResponse.data));
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     */
    public removeSubordinateSubordinatesTreeWithHttpInfo(username: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.removeSubordinateSubordinatesTree(username, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.removeSubordinateSubordinatesTreeWithHttpInfo(rsp)));
            }));
    }

    /**
     * Fully delete the direct subordinate relationship of the subordinate account.
     * Clear Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     */
    public removeSubordinateSubordinatesTree(username: string, _options?: Configuration): Observable<boolean> {
        return this.removeSubordinateSubordinatesTreeWithHttpInfo(username, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateSubordinateAuthoritiesWithHttpInfo(username: string, requestBody: Set<string>, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateSubordinateAuthorities(username, requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateSubordinateAuthoritiesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     * Update Subordinate Permissions
     * @param username Username
     * @param requestBody Permission list
     */
    public updateSubordinateAuthorities(username: string, requestBody: Set<string>, _options?: Configuration): Observable<boolean> {
        return this.updateSubordinateAuthoritiesWithHttpInfo(username, requestBody, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateOwnersWithHttpInfo(username: string, requestBody: Array<string>, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateSubordinateOwners(username, requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateSubordinateOwnersWithHttpInfo(rsp)));
            }));
    }

    /**
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Superior Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) superior account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateOwners(username: string, requestBody: Array<string>, _options?: Configuration): Observable<boolean> {
        return this.updateSubordinateOwnersWithHttpInfo(username, requestBody, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateSubordinatesWithHttpInfo(username: string, requestBody: Array<string>, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updateSubordinateSubordinates(username, requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updateSubordinateSubordinatesWithHttpInfo(rsp)));
            }));
    }

    /**
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     * Update Subordinate Relationship
     * @param username The account being operated, must be a subordinate account of the current account
     * @param requestBody The (direct) subordinate account of the subordinate account, all accounts must be subordinate accounts of the current account
     */
    public updateSubordinateSubordinates(username: string, requestBody: Array<string>, _options?: Configuration): Observable<boolean> {
        return this.updateSubordinateSubordinatesWithHttpInfo(username, requestBody, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

}

import { PluginApiRequestFactory, PluginApiResponseProcessor} from "../apis/PluginApi.js";
export class ObservablePluginApi {
    private requestFactory: PluginApiRequestFactory;
    private responseProcessor: PluginApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: PluginApiRequestFactory,
        responseProcessor?: PluginApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new PluginApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new PluginApiResponseProcessor();
    }

    /**
     * Batch call shortcut for /api/v1/plugin/details/search.
     * Batch Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginDetailsWithHttpInfo(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<PluginDetailsDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchPluginDetails(pluginQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchPluginDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/plugin/details/search.
     * Batch Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginDetails(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Observable<Array<Array<PluginDetailsDTO>>> {
        return this.batchSearchPluginDetailsWithHttpInfo(pluginQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<PluginDetailsDTO>>>) => apiResponse.data));
    }

    /**
     * Batch call shortcut for /api/v1/plugin/search.
     * Batch Search Plugin Summaries
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginSummaryWithHttpInfo(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<PluginSummaryDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchPluginSummary(pluginQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchPluginSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/plugin/search.
     * Batch Search Plugin Summaries
     * @param pluginQueryDTO Query conditions
     */
    public batchSearchPluginSummary(pluginQueryDTO: Array<PluginQueryDTO>, _options?: Configuration): Observable<Array<Array<PluginSummaryDTO>>> {
        return this.batchSearchPluginSummaryWithHttpInfo(pluginQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<PluginSummaryDTO>>>) => apiResponse.data));
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param pluginQueryDTO Query conditions
     */
    public countPluginsWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.countPlugins(pluginQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.countPluginsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Calculate the number of plugins according to the specified query conditions.
     * Calculate Number of Plugins
     * @param pluginQueryDTO Query conditions
     */
    public countPlugins(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Observable<number> {
        return this.countPluginsWithHttpInfo(pluginQueryDTO, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param pluginCreateDTO Information of the plugin to be created
     */
    public createPluginWithHttpInfo(pluginCreateDTO: PluginCreateDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createPlugin(pluginCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createPluginWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a plugin, required fields: - Plugin name - Plugin manifestInfo (URL or JSON) - Plugin apiInfo (URL or JSON)  Limitations: - Name: 100 characters - Example: 2000 characters - Tags: 5 
     * Create Plugin
     * @param pluginCreateDTO Information of the plugin to be created
     */
    public createPlugin(pluginCreateDTO: PluginCreateDTO, _options?: Configuration): Observable<string> {
        return this.createPluginWithHttpInfo(pluginCreateDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param pluginCreateDTO List of plugin information to be created
     */
    public createPluginsWithHttpInfo(pluginCreateDTO: Array<PluginCreateDTO>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.createPlugins(pluginCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createPluginsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch create multiple plugins. Ensure transactionality, return the pluginId list after success.
     * Batch Create Plugins
     * @param pluginCreateDTO List of plugin information to be created
     */
    public createPlugins(pluginCreateDTO: Array<PluginCreateDTO>, _options?: Configuration): Observable<Array<string>> {
        return this.createPluginsWithHttpInfo(pluginCreateDTO, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param pluginId The pluginId to be deleted
     */
    public deletePluginWithHttpInfo(pluginId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deletePlugin(pluginId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deletePluginWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete plugin. Returns success or failure.
     * Delete Plugin
     * @param pluginId The pluginId to be deleted
     */
    public deletePlugin(pluginId: string, _options?: Configuration): Observable<boolean> {
        return this.deletePluginWithHttpInfo(pluginId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param requestBody List of pluginIds to be deleted
     */
    public deletePluginsWithHttpInfo(requestBody: Array<string>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.deletePlugins(requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deletePluginsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete multiple plugins. Ensure transactionality, return the list of successfully deleted pluginIds.
     * Batch Delete Plugins
     * @param requestBody List of pluginIds to be deleted
     */
    public deletePlugins(requestBody: Array<string>, _options?: Configuration): Observable<Array<string>> {
        return this.deletePluginsWithHttpInfo(requestBody, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param pluginId PluginId to be obtained
     */
    public getPluginDetailsWithHttpInfo(pluginId: string, _options?: Configuration): Observable<HttpInfo<PluginDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getPluginDetails(pluginId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getPluginDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get plugin detailed information.
     * Get Plugin Details
     * @param pluginId PluginId to be obtained
     */
    public getPluginDetails(pluginId: string, _options?: Configuration): Observable<PluginDetailsDTO> {
        return this.getPluginDetailsWithHttpInfo(pluginId, _options).pipe(map((apiResponse: HttpInfo<PluginDetailsDTO>) => apiResponse.data));
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param pluginId PluginId to be obtained
     */
    public getPluginSummaryWithHttpInfo(pluginId: string, _options?: Configuration): Observable<HttpInfo<PluginSummaryDTO>> {
        const requestContextPromise = this.requestFactory.getPluginSummary(pluginId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getPluginSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get plugin summary information.
     * Get Plugin Summary
     * @param pluginId PluginId to be obtained
     */
    public getPluginSummary(pluginId: string, _options?: Configuration): Observable<PluginSummaryDTO> {
        return this.getPluginSummaryWithHttpInfo(pluginId, _options).pipe(map((apiResponse: HttpInfo<PluginSummaryDTO>) => apiResponse.data));
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param pluginId The pluginId to be fetched
     */
    public refreshPluginInfoWithHttpInfo(pluginId: string, _options?: Configuration): Observable<HttpInfo<void>> {
        const requestContextPromise = this.requestFactory.refreshPluginInfo(pluginId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.refreshPluginInfoWithHttpInfo(rsp)));
            }));
    }

    /**
     * For online manifest, api-docs information provided at the time of entry, this interface can immediately refresh the information in the system cache (default cache time is 1 hour). Generally, there is no need to call, unless you know that the corresponding plugin platform has just updated the interface, and the business side wants to get the latest information immediately, then call this interface to delete the system cache.
     * Refresh Plugin Information
     * @param pluginId The pluginId to be fetched
     */
    public refreshPluginInfo(pluginId: string, _options?: Configuration): Observable<void> {
        return this.refreshPluginInfoWithHttpInfo(pluginId, _options).pipe(map((apiResponse: HttpInfo<void>) => apiResponse.data));
    }

    /**
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginDetailsWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<PluginDetailsDTO>>> {
        const requestContextPromise = this.requestFactory.searchPluginDetails(pluginQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchPluginDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Same as /api/v1/plugin/search, but returns detailed information of the plugin.
     * Search Plugin Details
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginDetails(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Observable<Array<PluginDetailsDTO>> {
        return this.searchPluginDetailsWithHttpInfo(pluginQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<PluginDetailsDTO>>) => apiResponse.data));
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginSummaryWithHttpInfo(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<PluginSummaryDTO>>> {
        const requestContextPromise = this.requestFactory.searchPluginSummary(pluginQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchPluginSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Search plugins: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Plugin information format: currently supported: dash_scope, open_ai.   - Interface information format: currently supported: openapi_v3.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Provider: left match.   - General: name, provider information, manifest (real-time pull mode is not currently supported), fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the plugin summary content. - Support pagination. 
     * Search Plugin Summary
     * @param pluginQueryDTO Query conditions
     */
    public searchPluginSummary(pluginQueryDTO: PluginQueryDTO, _options?: Configuration): Observable<Array<PluginSummaryDTO>> {
        return this.searchPluginSummaryWithHttpInfo(pluginQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<PluginSummaryDTO>>) => apiResponse.data));
    }

    /**
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param pluginId The pluginId to be updated
     * @param pluginUpdateDTO The plugin information to be updated
     */
    public updatePluginWithHttpInfo(pluginId: string, pluginUpdateDTO: PluginUpdateDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updatePlugin(pluginId, pluginUpdateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updatePluginWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update plugin, refer to /api/v1/plugin/create, required field: pluginId. Returns success or failure.
     * Update Plugin
     * @param pluginId The pluginId to be updated
     * @param pluginUpdateDTO The plugin information to be updated
     */
    public updatePlugin(pluginId: string, pluginUpdateDTO: PluginUpdateDTO, _options?: Configuration): Observable<boolean> {
        return this.updatePluginWithHttpInfo(pluginId, pluginUpdateDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

}

import { PromptApiRequestFactory, PromptApiResponseProcessor} from "../apis/PromptApi.js";
export class ObservablePromptApi {
    private requestFactory: PromptApiRequestFactory;
    private responseProcessor: PromptApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: PromptApiRequestFactory,
        responseProcessor?: PromptApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new PromptApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new PromptApiResponseProcessor();
    }

    /**
     * Apply parameters to prompt record.
     * Apply Parameters to Prompt Record
     * @param promptRefDTO Prompt record
     */
    public applyPromptRefWithHttpInfo(promptRefDTO: PromptRefDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.applyPromptRef(promptRefDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.applyPromptRefWithHttpInfo(rsp)));
            }));
    }

    /**
     * Apply parameters to prompt record.
     * Apply Parameters to Prompt Record
     * @param promptRefDTO Prompt record
     */
    public applyPromptRef(promptRefDTO: PromptRefDTO, _options?: Configuration): Observable<string> {
        return this.applyPromptRefWithHttpInfo(promptRefDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Apply parameters to prompt template.
     * Apply Parameters to Prompt Template
     * @param promptTemplateDTO String type prompt template
     */
    public applyPromptTemplateWithHttpInfo(promptTemplateDTO: PromptTemplateDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.applyPromptTemplate(promptTemplateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.applyPromptTemplateWithHttpInfo(rsp)));
            }));
    }

    /**
     * Apply parameters to prompt template.
     * Apply Parameters to Prompt Template
     * @param promptTemplateDTO String type prompt template
     */
    public applyPromptTemplate(promptTemplateDTO: PromptTemplateDTO, _options?: Configuration): Observable<string> {
        return this.applyPromptTemplateWithHttpInfo(promptTemplateDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Batch call shortcut for /api/v1/prompt/details/search.
     * Batch Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptDetailsWithHttpInfo(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<PromptDetailsDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchPromptDetails(promptQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchPromptDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/prompt/details/search.
     * Batch Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptDetails(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Observable<Array<Array<PromptDetailsDTO>>> {
        return this.batchSearchPromptDetailsWithHttpInfo(promptQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<PromptDetailsDTO>>>) => apiResponse.data));
    }

    /**
     * Batch call shortcut for /api/v1/prompt/search.
     * Batch Search Prompt Summaries
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptSummaryWithHttpInfo(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Observable<HttpInfo<Array<Array<PromptSummaryDTO>>>> {
        const requestContextPromise = this.requestFactory.batchSearchPromptSummary(promptQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.batchSearchPromptSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch call shortcut for /api/v1/prompt/search.
     * Batch Search Prompt Summaries
     * @param promptQueryDTO Query conditions
     */
    public batchSearchPromptSummary(promptQueryDTO: Array<PromptQueryDTO>, _options?: Configuration): Observable<Array<Array<PromptSummaryDTO>>> {
        return this.batchSearchPromptSummaryWithHttpInfo(promptQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<Array<PromptSummaryDTO>>>) => apiResponse.data));
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param promptId The referenced promptId
     */
    public clonePromptWithHttpInfo(promptId: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.clonePrompt(promptId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.clonePromptWithHttpInfo(rsp)));
            }));
    }

    /**
     * Enter the promptId, generate a new record, the content is basically the same as the original prompt, but the following fields are different: - Version number is 1 - Visibility is private - The parent prompt is the source promptId - The creation time is the current moment. - All statistical indicators are zeroed.  Return the new promptId. 
     * Clone Prompt
     * @param promptId The referenced promptId
     */
    public clonePrompt(promptId: string, _options?: Configuration): Observable<string> {
        return this.clonePromptWithHttpInfo(promptId, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param requestBody List of prompt information to be created
     */
    public clonePromptsWithHttpInfo(requestBody: Array<string>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.clonePrompts(requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.clonePromptsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch clone multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Clone Prompts
     * @param requestBody List of prompt information to be created
     */
    public clonePrompts(requestBody: Array<string>, _options?: Configuration): Observable<Array<string>> {
        return this.clonePromptsWithHttpInfo(requestBody, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPromptsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Observable<HttpInfo<number>> {
        const requestContextPromise = this.requestFactory.countPrompts(promptQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.countPromptsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Calculate the number of prompts according to the specified query conditions.
     * Calculate Number of Prompts
     * @param promptQueryDTO Query conditions
     */
    public countPrompts(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Observable<number> {
        return this.countPromptsWithHttpInfo(promptQueryDTO, _options).pipe(map((apiResponse: HttpInfo<number>) => apiResponse.data));
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param promptCreateDTO Information of the prompt to be created
     */
    public createPromptWithHttpInfo(promptCreateDTO: PromptCreateDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createPrompt(promptCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createPromptWithHttpInfo(rsp)));
            }));
    }

    /**
     * Create a prompt, required fields: - Prompt name - Prompt content - Applicable model  Limitations: - Description: 300 characters - Template: 1000 characters - Example: 2000 characters - Tags: 5 - Parameters: 10 
     * Create Prompt
     * @param promptCreateDTO Information of the prompt to be created
     */
    public createPrompt(promptCreateDTO: PromptCreateDTO, _options?: Configuration): Observable<string> {
        return this.createPromptWithHttpInfo(promptCreateDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param promptCreateDTO List of prompt information to be created
     */
    public createPromptsWithHttpInfo(promptCreateDTO: Array<PromptCreateDTO>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.createPrompts(promptCreateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createPromptsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Batch create multiple prompts. Ensure transactionality, return the promptId list after success.
     * Batch Create Prompts
     * @param promptCreateDTO List of prompt information to be created
     */
    public createPrompts(promptCreateDTO: Array<PromptCreateDTO>, _options?: Configuration): Observable<Array<string>> {
        return this.createPromptsWithHttpInfo(promptCreateDTO, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param promptId The promptId to be deleted
     */
    public deletePromptWithHttpInfo(promptId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deletePrompt(promptId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deletePromptWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete prompt. Returns success or failure.
     * Delete Prompt
     * @param promptId The promptId to be deleted
     */
    public deletePrompt(promptId: string, _options?: Configuration): Observable<boolean> {
        return this.deletePromptWithHttpInfo(promptId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Delete prompt by name. return the list of successfully deleted promptIds.
     * Delete Prompt by Name
     * @param name The prompt name to be deleted
     */
    public deletePromptByNameWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.deletePromptByName(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deletePromptByNameWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete prompt by name. return the list of successfully deleted promptIds.
     * Delete Prompt by Name
     * @param name The prompt name to be deleted
     */
    public deletePromptByName(name: string, _options?: Configuration): Observable<Array<string>> {
        return this.deletePromptByNameWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param requestBody List of promptIds to be deleted
     */
    public deletePromptsWithHttpInfo(requestBody: Array<string>, _options?: Configuration): Observable<HttpInfo<Array<string>>> {
        const requestContextPromise = this.requestFactory.deletePrompts(requestBody, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deletePromptsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete multiple prompts. Ensure transactionality, return the list of successfully deleted promptIds.
     * Batch Delete Prompts
     * @param requestBody List of promptIds to be deleted
     */
    public deletePrompts(requestBody: Array<string>, _options?: Configuration): Observable<Array<string>> {
        return this.deletePromptsWithHttpInfo(requestBody, _options).pipe(map((apiResponse: HttpInfo<Array<string>>) => apiResponse.data));
    }

    /**
     * Check if the name already exists.
     * Check If Name Exists
     * @param name Name
     */
    public existsNameWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.existsName(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.existsNameWithHttpInfo(rsp)));
            }));
    }

    /**
     * Check if the name already exists.
     * Check If Name Exists
     * @param name Name
     */
    public existsName(name: string, _options?: Configuration): Observable<boolean> {
        return this.existsNameWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param promptId PromptId to be obtained
     */
    public getPromptDetailsWithHttpInfo(promptId: string, _options?: Configuration): Observable<HttpInfo<PromptDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getPromptDetails(promptId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getPromptDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get prompt detailed information.
     * Get Prompt Details
     * @param promptId PromptId to be obtained
     */
    public getPromptDetails(promptId: string, _options?: Configuration): Observable<PromptDetailsDTO> {
        return this.getPromptDetailsWithHttpInfo(promptId, _options).pipe(map((apiResponse: HttpInfo<PromptDetailsDTO>) => apiResponse.data));
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param promptId PromptId to be obtained
     */
    public getPromptSummaryWithHttpInfo(promptId: string, _options?: Configuration): Observable<HttpInfo<PromptSummaryDTO>> {
        const requestContextPromise = this.requestFactory.getPromptSummary(promptId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getPromptSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get prompt summary information.
     * Get Prompt Summary
     * @param promptId PromptId to be obtained
     */
    public getPromptSummary(promptId: string, _options?: Configuration): Observable<PromptSummaryDTO> {
        return this.getPromptSummaryWithHttpInfo(promptId, _options).pipe(map((apiResponse: HttpInfo<PromptSummaryDTO>) => apiResponse.data));
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param name Prompt name
     */
    public listPromptVersionsByNameWithHttpInfo(name: string, _options?: Configuration): Observable<HttpInfo<Array<PromptItemForNameDTO>>> {
        const requestContextPromise = this.requestFactory.listPromptVersionsByName(name, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.listPromptVersionsByNameWithHttpInfo(rsp)));
            }));
    }

    /**
     * List the versions and corresponding promptIds by prompt name.
     * List Versions by Prompt Name
     * @param name Prompt name
     */
    public listPromptVersionsByName(name: string, _options?: Configuration): Observable<Array<PromptItemForNameDTO>> {
        return this.listPromptVersionsByNameWithHttpInfo(name, _options).pipe(map((apiResponse: HttpInfo<Array<PromptItemForNameDTO>>) => apiResponse.data));
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param promptId The promptId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishPromptWithHttpInfo(promptId: string, visibility: string, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.publishPrompt(promptId, visibility, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.publishPromptWithHttpInfo(rsp)));
            }));
    }

    /**
     * Publish prompt, draft content becomes formal content, version number increases by 1. After successful publication, a new promptId will be generated and returned. You need to specify the visibility for publication.
     * Publish Prompt
     * @param promptId The promptId to be published
     * @param visibility Visibility: public | private | ...
     */
    public publishPrompt(promptId: string, visibility: string, _options?: Configuration): Observable<string> {
        return this.publishPromptWithHttpInfo(promptId, visibility, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Same as /api/v1/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public searchPromptDetailsWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<PromptDetailsDTO>>> {
        const requestContextPromise = this.requestFactory.searchPromptDetails(promptQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchPromptDetailsWithHttpInfo(rsp)));
            }));
    }

    /**
     * Same as /api/v1/prompt/search, but returns detailed information of the prompt.
     * Search Prompt Details
     * @param promptQueryDTO Query conditions
     */
    public searchPromptDetails(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Observable<Array<PromptDetailsDTO>> {
        return this.searchPromptDetailsWithHttpInfo(promptQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<PromptDetailsDTO>>) => apiResponse.data));
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPromptSummaryWithHttpInfo(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Observable<HttpInfo<Array<PromptSummaryDTO>>> {
        const requestContextPromise = this.requestFactory.searchPromptSummary(promptQueryDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.searchPromptSummaryWithHttpInfo(rsp)));
            }));
    }

    /**
     * Search prompts: - Specifiable query fields, and relationship:   - Scope: private, public_org or public. Private can only search this account.   - Username: exact match, only valid when searching public, public_org. If not specified, search all users.   - Tags: exact match (support and, or logic).   - Model type: exact match (support and, or logic).   - Name: left match.   - Type, exact match: string (default) | chat.   - Language, exact match.   - General: name, description, template, example, fuzzy match, one hit is enough; public scope + all user\'s general search does not guarantee timeliness. - A certain sorting rule can be specified, such as view count, reference count, rating, time, descending or ascending. - The search result is the prompt summary content. - Support pagination. 
     * Search Prompt Summary
     * @param promptQueryDTO Query conditions
     */
    public searchPromptSummary(promptQueryDTO: PromptQueryDTO, _options?: Configuration): Observable<Array<PromptSummaryDTO>> {
        return this.searchPromptSummaryWithHttpInfo(promptQueryDTO, _options).pipe(map((apiResponse: HttpInfo<Array<PromptSummaryDTO>>) => apiResponse.data));
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param promptAiParamDTO Call parameters
     */
    public sendPromptWithHttpInfo(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Observable<HttpInfo<LlmResultDTO>> {
        const requestContextPromise = this.requestFactory.sendPrompt(promptAiParamDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.sendPromptWithHttpInfo(rsp)));
            }));
    }

    /**
     * Send the prompt to the AI service. Note that if the embedding model is called, the return is an embedding array, placed in the details field of the result; the original text is in the text field of the result.
     * Send Prompt
     * @param promptAiParamDTO Call parameters
     */
    public sendPrompt(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Observable<LlmResultDTO> {
        return this.sendPromptWithHttpInfo(promptAiParamDTO, _options).pipe(map((apiResponse: HttpInfo<LlmResultDTO>) => apiResponse.data));
    }

    /**
     * Refer to /api/v1/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param promptAiParamDTO Call parameters
     */
    public streamSendPromptWithHttpInfo(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Observable<HttpInfo<SseEmitter>> {
        const requestContextPromise = this.requestFactory.streamSendPrompt(promptAiParamDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.streamSendPromptWithHttpInfo(rsp)));
            }));
    }

    /**
     * Refer to /api/v1/prompt/send, stream back chunks of the response.
     * Send Prompt by Streaming Back
     * @param promptAiParamDTO Call parameters
     */
    public streamSendPrompt(promptAiParamDTO: PromptAiParamDTO, _options?: Configuration): Observable<SseEmitter> {
        return this.streamSendPromptWithHttpInfo(promptAiParamDTO, _options).pipe(map((apiResponse: HttpInfo<SseEmitter>) => apiResponse.data));
    }

    /**
     * Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param promptId The promptId to be updated
     * @param promptUpdateDTO The prompt information to be updated
     */
    public updatePromptWithHttpInfo(promptId: string, promptUpdateDTO: PromptUpdateDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updatePrompt(promptId, promptUpdateDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updatePromptWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update prompt, refer to /api/v1/prompt/create, required field: promptId. Returns success or failure.
     * Update Prompt
     * @param promptId The promptId to be updated
     * @param promptUpdateDTO The prompt information to be updated
     */
    public updatePrompt(promptId: string, promptUpdateDTO: PromptUpdateDTO, _options?: Configuration): Observable<boolean> {
        return this.updatePromptWithHttpInfo(promptId, promptUpdateDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

}

import { PromptTaskApiRequestFactory, PromptTaskApiResponseProcessor} from "../apis/PromptTaskApi.js";
export class ObservablePromptTaskApi {
    private requestFactory: PromptTaskApiRequestFactory;
    private responseProcessor: PromptTaskApiResponseProcessor;
    private configuration: Configuration;

    public constructor(
        configuration: Configuration,
        requestFactory?: PromptTaskApiRequestFactory,
        responseProcessor?: PromptTaskApiResponseProcessor
    ) {
        this.configuration = configuration;
        this.requestFactory = requestFactory || new PromptTaskApiRequestFactory(configuration);
        this.responseProcessor = responseProcessor || new PromptTaskApiResponseProcessor();
    }

    /**
     * Add a prompt task.
     * Add Prompt Task
     * @param promptTaskDTO The prompt task to be added
     */
    public createPromptTaskWithHttpInfo(promptTaskDTO: PromptTaskDTO, _options?: Configuration): Observable<HttpInfo<string>> {
        const requestContextPromise = this.requestFactory.createPromptTask(promptTaskDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.createPromptTaskWithHttpInfo(rsp)));
            }));
    }

    /**
     * Add a prompt task.
     * Add Prompt Task
     * @param promptTaskDTO The prompt task to be added
     */
    public createPromptTask(promptTaskDTO: PromptTaskDTO, _options?: Configuration): Observable<string> {
        return this.createPromptTaskWithHttpInfo(promptTaskDTO, _options).pipe(map((apiResponse: HttpInfo<string>) => apiResponse.data));
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param promptTaskId The promptTaskId to be deleted
     */
    public deletePromptTaskWithHttpInfo(promptTaskId: string, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.deletePromptTask(promptTaskId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.deletePromptTaskWithHttpInfo(rsp)));
            }));
    }

    /**
     * Delete a prompt task.
     * Delete Prompt Task
     * @param promptTaskId The promptTaskId to be deleted
     */
    public deletePromptTask(promptTaskId: string, _options?: Configuration): Observable<boolean> {
        return this.deletePromptTaskWithHttpInfo(promptTaskId, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param promptTaskId The promptTaskId to be queried
     */
    public getPromptTaskWithHttpInfo(promptTaskId: string, _options?: Configuration): Observable<HttpInfo<PromptTaskDetailsDTO>> {
        const requestContextPromise = this.requestFactory.getPromptTask(promptTaskId, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.getPromptTaskWithHttpInfo(rsp)));
            }));
    }

    /**
     * Get the prompt task details.
     * Get Prompt Task
     * @param promptTaskId The promptTaskId to be queried
     */
    public getPromptTask(promptTaskId: string, _options?: Configuration): Observable<PromptTaskDetailsDTO> {
        return this.getPromptTaskWithHttpInfo(promptTaskId, _options).pipe(map((apiResponse: HttpInfo<PromptTaskDetailsDTO>) => apiResponse.data));
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param promptTaskId The promptTaskId to be updated
     * @param promptTaskDTO The prompt task info to be updated
     */
    public updatePromptTaskWithHttpInfo(promptTaskId: string, promptTaskDTO: PromptTaskDTO, _options?: Configuration): Observable<HttpInfo<boolean>> {
        const requestContextPromise = this.requestFactory.updatePromptTask(promptTaskId, promptTaskDTO, _options);

        // build promise chain
        let middlewarePreObservable = from<RequestContext>(requestContextPromise);
        for (let middleware of this.configuration.middleware) {
            middlewarePreObservable = middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => middleware.pre(ctx)));
        }

        return middlewarePreObservable.pipe(mergeMap((ctx: RequestContext) => this.configuration.httpApi.send(ctx))).
            pipe(mergeMap((response: ResponseContext) => {
                let middlewarePostObservable = of(response);
                for (let middleware of this.configuration.middleware) {
                    middlewarePostObservable = middlewarePostObservable.pipe(mergeMap((rsp: ResponseContext) => middleware.post(rsp)));
                }
                return middlewarePostObservable.pipe(map((rsp: ResponseContext) => this.responseProcessor.updatePromptTaskWithHttpInfo(rsp)));
            }));
    }

    /**
     * Update a prompt task.
     * Update Prompt Task
     * @param promptTaskId The promptTaskId to be updated
     * @param promptTaskDTO The prompt task info to be updated
     */
    public updatePromptTask(promptTaskId: string, promptTaskDTO: PromptTaskDTO, _options?: Configuration): Observable<boolean> {
        return this.updatePromptTaskWithHttpInfo(promptTaskId, promptTaskDTO, _options).pipe(map((apiResponse: HttpInfo<boolean>) => apiResponse.data));
    }

}
