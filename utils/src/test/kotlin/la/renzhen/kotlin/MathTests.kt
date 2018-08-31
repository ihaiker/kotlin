package la.renzhen.kotlin

import la.renzhen.kotlin.utils.bd
import la.renzhen.kotlin.utils.scaled
import org.junit.Test

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 31/08/2018 8:08 PM
 */

class MathTests {

    @Test
    fun add() {
//        println(1.0 - 0.9)
//        println(1.0.minus(0.9))
//        println(BigDecimal(1.0.toString()).minus(BigDecimal(0.9.toString())).toDouble())

        println(((1.0.bd() - 0.9.bd()) * (1.3.bd())).scaled().toDouble())
    }
}