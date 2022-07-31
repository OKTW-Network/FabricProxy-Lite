package one.oktw.mixin.hack;

import net.minecraft.network.message.AcknowledgmentValidator;
import net.minecraft.network.message.LastSeenMessageList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;
import java.util.Set;

@Mixin(AcknowledgmentValidator.class)
public class AcknowledgmentValidator_SkipFirstMessageValidation {
    private boolean firstMessage = true;

    @Redirect(method = "validate", at = @At(value = "INVOKE", target = "Ljava/util/EnumSet;add(Ljava/lang/Object;)Z"))
    private boolean allowUnknownMessage(EnumSet<AcknowledgmentValidator.FailureReason> instance, Object o) {
        if (firstMessage && o == AcknowledgmentValidator.FailureReason.UNKNOWN_MESSAGES) return false;
        return instance.add((AcknowledgmentValidator.FailureReason) o);
    }

    @Inject(method = "validate", at = @At("RETURN"))
    private void firstMessageSanded(LastSeenMessageList.Acknowledgment acknowledgment, CallbackInfoReturnable<Set<AcknowledgmentValidator.FailureReason>> cir) {
        firstMessage = false;
    }
}
