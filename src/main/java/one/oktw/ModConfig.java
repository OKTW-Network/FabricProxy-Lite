package one.oktw;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Config(name = "FabricProxy-Lite")
public class ModConfig implements ConfigData {
    private boolean hackOnlineMode = true;
    private boolean hackEarlySend = false;

    @Comment("Velocity proxy secret")
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

    public String getSecret() {
        String env = System.getenv("FABRIC_PROXY_SECRET");
        if (env == null) {
            return secret;
        } else {
            return env;
        }
    }
}
