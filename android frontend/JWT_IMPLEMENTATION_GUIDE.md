# JWT Authentication Implementation Guide

## Tóm tắt Tính năng

Đã implement một hệ thống JWT authentication hoàn chỉnh cho Android app với các tính năng sau:

### 1. JWT Token Decoding và Validation
- **JwtUtils.java**: Utility class để decode và validate JWT tokens
- **TokenManager.java**: Quản lý lưu trữ token trong SharedPreferences
- **AuthUtils.java**: High-level authentication utilities

### 2. Các Method chính trong JwtUtils

```java
// Kiểm tra token hết hạn
public static boolean isTokenExpired(String token)

// Validate format JWT
public static boolean isValidTokenFormat(String token)

// Lấy thông tin user từ JWT
public static JSONObject getUserInfoFromToken(String token)

// Lấy các field cụ thể
public static String getUserIdFromToken(String token)
public static String getEmailFromToken(String token)
public static String getFullNameFromToken(String token)
public static String getRoleFromToken(String token)
public static long getExpirationFromToken(String token)

// Test utility (remove in production)
public static String createTestToken(...)
```

### 3. AuthUtils - High-level Authentication

```java
// Kiểm tra authentication với token validation
public static boolean isUserAuthenticated(Context context)

// Lấy thông tin user đầy đủ
public static UserInfo getAuthenticatedUserInfo(Context context)

// Logout user
public static void logout(Context context, boolean showToast)

// Utility methods
public static long getTokenTimeRemaining(Context context)

// UserInfo class với các method helper
public static class UserInfo {
    public boolean isAdmin()
    public boolean isManager()
    public boolean isVip()
    public String getDisplayName()
}
```

### 4. HomeFragment Integration

- **Fast JWT-based Authentication**: Ưu tiên hiển thị thông tin từ JWT token
- **API Fallback**: Nếu JWT không có đủ thông tin, fallback sang API call
- **Dynamic Greeting**: Thay đổi lời chào dựa trên role user
- **Debug Support**: Method debug để log thông tin JWT

#### Key Methods:
```java
private void setupTopBar()                    // Check token và hiển thị UI
private void loadUserProfile()                // Load user info từ JWT hoặc API
private void displayUserInfoFromJWT(...)      // Hiển thị info từ JWT
private void debugJWTTokenInfo(String token)  // Debug JWT info
public void logout()                          // Logout và clear token
public boolean isUserAuthenticated()         // Check auth status
```

### 5. UI Integration

**Layout Elements (fragment_home.xml):**
- `img_user_avatar`: Avatar placeholder
- `tv_greeting`: Lời chào động dựa trên role
- `tv_full_name`: Tên người dùng
- `btn_login`: Nút đăng nhập (ẩn khi authenticated)

**Display Logic:**
- **Authenticated**: Hiện avatar + greeting + name, ẩn nút login
- **Guest**: Hiện "Chào khách", ẩn name, hiện nút login
- **Role-based Greeting**: Admin/Manager/VIP có lời chào riêng

### 6. Test Activity (Development Only)

**JwtTestActivity.java** - Activity để test JWT features:
- Tạo test token với thông tin giả
- Clear token
- Hiển thị chi tiết token info
- Debug authentication status

## Cách sử dụng

### 1. Cơ bản - Check Authentication
```java
if (AuthUtils.isUserAuthenticated(context)) {
    // User authenticated
    AuthUtils.UserInfo userInfo = AuthUtils.getAuthenticatedUserInfo(context);
    String displayName = userInfo.getDisplayName();
} else {
    // Show login
}
```

### 2. Trong Fragment/Activity
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Check authentication on startup
    if (AuthUtils.isUserAuthenticated(this)) {
        // Load authenticated UI
        loadUserContent();
    } else {
        // Redirect to login
        startActivity(new Intent(this, Login.class));
    }
}
```

### 3. Logout
```java
// Simple logout (không navigate)
AuthUtils.logout(context, false);

// Full logout (với toast và navigate to login)
AuthUtils.logout(context, true);
```

### 4. Role-based Logic
```java
AuthUtils.UserInfo userInfo = AuthUtils.getAuthenticatedUserInfo(context);
if (userInfo != null) {
    if (userInfo.isAdmin()) {
        // Show admin features
    } else if (userInfo.isManager()) {
        // Show manager features
    } else if (userInfo.isVip()) {
        // Show VIP features
    }
}
```

## JWT Token Format Expected

JWT token payload should contain:
```json
{
  "userId": "123",          // or "sub", "id"
  "fullName": "John Doe",   // or "name", "displayName", "username"
  "email": "john@example.com",
  "role": "ADMIN",          // or "authorities", "scope"
  "exp": 1640995200,        // expiration timestamp
  "iat": 1640908800         // issued at timestamp
}
```

## Production Notes

1. **Remove Test Methods**: Remove `createTestToken()` và `JwtTestActivity`
2. **Security**: Implement proper JWT signature validation
3. **Token Refresh**: Add token refresh mechanism
4. **Error Handling**: Enhanced error handling cho network issues
5. **Performance**: Consider caching user info to reduce JWT decoding

## File Structure

```
utils/
├── JwtUtils.java          # JWT decoding & validation
├── TokenManager.java      # Token storage management
└── AuthUtils.java         # High-level auth utilities

fragments/
└── HomeFragment.java      # JWT integration example

activity/
└── JwtTestActivity.java   # Test activity (remove in prod)

res/layout/
├── fragment_home.xml      # Home fragment layout
└── activity_jwt_test.xml  # Test activity layout (remove in prod)
```

## Debug Information

Để debug JWT token, sử dụng method `debugJWTTokenInfo()` trong HomeFragment hoặc chạy JwtTestActivity để xem chi tiết token information.

---

**Lưu ý**: Đây là implementation demo, cần review security và performance trước khi deploy production.
