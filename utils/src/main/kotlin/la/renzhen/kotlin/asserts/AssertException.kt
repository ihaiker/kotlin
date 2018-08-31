package la.renzhen.kotlin.asserts

/**
 * 断言异常 <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 25/08/2018 8:09 PM
 */
class AssertException(
        val status: Int = 500,
        val error: String = "InternalError",
        override val message: String = "We encountered an internal error. Please try again."
) : RuntimeException(message) {

    override fun toString(): String {
        return "status: $status, error: $error, message: $message"
    }
}