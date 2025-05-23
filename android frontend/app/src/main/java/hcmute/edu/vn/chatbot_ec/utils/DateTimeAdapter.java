package hcmute.edu.vn.chatbot_ec.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Gson TypeAdapter for Java 8's LocalDateTime type.
 * This adapter handles serialization and deserialization of LocalDateTime objects
 * when working with Retrofit/Gson and JSON data from the backend.
 */
public class DateTimeAdapter extends TypeAdapter<LocalDateTime> {
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }
    
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        
        String dateStr = in.nextString();
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            // If standard ISO format fails, try with more flexible parsing
            try {
                // Handle potential format from Spring backend with timezone
                if (dateStr.contains("T") && dateStr.contains("Z")) {
                    dateStr = dateStr.replace("Z", "");
                    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                
                // Try a more lenient parser for different date formats
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException ex) {
                throw new IOException("Failed to parse LocalDateTime: " + dateStr, ex);
            }
        }
    }
}
