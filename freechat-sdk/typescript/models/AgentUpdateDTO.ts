/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.6.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Request data for updating agent information
*/
export class AgentUpdateDTO {
    /**
    * Referenced agent
    */
    'parentUid'?: string;
    /**
    * Visibility: private (default), public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Agent format, currently supported: langflow
    */
    'format'?: string;
    /**
    * Agent name
    */
    'name': string;
    /**
    * Agent description
    */
    'description'?: string;
    /**
    * Agent configuration
    */
    'config'?: string;
    /**
    * Agent example
    */
    'example'?: string;
    /**
    * Agent parameters, JSON format
    */
    'parameters'?: string;
    /**
    * Additional information, JSON format
    */
    'ext'?: string;
    /**
    * Draft content
    */
    'draft'?: string;
    /**
    * Tag set
    */
    'tags'?: Array<string>;
    /**
    * Supported model set, empty means no model limit
    */
    'aiModels'?: Array<string>;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "parentUid",
            "baseName": "parentUid",
            "type": "string",
            "format": ""
        },
        {
            "name": "visibility",
            "baseName": "visibility",
            "type": "string",
            "format": ""
        },
        {
            "name": "format",
            "baseName": "format",
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
            "name": "description",
            "baseName": "description",
            "type": "string",
            "format": ""
        },
        {
            "name": "config",
            "baseName": "config",
            "type": "string",
            "format": ""
        },
        {
            "name": "example",
            "baseName": "example",
            "type": "string",
            "format": ""
        },
        {
            "name": "parameters",
            "baseName": "parameters",
            "type": "string",
            "format": ""
        },
        {
            "name": "ext",
            "baseName": "ext",
            "type": "string",
            "format": ""
        },
        {
            "name": "draft",
            "baseName": "draft",
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
            "type": "Array<string>",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return AgentUpdateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

