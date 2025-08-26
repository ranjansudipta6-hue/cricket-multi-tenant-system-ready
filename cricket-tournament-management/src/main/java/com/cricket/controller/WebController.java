package com.cricket.controller;

import com.cricket.entity.Team;
import com.cricket.repository.TeamRepository;
import com.cricket.service.TenantService;
import com.example.multitenant.entity.Tenant;
import com.example.multitenant.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class WebController {
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private TenantService tenantService;
    
    private void addCommonAttributes(Model model, HttpSession session) {
        // Add available tenants to all pages
        List<Tenant> tenants = tenantService.getAllActiveTenants();
        model.addAttribute("availableTenants", tenants);
        
        // Add current tenant from session or default
        String currentTenantId = (String) session.getAttribute("currentTenant");
        if (currentTenantId == null && !tenants.isEmpty()) {
            currentTenantId = tenants.get(0).getId();
            session.setAttribute("currentTenant", currentTenantId);
        }
        model.addAttribute("currentTenant", currentTenantId);
    }
    
    @GetMapping("/switch-tenant")
    public String switchTenantGet() {
        // Guard: direct GET to /switch-tenant should not error
        return "redirect:/";
    }

    @PostMapping("/switch-tenant")
    public String switchTenant(@RequestParam(name = "tenantId", required = false) String tenantId, HttpSession session) {
        if (tenantId == null || tenantId.isBlank()) {
            System.out.println("switch-tenant called without tenantId; redirecting to home");
            return "redirect:/";
        }
        // Sanitize: some browsers may submit duplicate values, e.g., "tenant2,tenant2"
        if (tenantId.contains(",")) {
            tenantId = tenantId.split(",")[0];
        }
        tenantId = tenantId.trim();
        // Set the new tenant in session
        session.setAttribute("currentTenant", tenantId);
        // Also update the current tenant context
        TenantContext.setTenantId(tenantId);
        System.out.println("Switched to tenant: " + tenantId);
        // Redirect to the teams page to see the updated data
        return "redirect:/teams";
    }
    
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        addCommonAttributes(model, session);
        return "index";
    }
    
    @GetMapping("/test")
    public String test(Model model, HttpSession session) {
        System.out.println("=== TEST ENDPOINT CALLED ===");
        model.addAttribute("message", "Test endpoint working");
        return "index"; // Use existing template
    }

    @GetMapping("/debug/tenant")
    public org.springframework.http.ResponseEntity<String> debugTenant(HttpSession session) {
        String sessionTenant = (String) session.getAttribute("currentTenant");
        String contextTenant = TenantContext.getTenantId();
        String body = "sessionTenant=" + sessionTenant + ", contextTenant=" + contextTenant;
        System.out.println("[DEBUG] " + body);
        return org.springframework.http.ResponseEntity.ok(body);
    }
    
    @GetMapping("/teams")
    public String teams(Model model, HttpSession session) {
        System.out.println("=== TEAMS METHOD CALLED ===");
        
        // Add common attributes (tenant dropdown, current tenant, etc.)
        addCommonAttributes(model, session);
        
        // Get current tenant from context (should be set by WebTenantFilter)
        String currentTenantId = TenantContext.getTenantId();
        System.out.println("Current tenant in teams(): " + currentTenantId);
        
        try {
            // Load teams from repository
            List<Team> teams = teamRepository.findAll();
            System.out.println("Successfully loaded " + teams.size() + " teams for tenant: " + TenantContext.getTenantId());
            
            model.addAttribute("teams", teams);
            
        } catch (Exception e) {
            System.out.println("ERROR loading teams: " + e.getMessage());
            e.printStackTrace();
            
            // Add fallback data and error message
            model.addAttribute("teams", java.util.Collections.emptyList());
            model.addAttribute("error", "Error loading teams for tenant '" + TenantContext.getTenantId() + "': " + e.getMessage());
        }
        
        return "teams-working";
    }
    
    @GetMapping("/teams/{id}")
    public String teamDetail(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        addCommonAttributes(model, session);
        
        // The WebTenantFilter should have already set the tenant context
        String currentTenantId = TenantContext.getTenantId();
        
        try {
            System.out.println("[teamDetail] Looking up team id=" + id + " for tenant=" + currentTenantId);
            Team team = teamRepository.findWithPlayersById(id).orElse(null);
            
            if (team == null) {
                System.out.println("[teamDetail] Team not found for id=" + id + " tenant=" + currentTenantId);
                redirectAttributes.addFlashAttribute("error", "Team not found for id " + id + " in tenant " + currentTenantId);
                return "redirect:/teams";
            }
            
            model.addAttribute("team", team);
            System.out.println("Loaded team: " + team.getName() + " for tenant: " + currentTenantId);
        } catch (Exception e) {
            System.out.println("Error loading team: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error loading team: " + e.getMessage());
            return "redirect:/teams";
        }
        return "team-detail";
    }
}
