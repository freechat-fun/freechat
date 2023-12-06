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
    instance = new freechat-sdk.AppMetaDTO();
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

  describe('AppMetaDTO', function() {
    it('should create an instance of AppMetaDTO', function() {
      // uncomment below and update the code to test AppMetaDTO
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be.a(freechat-sdk.AppMetaDTO);
    });

    it('should have the property name (base name: "name")', function() {
      // uncomment below and update the code to test the property name
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be();
    });

    it('should have the property version (base name: "version")', function() {
      // uncomment below and update the code to test the property version
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be();
    });

    it('should have the property buildTimestamp (base name: "buildTimestamp")', function() {
      // uncomment below and update the code to test the property buildTimestamp
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be();
    });

    it('should have the property buildNumber (base name: "buildNumber")', function() {
      // uncomment below and update the code to test the property buildNumber
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be();
    });

    it('should have the property commitUrl (base name: "commitUrl")', function() {
      // uncomment below and update the code to test the property commitUrl
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be();
    });

    it('should have the property releaseNoteUrl (base name: "releaseNoteUrl")', function() {
      // uncomment below and update the code to test the property releaseNoteUrl
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be();
    });

    it('should have the property runningEnv (base name: "runningEnv")', function() {
      // uncomment below and update the code to test the property runningEnv
      //var instance = new freechat-sdk.AppMetaDTO();
      //expect(instance).to.be();
    });

  });

}));
