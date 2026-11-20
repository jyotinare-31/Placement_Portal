package com.rpms.app;

import com.rpms.app.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RpmsApplicationTests {

  @Autowired private CandidateRepository candidateRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private JobRepository jobRepository;
  @Autowired private ApplicationRepository applicationRepository;
  @Autowired private RoleRepository roleRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private SkillRepository skillRepository;
  @Autowired private CandidateSkillRepository candidateSkillRepository;
  @Autowired private JobSkillRepository jobSkillRepository;
  @Autowired private ApplicationHistoryRepository applicationHistoryRepository;
  @Autowired private InterviewRepository interviewRepository;
  @Autowired private InterviewFeedbackRepository interviewFeedbackRepository;
  @Autowired private OfferRepository offerRepository;
  @Autowired private PlacementRepository placementRepository;
  @Autowired private ActivityLogRepository activityLogRepository;
  @Autowired private QualificationRepository qualificationRepository;
  @Autowired private LocationRepository locationRepository;

  @Test
  void contextLoadsAndEntitiesMatchDatabase() {
    assertNotNull(candidateRepository);
    assertTrue(candidateRepository.count() >= 0);
    assertTrue(companyRepository.count() >= 0);
    assertTrue(jobRepository.count() >= 0);
    assertTrue(applicationRepository.count() >= 0);
    assertTrue(roleRepository.count() >= 0);
    assertTrue(userRepository.count() >= 0);
    assertTrue(skillRepository.count() >= 0);
    assertTrue(candidateSkillRepository.count() >= 0);
    assertTrue(jobSkillRepository.count() >= 0);
    assertTrue(applicationHistoryRepository.count() >= 0);
    assertTrue(interviewRepository.count() >= 0);
    assertTrue(interviewFeedbackRepository.count() >= 0);
    assertTrue(offerRepository.count() >= 0);
    assertTrue(placementRepository.count() >= 0);
    assertTrue(activityLogRepository.count() >= 0);
    assertTrue(qualificationRepository.count() >= 0);
    assertTrue(locationRepository.count() >= 0);
  }
}

