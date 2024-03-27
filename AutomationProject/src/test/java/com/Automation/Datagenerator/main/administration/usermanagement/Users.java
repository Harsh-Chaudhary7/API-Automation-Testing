package com.Automation.Datagenerator.main.administration.usermanagement;

import com.Automation.CommonUtils.BaseTest;
import org.json.JSONArray;
import org.json.JSONObject;

public class Users {

	public String createNewUserWithLicensePermission(String userName, String description, String firstname, String lastname, String email, String password,
													 JSONArray roleId, JSONArray licensesType, Boolean isDisabled, Boolean enableAutoLogin) {
		String body = "";

		switch (BaseTest.authenticationType.toUpperCase()) {
			case "ACTIVE_DIRECTORY":
				body = ActiveDirectoryUserCreationBody(userName, description, firstname, lastname, BaseTest.domain, email, password, roleId, licensesType, isDisabled, enableAutoLogin);
			break;
			case "DATABASE":
				body = DatabaseUserCreationBody(userName, description, firstname, lastname, email, password, roleId, licensesType, isDisabled, enableAutoLogin);
		}
		return body;
	}

	public String changePassword(String oldPassword, String newPassword) {
		JSONObject body = new JSONObject();

		JSONArray questionsAnswersArray = new JSONArray();
		questionsAnswersArray.put(new JSONObject().put("question","question 1").put("answer","answer 1"));
		questionsAnswersArray.put(new JSONObject().put("question","question 2").put("answer","answer 2"));
		questionsAnswersArray.put(new JSONObject().put("question","question 3").put("answer","answer 3"));

		body.put("oldPassword",oldPassword);
		body.put("password",newPassword);
		body.put("questionsAnswers",questionsAnswersArray);

		return body.toString();
	}

	public String Search(String Username) {

		JSONObject body = new JSONObject();

		JSONArray sortArray = new JSONArray();
		sortArray.put(new JSONObject().put("field","username").put("direction", "asc"));

		JSONObject filterObject = new JSONObject();
		if(Username != null && !Username.equals("")) {
			filterObject.put("operator", "substring");
			filterObject.put("value", Username);
			filterObject.put("field", "username");
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

	private String DatabaseUserCreationBody(String userName, String description, String firstname, String lastname, String email, String password,
											JSONArray roleId, JSONArray licensesType, Boolean isDisabled, Boolean enableAutoLogin) {

    	JSONObject body = new JSONObject();

    	JSONArray rolesArray = new JSONArray();
		for (Object id : roleId) {
			rolesArray.put(new JSONObject().put("id",id));
		}

		JSONArray licenseFeatures = new JSONArray();
		for (Object license : licensesType) {
			licenseFeatures.put(license);
		}

		body.put("username",userName);
		body.put("description",description);
		body.put("firstName",firstname);
		body.put("lastName",lastname);
		body.put("email",email);
		body.put("password",password);

		body.put("licenseFeatures",licenseFeatures);
		body.put("disabled",isDisabled);
		body.put("enableAutoLogin",enableAutoLogin);
		body.put("roles",rolesArray);

		return body.toString();
	}

	private String ActiveDirectoryUserCreationBody(String userName, String description, String firstname, String lastname, String domain, String email,
												   String password, JSONArray roleId, JSONArray licensesType, Boolean isDisabled, Boolean enableAutoLogin){
		JSONObject body = new JSONObject();

		JSONArray rolesArray = new JSONArray();
		for (Object id : roleId) {
			rolesArray.put(new JSONObject().put("id",id));
		}

		JSONArray licenseFeatures = new JSONArray();
		for (Object license : licensesType) {
			licenseFeatures.put(license);
		}

		body.put("username",userName);
		body.put("description",description);
		body.put("firstName",firstname);
		body.put("lastName",lastname);
		body.put("email",email);
		body.put("domain",domain);
		body.put("password",password);

		body.put("licenseFeatures",licenseFeatures);
		body.put("disabled",isDisabled);
		body.put("enableAutoLogin",enableAutoLogin);
		body.put("roles",rolesArray);

		return body.toString();
	}
}
