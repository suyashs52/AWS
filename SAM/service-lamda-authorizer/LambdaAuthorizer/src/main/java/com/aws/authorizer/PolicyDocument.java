package com.aws.authorizer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;
@JsonDeserialize(builder=PolicyDocument.Builder.class)
public class PolicyDocument {

    public  String Version;
    public List<Statement> Statement;

    private PolicyDocument(Builder builder) {
        Version = builder.Version;
        Statement = builder.Statement;
    }
    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix="")
    public static final class Builder {
        private String Version;
        private List<com.aws.authorizer.Statement> Statement;

        private Builder() {
            Statement=new ArrayList<>();
        }



        public Builder Version(String val) {
            Version = val;
            return this;
        }

        public Builder Statement(List<Statement> val) {
            Statement = val;
            return this;
        }

        public PolicyDocument build() {
            return new PolicyDocument(this);
        }
    }
}
