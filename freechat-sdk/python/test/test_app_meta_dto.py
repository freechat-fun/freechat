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

from freechat-sdk.models.app_meta_dto import AppMetaDTO

class TestAppMetaDTO(unittest.TestCase):
    """AppMetaDTO unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> AppMetaDTO:
        """Test AppMetaDTO
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `AppMetaDTO`
        """
        model = AppMetaDTO()
        if include_optional:
            return AppMetaDTO(
                name = '',
                version = '',
                build_timestamp = '',
                build_number = '',
                commit_url = '',
                release_note_url = '',
                running_env = ''
            )
        else:
            return AppMetaDTO(
        )
        """

    def testAppMetaDTO(self):
        """Test AppMetaDTO"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
