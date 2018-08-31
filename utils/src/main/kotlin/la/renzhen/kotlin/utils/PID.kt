package la.renzhen.kotlin.utils

import java.lang.management.ManagementFactory

/**
 * <p>
 * @author <a href="mailto:zhouhaichao@2008.sina.com">haiker</a>
 * @version 31/08/2018 6:06 PM
 */

/**
 * @author [haiker](mailto:zhouhaichao@2008.sina.com)
 * @version 1.0 &amp; 2016/3/23 16:59
 */
object PID {
    fun get(): String {
        return ManagementFactory.getRuntimeMXBean().name.split("@").dropLastWhile { it.isEmpty() }.toTypedArray().get(0)
    }
}
