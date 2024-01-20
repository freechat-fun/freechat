# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.11
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat_sdk.models.character_summary_stats_dto import CharacterSummaryStatsDTO

class TestCharacterSummaryStatsDTO(unittest.TestCase):
    """CharacterSummaryStatsDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterSummaryStatsDTO:
        """Test CharacterSummaryStatsDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterSummaryStatsDTO`
        """
        model = CharacterSummaryStatsDTO()
        if include_optional:
            return CharacterSummaryStatsDTO(
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
                    ],
                view_count = 56,
                refer_count = 56,
                recommend_count = 56,
                score_count = 56,
                score = 56
            )
        else:
            return CharacterSummaryStatsDTO(
                name = '',
        )
        """

    def testCharacterSummaryStatsDTO(self):
        """Test CharacterSummaryStatsDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
