/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.2.11
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Account detailed information
*/
export class UserDetailsDTO {
    /**
    * Request identifier
    */
    'requestId'?: string;
    'username'?: string;
    'nickname'?: string;
    'givenName'?: string;
    'middleName'?: string;
    'familyName'?: string;
    'preferredUsername'?: string;
    'profile'?: string;
    'picture'?: string;
    'website'?: string;
    'email'?: string;
    'gender'?: string;
    'birthdate'?: Date;
    'zoneinfo'?: string;
    'locale'?: string;
    'phoneNumber'?: string;
    'updatedAt'?: Date;
    'platform'?: string;
    'enabled'?: boolean;
    'locked'?: boolean;
    'expiresAt'?: Date;
    'passwordExpiresAt'?: Date;
    'address'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "requestId",
            "baseName": "requestId",
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
            "name": "nickname",
            "baseName": "nickname",
            "type": "string",
            "format": ""
        },
        {
            "name": "givenName",
            "baseName": "givenName",
            "type": "string",
            "format": ""
        },
        {
            "name": "middleName",
            "baseName": "middleName",
            "type": "string",
            "format": ""
        },
        {
            "name": "familyName",
            "baseName": "familyName",
            "type": "string",
            "format": ""
        },
        {
            "name": "preferredUsername",
            "baseName": "preferredUsername",
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
            "name": "picture",
            "baseName": "picture",
            "type": "string",
            "format": ""
        },
        {
            "name": "website",
            "baseName": "website",
            "type": "string",
            "format": ""
        },
        {
            "name": "email",
            "baseName": "email",
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
            "name": "birthdate",
            "baseName": "birthdate",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "zoneinfo",
            "baseName": "zoneinfo",
            "type": "string",
            "format": ""
        },
        {
            "name": "locale",
            "baseName": "locale",
            "type": "string",
            "format": ""
        },
        {
            "name": "phoneNumber",
            "baseName": "phoneNumber",
            "type": "string",
            "format": ""
        },
        {
            "name": "updatedAt",
            "baseName": "updatedAt",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "platform",
            "baseName": "platform",
            "type": "string",
            "format": ""
        },
        {
            "name": "enabled",
            "baseName": "enabled",
            "type": "boolean",
            "format": ""
        },
        {
            "name": "locked",
            "baseName": "locked",
            "type": "boolean",
            "format": ""
        },
        {
            "name": "expiresAt",
            "baseName": "expiresAt",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "passwordExpiresAt",
            "baseName": "passwordExpiresAt",
            "type": "Date",
            "format": "date-time"
        },
        {
            "name": "address",
            "baseName": "address",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return UserDetailsDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

