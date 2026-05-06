package grupo10.olympo_academy.controllers.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_TIME_MS = 15 * 60 * 1000; // 15 min

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUntil = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blockedUntil.remove(username);
    }

    public void loginFailed(String username) {
       attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        if (attempts.get(username) >= MAX_ATTEMPTS) {
            blockedUntil.put(username, System.currentTimeMillis() + BLOCK_TIME_MS);
        }
    }

    public boolean isBlocked(String username) {
        Long until = blockedUntil.get(username);

        if (until == null) return false;

        if (System.currentTimeMillis() > until) {
            blockedUntil.remove(username);
            attempts.remove(username);
            return false;
        }

        return true;
    }
}
