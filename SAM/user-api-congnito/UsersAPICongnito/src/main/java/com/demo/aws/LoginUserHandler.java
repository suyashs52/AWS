package com.demo.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.demo.aws.service.CongnitoUserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CongnitoUserService congnitoUserService;
    private final String appCliendId;
    private final String appClientSecret;
    public LoginUserHandler(){
        this.congnitoUserService=new CongnitoUserService(System.getenv("AWS_REGION"));
        this.appCliendId= Utils. decryptKey("MY_COGNITO_POOL_APP_CLIENT_ID");
        this.appClientSecret=Utils.decryptKey("MY_COGNITO_POOL_APP_CLIENT_SECRET");

    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        JsonObject loginDetail=JsonParser.parseString(input.getBody()).getAsJsonObject();

        LambdaLogger logger=context.getLogger();

        try {
            JsonObject loginResult=  congnitoUserService.userLogin(loginDetail,appCliendId,appClientSecret);

            return     response.withStatusCode(200)
                    .withBody(new Gson().toJson(loginResult,JsonObject.class));

        }catch (AwsServiceException ex){
            logger.log(ex.awsErrorDetails().errorMessage());
            ErrorResponse errorResponse=new ErrorResponse(ex.awsErrorDetails().errorMessage());

          return   response.withStatusCode(500).withBody( new Gson().toJson(errorResponse,JsonObject.class));
        }catch (Exception ex){
            logger.log(ex.getMessage());
            ErrorResponse errorResponse=new ErrorResponse(ex.getMessage());
            //sereialize when null too don't use  new Gson().toJson(errorResponse,JsonObject.class)
            String erorResponseJson=new GsonBuilder().serializeNulls().create().toJson(errorResponse,ErrorResponse.class);
          return   response.withBody(erorResponseJson).withStatusCode(500);
        }



    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
