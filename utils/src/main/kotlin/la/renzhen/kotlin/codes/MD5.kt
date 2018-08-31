package la.renzhen.kotlin.codes

import java.security.MessageDigest

/**
 * 采用MD5加密
 */
object MD5 {
    /***
     * MD5加码 生成32位md5码
     * @param inStr 输入值
     * @return md5
     */
    fun encode(inStr: String): String {
        val md5: MessageDigest = MessageDigest.getInstance("MD5")
        val charArray = inStr.toCharArray()
        val byteArray = ByteArray(charArray.size)

        for (i in charArray.indices) {
            byteArray[i] = charArray[i].toByte()
        }
        val md5Bytes = md5.digest(byteArray)
        val hexValue = StringBuffer()
        for (i in md5Bytes.indices) {
            val value = md5Bytes[i].toInt() and 0xff
            if (value < 16) {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(value))
        }
        return hexValue.toString().toUpperCase()

    }
}