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

export class SseEmitter {
    'timeout'?: number;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "timeout",
            "baseName": "timeout",
            "type": "number",
            "format": "int64"
        }    ];

    static getAttributeTypeMap() {
        return SseEmitter.attributeTypeMap;
    }

    public constructor() {
    }
}

