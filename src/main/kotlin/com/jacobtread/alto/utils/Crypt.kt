package com.jacobtread.alto.utils

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Crypt An object for doing cryptographic functions such
 * as encryption and decryption
 *
 * @constructor Create empty Crypt
 */
object Crypt {

    /**
     * createSecretKey Generates a secret key using the
     * AES encryption algorithm
     *
     * @return The secret key
     */
    fun createSecretKey(): SecretKey {
        try {
            val generator = KeyGenerator.getInstance("AES")
            generator.init(128)
            return generator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw Error(e)
        }
    }

    /**
     * generateKeyPair Creates a new public, private key pair
     * using RSA encryption
     *
     * @return The public, private keypair
     */
    fun generateKeyPair(): KeyPair {
        return try {
            val generator = KeyPairGenerator.getInstance("RSA")
            generator.initialize(1024)
            generator.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    /**
     * getServerHash Creates a hash of the id, publicKey, and privateKey
     * provided. Used to tell the minecraft session server what server we
     * are on
     *
     * @param id The server ID
     * @param publicKey The public key
     * @param secretKey The private/secret key
     * @return The resulting hash
     */
    fun getServerHash(id: String, publicKey: PublicKey, secretKey: SecretKey): String {
        try {
            val digest = MessageDigest.getInstance("SHA-1")
            digest.update(id.toByteArray(StandardCharsets.ISO_8859_1))
            digest.update(secretKey.encoded)
            digest.update(publicKey.encoded)
            return BigInteger(digest.digest()).toString(16)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    /**
     * decodePublicKey Decodes the provided public key which
     * is stored in the form of a [ByteArray] so that it could
     * be sent via a packet
     *
     * @param encoded The [ByteArray] encoded public key
     * @return The decoded public key
     */
    fun decodePublicKey(encoded: ByteArray): PublicKey {
        try {
            val keySpec = X509EncodedKeySpec(encoded)
            val factory = KeyFactory.getInstance("RSA")
            return factory.generatePublic(keySpec)
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }

    /**
     * decryptSharedKey Decrypts the shared key stored in the [sharedKey] [ByteArray]
     * using the provided private key [key]
     *
     * @param key The private key to decrypt with
     * @param sharedKey The encoded bytes of the shared key
     * @return The decrypted shared key
     */
    fun decryptSharedKey(key: PrivateKey, sharedKey: ByteArray): SecretKeySpec =
        SecretKeySpec(decrypt(key, sharedKey), "AES")

    /**
     * decrypt Decrypts the provided [ByteArray] [data] using the provided
     * key [key] and returns the result
     *
     * @param key The key to decrypt with
     * @param data The data to decrypt
     * @return The decrypted bytes
     */
    fun decrypt(key: Key, data: ByteArray) = cipherOperation(2, key, data)

    /**
     * encrypt Encrypts the provided [ByteArray] [data] using the provided
     * key [key] and returns the result
     *
     * @param key The key to encrypt with
     * @param data The data to encrypt
     * @return The encrypted bytes
     */
    fun encrypt(key: Key, data: ByteArray) = cipherOperation(1, key, data)

    /**
     * cipherOperation Runs the provided [opMode] on the provided
     * [ByteArray] [data] using the provided [key]
     *
     * @param opMode The op mode to use
     * @param key The key to use
     * @param data The data to run the cipher operation on
     * @return The resulting data from the cipher
     */
    fun cipherOperation(opMode: Int, key: Key, data: ByteArray): ByteArray {
        return try {
            val cipher = Cipher.getInstance(key.algorithm)
            cipher.init(opMode, key)
            cipher.doFinal(data)
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }

    /**
     * createNetCipher Creates a cipher used by networking encryption
     * and decryption with the provided [opMode] and [key]
     *
     * @param opMode The chosen op mode
     * @param key The key to create for
     * @return The resulting cipher
     */
    fun createNetCipher(opMode: Int, key: Key): Cipher {
        try {
            val cipher = Cipher.getInstance("AES/CFB8/NoPadding")
            cipher.init(opMode, key, IvParameterSpec(key.encoded))
            return cipher
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }
}