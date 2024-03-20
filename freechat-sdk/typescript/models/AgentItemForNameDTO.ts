/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.5.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { InteractiveStatsDTO } from '../models/InteractiveStatsDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Agent identifier and version information
*/
export class AgentItemForNameDTO {
    /**
    * agentId
    */
    'agentId'?: number;
    /**
    * version
    */
    'version'?: number;
    'stats'?: InteractiveStatsDTO;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "agentId",
            "baseName": "agentId",
            "type": "number",
            "format": "int64"
        },
        {
            "name": "version",
            "baseName": "version",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "stats",
            "baseName": "stats",
            "type": "InteractiveStatsDTO",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return AgentItemForNameDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

