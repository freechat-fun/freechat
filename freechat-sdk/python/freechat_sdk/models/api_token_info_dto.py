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

from datetime import datetime
from pydantic import BaseModel, Field, StrictInt, StrictStr
from typing import Any, ClassVar, Dict, List, Optional
from typing import Optional, Set
from typing_extensions import Self

class ApiTokenInfoDTO(BaseModel):
    """
    API token information
    """ # noqa: E501
    request_id: Optional[StrictStr] = Field(default=None, description="Request identifier", alias="requestId")
    id: Optional[StrictInt] = Field(default=None, description="Token identifier")
    gmt_create: Optional[datetime] = Field(default=None, description="Creation time", alias="gmtCreate")
    gmt_modified: Optional[datetime] = Field(default=None, description="Modification time", alias="gmtModified")
    type: Optional[StrictStr] = Field(default=None, description="Token type")
    issued_at: Optional[datetime] = Field(default=None, description="Token issuance time", alias="issuedAt")
    expires_at: Optional[datetime] = Field(default=None, description="Token expiration time", alias="expiresAt")
    token: Optional[StrictStr] = Field(default=None, description="Token content")
    policy: Optional[StrictStr] = Field(default=None, description="Token policy")
    username: Optional[StrictStr] = Field(default=None, description="Token owner")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["requestId", "id", "gmtCreate", "gmtModified", "type", "issuedAt", "expiresAt", "token", "policy", "username"]

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
        """Create an instance of ApiTokenInfoDTO from a JSON string"""
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
        """Create an instance of ApiTokenInfoDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "requestId": obj.get("requestId"),
            "id": obj.get("id"),
            "gmtCreate": obj.get("gmtCreate"),
            "gmtModified": obj.get("gmtModified"),
            "type": obj.get("type"),
            "issuedAt": obj.get("issuedAt"),
            "expiresAt": obj.get("expiresAt"),
            "token": obj.get("token"),
            "policy": obj.get("policy"),
            "username": obj.get("username")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


