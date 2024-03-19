/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.4.2
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { AiModelInfoDTO } from '../models/AiModelInfoDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Plugin detailed content
*/
export class PluginDetailsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Plugin identifier
    */
    'pluginId'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Visibility: private, public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Plugin name
    */
    'name'?: string;
    /**
    * Information of the provider
    */
    'provider'?: string;
    /**
    * Manifest format, currently supported: dash_scope, open_ai
    */
    'manifestFormat'?: string;
    /**
    * API description format, currently supported: openapi_v3
    */
    'apiFormat'?: string;
    /**
    * Plugin owner
    */
    'username'?: string;
    /**
    * Tag set
    */
    'tags'?: Array<string>;
    /**
    * Supported model set
    */
    'aiModels'?: Array<AiModelInfoDTO>;
    /**
    * Manifest content, different formats may have differences
    */
    'manifestInfo'?: string;
    /**
    * API description content, different formats may have content differences
    */
    'apiInfo'?: string;
    /**
    * Tool specification format, currently supported: open_ai
    */
    'toolSpecFormat'?: string;
    /**
    * Tool specification content
    */
    'toolSpecInfo'?: string;
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
            "name": "pluginId",
            "baseName": "pluginId",
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
            "name": "visibility",
            "baseName": "visibility",
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
            "name": "provider",
            "baseName": "provider",
            "type": "string",
            "format": ""
        },
        {
            "name": "manifestFormat",
            "baseName": "manifestFormat",
            "type": "string",
            "format": ""
        },
        {
            "name": "apiFormat",
            "baseName": "apiFormat",
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
            "name": "tags",
            "baseName": "tags",
            "type": "Array<string>",
            "format": ""
        },
        {
            "name": "aiModels",
            "baseName": "aiModels",
            "type": "Array<AiModelInfoDTO>",
            "format": ""
        },
        {
            "name": "manifestInfo",
            "baseName": "manifestInfo",
            "type": "string",
            "format": ""
        },
        {
            "name": "apiInfo",
            "baseName": "apiInfo",
            "type": "string",
            "format": ""
        },
        {
            "name": "toolSpecFormat",
            "baseName": "toolSpecFormat",
            "type": "string",
            "format": ""
        },
        {
            "name": "toolSpecInfo",
            "baseName": "toolSpecInfo",
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
        return PluginDetailsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

