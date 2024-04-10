/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.8.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Request data for creating new character information
*/
export class CharacterCreateDTO {
    /**
    * Referenced character
    */
    'parentUid'?: string;
    /**
    * Visibility: private (default), public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Character name
    */
    'name': string;
    /**
    * Character description
    */
    'description'?: string;
    /**
    * Character nickname
    */
    'nickname'?: string;
    /**
    * Character avatar url
    */
    'avatar'?: string;
    /**
    * Character picture url
    */
    'picture'?: string;
    /**
    * Character gender: male | female | other
    */
    'gender'?: string;
    /**
    * Character profile
    */
    'profile'?: string;
    /**
    * Character greeting
    */
    'greeting'?: string;
    /**
    * Character chat-style
    */
    'chatStyle'?: string;
    /**
    * Character chat-example
    */
    'chatExample'?: string;
    /**
    * Character language: en (default) | zh | ...
    */
    'lang'?: string;
    /**
    * Additional information, JSON format
    */
    'ext'?: string;
    /**
    * Character draft information
    */
    'draft'?: string;
    /**
    * Tag set
    */
    'tags'?: Array<string>;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "parentUid",
            "baseName": "parentUid",
            "type": "string",
            "format": ""
        },
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
            "name": "description",
            "baseName": "description",
            "type": "string",
            "format": ""
        },
        {
            "name": "nickname",
            "baseName": "nickname",
            "type": "string",
            "format": ""
        },
        {
            "name": "avatar",
            "baseName": "avatar",
            "type": "string",
            "format": ""
        },
        {
            "name": "picture",
            "baseName": "picture",
            "type": "string",
            "format": ""
        },
        {
            "name": "gender",
            "baseName": "gender",
            "type": "string",
            "format": ""
        },
        {
            "name": "profile",
            "baseName": "profile",
            "type": "string",
            "format": ""
        },
        {
            "name": "greeting",
            "baseName": "greeting",
            "type": "string",
            "format": ""
        },
        {
            "name": "chatStyle",
            "baseName": "chatStyle",
            "type": "string",
            "format": ""
        },
        {
            "name": "chatExample",
            "baseName": "chatExample",
            "type": "string",
            "format": ""
        },
        {
            "name": "lang",
            "baseName": "lang",
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
            "name": "draft",
            "baseName": "draft",
            "type": "string",
            "format": ""
        },
        {
            "name": "tags",
            "baseName": "tags",
            "type": "Array<string>",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return CharacterCreateDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

