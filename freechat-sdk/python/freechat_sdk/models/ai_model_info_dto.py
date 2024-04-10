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

class AiModelInfoDTO(BaseModel):
    """
    Model information
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    model_id: Optional[StrictStr] = Field(default=None, description="Model identifier: [provider]name", alias="modelId")
    name: Optional[StrictStr] = Field(default=None, description="Model name")
    description: Optional[StrictStr] = Field(default=None, description="Model description")
    provider: Optional[StrictStr] = Field(default=None, description="Model provider: hugging_face | open_ai | local_ai | in_process | dash_scope | unknown")
    type: Optional[StrictStr] = Field(default=None, description="Model type: text2text | text2chat | text2image | embedding | moderation")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "modelId", "name", "description", "provider", "type"]

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
        """Create an instance of AiModelInfoDTO from a JSON string"""
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
        """Create an instance of AiModelInfoDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "modelId": obj.get("modelId"),
            "name": obj.get("name"),
            "description": obj.get("description"),
            "provider": obj.get("provider"),
            "type": obj.get("type")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


