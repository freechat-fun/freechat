/*
 * FreeChat OpenAPI Definition
 * https://github.com/freechat-fun/freechat
 *
 * The version of the OpenAPI document: 0.2.6
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package fun.freechat.client.api;

import fun.freechat.client.ApiException;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for OrganizationApi
 */
@Disabled
public class OrganizationApiTest {

    private final OrganizationApi api = new OrganizationApi();

    /**
     * Get My Superior Relationship
     *
     * Get the superior relationships of the current account, including direct and indirect owners, by default does not include virtual reported owners, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual reported owners can be returned, in this case, there may be a circular relationship.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getOwnersTest() throws ApiException {
        String all = null;
        List<String> response = api.getOwners(all);
        // TODO: test validations
    }

    /**
     * Get DOT of Superior Relationship
     *
     * Same as /api/v1/org/owners, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getOwnersDotTest() throws ApiException {
        String all = null;
        String response = api.getOwnersDot(all);
        // TODO: test validations
    }

    /**
     * Get Superior Relationship
     *
     * Get the superior relationship of the subordinate account, including direct and indirect owners, default does not include virtual reported owners, so there will be no circular relationship.&lt;br/&gt; By specifying all&#x3D;1, virtual reported owners can be returned, in this case, there may be a circular relationship. 
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getSubordinateOwnersTest() throws ApiException {
        String username = null;
        String all = null;
        List<String> response = api.getSubordinateOwners(username, all);
        // TODO: test validations
    }

    /**
     * Get Subordinate Relationship
     *
     * Get the subordinate relationship of the subordinate account, including direct and indirect subordinates, default does not include virtual managed subordinates, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getSubordinateSubordinatesTest() throws ApiException {
        String username = null;
        String all = null;
        List<String> response = api.getSubordinateSubordinates(username, all);
        // TODO: test validations
    }

    /**
     * Get My Subordinate Relationship
     *
     * Get the subordinate relationships of the current account, including direct and indirect subordinates, by default does not include virtual managed subordinates, so there will be no circular relationship.&lt;br/&gt;By specifying all&#x3D;1, virtual managed subordinates can be returned, in this case, there may be a circular relationship.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getSubordinatesTest() throws ApiException {
        String all = null;
        List<String> response = api.getSubordinates(all);
        // TODO: test validations
    }

    /**
     * Get DOT of Subordinate Relationship
     *
     * Same as /api/v1/org/subordinates, but returns a DOT format view, DOT reference: [graphviz](https://www.graphviz.org/)
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void getSubordinatesDotTest() throws ApiException {
        String all = null;
        String response = api.getSubordinatesDot(all);
        // TODO: test validations
    }

    /**
     * List Subordinate Permissions
     *
     * List the permission list of the subordinate account.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void listSubordinateAuthoritiesTest() throws ApiException {
        String username = null;
        Set<String> response = api.listSubordinateAuthorities(username);
        // TODO: test validations
    }

    /**
     * Clear Subordinate Relationship
     *
     * Fully delete the direct subordinate relationship of the subordinate account.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void removeSubordinateSubordinatesTreeTest() throws ApiException {
        String username = null;
        Boolean response = api.removeSubordinateSubordinatesTree(username);
        // TODO: test validations
    }

    /**
     * Update Subordinate Permissions
     *
     * Update the permission list of the subordinate account, the granted permissions cannot be higher than the permissions owned by oneself, for example, a resource administrator cannot grant the role of an organization administrator to a subordinate account.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateSubordinateAuthoritiesTest() throws ApiException {
        String username = null;
        Set<String> requestBody = null;
        Boolean response = api.updateSubordinateAuthorities(username, requestBody);
        // TODO: test validations
    }

    /**
     * Update Superior Relationship
     *
     * Fully update the direct superior relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateSubordinateOwnersTest() throws ApiException {
        String username = null;
        List<String> requestBody = null;
        Boolean response = api.updateSubordinateOwners(username, requestBody);
        // TODO: test validations
    }

    /**
     * Update Subordinate Relationship
     *
     * Fully update the direct subordinate relationship of the subordinate account (i.e., will delete the relationship that existed before but is not in this list), if there is a circular relationship, it will automatically be set as a virtual relationship.
     *
     * @throws ApiException if the Api call fails
     */
    @Test
    public void updateSubordinateSubordinatesTest() throws ApiException {
        String username = null;
        List<String> requestBody = null;
        Boolean response = api.updateSubordinateSubordinates(username, requestBody);
        // TODO: test validations
    }

}
