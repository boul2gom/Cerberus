package fr.boul2gom.cerberus.common.utils.crypto;

import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.configuration.IConfiguration;
import fr.boul2gom.cerberus.api.i18n.IMessage;
import fr.boul2gom.cerberus.api.utils.types.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CertUtils {

    public static Pair.Double<Path> fetch(IConfiguration configuration, Path data) throws RuntimeException {
        final Path first = data.resolve((String) configuration.get(ConfigurationKey.PUBLIC_KEY));
        final Path second = data.resolve((String) configuration.get(ConfigurationKey.PRIVATE_KEY));

        if (!Files.exists(first) || !Files.exists(second)) {
            throw new RuntimeException(IMessage.get("error.certificates.not-found"));
        }

        return new Pair.Double<>(first, second);
    }

    public static RSAPublicKey getPublic(Path file) throws Exception {
        final String key = CertUtils.format(file, "PUBLIC");
        final byte[] encoded = CertUtils.decode(key);

        final KeyFactory factory = KeyFactory.getInstance("RSA");
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);

        return (RSAPublicKey) factory.generatePublic(spec);
    }

    public static RSAPrivateKey getPrivate(Path file) throws Exception {
        final String key = CertUtils.format(file, "PRIVATE");
        final byte[] encoded = CertUtils.decode(key);

        final KeyFactory factory = KeyFactory.getInstance("RSA");
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encoded);

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private static String format(Path file, String word) throws Exception {
        final String key = Files.readString(file);

        return key.replace("-----BEGIN " + word + " KEY-----", "")
                .replaceAll("[\n|\r]", "")
                .replace("-----END " + word + " KEY-----", "");
    }

    private static byte[] decode(String key) {
        return Base64.getDecoder().decode(key);
    }
}
