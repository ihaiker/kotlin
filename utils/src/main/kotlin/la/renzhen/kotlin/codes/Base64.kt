package la.renzhen.kotlin.codes

import java.util.Base64 as Base64Sys

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 30/08/2018 9:46 PM
 */
object Base64 {

    /**
     * 使用base64编码
     *
     * @param data 编码
     * @return 编码结果
     */
    fun encode(data: ByteArray): String {
        return Base64Sys.getEncoder().encodeToString(data)
    }

    /**
     * Base64编码
     *
     * @param str 需要编码内容
     * @return 编码结果
     */
    fun encode(str: String): String {
        return encode(str.toByteArray())
    }

    /**
     * Base64解码
     *
     * @param str 需要解码内容
     * @return 编码结果string
     */
    fun decode(str: String): String {
        return String(decode2bytes(str))
    }


    fun decode2bytes(str: String): ByteArray {
        return decode(str.toByteArray())
    }

    /**
     * Base64解码
     *
     * @param data 要解密的字符串
     * @return 解码内容字节
     */
    fun decode(data: ByteArray): ByteArray {
        return Base64Sys.getDecoder().decode(data)
    }
}

fun ByteArray.base64(): String {
    return Base64.encode(this)
}

fun String.decodeBase64(): ByteArray {
    return Base64.decode2bytes(this)
}

fun String.base64(): String {
    return Base64.encode(this)
}

