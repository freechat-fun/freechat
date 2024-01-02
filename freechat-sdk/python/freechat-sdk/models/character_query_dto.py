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


from typing import Any, ClassVar, Dict, List, Optional
from pydantic import BaseModel, StrictInt, StrictStr
from pydantic import Field
from freechat-sdk.models.character_query_where import CharacterQueryWhere
try:
    from typing import Self
except ImportError:
    from typing_extensions import Self

class CharacterQueryDTO(BaseModel):
    """
    Character query request
    """ # noqa: E501
    where: Optional[CharacterQueryWhere] = None
    order_by: Optional[List[StrictStr]] = Field(default=None, description="Sorting condition, supported sorting fields are: - version - modifyTime - createTime  Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\\"score\\\", \\\"scoreCount asc\\\"] (scoreCount in ascending order) ", alias="orderBy")
    page_num: Optional[StrictInt] = Field(default=None, description="Page number, default is 0", alias="pageNum")
    page_size: Optional[StrictInt] = Field(default=None, description="Number of items per page, 1～50, default is 10", alias="pageSize")
    additional_properties: Dict[str, Any] = {}
    __properties: ClassVar[List[str]] = ["where", "orderBy", "pageNum", "pageSize"]

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
        """Create an instance of CharacterQueryDTO from a JSON string"""
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
        # override the default output from pydantic by calling `to_dict()` of where
        if self.where:
            _dict['where'] = self.where.to_dict()
        # puts key-value pairs in additional_properties in the top level
        if self.additional_properties is not None:
            for _key, _value in self.additional_properties.items():
                _dict[_key] = _value

        return _dict

    @classmethod
    def from_dict(cls, obj: Dict) -> Self:
        """Create an instance of CharacterQueryDTO from a dict"""
        if obj is None:
            return None

        if not isinstance(obj, dict):
            return cls.model_validate(obj)

        _obj = cls.model_validate({
            "where": CharacterQueryWhere.from_dict(obj.get("where")) if obj.get("where") is not None else None,
            "orderBy": obj.get("orderBy"),
            "pageNum": obj.get("pageNum"),
            "pageSize": obj.get("pageSize")
        })
        # store additional fields in additional_properties
        for _key in obj.keys():
            if _key not in cls.__properties:
                _obj.additional_properties[_key] = obj.get(_key)

        return _obj


