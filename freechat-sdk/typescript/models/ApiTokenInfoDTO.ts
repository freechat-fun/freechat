/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.9.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* API token information
*/
export class ApiTokenInfoDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Token identifier
    */
    'id'?: number;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Token type
    */
    'type'?: string;
    /**
    * Token issuance time
    */
    'issuedAt'?: Date;
    /**
    * Token expiration time
    */
    'expiresAt'?: Date;
    /**
    * Token content
    */
    'token'?: string;
    /**
    * Token policy
    */
    'policy'?: string;
    /**
    * Token owner
    */
    'username'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
            "type": "string",
            "format": ""
        },
        {
            "name": "id",
            "baseName": "id",
            "type": "number",
            "format": "int64"
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
            "name": "type",
            "baseName": "type",
            "type": "string",
            "format": ""
        },
        {
            "name": "issuedAt",
            "baseName": "issuedAt",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "expiresAt",
            "baseName": "expiresAt",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "token",
            "baseName": "token",
            "type": "string",
            "format": ""
        },
        {
            "name": "policy",
            "baseName": "policy",
            "type": "string",
            "format": ""
        },
        {
            "name": "username",
            "baseName": "username",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return ApiTokenInfoDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

