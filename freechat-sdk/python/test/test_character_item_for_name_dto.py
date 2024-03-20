# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.character_item_for_name_dto import CharacterItemForNameDTO

class TestCharacterItemForNameDTO(unittest.TestCase):
    """CharacterItemForNameDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterItemForNameDTO:
        """Test CharacterItemForNameDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterItemForNameDTO`
        """
        model = CharacterItemForNameDTO()
        if include_optional:
            return CharacterItemForNameDTO(
                character_id = 56,
                version = 56,
                stats = freechat_sdk.models.interactive_stats_dto.InteractiveStatsDTO(
                    request_id = '', 
                    gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                    gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                    refer_type = '', 
                    refer_id = '', 
                    view_count = 56, 
                    refer_count = 56, 
                    recommend_count = 56, 
                    score_count = 56, 
                    score = 56, )
            )
        else:
            return CharacterItemForNameDTO(
        )
        """

    def testCharacterItemForNameDTO(self):
        """Test CharacterItemForNameDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
