package one.oktw.mixin.hack;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.VelocityLib;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandler_EarlySendPacket {
    @Shadow
    @Final
    public ClientConnection connection;

    @Inject(method = "onHello", at = @At(value = "HEAD"), cancellable = true)
    private void skipKeyPacket(LoginHelloC2SPacket packet, CallbackInfo ci) {
        if (packet.getProfile().isComplete()) return; // Already receive profile form velocity.

        ((ServerLoginNetworkAddon) ((NetworkHandlerExtensions) this).getAddon()).sendPacket(VelocityLib.PLAYER_INFO_CHANNEL, PacketByteBufs.empty());
        ci.cancel();
    }
}
