package com.demo.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.demo.aws.service.CongnitoUserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

public class AddUserToGroupHandler  implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CongnitoUserService congnitoUserService;
    private final String userPoolId;

    public AddUserToGroupHandler(){
        this.congnitoUserService=new CongnitoUserService(System.getenv("AWS_REGION"));

        this.userPoolId=Utils.decryptKey("MY_COGNITO_POOL_ID");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response=new APIGatewayProxyResponseEvent();
        LambdaLogger logger=context.getLogger();
        try{
            JsonObject requestBody= JsonParser.parseString(input.getBody()).getAsJsonObject();
            String groupName=requestBody.get("group").getAsString();
            String userName=input.getPathParameters().get("userName");
            logger.log(userPoolId.replace("\n","")+" userPoolId");

          JsonObject addUserToGroupResult=  congnitoUserService.addUserToGroup(groupName,userName,userPoolId.replace("\n",""));
          response.withBody(new Gson().toJson(addUserToGroupResult,JsonObject.class)).withStatusCode(200);
        }catch (AwsServiceException ex){
            logger.log(ex.awsErrorDetails().errorMessage());
            response.withBody(ex.awsErrorDetails().errorMessage());
            response.withStatusCode(ex.awsErrorDetails().sdkHttpResponse().statusCode());
        }catch (Exception ex){
            logger.log(ex.getMessage());
            response.withBody(ex.getMessage());
            response.withStatusCode(500);

        }

        return response;
    }
}
