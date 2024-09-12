package io.github.cakioe
import java.util.Calendar
import java.util.Date

/**
 * this is a class for carbon
 * input timestamp and return year, month, day, hour, minute
 *
 * @author cleveng
 * @since 1.0.3
 */
class Carbon(private val timestamp: Long) {
    fun toIntArray() : IntArray {
        val date = Date(this.timestamp)
        val calendar = Calendar.getInstance()
        calendar.time = date

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // 月份从0开始，所以需要加1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY) // 24小时制
        val minute = calendar.get(Calendar.MINUTE)

        return intArrayOf(year, month, day, hour, minute)
    }
}