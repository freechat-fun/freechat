# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.6
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json

from datetime import datetime
from typing import Any, ClassVar, Dict, List, Optional, Union
from pydantic import BaseModel, StrictStr
from pydantic import Field
from freechat-sdk.models.prompt_ref_dto import PromptRefDTO
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class PromptTaskDetailsDTO(BaseModel):
    """
    Prompt task detailed information
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    task_id: Optional[StrictStr] = Field(default=None, description="Prompt task identifier", alias="taskId")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    gmt_executed: Optional[datetime] = Field(default=None, description="Latest executed time", alias="gmtExecuted")
    prompt_ref: Optional[PromptRefDTO] = Field(default=None, alias="promptRef")
    model_id: Optional[StrictStr] = Field(default=None, description="Model identifier", alias="modelId")
    api_key_name: Optional[StrictStr] = Field(default=None, description="API-KEY name", alias="apiKeyName")
    params: Optional[Dict[str, Union[str, Any]]] = Field(default=None, description="Model call parameters")
    cron: Optional[StrictStr] = Field(default=None, description="Task scheduling configuration which compatible with Quartz cron format")
    status: Optional[StrictStr] = Field(default=None, description="Task execution status: pending | running | succeeded | failed")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "taskId", "gmtCreate", "gmtModified", "gmtExecuted", "promptRef", "modelId", "apiKeyName", "params", "cron", "status"]

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
        """Create an instance of PromptTaskDetailsDTO from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of prompt_ref
        if self.prompt_ref:
            _dict['promptRef'] = self.prompt_ref.to_dict()
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of PromptTaskDetailsDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "taskId": obj.get("taskId"),
            "gmtCreate": obj.get("gmtCreate"),
            "gmtModified": obj.get("gmtModified"),
            "gmtExecuted": obj.get("gmtExecuted"),
            "promptRef": PromptRefDTO.from_dict(obj.get("promptRef")) if obj.get("promptRef") is not None else None,
            "modelId": obj.get("modelId"),
            "apiKeyName": obj.get("apiKeyName"),
            "params": obj.get("params"),
            "cron": obj.get("cron"),
            "status": obj.get("status")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


