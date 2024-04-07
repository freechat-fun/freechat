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

import { HttpFile } from '../http/http.js';

/**
* Character backend information
*/
export class CharacterBackendDTO {
    /**
    * Whether it is the default backend
    */
    'isDefault'?: boolean;
    /**
    * Prompt task identifier for chat
    */
    'chatPromptTaskId'?: string;
    /**
    * Prompt task identifier for greeting
    */
    'greetingPromptTaskId'?: string;
    /**
    * Moderation model for the character\'s response
    */
    'moderationModelId'?: string;
    /**
    * Api key name for moderation model
    */
    'moderationApiKeyName'?: string;
    /**
    * Parameters for moderation model
    */
    'moderationParams'?: string;
    /**
    * Max messages in the character\'s memory
    */
    'messageWindowSize'?: number;
    /**
    * Weather to forward messages to the character owner
    */
    'forwardToUser'?: boolean;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "isDefault",
            "baseName": "isDefault",
            "type": "boolean",
            "format": ""
        },
        {
            "name": "chatPromptTaskId",
            "baseName": "chatPromptTaskId",
            "type": "string",
            "format": ""
        },
        {
            "name": "greetingPromptTaskId",
            "baseName": "greetingPromptTaskId",
            "type": "string",
            "format": ""
        },
        {
            "name": "moderationModelId",
            "baseName": "moderationModelId",
            "type": "string",
            "format": ""
        },
        {
            "name": "moderationApiKeyName",
            "baseName": "moderationApiKeyName",
            "type": "string",
            "format": ""
        },
        {
            "name": "moderationParams",
            "baseName": "moderationParams",
            "type": "string",
            "format": ""
        },
        {
            "name": "messageWindowSize",
            "baseName": "messageWindowSize",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "forwardToUser",
            "baseName": "forwardToUser",
            "type": "boolean",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return CharacterBackendDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

