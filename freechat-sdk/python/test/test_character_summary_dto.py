# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.6
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat-sdk.models.character_summary_dto import CharacterSummaryDTO

class TestCharacterSummaryDTO(unittest.TestCase):
    """CharacterSummaryDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterSummaryDTO:
        """Test CharacterSummaryDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterSummaryDTO`
        """
        model = CharacterSummaryDTO()
        if include_optional:
            return CharacterSummaryDTO(
                request_id = '',
                character_id = '',
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                visibility = '',
                version = 56,
                name = '',
                description = '',
                avatar = '',
                picture = '',
                lang = '',
                username = '',
                tags = [
                    ''
                    ]
            )
        else:
            return CharacterSummaryDTO(
                name = '',
        )
        """

    def testCharacterSummaryDTO(self):
        """Test CharacterSummaryDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
