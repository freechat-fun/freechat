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
* Configuration information
*/
export class AppConfigInfoDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Configuration name
    */
    'name'?: string;
    /**
    * Configuration format: kv | json | yaml
    */
    'format'?: string;
    /**
    * Configuration content
    */
    'content'?: string;
    /**
    * Configuration version
    */
    'version'?: number;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
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
        },
        {
            "name": "version",
            "baseName": "version",
            "type": "number",
            "format": "int32"
        }    ];

    static getAttributeTypeMap() {
        return AppConfigInfoDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

