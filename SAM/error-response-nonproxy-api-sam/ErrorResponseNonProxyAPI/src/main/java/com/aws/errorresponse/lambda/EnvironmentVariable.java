package com.aws.errorresponse.lambda;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 * sam build , sam deploy, add query param in method request,
 * api gateway return 4** when api gateway issue, 5** when lambda issue
 * in gateway respose menu can change the messages
 * https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html
 */
public class EnvironmentVariable implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
       // Map<String, String> response = new HashMap<>();
        APIGatewayProxyResponseEvent response=new APIGatewayProxyResponseEvent().withHeaders(headers);
        try {
           LambdaLogger logger= context.getLogger();

            String env=System.getenv("MY_CONGNITO_USER_POOL_ID");


            logger.log("MY_CONGNITO_USER_POOL_ID ="+env);
            logger.log("PARAM1 ="+PARAM1);
            logger.log("G_MY_VAR ="+G_MY_VAR);

      //      response.put("version",context.getFunctionVersion());

            response= response.withBody("{}").withStatusCode(200);

        } catch (Exception ex) {
            throw new MyException("Exception: "+ex.getMessage(),ex.getCause());
        }
        return response;


    }

    private final String G_MY_VAR = decryptKey("G_MY_VAR");
    private final String PARAM1 = decryptKey("PARAM1");
    private   String decryptKey(String name) {
        System.out.println("Decrypting key");
        byte[] encryptedKey = Base64.decode(System.getenv(name));
        Map<String, String> encryptionContext = new HashMap<>();
        encryptionContext.put("LambdaFunctionName",
                System.getenv("AWS_LAMBDA_FUNCTION_NAME"));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey))
                .withEncryptionContext(encryptionContext);

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8"));
    }

}
