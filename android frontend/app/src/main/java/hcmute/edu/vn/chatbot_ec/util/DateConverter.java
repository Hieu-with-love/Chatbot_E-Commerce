package hcmute.edu.vn.chatbot_ec.util;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Type converters for Room to convert between Date objects and Long timestamps
 */
public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
