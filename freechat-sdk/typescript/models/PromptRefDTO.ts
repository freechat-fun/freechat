/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.1.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http';

/**
* Prompt reference information
*/
export class PromptRefDTO {
    /**
    * Prompt identifier
    */
    'promptId': string;
    /**
    * Variables applied to the template, can be empty
    */
    'variables'?: { [key: string]: any; };
    /**
    * Whether to use draft content
    */
    'draft'?: boolean;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "promptId",
            "baseName": "promptId",
            "type": "string",
            "format": ""
        },
        {
            "name": "variables",
            "baseName": "variables",
            "type": "{ [key: string]: any; }",
            "format": ""
        },
        {
            "name": "draft",
            "baseName": "draft",
            "type": "boolean",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return PromptRefDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

