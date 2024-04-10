# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.8.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.hot_tag_dto import HotTagDTO

class TestHotTagDTO(unittest.TestCase):
    """HotTagDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> HotTagDTO:
        """Test HotTagDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `HotTagDTO`
        """
        model = HotTagDTO()
        if include_optional:
            return HotTagDTO(
                content = '',
                count = 56
            )
        else:
            return HotTagDTO(
        )
        """

    def testHotTagDTO(self):
        """Test HotTagDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
