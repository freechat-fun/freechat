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

import { PromptRefDTO } from '../models/PromptRefDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Prompt task information
*/
export class PromptTaskDTO {
    'promptRef': PromptRefDTO;
    /**
    * Model identifier
    */
    'modelId'?: string;
    /**
    * API-KEY name
    */
    'apiKeyName'?: string;
    /**
    * Model call parameters
    */
    'params'?: { [key: string]: any; };
    /**
    * Task scheduling configuration which compatible with Quartz cron format
    */
    'cron'?: string;
    /**
    * Task execution status: pending | running | succeeded | failed
    */
    'status'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "promptRef",
            "baseName": "promptRef",
            "type": "PromptRefDTO",
            "format": ""
        },
        {
            "name": "modelId",
            "baseName": "modelId",
            "type": "string",
            "format": ""
        },
        {
            "name": "apiKeyName",
            "baseName": "apiKeyName",
            "type": "string",
            "format": ""
        },
        {
            "name": "params",
            "baseName": "params",
            "type": "{ [key: string]: any; }",
            "format": ""
        },
        {
            "name": "cron",
            "baseName": "cron",
            "type": "string",
            "format": ""
        },
        {
            "name": "status",
            "baseName": "status",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return PromptTaskDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

