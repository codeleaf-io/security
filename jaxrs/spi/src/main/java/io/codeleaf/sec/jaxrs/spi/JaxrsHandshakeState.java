package io.codeleaf.sec.jaxrs.spi;

import io.codeleaf.common.utils.StringEncoder;

import java.net.URI;
import java.util.*;

// TODO: handshake state is not encrypted - is this a problem?
public final class JaxrsHandshakeState {

    private final URI uri;
    private final List<String> authenticatorNames = new LinkedList<>();

    public JaxrsHandshakeState(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    public String getLastAuthenticatorName() {
        return authenticatorNames.isEmpty() ?
                null : authenticatorNames.get(authenticatorNames.size() - 1);

    }

    public String getFirstAuthenticatorName() {
        return authenticatorNames.isEmpty() ?
                null : authenticatorNames.get(0);
    }

    public List<String> getAuthenticatorNames() {
        return authenticatorNames;
    }

    public String encode() {
        Map<String, String> fields = new HashMap<>();
        fields.put("u", uri.toString());
        fields.put("a", StringEncoder.encodeList(authenticatorNames));
        return StringEncoder.encodeMap(fields);
    }

    public static JaxrsHandshakeState decode(String encodedHandshakeState) {
        Objects.requireNonNull(encodedHandshakeState);
        Map<String, String> fields = StringEncoder.decodeMap(encodedHandshakeState);
        URI uri = URI.create(fields.get("u"));
        List<String> authenticatorNames = StringEncoder.decodeList(fields.get("a"));
        JaxrsHandshakeState handshakeState = new JaxrsHandshakeState(uri);
        handshakeState.getAuthenticatorNames().addAll(authenticatorNames);
        return handshakeState;
    }
}
