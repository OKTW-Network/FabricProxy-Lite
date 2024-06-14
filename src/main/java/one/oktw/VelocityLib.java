package one.oktw;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.util.Arrays.binarySearch;

public class VelocityLib {
    public static final Identifier PLAYER_INFO_CHANNEL = Identifier.of("velocity", "player_info");
    public static final int MODERN_FORWARDING_DEFAULT = 1;
    public static final int MODERN_FORWARDING_WITH_KEY = 2;
    public static final int MODERN_FORWARDING_WITH_KEY_V2 = 3;
    public static final int MODERN_LAZY_SESSION = 4;
    public static final PacketByteBuf PLAYER_INFO_PACKET = new PacketByteBuf(Unpooled.wrappedBuffer(new byte[]{(byte) VelocityLib.MODERN_LAZY_SESSION}).asReadOnly());
    private static final int[] SUPPORTED_FORWARDING_VERSION = {MODERN_FORWARDING_DEFAULT, MODERN_LAZY_SESSION};

    public static boolean checkIntegrity(final PacketByteBuf buf) {
        final byte[] signature = new byte[32];
        buf.readBytes(signature);

        final byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);

        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(FabricProxyLite.config.getSecret().getBytes(), "HmacSHA256"));
            final byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false;
            }
        } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }

        return true;
    }

    public static void checkVersion(final PacketByteBuf buf) {
        int version = buf.readVarInt();
        if (binarySearch(SUPPORTED_FORWARDING_VERSION, version) < 0) {
            throw new IllegalStateException("Unsupported forwarding version " + version + ", supported " + Arrays.toString(SUPPORTED_FORWARDING_VERSION));
        }

    }

    public static InetAddress readAddress(final PacketByteBuf buf) {
        return InetAddresses.forString(buf.readString(Short.MAX_VALUE));
    }

    public static GameProfile createProfile(final PacketByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.readUuid(), buf.readString(16));
        readProperties(buf, profile);
        return profile;
    }

    private static void readProperties(final PacketByteBuf buf, final GameProfile profile) {
        final int properties = buf.readVarInt();
        for (int i1 = 0; i1 < properties; i1++) {
            final String name = buf.readString(Short.MAX_VALUE);
            final String value = buf.readString(Short.MAX_VALUE);
            final String signature = buf.readBoolean() ? buf.readString(Short.MAX_VALUE) : null;
            profile.getProperties().put(name, new Property(name, value, signature));
        }
    }
}
