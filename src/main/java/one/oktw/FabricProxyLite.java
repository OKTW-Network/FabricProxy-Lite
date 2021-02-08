package one.oktw;

import com.mojang.authlib.GameProfile;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import one.oktw.mixin.ClientConnection_AddressAccessor;
import one.oktw.mixin.ServerLoginNetworkHandler_ProfileAccessor;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FabricProxyLite implements DedicatedServerModInitializer, IMixinConfigPlugin {
    public static ModConfig config;

    @Override
    public void onInitializeServer() {
        // Packet receiver
        ServerLoginNetworking.registerGlobalReceiver(VelocityLib.PLAYER_INFO_CHANNEL, this::handleVelocityPacket);
        if (!config.getHackEarlySend()) {
            ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> sender.sendPacket(VelocityLib.PLAYER_INFO_CHANNEL, PacketByteBufs.empty()));
        }
    }

    private void handleVelocityPacket(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
        if (!understood) {
            handler.disconnect(new LiteralText("This server requires you to connect with Velocity."));
            return;
        }

        synchronizer.waitFor(server.submit(() -> {
            if (!VelocityLib.checkIntegrity(buf)) {
                handler.disconnect(new LiteralText("Unable to verify player details"));
                return;
            }

            ((ClientConnection_AddressAccessor) handler.connection).setAddress(new java.net.InetSocketAddress(VelocityLib.readAddress(buf), ((java.net.InetSocketAddress) handler.connection.getAddress()).getPort()));

            GameProfile profile = VelocityLib.createProfile(buf);

            if (config.getHackEarlySend()) {
                handler.onHello(new LoginHelloC2SPacket(profile));
            } else {
                ((ServerLoginNetworkHandler_ProfileAccessor) handler).setProfile(profile);
            }
        }));
    }

    // Only load hack mixin if enabled
    @Override
    public void onLoad(String mixinPackage) {
        if (config == null) {
            AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
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
