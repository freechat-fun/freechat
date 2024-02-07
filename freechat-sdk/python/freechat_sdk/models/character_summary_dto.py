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
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class CharacterSummaryDTO(BaseModel):
    """
    Character summary content
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    character_id: Optional[StrictStr] = Field(default=None, description="Character identifier", alias="characterId")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private, public, public_org, hidden")
    version: Optional[StrictInt] = Field(default=None, description="Version")
    name: StrictStr = Field(description="Character name")
    description: Optional[StrictStr] = Field(default=None, description="Character description")
    nickname: Optional[StrictStr] = Field(default=None, description="Character nickname")
    avatar: Optional[StrictStr] = Field(default=None, description="Character avatar url")
    picture: Optional[StrictStr] = Field(default=None, description="Character picture url")
    gender: Optional[StrictStr] = Field(default=None, description="Character gender: male | female | other")
    lang: Optional[StrictStr] = Field(default=None, description="Character language: English | Chinese (Simplified) | ...")
    username: Optional[StrictStr] = Field(default=None, description="Character owner")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "characterId", "gmtCreate", "gmtModified", "visibility", "version", "name", "description", "nickname", "avatar", "picture", "gender", "lang", "username", "tags"]

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
        """Create an instance of CharacterSummaryDTO from a JSON string"""
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
        """Create an instance of CharacterSummaryDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "characterId": obj.get("characterId"),
            "gmtCreate": obj.get("gmtCreate"),
            "gmtModified": obj.get("gmtModified"),
            "visibility": obj.get("visibility"),
            "version": obj.get("version"),
            "name": obj.get("name"),
            "description": obj.get("description"),
            "nickname": obj.get("nickname"),
            "avatar": obj.get("avatar"),
            "picture": obj.get("picture"),
            "gender": obj.get("gender"),
            "lang": obj.get("lang"),
            "username": obj.get("username"),
            "tags": obj.get("tags")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj

