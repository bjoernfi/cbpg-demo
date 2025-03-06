package cbpg.demo.pwdchangeldap;

import org.keycloak.component.ComponentModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;

public class PwdChangeLDAPMapperFactory extends AbstractLDAPStorageMapperFactory {

    @Override
    protected AbstractLDAPStorageMapper createMapper(ComponentModel mapperModel, LDAPStorageProvider federationProvider) {
        return new PwdChangeLDAPMapper(mapperModel, federationProvider);
    }

    @Override
    public String getId() {
        return "pwd-change-ldap-mapper";
    }
}