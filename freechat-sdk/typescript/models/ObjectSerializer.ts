export * from '../models/AiApiKeyCreateDTO';
export * from '../models/AiApiKeyInfoDTO';
export * from '../models/AiModelInfoDTO';
export * from '../models/AppConfigCreateDTO';
export * from '../models/AppConfigInfoDTO';
export * from '../models/AppMetaDTO';
export * from '../models/CharacterBackendDTO';
export * from '../models/CharacterBackendDetailsDTO';
export * from '../models/CharacterCreateDTO';
export * from '../models/CharacterDetailsDTO';
export * from '../models/CharacterInfoDraftDTO';
export * from '../models/CharacterItemForNameDTO';
export * from '../models/CharacterQueryDTO';
export * from '../models/CharacterSummaryDTO';
export * from '../models/CharacterSummaryStatsDTO';
export * from '../models/CharacterUpdateDTO';
export * from '../models/ChatContentDTO';
export * from '../models/ChatCreateDTO';
export * from '../models/ChatMessageDTO';
export * from '../models/ChatPromptContentDTO';
export * from '../models/ChatToolCallDTO';
export * from '../models/FlowCreateDTO';
export * from '../models/FlowDetailsDTO';
export * from '../models/FlowItemForNameDTO';
export * from '../models/FlowQueryDTO';
export * from '../models/FlowSummaryDTO';
export * from '../models/FlowSummaryStatsDTO';
export * from '../models/FlowUpdateDTO';
export * from '../models/InteractiveStatsDTO';
export * from '../models/LlmResultDTO';
export * from '../models/LlmTokenUsageDTO';
export * from '../models/OpenAiParamDTO';
export * from '../models/PluginCreateDTO';
export * from '../models/PluginDetailsDTO';
export * from '../models/PluginQueryDTO';
export * from '../models/PluginSummaryDTO';
export * from '../models/PluginSummaryStatsDTO';
export * from '../models/PluginUpdateDTO';
export * from '../models/PromptAiParamDTO';
export * from '../models/PromptCreateDTO';
export * from '../models/PromptDetailsDTO';
export * from '../models/PromptItemForNameDTO';
export * from '../models/PromptQueryDTO';
export * from '../models/PromptRefDTO';
export * from '../models/PromptSummaryDTO';
export * from '../models/PromptSummaryStatsDTO';
export * from '../models/PromptTaskDTO';
export * from '../models/PromptTaskDetailsDTO';
export * from '../models/PromptTemplateDTO';
export * from '../models/PromptUpdateDTO';
export * from '../models/QwenParamDTO';
export * from '../models/SseEmitter';
export * from '../models/UserBasicInfoDTO';
export * from '../models/UserDetailsDTO';
export * from '../models/UserFullDetailsDTO';
export * from '../models/Where';

