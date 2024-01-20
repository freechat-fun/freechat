/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.11
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Request data for adding new model credential information
*/
export class AiApiKeyCreateDTO {
    /**
    * Credential name
    */
    'name': string;
    /**
    * Model provider: hugging_face | open_ai | local_ai | in_process | dash_scope | unknown
    */
    'provider': string;
    /**
    * Credential content
    */
    'token': string;
    /**
    * Whether to enable (enabled by default)
    */
    'enabled'?: boolean;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
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
            "name": "token",
            "baseName": "token",
            "type": "string",
            "format": ""
        },
        {
            "name": "enabled",
            "baseName": "enabled",
            "type": "boolean",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return AiApiKeyCreateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

