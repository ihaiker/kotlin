package la.renzhen.kotlin.crypt

import java.nio.charset.Charset
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec

/**
 * 加密Key必须大于 `DESedeKeySpec.DES_EDE_KEY_LEN` 24位
 * IvParamter 必须为8位
 */
class Des3Crypt constructor(password: String = "Evildoer Des3 Crypt Key.", ivParameter: String = "XCrypt!!", charset: Charset = Charsets.UTF_8) : AbstractCrypt(password, ivParameter, charset) {

    override val cipher: Cipher
        @Throws(Exception::class)
        get() = Cipher.getInstance("desede/CBC/PKCS5Padding")


    @Throws(Exception::class)
    override fun generatorKey(secretKey: String): Key {
        val spec = DESedeKeySpec(secretKey.toByteArray())
        val keyFactory = SecretKeyFactory.getInstance("desede")
        return keyFactory.generateSecret(spec)
    }


}
