package one.oktw;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Identifier;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Arrays.binarySearch;
import static net.minecraft.network.encryption.NetworkEncryptionUtils.decodeEncodedRsaPublicKey;

public class VelocityLib {
    public static final Identifier PLAYER_INFO_CHANNEL = new Identifier("velocity", "player_info");

    public static final int MODERN_FORWARDING_DEFAULT = 1;
    public static final int MODERN_FORWARDING_WITH_KEY = 2;
    private static final int[] SUPPORTED_FORWARDING_VERSION = {MODERN_FORWARDING_DEFAULT, MODERN_FORWARDING_WITH_KEY};

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

    public static int checkVersion(final PacketByteBuf buf) {
        int version = buf.readVarInt();
        if (binarySearch(SUPPORTED_FORWARDING_VERSION, version) < 0) {
            throw new IllegalStateException("Unsupported forwarding version " + version + ", supported " + Arrays.toString(SUPPORTED_FORWARDING_VERSION));
        }

        return version;
    }

    public static InetAddress readAddress(final PacketByteBuf buf) {
        //noinspection UnstableApiUsage
        return InetAddresses.forString(buf.readString(Short.MAX_VALUE));
    }

    public static GameProfile createProfile(final PacketByteBuf buf) {
        final GameProfile profile = new GameProfile(buf.readUuid(), buf.readString(16));
        readProperties(buf, profile);
        return profile;
    }

    public static Optional<PlayerPublicKey> readKey(final PacketByteBuf buf) throws NetworkEncryptionException {
        Instant expiry = Instant.ofEpochMilli(buf.readLong());
        PublicKey key = decodeEncodedRsaPublicKey(buf.readByteArray(512));
        byte[] signature = buf.readByteArray(4096);

        return Optional.of(new PlayerPublicKey(new PlayerPublicKey.PublicKeyData(expiry, key, signature)));
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
