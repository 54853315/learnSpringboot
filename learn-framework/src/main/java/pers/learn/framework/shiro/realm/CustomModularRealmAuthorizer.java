package pers.learn.framework.shiro.realm;

import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import pers.learn.common.constant.Auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CustomModularRealmAuthorizer extends ModularRealmAuthorizer {
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        assertRealmsConfigured();
        // 所有Realm
        Collection<Realm> realms = getRealms();
        HashMap<String, Realm> realmHashMap = new HashMap<>(realms.size());
        for (Realm realm : realms) {
            if (realm.getName().contains(Auth.BACKEND_USER)) {
                realmHashMap.put("ShiroRealmAdmin", realm);
            } else if (realm.getName().contains(Auth.USER)) {
                realmHashMap.put("ShiroRealmCustomer", realm);
            }
        }

        Set<String> realmNames = principals.getRealmNames();
        if (realmNames != null) {
            String realmName = null;
            Iterator<String> it = realmNames.iterator();
            while (it.hasNext()) {
                realmName = ConvertUtils.convert(it.next());
                if (realmName.contains(Auth.BACKEND_USER)) {
                    return ((BackendUserRealm) realmHashMap.get("ShiroRealmAdmin")).isPermitted(principals, permission);
                } else if (realmName.contains(Auth.USER)) {
                    return ((UserRealm) realmHashMap.get("ShiroRealmCustomer")).isPermitted(principals, permission);
                }
                break;
            }
        }
        return false;
    }
}


