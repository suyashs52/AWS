package com.aws.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Arrays;


public class LambdaAuthorizer implements RequestHandler<APIGatewayProxyRequestEvent, AuthorizerOutput> {

    public AuthorizerOutput handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        String effect = "Allow";
        String userName=input.getPathParameters().get("userName");
        String jwt=input.getHeaders().get("Authorization");
        String region=System.getenv("AWS_REGION");
        String userPoolId=System.getenv("APP_USERS_POOL_ID");
        String audience=System.getenv("APP_USERS_APP_CLIENT_ID");
//        if(userName.equalsIgnoreCase("123")){
//            effect="Deny";
//        }
        LambdaLogger logger=context.getLogger();
        JwtUtils jwtUtils = new JwtUtils();
        DecodedJWT decodedJWT = null;
        try{
            decodedJWT = jwtUtils.validateJwtForUser(jwt, region, userPoolId, userName, audience);
            userName = decodedJWT.getSubject();
        } catch (RuntimeException ex) {
            effect = "Deny";
            logger.log(ex.getMessage());
            ex.printStackTrace();
        }

        APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext=
                input.getRequestContext(); //to access account id, api id,stage, http method

        String arn=String.format("arn:aws:execute-api:%s:%s:%s/%s/%s/%s",
                System.getenv("AWS_REGION"),proxyRequestContext.getAccountId(),
                proxyRequestContext.getApiId(),proxyRequestContext.getStage(),
                proxyRequestContext.getHttpMethod(),"*"); //last is resourses
        Statement statement = Statement.newBuilder()
                .Action("execute-api:Invoke")
                .Effect(effect)
                .Resource(arn)
                .build();
      PolicyDocument policyDocument=  PolicyDocument.newBuilder()
                .Version("2012-10-17")
                .Statement(Arrays.asList(statement))
                .build();

        AuthorizerOutput authorizerOutput=  AuthorizerOutput.newBuilder()
                .principalId(userName)
                .policyDocument(policyDocument)
                .build();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        return authorizerOutput;
    }

}
