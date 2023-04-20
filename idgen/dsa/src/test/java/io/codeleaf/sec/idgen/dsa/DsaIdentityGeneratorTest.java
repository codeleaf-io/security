package io.codeleaf.sec.idgen.dsa;

import io.codeleaf.common.behaviors.Identity;
import io.codeleaf.sec.SecurityException;
import io.codeleaf.sec.spi.IdentityGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DsaIdentityGeneratorTest {

    @Test
    public void testGenerate() throws SecurityException {
        // Given
        IdentityGenerator generator = new DsaIdentityGenerator();
        generator.init();

        // When
        Identity result = generator.generate("test");

        // Then
        Assertions.assertEquals("test", result.getPrincipal().getName());
    }

    @Test
    public void testGenerate_SameName() throws SecurityException {
        // Given
        IdentityGenerator generator = new DsaIdentityGenerator();
        generator.init();
        Identity identity = generator.generate("sameName");

        // When
        Identity result = generator.generate("sameName");

        // Then
        Assertions.assertNotEquals(identity.getPublicId(), result.getPublicId());
        Assertions.assertNotEquals(identity, result);
    }
}
