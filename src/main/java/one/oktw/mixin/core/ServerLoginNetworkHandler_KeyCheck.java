package one.oktw.mixin.core;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.mixin.IServerLoginNetworkHandler_RealUUID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandler_KeyCheck implements IServerLoginNetworkHandler_RealUUID {
    private UUID realUUID = null;

    @Redirect(method = "acceptPlayer", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getId()Ljava/util/UUID;", remap = false))
    private UUID overrideUuid(GameProfile instance) {
        return realUUID != null ? realUUID : instance.getId();
    }

    @Override
    public void setRealUUID(UUID uuid) {
        realUUID = uuid;
    }
}
