package com.demo.aws.users;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;

import java.util.Map;

//input/output evnts
public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    //create a lamda func in aws account from scratch
    //install aws sam after craeting iam user-->
    //in termnial after installing aws cli and sam
    //aws configure
    //use iam access key id , access key

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

       Map<String,String> pathParameter= input.getPathParameters();
       String userId=pathParameter.get("userId");
        JsonObject returnValue=new JsonObject();
        returnValue.addProperty("firstName","Sergey");
        returnValue.addProperty("lastName","Kargopolov");
        returnValue.addProperty("id",userId);
       APIGatewayProxyResponseEvent responseEvent=new APIGatewayProxyResponseEvent();
       responseEvent.withStatusCode(200).withBody(returnValue.toString());
        return responseEvent;
    }
}
