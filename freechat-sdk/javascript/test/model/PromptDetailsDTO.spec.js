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
    instance = new freechat-sdk.PromptDetailsDTO();
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

  describe('PromptDetailsDTO', function() {
    it('should create an instance of PromptDetailsDTO', function() {
      // uncomment below and update the code to test PromptDetailsDTO
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be.a(freechat-sdk.PromptDetailsDTO);
    });

    it('should have the property requestId (base name: "requestId")', function() {
      // uncomment below and update the code to test the property requestId
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property promptId (base name: "promptId")', function() {
      // uncomment below and update the code to test the property promptId
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property gmtCreate (base name: "gmtCreate")', function() {
      // uncomment below and update the code to test the property gmtCreate
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property gmtModified (base name: "gmtModified")', function() {
      // uncomment below and update the code to test the property gmtModified
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property visibility (base name: "visibility")', function() {
      // uncomment below and update the code to test the property visibility
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property version (base name: "version")', function() {
      // uncomment below and update the code to test the property version
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property name (base name: "name")', function() {
      // uncomment below and update the code to test the property name
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property type (base name: "type")', function() {
      // uncomment below and update the code to test the property type
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property description (base name: "description")', function() {
      // uncomment below and update the code to test the property description
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property format (base name: "format")', function() {
      // uncomment below and update the code to test the property format
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property lang (base name: "lang")', function() {
      // uncomment below and update the code to test the property lang
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property username (base name: "username")', function() {
      // uncomment below and update the code to test the property username
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property tags (base name: "tags")', function() {
      // uncomment below and update the code to test the property tags
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property aiModels (base name: "aiModels")', function() {
      // uncomment below and update the code to test the property aiModels
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property template (base name: "template")', function() {
      // uncomment below and update the code to test the property template
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property chatTemplate (base name: "chatTemplate")', function() {
      // uncomment below and update the code to test the property chatTemplate
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property example (base name: "example")', function() {
      // uncomment below and update the code to test the property example
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property inputs (base name: "inputs")', function() {
      // uncomment below and update the code to test the property inputs
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property ext (base name: "ext")', function() {
      // uncomment below and update the code to test the property ext
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property draft (base name: "draft")', function() {
      // uncomment below and update the code to test the property draft
      //var instance = new freechat-sdk.PromptDetailsDTO();
      //expect(instance).to.be();
    });

  });

}));
