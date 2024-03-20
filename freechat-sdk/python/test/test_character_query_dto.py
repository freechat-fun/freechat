# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.character_query_dto import CharacterQueryDTO

class TestCharacterQueryDTO(unittest.TestCase):
    """CharacterQueryDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterQueryDTO:
        """Test CharacterQueryDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterQueryDTO`
        """
        model = CharacterQueryDTO()
        if include_optional:
            return CharacterQueryDTO(
                where = freechat_sdk.models.character_query/where.CharacterQuery.Where(
                    visibility = '', 
                    username = '', 
                    tags = [
                        ''
                        ], 
                    tags_op = '', 
                    name = '', 
                    lang = '', 
                    text = '', ),
                order_by = [
                    ''
                    ],
                page_num = 56,
                page_size = 56
            )
        else:
            return CharacterQueryDTO(
        )
        """

    def testCharacterQueryDTO(self):
        """Test CharacterQueryDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
