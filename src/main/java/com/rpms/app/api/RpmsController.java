package com.rpms.app.api;

import com.rpms.app.dto.ApiRequests.*;
import com.rpms.app.service.RpmsService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RpmsController {
  private final RpmsService service;
  public RpmsController(RpmsService service) { this.service = service; }

  @GetMapping("/candidates") public List<Map<String, Object>> candidates() { return service.candidates(); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/candidates") public Map<String, Object> createCandidate(@Valid @RequestBody CandidateRequest request, Principal actor) { return service.saveCandidate(request, null, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PutMapping("/candidates/{id}") public Map<String, Object> updateCandidate(@PathVariable Integer id, @Valid @RequestBody CandidateRequest request, Principal actor) { return service.saveCandidate(request, id, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PatchMapping("/candidates/{id}/status") public Map<String, Object> updateCandidateStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateCandidateStatus(id, request, actor.getName()); }
  @GetMapping("/candidates/{id}/skills") public List<Map<String, Object>> candidateSkills(@PathVariable Integer id) { return service.candidateSkills(id); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/candidates/{id}/skills") public Map<String, Object> addCandidateSkill(@PathVariable Integer id, @Valid @RequestBody CandidateSkillRequest request, Principal actor) { return service.addCandidateSkill(id, request, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PutMapping("/candidates/{id}/skills/{skillId}") public Map<String, Object> updateCandidateSkill(@PathVariable Integer id, @PathVariable Integer skillId, @Valid @RequestBody CandidateSkillRequest request, Principal actor) { return service.updateCandidateSkill(id, skillId, request, actor.getName()); }

  @GetMapping("/companies") public List<Map<String, Object>> companies() { return service.companies(); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/companies") public Map<String, Object> createCompany(@Valid @RequestBody CompanyRequest request, Principal actor) { return service.saveCompany(request, null, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PutMapping("/companies/{id}") public Map<String, Object> updateCompany(@PathVariable Integer id, @Valid @RequestBody CompanyRequest request, Principal actor) { return service.saveCompany(request, id, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PatchMapping("/companies/{id}/status") public Map<String, Object> updateCompanyStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateCompanyStatus(id, request, actor.getName()); }

  @GetMapping("/skills") public List<Map<String, Object>> skills() { return service.skills(); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/skills") public Map<String, Object> createSkill(@Valid @RequestBody SkillRequest request, Principal actor) { return service.saveSkill(request, null, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PutMapping("/skills/{id}") public Map<String, Object> updateSkill(@PathVariable Integer id, @Valid @RequestBody SkillRequest request, Principal actor) { return service.saveSkill(request, id, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PatchMapping("/skills/{id}/status") public Map<String, Object> updateSkillStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateSkillStatus(id, request, actor.getName()); }

  @GetMapping("/jobs") public List<Map<String, Object>> jobs(@RequestParam(required = false) String status) { return service.jobs(status); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/jobs") public Map<String, Object> createJob(@Valid @RequestBody JobRequest request, Principal actor) { return service.saveJob(request, null, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PutMapping("/jobs/{id}") public Map<String, Object> updateJob(@PathVariable Integer id, @Valid @RequestBody JobRequest request, Principal actor) { return service.saveJob(request, id, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PatchMapping("/jobs/{id}/status") public Map<String, Object> updateJobStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateJobStatus(id, request, actor.getName()); }
  @GetMapping("/jobs/{id}/skills") public List<Map<String, Object>> jobSkills(@PathVariable Integer id) { return service.jobSkills(id); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/jobs/{id}/skills") public Map<String, Object> addJobSkill(@PathVariable Integer id, @Valid @RequestBody JobSkillRequest request, Principal actor) { return service.addJobSkill(id, request, actor.getName()); }

  @GetMapping("/applications") public List<Map<String, Object>> applications() { return service.applications(); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/applications") public Map<String, Object> createApplication(@Valid @RequestBody ApplicationRequest request, Principal actor) { return service.saveApplication(request, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PatchMapping("/applications/{id}/status") public Map<String, Object> updateApplicationStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateApplicationStatus(id, request, actor.getName()); }
  @GetMapping("/applications/{id}/history") public List<Map<String, Object>> applicationHistory(@PathVariable Integer id) { return service.applicationHistory(id); }

  @GetMapping("/interviews") public List<Map<String, Object>> interviews() { return service.interviews(false); }
  @GetMapping("/interviews/upcoming") public List<Map<String, Object>> upcomingInterviews() { return service.interviews(true); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/interviews") public Map<String, Object> createInterview(@Valid @RequestBody InterviewRequest request, Principal actor) { return service.saveInterview(request, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PutMapping("/interviews/{id}") public Map<String, Object> rescheduleInterview(@PathVariable Integer id, @Valid @RequestBody InterviewRequest request, Principal actor) { return service.rescheduleInterview(id, request, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PatchMapping("/interviews/{id}/status") public Map<String, Object> updateInterviewStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateInterviewStatus(id, request, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/interviews/{id}/feedback") public Map<String, Object> addFeedback(@PathVariable Integer id, @Valid @RequestBody FeedbackRequest request, Principal actor) { return service.saveFeedback(id, request, actor.getName()); }
  @GetMapping("/interview-feedback") public List<Map<String, Object>> feedbackRecords() { return service.feedbackRecords(); }

  @GetMapping("/offers") public List<Map<String, Object>> offers() { return service.offers(); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PostMapping("/offers") public Map<String, Object> createOffer(@Valid @RequestBody OfferRequest request, Principal actor) { return service.saveOffer(request, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  @PatchMapping("/offers/{id}/status") public Map<String, Object> updateOfferStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateOfferStatus(id, request, actor.getName()); }

  @GetMapping("/placements") public List<Map<String, Object>> placements() { return service.placements(); }
  @PreAuthorize("hasAnyRole('ADMIN', 'PLACEMENT_OFFICER')")
  @PostMapping("/placements") public Map<String, Object> createPlacement(@Valid @RequestBody PlacementRequest request, Principal actor) { return service.savePlacement(request, actor.getName()); }
  @PreAuthorize("hasAnyRole('ADMIN', 'PLACEMENT_OFFICER')")
  @PatchMapping("/placements/{id}/status") public Map<String, Object> updatePlacementStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updatePlacementStatus(id, request, actor.getName()); }

  @GetMapping("/roles") public List<Map<String, Object>> roles() { return service.roleOptions(); }
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users") public List<Map<String, Object>> users() { return service.users(); }
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/users") public Map<String, Object> createUser(@Valid @RequestBody UserRequest request, Principal actor) { return service.saveUser(request, null, actor.getName()); }
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/users/{id}") public Map<String, Object> updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest request, Principal actor) { return service.saveUser(request, id, actor.getName()); }
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/users/{id}/status") public Map<String, Object> updateUserStatus(@PathVariable Integer id, @Valid @RequestBody StatusRequest request, Principal actor) { return service.updateUserStatus(id, request, actor.getName()); }

  @GetMapping("/activity-logs") public List<Map<String, Object>> activityLogs() { return service.recentActivity(); }
  @GetMapping("/qualifications") public List<Map<String, Object>> qualifications() { return service.qualifications(); }
  @GetMapping("/locations") public List<Map<String, Object>> locations() { return service.locations(); }
}
