package one.oktw.mixin.hack;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandler_SkipKeyPacket {
    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnlineMode()Z"))
    private boolean skipKeyPacket(MinecraftServer minecraftServer) {
        return false;
    }
}
