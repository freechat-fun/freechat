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

from freechat-sdk.models.character_backend_details_dto import CharacterBackendDetailsDTO

class TestCharacterBackendDetailsDTO(unittest.TestCase):
    """CharacterBackendDetailsDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterBackendDetailsDTO:
        """Test CharacterBackendDetailsDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterBackendDetailsDTO`
        """
        model = CharacterBackendDetailsDTO()
        if include_optional:
            return CharacterBackendDetailsDTO(
                request_id = '',
                backend_id = '',
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                character_id = '',
                is_default = True,
                greeting_prompt_task_id = '',
                experience_prompt_task_id = '',
                message_window_size = 56,
                forward_to_user = True
            )
        else:
            return CharacterBackendDetailsDTO(
        )
        """

    def testCharacterBackendDetailsDTO(self):
        """Test CharacterBackendDetailsDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
