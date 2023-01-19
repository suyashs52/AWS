package com.aws.authorizer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder=Statement.Builder.class)
public class Statement {

    public String Action;
    public String Effect;
    public String Resource;

    private Statement(Builder builder) {
        Action = builder.Action;
        Effect = builder.Effect;
        Resource = builder.Resource;
    }
    public static Builder newBuilder() {
        return new Builder();
    }
    @JsonPOJOBuilder(withPrefix="")
    public static final class Builder {
        private String Action;
        private String Effect;
        private String Resource;

        private Builder() {
        }



        public Builder Action(String val) {
            Action = val;
            return this;
        }

        public Builder Effect(String val) {
            Effect = val;
            return this;
        }

        public Builder Resource(String val) {
            Resource = val;
            return this;
        }

        public Statement build() {
            return new Statement(this);
        }
    }
}
