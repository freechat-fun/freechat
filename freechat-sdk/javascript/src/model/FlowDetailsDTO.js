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
 * The FlowDetailsDTO model module.
 * @module model/FlowDetailsDTO
 * @version 0.1.0
 */
class FlowDetailsDTO {
    /**
     * Constructs a new <code>FlowDetailsDTO</code>.
     * Flow detailed content
     * @alias module:model/FlowDetailsDTO
     */
    constructor() { 
        
        FlowDetailsDTO.initialize(this);
    }

    /**
     * Initializes the fields of this object.
     * This method is used by the constructors of any subclasses, in order to implement multiple inheritance (mix-ins).
     * Only for internal use.
     */
    static initialize(obj) { 
    }

    /**
     * Constructs a <code>FlowDetailsDTO</code> from a plain JavaScript object, optionally creating a new instance.
     * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @param {module:model/FlowDetailsDTO} obj Optional instance to populate.
     * @return {module:model/FlowDetailsDTO} The populated <code>FlowDetailsDTO</code> instance.
     */
    static constructFromObject(data, obj) {
        if (data) {
            obj = obj || new FlowDetailsDTO();

            if (data.hasOwnProperty('requestId')) {
                obj['requestId'] = ApiClient.convertToType(data['requestId'], 'String');
            }
            if (data.hasOwnProperty('flowId')) {
                obj['flowId'] = ApiClient.convertToType(data['flowId'], 'String');
            }
            if (data.hasOwnProperty('gmtCreate')) {
                obj['gmtCreate'] = ApiClient.convertToType(data['gmtCreate'], 'Date');
            }
            if (data.hasOwnProperty('gmtModified')) {
                obj['gmtModified'] = ApiClient.convertToType(data['gmtModified'], 'Date');
            }
            if (data.hasOwnProperty('parentId')) {
                obj['parentId'] = ApiClient.convertToType(data['parentId'], 'String');
            }
            if (data.hasOwnProperty('visibility')) {
                obj['visibility'] = ApiClient.convertToType(data['visibility'], 'String');
            }
            if (data.hasOwnProperty('format')) {
                obj['format'] = ApiClient.convertToType(data['format'], 'String');
            }
            if (data.hasOwnProperty('version')) {
                obj['version'] = ApiClient.convertToType(data['version'], 'Number');
            }
            if (data.hasOwnProperty('name')) {
                obj['name'] = ApiClient.convertToType(data['name'], 'String');
            }
            if (data.hasOwnProperty('description')) {
                obj['description'] = ApiClient.convertToType(data['description'], 'String');
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
            if (data.hasOwnProperty('config')) {
                obj['config'] = ApiClient.convertToType(data['config'], 'String');
            }
            if (data.hasOwnProperty('example')) {
                obj['example'] = ApiClient.convertToType(data['example'], 'String');
            }
            if (data.hasOwnProperty('parameters')) {
                obj['parameters'] = ApiClient.convertToType(data['parameters'], 'String');
            }
            if (data.hasOwnProperty('ext')) {
                obj['ext'] = ApiClient.convertToType(data['ext'], 'String');
            }
            if (data.hasOwnProperty('draft')) {
                obj['draft'] = ApiClient.convertToType(data['draft'], 'String');
            }
        }
        return obj;
    }

    /**
     * Validates the JSON data with respect to <code>FlowDetailsDTO</code>.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @return {boolean} to indicate whether the JSON data is valid with respect to <code>FlowDetailsDTO</code>.
     */
    static validateJSON(data) {
        // ensure the json data is a string
        if (data['requestId'] && !(typeof data['requestId'] === 'string' || data['requestId'] instanceof String)) {
            throw new Error("Expected the field `requestId` to be a primitive type in the JSON string but got " + data['requestId']);
        }
        // ensure the json data is a string
        if (data['flowId'] && !(typeof data['flowId'] === 'string' || data['flowId'] instanceof String)) {
            throw new Error("Expected the field `flowId` to be a primitive type in the JSON string but got " + data['flowId']);
        }
        // ensure the json data is a string
        if (data['parentId'] && !(typeof data['parentId'] === 'string' || data['parentId'] instanceof String)) {
            throw new Error("Expected the field `parentId` to be a primitive type in the JSON string but got " + data['parentId']);
        }
        // ensure the json data is a string
        if (data['visibility'] && !(typeof data['visibility'] === 'string' || data['visibility'] instanceof String)) {
            throw new Error("Expected the field `visibility` to be a primitive type in the JSON string but got " + data['visibility']);
        }
        // ensure the json data is a string
        if (data['format'] && !(typeof data['format'] === 'string' || data['format'] instanceof String)) {
            throw new Error("Expected the field `format` to be a primitive type in the JSON string but got " + data['format']);
        }
        // ensure the json data is a string
        if (data['name'] && !(typeof data['name'] === 'string' || data['name'] instanceof String)) {
            throw new Error("Expected the field `name` to be a primitive type in the JSON string but got " + data['name']);
        }
        // ensure the json data is a string
        if (data['description'] && !(typeof data['description'] === 'string' || data['description'] instanceof String)) {
            throw new Error("Expected the field `description` to be a primitive type in the JSON string but got " + data['description']);
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
        // ensure the json data is a string
        if (data['config'] && !(typeof data['config'] === 'string' || data['config'] instanceof String)) {
            throw new Error("Expected the field `config` to be a primitive type in the JSON string but got " + data['config']);
        }
        // ensure the json data is a string
        if (data['example'] && !(typeof data['example'] === 'string' || data['example'] instanceof String)) {
            throw new Error("Expected the field `example` to be a primitive type in the JSON string but got " + data['example']);
        }
        // ensure the json data is a string
        if (data['parameters'] && !(typeof data['parameters'] === 'string' || data['parameters'] instanceof String)) {
            throw new Error("Expected the field `parameters` to be a primitive type in the JSON string but got " + data['parameters']);
        }
        // ensure the json data is a string
        if (data['ext'] && !(typeof data['ext'] === 'string' || data['ext'] instanceof String)) {
            throw new Error("Expected the field `ext` to be a primitive type in the JSON string but got " + data['ext']);
        }
        // ensure the json data is a string
        if (data['draft'] && !(typeof data['draft'] === 'string' || data['draft'] instanceof String)) {
            throw new Error("Expected the field `draft` to be a primitive type in the JSON string but got " + data['draft']);
        }

        return true;
    }


}



/**
 * Request identifier
 * @member {String} requestId
 */
FlowDetailsDTO.prototype['requestId'] = undefined;

/**
 * Flow identifier
 * @member {String} flowId
 */
FlowDetailsDTO.prototype['flowId'] = undefined;

/**
 * Creation time
 * @member {Date} gmtCreate
 */
FlowDetailsDTO.prototype['gmtCreate'] = undefined;

/**
 * Modification time
 * @member {Date} gmtModified
 */
FlowDetailsDTO.prototype['gmtModified'] = undefined;

/**
 * Referenced flow
 * @member {String} parentId
 */
FlowDetailsDTO.prototype['parentId'] = undefined;

/**
 * Visibility: private, public, public_org, hidden
 * @member {String} visibility
 */
FlowDetailsDTO.prototype['visibility'] = undefined;

/**
 * Flow format, currently supported: langflow
 * @member {String} format
 */
FlowDetailsDTO.prototype['format'] = undefined;

/**
 * Version
 * @member {Number} version
 */
FlowDetailsDTO.prototype['version'] = undefined;

/**
 * Flow name
 * @member {String} name
 */
FlowDetailsDTO.prototype['name'] = undefined;

/**
 * Flow description
 * @member {String} description
 */
FlowDetailsDTO.prototype['description'] = undefined;

/**
 * Flow owner
 * @member {String} username
 */
FlowDetailsDTO.prototype['username'] = undefined;

/**
 * Tag set
 * @member {Array.<String>} tags
 */
FlowDetailsDTO.prototype['tags'] = undefined;

/**
 * Supported model set
 * @member {Array.<module:model/AiModelInfoDTO>} aiModels
 */
FlowDetailsDTO.prototype['aiModels'] = undefined;

/**
 * Flow configuration
 * @member {String} config
 */
FlowDetailsDTO.prototype['config'] = undefined;

/**
 * Flow example
 * @member {String} example
 */
FlowDetailsDTO.prototype['example'] = undefined;

/**
 * Flow parameters, JSON format
 * @member {String} parameters
 */
FlowDetailsDTO.prototype['parameters'] = undefined;

/**
 * Additional information, JSON format
 * @member {String} ext
 */
FlowDetailsDTO.prototype['ext'] = undefined;

/**
 * Draft content
 * @member {String} draft
 */
FlowDetailsDTO.prototype['draft'] = undefined;






export default FlowDetailsDTO;

