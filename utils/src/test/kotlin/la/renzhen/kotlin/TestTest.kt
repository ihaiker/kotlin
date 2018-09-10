package la.renzhen.kotlin

import la.renzhen.kotlin.load.IndexIterator
import la.renzhen.kotlin.load.PageIndex
import la.renzhen.kotlin.utils.Nets
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 02/09/2018 9:23 PM
 */

typealias ClickHandler = (Int, Int) -> Unit

class TestTest {
    val log = LoggerFactory.getLogger(TestTest::class.java)

    data class Result(val result: Int, val status: String)

    @Test
    fun testMap() {
        val (result, status) = Result(200, "ok")
        println(result)
        println(status)

        val list = mutableListOf(1)
        list.add(1)
        println(list.joinToString(",", "=", ";", 1, "***") { it.toString() })
    }

    @Test
    fun testHtml() {
        var h = html {
            head {
                title { +"标题" }
            }
            body {
                p { +"内容" }
            }
        }.toString()
        println(h)
    }

    @Test
    fun testIndexIt() {
        val it = IndexIterator<Int, Int>(10) {
            val a = it!!
            if (a < 30)
                mutableListOf(a + 1)
            else
                mutableListOf()
        }
        while (it.hasNext()) {
            val b = it.next()
            println(b)
            it.index = b
        }
    }

    @Test
    fun testPageIt() {
        val it = PageIndex<Int>(7) {
            if (it!! < 10) {
                mutableListOf(it)
            } else {
                mutableListOf()
            }
        }
        while (it.hasNext()) {
            val b = it.next()
            println(b)
        }
    }

    @Test
    fun testNets(){
        var l = Nets.getLocalHost("en6")
        println(l)

        println(Nets.hostname())
    }
}