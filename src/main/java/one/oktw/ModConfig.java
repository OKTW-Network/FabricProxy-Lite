package one.oktw;

@SuppressWarnings({ "FieldCanBeLocal", "FieldMayBeFinal" })
public class ModConfig {
    private boolean hackOnlineMode = true;
    private boolean hackEarlySend = false;
    private boolean hackMessageChain = true;
    private String disconnectMessage = "This server requires you to connect with Velocity.";
    private boolean allowBypassProxy = false;
    private boolean allowLocalOffline = false;
    private String localIp = "/127.0.0.1";

    private String secret = "";

    public String getAbortedMessage() {
        String env = System.getenv("FABRIC_PROXY_MESSAGE");
        if (env == null) {
            return disconnectMessage;
        } else {
            return env;
        }
    }

    public boolean getHackOnlineMode() {
        String env = System.getenv("FABRIC_PROXY_HACK_ONLINE_MODE");
        if (env == null) {
            return hackOnlineMode;
        } else {
            return Boolean.parseBoolean(env);
        }
    }
    public String getLocalIp() {
        String env = System.getenv("FABRIC_PROXY_LOCAL_IP");
        if (env == null) {
            return localIp;
        } else {
            return env;
        }
    }

    public boolean getAllowLocalOffline() {
        String env = System.getenv("FABRIC_PROXY_ALLOW_LOCAL_OFFLINE");
        if (env == null) {
            return allowLocalOffline;
        } else {
            return Boolean.parseBoolean(env);
        }
    }

    public boolean getHackEarlySend() {
        String env = System.getenv("FABRIC_PROXY_HACK_FABRIC_API");
        if (env == null) {
            return hackEarlySend;
        } else {
            return Boolean.parseBoolean(env);
        }
    }

    public boolean getHackMessageChain() {
        String env = System.getenv("FABRIC_PROXY_HACK_MESSAGE_CHAIN");
        if (env == null) {
            return hackMessageChain;
        } else {
            return Boolean.parseBoolean(env);
        }
    }

    public String getSecret() {
        String env = System.getenv("FABRIC_PROXY_SECRET");
        if (env == null) {
            return secret;
        } else {
            return env;
        }
    }

    public boolean getallowBypassProxy() {
        String env = System.getenv("ALLOW_BYPASS_PROXY");
        if (env == null) {
            return allowBypassProxy;
        } else {
            return Boolean.parseBoolean(env);
        }
    }

}
