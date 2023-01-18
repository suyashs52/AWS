package com.demo.aws;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.Base64;

public class Utils {


    public   static String decryptKey(String name) {
        System.out.println("Decrypting key");
       // return System.getenv(name);
        byte[] encryptedKey = Base64.decode(System.getenv(name));
        //comment because we didn't used this for encryption
//        Map<String, String> encryptionContext = new HashMap<>();
//        encryptionContext.put("LambdaFunctionName",
//                System.getenv("AWS_LAMBDA_FUNCTION_NAME"));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey));
             //   .withEncryptionContext(encryptionContext);

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8"));
    }

}
