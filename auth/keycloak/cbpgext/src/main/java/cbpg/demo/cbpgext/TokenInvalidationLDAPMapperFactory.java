package cbpg.demo.cbpgext;

import org.keycloak.component.ComponentModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;

public class TokenInvalidationLDAPMapperFactory extends AbstractLDAPStorageMapperFactory {

    @Override
    protected AbstractLDAPStorageMapper createMapper(ComponentModel mapperModel, LDAPStorageProvider federationProvider) {
        return new TokenInvalidationLDAPMapper(mapperModel, federationProvider);
    }

    @Override
    public String getId() {
        return "cbpg-token-inv-ldap-mapper";
    }
}