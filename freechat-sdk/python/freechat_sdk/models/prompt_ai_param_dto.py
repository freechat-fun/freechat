# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.4.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json

from pydantic import BaseModel, Field, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from freechat_sdk.models.prompt_ref_dto import PromptRefDTO
from freechat_sdk.models.prompt_template_dto import PromptTemplateDTO
from typing import Optional, Set
from typing_extensions import Self

class PromptAiParamDTO(BaseModel):
    """
    Prompt AI service information
    """ # noqa: E501
    prompt: Optional[StrictStr] = Field(default=None, description="Complete input content, priority: prompt > promptTemplate > promptRef")
    prompt_template: Optional[PromptTemplateDTO] = Field(default=None, alias="promptTemplate")
    prompt_ref: Optional[PromptRefDTO] = Field(default=None, alias="promptRef")
    params: Dict[str, Dict[str, Any]] = Field(description="Model call parameters, the actual supported fields are related to modelId, depending on the model provider, specific fields can refer to: OpenAiParamDTO, QwenParamDTO")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["prompt", "promptTemplate", "promptRef", "params"]

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
    def from_json(cls, json_str: str) -> Optional[Self]:
        """Create an instance of PromptAiParamDTO from a JSON string"""
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
        excluded_fields: Set[str] = set([
            "additional_properties",
        ])

        _dict = self.model_dump(
            by_alias=True,
            exclude=excluded_fields,
            exclude_none=True,
        )
        # override the default output from pydantic by calling `to_dict()` of prompt_template
        if self.prompt_template:
            _dict['promptTemplate'] = self.prompt_template.to_dict()
        # override the default output from pydantic by calling `to_dict()` of prompt_ref
        if self.prompt_ref:
            _dict['promptRef'] = self.prompt_ref.to_dict()
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Optional[Dict[str, Any]]) -> Optional[Self]:
        """Create an instance of PromptAiParamDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "prompt": obj.get("prompt"),
            "promptTemplate": PromptTemplateDTO.from_dict(obj["promptTemplate"]) if obj.get("promptTemplate") is not None else None,
            "promptRef": PromptRefDTO.from_dict(obj["promptRef"]) if obj.get("promptRef") is not None else None,
            "params": obj.get("params")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


