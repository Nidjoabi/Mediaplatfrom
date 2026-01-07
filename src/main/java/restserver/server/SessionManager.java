package restserver.server;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static SessionManager instance = null;

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public static class Session {
        public final String id;
        public final long userId;
        public final Instant expiresAt;

        public Session(String id, long userId, Instant expiresAt) {
            this.id = id;
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }

    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final long ttlSeconds = 60 * 60; // 1h

    private SessionManager() {
    }

    public Session createSession(long userId) {
        String id = UUID.randomUUID().toString();
        Session s = new Session(id, userId, Instant.now().plusSeconds(ttlSeconds));
        sessions.put(id, s);
        return s;
    }

    public Session getValidSession(String sessionId) {
        if (sessionId == null) return null;
        Session s = sessions.get(sessionId);
        if (s == null) return null;
        if (Instant.now().isAfter(s.expiresAt)) {
            sessions.remove(sessionId);
            return null;
        }
        return s;
    }

}
