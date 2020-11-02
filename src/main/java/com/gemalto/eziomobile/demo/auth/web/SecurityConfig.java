/*package com.gemalto.eziomobile.demo.auth.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant.LoginErrorCode;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private EzioMobileDemoAuthenticationProvider authProvider;

	@Autowired
	private AuthSuccessHandler authenticationSuccessHandler;

	@Autowired
	private AuthFailureHandler authenticationFailureHandler;
	
	@Autowired
	private EzioMobileLogoutHandler logoutSuccessHandler;
	

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		System.out.println("Coming here...");
		
		http.authorizeRequests()
				.antMatchers("/login").access("isAuthenticated() or isAnonymous()")
				.antMatchers("/**").access("isAuthenticated()")
				.anyRequest().denyAll()
		
		.and().formLogin()
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler)
				.loginPage("/login").loginProcessingUrl("/authenticate")
				.usernameParameter("j_username").passwordParameter("j_password")
				
		.and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login")
				.logoutSuccessHandler(logoutSuccessHandler)
				.invalidateHttpSession(true)
				.deleteCookies("EZIOSESSIONID")
				
		.and().exceptionHandling()
				.accessDeniedPage("/login?error="+LoginErrorCode.AccessDenied.name())
				
		.and().sessionManagement()
				.sessionFixation().changeSessionId()
				.invalidSessionUrl("/login")
				.sessionAuthenticationErrorUrl("/login?error="+LoginErrorCode.SessionExpired.name())
				.maximumSessions(1)
					.expiredUrl("/login?error="+LoginErrorCode.AnotherLoginDetected.name())
					.sessionRegistry(sessionRegistry());
	}

	@Bean
    public static ServletListenerRegistrationBean<?> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }

	@Bean
	public SessionRegistry sessionRegistry() {
		SessionRegistry sessionRegistry = new SessionRegistryImpl();
		return sessionRegistry;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider).eraseCredentials(true);

	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider).eraseCredentials(true);
	}

	
}
*/