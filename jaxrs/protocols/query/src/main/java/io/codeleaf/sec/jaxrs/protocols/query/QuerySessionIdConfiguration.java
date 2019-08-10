package io.codeleaf.sec.jaxrs.protocols.query;

import io.codeleaf.config.Configuration;

public final class QuerySessionIdConfiguration implements Configuration {

    private final String parameterName;

    public QuerySessionIdConfiguration(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
}
