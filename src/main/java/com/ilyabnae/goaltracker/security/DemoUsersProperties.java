package com.ilyabnae.goaltracker.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security.demo")
public class DemoUsersProperties {

	private List<DemoUser> users = new ArrayList<>();

	public List<DemoUser> getUsers() {
		return users;
	}

	public void setUsers(List<DemoUser> users) {
		this.users = users;
	}

	public Optional<DemoUser> findByUsername(String username) {
		if (username == null) {
			return Optional.empty();
		}
		return users.stream().filter(u -> username.equalsIgnoreCase(u.getUsername())).findFirst();
	}

	public static class DemoUser {

		private String username;
		private String password;
		private String displayName;
		private List<String> roles = new ArrayList<>();

		public List<String> resolvedRoles() {
			if (roles == null || roles.isEmpty()) {
				return List.of("ROLE_USER");
			}
			return Collections.unmodifiableList(roles);
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public List<String> getRoles() {
			return roles;
		}

		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

	}

}
