# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat-sdk.models.flow_details_dto import FlowDetailsDTO

class TestFlowDetailsDTO(unittest.TestCase):
    """FlowDetailsDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> FlowDetailsDTO:
        """Test FlowDetailsDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `FlowDetailsDTO`
        """
        model = FlowDetailsDTO()
        if include_optional:
            return FlowDetailsDTO(
                request_id = '',
                flow_id = '',
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                parent_id = '',
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
                    freechat-sdk.models.ai_model_info_dto.AiModelInfoDTO(
                        request_id = '', 
                        model_id = '', 
                        name = '', 
                        description = '', 
                        provider = '', 
                        type = '', )
                    ],
                config = '',
                example = '',
                parameters = '',
                ext = '',
                draft = ''
            )
        else:
            return FlowDetailsDTO(
        )
        """

    def testFlowDetailsDTO(self):
        """Test FlowDetailsDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
