# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.6.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.chat_content_dto import ChatContentDTO

class TestChatContentDTO(unittest.TestCase):
    """ChatContentDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> ChatContentDTO:
        """Test ChatContentDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `ChatContentDTO`
        """
        model = ChatContentDTO()
        if include_optional:
            return ChatContentDTO(
                type = '',
                content = ''
            )
        else:
            return ChatContentDTO(
                content = '',
        )
        """

    def testChatContentDTO(self):
        """Test ChatContentDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
