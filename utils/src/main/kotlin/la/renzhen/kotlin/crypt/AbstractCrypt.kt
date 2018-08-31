package la.renzhen.kotlin.crypt

import la.renzhen.kotlin.codes.Base64
import java.nio.charset.Charset
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 30/08/2018 8:48 PM
 */


abstract class AbstractCrypt @JvmOverloads constructor(password: String, ivParameter: String? = "XCryptV!12345678", internal var charset: Charset = Charsets.UTF_8) : Crypt {

    internal var key: Key
    internal var encryptCipher: Cipher
    internal var decryptCipher: Cipher

    protected abstract val cipher: Cipher

    init {
        try {
            this.key = generatorKey(password)
            this.encryptCipher = cipher(ivParameter, Cipher.ENCRYPT_MODE)
            this.decryptCipher = cipher(ivParameter, Cipher.DECRYPT_MODE)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(Exception::class)
    private fun cipher(ivParamter: String?, mode: Int): Cipher {
        val cipher = cipher
        if (ivParamter == null) {
            cipher.init(mode, key)
        } else {
            val ips = IvParameterSpec(ivParamter.toByteArray())
            cipher.init(mode, key, ips)
        }
        return cipher
    }

    @Throws(Exception::class)
    protected abstract fun generatorKey(secretKey: String): Key

    override fun encrypt(data: String): String {
        val encryptData = encrypt(data.toByteArray(charset))
        return Base64.encode(encryptData)
    }

    override fun encrypt(data: ByteArray): ByteArray {
        return encryptCipher.doFinal(data)
    }

    override fun decrypt(data: String): String {
        return String(decrypt(Base64.decode(data.toByteArray())), charset)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        return decryptCipher.doFinal(data)
    }
}
