package com.aws.authorizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonDeserialize(builder=AuthorizerOutput.Builder.class)
public class AuthorizerOutput {

    @JsonProperty("principalId")
    private String principalId;
    @JsonProperty("policyDocument")
    private PolicyDocument policyDocument;
    @JsonProperty("context")
    private Map<String,String> context;

    private AuthorizerOutput(Builder builder) {
        principalId = builder.principalId;
        policyDocument = builder.policyDocument;
        context = builder.context;
    }
    public static Builder newBuilder() {
        return new Builder();
    }

    public String getPrincipalId() {
        return principalId;
    }

    public PolicyDocument getPolicyDocument() {
        return policyDocument;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public static final class Builder {
        private String principalId;
        private PolicyDocument policyDocument;
        private Map<String, String> context;

        private Builder() {
        }



        public Builder principalId(String val) {
            principalId = val;
            return this;
        }

        public Builder policyDocument(PolicyDocument val) {
            policyDocument = val;
            return this;
        }

        public Builder context(Map<String, String> val) {
            context = val;
            return this;
        }

        public AuthorizerOutput build() {
            return new AuthorizerOutput(this);
        }
    }
}
