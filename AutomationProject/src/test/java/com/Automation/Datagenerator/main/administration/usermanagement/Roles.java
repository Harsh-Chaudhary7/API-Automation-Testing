package com.Automation.Datagenerator.main.administration.usermanagement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class Roles {
    public String createRole(String roleName, String description, JSONArray permissionsArray) {
        JSONObject body = new JSONObject();

        JSONArray principalsArray = new JSONArray();

        body.put("name",roleName);
        body.put("description",description);
        body.put("permissions",permissionsArray);
        body.put("principals",principalsArray);

        return body.toString();
    }

    public String folderPermission(ArrayList<Map<String, Object>> repoPermissionsAll) {

        JSONObject body = new JSONObject();

        JSONArray repoEnablePermissionsArray = new JSONArray();
        repoEnablePermissionsArray.put(new JSONObject().put("name","upload").put("enabled", true));
        repoEnablePermissionsArray.put(new JSONObject().put("name","download").put("enabled", true));
        repoEnablePermissionsArray.put(new JSONObject().put("name","viewcontent").put("enabled", true));
        repoEnablePermissionsArray.put(new JSONObject().put("name","clone").put("enabled", true));
        repoEnablePermissionsArray.put(new JSONObject().put("name","delete").put("enabled", true));
        repoEnablePermissionsArray.put(new JSONObject().put("name","run").put("enabled", true));


        JSONArray permissionsArray = new JSONArray();
        for (Map<String, Object> repoPermission :repoPermissionsAll) {
            JSONObject folderPermissionObject = new JSONObject();
            folderPermissionObject.put("folderPermission", new JSONObject().put("folderId",repoPermission.get("id").toString()).put("permissions",repoEnablePermissionsArray));
            permissionsArray.put(folderPermissionObject);
        }
        body.put("permissions",permissionsArray);

        return body.toString();
    }
}
