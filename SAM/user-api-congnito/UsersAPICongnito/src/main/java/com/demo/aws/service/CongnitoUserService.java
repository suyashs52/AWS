package com.demo.aws.service;

import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
public class CongnitoUserService {
    CognitoIdentityProviderClient cognitoIdentityProviderClient;
    public CongnitoUserService(String region){
        this.cognitoIdentityProviderClient=CognitoIdentityProviderClient.builder()
                .region(Region.of(region)).build();
    }
    CongnitoUserService(CognitoIdentityProviderClient cognitoIdentityProviderClient){
        this.cognitoIdentityProviderClient=cognitoIdentityProviderClient;
    }

    public JsonObject addUserToGroup(String groupName, String userName, String userPoolId){
        AdminAddUserToGroupRequest adminAddUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                .groupName(groupName)
                .username(userName)
                .userPoolId(userPoolId)
                .build();

        AdminAddUserToGroupResponse adminAddUserToGroupResponse =
                cognitoIdentityProviderClient.adminAddUserToGroup(adminAddUserToGroupRequest);

        JsonObject addUserToGroupResponse = new JsonObject();
        addUserToGroupResponse.addProperty("isSuccessful", adminAddUserToGroupResponse.sdkHttpResponse().isSuccessful());
        addUserToGroupResponse.addProperty("statusCode", adminAddUserToGroupResponse.sdkHttpResponse().statusCode());

        return addUserToGroupResponse;
    }

    public JsonObject userLogin(JsonObject loginDetails, String appClientId, String appClientSecret){
        String email=loginDetails.get("email").getAsString();
        String password=loginDetails.get("password").getAsString();
        String generatedSecretHash=calculateSecretHash(appClientId,appClientSecret,email);

        Map<String,String> authParam=new HashMap<String,String>(){{
            put("USERNAME",email);
            put("PASSWORD",password);
            put("SECRET_HASH",generatedSecretHash);
        }

        };
        InitiateAuthRequest initiateAuthRequest=  InitiateAuthRequest.builder().clientId(appClientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParam)
                .build();
         InitiateAuthResponse response=   cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);
         AuthenticationResultType authenticationResultType=response.authenticationResult();

         JsonObject loginUserResult=new JsonObject();
        loginUserResult.addProperty("isSuccessful",response.sdkHttpResponse().isSuccessful());
        loginUserResult.addProperty("statusCode",response.sdkHttpResponse().statusCode());
        loginUserResult.addProperty("idToken",authenticationResultType.idToken());
        loginUserResult.addProperty("accessToken",authenticationResultType.accessToken());
        loginUserResult.addProperty("refeshToken",authenticationResultType.refreshToken());

        return loginUserResult;

    }

    public JsonObject confirmUserSignup(String appClientId,String appClientSecret,String email,String confirmationCode){
        String generatedSecretHash=calculateSecretHash(appClientId,appClientSecret,email);

       ConfirmSignUpRequest confirmSignUpRequest= ConfirmSignUpRequest.builder().secretHash(generatedSecretHash).clientId(appClientId)
                .username(email).confirmationCode(confirmationCode).build();

        ConfirmSignUpResponse confirmSignUpResponse= cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);
        JsonObject confirmUserResponse=new JsonObject();
        confirmUserResponse.addProperty("isSuccessful",confirmSignUpResponse.sdkHttpResponse().isSuccessful());
        confirmUserResponse.addProperty("statusCode",confirmSignUpResponse.sdkHttpResponse().statusCode());
        return confirmUserResponse;

    }

    public JsonObject createUser(JsonObject user,String appClientId,String appClientSecret){

        String email=user.get("email").getAsString();
        String password=user.get("password").getAsString();
        String firstName=user.get("firstName").getAsString();
        String lastName=user.get("lastName").getAsString();
        String userId= UUID.randomUUID().toString();// user.get("userId").getAsString();

        AttributeType attributeEmail=AttributeType.builder().name("email")
                .value(email).build();

        AttributeType attributeUserId=AttributeType.builder().name("custom:userid")
                .value(userId).build();

        AttributeType attributeName=AttributeType.builder().name("name")
                .value(firstName+" "+lastName).build();

        List<AttributeType> attributes=new ArrayList<>();
        attributes.add(attributeEmail);
        attributes.add(attributeName);
        attributes.add(attributeUserId);

        String generatedSecretHash=calculateSecretHash(appClientId,appClientSecret,email);

        SignUpRequest signUpRequest=SignUpRequest.builder()
                .username(email).password(password).userAttributes(attributes)
                .clientId(appClientId)
                .secretHash(generatedSecretHash).build()
                ;

       SignUpResponse signUpResponse= cognitoIdentityProviderClient.signUp(signUpRequest);
        JsonObject createUserDetail=new JsonObject();
        createUserDetail.addProperty("isSuccessful",signUpResponse.sdkHttpResponse().isSuccessful());
        createUserDetail.addProperty("statusCode",signUpResponse.sdkHttpResponse().statusCode());
        createUserDetail.addProperty("congonitoUserId",signUpResponse.userSub());
        createUserDetail.addProperty("isConfirmed",signUpResponse.userConfirmed());
        return createUserDetail;
    }


    public   String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
