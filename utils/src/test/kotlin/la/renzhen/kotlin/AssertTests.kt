package la.renzhen.kotlin

import la.renzhen.kotlin.asserts.AssertException
import la.renzhen.kotlin.asserts.Asserts
import org.junit.Test

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 25/08/2018 8:12 PM
 */
class AssertTests {

    @Test
    fun testAssertException() {
        val ex = AssertException(404, "2001", "用户为找到")
        println(ex)
    }

    @Test
    fun testAssert() {
        Asserts.badRequest(true, "");
        Asserts.badRequest(true, "", "错误了")
        Asserts.badRequest(true, Asserts.Error("2001", "不可以访问"))
    }
}
