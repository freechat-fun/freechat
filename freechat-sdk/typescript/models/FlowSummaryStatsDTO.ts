/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.11
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { AiModelInfoDTO } from '../models/AiModelInfoDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Flow template summary content, including interactive statistical information
*/
export class FlowSummaryStatsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Flow identifier
    */
    'flowId'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Referenced flow
    */
    'parentId'?: string;
    /**
    * Visibility: private, public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Flow format, currently supported: langflow
    */
    'format'?: string;
    /**
    * Version
    */
    'version'?: number;
    /**
    * Flow name
    */
    'name'?: string;
    /**
    * Flow description
    */
    'description'?: string;
    /**
    * Flow owner
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
            "name": "flowId",
            "baseName": "flowId",
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
            "name": "parentId",
            "baseName": "parentId",
            "type": "string",
            "format": ""
        },
        {
            "name": "visibility",
            "baseName": "visibility",
            "type": "string",
            "format": ""
        },
        {
            "name": "format",
            "baseName": "format",
            "type": "string",
            "format": ""
        },
        {
            "name": "version",
            "baseName": "version",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "name",
            "baseName": "name",
            "type": "string",
            "format": ""
        },
        {
            "name": "description",
            "baseName": "description",
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
        return FlowSummaryStatsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

