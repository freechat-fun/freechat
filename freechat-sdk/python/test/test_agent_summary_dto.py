# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.agent_summary_dto import AgentSummaryDTO

class TestAgentSummaryDTO(unittest.TestCase):
    """AgentSummaryDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> AgentSummaryDTO:
        """Test AgentSummaryDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `AgentSummaryDTO`
        """
        model = AgentSummaryDTO()
        if include_optional:
            return AgentSummaryDTO(
                request_id = '',
                agent_id = 56,
                agent_uid = '',
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                parent_uid = '',
                visibility = '',
                format = '',
                version = 56,
                name = '',
                description = '',
                username = '',
                tags = [
                    ''
                    ],
                ai_models = [
                    freechat_sdk.models.ai_model_info_dto.AiModelInfoDTO(
                        request_id = '', 
                        model_id = '', 
                        name = '', 
                        description = '', 
                        provider = '', 
                        type = '', )
                    ]
            )
        else:
            return AgentSummaryDTO(
        )
        """

    def testAgentSummaryDTO(self):
        """Test AgentSummaryDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
