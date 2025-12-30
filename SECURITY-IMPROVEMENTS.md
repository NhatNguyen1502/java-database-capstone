# 🔐 Cải Thiện Authentication & Authorization

## 📋 Tổng quan

Đã cải thiện hệ thống bảo mật bằng cách:
1. ✅ Tạo JWT Authentication Filter tự động validate token
2. ✅ Bảo vệ dashboard endpoints với role-based access control
3. ✅ Sử dụng SecurityContextHolder thay vì manual token validation
4. ✅ Global exception handler cho authentication/authorization errors

---

## 🆕 Files mới được tạo

### 1. `JwtAuthentication.java`
**Location:** `app/src/main/java/com/project/back_end/security/JwtAuthentication.java`

**Mục đích:** Custom Authentication object để lưu thông tin user và role sau khi validate JWT token.

```java
public class JwtAuthentication implements Authentication {
    private final String email;
    private final String role;
    // ...
}
```

**Tính năng:**
- Implement Spring Security's `Authentication` interface
- Lưu email và role của user
- Tự động thêm prefix `ROLE_` cho role (Spring Security convention)

---

### 2. `JwtAuthenticationFilter.java`
**Location:** `app/src/main/java/com/project/back_end/security/JwtAuthenticationFilter.java`

**Mục đích:** Filter tự động validate JWT token cho MỌI request.

**Hoạt động:**
1. Lấy token từ `Authorization: Bearer <token>` header
2. Xác định role dựa trên request path (`/admin/*`, `/doctor/*`, `/patient/*`)
3. Validate token với AuthenticationService
4. Nếu valid → tạo JwtAuthentication và set vào SecurityContext
5. Nếu invalid → request tiếp tục nhưng không có authentication

**Skip filter cho:**
- Public endpoints: `/`, `/index.html`
- Static resources: `/css/*`, `/js/*`, `/assets/*`
- Login endpoints: `*/login`
- Actuator endpoints: `/actuator/*`

---

### 3. `GlobalExceptionHandler.java`
**Location:** `app/src/main/java/com/project/back_end/exception/GlobalExceptionHandler.java`

**Mục đích:** Xử lý tất cả authentication/authorization exceptions một cách thống nhất.

**Xử lý:**
- `AuthenticationException` → 401 Unauthorized
- `AccessDeniedException` → 403 Forbidden
- `Exception` → 500 Internal Server Error

---

## 🔄 Files được cập nhật

### 1. `SecurityConfig.java`
**Thay đổi:**

#### ❌ Trước (Insecure):
```java
.requestMatchers("/admin/dashboard", "/doctor/dashboard").permitAll()
```
→ **Ai cũng có thể truy cập dashboard!**

#### ✅ Sau (Secure):
```java
.requestMatchers("/admin/dashboard", "/admin/api/**").hasRole("ADMIN")
.requestMatchers("/doctor/dashboard", "/doctor/api/**").hasRole("DOCTOR")
.requestMatchers("/patient/dashboard", "/patient/api/**").hasRole("PATIENT")
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```
→ **Chỉ users với role phù hợp mới truy cập được!**

---

### 2. `AdminController.java`
**Thay đổi:** Loại bỏ manual token validation trong TỪNG method.

#### ❌ Trước (Redundant):
```java
@GetMapping("/api/dashboard")
public ResponseEntity<?> getDashboardStatistics(
        @RequestHeader("Authorization") String token) {
    
    // Manual validation trong MỖI method
    TokenValidationResponse validation = authenticationService.validateToken(token, "admin");
    if (!validation.isValid()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
    }
    // ... business logic
}
```

#### ✅ Sau (Clean):
```java
@GetMapping("/api/dashboard")
public ResponseEntity<?> getDashboardStatistics() {
    // Filter đã validate token tự động!
    // Chỉ cần lấy thông tin user từ SecurityContext nếu cần
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    logger.info("Admin {} accessing dashboard", auth.getName());
    // ... business logic
}
```

**Lợi ích:**
- Code ngắn gọn hơn
- Không lặp lại validation logic
- Dễ maintain và test

---

## 🔄 Workflow mới

### 1. **User Login**
```
User → POST /admin/login
      ↓ (username + password)
Backend validates credentials
      ↓ (success)
Return JWT token
```

