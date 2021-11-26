package one.oktw;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

public class FabricProxyLite implements DedicatedServerModInitializer, IMixinConfigPlugin {
    public static ModConfig config;

    @Override
    public void onInitializeServer() {
        // Packet receiver
        ServerLoginNetworking.registerGlobalReceiver(VelocityLib.PLAYER_INFO_CHANNEL, new PacketHandler(config)::handleVelocityPacket);
        if (!config.getHackEarlySend()) {
            ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> sender.sendPacket(VelocityLib.PLAYER_INFO_CHANNEL, PacketByteBufs.empty()));
        }
    }

    // Only load hack mixin if enabled
    @Override
    public void onLoad(String mixinPackage) {
        if (config == null) {
            var configFile = FabricLoader.getInstance().getConfigDir().resolve("FabricProxy-Lite.toml");
            if (!Files.exists(configFile)) {
                config = new ModConfig();
                try {
                    new TomlWriter().write(config, configFile.toFile());
                } catch (IOException e) {
                    LogManager.getLogger().error("Init config failed.", e);
                }
            } else {
                config = new Toml().read(FabricLoader.getInstance().getConfigDir().resolve("FabricProxy-Lite.toml").toFile()).to(ModConfig.class);
            }
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return mixinClassName.equals("one.oktw.mixin.hack.ServerLoginNetworkHandler_EarlySendPacket") && config.getHackEarlySend()
                || mixinClassName.equals("one.oktw.mixin.hack.ServerLoginNetworkHandler_SkipKeyPacket") && config.getHackOnlineMode();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
