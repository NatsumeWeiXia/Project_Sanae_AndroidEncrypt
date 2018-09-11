package th.weixia.sanae.crypt;

import java.util.Date;
import java.util.HashMap;

import th.weixia.common.log.WxLog;

/**
 * Created by sy on 16.10.14.
 */
public class Keys {

    static String token;

    private static String sessionId;

    private static long sessionTimeout;

    private static HashMap<String, HashMap<String, String>> p2pKeys = new HashMap<>();

    private static HashMap<String, HashMap<String, String>> groupKeys = new HashMap<>();

    static void addP2pKey(String key,  HashMap<String, String> value) {
        synchronized (Keys.class) {
            p2pKeys.put(key, value);
        }
    }

    static void addGroupKey(String key,  HashMap<String, String> value) {
        synchronized (Keys.class) {
            groupKeys.put(key, value);
        }
    }

    static String getP2pKey(String key, String type) {
        synchronized (Keys.class) {
            return p2pKeys.get(key).get(type);
        }
    }

    static String getGroupKey(String key, String type) {
        synchronized (Keys.class) {
            return groupKeys.get(key).get(type);
        }
    }

    static boolean doAuthentication(String _token) {

        if (token == null) {
            token = _token;
            return true;
        }

        if (token.equals(_token)) {
            return true;
        }

        token = null;
        sessionId = null;
        p2pKeys.clear();
        groupKeys.clear();
        WxLog.e("Authentication", "Authentication failed need Init");
        return false;
    }

    static boolean needInit() {
        return sessionId == null || token == null;
    }

    static void setSessionId(String _sessionId, long _sessionTimeout) {
        sessionId = _sessionId;
        sessionTimeout = _sessionTimeout;
    }

    static String getSessionId() {
        if (new Date().getTime() >= sessionTimeout) {
            return null;
        }
        return sessionId;
    }
}
