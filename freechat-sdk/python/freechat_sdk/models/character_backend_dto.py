# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


from __future__ import annotations
import pprint
import re  # noqa: F401
import json

from pydantic import BaseModel, Field, StrictBool, StrictInt, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from typing import Optional, Set
from typing_extensions import Self

class CharacterBackendDTO(BaseModel):
    """
    Character backend information
    """ # noqa: E501
    is_default: Optional[StrictBool] = Field(default=None, description="Whether it is the default backend", alias="isDefault")
    chat_prompt_task_id: Optional[StrictStr] = Field(default=None, description="Prompt task identifier for chat", alias="chatPromptTaskId")
    greeting_prompt_task_id: Optional[StrictStr] = Field(default=None, description="Prompt task identifier for greeting", alias="greetingPromptTaskId")
    moderation_model_id: Optional[StrictStr] = Field(default=None, description="Moderation model for the character's response", alias="moderationModelId")
    moderation_api_key_name: Optional[StrictStr] = Field(default=None, description="Api key name for moderation model", alias="moderationApiKeyName")
    moderation_params: Optional[StrictStr] = Field(default=None, description="Parameters for moderation model", alias="moderationParams")
    message_window_size: Optional[StrictInt] = Field(default=None, description="Max messages in the character's memory", alias="messageWindowSize")
    forward_to_user: Optional[StrictBool] = Field(default=None, description="Weather to forward messages to the character owner", alias="forwardToUser")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["isDefault", "chatPromptTaskId", "greetingPromptTaskId", "moderationModelId", "moderationApiKeyName", "moderationParams", "messageWindowSize", "forwardToUser"]

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
        """Create an instance of CharacterBackendDTO from a JSON string"""
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
        """Create an instance of CharacterBackendDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "isDefault": obj.get("isDefault"),
            "chatPromptTaskId": obj.get("chatPromptTaskId"),
            "greetingPromptTaskId": obj.get("greetingPromptTaskId"),
            "moderationModelId": obj.get("moderationModelId"),
            "moderationApiKeyName": obj.get("moderationApiKeyName"),
            "moderationParams": obj.get("moderationParams"),
            "messageWindowSize": obj.get("messageWindowSize"),
            "forwardToUser": obj.get("forwardToUser")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


