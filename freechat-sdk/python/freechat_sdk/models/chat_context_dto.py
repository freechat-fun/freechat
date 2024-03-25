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

from datetime import datetime
from pydantic import BaseModel, Field, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from typing import Optional, Set
from typing_extensions import Self

class ChatContextDTO(BaseModel):
    """
    Chat context
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    chat_id: Optional[StrictStr] = Field(default=None, description="Chat identifier", alias="chatId")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    gmt_read: Optional[datetime] = Field(default=None, description="Read time", alias="gmtRead")
    user_nickname: Optional[StrictStr] = Field(default=None, description="User nickname for this session", alias="userNickname")
    user_profile: Optional[StrictStr] = Field(default=None, description="User profile for this session", alias="userProfile")
    character_nickname: Optional[StrictStr] = Field(default=None, description="Character nickname for this session", alias="characterNickname")
    about: Optional[StrictStr] = Field(default=None, description="Anything about this session")
    backend_id: StrictStr = Field(description="Character backend for this session", alias="backendId")
    ext: Optional[StrictStr] = Field(default=None, description="Extra info for this session")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "chatId", "gmtCreate", "gmtModified", "gmtRead", "userNickname", "userProfile", "characterNickname", "about", "backendId", "ext"]

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
        """Create an instance of ChatContextDTO from a JSON string"""
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
        """Create an instance of ChatContextDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "chatId": obj.get("chatId"),
            "gmtCreate": obj.get("gmtCreate"),
            "gmtModified": obj.get("gmtModified"),
            "gmtRead": obj.get("gmtRead"),
            "userNickname": obj.get("userNickname"),
            "userProfile": obj.get("userProfile"),
            "characterNickname": obj.get("characterNickname"),
            "about": obj.get("about"),
            "backendId": obj.get("backendId"),
            "ext": obj.get("ext")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj

