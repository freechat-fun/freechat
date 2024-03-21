# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.6.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.plugin_update_dto import PluginUpdateDTO

class TestPluginUpdateDTO(unittest.TestCase):
    """PluginUpdateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PluginUpdateDTO:
        """Test PluginUpdateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PluginUpdateDTO`
        """
        model = PluginUpdateDTO()
        if include_optional:
            return PluginUpdateDTO(
                visibility = '',
                name = '',
                manifest_format = '',
                manifest_info = '',
                api_format = '',
                api_info = '',
                provider = '',
                ext = '',
                tags = [
                    ''
                    ],
                ai_models = [
                    ''
                    ]
            )
        else:
            return PluginUpdateDTO(
                name = '',
        )
        """

    def testPluginUpdateDTO(self):
        """Test PluginUpdateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
