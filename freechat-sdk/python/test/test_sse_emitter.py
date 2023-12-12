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

from freechat-sdk.models.sse_emitter import SseEmitter

class TestSseEmitter(unittest.TestCase):
    """SseEmitter unit test stubs"""

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def make_instance(self, include_optional) -> SseEmitter:
        """Test SseEmitter
            include_option is a boolean, when False only required
            params are included, when True both required and
            optional params are included """
        # uncomment below to create an instance of `SseEmitter`
        """
        model = SseEmitter()
        if include_optional:
            return SseEmitter(
                timeout = 56
            )
        else:
            return SseEmitter(
        )
        """

    def testSseEmitter(self):
        """Test SseEmitter"""
        # inst_req_only = self.make_instance(include_optional=False)
        # inst_req_and_optional = self.make_instance(include_optional=True)

if __name__ == '__main__':
    unittest.main()
