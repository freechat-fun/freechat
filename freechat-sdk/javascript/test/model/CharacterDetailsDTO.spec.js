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
    instance = new freechat-sdk.CharacterDetailsDTO();
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

  describe('CharacterDetailsDTO', function() {
    it('should create an instance of CharacterDetailsDTO', function() {
      // uncomment below and update the code to test CharacterDetailsDTO
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be.a(freechat-sdk.CharacterDetailsDTO);
    });

    it('should have the property requestId (base name: "requestId")', function() {
      // uncomment below and update the code to test the property requestId
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property characterId (base name: "characterId")', function() {
      // uncomment below and update the code to test the property characterId
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property gmtCreate (base name: "gmtCreate")', function() {
      // uncomment below and update the code to test the property gmtCreate
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property gmtModified (base name: "gmtModified")', function() {
      // uncomment below and update the code to test the property gmtModified
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property visibility (base name: "visibility")', function() {
      // uncomment below and update the code to test the property visibility
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property version (base name: "version")', function() {
      // uncomment below and update the code to test the property version
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property name (base name: "name")', function() {
      // uncomment below and update the code to test the property name
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property description (base name: "description")', function() {
      // uncomment below and update the code to test the property description
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property avatar (base name: "avatar")', function() {
      // uncomment below and update the code to test the property avatar
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property picture (base name: "picture")', function() {
      // uncomment below and update the code to test the property picture
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property lang (base name: "lang")', function() {
      // uncomment below and update the code to test the property lang
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property username (base name: "username")', function() {
      // uncomment below and update the code to test the property username
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property tags (base name: "tags")', function() {
      // uncomment below and update the code to test the property tags
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property profile (base name: "profile")', function() {
      // uncomment below and update the code to test the property profile
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property greeting (base name: "greeting")', function() {
      // uncomment below and update the code to test the property greeting
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property chatStyle (base name: "chatStyle")', function() {
      // uncomment below and update the code to test the property chatStyle
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property chatExample (base name: "chatExample")', function() {
      // uncomment below and update the code to test the property chatExample
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property experience (base name: "experience")', function() {
      // uncomment below and update the code to test the property experience
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property ext (base name: "ext")', function() {
      // uncomment below and update the code to test the property ext
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property draft (base name: "draft")', function() {
      // uncomment below and update the code to test the property draft
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

    it('should have the property backends (base name: "backends")', function() {
      // uncomment below and update the code to test the property backends
      //var instance = new freechat-sdk.CharacterDetailsDTO();
      //expect(instance).to.be();
    });

  });

}));
