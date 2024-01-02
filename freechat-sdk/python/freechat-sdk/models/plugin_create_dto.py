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


from typing import Any, ClassVar, Dict, List, Optional
from pydantic import BaseModel, StrictStr
from pydantic import Field
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class PluginCreateDTO(BaseModel):
    """
    Request data for creating new plugin information
    """ # noqa: E501
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private (default), public, public_org, hidden")
    name: StrictStr = Field(description="Plugin name")
    manifest_format: Optional[StrictStr] = Field(default=None, description="Manifest format, currently supported: dash_scope (default), open_ai", alias="manifestFormat")
    manifest_info: Optional[StrictStr] = Field(default=None, description="Manifest content, can be full content or a URL", alias="manifestInfo")
    api_format: Optional[StrictStr] = Field(default=None, description="API description format, currently supported: openapi_v3 (default)", alias="apiFormat")
    api_info: Optional[StrictStr] = Field(default=None, description="API description content, can be full content or a URL", alias="apiInfo")
    provider: Optional[StrictStr] = Field(default=None, description="Provider information, default is the current user's username")
    ext: Optional[StrictStr] = Field(default=None, description="Additional information, JSON format")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    ai_models: Optional[List[StrictStr]] = Field(default=None, description="Supported model set, empty means no model limit", alias="aiModels")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["visibility", "name", "manifestFormat", "manifestInfo", "apiFormat", "apiInfo", "provider", "ext", "tags", "aiModels"]

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
        """Create an instance of PluginCreateDTO from a JSON string"""
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
        """Create an instance of PluginCreateDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "visibility": obj.get("visibility"),
            "name": obj.get("name"),
            "manifestFormat": obj.get("manifestFormat"),
            "manifestInfo": obj.get("manifestInfo"),
            "apiFormat": obj.get("apiFormat"),
            "apiInfo": obj.get("apiInfo"),
            "provider": obj.get("provider"),
            "ext": obj.get("ext"),
            "tags": obj.get("tags"),
            "aiModels": obj.get("aiModels")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


