package la.renzhen.kotlin.crypt


import java.nio.charset.Charset
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 30/08/2018 8:52 PM
 */
class AESCrypt @JvmOverloads constructor(
        password: String,
        charset: Charset = Charsets.UTF_8
) : AbstractCrypt(password, null, charset) {

    companion object {
        //生成密钥需要迭代的次数，不用太多，废性能
        internal var ITERATION_COUNT = 7
        internal var KEY_LENGTH = 128
        internal var SALT = byteArrayOf(0, 7, 2, 3, 4, 5, 6, 7, 8, 1, 0xA, 0xB, 0xE, 0xD, 0xE, 0xF) //需要转16进制
    }

    override val cipher: Cipher
        @Throws(Exception::class)
        get() = Cipher.getInstance("AES/ECB/PKCS5Padding")


    @Throws(Exception::class)
    override fun generatorKey(secretKey: String): Key {
        val spec = PBEKeySpec(secretKey.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH)
        val sk = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec)
        return SecretKeySpec(sk.encoded, "AES")
    }

}
