package io.codeleaf.sec.jaxrs.protocols.header;

import io.codeleaf.config.Configuration;

public final class HeaderSessionIdConfiguration implements Configuration {

    private final String headerName;

    HeaderSessionIdConfiguration(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}
