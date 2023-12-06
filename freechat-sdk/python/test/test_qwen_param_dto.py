# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.1.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest
import datetime

from freechat-sdk.models.qwen_param_dto import QwenParamDTO

class TestQwenParamDTO(unittest.TestCase):
    """QwenParamDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> QwenParamDTO:
        """Test QwenParamDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `QwenParamDTO`
        """
        model = QwenParamDTO()
        if include_optional:
            return QwenParamDTO(
                api_key = '',
                api_key_name = '',
                model_id = '',
                top_p = 1.337,
                top_k = 56,
                enable_search = True,
                seed = 56
            )
        else:
            return QwenParamDTO(
                model_id = '',
        )
        """

    def testQwenParamDTO(self):
        """Test QwenParamDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
