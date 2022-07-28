package one.oktw;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import one.oktw.mixin.IServerLoginNetworkHandler_RealUUID;
import one.oktw.mixin.core.ClientConnection_AddressAccessor;
import one.oktw.mixin.core.ServerLoginNetworkHandler_ProfileAccessor;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;
import java.util.UUID;

class PacketHandler {
    private final ModConfig config;

    PacketHandler(ModConfig config) {
        this.config = config;
    }

    void handleVelocityPacket(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender ignored) {
        if (!understood) {
            handler.disconnect(Text.of("This server requires you to connect with Velocity."));
            return;
        }

        synchronizer.waitFor(server.submit(() -> {
            int forwardVersion;
            try {
                if (!VelocityLib.checkIntegrity(buf)) {
                    handler.disconnect(Text.of("Unable to verify player details"));
                    return;
                }
                forwardVersion = VelocityLib.checkVersion(buf);
            } catch (Throwable e) {
                LogManager.getLogger().error("Secret check failed.", e);
                handler.disconnect(Text.of("Unable to verify player details"));
                return;
            }

            ((ClientConnection_AddressAccessor) handler.connection).setAddress(new java.net.InetSocketAddress(VelocityLib.readAddress(buf), ((java.net.InetSocketAddress) handler.connection.getAddress()).getPort()));

            GameProfile profile;
            try {
                profile = VelocityLib.createProfile(buf);
            } catch (Exception e) {
                LogManager.getLogger().error("Profile create failed.", e);
                handler.disconnect(Text.of("Unable to read player profile"));
                return;
            }

            // Public key
            boolean keyEnforce = server.shouldEnforceSecureProfile();
            Optional<PlayerPublicKey.PublicKeyData> publicKey = Optional.empty();
            Optional<UUID> profileId = Optional.empty();
            try {
                // Ignore v1 KEY, it not work on 1.19.1+
                if (forwardVersion >= VelocityLib.MODERN_FORWARDING_WITH_KEY_V2) {
                    publicKey = VelocityLib.readKey(buf);
                    profileId = VelocityLib.readUuid(buf);
                }

                if (keyEnforce && publicKey.isEmpty()) {
                    handler.disconnect(Text.translatable("multiplayer.disconnect.missing_public_key"));
                    return;
                }
            } catch (Exception e) {
                LogManager.getLogger().error("Public key read failed.", e);
                if (keyEnforce) {
                    handler.disconnect(Text.translatable("multiplayer.disconnect.invalid_public_key"));
                    return;
                }
            }

            if (config.getHackEarlySend()) {
                handler.onHello(new LoginHelloC2SPacket(profile.getName(), publicKey, profileId.or(() ->Optional.of(profile.getId()))));
            }

            ((ServerLoginNetworkHandler_ProfileAccessor) handler).setProfile(profile);
            publicKey.ifPresent(((ServerLoginNetworkHandler_ProfileAccessor) handler)::setPublicKeyData);
            profileId.ifPresent(((IServerLoginNetworkHandler_RealUUID) handler)::setRealUUID);
        }));
    }
}
