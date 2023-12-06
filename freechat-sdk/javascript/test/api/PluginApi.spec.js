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
    instance = new freechat-sdk.PluginApi();
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

  describe('PluginApi', function() {
    describe('batchSearchPluginDetails', function() {
      it('should call batchSearchPluginDetails successfully', function(done) {
        //uncomment below and update the code to test batchSearchPluginDetails
        //instance.batchSearchPluginDetails(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('batchSearchPluginSummary', function() {
      it('should call batchSearchPluginSummary successfully', function(done) {
        //uncomment below and update the code to test batchSearchPluginSummary
        //instance.batchSearchPluginSummary(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('countPlugins', function() {
      it('should call countPlugins successfully', function(done) {
        //uncomment below and update the code to test countPlugins
        //instance.countPlugins(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('createPlugin', function() {
      it('should call createPlugin successfully', function(done) {
        //uncomment below and update the code to test createPlugin
        //instance.createPlugin(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('createPlugins', function() {
      it('should call createPlugins successfully', function(done) {
        //uncomment below and update the code to test createPlugins
        //instance.createPlugins(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('deletePlugin', function() {
      it('should call deletePlugin successfully', function(done) {
        //uncomment below and update the code to test deletePlugin
        //instance.deletePlugin(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('deletePlugins', function() {
      it('should call deletePlugins successfully', function(done) {
        //uncomment below and update the code to test deletePlugins
        //instance.deletePlugins(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('getPluginDetails', function() {
      it('should call getPluginDetails successfully', function(done) {
        //uncomment below and update the code to test getPluginDetails
        //instance.getPluginDetails(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('getPluginSummary', function() {
      it('should call getPluginSummary successfully', function(done) {
        //uncomment below and update the code to test getPluginSummary
        //instance.getPluginSummary(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('refreshPluginInfo', function() {
      it('should call refreshPluginInfo successfully', function(done) {
        //uncomment below and update the code to test refreshPluginInfo
        //instance.refreshPluginInfo(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('searchPluginDetails', function() {
      it('should call searchPluginDetails successfully', function(done) {
        //uncomment below and update the code to test searchPluginDetails
        //instance.searchPluginDetails(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('searchPluginSummary', function() {
      it('should call searchPluginSummary successfully', function(done) {
        //uncomment below and update the code to test searchPluginSummary
        //instance.searchPluginSummary(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
    describe('updatePlugin', function() {
      it('should call updatePlugin successfully', function(done) {
        //uncomment below and update the code to test updatePlugin
        //instance.updatePlugin(function(error) {
        //  if (error) throw error;
        //expect().to.be();
        //});
        done();
      });
    });
  });

}));
