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

import { ChatContentDTO } from '../models/ChatContentDTO.js';
import { ChatToolCallDTO } from '../models/ChatToolCallDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Chat message
*/
export class ChatMessageDTO {
    /**
    * Chat role: system | assistant | user | tool_call | tool_result
    */
    'role'?: string;
    /**
    * user: Name of the user role; tool_call: Name of the called tool
    */
    'name'?: string;
    /**
    * default: Dialogue content; tool_result: tool call result, serialized as json
    */
    'contents'?: Array<ChatContentDTO>;
    /**
    * Tool calls information during the conversation
    */
    'toolCalls'?: Array<ChatToolCallDTO>;
    /**
    * Contextual information in this round of conversation (the external RAG result can be passed in through this parameter)
    */
    'context'?: string;
    'contentText'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "role",
            "baseName": "role",
            "type": "string",
            "format": ""
        },
        {
            "name": "name",
            "baseName": "name",
            "type": "string",
            "format": ""
        },
        {
            "name": "contents",
            "baseName": "contents",
            "type": "Array<ChatContentDTO>",
            "format": ""
        },
        {
            "name": "toolCalls",
            "baseName": "toolCalls",
            "type": "Array<ChatToolCallDTO>",
            "format": ""
        },
        {
            "name": "context",
            "baseName": "context",
            "type": "string",
            "format": ""
        },
        {
            "name": "contentText",
            "baseName": "contentText",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return ChatMessageDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

