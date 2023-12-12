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

from freechat-sdk.models.prompt_task_dto import PromptTaskDTO

class TestPromptTaskDTO(unittest.TestCase):
    """PromptTaskDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PromptTaskDTO:
        """Test PromptTaskDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PromptTaskDTO`
        """
        model = PromptTaskDTO()
        if include_optional:
            return PromptTaskDTO(
                prompt_ref = freechat-sdk.models.prompt_ref_dto.PromptRefDTO(
                    prompt_id = '', 
                    variables = {
                        'key' : None
                        }, 
                    draft = True, ),
                model_id = '',
                api_key_name = '',
                params = {
                    'key' : None
                    },
                cron = '',
                status = ''
            )
        else:
            return PromptTaskDTO(
                prompt_ref = freechat-sdk.models.prompt_ref_dto.PromptRefDTO(
                    prompt_id = '', 
                    variables = {
                        'key' : None
                        }, 
                    draft = True, ),
        )
        """

    def testPromptTaskDTO(self):
        """Test PromptTaskDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
