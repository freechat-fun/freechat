/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.3
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Interactive statistics information
*/
export class InteractiveStatsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Resource type
    */
    'referType'?: string;
    /**
    * Resource identifier
    */
    'referId'?: string;
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
            "name": "referType",
            "baseName": "referType",
            "type": "string",
            "format": ""
        },
        {
            "name": "referId",
            "baseName": "referId",
            "type": "string",
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
        return InteractiveStatsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

