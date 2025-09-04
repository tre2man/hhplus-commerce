package kr.hhplus.be.server.utils;

import java.time.LocalDateTime;

public class StringUtils {
    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public static String getYYYYMMDD() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    public static String getKafkaId() {
        return getYYYYMMDD() + "-" + getRandomString(8);
    }
}
