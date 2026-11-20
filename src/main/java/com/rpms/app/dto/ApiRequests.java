package com.rpms.app.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public final class ApiRequests {
  private ApiRequests() {}

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
  public record CandidateRequest(
      @NotBlank @Size(max = 150) String fullName,
      @NotBlank @Email @Size(max = 150) String email,
      @NotBlank @Size(max = 20) String mobile,
      LocalDate dateOfBirth, String gender,
      @NotBlank @Size(max = 80) String qualification,
      String college, String university,
      @NotNull @Min(1980) @Max(2200) Integer passingYear,
      @DecimalMin("0") @DecimalMax("100") BigDecimal percentage,
      @DecimalMin("0") @DecimalMax("10") BigDecimal cgpa,
      @Min(0) Integer backlogCount, String primaryTechnology,
      String currentLocation, String preferredLocation, String profileStatus) {}

  public record CompanyRequest(@NotBlank @Size(max = 180) String companyName, String industry, String website, String location, String companyType, String hrName, @Email String hrEmail, String hrMobile, String status) {}
  public record SkillRequest(@NotBlank @Size(max = 100) String skillName, String category, Boolean active) {}
  public record CandidateSkillRequest(@NotNull @Positive Integer skillId, String proficiencyLevel, @DecimalMin("0") BigDecimal yearsExperience, Boolean primary) {}
  public record JobSkillRequest(@NotNull @Positive Integer skillId, String minimumLevel, Boolean mandatoryFlag) {}
  public record JobRequest(@NotNull @Positive Integer companyId, @NotBlank @Size(max = 150) String jobTitle, String employmentType, @NotBlank String location, @NotNull @Positive Integer openings, @DecimalMin("0") BigDecimal minPackageLpa, @DecimalMin("0") BigDecimal maxPackageLpa, @DecimalMin("0") @DecimalMax("100") BigDecimal minPercentage, Integer minPassingYear, Integer maxPassingYear, @Min(0) Integer maxBacklogs, String qualificationRule, @NotNull LocalDate postedDate, @NotNull LocalDate applicationDeadline, String status) {}
  public record ApplicationRequest(@NotNull @Positive Integer candidateId, @NotNull @Positive Integer jobId, String source, Integer assignedRecruiterId) {}
  public record StatusRequest(@NotBlank String status, String remarks) {}
  public record InterviewRequest(@NotNull @Positive Integer applicationId, @NotNull @Positive Integer roundNumber, @NotBlank String roundType, @NotNull LocalDate scheduledDate, LocalTime scheduledTime, @NotBlank String mode, String interviewerName, String meetingOrLocation) {}
  public record FeedbackRequest(@DecimalMin("0") @DecimalMax("5") BigDecimal technicalRating, @DecimalMin("0") @DecimalMax("5") BigDecimal communicationRating, @DecimalMin("0") @DecimalMax("5") BigDecimal problemSolvingRating, @DecimalMin("0") @DecimalMax("5") BigDecimal overallRating, @NotBlank String recommendation, String remarks) {}
  public record OfferRequest(@NotNull @Positive Integer applicationId, @NotBlank String designation, @NotNull @DecimalMin("0") BigDecimal offeredPackageLpa, @NotNull LocalDate offerDate, LocalDate expectedJoiningDate, String remarks) {}
  public record PlacementRequest(@NotNull @Positive Integer offerId, @NotBlank String designation, @NotNull @DecimalMin("0") BigDecimal finalPackageLpa, LocalDate expectedJoiningDate) {}
  public record UserRequest(@NotBlank @Size(max = 80) String username, @NotBlank @Size(max = 120) String fullName, @Email String email, @Size(min = 12, max = 100) String password, @NotNull @Positive Integer roleId, String status) {}
}
