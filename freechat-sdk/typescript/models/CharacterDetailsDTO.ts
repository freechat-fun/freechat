/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { CharacterBackendDetailsDTO } from '../models/CharacterBackendDetailsDTO.js';
import { CharacterInfoDraftDTO } from '../models/CharacterInfoDraftDTO.js';
import { HttpFile } from '../http/http.js';

/**
* Character detailed content
*/
export class CharacterDetailsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    /**
    * Character identifier
    */
    'characterId'?: string;
    /**
    * Creation time
    */
    'gmtCreate'?: Date;
    /**
    * Modification time
    */
    'gmtModified'?: Date;
    /**
    * Visibility: private, public, public_org, hidden
    */
    'visibility'?: string;
    /**
    * Version
    */
    'version'?: number;
    /**
    * Character name
    */
    'name': string;
    /**
    * Character description
    */
    'description'?: string;
    /**
    * Character avatar url
    */
    'avatar'?: string;
    /**
    * Character picture url
    */
    'picture'?: string;
    /**
    * Character language: English | Chinese (Simplified) | ...
    */
    'lang'?: string;
    /**
    * Character owner
    */
    'username'?: string;
    /**
    * Tag set
    */
    'tags'?: Array<string>;
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
    * Character experience
    */
    'experience'?: string;
    /**
    * Additional information, JSON format
    */
    'ext'?: string;
    'draft'?: CharacterInfoDraftDTO;
    /**
    * Character backends information
    */
    'backends'?: Array<CharacterBackendDetailsDTO>;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
            "type": "string",
            "format": ""
        },
        {
            "name": "characterId",
            "baseName": "characterId",
            "type": "string",
            "format": ""
        },
        {
            "name": "gmtCreate",
            "baseName": "gmtCreate",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "gmtModified",
            "baseName": "gmtModified",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "visibility",
            "baseName": "visibility",
            "type": "string",
            "format": ""
        },
        {
            "name": "version",
            "baseName": "version",
            "type": "number",
            "format": "int32"
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
            "name": "lang",
            "baseName": "lang",
            "type": "string",
            "format": ""
        },
        {
            "name": "username",
            "baseName": "username",
            "type": "string",
            "format": ""
        },
        {
            "name": "tags",
            "baseName": "tags",
            "type": "Array<string>",
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
            "name": "experience",
            "baseName": "experience",
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
            "type": "CharacterInfoDraftDTO",
            "format": ""
        },
        {
            "name": "backends",
            "baseName": "backends",
            "type": "Array<CharacterBackendDetailsDTO>",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return CharacterDetailsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

