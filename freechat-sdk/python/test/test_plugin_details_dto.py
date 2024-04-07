# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.7.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.plugin_details_dto import PluginDetailsDTO

class TestPluginDetailsDTO(unittest.TestCase):
    """PluginDetailsDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PluginDetailsDTO:
        """Test PluginDetailsDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PluginDetailsDTO`
        """
        model = PluginDetailsDTO()
        if include_optional:
            return PluginDetailsDTO(
                request_id = '',
                plugin_id = 56,
                plugin_uid = '',
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                visibility = '',
                name = '',
                provider = '',
                manifest_format = '',
                api_format = '',
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
                    ],
                manifest_info = '',
                api_info = '',
                tool_spec_format = '',
                tool_spec_info = '',
                ext = ''
            )
        else:
            return PluginDetailsDTO(
        )
        """

    def testPluginDetailsDTO(self):
        """Test PluginDetailsDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
