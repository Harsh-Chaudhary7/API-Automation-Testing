package com.Automation.Datagenerator.main.manage.device;

import org.json.JSONArray;
import org.json.JSONObject;

public class Devices {

    public String Search(String DeviceName) {

        JSONObject body = new JSONObject();

        JSONArray sortArray = new JSONArray();
        sortArray.put(new JSONObject().put("field","updatedOn").put("direction", "desc"));

        JSONObject filterObject = new JSONObject();
        if(DeviceName != null && !DeviceName.equals("")) {
            filterObject.put("operator", "substring");
            filterObject.put("value", DeviceName);
            filterObject.put("field", "hostName");
        }

        JSONArray fieldsArray = new JSONArray();
        JSONObject pageObject = new JSONObject();
        pageObject.put("offset",0);
        pageObject.put("length",200);

        body.put("sort",sortArray);
        body.put("filter",filterObject);
        body.put("fields",fieldsArray);
        body.put("page",pageObject);

        return body.toString();
    }

    public String AddRunAsUsers(int userId, int deviceId) {

        JSONObject body = new JSONObject();

        body.put("userId",userId);
        body.put("deviceId",deviceId);

        return body.toString();
    }

    public String SetAutoLogin(String loginUsername, String loginPassword) {

        JSONObject body = new JSONObject();

        body.put("loginUsername",loginUsername);
        body.put("loginPassword",loginPassword);

        return body.toString();
    }
}
