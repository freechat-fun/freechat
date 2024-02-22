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

from datetime import datetime
from pydantic import BaseModel, Field, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO
from typing import Optional, Set
from typing_extensions import Self

class PluginDetailsDTO(BaseModel):
    """
    Plugin detailed content
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    plugin_id: Optional[StrictStr] = Field(default=None, description="Plugin identifier", alias="pluginId")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private, public, public_org, hidden")
    name: Optional[StrictStr] = Field(default=None, description="Plugin name")
    provider: Optional[StrictStr] = Field(default=None, description="Information of the provider")
    manifest_format: Optional[StrictStr] = Field(default=None, description="Manifest format, currently supported: dash_scope, open_ai", alias="manifestFormat")
    api_format: Optional[StrictStr] = Field(default=None, description="API description format, currently supported: openapi_v3", alias="apiFormat")
    username: Optional[StrictStr] = Field(default=None, description="Plugin owner")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    ai_models: Optional[List[AiModelInfoDTO]] = Field(default=None, description="Supported model set", alias="aiModels")
    manifest_info: Optional[StrictStr] = Field(default=None, description="Manifest content, different formats may have differences", alias="manifestInfo")
    api_info: Optional[StrictStr] = Field(default=None, description="API description content, different formats may have content differences", alias="apiInfo")
    tool_spec_format: Optional[StrictStr] = Field(default=None, description="Tool specification format, currently supported: open_ai", alias="toolSpecFormat")
    tool_spec_info: Optional[StrictStr] = Field(default=None, description="Tool specification content", alias="toolSpecInfo")
    ext: Optional[StrictStr] = Field(default=None, description="Additional information, JSON format")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "pluginId", "gmtCreate", "gmtModified", "visibility", "name", "provider", "manifestFormat", "apiFormat", "username", "tags", "aiModels", "manifestInfo", "apiInfo", "toolSpecFormat", "toolSpecInfo", "ext"]

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
        """Create an instance of PluginDetailsDTO from a JSON string"""
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
        """Create an instance of PluginDetailsDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "pluginId": obj.get("pluginId"),
            "gmtCreate": obj.get("gmtCreate"),
            "gmtModified": obj.get("gmtModified"),
            "visibility": obj.get("visibility"),
            "name": obj.get("name"),
            "provider": obj.get("provider"),
            "manifestFormat": obj.get("manifestFormat"),
            "apiFormat": obj.get("apiFormat"),
            "username": obj.get("username"),
            "tags": obj.get("tags"),
            "aiModels": [AiModelInfoDTO.from_dict(_item) for _item in obj["aiModels"]] if obj.get("aiModels") is not None else None,
            "manifestInfo": obj.get("manifestInfo"),
            "apiInfo": obj.get("apiInfo"),
            "toolSpecFormat": obj.get("toolSpecFormat"),
            "toolSpecInfo": obj.get("toolSpecInfo"),
            "ext": obj.get("ext")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


