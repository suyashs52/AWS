package com.aws.errorresponse.lambda;

public class MyException extends RuntimeException{

    public MyException(String errorMessage,Throwable cause){
        super(errorMessage,cause);
    }
}
