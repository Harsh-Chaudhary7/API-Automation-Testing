package com.Automation.Datagenerator.main.administration.usermanagement;

import org.json.JSONObject;

public class Authentication {

    public String authenticationUser(String username, String password, String domain) {

        JSONObject body = new JSONObject();

        if(domain != null && !domain.equals("")) {
            body.put("username", domain+"\\"+username);
        } else {
            body.put("username", username);
        }
        body.put("password", password);

        return body.toString();
    }
}
