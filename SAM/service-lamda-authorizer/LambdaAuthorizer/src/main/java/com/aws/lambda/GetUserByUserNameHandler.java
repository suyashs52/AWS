package com.aws.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.aws.service.CognitoUserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class GetUserByUserNameHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;

    public GetUserByUserNameHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Map<String,String> queryStrParam=input.getQueryStringParameters();
        try {
            String userName=input.getPathParameters().get("userName");
            String poolId=System.getenv("APP_USERS_POOL_ID");

         JsonObject userDetails= cognitoUserService.getuserByUserName(userName,poolId);

            return response
                    .withStatusCode(200)
                    .withBody(new Gson().toJson(userDetails,JsonObject.class));
        } catch (ArithmeticException | NumberFormatException  ex) {
            return response
                    .withBody("{\"error\":\""+ex.getMessage().replaceAll("\"","\\\\\"")+"\"}")
                    .withStatusCode(500);
        }
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