import { AiApiKeyCreateDTO } from '../models/AiApiKeyCreateDTO';
import { AiApiKeyInfoDTO } from '../models/AiApiKeyInfoDTO';
import { AiModelInfoDTO } from '../models/AiModelInfoDTO';
import { AppConfigCreateDTO } from '../models/AppConfigCreateDTO';
import { AppConfigInfoDTO } from '../models/AppConfigInfoDTO';
import { AppMetaDTO } from '../models/AppMetaDTO';
import { CharacterBackendDTO } from '../models/CharacterBackendDTO';
import { CharacterBackendDetailsDTO } from '../models/CharacterBackendDetailsDTO';
import { CharacterCreateDTO } from '../models/CharacterCreateDTO';
import { CharacterDetailsDTO } from '../models/CharacterDetailsDTO';
import { CharacterInfoDraftDTO } from '../models/CharacterInfoDraftDTO';
import { CharacterItemForNameDTO } from '../models/CharacterItemForNameDTO';
import { CharacterQueryDTO } from '../models/CharacterQueryDTO';
import { CharacterSummaryDTO } from '../models/CharacterSummaryDTO';
import { CharacterSummaryStatsDTO } from '../models/CharacterSummaryStatsDTO';
import { CharacterUpdateDTO } from '../models/CharacterUpdateDTO';
import { ChatContentDTO } from '../models/ChatContentDTO';
import { ChatCreateDTO } from '../models/ChatCreateDTO';
import { ChatMessageDTO } from '../models/ChatMessageDTO';
import { ChatPromptContentDTO } from '../models/ChatPromptContentDTO';
import { ChatToolCallDTO } from '../models/ChatToolCallDTO';
import { FlowCreateDTO } from '../models/FlowCreateDTO';
import { FlowDetailsDTO } from '../models/FlowDetailsDTO';
import { FlowItemForNameDTO } from '../models/FlowItemForNameDTO';
import { FlowQueryDTO } from '../models/FlowQueryDTO';
import { FlowSummaryDTO } from '../models/FlowSummaryDTO';
import { FlowSummaryStatsDTO } from '../models/FlowSummaryStatsDTO';
import { FlowUpdateDTO } from '../models/FlowUpdateDTO';
import { InteractiveStatsDTO } from '../models/InteractiveStatsDTO';
import { LlmResultDTO } from '../models/LlmResultDTO';
import { LlmTokenUsageDTO } from '../models/LlmTokenUsageDTO';
import { OpenAiParamDTO } from '../models/OpenAiParamDTO';
import { PluginCreateDTO } from '../models/PluginCreateDTO';
import { PluginDetailsDTO } from '../models/PluginDetailsDTO';
import { PluginQueryDTO } from '../models/PluginQueryDTO';
import { PluginSummaryDTO } from '../models/PluginSummaryDTO';
import { PluginSummaryStatsDTO } from '../models/PluginSummaryStatsDTO';
import { PluginUpdateDTO } from '../models/PluginUpdateDTO';
import { PromptAiParamDTO } from '../models/PromptAiParamDTO';
import { PromptCreateDTO } from '../models/PromptCreateDTO';
import { PromptDetailsDTO } from '../models/PromptDetailsDTO';
import { PromptItemForNameDTO } from '../models/PromptItemForNameDTO';
import { PromptQueryDTO } from '../models/PromptQueryDTO';
import { PromptRefDTO } from '../models/PromptRefDTO';
import { PromptSummaryDTO } from '../models/PromptSummaryDTO';
import { PromptSummaryStatsDTO } from '../models/PromptSummaryStatsDTO';
import { PromptTaskDTO } from '../models/PromptTaskDTO';
import { PromptTaskDetailsDTO } from '../models/PromptTaskDetailsDTO';
import { PromptTemplateDTO } from '../models/PromptTemplateDTO';
import { PromptUpdateDTO } from '../models/PromptUpdateDTO';
import { QwenParamDTO } from '../models/QwenParamDTO';
import { SseEmitter } from '../models/SseEmitter';
import { UserBasicInfoDTO } from '../models/UserBasicInfoDTO';
import { UserDetailsDTO } from '../models/UserDetailsDTO';
import { UserFullDetailsDTO } from '../models/UserFullDetailsDTO';
import { Where } from '../models/Where';

/* tslint:disable:no-unused-variable */
let primitives = [
                    "string",
                    "boolean",
                    "double",
                    "integer",
                    "long",
                    "float",
                    "number",
                    "any"
                 ];

const supportedMediaTypes: { [mediaType: string]: number } = {
  "application/json": Infinity,
  "application/json-patch+json": 1,
  "application/merge-patch+json": 1,
  "application/strategic-merge-patch+json": 1,
  "application/octet-stream": 0,
  "application/x-www-form-urlencoded": 0
}


let enumsMap: Set<string> = new Set<string>([
]);

