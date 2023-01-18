package com.aws.datatransform.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
//sam init, cd .., sam build
    //change the template file
    //sam deploy --guided
    //check region
    //open api gateway, select region, you ll see your key
    //open your api>model>create>
    //open post resource>integration request>uncheck use lambda proxy integration
    //mapping template > check 2nd when no template defined,add content type and select created model
    //https://docs.aws.amazon.com/apigateway/latest/developerguide/rest-api-data-transformations.html
    // "firstName" : "$util.escapeJavaScript($inputRoot.userFirstName)",
    //once handleRequestCreated deploy changes
    // in test json pass userFirstName
    //change the Integration Response, add mapping template and change response template
    //"userFirstName" : "$inputRoot.firstName",
    //go to method response, select 200, add response body, responsebody template
    //now test with
    /*
 {
    "userFirstName":"Sergey",
    "userLastName":"karog",
    "userEmail":"test@test.com",
    "userPassword":"123456",
    "userRepeatPassword":"123456"
}
to read header add "secretKey":"$util.escapeJavaScript($input.params('secretKey'))" in integration
request mapping template
5** error when lambda thorw exception, api return 4** api gateway issue
     */
public class PostHandler implements RequestHandler<Map<String,String>, Map<String,String>> {



    public Map<String,String> handleRequest(final Map<String,String> input, final Context context) {

            String firstName=input.get("firstName");
        String lastName=input.get("lastName");
        String email=input.get("email");
        String password=input.get("password");
        String repeatpassword=input.get("repeatpassword");

        LambdaLogger logger=context.getLogger();
        logger.log("\n firstName= "+firstName);
         logger.log("\n lastName= "+lastName);
         logger.log("\n email= "+email);
         logger.log("\n password= "+password);
         logger.log("\n repeat password= "+repeatpassword);

        Map<String,String> response=new HashMap<>();
        response.put("firstName",firstName);
        response.put("lastName",lastName);
        response.put("email",email);
        response.put("id", UUID.randomUUID().toString());

        return response;
    }


}
