// index.js - Handle login modals and authentication

// Open modal for login
function openModal(type) {
  console.log('Opening modal for:', type);
  const modal = document.getElementById('modal');
  const modalBody = document.getElementById('modal-body');
  
  if (!modal || !modalBody) {
    console.error('Modal elements not found');
    return;
  }

  let modalContent = '';

  if (type === 'admin') {
    modalContent = '<h2>Admin Login</h2>' +
      '<form onsubmit="event.preventDefault(); adminLoginHandler();">' +
        '<input type="text" id="adminUsername" class="input-field" placeholder="Username or Email" required />' +
        '<input type="password" id="adminPassword" class="input-field" placeholder="Password" required />' +
        '<button type="submit" class="dashboard-btn">Login</button>' +
      '</form>';
  } else if (type === 'doctor') {
    modalContent = '<h2>Doctor Login</h2>' +
      '<form onsubmit="event.preventDefault(); doctorLoginHandler();">' +
        '<input type="email" id="doctorEmail" class="input-field" placeholder="Email" required />' +
        '<input type="password" id="doctorPassword" class="input-field" placeholder="Password" required />' +
        '<button type="submit" class="dashboard-btn">Login</button>' +
      '</form>';
  } else if (type === 'patient') {
    modalContent = '<h2>Patient Login</h2>' +
      '<form onsubmit="event.preventDefault(); patientLoginHandler();">' +
        '<input type="email" id="patientEmail" class="input-field" placeholder="Email" required />' +
        '<input type="password" id="patientPassword" class="input-field" placeholder="Password" required />' +
        '<button type="submit" class="dashboard-btn">Login</button>' +
      '</form>';
  }

  modalBody.innerHTML = modalContent;
  modal.style.display = 'block';
  console.log('Modal opened');
}

// Close modal
function closeModal() {
  const modal = document.getElementById('modal');
  if (modal) {
    modal.style.display = 'none';
  }
}

// Admin login handler
async function adminLoginHandler() {
  const username = document.getElementById('adminUsername').value;
  const password = document.getElementById('adminPassword').value;

  try {
    const response = await fetch('/api/admin/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await response.json();

    if (data.success && data.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('userRole', 'admin');
      window.location.href = '/admin/dashboard';
    } else {
      alert('Login failed: ' + (data.message || 'Invalid credentials'));
    }
  } catch (error) {
    console.error('Login error:', error);
    alert('Login failed. Please try again.');
  }
}

// Doctor login handler
async function doctorLoginHandler() {
  const username = document.getElementById('doctorEmail').value;
  const password = document.getElementById('doctorPassword').value;

  try {
    const response = await fetch('/api/doctor/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await response.json();

    if (data.success && data.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('userRole', 'doctor');
      window.location.href = '/doctor/dashboard';
    } else {
      alert('Login failed: ' + (data.message || 'Invalid credentials'));
    }
  } catch (error) {
    console.error('Login error:', error);
    alert('Login failed. Please try again.');
  }
}

// Patient login handler
async function patientLoginHandler() {
  const username = document.getElementById('patientEmail').value;
  const password = document.getElementById('patientPassword').value;

  try {
    const response = await fetch('/api/patient/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await response.json();

    if (data.success && data.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('userRole', 'loggedPatient');
      window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
      alert('Login failed: ' + (data.message || 'Invalid credentials'));
    }
  } catch (error) {
    console.error('Login error:', error);
    alert('Login failed. Please try again.');
  }
}

// Setup modal close button
document.addEventListener('DOMContentLoaded', function() {
  console.log('DOM loaded, setting up modal handlers');
  
  const closeBtn = document.getElementById('closeModal');
  if (closeBtn) {
    closeBtn.onclick = closeModal;
  }

  // Close modal when clicking outside
  const modal = document.getElementById('modal');
  if (modal) {
    modal.onclick = function(event) {
      if (event.target === modal) {
        closeModal();
      }
    };
  }
});

// Make functions globally available
window.openModal = openModal;
window.closeModal = closeModal;
window.adminLoginHandler = adminLoginHandler;
window.doctorLoginHandler = doctorLoginHandler;
window.patientLoginHandler = patientLoginHandler;
