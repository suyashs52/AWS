package com.aws.service;

import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import java.util.List;

public class CognitoUserService {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
    public CognitoUserService(String region){
        this.cognitoIdentityProviderClient=CognitoIdentityProviderClient.builder()
                .region(Region.of(region)).build();
    }

    public JsonObject getuserByUserName(String userName,String poolId){
     AdminGetUserRequest adminGetUserRequest=   AdminGetUserRequest.builder().username(userName)
                .userPoolId(poolId)
                .build();

        AdminGetUserResponse adminGetUserResponse=cognitoIdentityProviderClient.adminGetUser(adminGetUserRequest);
        JsonObject jsonObject=new JsonObject();

        if(!adminGetUserResponse.sdkHttpResponse().isSuccessful()){
            throw new IllegalArgumentException("Unsuccessful result. status code: "+adminGetUserResponse.sdkHttpResponse().statusCode());
        }

           List<AttributeType> attributeTypes= adminGetUserResponse.userAttributes();
            attributeTypes.stream().forEach((attribute)->{
                jsonObject.addProperty(attribute.name(),attribute.value());
            });

            return jsonObject;
    }
}
