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

from datetime import datetime
from typing import Any, ClassVar, Dict, List, Optional
from pydantic import BaseModel, StrictStr
from pydantic import Field
from freechat-sdk.models.chat_tool_call_dto import ChatToolCallDTO
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class ChatMessageDTO(BaseModel):
    """
    Chat message
    """ # noqa: E501
    role: Optional[StrictStr] = Field(default=None, description="Chat role: system | assistant | user | function_call | function_result")
    name: Optional[StrictStr] = Field(default=None, description="user: Name of the user role; function_call: Name of the called tool")
    content: Optional[StrictStr] = Field(default=None, description="default: Dialogue content; function_result: function call result, serialized as json")
    tool_calls: Optional[List[ChatToolCallDTO]] = Field(default=None, description="Tool calls information during the conversation", alias="toolCalls")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["role", "name", "content", "toolCalls", "gmtCreate"]

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
        """Create an instance of ChatMessageDTO from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of each item in tool_calls (list)
        _items = []
        if self.tool_calls:
            for _item in self.tool_calls:
                if _item:
                    _items.append(_item.to_dict())
            _dict['toolCalls'] = _items
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of ChatMessageDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "role": obj.get("role"),
            "name": obj.get("name"),
            "content": obj.get("content"),
            "toolCalls": [ChatToolCallDTO.from_dict(_item) for _item in obj.get("toolCalls")] if obj.get("toolCalls") is not None else None,
            "gmtCreate": obj.get("gmtCreate")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


