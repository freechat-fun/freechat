/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.6
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { ChatToolCallDTO } from '../models/ChatToolCallDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Chat message
*/
export class ChatMessageDTO {
    /**
    * Chat role: system | assistant | user | function_call | function_result
    */
    'role'?: string;
    /**
    * user: Name of the user role; function_call: Name of the called tool
    */
    'name'?: string;
    /**
    * default: Dialogue content; function_result: function call result, serialized as json
    */
    'content'?: string;
    /**
    * Tool calls information during the conversation
    */
    'toolCalls'?: Array<ChatToolCallDTO>;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;

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
            "name": "content",
            "baseName": "content",
            "type": "string",
            "format": ""
        },
        {
            "name": "toolCalls",
            "baseName": "toolCalls",
            "type": "Array<ChatToolCallDTO>",
            "format": ""
        },
        {
            "name": "gmtCreate",
            "baseName": "gmtCreate",
            "type": "Date",
            "format": "date-time"
        }    ];

    static getAttributeTypeMap() {
        return ChatMessageDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

