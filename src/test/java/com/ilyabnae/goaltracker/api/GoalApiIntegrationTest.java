package com.ilyabnae.goaltracker.api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyabnae.goaltracker.api.dto.AdminReviewRequest;
import com.ilyabnae.goaltracker.api.dto.CreateGoalRequest;
import com.ilyabnae.goaltracker.api.dto.UpdateGoalRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GoalApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void goals_withoutJwt_returns401() throws Exception {
		mockMvc.perform(get("/api/v1/goals")).andExpect(status().isUnauthorized());
	}

	@Test
	void profile_and_goals_happyPath() throws Exception {
		var alice = jwt().jwt(j -> j.subject("integration-alice").claim("name", "Alice"));

		mockMvc.perform(get("/api/v1/me").with(alice)).andExpect(status().isOk()).andExpect(jsonPath("$.displayName").value("Alice"));

		String body = objectMapper.writeValueAsString(new CreateGoalRequest("Pass exam", "Study spring", null, null));
		String created = mockMvc
				.perform(post("/api/v1/goals").with(alice).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value("Pass exam"))
				.andExpect(jsonPath("$.approvalStatus").value("PENDING"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());

		mockMvc.perform(get("/api/v1/goals").with(alice)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));

		String update = objectMapper.writeValueAsString(new UpdateGoalRequest("Pass exam (done)", null, null, null));
		mockMvc.perform(put("/api/v1/goals/" + id).with(alice).contentType(MediaType.APPLICATION_JSON).content(update))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Pass exam (done)"));

		mockMvc.perform(delete("/api/v1/goals/" + id).with(alice)).andExpect(status().isNoContent());

		mockMvc.perform(get("/api/v1/goals").with(alice)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void createGoal_invalidTitle_returns400() throws Exception {
		var jwt = jwt().jwt(j -> j.subject("bad-req-user"));
		String body = objectMapper.writeValueAsString(Map.of("title", ""));
		mockMvc.perform(post("/api/v1/goals").with(jwt).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getForeignGoal_returns404() throws Exception {
		var alice = jwt().jwt(j -> j.subject("owner-user").claim("name", "A"));
		var bob = jwt().jwt(j -> j.subject("other-user").claim("name", "B"));

		String body = objectMapper.writeValueAsString(new CreateGoalRequest("Secret", null, null, null));
		String created = mockMvc
				.perform(post("/api/v1/goals").with(alice).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());

		mockMvc.perform(get("/api/v1/goals/" + id).with(bob)).andExpect(status().isNotFound());
	}

	@Test
	void getMissingGoal_returns404() throws Exception {
		var jwt = jwt().jwt(j -> j.subject("lonely-user"));
		UUID random = UUID.randomUUID();
		mockMvc.perform(get("/api/v1/goals/" + random).with(jwt)).andExpect(status().isNotFound());
	}

	@Test
	void adminPending_withoutAdminRole_returns403() throws Exception {
		var userJwt = jwt()
				.jwt(j -> j.subject("plain-user").claim("roles", List.of("ROLE_USER")))
				.authorities(new SimpleGrantedAuthority("ROLE_USER"));
		mockMvc.perform(get("/api/v1/admin/goals/pending").with(userJwt)).andExpect(status().isForbidden());
	}

	@Test
	void admin_canListPendingAndApproveGoal() throws Exception {
		var alice = jwt()
				.jwt(j -> j.subject("admin-flow-alice").claim("name", "Alice").claim("roles", List.of("ROLE_USER")))
				.authorities(new SimpleGrantedAuthority("ROLE_USER"));
		var admin = jwt()
				.jwt(j -> j.subject("admin-flow-admin").claim("name", "Admin").claim("roles", List.of("ROLE_ADMIN")))
				.authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

		String body = objectMapper.writeValueAsString(new CreateGoalRequest("Need approval", null, null, null));
		String created = mockMvc
				.perform(post("/api/v1/goals").with(alice).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.approvalStatus").value("PENDING"))
				.andReturn()
				.getResponse()
				.getContentAsString();
		UUID id = UUID.fromString(objectMapper.readTree(created).get("id").asText());

		mockMvc.perform(get("/api/v1/admin/goals/pending").with(admin)).andExpect(status().isOk()).andExpect(jsonPath("$[*].id", hasItem(id.toString())));

		String review = objectMapper.writeValueAsString(new AdminReviewRequest(true));
		mockMvc
				.perform(post("/api/v1/admin/goals/" + id + "/review").with(admin).contentType(MediaType.APPLICATION_JSON).content(review))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.approvalStatus").value("APPROVED"));

		mockMvc.perform(get("/api/v1/goals/" + id).with(alice)).andExpect(status().isOk()).andExpect(jsonPath("$.approvalStatus").value("APPROVED"));

		String reviewAgain = objectMapper.writeValueAsString(new AdminReviewRequest(true));
		mockMvc
				.perform(post("/api/v1/admin/goals/" + id + "/review").with(admin).contentType(MediaType.APPLICATION_JSON).content(reviewAgain))
				.andExpect(status().isBadRequest());
	}

}
