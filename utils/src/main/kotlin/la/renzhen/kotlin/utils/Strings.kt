package la.renzhen.kotlin.utils

import com.google.common.base.Ascii
import com.google.common.base.CaseFormat


fun String.firstCharToUpper(): String {
    return if (this.isEmpty()) this else StringBuilder(this.length).append(Ascii.toUpperCase(this[0])).append(this.substring(1)).toString()
}

fun String.firstCharToLower(): String {
    val word = this
    return if (word.isEmpty()) word else StringBuilder(word.length).append(Ascii.toLowerCase(word[0])).append(word.substring(1)).toString()
}

/**
 * 驼峰命名变成下划线命名
 *
 * @return 下划线名称
 */
fun String.underScore(): String {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this)
}

/**
 * 返驼峰命名
 *
 * @return 驼峰命名法
 */
fun String.unUnderScore(): String? {
    return CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).reverse().convert(this)
}

/**
 * 安全截取操作
 * @param start 截取开始字段
 * @param end   截取结束字段
 * @return 截取的字符串
 */
fun String.sub(start: Int, end: Int = 0): String {
    if ("".equals(this.trim())) {
        return ""
    }
    val length = this.length
    if (start > length - 1) {
        return ""
    }
    var endIdx = end
    if (endIdx == 0 || endIdx > length) {
        endIdx = length - 1
    } else if (endIdx < 0) {
        endIdx = length + end
        if (endIdx < 0) {
            return ""
        }
    }
    return if (start >= endIdx) "" else this.substring(start, endIdx)
}

fun String?.or(v: String): String {
    return this.trimToNull() ?: v
}

fun String?.trimToNull(): String? {
    val v = this?.trim()
    if (v == null || "".equals(v)) {
        return null
    }
    return v
}

fun String?.trimToEmpty(): String {
    return this.trimToNull() ?: ""
}

