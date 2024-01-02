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

from freechat-sdk.models.character_backend_dto import CharacterBackendDTO

class TestCharacterBackendDTO(unittest.TestCase):
    """CharacterBackendDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterBackendDTO:
        """Test CharacterBackendDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterBackendDTO`
        """
        model = CharacterBackendDTO()
        if include_optional:
            return CharacterBackendDTO(
                is_default = True,
                chat_prompt_task_id = '',
                greeting_prompt_task_id = '',
                experience_prompt_task_id = '',
                moderation_model_id = '',
                moderation_api_key_name = '',
                moderation_params = '',
                message_window_size = 56,
                forward_to_user = True
            )
        else:
            return CharacterBackendDTO(
        )
        """

    def testCharacterBackendDTO(self):
        """Test CharacterBackendDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
