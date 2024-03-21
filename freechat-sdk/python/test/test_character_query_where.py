# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.6.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.character_query_where import CharacterQueryWhere

class TestCharacterQueryWhere(unittest.TestCase):
    """CharacterQueryWhere unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterQueryWhere:
        """Test CharacterQueryWhere
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterQueryWhere`
        """
        model = CharacterQueryWhere()
        if include_optional:
            return CharacterQueryWhere(
                visibility = '',
                username = '',
                tags = [
                    ''
                    ],
                tags_op = '',
                name = '',
                lang = '',
                text = ''
            )
        else:
            return CharacterQueryWhere(
        )
        """

    def testCharacterQueryWhere(self):
        """Test CharacterQueryWhere"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
