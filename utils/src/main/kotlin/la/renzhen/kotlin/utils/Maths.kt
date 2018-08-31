package la.renzhen.kotlin.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


fun Double.bd(): BigDecimal {
    return this.toBigDecimal()
}

/**
 * @param scale        保留小数位数
 * @param roundingMode 小数位round模式
 * @return out
 */
fun Double.scaled(scale: Int = 2, roundingMode: RoundingMode = RoundingMode.DOWN): Double {
    return BigDecimal(this).setScale(scale, roundingMode).toDouble()
}

/**
 * @param scale        保留小数位数
 * @param roundingMode 小数位round模式
 * @return out
 */
fun BigDecimal.scaled(scale: Int = 2, roundingMode: RoundingMode = RoundingMode.DOWN): BigDecimal {
    return this.setScale(scale, roundingMode)
}

/**
 * 格式化输出，千分位不带逗号
 *
 * @param digits       保留小数位数
 * @param roundingMode 小数位数保留方式
 * @return 格式化结果
 */
fun Double.format(digits: Int, roundingMode: RoundingMode = RoundingMode.DOWN): String {
    val f = DecimalFormat("##0.0000000000")
    f.maximumFractionDigits = digits
    f.roundingMode = roundingMode
    return f.format(this)
}

/**
 * 格式刷输出
 *
 * @param digits       保留小数位数
 * @param roundingMode 小数位数保留方式
 * @return 优化格式结果
 */
fun Double.pretty(digits: Int = 2, roundingMode: RoundingMode = RoundingMode.DOWN): String {
    val f = DecimalFormat("###,###,###,###,##0.0000000000")
    f.maximumFractionDigits = digits
    f.roundingMode = roundingMode
    return f.format(this)
}


/**
 * 格式化输出，千分位不带逗号
 *
 * @param digits       保留小数位数
 * @param roundingMode 小数位数保留方式
 * @return 格式化结果
 */
fun BigDecimal.format(digits: Int = 2, roundingMode: RoundingMode = RoundingMode.DOWN): String {
    return this.toDouble().format(digits, roundingMode)
}

/**
 * 格式刷输出
 *
 * @param digits       保留小数位数
 * @param roundingMode 小数位数保留方式
 * @return 优化格式结果
 */
fun BigDecimal.pretty(digits: Int = 2, roundingMode: RoundingMode = RoundingMode.DOWN): String {
    return this.toDouble().pretty(digits, roundingMode)
}