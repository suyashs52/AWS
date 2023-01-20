package com.demo.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.demo.aws.service.CongnitoUserService;
import com.demo.aws.shared.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.http.SdkHttpResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateUserHandlerTest {

    @Mock
    CongnitoUserService congnitoUserService;
    @Mock
    APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent;

    @Mock
    Context context;

    @Mock
    LambdaLogger lamdaLoggerMock;

    @InjectMocks
    CreateUserHandler createUserHandler;

    @BeforeEach
    public void runBeforeEachTestMethod(){
        System.out.println("run @beforeeach method");

        when(context.getLogger()).thenReturn(lamdaLoggerMock);
    }

    @AfterEach
    public void runAfterEachTestMethod(){
        System.out.println("executing @after each method");
    }
    @Test
    public void test_handleRequest_whenValidDetailsProvided_returnsSuccesfulResponse(){
        //arrange
        JsonObject userDetail=new JsonObject();
        userDetail.addProperty("firstName","serg");
        userDetail.addProperty("lastName","kum");
        userDetail.addProperty("email","suyash@gmail.com");
        userDetail.addProperty("password","12345678");
        String userDetailJsonString=new Gson().toJson(userDetail,JsonObject.class);

        when(apiGatewayProxyRequestEvent.getBody()).thenReturn(userDetailJsonString);
        JsonObject createUserDetail=new JsonObject();

        createUserDetail.addProperty(Constants.IS_SUCCESSFUL,true);
        createUserDetail.addProperty(Constants.STATUS_CODE,200);
        createUserDetail.addProperty(Constants.CONGITO_USER_ID, UUID.randomUUID().toString());
        createUserDetail.addProperty(Constants.IS_CONFIRMED,true);
        when(congnitoUserService.createUser(any(),any(),any())).thenReturn(createUserDetail);


        //act
       APIGatewayProxyResponseEvent response= createUserHandler.handleRequest(apiGatewayProxyRequestEvent,context);
        String responseString=response.getBody();
        JsonObject object= JsonParser.parseString(responseString).getAsJsonObject();
        //assert
        verify(lamdaLoggerMock,times(1)).log(anyString());
        assertTrue(object.get(Constants.IS_CONFIRMED).getAsBoolean());
        assertEquals(200,object.get(Constants.STATUS_CODE).getAsInt());
    }

    @Test
    public void testHandleRequest_whenEmptyRequestBodyProvided_returnErrorMessage(){
        //arrange
        when(apiGatewayProxyRequestEvent.getBody()).thenReturn("");

        //act
        APIGatewayProxyResponseEvent response= createUserHandler.handleRequest(apiGatewayProxyRequestEvent,context);
        String responseString=response.getBody();
        JsonObject responseBodyJson= JsonParser.parseString(responseString).getAsJsonObject();

        //assert
        assertEquals(500,response.getStatusCode());
        assertNotNull(responseBodyJson.get("message"),"missing the message property");
    }

    @Test
    public void testHandleRequest_whenAwsServiceExceptionTakePlace_returnErrorMessage(){
        AwsErrorDetails awsErrorDetails=AwsErrorDetails.builder().errorCode("")
                        .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
                                .errorMessage("AwsServiceException took place").build();
        when(apiGatewayProxyRequestEvent.getBody()).thenReturn("{}");
        when(congnitoUserService.createUser(any(),any(),any())).thenThrow(AwsServiceException
                .builder().statusCode(500).awsErrorDetails(awsErrorDetails).build());

        //act
        APIGatewayProxyResponseEvent response= createUserHandler.handleRequest(apiGatewayProxyRequestEvent,context);
        String responseString=response.getBody();
       // JsonObject responseBodyJson= JsonParser.parseString(responseString).getAsJsonObject();

        //assert
        assertEquals(awsErrorDetails.sdkHttpResponse().statusCode(),response.getStatusCode());

        assertTrue(responseString.length()>0);
    }
}
