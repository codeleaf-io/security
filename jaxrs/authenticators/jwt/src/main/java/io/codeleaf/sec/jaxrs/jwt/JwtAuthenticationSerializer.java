package io.codeleaf.sec.jaxrs.jwt;

import io.codeleaf.common.utils.StringEncoder;
import io.codeleaf.sec.Authentication;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JwtAuthenticationSerializer {

    public String serialize(Authentication authenticationContext) {
        Map<String, String> fields = new HashMap<>();
        fields.put("identity", authenticationContext.getIdentity());
        fields.put("secure", Boolean.toString(authenticationContext.isSecure()));
        return StringEncoder.encodeMap(fields);
    }

    public Authentication deserialize(String serializedAuthenticationContext) {
        Map<String, String> fields = StringEncoder.decodeMap(serializedAuthenticationContext);
        return new Authentication() {
            @Override
            public Principal getPrincipal() {
                return () -> fields.get("identity");
            }

            @Override
            public Map<String, Object> getAttributes() {
                return Collections.emptyMap();
            }

            @Override
            public boolean isSecure() {
                return Boolean.parseBoolean(fields.get("secure"));
            }
        };
    }
}
