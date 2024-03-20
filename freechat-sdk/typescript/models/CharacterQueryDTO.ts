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

import { CharacterQueryWhere } from '../models/CharacterQueryWhere.js';
import { HttpFile } from '../http/http.js';

/**
* Character query request
*/
export class CharacterQueryDTO {
    'where'?: CharacterQueryWhere;
    /**
    * Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\"score\\\", \\\"scoreCount asc\\\"] (scoreCount in ascending order) 
    */
    'orderBy'?: Array<string>;
    /**
    * Page number, default is 0
    */
    'pageNum'?: number;
    /**
    * Number of items per page, 1～50, default is 10
    */
    'pageSize'?: number;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "where",
            "baseName": "where",
            "type": "CharacterQueryWhere",
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
        return CharacterQueryDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

