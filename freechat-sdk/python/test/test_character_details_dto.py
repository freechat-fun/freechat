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

from freechat-sdk.models.character_details_dto import CharacterDetailsDTO

class TestCharacterDetailsDTO(unittest.TestCase):
    """CharacterDetailsDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> CharacterDetailsDTO:
        """Test CharacterDetailsDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `CharacterDetailsDTO`
        """
        model = CharacterDetailsDTO()
        if include_optional:
            return CharacterDetailsDTO(
                request_id = '',
                character_id = '',
                gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'),
                visibility = '',
                version = 56,
                name = '',
                description = '',
                avatar = '',
                picture = '',
                lang = '',
                username = '',
                tags = [
                    ''
                    ],
                profile = '',
                greeting = '',
                chat_style = '',
                chat_example = '',
                experience = '',
                ext = '',
                draft = freechat-sdk.models.character_info_draft_dto.CharacterInfoDraftDTO(
                    profile = '', 
                    greeting = '', 
                    chat_style = '', 
                    chat_example = '', 
                    experience = '', ),
                backends = [
                    freechat-sdk.models.character_backend_details_dto.CharacterBackendDetailsDTO(
                        request_id = '', 
                        backend_id = '', 
                        gmt_create = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                        gmt_modified = datetime.datetime.strptime('2013-10-20 19:20:30.00', '%Y-%m-%d %H:%M:%S.%f'), 
                        character_id = '', 
                        is_default = True, 
                        greeting_prompt_task_id = '', 
                        experience_prompt_task_id = '', 
                        message_window_size = 56, 
                        forward_to_user = True, )
                    ]
            )
        else:
            return CharacterDetailsDTO(
                name = '',
        )
        """

    def testCharacterDetailsDTO(self):
        """Test CharacterDetailsDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
