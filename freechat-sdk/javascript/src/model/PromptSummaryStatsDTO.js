/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.1.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 *
 */

import ApiClient from '../ApiClient';
import AiModelInfoDTO from './AiModelInfoDTO';

/**
 * The PromptSummaryStatsDTO model module.
 * @module model/PromptSummaryStatsDTO
 * @version 0.1.0
 */
class PromptSummaryStatsDTO {
    /**
     * Constructs a new <code>PromptSummaryStatsDTO</code>.
     * Prompt template summary content, including interactive statistical information
     * @alias module:model/PromptSummaryStatsDTO
     */
    constructor() { 
        
        PromptSummaryStatsDTO.initialize(this);
    }

    /**
     * Initializes the fields of this object.
     * This method is used by the constructors of any subclasses, in order to implement multiple inheritance (mix-ins).
     * Only for internal use.
     */
    static initialize(obj) { 
    }

    /**
     * Constructs a <code>PromptSummaryStatsDTO</code> from a plain JavaScript object, optionally creating a new instance.
     * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @param {module:model/PromptSummaryStatsDTO} obj Optional instance to populate.
     * @return {module:model/PromptSummaryStatsDTO} The populated <code>PromptSummaryStatsDTO</code> instance.
     */
    static constructFromObject(data, obj) {
        if (data) {
            obj = obj || new PromptSummaryStatsDTO();

            if (data.hasOwnProperty('requestId')) {
                obj['requestId'] = ApiClient.convertToType(data['requestId'], 'String');
            }
            if (data.hasOwnProperty('promptId')) {
                obj['promptId'] = ApiClient.convertToType(data['promptId'], 'String');
            }
            if (data.hasOwnProperty('gmtCreate')) {
                obj['gmtCreate'] = ApiClient.convertToType(data['gmtCreate'], 'Date');
            }
            if (data.hasOwnProperty('gmtModified')) {
                obj['gmtModified'] = ApiClient.convertToType(data['gmtModified'], 'Date');
            }
            if (data.hasOwnProperty('visibility')) {
                obj['visibility'] = ApiClient.convertToType(data['visibility'], 'String');
            }
            if (data.hasOwnProperty('version')) {
                obj['version'] = ApiClient.convertToType(data['version'], 'Number');
            }
            if (data.hasOwnProperty('name')) {
                obj['name'] = ApiClient.convertToType(data['name'], 'String');
            }
            if (data.hasOwnProperty('type')) {
                obj['type'] = ApiClient.convertToType(data['type'], 'String');
            }
            if (data.hasOwnProperty('description')) {
                obj['description'] = ApiClient.convertToType(data['description'], 'String');
            }
            if (data.hasOwnProperty('format')) {
                obj['format'] = ApiClient.convertToType(data['format'], 'String');
            }
            if (data.hasOwnProperty('lang')) {
                obj['lang'] = ApiClient.convertToType(data['lang'], 'String');
            }
            if (data.hasOwnProperty('username')) {
                obj['username'] = ApiClient.convertToType(data['username'], 'String');
            }
            if (data.hasOwnProperty('tags')) {
                obj['tags'] = ApiClient.convertToType(data['tags'], ['String']);
            }
            if (data.hasOwnProperty('aiModels')) {
                obj['aiModels'] = ApiClient.convertToType(data['aiModels'], [AiModelInfoDTO]);
            }
            if (data.hasOwnProperty('viewCount')) {
                obj['viewCount'] = ApiClient.convertToType(data['viewCount'], 'Number');
            }
            if (data.hasOwnProperty('referCount')) {
                obj['referCount'] = ApiClient.convertToType(data['referCount'], 'Number');
            }
            if (data.hasOwnProperty('recommendCount')) {
                obj['recommendCount'] = ApiClient.convertToType(data['recommendCount'], 'Number');
            }
            if (data.hasOwnProperty('scoreCount')) {
                obj['scoreCount'] = ApiClient.convertToType(data['scoreCount'], 'Number');
            }
            if (data.hasOwnProperty('score')) {
                obj['score'] = ApiClient.convertToType(data['score'], 'Number');
            }
        }
        return obj;
    }

