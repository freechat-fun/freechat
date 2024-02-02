# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.15
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat_sdk.models.character_update_dto import CharacterUpdateDTO

class TestCharacterUpdateDTO(unittest.TestCase):
    """CharacterUpdateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterUpdateDTO:
        """Test CharacterUpdateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterUpdateDTO`
        """
        model = CharacterUpdateDTO()
        if include_optional:
            return CharacterUpdateDTO(
                parent_id = '',
                visibility = '',
                name = '',
                description = '',
                nickname = '',
                avatar = '',
                picture = '',
                gender = '',
                profile = '',
                greeting = '',
                chat_style = '',
                chat_example = '',
                lang = '',
                ext = '',
                draft = '',
                tags = [
                    ''
                    ]
            )
        else:
            return CharacterUpdateDTO(
                name = '',
        )
        """

    def testCharacterUpdateDTO(self):
        """Test CharacterUpdateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
