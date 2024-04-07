# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.7.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.ai_model_info_dto import AiModelInfoDTO

class TestAiModelInfoDTO(unittest.TestCase):
    """AiModelInfoDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> AiModelInfoDTO:
        """Test AiModelInfoDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `AiModelInfoDTO`
        """
        model = AiModelInfoDTO()
        if include_optional:
            return AiModelInfoDTO(
                request_id = '',
                model_id = '',
                name = '',
                description = '',
                provider = '',
                type = ''
            )
        else:
            return AiModelInfoDTO(
        )
        """

    def testAiModelInfoDTO(self):
        """Test AiModelInfoDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
