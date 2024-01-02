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

from freechat-sdk.models.flow_update_dto import FlowUpdateDTO

class TestFlowUpdateDTO(unittest.TestCase):
    """FlowUpdateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> FlowUpdateDTO:
        """Test FlowUpdateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `FlowUpdateDTO`
        """
        model = FlowUpdateDTO()
        if include_optional:
            return FlowUpdateDTO(
                parent_id = '',
                visibility = '',
                format = '',
                name = '',
                description = '',
                config = '',
                example = '',
                parameters = '',
                ext = '',
                draft = '',
                tags = [
                    ''
                    ],
                ai_models = [
                    ''
                    ]
            )
        else:
            return FlowUpdateDTO(
                name = '',
        )
        """

    def testFlowUpdateDTO(self):
        """Test FlowUpdateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
