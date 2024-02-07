# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.15
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json


from typing import Any, ClassVar, Dict, List, Optional, Union
from pydantic import BaseModel, StrictFloat, StrictInt, StrictStr
from pydantic import Field
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class OpenAiParamDTO(BaseModel):
    """
    OpenAI series model parameters
    """ # noqa: E501
    api_key: Optional[StrictStr] = Field(default=None, description="API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified.", alias="apiKey")
    api_key_name: Optional[StrictStr] = Field(default=None, description="API-KEY name", alias="apiKeyName")
    model_id: StrictStr = Field(description="Model identifier", alias="modelId")
    base_url: Optional[StrictStr] = Field(default=None, description="OpenAI service provider address, default: https://api.openai.com/v1", alias="baseUrl")
    temperature: Optional[Union[StrictFloat, StrictInt]] = Field(default=None, description="Used to adjust the degree of randomness from sampling in the generated model, the value range is (0, 1.0), a temperature of 0 will always produce the same output. The higher the temperature, the greater the randomness.")
    top_p: Optional[Union[StrictFloat, StrictInt]] = Field(default=None, description="Probability threshold of the nucleus sampling method in the generation process, for example, when the value is 0.8, only the smallest set of most likely tokens whose probabilities add up to 0.8 or more is retained as the candidate set. The value range is (0, 1.0), the larger the value, the higher the randomness of the generation; the smaller the value, the higher the certainty of the generation.", alias="topP")
    max_tokens: Optional[StrictInt] = Field(default=None, description="The maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model's context length.", alias="maxTokens")
    presence_penalty: Optional[Union[StrictFloat, StrictInt]] = Field(default=None, description="Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model's likelihood to talk about new topics.", alias="presencePenalty")
    frequency_penalty: Optional[Union[StrictFloat, StrictInt]] = Field(default=None, description="Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim.", alias="frequencyPenalty")
    seed: Optional[StrictInt] = Field(default=None, description="If specified, OpenAI will make a best effort to sample deterministically, such that repeated requests with the same seed and parameters should return the same result.")
    stop: Optional[List[StrictStr]] = Field(default=None, description="A collection of stop words that controls the API from generating more tokens.")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["apiKey", "apiKeyName", "modelId", "baseUrl", "temperature", "topP", "maxTokens", "presencePenalty", "frequencyPenalty", "seed", "stop"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True,
        "protected_namespaces": (),
    }


    def to_str(self) -> str:
        """Returns the string representation of the model using alias"""
        return pprint.pformat(self.model_dump(by_alias=True))

    def to_json(self) -> str:
        """Returns the JSON representation of the model using alias"""
        # TODO: pydantic v2: use .model_dump_json(by_alias=True, exclude_unset=True) instead
        return json.dumps(self.to_dict())

    @classmethod
    def from_json(cls, json_str: str) -> Self:
        """Create an instance of OpenAiParamDTO from a JSON string"""
        return cls.from_dict(json.loads(json_str))

    def to_dict(self) -> Dict[str, Any]:
        """Return the dictionary representation of the model using alias.

        This has the following differences from calling pydantic's
        `self.model_dump(by_alias=True)`:

        * `None` is only added to the output dict for nullable fields that
          were set at model initialization. Other fields with value `None`
          are ignored.
        * Fields in `self.additional_properties` are added to the output dict.
        """
        _dict = self.model_dump(
            by_alias=True,
            exclude={
                "additional_properties",
            },
            exclude_none=True,
        )
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of OpenAiParamDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "apiKey": obj.get("apiKey"),
            "apiKeyName": obj.get("apiKeyName"),
            "modelId": obj.get("modelId"),
            "baseUrl": obj.get("baseUrl"),
            "temperature": obj.get("temperature"),
            "topP": obj.get("topP"),
            "maxTokens": obj.get("maxTokens"),
            "presencePenalty": obj.get("presencePenalty"),
            "frequencyPenalty": obj.get("frequencyPenalty"),
            "seed": obj.get("seed"),
            "stop": obj.get("stop")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj

