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

from freechat_sdk.models.agent_query_where import AgentQueryWhere

class TestAgentQueryWhere(unittest.TestCase):
    """AgentQueryWhere unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> AgentQueryWhere:
        """Test AgentQueryWhere
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `AgentQueryWhere`
        """
        model = AgentQueryWhere()
        if include_optional:
            return AgentQueryWhere(
                visibility = '',
                username = '',
                format = '',
                tags = [
                    ''
                    ],
                tags_op = '',
                ai_models = [
                    ''
                    ],
                ai_models_op = '',
                name = '',
                text = ''
            )
        else:
            return AgentQueryWhere(
        )
        """

    def testAgentQueryWhere(self):
        """Test AgentQueryWhere"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()