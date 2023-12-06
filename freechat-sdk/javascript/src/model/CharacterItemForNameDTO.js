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
import InteractiveStatsDTO from './InteractiveStatsDTO';

/**
 * The CharacterItemForNameDTO model module.
 * @module model/CharacterItemForNameDTO
 * @version 0.1.0
 */
class CharacterItemForNameDTO {
    /**
     * Constructs a new <code>CharacterItemForNameDTO</code>.
     * Character identifier and version information
     * @alias module:model/CharacterItemForNameDTO
     */
    constructor() { 
        
        CharacterItemForNameDTO.initialize(this);
    }

    /**
     * Initializes the fields of this object.
     * This method is used by the constructors of any subclasses, in order to implement multiple inheritance (mix-ins).
     * Only for internal use.
     */
    static initialize(obj) { 
    }

    /**
     * Constructs a <code>CharacterItemForNameDTO</code> from a plain JavaScript object, optionally creating a new instance.
     * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @param {module:model/CharacterItemForNameDTO} obj Optional instance to populate.
     * @return {module:model/CharacterItemForNameDTO} The populated <code>CharacterItemForNameDTO</code> instance.
     */
    static constructFromObject(data, obj) {
        if (data) {
            obj = obj || new CharacterItemForNameDTO();

            if (data.hasOwnProperty('characterId')) {
                obj['characterId'] = ApiClient.convertToType(data['characterId'], 'String');
            }
            if (data.hasOwnProperty('version')) {
                obj['version'] = ApiClient.convertToType(data['version'], 'Number');
            }
            if (data.hasOwnProperty('stats')) {
                obj['stats'] = InteractiveStatsDTO.constructFromObject(data['stats']);
            }
        }
        return obj;
    }

    /**
     * Validates the JSON data with respect to <code>CharacterItemForNameDTO</code>.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @return {boolean} to indicate whether the JSON data is valid with respect to <code>CharacterItemForNameDTO</code>.
     */
    static validateJSON(data) {
        // ensure the json data is a string
        if (data['characterId'] && !(typeof data['characterId'] === 'string' || data['characterId'] instanceof String)) {
            throw new Error("Expected the field `characterId` to be a primitive type in the JSON string but got " + data['characterId']);
        }
        // validate the optional field `stats`
        if (data['stats']) { // data not null
          InteractiveStatsDTO.validateJSON(data['stats']);
        }

        return true;
    }


}



/**
 * characterId
 * @member {String} characterId
 */
CharacterItemForNameDTO.prototype['characterId'] = undefined;

/**
 * version
 * @member {Number} version
 */
CharacterItemForNameDTO.prototype['version'] = undefined;

/**
 * @member {module:model/InteractiveStatsDTO} stats
 */
CharacterItemForNameDTO.prototype['stats'] = undefined;






export default CharacterItemForNameDTO;

