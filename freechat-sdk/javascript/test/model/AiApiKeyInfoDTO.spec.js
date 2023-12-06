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

(function(root, factory) {
  if (typeof define === 'function' && define.amd) {
    // AMD.
    define(['expect.js', process.cwd()+'/src/index'], factory);
  } else if (typeof module === 'object' && module.exports) {
    // CommonJS-like environments that support module.exports, like Node.
    factory(require('expect.js'), require(process.cwd()+'/src/index'));
  } else {
    // Browser globals (root is window)
    factory(root.expect, root.freechat-sdk);
  }
}(this, function(expect, freechat-sdk) {
  'use strict';

  var instance;

  beforeEach(function() {
    instance = new freechat-sdk.AiApiKeyInfoDTO();
  });

  var getProperty = function(object, getter, property) {
    // Use getter method if present; otherwise, get the property directly.
    if (typeof object[getter] === 'function')
      return object[getter]();
    else
      return object[property];
  }

  var setProperty = function(object, setter, property, value) {
    // Use setter method if present; otherwise, set the property directly.
    if (typeof object[setter] === 'function')
      object[setter](value);
    else
      object[property] = value;
  }

  describe('AiApiKeyInfoDTO', function() {
    it('should create an instance of AiApiKeyInfoDTO', function() {
      // uncomment below and update the code to test AiApiKeyInfoDTO
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be.a(freechat-sdk.AiApiKeyInfoDTO);
    });

    it('should have the property requestId (base name: "requestId")', function() {
      // uncomment below and update the code to test the property requestId
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property id (base name: "id")', function() {
      // uncomment below and update the code to test the property id
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property gmtCreate (base name: "gmtCreate")', function() {
      // uncomment below and update the code to test the property gmtCreate
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property gmtModified (base name: "gmtModified")', function() {
      // uncomment below and update the code to test the property gmtModified
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property gmtUsed (base name: "gmtUsed")', function() {
      // uncomment below and update the code to test the property gmtUsed
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property name (base name: "name")', function() {
      // uncomment below and update the code to test the property name
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property provider (base name: "provider")', function() {
      // uncomment below and update the code to test the property provider
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property token (base name: "token")', function() {
      // uncomment below and update the code to test the property token
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property enabled (base name: "enabled")', function() {
      // uncomment below and update the code to test the property enabled
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

    it('should have the property username (base name: "username")', function() {
      // uncomment below and update the code to test the property username
      //var instance = new freechat-sdk.AiApiKeyInfoDTO();
      //expect(instance).to.be();
    });

  });

}));
