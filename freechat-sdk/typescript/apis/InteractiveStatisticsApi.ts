// TODO: better import syntax?
import {BaseAPIRequestFactory, RequiredError, COLLECTION_FORMATS} from './baseapi.js';
import {Configuration} from '../configuration.js';
import {RequestContext, HttpMethod, ResponseContext, HttpFile, HttpInfo} from '../http/http.js';
import {ObjectSerializer} from '../models/ObjectSerializer.js';
import {ApiException} from './exception.js';
import {canConsumeForm, isCodeInRange} from '../util.js';
import {SecurityAuthentication} from '../auth/auth.js';


import { CharacterSummaryStatsDTO } from '../models/CharacterSummaryStatsDTO.js';
import { FlowSummaryStatsDTO } from '../models/FlowSummaryStatsDTO.js';
import { HotTagDTO } from '../models/HotTagDTO.js';
import { InteractiveStatsDTO } from '../models/InteractiveStatsDTO.js';
import { PluginSummaryStatsDTO } from '../models/PluginSummaryStatsDTO.js';
import { PromptSummaryStatsDTO } from '../models/PromptSummaryStatsDTO.js';

/**
 * no description
 */
export class InteractiveStatisticsApiRequestFactory extends BaseAPIRequestFactory {

    /**
     * Add the statistics of the corresponding metrics of the corresponding resources. The increment can be negative. Return the latest statistics.
     * Add Statistics
     * @param infoType Info type: prompt | flow | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param delta Delta in statistical value
     */
    public async addStatistic(infoType: string, infoId: string, statsType: string, delta: number, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'infoType' is not null or undefined
        if (infoType === null || infoType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "addStatistic", "infoType");
        }


