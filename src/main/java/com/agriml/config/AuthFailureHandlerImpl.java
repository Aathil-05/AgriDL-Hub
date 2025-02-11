package com.agriml.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.agriml.model.Userdetail;
import com.agriml.repository.UserRepository;
import com.agriml.service.UserService;
import com.agriml.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String phone = request.getParameter("username");

		Userdetail userdetail = userRepository.findByEmail(phone);

		if (userdetail != null) {
			
			if (userdetail.getIsEnabled()) {

				if (userdetail.getAccountNonLocked()) {

					if (userdetail.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {

						userService.increaseFailedAttempt(userdetail);
					} else {
						userService.userAccLock(userdetail);
						exception = new LockedException("Your account is locked !! failed attempt 3");
					}
				} else {

					if (userService.unlockAccTimeExpired(userdetail)) {
						exception = new LockedException("Your account is unlocked !! please try to login");
					} else {
						exception = new LockedException("Your account is locked !! please try after sometime");
					}
				}
			} else {
				exception = new LockedException("Your account is inactive");
			}
		} else {
			exception = new LockedException("Mobile number & password is invalid");
		}

		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
