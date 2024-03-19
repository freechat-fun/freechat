/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.4.2
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Request data for updating a chat session
*/
export class ChatUpdateDTO {
    /**
    * User nickname for this session
    */
    'userNickname'?: string;
    /**
    * User profile for this session
    */
    'userProfile'?: string;
    /**
    * Character nickname for this session
    */
    'characterNickname'?: string;
    /**
    * Anything about this session
    */
    'about'?: string;
    /**
    * Character id for this session
    */
    'characterId': string;
    /**
    * Character backend for this session
    */
    'backendId'?: string;
    /**
    * Extra info for this session
    */
    'ext'?: string;
    /**
    * Read time
    */
    'gmtRead'?: Date;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "userNickname",
            "baseName": "userNickname",
            "type": "string",
            "format": ""
        },
        {
            "name": "userProfile",
            "baseName": "userProfile",
            "type": "string",
            "format": ""
        },
        {
            "name": "characterNickname",
            "baseName": "characterNickname",
            "type": "string",
            "format": ""
        },
        {
            "name": "about",
            "baseName": "about",
            "type": "string",
            "format": ""
        },
        {
            "name": "characterId",
            "baseName": "characterId",
            "type": "string",
            "format": ""
        },
        {
            "name": "backendId",
            "baseName": "backendId",
            "type": "string",
            "format": ""
        },
        {
            "name": "ext",
            "baseName": "ext",
            "type": "string",
            "format": ""
        },
        {
            "name": "gmtRead",
            "baseName": "gmtRead",
            "type": "Date",
            "format": "date-time"
        }    ];

    static getAttributeTypeMap() {
        return ChatUpdateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

