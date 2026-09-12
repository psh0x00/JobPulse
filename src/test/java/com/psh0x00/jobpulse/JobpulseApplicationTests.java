package com.psh0x00.jobpulse;

import com.psh0x00.jobpulse.dto.ApplicationRequest;
import com.psh0x00.jobpulse.dto.RegisterRequest;
import com.psh0x00.jobpulse.model.enums.JobType;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
class JobpulseApplicationTests {

	@Autowired
	private MockMvc mockMvc;

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

	@Test
	void shouldCreateUserAndSaveToRealDatabase() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

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
	void shouldCreateApplication() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
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
	void shouldGetDashboardStats() throws Exception {
		String token = getValidJwtToken();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dashboard/stats")
				.header("Authorization", "Bearer " + token)
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalApplications").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.totalInterviews").exists());
	}
	
	private String getValidJwtToken() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setName("Integration Test User");
		registerRequest.setEmail("integration@test.com");
		registerRequest.setPassword("password123");

		var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest))
		).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String jsonResponse = result.getResponse().getContentAsString();
		return objectMapper.readTree(jsonResponse).get("token").asText();
	}
}
