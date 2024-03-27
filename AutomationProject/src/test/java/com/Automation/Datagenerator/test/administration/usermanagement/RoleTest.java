package com.Automation.Datagenerator.test.administration.usermanagement;

import com.Automation.CommonUtils.BaseTest;
import com.Automation.CommonUtils.GenericUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class RoleTest extends BaseTest {

    @BeforeClass
    public void BeforeClass() throws JsonProcessingException {
        getFeaturePermissionsList();
        getRepositoryPermissionsList();
    }

    @DataProvider(name = "testdata_create_role")
    public Iterator<Object []> testdata_create( ) throws IOException, InterruptedException {
            return GenericUtil.readTestDataCSVFile("datagenerator//create_role.csv");
    }

    @Test(priority = 1, dataProvider = "testdata_create_role")
    public void CreateRole(String roleName, String description, String rolePermissionType, String repoPermissionType) throws Exception {

        JSONArray permissionList = new JSONArray();
        for (Map<String, Object> featurePermission :featurePermissionsList) {
            try {
                if(rolePermissionType.toUpperCase().equals("ALL")) {
                    permissionList.put(featurePermission);
                } else if(rolePermissionType.toUpperCase().equals(featurePermission.get("resourceType").toString().toUpperCase())) {
                    permissionList.put(featurePermission);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response = postRequest(getHost() + "/v1/usermanagement/roles", rolesAPI.createRole(roleName, description, permissionList), getAuthToken());
        if((response.statusCode())==201)
            printResponse("POST",getHost() + "/v1/usermanagement/roles", rolesAPI.createRole(roleName, description, permissionList),response);


        Map<String, Object> mapList = mapper.readValue(response.body().asString(), HashMap.class);
        if(mapList.size() > 3) {
            int id = Integer.parseInt(mapList.get("id").toString());

            if(repoPermissionType.toUpperCase().equals("ALL")) {
                response = putRequest(getHost() + "/v2/repository/role/" + id + "/permissions", rolesAPI.folderPermission(repositoryPermissionsList), getAuthToken());
                if((response.statusCode())==201)
                    printResponse("PUT",getHost() + "/v2/repository/role/" + id + "/permissions", rolesAPI.folderPermission(repositoryPermissionsList),response);
            }

            assertTrue(id > 0);
        }
        else {
            assertTrue(false);
        }
   }
    private void printResponse(String method, String uri, String reqBody, Response res) {
        String resString = res.body().asString();

        System.out.println("--------------------------------------------------");
        System.out.println(method + ": " + uri);
        if (reqBody != null) {
            System.out.println("Request Body  : " + reqBody);
        }

        System.out.println("Response Code : " + res.getStatusCode());
    }
}
