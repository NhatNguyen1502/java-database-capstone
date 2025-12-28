// render.js

function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');
  if (role === "admin") {
    if (token) {
      window.location.href = `/adminDashboard/${token}`;
    }
  } else if (role === "patient") {
    window.location.href = "/pages/patientDashboard.html";
  } else if (role === "doctor") {
    if (token) {
      window.location.href = `/doctorDashboard/${token}`;
    }
  } else if (role === "loggedPatient") {
    window.location.href = "loggedPatientDashboard.html";
  }
}

// For index page - just render header/footer, don't check role
function renderIndexPage() {
  console.log('Index page loaded');
  // Clear any existing role when on index page
  localStorage.removeItem('userRole');
  // Header and footer will be rendered by their respective scripts
}

// For other pages - check if user has role
function renderContent() {
  const role = getRole();
  if (!role) {
    // Only redirect if NOT on index page
    if (!window.location.pathname.endsWith('/') && !window.location.pathname.endsWith('/index.html')) {
      window.location.href = "/";
      return;
    }
  }
  console.log('Current role:', role);
}
