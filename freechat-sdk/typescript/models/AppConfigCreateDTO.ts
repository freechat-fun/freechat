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
* Configuration creation information
*/
export class AppConfigCreateDTO {
    /**
    * Configuration name
    */
    'name': string;
    /**
    * Configuration format: kv | json | yaml
    */
    'format'?: string;
    /**
    * Configuration content
    */
    'content'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "name",
            "baseName": "name",
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
            "name": "content",
            "baseName": "content",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return AppConfigCreateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

