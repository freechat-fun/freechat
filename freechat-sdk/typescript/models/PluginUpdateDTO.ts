/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.6.2
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Request data for updating plugin information
*/
export class PluginUpdateDTO {
    /**
    * Visibility: private (default), public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Plugin name
    */
    'name': string;
    /**
    * Manifest format, currently supported: dash_scope (default), open_ai
    */
    'manifestFormat'?: string;
    /**
    * Manifest content, can be full content or a URL
    */
    'manifestInfo'?: string;
    /**
    * API description format, currently supported: openapi_v3 (default)
    */
    'apiFormat'?: string;
    /**
    * API description content, can be full content or a URL
    */
    'apiInfo'?: string;
    /**
    * Provider information, default is the current user\'s username
    */
    'provider'?: string;
    /**
    * Additional information, JSON format
    */
    'ext'?: string;
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
            "name": "manifestFormat",
            "baseName": "manifestFormat",
            "type": "string",
            "format": ""
        },
        {
            "name": "manifestInfo",
            "baseName": "manifestInfo",
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
            "name": "apiInfo",
            "baseName": "apiInfo",
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
            "name": "ext",
            "baseName": "ext",
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
        return PluginUpdateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

