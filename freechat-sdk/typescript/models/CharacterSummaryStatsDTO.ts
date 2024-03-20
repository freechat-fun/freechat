/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.5.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Character summary content, including interactive statistical information
*/
export class CharacterSummaryStatsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Character identifier, will change after publish
    */
    'characterId'?: number;
    /**
    * Character immutable identifier
    */
    'characterUid'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Referenced character
    */
    'parentUid'?: string;
    /**
    * Visibility: private, public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Version
    */
    'version'?: number;
    /**
    * Character name
    */
    'name'?: string;
    /**
    * Character description
    */
    'description'?: string;
    /**
    * Character nickname
    */
    'nickname'?: string;
    /**
    * Character avatar url
    */
    'avatar'?: string;
    /**
    * Character picture url
    */
    'picture'?: string;
    /**
    * Character gender: male | female | other
    */
    'gender'?: string;
    /**
    * Character language: en (default) | zh | ...
    */
    'lang'?: string;
    /**
    * Character owner
    */
    'username'?: string;
    /**
    * Tag set
    */
    'tags'?: Array<string>;
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
            "name": "characterId",
            "baseName": "characterId",
            "type": "number",
            "format": "int64"
        },
        {
            "name": "characterUid",
            "baseName": "characterUid",
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
            "name": "parentUid",
            "baseName": "parentUid",
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
            "name": "nickname",
            "baseName": "nickname",
            "type": "string",
            "format": ""
        },
        {
            "name": "avatar",
            "baseName": "avatar",
            "type": "string",
            "format": ""
        },
        {
            "name": "picture",
            "baseName": "picture",
            "type": "string",
            "format": ""
        },
        {
            "name": "gender",
            "baseName": "gender",
            "type": "string",
            "format": ""
        },
        {
            "name": "lang",
            "baseName": "lang",
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
        return CharacterSummaryStatsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

