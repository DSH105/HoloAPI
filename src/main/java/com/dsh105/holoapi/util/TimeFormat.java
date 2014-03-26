/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * https://github.com/essentials/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/utils/DescParseTickFormat.java
 */

public class TimeFormat {

    public static final int ticksAtMidnight = 18000;
    public static final int ticksPerDay = 24000;
    public static final int ticksPerHour = 1000;
    public static final double ticksPerMinute = 1000d / 60d;
    public static final double ticksPerSecond = 1000d / 60d / 60d;
    private static final SimpleDateFormat SDFTwentyFour = new SimpleDateFormat("HH", Locale.ENGLISH);
    private static final SimpleDateFormat SDFTwelve = new SimpleDateFormat("h aa", Locale.ENGLISH);

    static {
        SDFTwelve.setTimeZone(TimeZone.getTimeZone("GMT"));
        SDFTwentyFour.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String format12(final long ticks) {
        return formatDate(ticks, SDFTwelve);
    }

    public static String format24(final long ticks) {
        return formatDate(ticks, SDFTwentyFour);
    }

    public static String formatDate(final long ticks, final SimpleDateFormat format) {
        return format.format(ticksToDate(ticks));
    }


    public static Date ticksToDate(long ticks) {
        // Assume the server time starts at 0. It would start on a day.
        // But we will simulate that the server started with 0 at midnight.
        ticks = ticks - ticksAtMidnight + ticksPerDay;

        // How many ingame days have passed since the server start?
        final long days = ticks / ticksPerDay;
        ticks -= days * ticksPerDay;

        // How many hours on the last day?
        final long hours = ticks / ticksPerHour;
        ticks -= hours * ticksPerHour;

        // How many minutes on the last day?
        final long minutes = (long) Math.floor(ticks / ticksPerMinute);
        final double dticks = ticks - minutes * ticksPerMinute;

        // How many seconds on the last day?
        final long seconds = (long) Math.floor(dticks / ticksPerSecond);

        // Now we create an english GMT calendar (We want no daylight savings)
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
        cal.setLenient(true);

        // And we set the time to 0! And append the time that passed!
        cal.set(0, Calendar.JANUARY, 1, 0, 0, 0);
        cal.add(Calendar.DAY_OF_YEAR, (int) days);
        cal.add(Calendar.HOUR_OF_DAY, (int) hours);
        cal.add(Calendar.MINUTE, (int) minutes);
        cal.add(Calendar.SECOND, (int) seconds + 1); // To solve rounding errors.

        return cal.getTime();
    }
}