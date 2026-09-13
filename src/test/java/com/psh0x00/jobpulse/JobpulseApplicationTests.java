package com.psh0x00.jobpulse;

import com.psh0x00.jobpulse.dto.ApplicationRequest;
import com.psh0x00.jobpulse.dto.RegisterRequest;
import com.psh0x00.jobpulse.dto.LoginRequest;
import com.psh0x00.jobpulse.dto.ContactRequest;
import com.psh0x00.jobpulse.dto.TagRequest;
import com.psh0x00.jobpulse.dto.ReminderRequest;
import com.psh0x00.jobpulse.model.enums.JobType;
import com.psh0x00.jobpulse.model.enums.InterviewType;
import com.psh0x00.jobpulse.model.enums.RelationshipType;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
class JobpulseApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());

	@Container
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("jobpulse_test")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@TestConfiguration
	static class CacheTestConfig{
		@Bean
		public CacheManager cacheManager() {
			return new ConcurrentMapCacheManager("dashboardStats");
		}
	}

	@Test
	void contextLoads() {
	}

	// ==================== AUTH TESTS ====================

	@Test
	void shouldRegisterUser() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setName("Integration Test User");
		request.setEmail("integration@test.com");
		request.setPassword("password123");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
	}

	@Test
	void shouldLoginAfterRegister() throws Exception {
		// First register
		getValidJwtToken();

		// Then login with the same credentials
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("integration@test.com");
		loginRequest.setPassword("password123");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest))
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
	}

	@Test
	void shouldGetCurrentUserProfile() throws Exception {
		String token = getValidJwtToken();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/me")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("integration@test.com"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Integration Test User"));
	}

	// ==================== APPLICATION TESTS ====================

	@Test
	void shouldCreateApplication() throws Exception {
		String token = getValidJwtToken();

		ApplicationRequest applicationRequest = new ApplicationRequest();
		applicationRequest.setCompanyName("Integration Test Company");
		applicationRequest.setRoleTitle("Integration Test Role");
		applicationRequest.setJobType(JobType.FULL_TIME);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/applications")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(applicationRequest))
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value("Integration Test Company"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.roleTitle").value("Integration Test Role"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.jobType").value("FULL_TIME"));
	}

	@Test
	void shouldListApplications() throws Exception {
		String token = getValidJwtToken();

		// Create an application first
		createApplicationAndGetId(token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/applications")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].companyName").value("Integration Test Company"));
	}

	@Test
	void shouldUpdateApplication() throws Exception {
		String token = getValidJwtToken();
		Long applicationId = createApplicationAndGetId(token);

		ApplicationRequest updatedRequest = new ApplicationRequest();
		updatedRequest.setCompanyName("Updated Company");
		updatedRequest.setRoleTitle("Updated Role");
		updatedRequest.setJobType(JobType.REMOTE);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/applications/" + applicationId)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedRequest))
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.roleTitle").value("Updated Role"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.jobType").value("REMOTE"));
	}

	@Test
	void shouldDeleteApplication() throws Exception {
		String token = getValidJwtToken();
		Long applicationId = createApplicationAndGetId(token);

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/applications/" + applicationId)
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	void shouldUpdateApplicationStatus() throws Exception {
		String token = getValidJwtToken();
		Long applicationId = createApplicationAndGetId(token);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/applications/" + applicationId + "/status")
				.header("Authorization", "Bearer " + token)
				.param("newStatus", "APPLIED")
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.applicationStatus").value("APPLIED"));
	}

	// ==================== COMPANY TESTS ====================

	@Test
	void shouldListCompanies() throws Exception {
		String token = getValidJwtToken();

		// Creating an application auto-creates a company
		createApplicationAndGetId(token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/companies")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Integration Test Company"));
	}

	// ==================== TAG TESTS ====================

	@Test
	void shouldCreateTag() throws Exception {
		String token = getValidJwtToken();

		TagRequest tagRequest = new TagRequest();
		tagRequest.setName("Urgent");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tags")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tagRequest))
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Urgent"));
	}

	@Test
	void shouldListTags() throws Exception {
		String token = getValidJwtToken();

		// Create a tag first
		TagRequest tagRequest = new TagRequest();
		tagRequest.setName("Remote");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tags")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tagRequest))
		).andExpect(MockMvcResultMatchers.status().isOk());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tags")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Remote"));
	}

	// ==================== CONTACT TESTS ====================

	@Test
	void shouldCreateContact() throws Exception {
		String token = getValidJwtToken();

		// Create an application first (auto-creates a company, we need the companyId)
		Long applicationId = createApplicationAndGetId(token);

		// Get the company ID from the companies list
		MvcResult companiesResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/companies")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		Long companyId = objectMapper.readTree(companiesResult.getResponse().getContentAsString())
				.get("content").get(0).get("id").asLong();

		ContactRequest contactRequest = new ContactRequest();
		contactRequest.setName("John Recruiter");
		contactRequest.setCompanyId(companyId);
		contactRequest.setRole("HR Manager");
		contactRequest.setEmail("john@company.com");
		contactRequest.setRelationshipType(RelationshipType.COLD);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/contacts")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(contactRequest))
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Recruiter"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.role").value("HR Manager"));
	}

	@Test
	void shouldListContacts() throws Exception {
		String token = getValidJwtToken();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/contacts")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
	}

	// ==================== INTERVIEW TESTS ====================

	@Test
	void shouldCreateInterview() throws Exception {
		String token = getValidJwtToken();
		Long applicationId = createApplicationAndGetId(token);

		String interviewJson = """
				{
					"scheduledAt": "%s",
					"interviewType": "TECHNICAL",
					"locationOrLink": "https://meet.google.com/abc",
					"notes": "Prepare system design"
				}
				""".formatted(LocalDateTime.now().plusDays(7).toString());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/applications/" + applicationId + "/interviews")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(interviewJson)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.interviewType").value("TECHNICAL"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.locationOrLink").value("https://meet.google.com/abc"));
	}

	@Test
	void shouldListInterviewsForApplication() throws Exception {
		String token = getValidJwtToken();
		Long applicationId = createApplicationAndGetId(token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/applications/" + applicationId + "/interviews")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
	}

	// ==================== REMINDER TESTS ====================

	@Test
	void shouldCreateReminder() throws Exception {
		String token = getValidJwtToken();
		Long applicationId = createApplicationAndGetId(token);

		String reminderJson = """
				{
					"applicationId": %d,
					"actionDescription": "Follow up with recruiter",
					"remindAt": "%s"
				}
				""".formatted(applicationId, LocalDateTime.now().plusDays(3).toString());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reminders")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reminderJson)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.actionDescription").value("Follow up with recruiter"));
	}

	@Test
	void shouldListReminders() throws Exception {
		String token = getValidJwtToken();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reminders")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
	}

	// ==================== DASHBOARD TESTS ====================

	@Test
	void shouldGetDashboardStats() throws Exception {
		String token = getValidJwtToken();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dashboard/stats")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalApplications").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalInterviews").exists());
	}

	// ==================== ACTUATOR TESTS ====================

	@Test
	void shouldReturnActuatorHealthUp() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UP"));
	}

	// ==================== HELPER METHODS ====================

	private String getValidJwtToken() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setName("Integration Test User");
		registerRequest.setEmail("integration@test.com");
		registerRequest.setPassword("password123");

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest))
		).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();
		return objectMapper.readTree(jsonResponse).get("token").asText();
	}

	private Long createApplicationAndGetId(String token) throws Exception {
		ApplicationRequest applicationRequest = new ApplicationRequest();
		applicationRequest.setCompanyName("Integration Test Company");
		applicationRequest.setRoleTitle("Integration Test Role");
		applicationRequest.setJobType(JobType.FULL_TIME);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/applications")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(applicationRequest))
		).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id").asLong();
	}
}

