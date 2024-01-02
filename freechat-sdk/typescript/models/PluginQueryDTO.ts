/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.6
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { PluginQueryWhere } from '../models/PluginQueryWhere.js';
import { HttpFile } from '../http/http.js';

/**
* Plugin information query request
*/
export class PluginQueryDTO {
    'where'?: PluginQueryWhere;
    /**
    * Ordering condition, supported sorting fields are: - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\"score\\\", \\\"scoreCount asc\\\"] (scoreCount in ascending order) 
    */
    'orderBy'?: Array<string>;
    /**
    * Page number, default is 0
    */
    'pageNum'?: number;
    /**
    * Page item count, 1～50, default is 10
    */
    'pageSize'?: number;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "where",
            "baseName": "where",
            "type": "PluginQueryWhere",
            "format": ""
        },
        {
            "name": "orderBy",
            "baseName": "orderBy",
            "type": "Array<string>",
            "format": ""
        },
        {
            "name": "pageNum",
            "baseName": "pageNum",
            "type": "number",
            "format": "int64"
        },
        {
            "name": "pageSize",
            "baseName": "pageSize",
            "type": "number",
            "format": "int64"
        }    ];

    static getAttributeTypeMap() {
        return PluginQueryDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

