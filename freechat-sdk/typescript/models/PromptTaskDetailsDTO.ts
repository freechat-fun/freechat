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

import { PromptRefDTO } from '../models/PromptRefDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Prompt task detailed information
*/
export class PromptTaskDetailsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Prompt task identifier
    */
    'taskId'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Task start execution time
    */
    'gmtStart'?: Date;
    /**
    * Task end execution time
    */
    'gmtEnd'?: Date;
    'promptRef'?: PromptRefDTO;
    /**
    * Model identifier
    */
    'modelId'?: string;
    /**
    * API-KEY name
    */
    'apiKeyName'?: string;
    /**
    * API-KEY value
    */
    'apiKeyValue'?: string;
    /**
    * Model call parameters
    */
    'params'?: { [key: string]: any; };
    /**
    * Task scheduling configuration which compatible with Quartz cron format
    */
    'cron'?: string;
    /**
    * Task execution status: pending | running | succeeded | failed | canceled
    */
    'status'?: string;
    /**
    * Additional information, JSON format
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
            "name": "taskId",
            "baseName": "taskId",
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
            "name": "gmtStart",
            "baseName": "gmtStart",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "gmtEnd",
            "baseName": "gmtEnd",
            "type": "Date",
            "format": "date-time"
        },
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
            "name": "apiKeyValue",
            "baseName": "apiKeyValue",
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
        },
        {
            "name": "ext",
            "baseName": "ext",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return PromptTaskDetailsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

