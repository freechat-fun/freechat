/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.1.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { AiModelInfoDTO } from '../models/AiModelInfoDTO';
import { HttpFile } from '../http/http';

/**
* Plugin template summary content, including interactive statistical information
*/
export class PluginSummaryStatsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Plugin identifier
    */
    'pluginId'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Visibility: private, public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Plugin name
    */
    'name'?: string;
    /**
    * Information of the provider
    */
    'provider'?: string;
    /**
    * Manifest format, currently supported: dash_scope, open_ai
    */
    'manifestFormat'?: string;
    /**
    * API description format, currently supported: openapi_v3
    */
    'apiFormat'?: string;
    /**
    * Plugin owner
    */
    'username'?: string;
    /**
    * Tag set
    */
    'tags'?: Array<string>;
    /**
    * Supported model set
    */
    'aiModels'?: Array<AiModelInfoDTO>;
    /**
    * View count
    */
    'viewCount'?: number;
    /**
    * Reference count
    */
    'referCount'?: number;
    /**
    * Recommendation count
    */
    'recommendCount'?: number;
    /**
    * Score count
    */
    'scoreCount'?: number;
    /**
    * Average score
    */
    'score'?: number;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
            "type": "string",
            "format": ""
        },
        {
            "name": "pluginId",
            "baseName": "pluginId",
            "type": "string",
            "format": ""
        },
        {
            "name": "gmtCreate",
            "baseName": "gmtCreate",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "gmtModified",
            "baseName": "gmtModified",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "visibility",
            "baseName": "visibility",
            "type": "string",
            "format": ""
        },
        {
            "name": "name",
            "baseName": "name",
            "type": "string",
            "format": ""
        },
        {
            "name": "provider",
            "baseName": "provider",
            "type": "string",
            "format": ""
        },
        {
            "name": "manifestFormat",
            "baseName": "manifestFormat",
            "type": "string",
            "format": ""
        },
        {
            "name": "apiFormat",
            "baseName": "apiFormat",
            "type": "string",
            "format": ""
        },
        {
            "name": "username",
            "baseName": "username",
            "type": "string",
            "format": ""
        },
        {
            "name": "tags",
            "baseName": "tags",
            "type": "Array<string>",
            "format": ""
        },
        {
            "name": "aiModels",
            "baseName": "aiModels",
            "type": "Array<AiModelInfoDTO>",
            "format": ""
        },
        {
            "name": "viewCount",
            "baseName": "viewCount",
            "type": "number",
            "format": "int64"
        },
        {
            "name": "referCount",
            "baseName": "referCount",
            "type": "number",
            "format": "int64"
        },
        {
            "name": "recommendCount",
            "baseName": "recommendCount",
            "type": "number",
            "format": "int64"
        },
        {
            "name": "scoreCount",
            "baseName": "scoreCount",
            "type": "number",
            "format": "int64"
        },
        {
            "name": "score",
            "baseName": "score",
            "type": "number",
            "format": "int64"
        }    ];

    static getAttributeTypeMap() {
        return PluginSummaryStatsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

