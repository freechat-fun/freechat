# coding: utf-8

"""
    FreeChat OpenAPI Definition

    https://github.com/freechat-fun/freechat

    The version of the OpenAPI document: 0.2.11
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import unittest

from freechat_sdk.api.organization_api import OrganizationApi


class TestOrganizationApi(unittest.TestCase):
    """OrganizationApi unit test stubs"""

    def setUp(self) -> None:
        self.api = OrganizationApi()

    def tearDown(self) -> None:
        pass

    def test_get_owners(self) -> None:
        """Test case for get_owners

        Get My Superior Relationship
        """
        pass

    def test_get_owners_dot(self) -> None:
        """Test case for get_owners_dot

        Get DOT of Superior Relationship
        """
        pass

    def test_get_subordinate_owners(self) -> None:
        """Test case for get_subordinate_owners

        Get Superior Relationship
        """
        pass

    def test_get_subordinate_subordinates(self) -> None:
        """Test case for get_subordinate_subordinates

        Get Subordinate Relationship
        """
        pass

    def test_get_subordinates(self) -> None:
        """Test case for get_subordinates

        Get My Subordinate Relationship
        """
        pass

    def test_get_subordinates_dot(self) -> None:
        """Test case for get_subordinates_dot

        Get DOT of Subordinate Relationship
        """
        pass

    def test_list_subordinate_authorities(self) -> None:
        """Test case for list_subordinate_authorities

        List Subordinate Permissions
        """
        pass

    def test_remove_subordinate_subordinates_tree(self) -> None:
        """Test case for remove_subordinate_subordinates_tree

        Clear Subordinate Relationship
        """
        pass

    def test_update_subordinate_authorities(self) -> None:
        """Test case for update_subordinate_authorities

        Update Subordinate Permissions
        """
        pass

    def test_update_subordinate_owners(self) -> None:
        """Test case for update_subordinate_owners

        Update Superior Relationship
        """
        pass

    def test_update_subordinate_subordinates(self) -> None:
        """Test case for update_subordinate_subordinates

        Update Subordinate Relationship
        """
        pass


if __name__ == '__main__':
    unittest.main()
