package com.Automation.Datagenerator.test.administration.usermanagement;

import com.Automation.CommonUtils.BaseTest;
import com.Automation.CommonUtils.GenericUtil;
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

public class UserTest extends BaseTest {

    @BeforeClass
    public void BeforeClass() {
        getRoleList();
    }

    @DataProvider(name = "testdata_create_user")
    public Iterator<Object []> testdata_create( ) throws IOException, InterruptedException {
        return GenericUtil.readTestDataCSVFile("datagenerator//create_user.csv");
    }

    @Test(priority = 1, dataProvider = "testdata_create_user")
    public void CreateUser(String userName, String description, String firstname, String lastname, String email, String password, String roleNames,
                           String userLicenses) throws Exception {

        JSONArray roleIds = GetJsonArrayIdsFromPipeSeparatedNameString(rolesList, roleNames, "name", "id");

        userLicenses = userLicenses.toUpperCase().replace("ADMIN", "");
        JSONArray licensesType = new JSONArray();
        if(userLicenses != null && !userLicenses.trim().equals("")) {
            for (String license : userLicenses.split("\\|")) {
                licensesType.put(license);
            }
        }

        response = postRequest(getHost() + "/v1/usermanagement/users?noemail=true", usersAPI.createNewUserWithLicensePermission(
                userName, description, firstname, lastname,
                email, password, roleIds, licensesType,
                false, true), getAuthToken());

        if(response.statusCode()==201){
            printResponse("POST",getHost() + "/v1/usermanagement/users?noemail=true", usersAPI.createNewUserWithLicensePermission(
                    userName, description, firstname, lastname,
                    email, password, roleIds, licensesType,
                    false, true),response);
        }


        Map<String, Object> mapList = mapper.readValue(response.body().asString(), HashMap.class);
        if(mapList.size() > 3) {
            int id = Integer.parseInt(mapList.get("id").toString());
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
