// Tenant switching functionality
function switchTenant() {
    const form = document.getElementById('tenantForm');
    const select = document.getElementById('tenantSelect');
    
    if (form && select) {
        // Submit the form
        form.submit();
    }
}

// Add loading indicator when tenant is being switched
document.addEventListener('DOMContentLoaded', function() {
    const tenantSelect = document.getElementById('tenantSelect');
    
    if (tenantSelect) {
        tenantSelect.addEventListener('change', function() {
            // Add visual feedback
            this.style.opacity = '0.6';
            
            // Create and show loading message
            const loadingMsg = document.createElement('span');
            loadingMsg.id = 'tenant-loading';
            loadingMsg.textContent = 'Switching tenant...';
            loadingMsg.style.marginLeft = '10px';
            loadingMsg.style.color = '#666';
            loadingMsg.style.fontSize = '0.9em';
            
            const tenantSelector = document.querySelector('.tenant-selector');
            if (tenantSelector) {
                tenantSelector.appendChild(loadingMsg);
            }
        });
    }
});

// Display current tenant info
function displayTenantInfo() {
    const currentTenant = document.querySelector('.tenant-selector select').value;
    console.log('Current tenant:', currentTenant);
}
