# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.0
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

class CharacterSummaryStatsDTO(BaseModel):
    """
    Character summary content, including interactive statistical information
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    character_id: Optional[StrictStr] = Field(default=None, description="Character identifier", alias="characterId")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    visibility: Optional[StrictStr] = Field(default=None, description="Visibility: private, public, public_org, hidden")
    version: Optional[StrictInt] = Field(default=None, description="Version")
    name: StrictStr = Field(description="Character name")
    description: Optional[StrictStr] = Field(default=None, description="Character description")
    avatar: Optional[StrictStr] = Field(default=None, description="Character avatar url")
    picture: Optional[StrictStr] = Field(default=None, description="Character picture url")
    lang: Optional[StrictStr] = Field(default=None, description="Character language: English | Chinese (Simplified) | ...")
    username: Optional[StrictStr] = Field(default=None, description="Character owner")
    tags: Optional[List[StrictStr]] = Field(default=None, description="Tag set")
    view_count: Optional[StrictInt] = Field(default=None, description="View count", alias="viewCount")
    refer_count: Optional[StrictInt] = Field(default=None, description="Reference count", alias="referCount")
    recommend_count: Optional[StrictInt] = Field(default=None, description="Recommendation count", alias="recommendCount")
    score_count: Optional[StrictInt] = Field(default=None, description="Score count", alias="scoreCount")
    score: Optional[StrictInt] = Field(default=None, description="Average score")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "characterId", "gmtCreate", "gmtModified", "visibility", "version", "name", "description", "avatar", "picture", "lang", "username", "tags", "viewCount", "referCount", "recommendCount", "scoreCount", "score"]

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
        """Create an instance of CharacterSummaryStatsDTO from a JSON string"""
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
        """Create an instance of CharacterSummaryStatsDTO from a dict"""
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
            "avatar": obj.get("avatar"),
            "picture": obj.get("picture"),
            "lang": obj.get("lang"),
            "username": obj.get("username"),
            "tags": obj.get("tags"),
            "viewCount": obj.get("viewCount"),
            "referCount": obj.get("referCount"),
            "recommendCount": obj.get("recommendCount"),
            "scoreCount": obj.get("scoreCount"),
            "score": obj.get("score")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


