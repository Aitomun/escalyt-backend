package com.decadev.escalayt.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateUtil {


    public static String toTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (days < 7) {
            return days + " days ago";
        } else if (weeks < 4) {
            return weeks + " weeks ago";
        } else if (months < 12) {
            return months + " months ago";
        } else {
            return years + " years ago";
        }
    }


}
