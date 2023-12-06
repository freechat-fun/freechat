# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.1.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json


from typing import Any, ClassVar, Dict, List, Optional, Union
from pydantic import BaseModel, StrictStr
from pydantic import Field
from freechat-sdk.models.chat_prompt_content_dto import ChatPromptContentDTO
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class PromptTemplateDTO(BaseModel):
    """
    Prompt template content
    """ # noqa: E501
    template: Optional[StrictStr] = Field(default=None, description="Prompt text template content, choose one between this and chatTemplate field, priority: template > chatTemplate")
    chat_template: Optional[ChatPromptContentDTO] = Field(default=None, alias="chatTemplate")
    variables: Optional[Dict[str, Union[str, Any]]] = Field(default=None, description="Variables applied to the template, can be empty")
    format: Optional[StrictStr] = Field(default=None, description="Prompt format: mustache (default) | f_string")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["template", "chatTemplate", "variables", "format"]

    model_config = {
        "populate_by_name": True,
        "validate_assignment": True
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
        """Create an instance of PromptTemplateDTO from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of chat_template
        if self.chat_template:
            _dict['chatTemplate'] = self.chat_template.to_dict()
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of PromptTemplateDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "template": obj.get("template"),
            "chatTemplate": ChatPromptContentDTO.from_dict(obj.get("chatTemplate")) if obj.get("chatTemplate") is not None else None,
            "variables": obj.get("variables"),
            "format": obj.get("format")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


