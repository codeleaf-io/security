package io.codeleaf.sec.password.ldap;

import io.codeleaf.config.Configuration;


/*
 Object objUser = GetObject("LDAP://cn=Joe Smith,ou=East,dc=MyDomain,dc=com")
Object objComputer = GetObject("LDAP://cn=Test2,cn=Users,dc=MyDomain,dc=com")
Object objGroup = GetObject("LDAP://cn=Engr,ou=East,dc=MyDomain,dc=net")
Object objOU = GetObject("LDAP://ou=Sales,ou=East,dc=MyDomain,dc=MyFirm,dc=com")

where:

LDAP:	The provider (case sensitive)
objUser, objComputer, objGroup, objOU	Variable referring to the object
cn=Joe Smith,ou=East,dc=MyDomain,dc=net	Distinguished Name of user "Joe Smith"
cn=Joe Smith	Relative Distinguished Name of user "Joe Smith"
dc=MyDomain,dc=com	DNS domain name (MyDomain.com)
cn=Users	Relative Distinguished Name of container "Users"
ou=East	Organizational Unit where user "Joe Smith" resides
cn	Common Name
ou	Organizational Unit
dc	Domain Component
O       organizationName
C       countryName
L       localityName
ST      stateOrProvinceName
STREET  streetAddress
UID     userid
*/
public final class LdapConfiguration implements Configuration {

    private final String host;
    private final int port;
    private final String dnsDomainNameComponent;
    private final String organizationalUnit;
    private final String organizationName;
    private final LdapEnvironment.Factory factory;

    public LdapConfiguration(String host, int port, String dnsDomainNameComponent, String organizationalUnit, String organizationName) {
        this.host = host;
        this.port = port;
        this.dnsDomainNameComponent = dnsDomainNameComponent;
        this.organizationalUnit = organizationalUnit;
        this.organizationName = organizationName;
        this.factory = new LdapEnvironment.Factory(this);
    }

    public String getHost() {
        return host;
    }

    public String getDnsDomainNameComponent() {
        return dnsDomainNameComponent;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public int getPort() {
        return port;
    }

    public String getLdapUrl() {
        return "ldap://" + getHost() + ":" + getPort();
    }

    public String getBase() {
        return "ou=" + getOrganizationalUnit() + "dc=" + getDnsDomainNameComponent();
    }

    public LdapEnvironment.Factory getEnvironmentFactory() {
        return factory;
    }

}
