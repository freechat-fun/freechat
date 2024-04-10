# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.8.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json

from datetime import datetime
from pydantic import BaseModel, Field, StrictInt, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO
from typing import Optional, Set
from typing_extensions import Self

class AgentSummaryDTO(BaseModel):
    """
    Agent summary information
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    agent_id: Optional[StrictInt] = Field(default=None, description="Agent identifier, will change after publish", alias="agentId")
    agent_uid: Optional[StrictStr] = Field(default=None, description="Agent immutable identifier", alias="agentUid")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    parent_uid: Optional[StrictStr] = Field(default=None, description="Referenced agent", alias="parentUid")
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private, public, public_org, hidden")
    format: Optional[StrictStr] = Field(default=None, description="Agent format, currently supported: langflow")
    version: Optional[StrictInt] = Field(default=None, description="Version")
    name: Optional[StrictStr] = Field(default=None, description="Agent name")
    description: Optional[StrictStr] = Field(default=None, description="Agent description")
    username: Optional[StrictStr] = Field(default=None, description="Agent owner")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    ai_models: Optional[List[AiModelInfoDTO]] = Field(default=None, description="Supported model set", alias="aiModels")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "agentId", "agentUid", "gmtCreate", "gmtModified", "parentUid", "visibility", "format", "version", "name", "description", "username", "tags", "aiModels"]

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
        """Create an instance of AgentSummaryDTO from a JSON string"""
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
    def from_dict(cls, obj: Optional[Dict[str, Any]]) -> Optional[Self]:
        """Create an instance of AgentSummaryDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "agentId": obj.get("agentId"),
            "agentUid": obj.get("agentUid"),
            "gmtCreate": obj.get("gmtCreate"),
            "gmtModified": obj.get("gmtModified"),
            "parentUid": obj.get("parentUid"),
            "visibility": obj.get("visibility"),
            "format": obj.get("format"),
            "version": obj.get("version"),
            "name": obj.get("name"),
            "description": obj.get("description"),
            "username": obj.get("username"),
            "tags": obj.get("tags"),
            "aiModels": [AiModelInfoDTO.from_dict(_item) for _item in obj["aiModels"]] if obj.get("aiModels") is not None else None
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


