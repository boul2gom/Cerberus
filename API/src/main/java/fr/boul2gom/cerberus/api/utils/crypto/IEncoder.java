package fr.boul2gom.cerberus.api.utils.crypto;

import javax.crypto.Cipher;

public interface IEncoder {

    Cipher getEncrypt();

    Cipher getDecrypt();
}
