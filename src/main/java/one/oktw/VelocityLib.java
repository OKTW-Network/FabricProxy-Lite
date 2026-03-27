package one.oktw;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import net.minecraft.resources.Identifier;

import static java.util.Arrays.binarySearch;

public class VelocityLib {
    public static final Identifier PLAYER_INFO_CHANNEL = Identifier.fromNamespaceAndPath("velocity", "player_info");
    public static final int MODERN_FORWARDING_DEFAULT = 1;
    public static final int MODERN_FORWARDING_WITH_KEY = 2;
    public static final int MODERN_FORWARDING_WITH_KEY_V2 = 3;
    public static final int MODERN_LAZY_SESSION = 4;
    public static final FriendlyByteBuf PLAYER_INFO_PACKET = new FriendlyByteBuf(Unpooled.wrappedBuffer(new byte[]{(byte) VelocityLib.MODERN_LAZY_SESSION}).asReadOnly());
    private static final int[] SUPPORTED_FORWARDING_VERSION = {MODERN_FORWARDING_DEFAULT, MODERN_LAZY_SESSION};

    public static boolean checkIntegrity(final FriendlyByteBuf buf) {
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

    public static void checkVersion(final FriendlyByteBuf buf) {
        int version = buf.readVarInt();
        if (binarySearch(SUPPORTED_FORWARDING_VERSION, version) < 0) {
            throw new IllegalStateException("Unsupported forwarding version " + version + ", supported " + Arrays.toString(SUPPORTED_FORWARDING_VERSION));
        }

    }

    public static InetAddress readAddress(final FriendlyByteBuf buf) {
        return InetAddresses.forString(buf.readUtf(Short.MAX_VALUE));
    }

    public static GameProfile createProfile(final FriendlyByteBuf buf) {
        return new GameProfile(buf.readUUID(), buf.readUtf(16), readProperties(buf));
    }

    private static PropertyMap readProperties(final FriendlyByteBuf buf) {
        final ImmutableMultimap.Builder<String, Property> propertiesBuilder = ImmutableMultimap.builder();
        final int properties = buf.readVarInt();
        for (int i1 = 0; i1 < properties; i1++) {
            final String name = buf.readUtf(Short.MAX_VALUE);
            final String value = buf.readUtf(Short.MAX_VALUE);
            final String signature = buf.readBoolean() ? buf.readUtf(Short.MAX_VALUE) : null;
            propertiesBuilder.put(name, new Property(name, value, signature));
        }
        final ImmutableMultimap<String, Property> propertiesMap = propertiesBuilder.build();
        return new PropertyMap(propertiesMap);
    }
}
