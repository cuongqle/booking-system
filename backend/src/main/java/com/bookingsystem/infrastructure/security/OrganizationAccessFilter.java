package com.bookingsystem.infrastructure.security;

import com.bookingsystem.application.organization.OrganizationService;
import com.bookingsystem.application.organization.OrganizationSuspendedException;
import com.bookingsystem.application.user.UserInactiveException;
import com.bookingsystem.application.user.UserService;
import com.bookingsystem.domain.user.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class OrganizationAccessFilter extends OncePerRequestFilter {

	private final OrganizationService organizationService;
	private final UserService userService;

	public OrganizationAccessFilter(OrganizationService organizationService, UserService userService) {
		this.organizationService = organizationService;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
			if (user.getRole() != UserRole.SUPER_ADMIN) {
				try {
					userService.requireActive(user.getId());
					if (user.getOrganizationId() != null) {
						organizationService.requireActiveById(user.getOrganizationId());
					}
				} catch (UserInactiveException ex) {
					writeForbidden(response, "USER_INACTIVE", ex.getMessage());
					return;
				} catch (OrganizationSuspendedException ex) {
					writeForbidden(response, "ORG_SUSPENDED", ex.getMessage());
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private static void writeForbidden(HttpServletResponse response, String code, String message)
			throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(
				"{\"status\":403,\"code\":\""
						+ code
						+ "\",\"message\":\""
						+ escapeJson(message)
						+ "\"}");
	}

	private static String escapeJson(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