let typeMap: {[index: string]: any} = {
    "AiApiKeyCreateDTO": AiApiKeyCreateDTO,
    "AiApiKeyInfoDTO": AiApiKeyInfoDTO,
    "AiModelInfoDTO": AiModelInfoDTO,
    "AppConfigCreateDTO": AppConfigCreateDTO,
    "AppConfigInfoDTO": AppConfigInfoDTO,
    "AppMetaDTO": AppMetaDTO,
    "CharacterBackendDTO": CharacterBackendDTO,
    "CharacterBackendDetailsDTO": CharacterBackendDetailsDTO,
    "CharacterCreateDTO": CharacterCreateDTO,
    "CharacterDetailsDTO": CharacterDetailsDTO,
    "CharacterInfoDraftDTO": CharacterInfoDraftDTO,
    "CharacterItemForNameDTO": CharacterItemForNameDTO,
    "CharacterQueryDTO": CharacterQueryDTO,
    "CharacterSummaryDTO": CharacterSummaryDTO,
    "CharacterSummaryStatsDTO": CharacterSummaryStatsDTO,
    "CharacterUpdateDTO": CharacterUpdateDTO,
    "ChatContentDTO": ChatContentDTO,
    "ChatCreateDTO": ChatCreateDTO,
    "ChatMessageDTO": ChatMessageDTO,
    "ChatPromptContentDTO": ChatPromptContentDTO,
    "ChatToolCallDTO": ChatToolCallDTO,
    "FlowCreateDTO": FlowCreateDTO,
    "FlowDetailsDTO": FlowDetailsDTO,
    "FlowItemForNameDTO": FlowItemForNameDTO,
    "FlowQueryDTO": FlowQueryDTO,
    "FlowSummaryDTO": FlowSummaryDTO,
    "FlowSummaryStatsDTO": FlowSummaryStatsDTO,
    "FlowUpdateDTO": FlowUpdateDTO,
    "InteractiveStatsDTO": InteractiveStatsDTO,
    "LlmResultDTO": LlmResultDTO,
    "LlmTokenUsageDTO": LlmTokenUsageDTO,
    "OpenAiParamDTO": OpenAiParamDTO,
    "PluginCreateDTO": PluginCreateDTO,
    "PluginDetailsDTO": PluginDetailsDTO,
    "PluginQueryDTO": PluginQueryDTO,
    "PluginSummaryDTO": PluginSummaryDTO,
    "PluginSummaryStatsDTO": PluginSummaryStatsDTO,
    "PluginUpdateDTO": PluginUpdateDTO,
    "PromptAiParamDTO": PromptAiParamDTO,
    "PromptCreateDTO": PromptCreateDTO,
    "PromptDetailsDTO": PromptDetailsDTO,
    "PromptItemForNameDTO": PromptItemForNameDTO,
    "PromptQueryDTO": PromptQueryDTO,
    "PromptRefDTO": PromptRefDTO,
    "PromptSummaryDTO": PromptSummaryDTO,
    "PromptSummaryStatsDTO": PromptSummaryStatsDTO,
    "PromptTaskDTO": PromptTaskDTO,
    "PromptTaskDetailsDTO": PromptTaskDetailsDTO,
    "PromptTemplateDTO": PromptTemplateDTO,
    "PromptUpdateDTO": PromptUpdateDTO,
    "QwenParamDTO": QwenParamDTO,
    "SseEmitter": SseEmitter,
    "UserBasicInfoDTO": UserBasicInfoDTO,
    "UserDetailsDTO": UserDetailsDTO,
    "UserFullDetailsDTO": UserFullDetailsDTO,
    "Where": Where,
}

export class ObjectSerializer {
    public static findCorrectType(data: any, expectedType: string) {
        if (data == undefined) {
            return expectedType;
        } else if (primitives.indexOf(expectedType.toLowerCase()) !== -1) {
            return expectedType;
        } else if (expectedType === "Date") {
            return expectedType;
        } else {
            if (enumsMap.has(expectedType)) {
                return expectedType;
            }

            if (!typeMap[expectedType]) {
                return expectedType; // w/e we don't know the type
            }

            // Check the discriminator
            let discriminatorProperty = typeMap[expectedType].discriminator;
            if (discriminatorProperty == null) {
                return expectedType; // the type does not have a discriminator. use it.
            } else {
                if (data[discriminatorProperty]) {
                    var discriminatorType = data[discriminatorProperty];
                    if(typeMap[discriminatorType]){
                        return discriminatorType; // use the type given in the discriminator
                    } else {
                        return expectedType; // discriminator did not map to a type
                    }
                } else {
                    return expectedType; // discriminator was not present (or an empty string)
                }
            }
        }
    }

    public static serialize(data: any, type: string, format: string) {
        if (data == undefined) {
            return data;
        } else if (primitives.indexOf(type.toLowerCase()) !== -1) {
            return data;
        } else if (type.lastIndexOf("Array<", 0) === 0) { // string.startsWith pre es6
            let subType: string = type.replace("Array<", ""); // Array<Type> => Type>
            subType = subType.substring(0, subType.length - 1); // Type> => Type
            let transformedData: any[] = [];
            for (let date of data) {
                transformedData.push(ObjectSerializer.serialize(date, subType, format));
            }
            return transformedData;
        } else if (type === "Date") {
            if (format == "date") {
                let month = data.getMonth()+1
                month = month < 10 ? "0" + month.toString() : month.toString()
                let day = data.getDate();
                day = day < 10 ? "0" + day.toString() : day.toString();

                return data.getFullYear() + "-" + month + "-" + day;
            } else {
                return data.toISOString();
            }
        } else {
            if (enumsMap.has(type)) {
                return data;
            }
            if (!typeMap[type]) { // in case we dont know the type
                return data;
            }

            // Get the actual type of this object
            type = this.findCorrectType(data, type);

            // get the map for the correct type.
            let attributeTypes = typeMap[type].getAttributeTypeMap();
            let instance: {[index: string]: any} = {};
            for (let attributeType of attributeTypes) {
                instance[attributeType.baseName] = ObjectSerializer.serialize(data[attributeType.name], attributeType.type, attributeType.format);
            }
            return instance;
        }
    }

