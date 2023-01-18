package com.aws.photoapp.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

/**
 * Handler for requests to Lambda function.
 */
//sam init
//go to poto-user-api-sam dir and write sam build after changed template.yaml,event.json
 //local sam required docker to run, maven
 //sam local invoke PostHandlerLambdaFunction --event events/event.json
 //sam local invoke PostHandlerLambdaFunction --event events/event.json -d 5858 //debug mode
 // add remote jvm debug in config> change port 5858
 //sam deploy
 //after changes
 //sam build
 //sam deploy
 // sam logs --name PostHandlerLambdaFunction --stack-name PhotoAppUserRestAPI
 //sam logs  --stack-name PhotoAppUserRestAPI
 // //sam logs  --stack-name PhotoAppUserRestAPI --tail
 //to delete : sam delete PhotoAppUserRestAPI (check sam.toml file)
public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

     LambdaLogger logger=context.getLogger();
     logger.log("Handling HTTP Post request for the /users API endpoint");

       String requestBody=input.getBody();
        Gson gson=new Gson();

        Map<String,String> userDetail=gson.fromJson(requestBody,Map.class);
        userDetail.put("userId", UUID.randomUUID().toString());

        //TODO: process user details


        Map returnValue=new HashMap();
        returnValue.put("firstName",userDetail.get("firstName"));
        returnValue.put("lastName",userDetail.get("lastName"));
        returnValue.put("userId",userDetail.get("userId"));


        APIGatewayProxyResponseEvent response=new APIGatewayProxyResponseEvent();
        response.withStatusCode(200);
        response.withBody(gson.toJson(returnValue,Map.class));

        Map responseHeader=new HashMap();
        responseHeader.put("Content-Type","application/json");
        response.withHeaders(responseHeader);

        return  response;
    }


}
