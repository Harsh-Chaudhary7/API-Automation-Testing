package com.Automation.CommonUtils;

import com.Automation.Datagenerator.main.administration.usermanagement.Authentication;
import com.Automation.Datagenerator.main.administration.usermanagement.Roles;
import com.Automation.Datagenerator.main.administration.usermanagement.Users;
import com.Automation.Datagenerator.main.manage.device.Devices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;

public class BaseTest {
    protected Response response;
    protected static RequestSpecification spec;
    protected ObjectMapper mapper = new ObjectMapper();
    protected Devices devicesAPI = new Devices();
    protected Users usersAPI = new Users();
    protected Roles rolesAPI = new Roles();
    protected ArrayList<Map<String, Object>> rolesList = new ArrayList<>();
    protected ArrayList<Map<String, Object>> usersList = new ArrayList<>();
    protected ArrayList<Map<String, Object>> featurePermissionsList = new ArrayList<>();
    protected ArrayList<Map<String, Object>> repositoryPermissionsList = new ArrayList<>();
    protected ArrayList<Map<String, Object>> devicesList = new ArrayList<>();
    protected static Properties testProperties = null;
    protected static InputStream inputstream = null;
    public static String authenticationType = "";
    protected String token = null;
    public static String domain = "";
    private long tokenGenerateTime = 0;
    private String crLogin_Username = null;
    private String crLogin_Password = null;

