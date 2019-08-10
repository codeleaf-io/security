package io.codeleaf.sec.spi;

public interface SessionDataStore {

    String storeSessionData(String sessionData);

    String retrieveSessionData(String sessionId);
}
