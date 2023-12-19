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
* Chat content
*/
export class ChatContentDTO {
    /**
    * Chat content.
    */
    'content': string;
    /**
    * Chat attachment.
    */
    'attachment'?: string;
    /**
    * Contextual information the character should know in this round of conversation
    */
    'context'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "content",
            "baseName": "content",
            "type": "string",
            "format": ""
        },
        {
            "name": "attachment",
            "baseName": "attachment",
            "type": "string",
            "format": ""
        },
        {
            "name": "context",
            "baseName": "context",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return ChatContentDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

