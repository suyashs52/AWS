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

import java.util.Map;

public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CongnitoUserService congnitoUserService;


    public GetUserHandler(){
        this.congnitoUserService=new CongnitoUserService(System.getenv("AWS_REGION"));


    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response=new APIGatewayProxyResponseEvent();
        LambdaLogger logger=context.getLogger();
        try{
          Map<String,String> requestHeader=  input.getHeaders();

          JsonObject userDetails=  congnitoUserService.getUser(requestHeader.get("AccessToken"));

          return   response.withBody(new Gson().toJson(userDetails,JsonObject.class)).withStatusCode(200);
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
