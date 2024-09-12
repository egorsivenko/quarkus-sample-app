package org.acme.verification;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class VerificationTokenStorage {

    private final Map<String, VerificationToken> tokens;

    public VerificationTokenStorage() {
        tokens = Collections.synchronizedMap(new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, VerificationToken> eldest) {
                Set<Map.Entry<String, VerificationToken>> entries = entrySet();

                synchronized (this) {
                    Iterator<Map.Entry<String, VerificationToken>> iterator = entries.iterator();

                    while (iterator.hasNext()) {
                        VerificationToken token = iterator.next().getValue();
                        if (token.isExpired()) {
                            iterator.remove();
                        } else {
                            break;
                        }
                    }
                }
                return false;
            }
        });
    }

    public synchronized void create(String token, User user) {
        tokens.put(token, new VerificationToken(
                token,
                user,
                LocalDateTime.now().plusHours(1L)
        ));
    }

    public VerificationToken get(String token) {
        return tokens.get(token);
    }

    public void remove(String token) {
        tokens.remove(token);
    }
}
