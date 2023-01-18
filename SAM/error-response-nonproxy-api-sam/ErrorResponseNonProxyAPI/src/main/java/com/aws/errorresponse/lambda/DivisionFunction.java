package com.aws.errorresponse.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 * sam build , sam deploy, add query param in method request,
 * api gateway return 4** when api gateway issue, 5** when lambda issue
 * in gateway respose menu can change the messages
 * https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
 */
public class DivisionFunction implements RequestHandler<Map<String,String>, Map<String,String>> {

    public Map<String, String> handleRequest(final Map<String,String> input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        Map<String, String> response = new HashMap<>();

        try {

            int dividend = Integer.parseInt(input.get("dividend"));
            int divisor = Integer.parseInt(input.get("divisor"));
            int result = dividend / divisor;



            response.put("dividend", dividend+"");
            response.put("divisor", divisor+"");
            response.put("result", result+"");
            response.put("version",context.getFunctionVersion());



        } catch (Exception ex) {
            throw new MyException("Exception: "+ex.getMessage(),ex.getCause());
        }
        return response;


    }

}
