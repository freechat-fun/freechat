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
* Chat context
*/
export class ChatContextDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Chat identifier
    */
    'chatId'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Read time
    */
    'gmtRead'?: Date;
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
    * Character backend for this session
    */
    'backendId': string;
    /**
    * Extra info for this session
    */
    'ext'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
            "type": "string",
            "format": ""
        },
        {
            "name": "chatId",
            "baseName": "chatId",
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
            "name": "gmtRead",
            "baseName": "gmtRead",
            "type": "Date",
            "format": "date-time"
        },
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
        }    ];

    static getAttributeTypeMap() {
        return ChatContextDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