### 2. **Access Dashboard (NEW!)**
```
User → GET /admin/dashboard (with token in header)
      ↓
JwtAuthenticationFilter intercepts
      ↓
Validate token + role
      ↓ (valid + role=ADMIN)
SecurityConfig checks .hasRole("ADMIN")
      ↓ (pass)
Return adminDashboard.html
```

**Nếu token invalid hoặc role không match → 403 Forbidden**

### 3. **API Call**
```
Frontend → GET /admin/api/dashboard (with token in header)
         ↓
JwtAuthenticationFilter validates token
         ↓ (valid)
Set authentication in SecurityContext
         ↓
Controller method executes
         ↓
Return data
```

---

## 🔒 Security Improvements

### Trước (❌ Điểm yếu):
1. **Dashboard không bảo vệ:** Ai cũng truy cập được HTML
2. **Manual validation:** Dễ quên check trong một số methods
3. **Code duplication:** Lặp lại logic validate trong mỗi method
4. **Client-side only:** Frontend check token, dễ bypass

### Sau (✅ Cải thiện):
1. **Server-side protection:** Spring Security bảo vệ dashboard
2. **Automatic validation:** Filter tự động check MỌI request
3. **DRY principle:** Logic validation ở một chỗ duy nhất
4. **Role-based access:** `.hasRole("ADMIN")` enforce server-side
5. **Global error handling:** Consistent error responses

---

## 📊 So sánh

| Aspect | Trước | Sau |
|--------|-------|-----|
| Dashboard protection | ❌ permitAll() | ✅ .hasRole("ADMIN") |
| Token validation | ❌ Manual trong mỗi method | ✅ Automatic filter |
| Code duplication | ❌ Nhiều | ✅ Không có |
| Security | ❌ Client-side only | ✅ Server-side enforced |
| Maintainability | ❌ Khó | ✅ Dễ |
| Test coverage | ❌ Phải test mỗi method | ✅ Test filter một lần |

---

## 🚀 Testing

### 1. Test Dashboard Protection
```bash
# Không có token → 403 Forbidden
curl http://localhost:8080/admin/dashboard

# Token không hợp lệ → 403 Forbidden
curl -H "Authorization: Bearer invalid_token" http://localhost:8080/admin/dashboard

# Token hợp lệ + role=ADMIN → 200 OK
curl -H "Authorization: Bearer <valid_admin_token>" http://localhost:8080/admin/dashboard
```

### 2. Test API Protection
```bash
# Không có token → 403 Forbidden
curl http://localhost:8080/admin/api/dashboard

# Token hợp lệ + role=ADMIN → 200 OK + data
curl -H "Authorization: Bearer <valid_admin_token>" http://localhost:8080/admin/api/dashboard

# Token hợp lệ nhưng role=DOCTOR → 403 Forbidden
curl -H "Authorization: Bearer <valid_doctor_token>" http://localhost:8080/admin/api/dashboard
```

---

## 🎯 Best Practices được áp dụng

1. ✅ **Single Responsibility:** Mỗi class có một nhiệm vụ rõ ràng
2. ✅ **DRY (Don't Repeat Yourself):** Validation logic ở một chỗ
3. ✅ **Security by Default:** Mặc định là secure, phải explicit để public
4. ✅ **Fail Secure:** Nếu validation fail → deny access
5. ✅ **Centralized Error Handling:** Global exception handler
6. ✅ **Stateless Authentication:** JWT không cần session
7. ✅ **Role-Based Access Control (RBAC):** hasRole() enforcement

---

## 🔮 Next Steps (Optional)

1. **Refresh Token:** Implement refresh token cho long-term sessions
2. **Rate Limiting:** Thêm rate limiting để chống brute force
3. **Audit Logging:** Log tất cả authentication/authorization events
4. **Token Blacklist:** Implement token revocation
5. **CORS Configuration:** Configure CORS cho production
6. **HTTPS Only:** Force HTTPS trong production

---

## 📝 Notes

- **Backward Compatibility:** Frontend code KHÔNG CẦN thay đổi! Vẫn gửi token trong Authorization header như cũ.
- **Performance:** Filter rất nhanh, không ảnh hưởng performance đáng kể.
- **Testing:** Dễ test hơn vì logic tập trung ở filter.
- **Scalability:** Dễ dàng thêm role hoặc permission mới.

---

**Author:** GitHub Copilot  
**Date:** December 28, 2025  
**Version:** 1.0
