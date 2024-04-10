# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.8.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.models.app_config_create_dto import AppConfigCreateDTO

class TestAppConfigCreateDTO(unittest.TestCase):
    """AppConfigCreateDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> AppConfigCreateDTO:
        """Test AppConfigCreateDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `AppConfigCreateDTO`
        """
        model = AppConfigCreateDTO()
        if include_optional:
            return AppConfigCreateDTO(
                name = '',
                format = '',
                content = ''
            )
        else:
            return AppConfigCreateDTO(
                name = '',
        )
        """

    def testAppConfigCreateDTO(self):
        """Test AppConfigCreateDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
