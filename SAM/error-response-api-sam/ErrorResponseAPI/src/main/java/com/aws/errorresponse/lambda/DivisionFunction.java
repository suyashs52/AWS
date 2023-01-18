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
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 * sam build , sam deploy, add query param in method request,
 * api gateway return 4** when api gateway issue, 5** when lambda issue
 * in gateway respose menu can change the messages
 * https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
 */
public class DivisionFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Map<String,String> queryStrParam=input.getQueryStringParameters();
        try {

            int dividend=Integer.parseInt(queryStrParam.get("dividend"));
            int divisor=Integer.parseInt(queryStrParam.get("divisor"));
            int result=dividend/divisor;
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            String output = String.format("{ \"dividend\": "+dividend+"," +
                        "    \"divisor\": "+divisor+
                        ",\"result\":"+result+
                    " }");

            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (ArithmeticException | NumberFormatException | IOException ex) {
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
