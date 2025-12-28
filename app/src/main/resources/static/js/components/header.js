// header.js - Render dynamic header based on user role

function renderHeader() {
  const headerDiv = document.getElementById('header');
  if (!headerDiv) return;

  const pathname = window.location.pathname;
  
  // For index page, show simple header without navigation
  if (pathname === '/' || pathname.endsWith('/index.html')) {
    headerDiv.innerHTML = 
      <header class="header">
        <a href="/" class="logo-link">
          <img src="./assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </a>
      </header>;
    return;
  }

  // For other pages, show full header with role-based navigation
  const role = localStorage.getItem('userRole');
  const token = localStorage.getItem('token');

  let headerContent = 
    <header class="header">
      <a href="/" class="logo-link">
        <img src="../assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </a>
      <nav>;

  // Check if session is valid for logged-in roles
  if ((role === 'admin' || role === 'doctor' || role === 'loggedPatient') && !token) {
    localStorage.removeItem('userRole');
    alert('Session expired. Please log in again.');
    window.location.href = '/';
    return;
  }

  // Role-specific navigation
  if (role === 'admin') {
    headerContent += 
      <button class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" onclick="logout()">Logout</a>;
  } else if (role === 'doctor') {
    headerContent += 
      <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" onclick="logout()">Logout</a>;
  } else if (role === 'patient') {
    headerContent += 
      <button id="patientLogin" class="adminBtn">Login</button>
      <button id="patientSignup" class="adminBtn">Sign Up</button>;
  } else if (role === 'loggedPatient') {
    headerContent += 
      <button class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" onclick="logoutPatient()">Logout</a>;
  }

  headerContent += 
      </nav>
    </header>;

  headerDiv.innerHTML = headerContent;
}

// Logout functions
function logout() {
  localStorage.removeItem('userRole');
  localStorage.removeItem('token');
  window.location.href = '/';
}

function logoutPatient() {
  localStorage.removeItem('token');
  localStorage.setItem('userRole', 'patient');
  window.location.href = '/pages/patientDashboard.html';
}

// Render header when DOM is loaded
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', renderHeader);
} else {
  renderHeader();
}
