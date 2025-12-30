# Test Spring MVC Migration

## Cách Test Migration

### 1. Start Application

```bash
cd app
mvn spring-boot:run
```

Hoặc:
```bash
./mvnw spring-boot:run   # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

### 2. Access MVC Login Pages

#### Doctor Login (MVC Form)
```
http://localhost:8080/doctor/login
```

**Test Credentials:** (dựa vào sample data của bạn)
- Username: `doctor@example.com` hoặc username
- Password: password trong database

#### Patient Login (MVC Form)
```
http://localhost:8080/patient/login
```

#### Admin Login (MVC Form)
```
http://localhost:8080/admin/login
```

#### Patient Registration (MVC Form)
```
http://localhost:8080/patient/register
```

### 3. Test Validation

#### Empty Form Submission
1. Vào login page
2. Submit form trống
3. ✅ Kiểm tra: Error messages hiển thị ngay bên dưới fields

#### Invalid Credentials
1. Nhập sai username/password
2. Submit
3. ✅ Kiểm tra: Error message hiển thị: "Invalid password" hoặc "User not found"

#### Successful Login
1. Nhập đúng credentials
2. Submit
3. ✅ Kiểm tra: 
   - Redirect đến dashboard
   - Session được tạo
   - Flash message "Login successful!" hiển thị

### 4. Test Dashboards

Sau khi login thành công:

#### Admin Dashboard
```
http://localhost:8080/admin/dashboard
```
✅ Kiểm tra:
- Statistics được load từ server
- userName hiển thị trong page

#### Doctor Dashboard
```
http://localhost:8080/doctor/dashboard
```
✅ Kiểm tra:
- Doctor-specific statistics
- Appointments data

#### Patient Dashboard
```
http://localhost:8080/patient/dashboard
```

### 5. Test Authorization

#### Try to access dashboard without login:
```
http://localhost:8080/doctor/dashboard
```
✅ Kiểm tra: Redirect về `/doctor/login`

#### Test Logout
```
http://localhost:8080/logout
```
✅ Kiểm tra:
- Session bị invalidate
- Redirect về homepage
- Flash message "Logged out successfully!"

### 6. Test Patient Registration

```
http://localhost:8080/patient/register
```

Fill form với:
- First Name: John
- Last Name: Doe
- Email: john.doe@example.com
- Password: password123
- Phone: 0123456789
- Date of Birth: 1990-01-01
- Gender: Male
- Address: 123 Test St

✅ Kiểm tra:
- Validation cho required fields
- Email format validation
- Successful registration → auto login → redirect to dashboard

### 7. Test REST API (vẫn hoạt động)

REST API endpoints vẫn work như cũ:

#### Doctor Login via REST API
```bash
curl -X POST http://localhost:8080/api/doctor/login \
  -H "Content-Type: application/json" \
  -d '{"username":"doctor@example.com","password":"password"}'
```

Response:
```json
{
  "success": true,
  "token": "eyJhbGc...",
  "message": "Login successful",
  "user": {...}
}
```

#### Get Doctor Appointments (REST API with JWT)
```bash
curl http://localhost:8080/api/doctor/appointments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 8. Browser Developer Tools Testing

#### Check Session Cookie
1. Login via MVC form
2. Open DevTools → Application → Cookies
3. ✅ Kiểm tra: `JSESSIONID` cookie được set

#### Check Network Requests
1. Submit login form
2. Open DevTools → Network
3. ✅ Kiểm tra:
   - POST `/doctor/login` → Status 302 (Redirect)
   - GET `/doctor/dashboard` → Status 200

#### Check Form Validation
1. Try to submit empty form
2. ✅ Kiểm tra: Browser validation hoặc server-side errors

---

## Expected Behaviors

### ✅ MVC Form Flow

```
User → GET /doctor/login
     → Shows HTML form (Thymeleaf template)
     → User fills form
     → POST /doctor/login
     → Server validates
     → If valid: Create session + redirect to dashboard
     → If invalid: Return form with errors
```

### ✅ Session Management

```
POST /login → Create HttpSession
           → Store: token, userRole, userId, userName
           → Set JSESSIONID cookie

GET /dashboard → Check session
              → If exists: Load data + render
              → If not: Redirect to login

GET /logout → Invalidate session
           → Clear cookie
           → Redirect to home
```

### ✅ REST API Flow (unchanged)

```
POST /api/doctor/login → Return JWT token
GET /api/doctor/appointments → Validate JWT
                              → Return JSON
```

---

## Troubleshooting

### Issue: "Whitelabel Error Page" hoặc 404

**Cause:** Thymeleaf templates không tìm thấy

**Solution:**
```bash
# Check templates location
ls -la app/src/main/resources/templates/auth/
```

Templates phải ở đúng vị trí:
```
templates/
├── auth/
│   ├── doctorLogin.html
│   ├── patientLogin.html
│   ├── adminLogin.html
│   └── patientRegister.html
```

### Issue: CSS không load

**Cause:** Static resources không được serve

**Solution:**
1. Check SecurityConfig cho phép `/assets/**`
2. CSS files phải ở: `src/main/resources/static/assets/css/auth.css`

### Issue: "Invalid token" khi dùng REST API

**Cause:** REST API và MVC authentication khác nhau

**Solution:**
- MVC: Dùng session-based auth (JSESSIONID cookie)
- REST API: Dùng JWT token trong Authorization header

Don't mix them!

### Issue: Validation errors không hiển thị

**Cause:** Thymeleaf binding chưa đúng

**Solution:**
```html
<!-- Correct way -->
<span th:if="${#fields.hasErrors('username')}" 
      th:errors="*{username}" 
      class="error-message"></span>
```

---

## Manual Testing Checklist

- [ ] Doctor login page loads with proper styling
- [ ] Patient login page loads
- [ ] Admin login page loads
- [ ] Patient registration form loads
- [ ] Empty form shows validation errors
- [ ] Invalid credentials show error message
- [ ] Valid login redirects to dashboard
- [ ] Dashboard loads with user data
- [ ] Logout clears session and redirects
- [ ] Cannot access dashboard without login
- [ ] REST API still works with JWT tokens
- [ ] CSS styling looks good on mobile
- [ ] Flash messages appear and disappear
- [ ] Session persists across page refreshes
- [ ] Multiple users can login simultaneously

---

## Next Steps After Testing

1. ✅ Migrate more forms to MVC:
   - Appointment booking
   - Profile update
   - Password change

2. ✅ Add more validation:
   - Custom validators
   - Business rule validation
   - Cross-field validation

3. ✅ Improve error handling:
   - Custom error pages
   - @ControllerAdvice for global handling
   - User-friendly error messages

4. ✅ Add security features:
   - CSRF protection
   - Remember me
   - Account lockout after failed attempts

5. ✅ Add client-side enhancements:
   - Form validation feedback
   - Password strength meter
   - Auto-complete

Happy Testing! 🚀
