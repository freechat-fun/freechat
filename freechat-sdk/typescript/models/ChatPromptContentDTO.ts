/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.7.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { ChatMessageDTO } from '../models/ChatMessageDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Prompt chat template content
*/
export class ChatPromptContentDTO {
    /**
    * Prompt system template
    */
    'system'?: string;
    'messageToSend'?: ChatMessageDTO;
    /**
    * Pre-set chat messages in the Prompt
    */
    'messages'?: Array<ChatMessageDTO>;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "system",
            "baseName": "system",
            "type": "string",
            "format": ""
        },
        {
            "name": "messageToSend",
            "baseName": "messageToSend",
            "type": "ChatMessageDTO",
            "format": ""
        },
        {
            "name": "messages",
            "baseName": "messages",
            "type": "Array<ChatMessageDTO>",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return ChatPromptContentDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

