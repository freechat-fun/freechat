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


from typing import Any, ClassVar, Dict, List, Optional
from pydantic import BaseModel, StrictStr
from pydantic import Field
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class CharacterQueryWhere(BaseModel):
    """
    Query condition
    """ # noqa: E501
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: public, public_org (search this organization), private (default)")
    username: Optional[StrictStr] = Field(default=None, description="Effective when searching public, public_org characters, if not specified, search all users")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tags")
    tags_op: Optional[StrictStr] = Field(default=None, description="Relationship between tags: and | or (default)", alias="tagsOp")
    name: Optional[StrictStr] = Field(default=None, description="Name, left match")
    lang: Optional[StrictStr] = Field(default=None, description="Language, exact match")
    text: Optional[StrictStr] = Field(default=None, description="Name, description, profile, chat style, fuzzy match, any one match is sufficient; public scope + general search for all users does not guarantee real-time.")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["visibility", "username", "tags", "tagsOp", "name", "lang", "text"]

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
        """Create an instance of CharacterQueryWhere from a JSON string"""
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
        """Create an instance of CharacterQueryWhere from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "visibility": obj.get("visibility"),
            "username": obj.get("username"),
            "tags": obj.get("tags"),
            "tagsOp": obj.get("tagsOp"),
            "name": obj.get("name"),
            "lang": obj.get("lang"),
            "text": obj.get("text")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


