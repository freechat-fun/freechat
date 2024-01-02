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

from freechat-sdk.models.prompt_task_details_dto import PromptTaskDetailsDTO

class TestPromptTaskDetailsDTO(unittest.TestCase):
    """PromptTaskDetailsDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> PromptTaskDetailsDTO:
        """Test PromptTaskDetailsDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `PromptTaskDetailsDTO`
        """
        model = PromptTaskDetailsDTO()
        if include_optional:
            return PromptTaskDetailsDTO(
                request_id = '',
                task_id = '',
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_executed = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
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
            return PromptTaskDetailsDTO(
        )
        """

    def testPromptTaskDetailsDTO(self):
        """Test PromptTaskDetailsDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