    static {
        try {
            String log4jConfigFile = System.getProperty("user.dir")
                    + File.separator + "log4j.properties";
            PropertyConfigurator.configure(log4jConfigFile);

            String propFile = System.getProperty("testprop");
            if (propFile == null)
                propFile = "AutomationProject.properties";

            System.setProperty("testprop",propFile);
            testProperties = new Properties();
            inputstream = BaseTest.class.getClassLoader().getResourceAsStream(propFile);
            testProperties.load(inputstream);

            setLoggingSpecification();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String propertyName(String name) {
        return (String) testProperties.get(name);
    }

    /** This Function sets the LoggingSpecification*/
    private static void setLoggingSpecification() {
        spec = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter(LogDetail.URI))
                .addFilter(new ResponseLoggingFilter(LogDetail.STATUS))
                .build();
    }

    /**
     * given()
     *  content type, set cookies, add auth, add param, set header info etc...
     *
     *  when()
     *  get, post, put, delete
     *
     *  then()
     *  validate status code, extract response, extract headers cookies & response body
     * */

    public Response getRequest(String uri, String token) {
        Response res =
                given().
                        header("X-Authorization", token).
                        contentType("application/json").
                when().
                        get(uri).
                then().extract().response();

        response = res;
        printResponse("GET",uri,"",res);

        return res;
    }

    public Response getRequestWithoutToken(String uri) {
        Response res =
                given().
                        contentType("application/json").
                when().
                        get(uri).
                then().extract().response();

        response = res;
        printResponse("GET",uri,"",res);

        return response;
    }

    public Response postRequest(String uri, String reqBody, String token) {
        Response res =
                given().
                        header("X-Authorization", token).
                        header("X-Version", "v1").
                        contentType("application/json").
                        body(reqBody).
                when().
                        post(uri).
                then().
                        extract().
                        response();

        response = res;
        printResponse("POST", uri, reqBody, res);

        return res;
    }

    public Response putRequest(String uri, String reqBody, String token) {
        Response res =
                given().
                        header("X-Authorization", token).
                        contentType("application/json").
                        body(reqBody).
                        when().
                        put(uri).
                        then().extract().response();

        response = res;
        printResponse("PUT", uri, reqBody, res);

        return res;
    }

    public Response deleteRequest(String uri, String token) {
        Response res =
                given().
                        header("X-Authorization", token).
                        contentType("application/json").
                        when().
                        delete(uri).
                        then().extract().response();
        response = res;
        printResponse("DELETE", uri, null, res);

        return res;
    }

    private void printResponse(String method, String uri, String reqBody, Response res) {
        String resString = res.body().asString();

        if(res.getStatusCode() != 200
                && res.getStatusCode() != 201
                && res.getStatusCode() != 202) {
            System.out.println("--------------------------------------------------");
            System.out.println(method + ": " + uri);
            if (reqBody != null) {
                System.out.println("Request Body  : " + reqBody);
            }

            System.out.println("Response Code : " + res.getStatusCode());
            System.out.println("Response Body : " + resString);
        }
    }

    protected String getResourceFilePath(String resourceFileName) {
        return BaseTest.class.getClassLoader().getResource(resourceFileName).getFile();
    }

    public String getHost() {

        String url = "";
        try {
            url = testProperties.getProperty("AACRURL");
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @BeforeClass(alwaysRun = true)
    public String generateAuthenticationToken() {
        if(crLogin_Username == null || crLogin_Username.equals("")) {
            return generateAuthenticationToken(null);
        } else {
            return generateAuthenticationToken(
                    new String[]{crLogin_Username,crLogin_Password}
            );
        }
    }
    public String generateAuthenticationToken(String[] credentials) {

        authenticationType = getAuthType();
        if (authenticationType.toUpperCase().equals("ACTIVE_DIRECTORY")) {
            domain = getDomains();
            System.out.println("domain: "+domain);
        }

        if(credentials == null) {
            credentials = getAdminUserNamePassword();
        }

        token = performLogin(credentials[0], credentials[1], domain);
        if (getAuthToken() == null || getAuthToken() == "" || getAuthToken().equals("")) {
            credentials = getInitialAdminUserNamePassword();
            token = performLogin(credentials[0], credentials[1], domain);
        }

        assertTrue(getAuthToken().length() > 0);

        return getAuthToken();
    }

    protected String getAuthToken() {
        long currentTime = System.currentTimeMillis();

        if((currentTime - tokenGenerateTime) >= (1000*60*5) && token != null && !token.equals("")) {
            System.out.println("Token refreshed required");

            generateAuthenticationToken();
        }

        return token;
    }
    private String getAuthType() {
        Response res = getRequestWithoutToken(getHost() + "/v1/authentication/type");
        return res.jsonPath().get("authType");
    }

    public String performLogin(String userName, String password, String domain){

        String token = "";
        Authentication authentication = new Authentication();

        Response response = postRequest(getHost() + "/v1/authentication", authentication.authenticationUser(userName, password, domain), "");

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> bodyList = mapper.readValue(response.body().asString(), HashMap.class);
            if (response.body() != null && response.body().asString().contains("token")) {
                token = bodyList.get("token").toString();
                tokenGenerateTime = System.currentTimeMillis();
            } else {
                token = "";
            }

            //System.out.println("Token: " + getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
    private String getDomains() {
        Response res = getRequest(getHost() + "/v1/usermanagement/activeDirectory", "");
        JSONArray domainArray = new JSONArray(res.asString());
        String returnDomain = null;
        for(Object domain:domainArray){
            returnDomain = domain.toString();
        }

        if(!returnDomain.toLowerCase().equals(testProperties.getProperty("FIRSTADMIN_DOMAIN").toLowerCase())) {
            assertTrue(false);
        }

        return returnDomain;
    }
    private String[] getInitialAdminUserNamePassword() {
        String username = testProperties.getProperty("FIRSTADMIN_USERNAME");
        String password = testProperties.getProperty("FIRSTADMIN_PASSWORD");

        return new String[]{username, password};
    }
    private String[] getAdminUserNamePassword() {
        String username = testProperties.getProperty("ALLPERMISSION_ADMIN_USERNAME");
        String password = testProperties.getProperty("ALLPERMISSION_FINAL_PASSWORD");

        return new String[]{username, password};
    }

    protected ArrayList<Map<String, Object>> getFeaturePermissionsList() throws JsonProcessingException {

        try {
            response = getRequest(getHost() + "/v1/usermanagement/permissions", getAuthToken());
            System.out.println(response.asString()+"\n");
            featurePermissionsList = mapper.readValue(response.asString(), ArrayList.class);
        } catch (JsonProcessingException e) {
            JSONObject jsonObject = new JSONObject(response.asString());
            JSONArray resultsArray = jsonObject.getJSONArray("list");
            featurePermissionsList = mapper.readValue(resultsArray.toString(), ArrayList.class);
            e.printStackTrace();
        }

        return featurePermissionsList;
    }

    protected ArrayList<Map<String, Object>>  getRepositoryPermissionsList(){

        try {
            response = getRequest(getHost() + "/v1/repository/newrole/directories", getAuthToken());
            repositoryPermissionsList = mapper.readValue(response.asString(), ArrayList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return repositoryPermissionsList;
    }

    protected ArrayList<Map<String, Object>>  getRoleList(){

        try {
            response = getRequest(getHost() + "/v1/usermanagement/roles", getAuthToken());
            rolesList = mapper.readValue(response.asString(), ArrayList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return rolesList;
    }

    protected ArrayList<Map<String, Object>>  getDeviceList(){

        try {
            response = postRequest(getHost() + "/v2/devices/list", devicesAPI.Search(""), getAuthToken());
            Map<String, ArrayList<Map<String, Object>>> responseObject = mapper.readValue(response.asString(), HashMap.class);
            devicesList = responseObject.get("list");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return devicesList;
    }
    protected JSONArray GetJsonArrayIdsFromPipeSeparatedNameString(ArrayList<Map<String, Object>> fullList,
                                                                   String pipeSeperatedNameString,
                                                                   String objectSearchKey,
                                                                   String objectSearchValue) {

        JSONArray idJsonArray = new JSONArray();
        for(String name : pipeSeperatedNameString.split(("\\|"))) {
            for (Map<String, Object> obj : fullList) {
                if (obj.get(objectSearchKey).toString().toLowerCase().equals(name.toLowerCase())) {
                    idJsonArray.put(Integer.parseInt(obj.get(objectSearchValue).toString()));
                }
            }
        }

        return idJsonArray;
    }
    protected int GetDeviceIdFromDevicename(String deviceName) {

        int id = 0;

        if(devicesList == null || devicesList.size() == 0) {
            getDeviceList();
        }

        for (Map<String, Object> folder : devicesList) {
            if (folder.get("hostName").toString().toLowerCase().equals(deviceName.toLowerCase())) {

                id = Integer.parseInt(folder.get("id").toString());
                break;
            }
        }

        return id;
    }
}
