# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.6.0
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

class CharacterUpdateDTO(BaseModel):
    """
    Request data for updating character information
    """ # noqa: E501
    parent_uid: Optional[StrictStr] = Field(default=None, description="Referenced character", alias="parentUid")
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private (default), public, public_org, hidden")
    name: StrictStr = Field(description="Character name")
    description: Optional[StrictStr] = Field(default=None, description="Character description")
    nickname: Optional[StrictStr] = Field(default=None, description="Character nickname")
    avatar: Optional[StrictStr] = Field(default=None, description="Character avatar url")
    picture: Optional[StrictStr] = Field(default=None, description="Character picture url")
    gender: Optional[StrictStr] = Field(default=None, description="Character gender: male | female | other")
    profile: Optional[StrictStr] = Field(default=None, description="Character profile")
    greeting: Optional[StrictStr] = Field(default=None, description="Character greeting")
    chat_style: Optional[StrictStr] = Field(default=None, description="Character chat-style", alias="chatStyle")
    chat_example: Optional[StrictStr] = Field(default=None, description="Character chat-example", alias="chatExample")
    lang: Optional[StrictStr] = Field(default=None, description="Character language: en (default) | zh | ...")
    ext: Optional[StrictStr] = Field(default=None, description="Additional information, JSON format")
    draft: Optional[StrictStr] = Field(default=None, description="Character draft information")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["parentUid", "visibility", "name", "description", "nickname", "avatar", "picture", "gender", "profile", "greeting", "chatStyle", "chatExample", "lang", "ext", "draft", "tags"]

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
        """Create an instance of CharacterUpdateDTO from a JSON string"""
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
        """Create an instance of CharacterUpdateDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "parentUid": obj.get("parentUid"),
            "visibility": obj.get("visibility"),
            "name": obj.get("name"),
            "description": obj.get("description"),
            "nickname": obj.get("nickname"),
            "avatar": obj.get("avatar"),
            "picture": obj.get("picture"),
            "gender": obj.get("gender"),
            "profile": obj.get("profile"),
            "greeting": obj.get("greeting"),
            "chatStyle": obj.get("chatStyle"),
            "chatExample": obj.get("chatExample"),
            "lang": obj.get("lang"),
            "ext": obj.get("ext"),
            "draft": obj.get("draft"),
            "tags": obj.get("tags")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


