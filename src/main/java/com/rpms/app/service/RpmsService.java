package com.rpms.app.service;

import com.rpms.app.domain.*;
import com.rpms.app.dto.ApiRequests.*;
import com.rpms.app.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class RpmsService {
  private final CandidateRepository candidates;
  private final CandidateSkillRepository candidateSkills;
  private final SkillRepository skills;
  private final JobSkillRepository jobSkills;
  private final QualificationRepository qualifications;
  private final LocationRepository locations;
  private final CompanyRepository companies;
  private final JobRepository jobs;
  private final ApplicationRepository applications;
  private final ApplicationHistoryRepository history;
  private final InterviewRepository interviews;
  private final InterviewFeedbackRepository feedback;
  private final OfferRepository offers;
  private final PlacementRepository placements;
  private final ActivityLogRepository activity;
  private final UserRepository users;
  private final RoleRepository roles;
  private final PasswordEncoder passwordEncoder;

  public RpmsService(CandidateRepository candidates, CandidateSkillRepository candidateSkills, SkillRepository skills,
      JobSkillRepository jobSkills, QualificationRepository qualifications, LocationRepository locations,
      CompanyRepository companies, JobRepository jobs, ApplicationRepository applications, ApplicationHistoryRepository history,
      InterviewRepository interviews, InterviewFeedbackRepository feedback, OfferRepository offers, PlacementRepository placements,
      ActivityLogRepository activity, UserRepository users, RoleRepository roles, PasswordEncoder passwordEncoder) {
    this.candidates = candidates; this.candidateSkills = candidateSkills; this.skills = skills; this.jobSkills = jobSkills;
    this.qualifications = qualifications; this.locations = locations; this.companies = companies;
    this.jobs = jobs; this.applications = applications; this.history = history; this.interviews = interviews; this.feedback = feedback;
    this.offers = offers; this.placements = placements; this.activity = activity; this.users = users; this.roles = roles;
    this.passwordEncoder = passwordEncoder;
  }

  public List<Map<String, Object>> candidates() { return candidates.findAll().stream().map(this::candidateMap).toList(); }
  public Map<String, Object> saveCandidate(CandidateRequest request, Integer id, String actor) {
    CandidateEntity entity = id == null ? new CandidateEntity() : candidates.findById(id).orElseThrow(() -> missing("Candidate"));
    boolean create = id == null;
    entity.setFullName(request.fullName().trim()); entity.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
    entity.setMobile(request.mobile().trim()); entity.setDateOfBirth(request.dateOfBirth()); entity.setGender(request.gender());
    entity.setQualification(request.qualification().trim()); entity.setCollege(request.college()); entity.setUniversity(request.university());
    entity.setPassingYear(request.passingYear() == null ? null : request.passingYear().shortValue()); entity.setPercentage(request.percentage()); entity.setCgpa(request.cgpa());
    entity.setBacklogCount(request.backlogCount() == null ? 0 : request.backlogCount()); entity.setPrimaryTechnology(clean(request.primaryTechnology()));
    entity.setCurrentLocation(clean(request.currentLocation())); entity.setPreferredLocation(clean(request.preferredLocation()));
    if (request.profileStatus() != null) entity.setProfileStatus(allowed(request.profileStatus(), Set.of("ACTIVE", "INACTIVE", "ON_HOLD", "BLACKLISTED"), "profile status"));
    if (create) { entity.setCandidateCode(code("CAN")); entity.setPlacementStatus("NOT_PLACED"); }
    CandidateEntity saved = candidates.save(entity);
    log(actor, "CANDIDATES", create ? "CREATE" : "UPDATE", saved.getId(), saved.getFullName());
    return candidateMap(saved);
  }

  public Map<String, Object> updateCandidateStatus(Integer id, StatusRequest request, String actor) {
    CandidateEntity candidate = require(candidates.findById(id), "Candidate"); candidate.setProfileStatus(allowed(request.status(), Set.of("ACTIVE", "INACTIVE", "ON_HOLD", "BLACKLISTED"), "profile status"));
    CandidateEntity saved = candidates.save(candidate); log(actor, "CANDIDATES", "STATUS_CHANGED", id, saved.getProfileStatus()); return candidateMap(saved);
  }
  public List<Map<String, Object>> candidateSkills(Integer candidateId) {
    require(candidates.findById(candidateId), "Candidate");
    Map<Integer, SkillEntity> catalog = skills.findAll().stream().collect(Collectors.toMap(SkillEntity::getId, Function.identity()));
    return candidateSkills.findByCandidateId(candidateId).stream().map(item -> map("candidateSkillId", item.getId(), "candidateId", item.getCandidateId(), "skillId", item.getSkillId(), "skillName", Optional.ofNullable(catalog.get(item.getSkillId())).map(SkillEntity::getSkillName).orElse("—"), "proficiencyLevel", item.getProficiencyLevel(), "yearsExperience", item.getYearsExperience(), "isPrimary", item.getPrimary())).toList();
  }

  public Map<String, Object> updateCandidateSkill(Integer candidateId, Integer candidateSkillId, CandidateSkillRequest request, String actor) {
    CandidateSkillEntity entity = require(candidateSkills.findById(candidateSkillId), "Candidate skill");
    if (!Objects.equals(entity.getCandidateId(), candidateId)) throw missing("Candidate skill");
    require(candidates.findById(candidateId), "Candidate"); require(skills.findById(request.skillId()), "Skill");
    if (!Objects.equals(entity.getSkillId(), request.skillId()) && candidateSkills.existsByCandidateIdAndSkillId(candidateId, request.skillId())) throw new IllegalArgumentException("This skill is already assigned to the candidate.");
    entity.setSkillId(request.skillId()); entity.setProficiencyLevel(request.proficiencyLevel() == null ? null : allowed(request.proficiencyLevel(), Set.of("BASIC", "INTERMEDIATE", "ADVANCED"), "proficiency level"));
    entity.setYearsExperience(request.yearsExperience() == null ? BigDecimal.ZERO : request.yearsExperience()); entity.setPrimary(Boolean.TRUE.equals(request.primary()));
    CandidateSkillEntity saved = candidateSkills.save(entity); log(actor, "CANDIDATES", "SKILL_UPDATED", candidateId, "Skill " + request.skillId());
    return map("candidateSkillId", saved.getId(), "candidateId", candidateId, "skillId", saved.getSkillId(), "proficiencyLevel", saved.getProficiencyLevel(), "yearsExperience", saved.getYearsExperience(), "isPrimary", saved.getPrimary());
  }
  public Map<String, Object> addCandidateSkill(Integer candidateId, CandidateSkillRequest request, String actor) {
    require(candidates.findById(candidateId), "Candidate"); require(skills.findById(request.skillId()), "Skill");
    if (candidateSkills.existsByCandidateIdAndSkillId(candidateId, request.skillId())) throw new IllegalArgumentException("This skill is already assigned to the candidate.");
    CandidateSkillEntity entity = new CandidateSkillEntity(); entity.setCandidateId(candidateId); entity.setSkillId(request.skillId());
    entity.setProficiencyLevel(request.proficiencyLevel() == null ? null : allowed(request.proficiencyLevel(), Set.of("BASIC", "INTERMEDIATE", "ADVANCED"), "proficiency level")); entity.setYearsExperience(request.yearsExperience() == null ? BigDecimal.ZERO : request.yearsExperience()); entity.setPrimary(Boolean.TRUE.equals(request.primary()));
    CandidateSkillEntity saved = candidateSkills.save(entity); log(actor, "CANDIDATES", "SKILL_ADDED", candidateId, "Skill " + request.skillId());
    return map("candidateSkillId", saved.getId(), "candidateId", candidateId, "skillId", saved.getSkillId(), "proficiencyLevel", saved.getProficiencyLevel(), "yearsExperience", saved.getYearsExperience(), "isPrimary", saved.getPrimary());
  }

  public List<Map<String, Object>> companies() { return companies.findAll().stream().map(this::companyMap).toList(); }
  public Map<String, Object> saveCompany(CompanyRequest request, Integer id, String actor) {
    CompanyEntity entity = id == null ? new CompanyEntity() : companies.findById(id).orElseThrow(() -> missing("Company"));
    boolean create = id == null; entity.setCompanyName(request.companyName().trim()); entity.setIndustry(clean(request.industry()));
    entity.setWebsite(clean(request.website())); entity.setLocation(clean(request.location())); entity.setCompanyType(clean(request.companyType()));
    entity.setHrName(clean(request.hrName())); entity.setHrEmail(clean(request.hrEmail())); entity.setHrMobile(clean(request.hrMobile()));
    if (request.status() != null) entity.setStatus(allowed(request.status(), Set.of("ACTIVE", "INACTIVE"), "company status"));
    if (create) entity.setCompanyCode(code("CMP"));
    CompanyEntity saved = companies.save(entity); log(actor, "COMPANIES", create ? "CREATE" : "UPDATE", saved.getId(), saved.getCompanyName());
    return companyMap(saved);
  }

  public Map<String, Object> updateCompanyStatus(Integer id, StatusRequest request, String actor) {
    CompanyEntity company = require(companies.findById(id), "Company"); company.setStatus(allowed(request.status(), Set.of("ACTIVE", "INACTIVE"), "company status"));
    CompanyEntity saved = companies.save(company); log(actor, "COMPANIES", "STATUS_CHANGED", id, saved.getStatus()); return companyMap(saved);
  }
  public List<Map<String, Object>> skills() { return skills.findAll().stream().map(this::skillMap).toList(); }
  public Map<String, Object> updateSkillStatus(Integer id, StatusRequest request, String actor) {
    SkillEntity skill = require(skills.findById(id), "Skill"); String status = allowed(request.status(), Set.of("ACTIVE", "INACTIVE"), "skill status"); skill.setActive("ACTIVE".equals(status));
    SkillEntity saved = skills.save(skill); log(actor, "SKILLS", "STATUS_CHANGED", id, status); return skillMap(saved);
  }
  public Map<String, Object> saveSkill(SkillRequest request, Integer id, String actor) {
    SkillEntity entity = id == null ? new SkillEntity() : skills.findById(id).orElseThrow(() -> missing("Skill"));
    boolean create = id == null; entity.setSkillName(request.skillName().trim()); entity.setCategory(clean(request.category()));
    if (request.active() != null) entity.setActive(request.active());
    SkillEntity saved = skills.save(entity); log(actor, "SKILLS", create ? "CREATE" : "UPDATE", saved.getId(), saved.getSkillName());
    return skillMap(saved);
  }

  public List<Map<String, Object>> jobs(String status) {
    List<JobEntity> found = status == null || status.isBlank() ? jobs.findAll() : jobs.findByStatusOrderByCreatedAtDesc(allowed(status, Set.of("DRAFT", "OPEN", "CLOSED", "CANCELLED"), "job status"));
    Map<Integer, CompanyEntity> companyIndex = companies.findAll().stream().collect(Collectors.toMap(CompanyEntity::getId, Function.identity()));
    return found.stream().map(job -> jobMap(job, companyIndex)).toList();
  }
  public Map<String, Object> saveJob(JobRequest request, Integer id, String actor) {
    CompanyEntity company = require(companies.findById(request.companyId()), "Company");
    if (company.getStatus().equals("INACTIVE")) throw new IllegalArgumentException("Open a job only with an active company.");
    if (request.applicationDeadline().isBefore(request.postedDate())) throw new IllegalArgumentException("Application deadline must be on or after the posted date.");
    if (request.minPackageLpa() != null && request.maxPackageLpa() != null && request.minPackageLpa().compareTo(request.maxPackageLpa()) > 0) throw new IllegalArgumentException("Minimum package cannot exceed maximum package.");
    if (request.minPassingYear() != null && request.maxPassingYear() != null && request.minPassingYear() > request.maxPassingYear()) throw new IllegalArgumentException("Minimum passing year cannot exceed maximum passing year.");
    JobEntity entity = id == null ? new JobEntity() : jobs.findById(id).orElseThrow(() -> missing("Job"));
    boolean create = id == null; entity.setCompanyId(company.getId()); entity.setJobTitle(request.jobTitle().trim());
    entity.setEmploymentType(request.employmentType() == null ? "FULL_TIME" : allowed(request.employmentType(), Set.of("FULL_TIME", "PART_TIME", "CONTRACT", "INTERNSHIP"), "employment type"));
    entity.setLocation(request.location().trim()); entity.setOpenings(request.openings()); entity.setMinPackageLpa(request.minPackageLpa()); entity.setMaxPackageLpa(request.maxPackageLpa());
    entity.setMinPercentage(request.minPercentage()); entity.setMinPassingYear(request.minPassingYear() == null ? null : request.minPassingYear().shortValue()); entity.setMaxPassingYear(request.maxPassingYear() == null ? null : request.maxPassingYear().shortValue());
    entity.setMaxBacklogs(request.maxBacklogs() == null ? 0 : request.maxBacklogs()); entity.setQualificationRule(clean(request.qualificationRule()));
    entity.setPostedDate(request.postedDate()); entity.setApplicationDeadline(request.applicationDeadline());
    if (request.status() != null) entity.setStatus(allowed(request.status(), Set.of("DRAFT", "OPEN", "CLOSED", "CANCELLED"), "job status"));
    if (create) { entity.setJobCode(code("JOB")); entity.setCreatedBy(userId(actor)); entity.setExperienceMinYears(BigDecimal.ZERO); }
    JobEntity saved = jobs.save(entity); log(actor, "JOBS", create ? "CREATE" : "UPDATE", saved.getId(), saved.getJobTitle());
    return jobMap(saved, Map.of(company.getId(), company));
  }

  public Map<String, Object> updateJobStatus(Integer id, StatusRequest request, String actor) {
    JobEntity job = require(jobs.findById(id), "Job");
    job.setStatus(allowed(request.status(), Set.of("DRAFT", "OPEN", "CLOSED", "CANCELLED"), "job status"));
    JobEntity saved = jobs.save(job); log(actor, "JOBS", "STATUS_CHANGED", id, saved.getStatus());
    return jobs(saved.getStatus()).stream().filter(row -> Objects.equals(row.get("jobId"), id)).findFirst().orElseThrow();
  }

  public List<Map<String, Object>> jobSkills(Integer jobId) {
    require(jobs.findById(jobId), "Job"); Map<Integer, SkillEntity> catalog = skills.findAll().stream().collect(Collectors.toMap(SkillEntity::getId, Function.identity()));
    return jobSkills.findByJobId(jobId).stream().map(item -> map("jobSkillId", item.getId(), "jobId", item.getJobId(), "skillId", item.getSkillId(), "skillName", Optional.ofNullable(catalog.get(item.getSkillId())).map(SkillEntity::getSkillName).orElse("—"), "minimumLevel", item.getMinimumLevel(), "mandatoryFlag", item.getMandatory())).toList();
  }
  public Map<String, Object> addJobSkill(Integer jobId, JobSkillRequest request, String actor) {
    require(jobs.findById(jobId), "Job"); require(skills.findById(request.skillId()), "Skill");
    if (jobSkills.existsByJobIdAndSkillId(jobId, request.skillId())) throw new IllegalArgumentException("This skill is already required for the job.");
    JobSkillEntity item = new JobSkillEntity(); item.setJobId(jobId); item.setSkillId(request.skillId()); item.setMinimumLevel(request.minimumLevel() == null ? null : allowed(request.minimumLevel(), Set.of("BASIC", "INTERMEDIATE", "ADVANCED"), "minimum skill level")); item.setMandatory(request.mandatoryFlag() == null || request.mandatoryFlag());
    JobSkillEntity saved = jobSkills.save(item); log(actor, "JOBS", "SKILL_ADDED", jobId, "Skill " + request.skillId());
    return map("jobSkillId", saved.getId(), "jobId", jobId, "skillId", saved.getSkillId(), "minimumLevel", saved.getMinimumLevel(), "mandatoryFlag", saved.getMandatory());
  }
  public List<Map<String, Object>> qualifications() { return qualifications.findAll().stream().filter(item -> Boolean.TRUE.equals(item.getActive())).map(item -> map("qualificationId", item.getId(), "qualificationName", item.getQualificationName())).toList(); }
  public List<Map<String, Object>> locations() { return locations.findAll().stream().filter(item -> Boolean.TRUE.equals(item.getActive())).map(item -> map("locationId", item.getId(), "cityName", item.getCityName(), "stateName", item.getStateName(), "countryName", item.getCountryName())).toList(); }
  public List<Map<String, Object>> applicationHistory(Integer applicationId) {
    require(applications.findById(applicationId), "Application");
    return history.findByApplicationIdOrderByChangedAtDesc(applicationId).stream().map(item -> map("historyId", item.getId(), "applicationId", item.getApplicationId(), "oldStatus", item.getOldStatus(), "newStatus", item.getNewStatus(), "remarks", item.getRemarks(), "changedBy", users.findById(item.getChangedBy()).map(UserEntity::getFullName).orElse("—"), "changedAt", item.getChangedAt())).toList();
  }
  public List<Map<String, Object>> feedbackRecords() {
    Map<Integer, InterviewEntity> rounds = interviews.findAll().stream().collect(Collectors.toMap(InterviewEntity::getId, Function.identity()));
    Map<Integer, ApplicationEntity> apps = applications.findAll().stream().collect(Collectors.toMap(ApplicationEntity::getId, Function.identity()));
    Map<Integer, CandidateEntity> candidateIndex = candidates.findAll().stream().collect(Collectors.toMap(CandidateEntity::getId, Function.identity()));
    Map<Integer, JobEntity> jobIndex = jobs.findAll().stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));
    return feedback.findAll().stream().map(item -> {
      InterviewEntity round = rounds.get(item.getInterviewId()); ApplicationEntity app = round == null ? null : apps.get(round.getApplicationId());
      CandidateEntity candidate = app == null ? null : candidateIndex.get(app.getCandidateId()); JobEntity job = app == null ? null : jobIndex.get(app.getJobId());
      return map("feedbackId", item.getId(), "interviewId", item.getInterviewId(), "applicationId", round == null ? null : round.getApplicationId(), "candidateName", candidate == null ? "—" : candidate.getFullName(), "jobTitle", job == null ? "—" : job.getJobTitle(), "technicalRating", item.getTechnicalRating(), "communicationRating", item.getCommunicationRating(), "problemSolvingRating", item.getProblemSolvingRating(), "overallRating", item.getOverallRating(), "recommendation", item.getRecommendation(), "remarks", item.getRemarks(), "submittedAt", item.getSubmittedAt());
    }).toList();
  }

  public List<Map<String, Object>> applications() {
    Map<Integer, CandidateEntity> candidateIndex = candidates.findAll().stream().collect(Collectors.toMap(CandidateEntity::getId, Function.identity()));
    Map<Integer, JobEntity> jobIndex = jobs.findAll().stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));
    Map<Integer, CompanyEntity> companyIndex = companies.findAll().stream().collect(Collectors.toMap(CompanyEntity::getId, Function.identity()));
    return applications.findAll().stream().map(item -> applicationMap(item, candidateIndex, jobIndex, companyIndex)).toList();
  }
  public Map<String, Object> saveApplication(ApplicationRequest request, String actor) {
    CandidateEntity candidate = require(candidates.findById(request.candidateId()), "Candidate");
    JobEntity job = require(jobs.findById(request.jobId()), "Job");
    if (applications.existsByCandidateIdAndJobId(candidate.getId(), job.getId())) throw new IllegalArgumentException("This candidate has already applied to the selected job.");
    ApplicationEntity entity = new ApplicationEntity(); entity.setApplicationCode(code("APP")); entity.setCandidateId(candidate.getId()); entity.setJobId(job.getId()); entity.setSource(clean(request.source()));
    entity.setAssignedRecruiterId(request.assignedRecruiterId() == null ? userId(actor) : request.assignedRecruiterId());
    List<String> reasons = new ArrayList<>();
    if (!"ACTIVE".equals(candidate.getProfileStatus())) reasons.add("Candidate profile is not active");
    if (!"OPEN".equals(job.getStatus()) || job.getApplicationDeadline().isBefore(LocalDate.now())) reasons.add("Job is closed or past its application deadline");
    if (candidate.getPercentage() != null && job.getMinPercentage() != null && candidate.getPercentage().compareTo(job.getMinPercentage()) < 0) reasons.add("Percentage is below the job minimum");
    if (candidate.getBacklogCount() != null && candidate.getBacklogCount() > job.getMaxBacklogs()) reasons.add("Backlog count exceeds the job limit");
    if (job.getMinPassingYear() != null && candidate.getPassingYear() < job.getMinPassingYear()) reasons.add("Passing year is earlier than the eligible range");
    if (job.getMaxPassingYear() != null && candidate.getPassingYear() > job.getMaxPassingYear()) reasons.add("Passing year is later than the eligible range");
    Map<Integer, Integer> candidateSkillLevels = candidateSkills.findByCandidateId(candidate.getId()).stream().collect(Collectors.toMap(CandidateSkillEntity::getSkillId, skill -> levelRank(skill.getProficiencyLevel()), (left, right) -> Math.max(left, right)));
    for (JobSkillEntity requiredSkill : jobSkills.findByJobId(job.getId())) {
      Integer candidateLevel = candidateSkillLevels.get(requiredSkill.getSkillId());
      if (Boolean.TRUE.equals(requiredSkill.getMandatory())) {
        if (candidateLevel == null) reasons.add("Missing required skill " + skills.findById(requiredSkill.getSkillId()).map(SkillEntity::getSkillName).orElse("#" + requiredSkill.getSkillId()));
        else if (requiredSkill.getMinimumLevel() != null && candidateLevel < levelRank(requiredSkill.getMinimumLevel())) reasons.add("Skill proficiency below the required level");
      }
    }
    entity.setEligibilityStatus(reasons.isEmpty() ? "ELIGIBLE" : "NOT_ELIGIBLE"); entity.setEligibilityReason(reasons.isEmpty() ? "Meets the configured job criteria" : String.join("; ", reasons));
    ApplicationEntity saved = applications.save(entity); history(saved, null, "APPLIED", "Application created", actor);
    log(actor, "APPLICATIONS", "CREATE", saved.getId(), candidate.getFullName() + " → " + job.getJobTitle());
    return applicationMap(saved, Map.of(candidate.getId(), candidate), Map.of(job.getId(), job), Map.of(job.getCompanyId(), require(companies.findById(job.getCompanyId()), "Company")));
  }
  public Map<String, Object> updateApplicationStatus(Integer id, StatusRequest request, String actor) {
    ApplicationEntity entity = require(applications.findById(id), "Application");
    String next = allowed(request.status(), Set.of("APPLIED", "ELIGIBILITY_VERIFIED", "SHORTLISTED", "INTERVIEW_SCHEDULED", "INTERVIEWED", "SELECTED", "REJECTED", "OFFERED", "JOINED", "WITHDRAWN"), "application status");
    if (!Objects.equals(entity.getApplicationStatus(), next)) {
      if (!validTransition(entity.getApplicationStatus(), next)) throw new IllegalArgumentException("The application cannot move from " + entity.getApplicationStatus() + " to " + next + ".");
      String previous = entity.getApplicationStatus(); entity.setApplicationStatus(next); applications.save(entity); history(entity, previous, next, request.remarks(), actor);
      log(actor, "APPLICATIONS", "STATUS_CHANGED", id, previous + " → " + next);
    }
    return applications().stream().filter(item -> Objects.equals(item.get("applicationId"), id)).findFirst().orElseThrow(() -> missing("Application"));
  }

  public List<Map<String, Object>> interviews(boolean upcomingOnly) {
    List<InterviewEntity> found = upcomingOnly ? interviews.findByScheduledDateGreaterThanEqualAndStatusOrderByScheduledDateAsc(LocalDate.now(), "SCHEDULED") : interviews.findAll();
    Map<Integer, ApplicationEntity> appIndex = applications.findAll().stream().collect(Collectors.toMap(ApplicationEntity::getId, Function.identity()));
    Map<Integer, CandidateEntity> candidateIndex = candidates.findAll().stream().collect(Collectors.toMap(CandidateEntity::getId, Function.identity()));
    Map<Integer, JobEntity> jobIndex = jobs.findAll().stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));
    return found.stream().map(item -> {
      ApplicationEntity app = appIndex.get(item.getApplicationId()); CandidateEntity candidate = app == null ? null : candidateIndex.get(app.getCandidateId()); JobEntity job = app == null ? null : jobIndex.get(app.getJobId());
      return map("interviewId", item.getId(), "applicationId", item.getApplicationId(), "candidateName", candidate == null ? "—" : candidate.getFullName(), "jobTitle", job == null ? "—" : job.getJobTitle(), "roundNumber", item.getRoundNumber(), "roundType", item.getRoundType(), "scheduledDate", item.getScheduledDate(), "scheduledTime", item.getScheduledTime(), "mode", item.getMode(), "interviewerName", item.getInterviewerName(), "meetingOrLocation", item.getMeetingOrLocation(), "status", item.getStatus());
    }).toList();
  }
  public Map<String, Object> rescheduleInterview(Integer id, InterviewRequest request, String actor) {
    InterviewEntity entity = require(interviews.findById(id), "Interview");
    require(applications.findById(request.applicationId()), "Application");
    if ((!Objects.equals(entity.getApplicationId(), request.applicationId()) || !Objects.equals(entity.getRoundNumber(), request.roundNumber())) && interviews.existsByApplicationIdAndRoundNumber(request.applicationId(), request.roundNumber())) throw new IllegalArgumentException("That interview round already exists for this application.");
    boolean moved = !Objects.equals(entity.getScheduledDate(), request.scheduledDate()) || !Objects.equals(entity.getScheduledTime(), request.scheduledTime());
    entity.setApplicationId(request.applicationId()); entity.setRoundNumber(request.roundNumber()); entity.setRoundType(request.roundType().trim()); entity.setScheduledDate(request.scheduledDate()); entity.setScheduledTime(request.scheduledTime());
    entity.setMode(allowed(request.mode(), Set.of("ONLINE", "OFFLINE", "TELEPHONIC"), "interview mode")); entity.setInterviewerName(clean(request.interviewerName())); entity.setMeetingOrLocation(clean(request.meetingOrLocation()));
    if (moved) entity.setStatus("RESCHEDULED");
    InterviewEntity saved = interviews.save(entity); log(actor, "INTERVIEWS", moved ? "RESCHEDULED" : "UPDATED", id, request.roundType());
    return interviews(false).stream().filter(row -> Objects.equals(row.get("interviewId"), saved.getId())).findFirst().orElseThrow();
  }
  public Map<String, Object> updateInterviewStatus(Integer id, StatusRequest request, String actor) {
    InterviewEntity interview = require(interviews.findById(id), "Interview");
    interview.setStatus(allowed(request.status(), Set.of("SCHEDULED", "COMPLETED", "RESCHEDULED", "CANCELLED", "NO_SHOW"), "interview status"));
    InterviewEntity saved = interviews.save(interview); log(actor, "INTERVIEWS", "STATUS_CHANGED", id, saved.getStatus());
    return interviews(false).stream().filter(row -> Objects.equals(row.get("interviewId"), id)).findFirst().orElseThrow();
  }
  public Map<String, Object> saveInterview(InterviewRequest request, String actor) {
    ApplicationEntity app = require(applications.findById(request.applicationId()), "Application");
    if ("REJECTED".equals(app.getApplicationStatus()) || "WITHDRAWN".equals(app.getApplicationStatus())) throw new IllegalArgumentException("Interviews cannot be scheduled for a rejected or withdrawn application.");
    if (interviews.existsByApplicationIdAndRoundNumber(app.getId(), request.roundNumber())) throw new IllegalArgumentException("That interview round already exists for this application.");
    InterviewEntity entity = new InterviewEntity(); entity.setApplicationId(app.getId()); entity.setRoundNumber(request.roundNumber()); entity.setRoundType(request.roundType().trim()); entity.setScheduledDate(request.scheduledDate()); entity.setScheduledTime(request.scheduledTime());
    entity.setMode(allowed(request.mode(), Set.of("ONLINE", "OFFLINE", "TELEPHONIC"), "interview mode")); entity.setInterviewerName(clean(request.interviewerName())); entity.setMeetingOrLocation(clean(request.meetingOrLocation()));
    InterviewEntity saved = interviews.save(entity);
    if (!Set.of("INTERVIEW_SCHEDULED", "INTERVIEWED", "SELECTED", "OFFERED", "JOINED").contains(app.getApplicationStatus())) updateApplicationStatus(app.getId(), new StatusRequest("INTERVIEW_SCHEDULED", "Interview scheduled"), actor);
    log(actor, "INTERVIEWS", "SCHEDULED", saved.getId(), request.roundType()); return interviews(false).stream().filter(row -> Objects.equals(row.get("interviewId"), saved.getId())).findFirst().orElseThrow();
  }
  public Map<String, Object> saveFeedback(Integer interviewId, FeedbackRequest request, String actor) {
    InterviewEntity interview = require(interviews.findById(interviewId), "Interview");
    if (feedback.existsByInterviewId(interviewId)) throw new IllegalArgumentException("Feedback has already been submitted for this interview.");
    InterviewFeedbackEntity entity = new InterviewFeedbackEntity(); entity.setInterviewId(interviewId); entity.setTechnicalRating(request.technicalRating()); entity.setCommunicationRating(request.communicationRating());
    entity.setProblemSolvingRating(request.problemSolvingRating()); entity.setOverallRating(request.overallRating()); entity.setRecommendation(allowed(request.recommendation(), Set.of("NEXT_ROUND", "SELECTED", "REJECTED", "ON_HOLD"), "recommendation")); entity.setRemarks(request.remarks()); entity.setSubmittedBy(userId(actor));
    InterviewFeedbackEntity saved = feedback.save(entity); interview.setStatus("COMPLETED"); interviews.save(interview);
    ApplicationEntity app = require(applications.findById(interview.getApplicationId()), "Application");
    if ("SELECTED".equals(saved.getRecommendation()) && Set.of("INTERVIEWED", "INTERVIEW_SCHEDULED").contains(app.getApplicationStatus())) updateApplicationStatus(app.getId(), new StatusRequest("SELECTED", "Selected based on interview feedback"), actor);
    if ("REJECTED".equals(saved.getRecommendation()) && !Set.of("REJECTED", "WITHDRAWN").contains(app.getApplicationStatus())) updateApplicationStatus(app.getId(), new StatusRequest("REJECTED", "Rejected based on interview feedback"), actor);
    log(actor, "INTERVIEWS", "FEEDBACK_SUBMITTED", interviewId, saved.getRecommendation());
    return map("feedbackId", saved.getId(), "interviewId", interviewId, "technicalRating", saved.getTechnicalRating(), "communicationRating", saved.getCommunicationRating(), "problemSolvingRating", saved.getProblemSolvingRating(), "overallRating", saved.getOverallRating(), "recommendation", saved.getRecommendation(), "remarks", saved.getRemarks());
  }

  public List<Map<String, Object>> offers() {
    Map<Integer, ApplicationEntity> appIndex = applications.findAll().stream().collect(Collectors.toMap(ApplicationEntity::getId, Function.identity()));
    Map<Integer, CandidateEntity> candidateIndex = candidates.findAll().stream().collect(Collectors.toMap(CandidateEntity::getId, Function.identity()));
    Map<Integer, JobEntity> jobIndex = jobs.findAll().stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));
    Map<Integer, CompanyEntity> companyIndex = companies.findAll().stream().collect(Collectors.toMap(CompanyEntity::getId, Function.identity()));
    return offers.findAll().stream().map(offer -> {
      ApplicationEntity app = appIndex.get(offer.getApplicationId()); JobEntity job = app == null ? null : jobIndex.get(app.getJobId()); CandidateEntity candidate = app == null ? null : candidateIndex.get(app.getCandidateId()); CompanyEntity company = job == null ? null : companyIndex.get(job.getCompanyId());
      return map("offerId", offer.getId(), "offerCode", offer.getOfferCode(), "applicationId", offer.getApplicationId(), "candidateName", candidate == null ? "—" : candidate.getFullName(), "companyName", company == null ? "—" : company.getCompanyName(), "designation", offer.getDesignation(), "offeredPackageLpa", offer.getOfferedPackageLpa(), "offerDate", offer.getOfferDate(), "expectedJoiningDate", offer.getExpectedJoiningDate(), "offerStatus", offer.getOfferStatus(), "remarks", offer.getRemarks());
    }).toList();
  }
  public Map<String, Object> saveOffer(OfferRequest request, String actor) {
    ApplicationEntity app = require(applications.findById(request.applicationId()), "Application");
    if (!"SELECTED".equals(app.getApplicationStatus())) throw new IllegalArgumentException("An offer can only be created for a selected candidate.");
    if (offers.existsByApplicationId(app.getId())) throw new IllegalArgumentException("An offer already exists for this application.");
    JobEntity job = require(jobs.findById(app.getJobId()), "Job");
    if (job.getMinPackageLpa() != null && request.offeredPackageLpa().compareTo(job.getMinPackageLpa()) < 0) throw new IllegalArgumentException("Offered package is below the job's minimum package.");
    if (job.getMaxPackageLpa() != null && request.offeredPackageLpa().compareTo(job.getMaxPackageLpa()) > 0) throw new IllegalArgumentException("Offered package exceeds the job's maximum package.");
    OfferEntity entity = new OfferEntity(); entity.setOfferCode(code("OFF")); entity.setApplicationId(app.getId()); entity.setDesignation(request.designation().trim()); entity.setOfferedPackageLpa(request.offeredPackageLpa()); entity.setOfferDate(request.offerDate()); entity.setExpectedJoiningDate(request.expectedJoiningDate()); entity.setRemarks(clean(request.remarks()));
    OfferEntity saved = offers.save(entity); updateApplicationStatus(app.getId(), new StatusRequest("OFFERED", "Offer " + saved.getOfferCode() + " created"), actor); log(actor, "OFFERS", "CREATED", saved.getId(), saved.getOfferCode());
    return offers().stream().filter(row -> Objects.equals(row.get("offerId"), saved.getId())).findFirst().orElseThrow();
  }
  public Map<String, Object> updateOfferStatus(Integer id, StatusRequest request, String actor) {
    OfferEntity offer = require(offers.findById(id), "Offer"); String status = allowed(request.status(), Set.of("OFFERED", "ACCEPTED", "REJECTED", "EXPIRED", "WITHDRAWN"), "offer status");
    if ("ACCEPTED".equals(status) && !"OFFERED".equals(offer.getOfferStatus())) throw new IllegalArgumentException("Only an offered offer can be accepted.");
    offer.setOfferStatus(status); offers.save(offer); log(actor, "OFFERS", "STATUS_CHANGED", id, status);
    if (Set.of("REJECTED", "WITHDRAWN").contains(status)) updateApplicationStatus(offer.getApplicationId(), new StatusRequest(status, "Offer " + status.toLowerCase(Locale.ROOT)), actor);
    return offers().stream().filter(row -> Objects.equals(row.get("offerId"), id)).findFirst().orElseThrow();
  }

  public List<Map<String, Object>> placements() {
    Map<Integer, CandidateEntity> candidateIndex = candidates.findAll().stream().collect(Collectors.toMap(CandidateEntity::getId, Function.identity()));
    Map<Integer, CompanyEntity> companyIndex = companies.findAll().stream().collect(Collectors.toMap(CompanyEntity::getId, Function.identity()));
    return placements.findAll().stream().map(item -> map("placementId", item.getId(), "candidateId", item.getCandidateId(), "candidateName", Optional.ofNullable(candidateIndex.get(item.getCandidateId())).map(CandidateEntity::getFullName).orElse("—"), "companyId", item.getCompanyId(), "companyName", Optional.ofNullable(companyIndex.get(item.getCompanyId())).map(CompanyEntity::getCompanyName).orElse("—"), "jobId", item.getJobId(), "offerId", item.getOfferId(), "designation", item.getDesignation(), "finalPackageLpa", item.getFinalPackageLpa(), "expectedJoiningDate", item.getExpectedJoiningDate(), "actualJoiningDate", item.getActualJoiningDate(), "joiningStatus", item.getJoiningStatus(), "placementStatus", item.getPlacementStatus())).toList();
  }
  public Map<String, Object> savePlacement(PlacementRequest request, String actor) {
    OfferEntity offer = require(offers.findById(request.offerId()), "Offer");
    if (!"ACCEPTED".equals(offer.getOfferStatus())) throw new IllegalArgumentException("Only an accepted offer can become a placement.");
    if (placements.existsByOfferId(offer.getId())) throw new IllegalArgumentException("This offer already has a placement record.");
    ApplicationEntity app = require(applications.findById(offer.getApplicationId()), "Application"); JobEntity job = require(jobs.findById(app.getJobId()), "Job");
    PlacementEntity entity = new PlacementEntity(); entity.setOfferId(offer.getId()); entity.setCandidateId(app.getCandidateId()); entity.setCompanyId(job.getCompanyId()); entity.setJobId(job.getId()); entity.setDesignation(request.designation().trim()); entity.setFinalPackageLpa(request.finalPackageLpa()); entity.setExpectedJoiningDate(request.expectedJoiningDate() == null ? offer.getExpectedJoiningDate() : request.expectedJoiningDate());
    PlacementEntity saved = placements.save(entity); CandidateEntity candidate = require(candidates.findById(app.getCandidateId()), "Candidate"); candidate.setPlacementStatus("PLACED"); candidates.save(candidate);
    if (!"JOINED".equals(app.getApplicationStatus())) updateApplicationStatus(app.getId(), new StatusRequest("JOINED", "Placement record created"), actor);
    log(actor, "PLACEMENTS", "CREATED", saved.getId(), saved.getDesignation()); return placements().stream().filter(row -> Objects.equals(row.get("placementId"), saved.getId())).findFirst().orElseThrow();
  }
  public Map<String, Object> updatePlacementStatus(Integer id, StatusRequest request, String actor) {
    PlacementEntity entity = require(placements.findById(id), "Placement"); String status = allowed(request.status(), Set.of("PENDING", "JOINED", "NOT_JOINED", "DEFERRED", "NO_SHOW"), "joining status");
    entity.setJoiningStatus(status); if ("JOINED".equals(status) && entity.getActualJoiningDate() == null) entity.setActualJoiningDate(LocalDate.now());
    PlacementEntity saved = placements.save(entity); log(actor, "PLACEMENTS", "JOINING_STATUS_CHANGED", id, status);
    if (Set.of("NOT_JOINED", "NO_SHOW").contains(status)) { CandidateEntity candidate = require(candidates.findById(entity.getCandidateId()), "Candidate"); candidate.setPlacementStatus("NOT_PLACED"); candidates.save(candidate); }
    return placements().stream().filter(row -> Objects.equals(row.get("placementId"), saved.getId())).findFirst().orElseThrow();
  }

  public List<Map<String, Object>> roleOptions() { return roles.findAll().stream().filter(role -> Boolean.TRUE.equals(role.getActive())).map(role -> map("roleId", role.getId(), "roleCode", role.getRoleCode(), "roleName", role.getRoleName())).toList(); }
  public List<Map<String, Object>> users() { return users.findAll().stream().map(this::userMap).toList(); }
  public Map<String, Object> updateUserStatus(Integer id, StatusRequest request, String actor) {
    UserEntity user = require(users.findById(id), "User");
    user.setStatus(allowed(request.status(), Set.of("ACTIVE", "INACTIVE", "LOCKED"), "user status"));
    UserEntity saved = users.save(user); log(actor, "USERS", "STATUS_CHANGED", id, saved.getStatus()); return userMap(saved);
  }
  public Map<String, Object> saveUser(UserRequest request, Integer id, String actor) {
    UserEntity entity = id == null ? new UserEntity() : users.findById(id).orElseThrow(() -> missing("User")); boolean create = id == null;
    entity.setUsername(request.username().trim()); entity.setFullName(request.fullName().trim()); entity.setEmail(clean(request.email())); entity.setRole(require(roles.findById(request.roleId()), "Role"));
    if (request.password() != null && !request.password().isBlank()) entity.setPasswordHash(passwordEncoder.encode(request.password())); else if (create) throw new IllegalArgumentException("A password is required for a new user.");
    if (request.status() != null) entity.setStatus(allowed(request.status(), Set.of("ACTIVE", "INACTIVE", "LOCKED"), "user status"));
    UserEntity saved = users.save(entity); log(actor, "USERS", create ? "CREATE" : "UPDATE", saved.getId(), saved.getUsername()); return userMap(saved);
  }

  public Map<String, Object> dashboardSummary() {
    return map("candidates", candidates.count(), "companies", companies.count(), "openJobs", jobs.countByStatus("OPEN"), "applications", applications.count(), "interviews", interviews.count(), "offers", offers.count(), "placements", placements.count());
  }
  public List<Map<String, Object>> recentActivity() { return activity.findTop100ByOrderByLoggedAtDesc().stream().map(item -> map("logId", item.getId(), "userName", Optional.ofNullable(item.getUserId()).flatMap(users::findById).map(UserEntity::getFullName).orElse("System"), "moduleName", item.getModuleName(), "actionName", item.getActionName(), "recordId", item.getRecordId(), "description", item.getDescription(), "loggedAt", item.getLoggedAt())).toList(); }
  public Map<String, Object> dashboardAnalytics(int months) {
    int span = Math.max(3, Math.min(months, 12)); YearMonth current = YearMonth.now(); YearMonth first = current.minusMonths(span - 1L);
    Map<YearMonth, Long> monthly = applications.findAll().stream().filter(app -> app.getAppliedAt() != null).map(app -> YearMonth.from(app.getAppliedAt())).filter(month -> !month.isBefore(first) && !month.isAfter(current)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    List<Map<String, Object>> trend = new ArrayList<>(); for (int i = 0; i < span; i++) { YearMonth month = first.plusMonths(i); trend.add(map("month", month.format(DateTimeFormatter.ofPattern("MMM")), "count", monthly.getOrDefault(month, 0L))); }
    Map<String, Long> funnelCounts = applications.findAll().stream().collect(Collectors.groupingBy(ApplicationEntity::getApplicationStatus, Collectors.counting()));
    List<String> order = List.of("APPLIED", "ELIGIBILITY_VERIFIED", "SHORTLISTED", "INTERVIEW_SCHEDULED", "INTERVIEWED", "SELECTED", "OFFERED", "JOINED");
    List<Map<String, Object>> funnel = order.stream().map(stage -> map("stage", stage.replace('_', ' '), "count", funnelCounts.getOrDefault(stage, 0L))).toList();
    List<Map<String, Object>> technologies = candidates.findAll().stream().collect(Collectors.groupingBy(c -> c.getPrimaryTechnology() == null || c.getPrimaryTechnology().isBlank() ? "Unspecified" : c.getPrimaryTechnology(), Collectors.counting())).entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(7).map(e -> map("label", e.getKey(), "technology", e.getKey(), "count", e.getValue())).toList();
    List<Map<String, Object>> openJobsByCompany = groupJobsByCompany();
    List<Map<String, Object>> placementByCompany = groupPlacementsByCompany();
    List<Map<String, Object>> placementByTechnology = groupPlacementsByTechnology();
    List<Map<String, Object>> recruiterPerformance = groupRecruiterPerformance();
    List<Map<String, Object>> jobAging = groupJobAging();
    long joined = placements.countByJoiningStatus("JOINED");
    double conversion = offers.count() == 0 ? 0 : Math.round((joined * 1000.0 / offers.count())) / 10.0;
    List<Map<String, Object>> byStatus = applications.findAll().stream().collect(Collectors.groupingBy(ApplicationEntity::getApplicationStatus, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> map("label", e.getKey().replace('_', ' '), "count", e.getValue())).toList();
    long pendingInterviews = interviews.findByScheduledDateGreaterThanEqualAndStatusOrderByScheduledDateAsc(LocalDate.now(), "SCHEDULED").size();
    return map("applicationsByMonth", trend, "recruitmentFunnel", funnel, "candidateTechnology", technologies, "candidatesByTechnology", technologies, "jobsByCompany", openJobsByCompany, "placementsByCompany", placementByCompany, "placementsByTechnology", placementByTechnology, "recruiterPerformance", recruiterPerformance, "jobAging", jobAging, "applicationsByStatus", byStatus, "pendingInterviews", pendingInterviews, "offerToJoiningConversion", conversion);
  }

  private List<Map<String, Object>> groupJobsByCompany() {
    Map<Integer, CompanyEntity> index = companies.findAll().stream().collect(Collectors.toMap(CompanyEntity::getId, Function.identity()));
    return jobs.findByStatusOrderByCreatedAtDesc("OPEN").stream().collect(Collectors.groupingBy(JobEntity::getCompanyId, Collectors.summingInt(JobEntity::getOpenings))).entrySet().stream().map(e -> map("label", Optional.ofNullable(index.get(e.getKey())).map(CompanyEntity::getCompanyName).orElse("Unknown"), "count", e.getValue())).sorted(Comparator.comparingLong(e -> -((Number)e.get("count")).longValue())).limit(7).toList();
  }
  private List<Map<String, Object>> groupPlacementsByCompany() {
    Map<Integer, CompanyEntity> index = companies.findAll().stream().collect(Collectors.toMap(CompanyEntity::getId, Function.identity()));
    return placements.findAll().stream().collect(Collectors.groupingBy(PlacementEntity::getCompanyId, Collectors.counting())).entrySet().stream().map(e -> map("label", Optional.ofNullable(index.get(e.getKey())).map(CompanyEntity::getCompanyName).orElse("Unknown"), "count", e.getValue())).sorted(Comparator.comparingLong(e -> -((Number)e.get("count")).longValue())).limit(7).toList();
  }
  private List<Map<String, Object>> groupPlacementsByTechnology() {
    Map<Integer, CandidateEntity> index = candidates.findAll().stream().collect(Collectors.toMap(CandidateEntity::getId, Function.identity()));
    return placements.findAll().stream().collect(Collectors.groupingBy(p -> {
      CandidateEntity c = index.get(p.getCandidateId());
      return (c != null && c.getPrimaryTechnology() != null && !c.getPrimaryTechnology().isBlank()) ? c.getPrimaryTechnology() : "Unspecified";
    }, Collectors.counting())).entrySet().stream().map(e -> map("label", e.getKey(), "technology", e.getKey(), "count", e.getValue())).sorted(Comparator.comparingLong(e -> -((Number)e.get("count")).longValue())).limit(7).toList();
  }
  private List<Map<String, Object>> groupRecruiterPerformance() {
    Map<Integer, UserEntity> index = users.findAll().stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
    return applications.findAll().stream().filter(a -> a.getAssignedRecruiterId() != null).collect(Collectors.groupingBy(ApplicationEntity::getAssignedRecruiterId, Collectors.counting())).entrySet().stream().map(e -> map("label", Optional.ofNullable(index.get(e.getKey())).map(UserEntity::getFullName).orElse("Unknown"), "count", e.getValue())).sorted(Comparator.comparingLong(e -> -((Number)e.get("count")).longValue())).limit(7).toList();
  }
  private List<Map<String, Object>> groupJobAging() {
    LocalDate today = LocalDate.now(); Map<String, Long> bins = new LinkedHashMap<>(); bins.put("0–7 days", 0L); bins.put("8–14 days", 0L); bins.put("15–30 days", 0L); bins.put("31+ days", 0L);
    for (JobEntity job : jobs.findByStatusOrderByCreatedAtDesc("OPEN")) { long days = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(job.getPostedDate(), today)); String bucket = days <= 7 ? "0–7 days" : days <= 14 ? "8–14 days" : days <= 30 ? "15–30 days" : "31+ days"; bins.compute(bucket, (key, value) -> value + 1); }
    return bins.entrySet().stream().map(e -> map("label", e.getKey(), "count", e.getValue())).toList();
  }

  private Map<String, Object> candidateMap(CandidateEntity c) { return map("candidateId", c.getId(), "candidateCode", c.getCandidateCode(), "fullName", c.getFullName(), "email", c.getEmail(), "mobile", c.getMobile(), "dateOfBirth", c.getDateOfBirth(), "gender", c.getGender(), "qualification", c.getQualification(), "college", c.getCollege(), "university", c.getUniversity(), "passingYear", c.getPassingYear(), "percentage", c.getPercentage(), "cgpa", c.getCgpa(), "backlogCount", c.getBacklogCount(), "primaryTechnology", c.getPrimaryTechnology(), "currentLocation", c.getCurrentLocation(), "preferredLocation", c.getPreferredLocation(), "profileStatus", c.getProfileStatus(), "placementStatus", c.getPlacementStatus()); }
  private Map<String, Object> companyMap(CompanyEntity c) { return map("companyId", c.getId(), "companyCode", c.getCompanyCode(), "companyName", c.getCompanyName(), "industry", c.getIndustry(), "website", c.getWebsite(), "location", c.getLocation(), "companyType", c.getCompanyType(), "hrName", c.getHrName(), "hrEmail", c.getHrEmail(), "hrMobile", c.getHrMobile(), "status", c.getStatus()); }
  private Map<String, Object> skillMap(SkillEntity s) { return map("skillId", s.getId(), "skillName", s.getSkillName(), "category", s.getCategory(), "isActive", Boolean.TRUE.equals(s.getActive()) ? "ACTIVE" : "INACTIVE"); }
  private Map<String, Object> jobMap(JobEntity j, Map<Integer, CompanyEntity> companyIndex) { return map("jobId", j.getId(), "jobCode", j.getJobCode(), "companyId", j.getCompanyId(), "companyName", Optional.ofNullable(companyIndex.get(j.getCompanyId())).map(CompanyEntity::getCompanyName).orElse("—"), "jobTitle", j.getJobTitle(), "employmentType", j.getEmploymentType(), "location", j.getLocation(), "openings", j.getOpenings(), "minPackageLpa", j.getMinPackageLpa(), "maxPackageLpa", j.getMaxPackageLpa(), "minPercentage", j.getMinPercentage(), "minPassingYear", j.getMinPassingYear(), "maxPassingYear", j.getMaxPassingYear(), "maxBacklogs", j.getMaxBacklogs(), "qualificationRule", j.getQualificationRule(), "postedDate", j.getPostedDate(), "applicationDeadline", j.getApplicationDeadline(), "status", j.getStatus()); }
  private Map<String, Object> applicationMap(ApplicationEntity a, Map<Integer, CandidateEntity> candidateIndex, Map<Integer, JobEntity> jobIndex, Map<Integer, CompanyEntity> companyIndex) {
    CandidateEntity c = candidateIndex.get(a.getCandidateId()); JobEntity j = jobIndex.get(a.getJobId()); CompanyEntity company = j == null ? null : companyIndex.get(j.getCompanyId());
    return map("applicationId", a.getId(), "applicationCode", a.getApplicationCode(), "candidateId", a.getCandidateId(), "candidateName", c == null ? "—" : c.getFullName(), "jobId", a.getJobId(), "jobTitle", j == null ? "—" : j.getJobTitle(), "companyName", company == null ? "—" : company.getCompanyName(), "appliedAt", a.getAppliedAt(), "eligibilityStatus", a.getEligibilityStatus(), "eligibilityReason", a.getEligibilityReason(), "applicationStatus", a.getApplicationStatus(), "source", a.getSource(), "assignedRecruiterId", a.getAssignedRecruiterId());
  }
  private Map<String, Object> userMap(UserEntity u) { return map("userId", u.getId(), "username", u.getUsername(), "fullName", u.getFullName(), "email", u.getEmail(), "roleId", u.getRole().getId(), "roleName", u.getRole().getRoleName(), "status", u.getStatus()); }
  private int levelRank(String value) { return switch (value == null ? "" : value.toUpperCase(Locale.ROOT)) { case "BASIC" -> 1; case "INTERMEDIATE" -> 2; case "ADVANCED" -> 3; default -> 0; }; }
  private boolean validTransition(String from, String to) {
    if (Set.of("REJECTED", "WITHDRAWN", "JOINED").contains(from)) return false;
    Map<String, Set<String>> allowed = Map.of("APPLIED", Set.of("ELIGIBILITY_VERIFIED", "SHORTLISTED", "INTERVIEW_SCHEDULED", "REJECTED", "WITHDRAWN"), "ELIGIBILITY_VERIFIED", Set.of("SHORTLISTED", "INTERVIEW_SCHEDULED", "REJECTED", "WITHDRAWN"), "SHORTLISTED", Set.of("INTERVIEW_SCHEDULED", "INTERVIEWED", "REJECTED", "WITHDRAWN"), "INTERVIEW_SCHEDULED", Set.of("INTERVIEWED", "SELECTED", "REJECTED", "WITHDRAWN"), "INTERVIEWED", Set.of("SELECTED", "REJECTED", "WITHDRAWN"), "SELECTED", Set.of("OFFERED", "REJECTED", "WITHDRAWN"), "OFFERED", Set.of("JOINED", "REJECTED", "WITHDRAWN"));
    return allowed.getOrDefault(from, Set.of()).contains(to);
  }
  private void history(ApplicationEntity app, String oldStatus, String next, String remarks, String actor) { ApplicationHistoryEntity row = new ApplicationHistoryEntity(); row.setApplicationId(app.getId()); row.setOldStatus(oldStatus); row.setNewStatus(next); row.setRemarks(clean(remarks)); row.setChangedBy(userId(actor)); history.save(row); }
  private void log(String actor, String module, String action, Object id, String description) { ActivityLogEntity row = new ActivityLogEntity(); row.setUserId(actor == null ? null : userId(actor)); row.setModuleName(module); row.setActionName(action); row.setRecordId(id == null ? null : String.valueOf(id)); row.setDescription(description); activity.save(row); }
  private Integer userId(String username) { return username == null ? null : users.findByUsername(username).map(UserEntity::getId).orElse(null); }
  private String code(String prefix) { return prefix + "-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT); }
  private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
  private String allowed(String value, Set<String> options, String label) { String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT); if (!options.contains(normalized)) throw new IllegalArgumentException("Invalid " + label + "."); return normalized; }
  private <T> T require(Optional<T> value, String name) { return value.orElseThrow(() -> missing(name)); }
  private EntityNotFoundException missing(String name) { return new EntityNotFoundException(name + " not found."); }
  private Map<String, Object> map(Object... pairs) { Map<String, Object> result = new LinkedHashMap<>(); for (int i = 0; i < pairs.length; i += 2) result.put((String)pairs[i], pairs[i + 1]); return result; }
}
