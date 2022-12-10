package one.oktw.mixin.hack;

import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandler_HackMessageChain {
    @Inject(method = "getSignedMessage", at = @At("RETURN"), cancellable = true)
    private void disableSecureChat(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<SignedMessage> cir) {
        cir.setReturnValue(SignedMessage.ofUnsigned(packet.chatMessage()));
    }
}
