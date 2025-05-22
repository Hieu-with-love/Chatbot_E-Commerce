package android.hcmute.edu.vn.chatbot_spring.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {
    public static Pageable createPageable(Integer page, Integer size, String sort, String direction) {
        if (page < 1) {
            throw new IllegalArgumentException("Page number must not be less than 1");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        Sort.Direction sortDirection = Sort.Direction.fromOptionalString(direction)
                .orElseThrow(() -> new IllegalArgumentException("Invalid direction value: " + direction + ". Use 'asc' or 'desc'."));
        return PageRequest.of(page - 1, size, Sort.by(sortDirection, sort));
    }
}
