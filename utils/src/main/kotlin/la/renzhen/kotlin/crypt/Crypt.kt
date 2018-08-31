package la.renzhen.kotlin.crypt;

/**
 * 机密解密算法汇总
 *
 * @author zhouhaichao(a)2008.sina.com
 * @version 1.0 &amp; 22:52　2016/1/30
 */
interface Crypt {

    /**
     * 加密
     *
     * @param data 明文数据
     * @return 密文
     */
    fun encrypt(data: String): String

    /**
     * 加密
     *
     * @param data 明文数据
     * @return 秘文
     */
    fun encrypt(data: ByteArray): ByteArray


    /**
     * 解密
     *
     * @param data 秘文
     * @return 明文
     */
    fun decrypt(data: String): String

    /**
     * 解密
     *
     * @param data 秘文数据
     * @return 明文
     */
    fun decrypt(data: ByteArray): ByteArray
}
