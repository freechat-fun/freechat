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
* Query condition
*/
export class AgentQueryWhere {
    /**
    * Visibility: public, public_org (search this organization), private (default)
    */
    'visibility'?: string;
    /**
    * Effective when searching public, public_org prompts, if not specified, search all users
    */
    'username'?: string;
    /**
    * Agent configuration format, currently supported: langflow.
    */
    'format'?: string;
    /**
    * Tags
    */
    'tags'?: Array<string>;
    /**
    * Relationship between tags: and | or (default)
    */
    'tagsOp'?: string;
    /**
    * Model set
    */
    'aiModels'?: Array<string>;
    /**
    * Relationship between model sets: and | or (default)
    */
    'aiModelsOp'?: string;
    /**
    * Name, left match
    */
    'name'?: string;
    /**
    * Name, description, example, fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time.
    */
    'text'?: string;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "visibility",
            "baseName": "visibility",
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
            "name": "format",
            "baseName": "format",
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
            "name": "tagsOp",
            "baseName": "tagsOp",
            "type": "string",
            "format": ""
        },
        {
            "name": "aiModels",
            "baseName": "aiModels",
            "type": "Array<string>",
            "format": ""
        },
        {
            "name": "aiModelsOp",
            "baseName": "aiModelsOp",
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
            "name": "text",
            "baseName": "text",
            "type": "string",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return AgentQueryWhere.attributeTypeMap;
    }

    public constructor() {
    }
}
