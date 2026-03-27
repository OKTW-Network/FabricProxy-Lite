package one.oktw.mixin.hack;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.impl.networking.PacketListenerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
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
@Mixin(ServerLoginPacketListenerImpl.class)
public class ServerLoginPacketListener_EarlySendPacket {
    @Shadow
    @Final
    private Connection connection;
    @Shadow
    @Nullable
    private GameProfile authenticatedProfile;

    @Inject(method = "handleHello", at = @At(value = "HEAD"), cancellable = true)
    private void earlySend(ServerboundHelloPacket packet, CallbackInfo ci) {
        if (authenticatedProfile != null) return; // Already receive profile form velocity.

        ServerLoginNetworkAddon addon = (ServerLoginNetworkAddon) ((PacketListenerExtensions) this).getAddon();
        connection.send(addon.createPacket(PLAYER_INFO_CHANNEL, PLAYER_INFO_PACKET));
        ci.cancel();
    }
}
