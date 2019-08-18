package io.codeleaf.sec.idgen.dsa;

import io.codeleaf.common.behaviors.Identity;
import io.codeleaf.common.behaviors.impl.DefaultIdentification;
import io.codeleaf.common.behaviors.impl.DefaultIdentity;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.spi.IdentityGenerator;

import java.security.*;
import java.util.UUID;

public final class DsaIdentityGenerator implements IdentityGenerator {

    private KeyPairGenerator generator;

    public void init() throws SecurityException {
        try {
            generator = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
            generator.initialize(1024, secureRandom);
        } catch (NoSuchAlgorithmException | NoSuchProviderException cause) {
            throw new SecurityException("Failed to init DsaIdentityGenerator: " + cause.getMessage(), cause);
        }
    }

    @Override
    public Identity generate(String name) {
        Principal principal = () -> name;
        UUID uuid = UUID.randomUUID();
        KeyPair keyPair = generator.genKeyPair();
        return new DefaultIdentity(new DefaultIdentification(principal, keyPair.getPublic(), uuid), keyPair.getPrivate());
    }

}
