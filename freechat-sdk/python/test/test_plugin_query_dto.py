# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.5.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.plugin_query_dto import PluginQueryDTO

class TestPluginQueryDTO(unittest.TestCase):
    """PluginQueryDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PluginQueryDTO:
        """Test PluginQueryDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PluginQueryDTO`
        """
        model = PluginQueryDTO()
        if include_optional:
            return PluginQueryDTO(
                where = freechat_sdk.models.plugin_query/where.PluginQuery.Where(
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
                    text = '', ),
                order_by = [
                    ''
                    ],
                page_num = 56,
                page_size = 56
            )
        else:
            return PluginQueryDTO(
        )
        """

    def testPluginQueryDTO(self):
        """Test PluginQueryDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
