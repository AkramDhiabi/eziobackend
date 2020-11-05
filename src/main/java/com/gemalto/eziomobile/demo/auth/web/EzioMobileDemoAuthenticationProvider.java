package com.gemalto.eziomobile.demo.auth.web;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
//import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant.RoleId;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.model.UserMasterInfo;

@Component
public class EzioMobileDemoAuthenticationProvider implements AuthenticationProvider{
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private static final LoggerUtil logger = new LoggerUtil(EzioMobileDemoAuthenticationProvider.class.getClass());
	
	
	@SuppressWarnings("unused")
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {


		String username = String.valueOf(authentication.getPrincipal());
		String password = String.valueOf(authentication.getCredentials());
		
		logger.info("EzioMobileDemoAuthenticationProvider [Username]: "+username);
		logger.info("EzioMobileDemoAuthenticationProvider [Password]: "+password);

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserMasterInfo> criteriaQuery = builder.createQuery(UserMasterInfo.class);
		Root<UserMasterInfo> studentRoot = criteriaQuery.from(UserMasterInfo.class);
		criteriaQuery.select(studentRoot);
		criteriaQuery.where(builder.equal(studentRoot.get("userId"),username));
		
		@SuppressWarnings("unchecked")
		UserMasterInfo user = entityManager.createQuery(criteriaQuery).getSingleResult();
		    
		logger.info("EzioMobileDemoAuthenticationProvider [user]: "+user.toString());
		logger.info("EzioMobileDemoAuthenticationProvider [Password]: "+user.getPassword());

		String encodePassword = EncryptionUtility.convertHexToString(user.getPassword());
		BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();

		if(!(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1 == user.getStatus())||RoleId.ROLE_USER.name().equals(user.getUserRole())){
			throw new BadCredentialsException("NotAuthorized");
		 }
		if (passwordEncoder.matches(password, encodePassword)) {

			return authentication;
		}
		else {
			throw new BadCredentialsException("CredentialNotMatched");
		}
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}


}
