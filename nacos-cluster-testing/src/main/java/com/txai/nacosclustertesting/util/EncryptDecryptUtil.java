package com.txai.nacosclustertesting.util;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.salt.RandomSaltGenerator;

@Slf4j
public class EncryptDecryptUtil {

    public static SimpleStringPBEConfig getPBEConfig(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM);
        config.setPoolSize(1);
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        return config;
    }

    public static String encrypt(String password, String value) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(getPBEConfig(password));
        String encrypt = encryptor.encrypt(value);
//        log.info("Encrypted password: " + encrypt);
        return encrypt;
    }

    public static String decrypt(String password, String encrypt) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(getPBEConfig(password));
        String decrypt = encryptor.decrypt(encrypt);
//        log.info("Decrypted password: " + decrypt);
        return decrypt;
    }

    public static void main(String[] args) {
        String name = "mypassword";
        String sale = "mysalt1";
        String encrypt = encrypt(sale, name);
        log.info("Encrypted password: " + encrypt);
        String decrypt = decrypt(sale, encrypt);
        log.info("Decrypted password: " + decrypt);
    }
}
