/**
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * OpenAPI spec version: 0.9.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { HttpFile } from '../http/http.js';

/**
* Qwen series model parameters
*/
export class QwenParamDTO {
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
    * Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.
    */
    'topP'?: number;
    /**
    * The size of the sampling candidate set during generation. For example, when the value is 50, only the top 50 tokens with the highest scores in a single generation are included in the random sampling candidate set. The larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation. The default value is 0, which means that the top_k strategy is not enabled, and only the top_p strategy is effective.
    */
    'topK'?: number;
    /**
    * The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model\'s context length.
    */
    'maxTokens'?: number;
    /**
    * Whether to use a search engine for data enhancement.
    */
    'enableSearch'?: boolean;
    /**
    * The random number seed used when generating, the user controls the randomness of the content generated by the model. seed supports unsigned 64-bit integers, with a default value of 1234. When using seed, the model will try its best to generate the same or similar results, but there is currently no guarantee that the results will be exactly the same every time.
    */
    'seed'?: number;
    /**
    * Used to control the repeatability when generating models. Increasing repetition_penalty can reduce the duplication of model generation. 1.0 means no punishment.
    */
    'repetitionPenalty'?: number;
    /**
    * Used to adjust the degree of randomness from sampling in the generated model, the value range is [0, 2), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.
    */
    'temperature'?: number;
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
            "name": "topP",
            "baseName": "topP",
            "type": "number",
            "format": "double"
        },
        {
            "name": "topK",
            "baseName": "topK",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "maxTokens",
            "baseName": "maxTokens",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "enableSearch",
            "baseName": "enableSearch",
            "type": "boolean",
            "format": ""
        },
        {
            "name": "seed",
            "baseName": "seed",
            "type": "number",
            "format": "int32"
        },
        {
            "name": "repetitionPenalty",
            "baseName": "repetitionPenalty",
            "type": "number",
            "format": "float"
        },
        {
            "name": "temperature",
            "baseName": "temperature",
            "type": "number",
            "format": "float"
        },
        {
            "name": "stop",
            "baseName": "stop",
            "type": "Array<string>",
            "format": ""
        }    ];

    static getAttributeTypeMap() {
        return QwenParamDTO.attributeTypeMap;
    }

    public constructor() {
    }
}

