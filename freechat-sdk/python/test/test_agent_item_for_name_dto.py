# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.7.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.agent_item_for_name_dto import AgentItemForNameDTO

class TestAgentItemForNameDTO(unittest.TestCase):
    """AgentItemForNameDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> AgentItemForNameDTO:
        """Test AgentItemForNameDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `AgentItemForNameDTO`
        """
        model = AgentItemForNameDTO()
        if include_optional:
            return AgentItemForNameDTO(
                agent_id = 56,
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
            return AgentItemForNameDTO(
        )
        """

    def testAgentItemForNameDTO(self):
        """Test AgentItemForNameDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
