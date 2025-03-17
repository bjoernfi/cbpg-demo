package cbpg.demo.cbpgext;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.jboss.logging.Logger;

import java.util.*;

public class TokenInvalidationLDAPMapper extends AbstractLDAPStorageMapper {
    private static final Logger LOGGER = Logger.getLogger(TokenInvalidationLDAPMapper.class);
    private static final String KC_ATTR_LAST_PW_CHANGE = "lastPWChange";

    /**
     * Try to lookup these attributes in the given order.
     * The first attribute for which a value exists is used as the reference for the
     * last password change timestamp.
     */
    private final List<String> LDAP_ATTRIBUTES = List.of(
            "pwdChangedTime", // OpenLDAP
            "pwdLastSet" // MSAD
    );

    public TokenInvalidationLDAPMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
    }

    @Override
    public void onImportUserFromLDAP(
            LDAPObject ldapUser, UserModel user, RealmModel realm, boolean isCreate
    ) {
        var lastPwChangeNew = LDAP_ATTRIBUTES.stream().map(ldapUser::getAttributeAsString)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);

        var lastPwChangeOld = user.getFirstAttribute(KC_ATTR_LAST_PW_CHANGE);
        if (lastPwChangeOld != null && !lastPwChangeOld.equals(lastPwChangeNew)) {
            session.sessions().removeUserSessions(realm, user);
            LOGGER.info(String.format("Invalidated tokens for user %s (id %s)", user.getUsername(),
                    user.getId()
            ));
        }

        user.setSingleAttribute(KC_ATTR_LAST_PW_CHANGE, lastPwChangeNew);
    }

    @Override
    public void onRegisterUserToLDAP(LDAPObject ldapUser, UserModel localUser, RealmModel realm) {

    }

    @Override
    public void beforeLDAPQuery(LDAPQuery query) {
        LDAP_ATTRIBUTES.forEach(query::addReturningLdapAttribute);
    }

    @Override
    public UserModel proxy(LDAPObject ldapUser, UserModel delegate, RealmModel realm) {
        return delegate;
    }
}