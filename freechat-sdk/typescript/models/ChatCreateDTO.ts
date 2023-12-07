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

import { HttpFile } from '../http/http';

/**
* Request data for starting a chat session
*/
export class ChatCreateDTO {
    /**
    * User nickname for this session.
    */
    'userNickname'?: string;
    /**
    * User profile for this session.
    */
    'userProfile'?: string;
    /**
    * Character nickname for this session.
    */
    'characterNickname'?: string;
    /**
    * Character backend for this session.
    */
    'backendId': string;
    /**
    * Extra info for this session.
    */
    'ext'?: string;

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
        return ChatCreateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

