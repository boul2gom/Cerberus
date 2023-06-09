package fr.boul2gom.cerberus.common.utils.crypto;

import fr.boul2gom.cerberus.api.i18n.IMessage;
import fr.boul2gom.cerberus.api.utils.crypto.IEncoder;
import fr.boul2gom.cerberus.api.utils.types.Pair;

import javax.crypto.Cipher;
import java.nio.file.Path;

public class Encoder implements IEncoder {

    private final Cipher encrypt;
    private final Cipher decrypt;

    public Encoder(Pair.Double<Path> keys) {
        try {
            this.encrypt = Cipher.getInstance("RSA");
            this.decrypt = Cipher.getInstance("RSA");

            this.encrypt.init(Cipher.ENCRYPT_MODE, CertUtils.getPublic(keys.first()));
            this.decrypt.init(Cipher.DECRYPT_MODE, CertUtils.getPrivate(keys.second()));
        } catch (Exception e) {
            throw new RuntimeException(IMessage.get("error.encoder.initialization"), e);
        }
    }

    @Override
    public Cipher getEncrypt() {
        return this.encrypt;
    }

    @Override
    public Cipher getDecrypt() {
        return this.decrypt;
    }
}
