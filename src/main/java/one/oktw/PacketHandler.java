package one.oktw;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import one.oktw.mixin.core.ConnectionAccessor;
import one.oktw.mixin.core.ServerLoginPacketListenerAccessor;
import org.apache.logging.log4j.LogManager;

class PacketHandler {
    private final ModConfig config;

    PacketHandler(ModConfig config) {
        this.config = config;
    }

    void handleVelocityPacket(MinecraftServer server, ServerLoginPacketListenerImpl handler, boolean understood, FriendlyByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender ignored) {
        if (!understood) {
            handler.disconnect(Component.nullToEmpty(config.getAbortedMessage()));
            return;
        }

        synchronizer.waitFor(server.submit(() -> {
            try {
                if (!VelocityLib.checkIntegrity(buf)) {
                    handler.disconnect(Component.nullToEmpty("Unable to verify player details"));
                    return;
                }
                VelocityLib.checkVersion(buf);
            } catch (Throwable e) {
                LogManager.getLogger().error("Secret check failed.", e);
                handler.disconnect(Component.nullToEmpty("Unable to verify player details"));
                return;
            }

            Connection connection = ((ServerLoginPacketListenerAccessor) handler).getConnection();
            ((ConnectionAccessor) connection).setAddress(new java.net.InetSocketAddress(VelocityLib.readAddress(buf), ((java.net.InetSocketAddress) (connection.getRemoteAddress())).getPort()));

            GameProfile profile;
            try {
                profile = VelocityLib.createProfile(buf);
            } catch (Exception e) {
                LogManager.getLogger().error("Profile create failed.", e);
                handler.disconnect(Component.nullToEmpty("Unable to read player profile"));
                return;
            }

            if (config.getHackEarlySend()) {
                ((ServerLoginPacketListenerAccessor) handler).setAuthenticatedProfile(profile);
                handler.handleHello(new ServerboundHelloPacket(profile.name(), profile.id()));
            }

            ((ServerLoginPacketListenerAccessor) handler).setAuthenticatedProfile(profile);
        }));
    }
}
