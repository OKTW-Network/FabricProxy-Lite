package one.oktw;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class ModConfig {
    private boolean hackOnlineMode = true;
    private boolean hackEarlySend = false;
    private boolean hackMessageChain = true;
    private String secret = "";

    public boolean getHackOnlineMode() {
        String env = System.getenv("FABRIC_PROXY_HACK_ONLINE_MODE");
        if (env == null) {
            return hackOnlineMode;
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
}
