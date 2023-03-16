package one.oktw.mixin.hack;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

import static one.oktw.VelocityLib.PLAYER_INFO_CHANNEL;
import static one.oktw.VelocityLib.PLAYER_INFO_PACKET;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandler_EarlySendPacket {
    @Shadow
    @Nullable GameProfile profile;

    @Shadow
    @Final
    ClientConnection connection;

    @Inject(method = "onHello", at = @At(value = "HEAD"), cancellable = true)
    private void skipKeyPacket(LoginHelloC2SPacket packet, CallbackInfo ci) {
        if (profile != null && profile.isComplete()) return; // Already receive profile form velocity.

        connection.send(new LoginQueryRequestS2CPacket(ThreadLocalRandom.current().nextInt(), PLAYER_INFO_CHANNEL, PLAYER_INFO_PACKET));
        ci.cancel();
    }
}
