package cn.lanink.autoresourcechest.utils

import java.text.DecimalFormat

/**
 * @author lt_name
 */
class Utils {

    companion object {

        @JvmStatic
        fun formatTime(time: Int): String {
            if (time >= 60) {
                val format = DecimalFormat("00")
                return format.format((time / 60).toLong()) + ":" + format.format((time % 60).toLong())
            }
            return time.toString()
        }

    }

}