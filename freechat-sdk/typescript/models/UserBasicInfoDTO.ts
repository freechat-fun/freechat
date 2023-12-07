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
* User summary information
*/
export class UserBasicInfoDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Username
    */
    'username'?: string;
    /**
    * Nickname
    */
    'nickname'?: string;
    /**
    * Avatar
    */
    'picture'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
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
            "name": "nickname",
            "baseName": "nickname",
            "type": "string",
            "format": ""
        },
        {
            "name": "picture",
            "baseName": "picture",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return UserBasicInfoDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

