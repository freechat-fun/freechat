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

from datetime import datetime
from typing import Any, ClassVar, Dict, List, Optional
from pydantic import BaseModel, StrictInt, StrictStr
from pydantic import Field
from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class FlowSummaryDTO(BaseModel):
    """
    Flow summary information
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    flow_id: Optional[StrictStr] = Field(default=None, description="Flow identifier", alias="flowId")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    parent_id: Optional[StrictStr] = Field(default=None, description="Referenced flow", alias="parentId")
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private, public, public_org, hidden")
    format: Optional[StrictStr] = Field(default=None, description="Flow format, currently supported: langflow")
    version: Optional[StrictInt] = Field(default=None, description="Version")
    name: Optional[StrictStr] = Field(default=None, description="Flow name")
    description: Optional[StrictStr] = Field(default=None, description="Flow description")
    username: Optional[StrictStr] = Field(default=None, description="Flow owner")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    ai_models: Optional[List[AiModelInfoDTO]] = Field(default=None, description="Supported model set", alias="aiModels")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "flowId", "gmtCreate", "gmtModified", "parentId", "visibility", "format", "version", "name", "description", "username", "tags", "aiModels"]

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
        """Create an instance of FlowSummaryDTO from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of each item in ai_models (list)
        _items = []
        if self.ai_models:
            for _item in self.ai_models:
                if _item:
                    _items.append(_item.to_dict())
            _dict['aiModels'] = _items
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of FlowSummaryDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "flowId": obj.get("flowId"),
            "gmtCreate": obj.get("gmtCreate"),
            "gmtModified": obj.get("gmtModified"),
            "parentId": obj.get("parentId"),
            "visibility": obj.get("visibility"),
            "format": obj.get("format"),
            "version": obj.get("version"),
            "name": obj.get("name"),
            "description": obj.get("description"),
            "username": obj.get("username"),
            "tags": obj.get("tags"),
            "aiModels": [AiModelInfoDTO.from_dict(_item) for _item in obj.get("aiModels")] if obj.get("aiModels") is not None else None
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


