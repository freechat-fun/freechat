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

class PluginQueryWhere(BaseModel):
    """
    Query condition
    """ # noqa: E501
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: public, public_org (search this organization), private (default)")
    username: Optional[StrictStr] = Field(default=None, description="Effective when searching public, public_org prompts, if not specified, search all users")
    manifest_format: Optional[StrictStr] = Field(default=None, description="Manifest configuration format, currently supported: dash_scope, open_ai.", alias="manifestFormat")
    api_format: Optional[StrictStr] = Field(default=None, description="API description format, currently supported: openapi_v3.", alias="apiFormat")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tags")
    tags_op: Optional[StrictStr] = Field(default=None, description="Relationship between tags: and | or (default)", alias="tagsOp")
    ai_models: Optional[List[StrictStr]] = Field(default=None, description="Model set", alias="aiModels")
    ai_models_op: Optional[StrictStr] = Field(default=None, description="Relationship between model sets: and | or (default)", alias="aiModelsOp")
    name: Optional[StrictStr] = Field(default=None, description="Name, left match")
    provider: Optional[StrictStr] = Field(default=None, description="Provider, left match")
    text: Optional[StrictStr] = Field(default=None, description="Name, provider Information, manifest (real-time pulling is not supported at the moment), fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time.")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["visibility", "username", "manifestFormat", "apiFormat", "tags", "tagsOp", "aiModels", "aiModelsOp", "name", "provider", "text"]

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
        """Create an instance of PluginQueryWhere from a JSON string"""
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
        """Create an instance of PluginQueryWhere from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "visibility": obj.get("visibility"),
            "username": obj.get("username"),
            "manifestFormat": obj.get("manifestFormat"),
            "apiFormat": obj.get("apiFormat"),
            "tags": obj.get("tags"),
            "tagsOp": obj.get("tagsOp"),
            "aiModels": obj.get("aiModels"),
            "aiModelsOp": obj.get("aiModelsOp"),
            "name": obj.get("name"),
            "provider": obj.get("provider"),
            "text": obj.get("text")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


