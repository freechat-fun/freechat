/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.6.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* OpenAI series model parameters
*/
export class OpenAiParamDTO {
    /**
    * API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified.
    */
    'apiKey'?: string;
    /**
    * API-KEY name
    */
    'apiKeyName'?: string;
    /**
    * Model identifier
    */
    'modelId': string;
    /**
    * OpenAI service provider address, default: https://api.openai.com/v1
    */
    'baseUrl'?: string;
    /**
    * Used to adjust the degree of randomness from sampling in the generated model, the value range is (0, 1.0), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.
    */
    'temperature'?: number;
    /**
    * Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.
    */
    'topP'?: number;
    /**
    * The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model\'s context length.
    */
    'maxTokens'?: number;
    /**
    * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model\'s likelihood to talk about new topics.
    */
    'presencePenalty'?: number;
    /**
    * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model\'s likelihood to repeat the same line verbatim.
    */
    'frequencyPenalty'?: number;
    /**
    * If specified, OpenAI will make a best effort to sample deterministically, such that repeated requests with the same seed and parameters should return the same result.
    */
    'seed'?: number;
    /**
    * A collection of stop words that controls the API from generating more tokens.
    */
    'stop'?: Array<string>;

    static readonly discriminator: string | undefined = undefined;

    static readonly attributeTypeMap: Array<{name: string, baseName: string, type: string, format: string}> = [
        {
            "name": "apiKey",
            "baseName": "apiKey",
            "type": "string",
            "format": ""
        },
        {
            "name": "apiKeyName",
            "baseName": "apiKeyName",
            "type": "string",
            "format": ""
        },
        {
            "name": "modelId",
            "baseName": "modelId",
            "type": "string",
            "format": ""
        },
        {
            "name": "baseUrl",
            "baseName": "baseUrl",
            "type": "string",
            "format": ""
        },
        {
            "name": "temperature",
            "baseName": "temperature",
            "type": "number",
            "format": "double"
        },
        {
            "name": "topP",
            "baseName": "topP",
            "type": "number",
            "format": "double"
        },
        {
            "name": "maxTokens",
            "baseName": "maxTokens",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "presencePenalty",
            "baseName": "presencePenalty",
            "type": "number",
            "format": "double"
        },
        {
            "name": "frequencyPenalty",
            "baseName": "frequencyPenalty",
            "type": "number",
            "format": "double"
        },
        {
            "name": "seed",
            "baseName": "seed",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "stop",
            "baseName": "stop",
            "type": "Array<string>",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return OpenAiParamDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

