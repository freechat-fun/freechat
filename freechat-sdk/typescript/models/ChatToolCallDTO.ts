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
* Tool call information during the conversation
*/
export class ChatToolCallDTO {
    /**
    * Tool name
    */
    'name'?: string;
    /**
    * Tool parameters
    */
    'arguments'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "name",
            "baseName": "name",
            "type": "string",
            "format": ""
        },
        {
            "name": "arguments",
            "baseName": "arguments",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return ChatToolCallDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

