package com.ilyabnae.goaltracker.api;

import com.ilyabnae.goaltracker.api.dto.UserProfileResponse;
import com.ilyabnae.goaltracker.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Current user resolved from JWT")
public class ProfileController {

	private final UserAccountService userAccountService;

	@GetMapping("/me")
	@Operation(summary = "Return profile for the authenticated subject (creates local user on first call)")
	public UserProfileResponse me() {
		var user = userAccountService.getOrCreateCurrentUser();
		return new UserProfileResponse(user.getId(), user.getExternalSubject(), user.getDisplayName());
	}

}
