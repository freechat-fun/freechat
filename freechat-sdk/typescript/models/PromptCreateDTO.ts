/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.4.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { ChatPromptContentDTO } from '../models/ChatPromptContentDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Request data for creating new prompt information
*/
export class PromptCreateDTO {
    /**
    * Referenced prompt
    */
    'parentId'?: string;
    /**
    * Visibility: private (default), public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Prompt name
    */
    'name': string;
    /**
    * Prompt description
    */
    'description'?: string;
    /**
    * Prompt text template content, choose one from template and chatTemplate field, priority: template > chatTemplate
    */
    'template'?: string;
    'chatTemplate'?: ChatPromptContentDTO;
    /**
    * Prompt format: mustache (default) | f_string
    */
    'format'?: string;
    /**
    * Prompt language: en (default) | zh_CN | ...
    */
    'lang'?: string;
    /**
    * Prompt example
    */
    'example'?: string;
    /**
    * Prompt parameters, JSON format
    */
    'inputs'?: string;
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
            "name": "parentId",
            "baseName": "parentId",
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
            "name": "template",
            "baseName": "template",
            "type": "string",
            "format": ""
        },
        {
            "name": "chatTemplate",
            "baseName": "chatTemplate",
            "type": "ChatPromptContentDTO",
            "format": ""
        },
        {
            "name": "format",
            "baseName": "format",
            "type": "string",
            "format": ""
        },
        {
            "name": "lang",
            "baseName": "lang",
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
            "name": "inputs",
            "baseName": "inputs",
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
        return PromptCreateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

