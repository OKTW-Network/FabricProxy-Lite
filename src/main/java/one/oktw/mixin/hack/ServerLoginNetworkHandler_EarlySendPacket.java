package one.oktw.mixin.hack;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static one.oktw.VelocityLib.PLAYER_INFO_CHANNEL;
import static one.oktw.VelocityLib.PLAYER_INFO_PACKET;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandler_EarlySendPacket {
    @Shadow
    @Final
    ClientConnection connection;
    @Shadow
    @Nullable
    private GameProfile profile;

    @Inject(method = "onHello", at = @At(value = "HEAD"), cancellable = true)
    private void skipKeyPacket(LoginHelloC2SPacket packet, CallbackInfo ci) {
        if (profile != null) return; // Already receive profile form velocity.

        ServerLoginNetworkAddon addon = (ServerLoginNetworkAddon) ((NetworkHandlerExtensions) this).getAddon();
        connection.send(addon.createPacket(PLAYER_INFO_CHANNEL, PLAYER_INFO_PACKET));
        ci.cancel();
    }
}
