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

/**
 * The SseEmitter model module.
 * @module model/SseEmitter
 * @version 0.1.0
 */
class SseEmitter {
    /**
     * Constructs a new <code>SseEmitter</code>.
     * @alias module:model/SseEmitter
     */
    constructor() { 
        
        SseEmitter.initialize(this);
    }

    /**
     * Initializes the fields of this object.
     * This method is used by the constructors of any subclasses, in order to implement multiple inheritance (mix-ins).
     * Only for internal use.
     */
    static initialize(obj) { 
    }

    /**
     * Constructs a <code>SseEmitter</code> from a plain JavaScript object, optionally creating a new instance.
     * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @param {module:model/SseEmitter} obj Optional instance to populate.
     * @return {module:model/SseEmitter} The populated <code>SseEmitter</code> instance.
     */
    static constructFromObject(data, obj) {
        if (data) {
            obj = obj || new SseEmitter();

            if (data.hasOwnProperty('timeout')) {
                obj['timeout'] = ApiClient.convertToType(data['timeout'], 'Number');
            }
        }
        return obj;
    }

    /**
     * Validates the JSON data with respect to <code>SseEmitter</code>.
     * @param {Object} data The plain JavaScript object bearing properties of interest.
     * @return {boolean} to indicate whether the JSON data is valid with respect to <code>SseEmitter</code>.
     */
    static validateJSON(data) {

        return true;
    }


}



/**
 * @member {Number} timeout
 */
SseEmitter.prototype['timeout'] = undefined;






export default SseEmitter;