    public static deserialize(data: any, type: string, format: string) {
        // polymorphism may change the actual type.
        type = ObjectSerializer.findCorrectType(data, type);
        if (data == undefined) {
            return data;
        } else if (primitives.indexOf(type.toLowerCase()) !== -1) {
            return data;
        } else if (type.lastIndexOf("Array<", 0) === 0) { // string.startsWith pre es6
            let subType: string = type.replace("Array<", ""); // Array<Type> => Type>
            subType = subType.substring(0, subType.length - 1); // Type> => Type
            let transformedData: any[] = [];
            for (let date of data) {
                transformedData.push(ObjectSerializer.deserialize(date, subType, format));
            }
            return transformedData;
        } else if (type === "Date") {
            return new Date(data);
        } else {
            if (enumsMap.has(type)) {// is Enum
                return data;
            }

            if (!typeMap[type]) { // dont know the type
                return data;
            }
            let instance = new typeMap[type]();
            let attributeTypes = typeMap[type].getAttributeTypeMap();
            for (let attributeType of attributeTypes) {
                let value = ObjectSerializer.deserialize(data[attributeType.baseName], attributeType.type, attributeType.format);
                if (value !== undefined) {
                    instance[attributeType.name] = value;
                }
            }
            return instance;
        }
    }


    /**
     * Normalize media type
     *
     * We currently do not handle any media types attributes, i.e. anything
     * after a semicolon. All content is assumed to be UTF-8 compatible.
     */
    public static normalizeMediaType(mediaType: string | undefined): string | undefined {
        if (mediaType === undefined) {
            return undefined;
        }
        return mediaType.split(";")[0].trim().toLowerCase();
    }

    /**
     * From a list of possible media types, choose the one we can handle best.
     *
     * The order of the given media types does not have any impact on the choice
     * made.
     */
    public static getPreferredMediaType(mediaTypes: Array<string>): string {
        /** According to OAS 3 we should default to json */
        if (mediaTypes.length === 0) {
            return "application/json";
        }

        const normalMediaTypes = mediaTypes.map(this.normalizeMediaType);
        let selectedMediaType: string | undefined = undefined;
        let selectedRank: number = -Infinity;
        for (const mediaType of normalMediaTypes) {
            if (supportedMediaTypes[mediaType!] > selectedRank) {
                selectedMediaType = mediaType;
                selectedRank = supportedMediaTypes[mediaType!];
            }
        }

        if (selectedMediaType === undefined) {
            throw new Error("None of the given media types are supported: " + mediaTypes.join(", "));
        }

        return selectedMediaType!;
    }

    /**
     * Convert data to a string according the given media type
     */
    public static stringify(data: any, mediaType: string): string {
        if (mediaType === "text/plain") {
            return String(data);
        }

        if (mediaType === "application/json" || mediaType === "application/json-patch+json" || mediaType === "application/merge-patch+json" || mediaType === "application/strategic-merge-patch+json") {
            return JSON.stringify(data);
        }

        throw new Error("The mediaType " + mediaType + " is not supported by ObjectSerializer.stringify.");
    }

    /**
     * Parse data from a string according to the given media type
     */
    public static parse(rawData: string, mediaType: string | undefined) {
        if (mediaType === undefined) {
            throw new Error("Cannot parse content. No Content-Type defined.");
        }

        if (mediaType === "text/plain") {
            return rawData;
        }

        if (mediaType === "application/json" || mediaType === "application/json-patch+json" || mediaType === "application/merge-patch+json" || mediaType === "application/strategic-merge-patch+json") {
            return JSON.parse(rawData);
        }

        if (mediaType === "text/html") {
            return rawData;
        }

        throw new Error("The mediaType " + mediaType + " is not supported by ObjectSerializer.parse.");
    }
}
