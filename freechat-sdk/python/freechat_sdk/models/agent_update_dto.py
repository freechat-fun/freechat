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

from pydantic import BaseModel, Field, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from typing import Optional, Set
from typing_extensions import Self

class AgentUpdateDTO(BaseModel):
    """
    Request data for updating agent information
    """ # noqa: E501
    parent_uid: Optional[StrictStr] = Field(default=None, description="Referenced agent", alias="parentUid")
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private (default), public, public_org, hidden")
    format: Optional[StrictStr] = Field(default=None, description="Agent format, currently supported: langflow")
    name: StrictStr = Field(description="Agent name")
    description: Optional[StrictStr] = Field(default=None, description="Agent description")
    config: Optional[StrictStr] = Field(default=None, description="Agent configuration")
    example: Optional[StrictStr] = Field(default=None, description="Agent example")
    parameters: Optional[StrictStr] = Field(default=None, description="Agent parameters, JSON format")
    ext: Optional[StrictStr] = Field(default=None, description="Additional information, JSON format")
    draft: Optional[StrictStr] = Field(default=None, description="Draft content")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    ai_models: Optional[List[StrictStr]] = Field(default=None, description="Supported model set, empty means no model limit", alias="aiModels")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["parentUid", "visibility", "format", "name", "description", "config", "example", "parameters", "ext", "draft", "tags", "aiModels"]

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
        """Create an instance of AgentUpdateDTO from a JSON string"""
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
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Optional[Dict[str, Any]]) -> Optional[Self]:
        """Create an instance of AgentUpdateDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "parentUid": obj.get("parentUid"),
            "visibility": obj.get("visibility"),
            "format": obj.get("format"),
            "name": obj.get("name"),
            "description": obj.get("description"),
            "config": obj.get("config"),
            "example": obj.get("example"),
            "parameters": obj.get("parameters"),
            "ext": obj.get("ext"),
            "draft": obj.get("draft"),
            "tags": obj.get("tags"),
            "aiModels": obj.get("aiModels")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


