// footer.js - Render footer

function renderFooter() {
  const footerDiv = document.getElementById('footer');
  if (!footerDiv) return;

  footerDiv.innerHTML = 
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-logo">
          <img src="./assets/images/logo/logo.png" alt="Hospital CMS Logo">
          <p>Your trusted healthcare management system. Providing quality care and efficient management solutions.</p>
        </div>
        <div class="footer-links">
          <div class="footer-column">
            <h4>Quick Links</h4>
            <a href="/">Home</a>
            <a href="/pages/patientDashboard.html">Find Doctors</a>
            <a href="#">About Us</a>
            <a href="#">Contact</a>
          </div>
          <div class="footer-column">
            <h4>Services</h4>
            <a href="#">Book Appointment</a>
            <a href="#">Medical Records</a>
            <a href="#">Prescriptions</a>
            <a href="#">Health Tips</a>
          </div>
          <div class="footer-column">
            <h4>Support</h4>
            <a href="#">Help Center</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Terms of Service</a>
            <a href="#">FAQ</a>
          </div>
        </div>
      </div>
      <div style="text-align: center; padding-top: 20px; border-top: 1px solid #34495e; margin-top: 20px; color: #bdc3c7;">
        <p>&copy; 2025 Hospital CMS. All rights reserved.</p>
      </div>
    </footer>;
}

// Render footer when DOM is loaded
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', renderFooter);
} else {
  renderFooter();
}
