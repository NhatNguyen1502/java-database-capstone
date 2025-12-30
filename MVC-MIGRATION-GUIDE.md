# Spring MVC Migration Guide

## Tổng Quan

Project đã được migrate để sử dụng **Spring MVC nhiều hơn** thay vì REST API cho các tương tác với forms và page navigation. Điều này giúp:

✅ Học được nhiều hơn về Spring MVC  
✅ Server-side form validation với error handling  
✅ Session management an toàn  
✅ SEO-friendly pages  
✅ Giảm JavaScript complexity cho basic operations  

---

## Kiến Trúc Mới

### MVC Controllers (`com.project.back_end.mvc`)

#### 1. **AuthController** - Xử lý Authentication
- **GET/POST** `/admin/login` - Admin login form
- **GET/POST** `/doctor/login` - Doctor login form  
- **GET/POST** `/patient/login` - Patient login form
- **GET** `/logout` - Logout và xóa session

#### 2. **PatientRegistrationController** - Đăng ký Patient
- **GET/POST** `/patient/register` - Patient registration form

#### 3. **DashboardController** - Render Dashboards
- **GET** `/admin/dashboard` - Load admin statistics
- **GET** `/doctor/dashboard` - Load doctor statistics  
- **GET** `/patient/dashboard` - Patient dashboard

### REST Controllers (giữ lại cho API calls)

Các REST controllers trong `com.project.back_end.controllers` vẫn được giữ lại cho:
- AJAX calls từ JavaScript
- Mobile apps / external clients
- API integrations

---

## Khi Nào Dùng MVC vs REST API?

### ✅ Dùng Spring MVC cho:

1. **Form Submissions**
   - Login forms
   - Registration forms
   - Profile update forms
   - Appointment booking forms

2. **Page Navigation**
   - Dashboard pages
   - View pages with server-side rendering

3. **Server-side Validation**
   - Form field validation
   - Business rule validation
   - Error message display

**Ví dụ:**
```java
@Controller
@RequiredArgsConstructor
public class AppointmentController {
    
    @GetMapping("/patient/appointments/book")
    public String showBookingForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        return "patient/bookAppointment";
    }
    
    @PostMapping("/patient/appointments/book")
    public String bookAppointment(
            @Valid @ModelAttribute("appointment") Appointment appointment,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "patient/bookAppointment";
        }
        
        // Save appointment
        appointmentService.save(appointment);
        
        redirectAttributes.addFlashAttribute("success", "Appointment booked!");
        return "redirect:/patient/appointments";
    }
}
```

### ✅ Dùng REST API cho:

1. **AJAX Calls**
   - Dynamic data loading without page refresh
   - Real-time updates
   - Auto-complete features

2. **Mobile Apps**
   - iOS/Android apps
   - Native mobile applications

3. **Single Page Applications (SPAs)**
   - React/Vue/Angular frontends

4. **Third-party Integrations**
   - External systems
   - Microservices communication

**Ví dụ:**
```java
@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentApiController {
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Appointment>> getUpcomingAppointments(
            @RequestParam Long patientId) {
        return ResponseEntity.ok(appointmentService.getUpcoming(patientId));
    }
}
```

---

## Flow Authentication

### MVC Flow (Form-based)

```
User → GET /doctor/login 
     → AuthController.showDoctorLoginForm() 
     → Return "auth/doctorLogin.html" (Thymeleaf)
     
User fills form & submits
     → POST /doctor/login
     → AuthController.doctorLogin()
     → Validate credentials
     → Store in HttpSession
     → Redirect to /doctor/dashboard
```

### REST API Flow (Token-based)

```
Client → POST /api/doctor/login (JSON)
       → DoctorController.doctorLogin()
       → Return JWT token
       
Client → GET /api/doctor/appointments
       → Send "Authorization: Bearer {token}"
       → JwtAuthenticationFilter validates token
       → Return JSON data
```

---

## Session Management

### MVC Controllers sử dụng HttpSession

```java
// Lưu thông tin vào session sau khi login
session.setAttribute("token", response.getToken());
session.setAttribute("userRole", "doctor");
session.setAttribute("userId", response.getUser().getId());
session.setAttribute("userName", response.getUser().getFullName());

// Check authentication trong dashboard
if (session.getAttribute("token") == null) {
    return "redirect:/doctor/login";
}

// Logout
session.invalidate();
```

### REST API sử dụng JWT Token

```java
// Client gửi token trong header
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

// JwtAuthenticationFilter tự động validate
```

---

## Form Validation với Spring MVC

### Server-side Validation

**1. Thêm validation annotations vào Model/DTO:**

```java
public class Login {
    @NotBlank(message = "Username or email is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}
```

**2. Sử dụng @Valid trong Controller:**

```java
@PostMapping("/login")
public String login(
        @Valid @ModelAttribute("login") Login login,
        BindingResult bindingResult,
        Model model) {
    
    // Check validation errors
    if (bindingResult.hasErrors()) {
        return "auth/login"; // Return to form with errors
    }
    
    // Process login...
}
```

**3. Hiển thị errors trong Thymeleaf:**

```html
<input type="text" th:field="*{username}" class="input-field" required>
<span th:if="${#fields.hasErrors('username')}" 
      th:errors="*{username}" 
      class="error-message"></span>
```