        // verify required parameter 'infoId' is not null or undefined
        if (infoId === null || infoId === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "addStatistic", "infoId");
        }


        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "addStatistic", "statsType");
        }


        // verify required parameter 'delta' is not null or undefined
        if (delta === null || delta === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "addStatistic", "delta");
        }


        // Path Params
        const localVarPath = '/api/v1/stats/{infoType}/{infoId}/{statsType}/{delta}'
            .replace('{' + 'infoType' + '}', encodeURIComponent(String(infoType)))
            .replace('{' + 'infoId' + '}', encodeURIComponent(String(infoId)))
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'delta' + '}', encodeURIComponent(String(delta)));

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
     * Get the current user\'s score for the corresponding resource.
     * Get Score for Resource
     * @param infoType Info type: prompt | flow | plugin | character
     * @param infoId Unique resource identifier
     */
    public async getScore(infoType: string, infoId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'infoType' is not null or undefined
        if (infoType === null || infoType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "getScore", "infoType");
        }


        // verify required parameter 'infoId' is not null or undefined
        if (infoId === null || infoId === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "getScore", "infoId");
        }


        // Path Params
        const localVarPath = '/api/v1/score/{infoType}/{infoId}'
            .replace('{' + 'infoType' + '}', encodeURIComponent(String(infoType)))
            .replace('{' + 'infoId' + '}', encodeURIComponent(String(infoId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
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
     * Get the statistics of the corresponding metrics of the corresponding resources.
     * Get Statistics
     * @param infoType Info type: prompt | flow | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public async getStatistic(infoType: string, infoId: string, statsType: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'infoType' is not null or undefined
        if (infoType === null || infoType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "getStatistic", "infoType");
        }


        // verify required parameter 'infoId' is not null or undefined
        if (infoId === null || infoId === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "getStatistic", "infoId");
        }


        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "getStatistic", "statsType");
        }


        // Path Params
        const localVarPath = '/api/v1/stats/{infoType}/{infoId}/{statsType}'
            .replace('{' + 'infoType' + '}', encodeURIComponent(String(infoType)))
            .replace('{' + 'infoId' + '}', encodeURIComponent(String(infoId)))
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
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
     * Get all statistics of the corresponding resources.
     * Get All Statistics
     * @param infoType Info type: prompt | flow | plugin | character
     * @param infoId Unique resource identifier
     */
    public async getStatistics(infoType: string, infoId: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'infoType' is not null or undefined
        if (infoType === null || infoType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "getStatistics", "infoType");
        }


        // verify required parameter 'infoId' is not null or undefined
        if (infoId === null || infoId === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "getStatistics", "infoId");
        }


        // Path Params
        const localVarPath = '/api/v1/stats/{infoType}/{infoId}'
            .replace('{' + 'infoType' + '}', encodeURIComponent(String(infoType)))
            .replace('{' + 'infoId' + '}', encodeURIComponent(String(infoId)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
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
     * Increase the statistics of the corresponding metrics of the corresponding resources by one. Return the latest statistics.
     * Increase Statistics
     * @param infoType Info type: prompt | flow | plugin | character
     * @param infoId Unique resource identifier
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     */
    public async increaseStatistic(infoType: string, infoId: string, statsType: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'infoType' is not null or undefined
        if (infoType === null || infoType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "increaseStatistic", "infoType");
        }


        // verify required parameter 'infoId' is not null or undefined
        if (infoId === null || infoId === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "increaseStatistic", "infoId");
        }


        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "increaseStatistic", "statsType");
        }


        // Path Params
        const localVarPath = '/api/v1/stats/{infoType}/{infoId}/{statsType}'
            .replace('{' + 'infoType' + '}', encodeURIComponent(String(infoType)))
            .replace('{' + 'infoId' + '}', encodeURIComponent(String(infoId)))
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)));

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
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listCharactersByStatistic(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listCharactersByStatistic", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listCharactersByStatistic", "pageSize");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/characters/by/{statsType}/{pageSize}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listCharactersByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listCharactersByStatistic1", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listCharactersByStatistic1", "pageSize");
        }


        // verify required parameter 'pageNum' is not null or undefined
        if (pageNum === null || pageNum === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listCharactersByStatistic1", "pageNum");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/characters/by/{statsType}/{pageSize}/{pageNum}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)))
            .replace('{' + 'pageNum' + '}', encodeURIComponent(String(pageNum)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List characters based on statistics, including interactive statistical data.
     * List Characters by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listCharactersByStatistic2(statsType: string, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listCharactersByStatistic2", "statsType");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/characters/by/{statsType}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listFlowsByStatistic(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listFlowsByStatistic", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listFlowsByStatistic", "pageSize");
        }


        // verify required parameter 'pageNum' is not null or undefined
        if (pageNum === null || pageNum === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listFlowsByStatistic", "pageNum");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/flows/by/{statsType}/{pageSize}/{pageNum}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)))
            .replace('{' + 'pageNum' + '}', encodeURIComponent(String(pageNum)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listFlowsByStatistic1(statsType: string, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listFlowsByStatistic1", "statsType");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/flows/by/{statsType}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List flows based on statistics, including interactive statistical data.
     * List Flows by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listFlowsByStatistic2(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listFlowsByStatistic2", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listFlowsByStatistic2", "pageSize");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/flows/by/{statsType}/{pageSize}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * Get popular tags for a specified info type.
     * Hot Tags
     * @param infoType Info type: prompt | flow | plugin | character
     * @param pageSize Maximum quantity
     * @param text Key word
     */
    public async listHotTags(infoType: string, pageSize: number, text?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'infoType' is not null or undefined
        if (infoType === null || infoType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listHotTags", "infoType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listHotTags", "pageSize");
        }



        // Path Params
        const localVarPath = '/api/v1/tags/hot/{infoType}/{pageSize}'
            .replace('{' + 'infoType' + '}', encodeURIComponent(String(infoType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (text !== undefined) {
            requestContext.setQueryParam("text", ObjectSerializer.serialize(text, "string", ""));
        }


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
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listPluginsByStatistic(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPluginsByStatistic", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPluginsByStatistic", "pageSize");
        }


        // verify required parameter 'pageNum' is not null or undefined
        if (pageNum === null || pageNum === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPluginsByStatistic", "pageNum");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/plugins/by/{statsType}/{pageSize}/{pageNum}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)))
            .replace('{' + 'pageNum' + '}', encodeURIComponent(String(pageNum)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listPluginsByStatistic1(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPluginsByStatistic1", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPluginsByStatistic1", "pageSize");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/plugins/by/{statsType}/{pageSize}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List plugins based on statistics, including interactive statistical data.
     * List Plugins by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listPluginsByStatistic2(statsType: string, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPluginsByStatistic2", "statsType");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/plugins/by/{statsType}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listPromptsByStatistic(statsType: string, pageSize: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPromptsByStatistic", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPromptsByStatistic", "pageSize");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/prompts/by/{statsType}/{pageSize}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param pageSize Maximum quantity
     * @param pageNum Current page number
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listPromptsByStatistic1(statsType: string, pageSize: number, pageNum: number, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPromptsByStatistic1", "statsType");
        }


        // verify required parameter 'pageSize' is not null or undefined
        if (pageSize === null || pageSize === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPromptsByStatistic1", "pageSize");
        }


        // verify required parameter 'pageNum' is not null or undefined
        if (pageNum === null || pageNum === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPromptsByStatistic1", "pageNum");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/prompts/by/{statsType}/{pageSize}/{pageNum}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)))
            .replace('{' + 'pageSize' + '}', encodeURIComponent(String(pageSize)))
            .replace('{' + 'pageNum' + '}', encodeURIComponent(String(pageNum)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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
     * List prompts based on statistics, including interactive statistical data.
     * List Prompts by Statistics
     * @param statsType Statistics type: view_count | refer_count | recommend_count | score
     * @param asc Default is descending order, set asc&#x3D;1 for ascending order
     */
    public async listPromptsByStatistic2(statsType: string, asc?: string, _options?: Configuration): Promise<RequestContext> {
        let _config = _options || this.configuration;

        // verify required parameter 'statsType' is not null or undefined
        if (statsType === null || statsType === undefined) {
            throw new RequiredError("InteractiveStatisticsApi", "listPromptsByStatistic2", "statsType");
        }



        // Path Params
        const localVarPath = '/api/v1/stats/prompts/by/{statsType}'
            .replace('{' + 'statsType' + '}', encodeURIComponent(String(statsType)));

        // Make Request Context
        const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
        requestContext.setHeaderParam("Accept", "application/json, */*;q=0.8")

        // Query Params
        if (asc !== undefined) {
            requestContext.setQueryParam("asc", ObjectSerializer.serialize(asc, "string", ""));
        }


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

export class InteractiveStatisticsApiResponseProcessor {

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to addStatistic
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async addStatisticWithHttpInfo(response: ResponseContext): Promise<HttpInfo<number >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getScore
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getScoreWithHttpInfo(response: ResponseContext): Promise<HttpInfo<number >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getStatistic
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getStatisticWithHttpInfo(response: ResponseContext): Promise<HttpInfo<number >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to getStatistics
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async getStatisticsWithHttpInfo(response: ResponseContext): Promise<HttpInfo<InteractiveStatsDTO >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: InteractiveStatsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "InteractiveStatsDTO", ""
            ) as InteractiveStatsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: InteractiveStatsDTO = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "InteractiveStatsDTO", ""
            ) as InteractiveStatsDTO;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to increaseStatistic
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async increaseStatisticWithHttpInfo(response: ResponseContext): Promise<HttpInfo<number >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: number = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "number", "int64"
            ) as number;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listCharactersByStatistic
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listCharactersByStatisticWithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<CharacterSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<CharacterSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<CharacterSummaryStatsDTO>", ""
            ) as Array<CharacterSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<CharacterSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<CharacterSummaryStatsDTO>", ""
            ) as Array<CharacterSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listCharactersByStatistic1
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listCharactersByStatistic1WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<CharacterSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<CharacterSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<CharacterSummaryStatsDTO>", ""
            ) as Array<CharacterSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<CharacterSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<CharacterSummaryStatsDTO>", ""
            ) as Array<CharacterSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listCharactersByStatistic2
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listCharactersByStatistic2WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<CharacterSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<CharacterSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<CharacterSummaryStatsDTO>", ""
            ) as Array<CharacterSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<CharacterSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<CharacterSummaryStatsDTO>", ""
            ) as Array<CharacterSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listFlowsByStatistic
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listFlowsByStatisticWithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<FlowSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<FlowSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<FlowSummaryStatsDTO>", ""
            ) as Array<FlowSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<FlowSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<FlowSummaryStatsDTO>", ""
            ) as Array<FlowSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listFlowsByStatistic1
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listFlowsByStatistic1WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<FlowSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<FlowSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<FlowSummaryStatsDTO>", ""
            ) as Array<FlowSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<FlowSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<FlowSummaryStatsDTO>", ""
            ) as Array<FlowSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listFlowsByStatistic2
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listFlowsByStatistic2WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<FlowSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<FlowSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<FlowSummaryStatsDTO>", ""
            ) as Array<FlowSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<FlowSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<FlowSummaryStatsDTO>", ""
            ) as Array<FlowSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listHotTags
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listHotTagsWithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<HotTagDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<HotTagDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<HotTagDTO>", ""
            ) as Array<HotTagDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<HotTagDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<HotTagDTO>", ""
            ) as Array<HotTagDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listPluginsByStatistic
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listPluginsByStatisticWithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<PluginSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<PluginSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PluginSummaryStatsDTO>", ""
            ) as Array<PluginSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<PluginSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PluginSummaryStatsDTO>", ""
            ) as Array<PluginSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listPluginsByStatistic1
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listPluginsByStatistic1WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<PluginSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<PluginSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PluginSummaryStatsDTO>", ""
            ) as Array<PluginSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<PluginSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PluginSummaryStatsDTO>", ""
            ) as Array<PluginSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listPluginsByStatistic2
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listPluginsByStatistic2WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<PluginSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<PluginSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PluginSummaryStatsDTO>", ""
            ) as Array<PluginSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<PluginSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PluginSummaryStatsDTO>", ""
            ) as Array<PluginSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listPromptsByStatistic
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listPromptsByStatisticWithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<PromptSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<PromptSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PromptSummaryStatsDTO>", ""
            ) as Array<PromptSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<PromptSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PromptSummaryStatsDTO>", ""
            ) as Array<PromptSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listPromptsByStatistic1
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listPromptsByStatistic1WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<PromptSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<PromptSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PromptSummaryStatsDTO>", ""
            ) as Array<PromptSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<PromptSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PromptSummaryStatsDTO>", ""
            ) as Array<PromptSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

    /**
     * Unwraps the actual response sent by the server from the response context and deserializes the response content
     * to the expected objects
     *
     * @params response Response returned by the server for a request to listPromptsByStatistic2
     * @throws ApiException if the response code was not in [200, 299]
     */
     public async listPromptsByStatistic2WithHttpInfo(response: ResponseContext): Promise<HttpInfo<Array<PromptSummaryStatsDTO> >> {
        const contentType = ObjectSerializer.normalizeMediaType(response.headers["content-type"]);
        if (isCodeInRange("200", response.httpStatusCode)) {
            const body: Array<PromptSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PromptSummaryStatsDTO>", ""
            ) as Array<PromptSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        // Work around for missing responses in specification, e.g. for petstore.yaml
        if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
            const body: Array<PromptSummaryStatsDTO> = ObjectSerializer.deserialize(
                ObjectSerializer.parse(await response.body.text(), contentType),
                "Array<PromptSummaryStatsDTO>", ""
            ) as Array<PromptSummaryStatsDTO>;
            return new HttpInfo(response.httpStatusCode, response.headers, response.body, body);
        }

        throw new ApiException<string | Blob | undefined>(response.httpStatusCode, "Unknown API Status Code!", await response.getBodyAsAny(), response.headers);
    }

}
