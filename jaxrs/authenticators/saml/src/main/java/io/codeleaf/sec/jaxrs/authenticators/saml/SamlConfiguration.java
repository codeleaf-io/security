package io.codeleaf.sec.jaxrs.authenticators.saml;

import io.codeleaf.config.Configuration;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.metadata.MetadataManager;

import java.util.Collections;

public final class SamlConfiguration implements Configuration {

    private final String idpMetaUrl;
    private final int idpRequestTimeout;
    private final String entityId;
    private final CachingMetadataManager metadataManager;
    private final MetadataGeneratorFilter metadataGeneratorFilter;

    public SamlConfiguration(String idpMetaUrl, int idpRequestTimeout, String entityId) throws MetadataProviderException {
        this.idpMetaUrl = idpMetaUrl;
        this.idpRequestTimeout = idpRequestTimeout;
        this.entityId = entityId;
        this.metadataManager = new CachingMetadataManager(Collections.singletonList(new HTTPMetadataProvider(idpMetaUrl, idpRequestTimeout)));
        MetadataGenerator generator = new MetadataGenerator();
        generator.setEntityId(entityId);
        this.metadataGeneratorFilter = new MetadataGeneratorFilter(generator);
    }

    public String getIdpMetaUrl() {
        return idpMetaUrl;
    }

    public int getIdpRequestTimeout() {
        return idpRequestTimeout;
    }

    public String getEntityId() {
        return entityId;
    }

    public MetadataManager getMetadataManager() {
        return metadataManager;
    }

    public MetadataGeneratorFilter getMetadataGeneratorFilter() {
        return metadataGeneratorFilter;
    }

}