---

## Thymeleaf Templates

### Template Location
```
src/main/resources/templates/
├── auth/
│   ├── doctorLogin.html
│   ├── patientLogin.html
│   ├── adminLogin.html
│   └── patientRegister.html
├── admin/
│   └── adminDashboard.html
└── doctor/
    └── doctorDashboard.html
```

### Sử dụng Thymeleaf

**1. Form Binding:**
```html
<form th:action="@{/patient/login}" th:object="${login}" method="post">
    <input type="text" th:field="*{username}" />
    <input type="password" th:field="*{password}" />
    <button type="submit">Login</button>
</form>
```

**2. Hiển thị dữ liệu:**
```html
<h1>Welcome, <span th:text="${userName}"></span>!</h1>

<div th:if="${statistics}">
    <p>Total Patients: <span th:text="${statistics.totalPatients}"></span></p>
</div>
```

**3. Conditional rendering:**
```html
<div th:if="${error}" class="alert alert-error">
    <p th:text="${error}"></p>
</div>

<div th:if="${success}" class="alert alert-success">
    <p th:text="${success}"></p>
</div>
```

**4. URL generation:**
```html
<link rel="stylesheet" th:href="@{/assets/css/auth.css}">
<a th:href="@{/patient/register}">Register</a>
<form th:action="@{/logout}" method="get">
```

---

## Security Configuration

### Cấu hình cho cả MVC và REST API

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // MVC login pages - public
            .requestMatchers("/admin/login", "/doctor/login", "/patient/login").permitAll()
            .requestMatchers("/patient/register").permitAll()
            
            // MVC dashboards - require session authentication
            .requestMatchers("/admin/dashboard").hasRole("ADMIN")
            .requestMatchers("/doctor/dashboard").hasRole("DOCTOR")
            
            // REST API - require JWT token
            .requestMatchers("/admin/api/**").hasRole("ADMIN")
            .requestMatchers("/doctor/api/**").hasRole("DOCTOR")
            
            .anyRequest().authenticated())
        .sessionManagement(session -> session
            // IF_REQUIRED: sessions for MVC, stateless for REST API
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

---

## Error Handling

### Global Exception Handler cho MVC

Tạo `@ControllerAdvice` để handle exceptions:

```java
@ControllerAdvice
public class MvcExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(
            MethodArgumentNotValidException ex,
            Model model) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        model.addAttribute("errors", errors);
        return "error/validation";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/generic";
    }
}
```

---

## Best Practices

### 1. **Tách biệt MVC và REST Controllers**
- MVC controllers trong package `mvc`
- REST controllers trong package `controllers`

### 2. **Sử dụng RedirectAttributes cho Flash Messages**
```java
redirectAttributes.addFlashAttribute("success", "Operation successful!");
return "redirect:/dashboard";
```

### 3. **Validate dữ liệu ở server-side**
- Không tin tưởng client-side validation
- Luôn validate lại ở server

### 4. **Session Security**
```java
// Set session timeout
server.servlet.session.timeout=30m

// HttpOnly cookies
server.servlet.session.cookie.http-only=true

// Secure cookies (HTTPS only)
server.servlet.session.cookie.secure=true
```

### 5. **CSRF Protection cho Forms**
Nếu enable CSRF:
```html
<form th:action="@{/login}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    <!-- form fields -->
</form>
```

---

## Testing

### Test MVC Controllers

```java
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthenticationService authService;
    
    @Test
    void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/doctor/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/doctorLogin"))
            .andExpect(model().attributeExists("login"));
    }
    
    @Test
    void testLoginSuccess() throws Exception {
        // Mock successful login
        when(authService.validateDoctor(any()))
            .thenReturn(LoginResponse.success("token", doctor));
        
        mockMvc.perform(post("/doctor/login")
                .param("username", "doctor@example.com")
                .param("password", "password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/doctor/dashboard"));
    }
    
    @Test
    void testLoginValidationError() throws Exception {
        mockMvc.perform(post("/doctor/login")
                .param("username", "")
                .param("password", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/doctorLogin"))
            .andExpect(model().hasErrors());
    }
}
```

---

## Các Bước Tiếp Theo (Recommended)

### 1. **Migrate Appointment Booking sang MVC**
- Tạo form để book appointment
- Server-side validation cho appointment data
- Show available time slots

### 2. **Migrate Profile Management sang MVC**
- Update patient/doctor profile forms
- Password change form
- Profile picture upload

### 3. **Add CSRF Protection**
- Enable CSRF trong SecurityConfig
- Add CSRF tokens vào forms

### 4. **Implement Flash Messages**
- Success/error messages sau mỗi operation
- Toast notifications

### 5. **Add Client-side Enhancement**
- Progressive Enhancement
- AJAX for non-critical features
- Form validation feedback

---

## Kết Luận

Migration này giúp bạn:

✅ **Học Spring MVC** - Form handling, validation, session management  
✅ **Best Practices** - Separation of concerns, security, error handling  
✅ **Hybrid Architecture** - MVC cho forms, REST API cho dynamic content  
✅ **Better UX** - Server-side rendering, SEO-friendly, faster initial load  

Tiếp tục practice bằng cách migrate thêm các features khác sang MVC pattern!
