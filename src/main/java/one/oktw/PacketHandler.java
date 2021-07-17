package one.oktw;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import one.oktw.mixin.ClientConnection_AddressAccessor;
import one.oktw.mixin.ServerLoginNetworkHandler_ProfileAccessor;
import org.apache.logging.log4j.LogManager;

class PacketHandler {
    private final ModConfig config;

    PacketHandler(ModConfig config) {
        this.config = config;
    }

    void handleVelocityPacket(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
        if (!understood) {
            handler.disconnect(new LiteralText("This server requires you to connect with Velocity."));
            return;
        }

        synchronizer.waitFor(server.submit(() -> {
            try {
                if (!VelocityLib.checkIntegrity(buf)) {
                    handler.disconnect(new LiteralText("Unable to verify player details"));
                    return;
                }
            } catch (Throwable e) {
                LogManager.getLogger().error("Secret check failed.", e);
                handler.disconnect(new LiteralText("Unable to verify player details"));
                return;
            }

            ((ClientConnection_AddressAccessor) handler.connection).setAddress(new java.net.InetSocketAddress(VelocityLib.readAddress(buf), ((java.net.InetSocketAddress) handler.connection.getAddress()).getPort()));

            GameProfile profile;
            try {
                profile = VelocityLib.createProfile(buf);
            } catch (Exception e) {
                LogManager.getLogger().error("Profile create failed.", e);
                handler.disconnect(new LiteralText("Unable to read player profile"));
                return;
            }

            if (config.getHackEarlySend()) {
                handler.onHello(new LoginHelloC2SPacket(profile));
            } else {
                ((ServerLoginNetworkHandler_ProfileAccessor) handler).setProfile(profile);
            }
        }));
    }
}
