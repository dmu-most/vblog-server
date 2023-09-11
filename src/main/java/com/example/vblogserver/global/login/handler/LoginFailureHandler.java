package com.example.vblogserver.global.login.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Change to 401 Unauthorized status code
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8"); // Change to application/json content type

		Map<String, Object> data = new HashMap<>();
		data.put("message", "아이디 또는 비밀번호가 올바르지 않습니다. 다시 확인해 주세요.");
		data.put("status", HttpServletResponse.SC_UNAUTHORIZED);

		Gson gson = new Gson();
		String jsonResponseBody = gson.toJson(data);

		response.getWriter().write(jsonResponseBody);

		log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
	}
}

