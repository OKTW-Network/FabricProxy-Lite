package one.oktw.mixin;

import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import org.jetbrains.annotations.Nullable;

public interface ServerLoginNetworkHandler_DelayHello {
    @Nullable
    default LoginHelloC2SPacket delayedHelloPacket() {
        return null;
    }
}
