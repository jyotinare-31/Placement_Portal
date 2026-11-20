package com.rpms.app.api;

import com.rpms.app.service.RpmsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
  private final RpmsService service;
  public DashboardController(RpmsService service) { this.service = service; }

  @GetMapping("/dashboard/summary")
  public Map<String, Object> summary() { return service.dashboardSummary(); }

  @GetMapping("/dashboard/analytics")
  public Map<String, Object> dashboardAnalytics(@RequestParam(defaultValue = "6") int months) { return service.dashboardAnalytics(months); }

  @GetMapping("/reports/analytics")
  public Map<String, Object> reportAnalytics() { return service.dashboardAnalytics(12); }

  @GetMapping("/dashboard/recent-activity")
  public List<Map<String, Object>> recentActivity() { return service.recentActivity(); }
}