    /**
     * Validates the JSON data with respect to <code>PromptSummaryStatsDTO</code>.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @return {boolean} to indicate whether the JSON data is valid with respect to <code>PromptSummaryStatsDTO</code>.
     */
    static validateJSON(data) {
        // ensure the json data is a string
        if (data['requestId'] && !(typeof data['requestId'] === 'string' || data['requestId'] instanceof String)) {
            throw new Error("Expected the field `requestId` to be a primitive type in the JSON string but got " + data['requestId']);
        }
        // ensure the json data is a string
        if (data['promptId'] && !(typeof data['promptId'] === 'string' || data['promptId'] instanceof String)) {
            throw new Error("Expected the field `promptId` to be a primitive type in the JSON string but got " + data['promptId']);
        }
        // ensure the json data is a string
        if (data['visibility'] && !(typeof data['visibility'] === 'string' || data['visibility'] instanceof String)) {
            throw new Error("Expected the field `visibility` to be a primitive type in the JSON string but got " + data['visibility']);
        }
        // ensure the json data is a string
        if (data['name'] && !(typeof data['name'] === 'string' || data['name'] instanceof String)) {
            throw new Error("Expected the field `name` to be a primitive type in the JSON string but got " + data['name']);
        }
        // ensure the json data is a string
        if (data['type'] && !(typeof data['type'] === 'string' || data['type'] instanceof String)) {
            throw new Error("Expected the field `type` to be a primitive type in the JSON string but got " + data['type']);
        }
        // ensure the json data is a string
        if (data['description'] && !(typeof data['description'] === 'string' || data['description'] instanceof String)) {
            throw new Error("Expected the field `description` to be a primitive type in the JSON string but got " + data['description']);
        }
        // ensure the json data is a string
        if (data['format'] && !(typeof data['format'] === 'string' || data['format'] instanceof String)) {
            throw new Error("Expected the field `format` to be a primitive type in the JSON string but got " + data['format']);
        }
        // ensure the json data is a string
        if (data['lang'] && !(typeof data['lang'] === 'string' || data['lang'] instanceof String)) {
            throw new Error("Expected the field `lang` to be a primitive type in the JSON string but got " + data['lang']);
        }
        // ensure the json data is a string
        if (data['username'] && !(typeof data['username'] === 'string' || data['username'] instanceof String)) {
            throw new Error("Expected the field `username` to be a primitive type in the JSON string but got " + data['username']);
        }
        // ensure the json data is an array
        if (!Array.isArray(data['tags'])) {
            throw new Error("Expected the field `tags` to be an array in the JSON data but got " + data['tags']);
        }
        if (data['aiModels']) { // data not null
            // ensure the json data is an array
            if (!Array.isArray(data['aiModels'])) {
                throw new Error("Expected the field `aiModels` to be an array in the JSON data but got " + data['aiModels']);
            }
            // validate the optional field `aiModels` (array)
            for (const item of data['aiModels']) {
                AiModelInfoDTO.validateJSON(item);
            };
        }

        return true;
    }


}



/**
 * Request identifier
 * @member {String} requestId
 */
PromptSummaryStatsDTO.prototype['requestId'] = undefined;

/**
 * Prompt identifier
 * @member {String} promptId
 */
PromptSummaryStatsDTO.prototype['promptId'] = undefined;

/**
 * Creation time
 * @member {Date} gmtCreate
 */
PromptSummaryStatsDTO.prototype['gmtCreate'] = undefined;

/**
 * Modification time
 * @member {Date} gmtModified
 */
PromptSummaryStatsDTO.prototype['gmtModified'] = undefined;

/**
 * Visibility: private, public, public_org, hidden
 * @member {String} visibility
 */
PromptSummaryStatsDTO.prototype['visibility'] = undefined;

/**
 * Version
 * @member {Number} version
 */
PromptSummaryStatsDTO.prototype['version'] = undefined;

/**
 * Prompt name
 * @member {String} name
 */
PromptSummaryStatsDTO.prototype['name'] = undefined;

/**
 * Prompt type: string | chat
 * @member {String} type
 */
PromptSummaryStatsDTO.prototype['type'] = undefined;

/**
 * Prompt description (50 characters, the excess part is represented by ellipsis)
 * @member {String} description
 */
PromptSummaryStatsDTO.prototype['description'] = undefined;

/**
 * Prompt format: mustache (default) | f_string
 * @member {String} format
 */
PromptSummaryStatsDTO.prototype['format'] = undefined;

/**
 * Prompt language: en (default) | zh_CN | ...
 * @member {String} lang
 */
PromptSummaryStatsDTO.prototype['lang'] = undefined;

/**
 * Prompt owner
 * @member {String} username
 */
PromptSummaryStatsDTO.prototype['username'] = undefined;

/**
 * Tag set
 * @member {Array.<String>} tags
 */
PromptSummaryStatsDTO.prototype['tags'] = undefined;

/**
 * Supported model set
 * @member {Array.<module:model/AiModelInfoDTO>} aiModels
 */
PromptSummaryStatsDTO.prototype['aiModels'] = undefined;

/**
 * View count
 * @member {Number} viewCount
 */
PromptSummaryStatsDTO.prototype['viewCount'] = undefined;

/**
 * Reference count
 * @member {Number} referCount
 */
PromptSummaryStatsDTO.prototype['referCount'] = undefined;

/**
 * Recommendation count
 * @member {Number} recommendCount
 */
PromptSummaryStatsDTO.prototype['recommendCount'] = undefined;

/**
 * Score count
 * @member {Number} scoreCount
 */
PromptSummaryStatsDTO.prototype['scoreCount'] = undefined;

/**
 * Average score
 * @member {Number} score
 */
PromptSummaryStatsDTO.prototype['score'] = undefined;






export default PromptSummaryStatsDTO;

