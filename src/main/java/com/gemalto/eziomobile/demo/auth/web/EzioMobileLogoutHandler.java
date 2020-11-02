package com.gemalto.eziomobile.demo.auth.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class EzioMobileLogoutHandler implements LogoutSuccessHandler{
	
	/*@Autowired
	private SessionRegistry sessionRegistry;*/
	
	/*@Autowired
	MongoOperations mongoTemplate;*/
	
	private String successURL = "/";
	
	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
		if (auth == null) {
			response.sendRedirect(request.getContextPath() + successURL);
			return;
		}
		
		/*if(request != null) {
			mongoTemplate.updateFirst(
					new Query(Criteria.where("_id").is(auth.getPrincipal().toString())),
					new Update().set("loginInfo.lastLogoutDate", LocalDateTime.now()), UserInfo.class);
		}
	*/
		response.sendRedirect(request.getContextPath() + successURL);
	}


}
