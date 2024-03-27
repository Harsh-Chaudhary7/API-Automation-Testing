package com.Automation.Datagenerator.test;

import com.Automation.CommonUtils.BaseTest;
import com.Automation.Datagenerator.main.administration.usermanagement.Roles;
import com.Automation.Datagenerator.main.administration.usermanagement.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PreRequisiteTest extends BaseTest {
    protected Roles rolesAPI = new Roles();
    protected Users usersAPI = new Users();
    private String DEFAULT_PASSWORD = testProperties.getProperty("ALLPERMISSION_DEFAULT_PASSWORD");
    private String NEW_PASSWORD = testProperties.getProperty("ALLPERMISSION_FINAL_PASSWORD");
    private String EMAIL = testProperties.getProperty("ALLPERMISSION_ADMIN_EMAIL");
    private String BOTAGENT_DEVICE_NAME = testProperties.getProperty("BOTAGENT_DEVICE_NAME");
    private String BOTAGENT_DEVICE_USERNAME = testProperties.getProperty("BOTAGENT_DEVICE_AUTOLOGIN_USERNAME");
    private String BOTAGENT_DEVICE_PASSWORD = testProperties.getProperty("BOTAGENT_DEVICE_AUTOLOGIN_PASSWORD");

    @BeforeClass
    public void BeforeClass() throws JsonProcessingException {
        getFeaturePermissionsList();
        getRepositoryPermissionsList();
        getRoleList();
    }

    @Test(priority = 2)
    public void CreateAllPermissionRole() throws JsonProcessingException {

        String roleName = testProperties.getProperty("ALLPERMISSION_ROLENAME");
        String description = "All Permission Role";

        JSONArray permissionList = new JSONArray();
        for (Object featurePermission :featurePermissionsList) {
            try {
                permissionList.put(featurePermission);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response = postRequest(getHost() + "/v1/usermanagement/roles", rolesAPI.createRole(roleName, description, permissionList), getAuthToken());
        int roleId = response.path("id");
        assertTrue(roleId > 0);

        response = putRequest(getHost() + "/v2/repository/role/"+roleId+"/permissions", rolesAPI.folderPermission(repositoryPermissionsList), getAuthToken());
        if(response.statusCode()==201)
            System.out.println("Role Created Successfully");

        getRoleList();
    }

    @Test(priority = 3)
    public void CreateAdminUserWithAllPermissions() throws Exception {

        String userName = testProperties.getProperty("ALLPERMISSION_ADMIN_USERNAME");
        String roleNames = "AAE_Admin|AAE_Bot Developer|AAE_Bot Migration Admin|AAE_Queue Admin|AAE_Pool Admin|AAE_Locker Admin|"+testProperties.getProperty("ALLPERMISSION_ROLENAME");
        String userLicenses = "";

        JSONArray licensesType = new JSONArray();
        if(userLicenses != null && !userLicenses.trim().equals("")) {
            for (String license : userLicenses.split("\\|")) {
                licensesType.put(license);
            }
        }

        JSONArray roleIds = GetJsonArrayIdsFromPipeSeparatedNameString(rolesList, roleNames, "name", "id");

        String body = usersAPI.createNewUserWithLicensePermission(
                userName, "", "", "",
                EMAIL, DEFAULT_PASSWORD, roleIds, licensesType,
                false, true);
        CreateUser(body);
        resetPasswordRequest(userName, DEFAULT_PASSWORD, NEW_PASSWORD);
    }

    @Test(priority = 4)
    public void CreateDeveloperUserWithAllPermissions() throws Exception {

        JSONArray arrUsersCreation = new JSONArray();

        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_DEVELOPER_USERNAME"))
                .put("userLicenses", "DEVELOPMENT")
                .put("roleNames", "AAE_Basic|AAE_Bot Developer|AAE_Bot Migration Admin|AAE_Queue Admin|AAE_Pool Admin|AAE_Locker Admin|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );
        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_DEVELOPER_USERNAME_RECORDER"))
                .put("userLicenses", "DEVELOPMENT")
                .put("roleNames", "AAE_Basic|AAE_Bot Developer|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );
        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_DEVELOPER_USERNAME_BOTSTORE"))
                .put("userLicenses", "DEVELOPMENT")
                .put("roleNames", "AAE_Basic|AAE_Bot Developer|AAE_Bot Store Publisher|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );
        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_DEVELOPER_USERNAME_SCHEDULER"))
                .put("userLicenses", "DEVELOPMENT")
                .put("roleNames", "AAE_Basic|AAE_Bot Developer|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );
        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_DEVELOPER_USERNAME_WLM"))
                .put("userLicenses", "DEVELOPMENT")
                .put("roleNames", "AAE_Basic|AAE_Bot Developer|AAE_Queue Admin|AAE_Pool Admin|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );
        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_DEVELOPER_USERNAME_REPOSITORY"))
                .put("userLicenses", "DEVELOPMENT")
                .put("roleNames", "AAE_Basic|AAE_Bot Developer|AAE_Bot Migration Admin|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );
        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_DEVELOPER_USERNAME_BOTINSIGNT"))
                .put("userLicenses", "DEVELOPMENT")
                .put("roleNames", "AAE_Basic|AAE_Bot Developer|AAE_Bot Insight Admin|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );

        for (int i=0; i < arrUsersCreation.length(); i++) {
            JSONObject jsonObject = arrUsersCreation.getJSONObject(i);

            String roleNames = jsonObject.get("roleNames").toString();
            String userName = jsonObject.get("userName").toString();
            String userLicenses = jsonObject.get("userLicenses").toString();

            JSONArray licensesType = new JSONArray();
            if(userLicenses != null && !userLicenses.trim().equals("")) {
                for (String license : userLicenses.split("\\|")) {
                    licensesType.put(license);
                }
            }
            JSONArray roleIds = GetJsonArrayIdsFromPipeSeparatedNameString(rolesList, roleNames, "name", "id");

            String body = usersAPI.createNewUserWithLicensePermission(
                    userName, "", "", "",
                    EMAIL, DEFAULT_PASSWORD, roleIds, licensesType,
                    false, true);
            CreateUser(body);
            resetPasswordRequest(userName, DEFAULT_PASSWORD, NEW_PASSWORD);

        }
    }

    @Test(priority = 5)
    public void CreateRunnerUserWithAllPermissions() throws Exception {

        JSONArray arrUsersCreation = new JSONArray();
        arrUsersCreation.put(new JSONObject()
                .put("userName", testProperties.getProperty("ALLPERMISSION_RUNNER_USERNAME"))
                .put("userLicenses", "RUNTIME")
                .put("roleNames", "AAE_Basic|AAE_Bot Insight Consumer|"+testProperties.getProperty("ALLPERMISSION_ROLENAME"))
        );

        for (int i=0; i < arrUsersCreation.length(); i++) {
            JSONObject jsonObject = arrUsersCreation.getJSONObject(i);

            String roleNames = jsonObject.get("roleNames").toString();
            String userName = jsonObject.get("userName").toString();
            String userLicenses = jsonObject.get("userLicenses").toString();

            JSONArray licensesType = new JSONArray();
            if(userLicenses != null && !userLicenses.trim().equals("")) {
                for (String license : userLicenses.split("\\|")) {
                    licensesType.put(license);
                }
            }

            JSONArray roleIds = GetJsonArrayIdsFromPipeSeparatedNameString(rolesList, roleNames, "name", "id");

            String body = usersAPI.createNewUserWithLicensePermission(
                    userName, "", "", "",
                    EMAIL, DEFAULT_PASSWORD, roleIds, licensesType,
                    false, true);
            int userId = CreateUser(body);

            if(userId > 0) {

                resetPasswordRequest(userName, DEFAULT_PASSWORD, NEW_PASSWORD);

                int deviceId = GetDeviceIdFromDevicename(BOTAGENT_DEVICE_NAME);

                String token_runner = performLogin(userName, NEW_PASSWORD, domain);
                response = postRequest(getHost() + "/v1/devices/runasusers/default", devicesAPI.AddRunAsUsers(userId, deviceId), token_runner);
                assertEquals(response.statusCode(),200);
                response = postRequest(getHost() + "/v2/credentialvault/loginsetting", devicesAPI.SetAutoLogin(BOTAGENT_DEVICE_USERNAME, BOTAGENT_DEVICE_PASSWORD),token_runner);
                assertEquals(response.statusCode(),201);
            }
        }
    }

    private int CreateUser(String requestBody) throws JsonProcessingException {

        int id = 0;

        response = postRequest(getHost() + "/v1/usermanagement/users?noemail=true", requestBody, getAuthToken());
        assertEquals(response.statusCode(),201);
        if(response.statusCode()==201)
            System.out.println("User Created Successfully");
        Map<String, Object> mapList = mapper.readValue(response.body().asString(), HashMap.class);
        if(mapList.size() > 3) {
            id = Integer.parseInt(mapList.get("id").toString());
            assertTrue(id > 0);
        }
        else {
            assertTrue(false);
        }

        return id;
    }

    private Response resetPasswordRequest(String userName, String oldPassword, String newPassword) throws JsonProcessingException {

        String token = performLogin(userName, oldPassword, domain);
        response = postRequest(getHost() + "/v1/usermanagement/users/self/changePassword", usersAPI.changePassword(oldPassword, newPassword),
                token);

        return response;
    }

}
