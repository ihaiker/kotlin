package la.renzhen.kotlin

import la.renzhen.kotlin.codes.Base64
import la.renzhen.kotlin.codes.MD5
import la.renzhen.kotlin.codes.base64
import la.renzhen.kotlin.crypt.AESCrypt
import la.renzhen.kotlin.crypt.Crypt
import la.renzhen.kotlin.crypt.Des3Crypt
import la.renzhen.kotlin.crypt.RSACrypt
import la.renzhen.kotlin.utils.PID
import org.junit.Test

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 30/08/2018 8:59 PM
 */

class CryptTest {

    @Test
    fun testAES() {
        val crypt = AESCrypt("123456789011ABCDEF")
        show(crypt)
    }

    @Test
    fun testDesc() {
        var crypt = Des3Crypt()
        show(crypt)
    }

    @Test
    fun testRSA() {
        var crypt = RSACrypt()
        show(crypt)
    }

    @Test
    fun testBase64() {
        println(Base64.encode("%s1=12"))
        println(MD5.encode("""Base64.decode("111")"""))
    }

    @Test
    fun testPID(){
        println(PID.get())
    }

    fun show(crypt: Crypt) {
        val b = crypt.encrypt("123123")
        print("密文：")
        println(b)

        val c = crypt.decrypt(b)
        print("原文：")
        println(c)
    }
}