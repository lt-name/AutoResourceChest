package cn.lanink.autoresourcechest.utils;

import java.text.DecimalFormat;

/**
 * @author lt_name
 */
public class Utils {

    private Utils() {

    }

    public static String formatTime(int time) {
        if (time >= 60) {
            DecimalFormat format = new DecimalFormat("00");
            return format.format(time / 60) + ":" + format.format(time % 60);
        }
        return String.valueOf(time);
    }

}
