package android.hcmute.edu.vn.chatbot_spring.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e, WebRequest request) {
        log.warn("Xung đột dữ liệu: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "Xung đột dữ liệu", e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request) {
        String errorMessage = String.format("Giá trị '%s' không hợp lệ cho tham số '%s'. Định dạng mong đợi: %s",
                e.getValue(), e.getName(), LocalDate.class.equals(e.getRequiredType()) ? "yyyy-MM-dd (ví dụ: 2025-03-30)" : e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "không xác định");
        log.warn("Lỗi tham số: {}", errorMessage);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Tham số không hợp lệ", errorMessage, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e, WebRequest request) {
        String errorMessage;
        if (e.getCause() != null && e.getCause().getMessage().contains("LocalDate")) {
            errorMessage = "Định dạng ngày không hợp lệ. Định dạng mong đợi: yyyy-MM-dd (ví dụ: 2025-03-30)";
        } else {
            errorMessage = e.getMostSpecificCause().getMessage();
        }
        log.warn("Lỗi phân tích JSON: {}", errorMessage);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ", errorMessage, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        log.warn("Tham số không hợp lệ: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Tham số không hợp lệ", e.getMessage(), request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        String errorMessage;
        String error;

        if (e instanceof MethodArgumentNotValidException ex) {
            error = "Dữ liệu đầu vào không hợp lệ";
            errorMessage = ex.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else {
            error = "Biến đường dẫn không hợp lệ";
            errorMessage = e.getMessage().substring(e.getMessage().indexOf(" ") + 1);
        }

        log.warn("Lỗi xác thực: {}", errorMessage);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, error, errorMessage, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        log.warn("Không tìm thấy tài nguyên: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Không tìm thấy tài nguyên", e.getMessage(), request);
    }

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedException(DuplicatedException e, WebRequest request) {
        log.warn("Dữ liệu trùng lặp: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "Dữ liệu trùng lặp", e.getMessage(), request);
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDataException(InvalidDataException e, WebRequest request) {
        log.warn("Dữ liệu không hợp lệ: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", e.getMessage(), request);
    }

    @ExceptionHandler({ForBiddenException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        log.warn("Truy cập bị từ chối: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Truy cập bị từ chối", e.getMessage(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAuthenticationException(AuthenticationException e, WebRequest request) {
        log.warn("Xác thực thất bại: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Xác thực thất bại", "Xác thực thất bại", request);
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataAccessException(DataAccessException e, WebRequest request) {
        log.error("Lỗi truy cập dữ liệu: {}", e.getMessage(), e);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Lỗi truy cập dữ liệu", e.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e, WebRequest request) {
        log.error("Lỗi toàn vẹn dữ liệu: {}", e.getCause().getMessage(), e);
        return buildErrorResponse(HttpStatus.CONFLICT, "Lỗi toàn vẹn dữ liệu", e.getCause().getMessage(), request);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSQLException(SQLException e, WebRequest request) {
        String detailedMessage = String.format("Lỗi cơ sở dữ liệu: %s (Trạng thái SQL: %s, Mã lỗi: %d)",
                e.getMessage(), e.getSQLState(), e.getErrorCode());
        log.error("Lỗi SQL: {}", detailedMessage, e);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Lỗi truy vấn cơ sở dữ liệu", detailedMessage, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(Exception e, WebRequest request) {
        log.error("Lỗi không xác định: {}", e.getMessage(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi không xác định", e.getMessage(), request);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message, WebRequest request) {
        return ErrorResponse.builder()
                .timestamp(new Date())
                .status(status.value())
                .path(request.getDescription(false).replace("uri=", ""))
                .error(error)
                .message(message)
                .build();
    }
}