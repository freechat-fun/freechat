/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.15
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Model information
*/
export class AiModelInfoDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Model identifier: [provider]name
    */
    'modelId'?: string;
    /**
    * Model name
    */
    'name'?: string;
    /**
    * Model description
    */
    'description'?: string;
    /**
    * Model provider: hugging_face | open_ai | local_ai | in_process | dash_scope | unknown
    */
    'provider'?: string;
    /**
    * Model type: text2text | text2chat | text2image | embedding | moderation
    */
    'type'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
            "type": "string",
            "format": ""
        },
        {
            "name": "modelId",
            "baseName": "modelId",
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
            "name": "provider",
            "baseName": "provider",
            "type": "string",
            "format": ""
        },
        {
            "name": "type",
            "baseName": "type",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return AiModelInfoDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

