# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.7.0
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
from typing import Optional, Set
from typing_extensions import Self

class PromptTaskDTO(BaseModel):
    """
    Prompt task information
    """ # noqa: E501
    prompt_ref: PromptRefDTO = Field(alias="promptRef")
    model_id: Optional[StrictStr] = Field(default=None, description="Model identifier", alias="modelId")
    api_key_name: Optional[StrictStr] = Field(default=None, description="API-KEY name, priority: apiKeyName > apiKeyValue", alias="apiKeyName")
    api_key_value: Optional[StrictStr] = Field(default=None, description="API-KEY value", alias="apiKeyValue")
    params: Optional[Dict[str, Dict[str, Any]]] = Field(default=None, description="Model call parameters")
    cron: Optional[StrictStr] = Field(default=None, description="Task scheduling configuration which compatible with Quartz cron format")
    status: Optional[StrictStr] = Field(default=None, description="Task execution status: pending | running | succeeded | failed")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["promptRef", "modelId", "apiKeyName", "apiKeyValue", "params", "cron", "status"]

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
        """Create an instance of PromptTaskDTO from a JSON string"""
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
        """Create an instance of PromptTaskDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "promptRef": PromptRefDTO.from_dict(obj["promptRef"]) if obj.get("promptRef") is not None else None,
            "modelId": obj.get("modelId"),
            "apiKeyName": obj.get("apiKeyName"),
            "apiKeyValue": obj.get("apiKeyValue"),
            "params": obj.get("params"),
            "cron": obj.get("cron"),
            "status": obj.get("status")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


