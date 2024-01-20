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

from freechat_sdk.models.plugin_query_where import PluginQueryWhere

class TestPluginQueryWhere(unittest.TestCase):
    """PluginQueryWhere unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PluginQueryWhere:
        """Test PluginQueryWhere
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PluginQueryWhere`
        """
        model = PluginQueryWhere()
        if include_optional:
            return PluginQueryWhere(
                visibility = '',
                username = '',
                manifest_format = '',
                api_format = '',
                tags = [
                    ''
                    ],
                tags_op = '',
                ai_models = [
                    ''
                    ],
                ai_models_op = '',
                name = '',
                provider = '',
                text = ''
            )
        else:
            return PluginQueryWhere(
        )
        """

    def testPluginQueryWhere(self):
        """Test PluginQueryWhere"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
