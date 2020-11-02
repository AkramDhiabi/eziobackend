package com.gemalto.eziomobile.demo.util;

import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.dto.UserMasterDTO;
import com.gemalto.eziomobile.demo.model.EzioDemoEmailInfo;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class EzioDemoEmailUtil {

	@Autowired
	private ApplicationPropertiesUtil applicationPropertiesUtil;

	private static final LoggerUtil logger = new LoggerUtil(EzioDemoEmailUtil.class);

	/**
	 * @param username user username
	 * @param password user password
	 * @param activationLink activation lik
	 * @return message
	 */
	public StringBuilder prepareEmailContentForNewAccount(String username, String password, String activationLink) {

		// message contains HTML markups
		StringBuilder message = new StringBuilder(EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_1 + BR_CONST);
		message.append(BR_CONST);
		message.append(EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_2 + BR_CONST);
		message.append(BR_CONST);
		message.append(B_CONST + USERNAME_LABEL_CONST + username + B_END_CONST + BR_CONST);
		message.append(BR_CONST);
		message.append(EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_3 + BR_CONST);
		message.append(activationLink);
		message.append(BR_CONST + BR_CONST);
		message.append(EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_4 + BR_CONST);
		message.append(BR_CONST);
		message.append(EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_5 + BR_CONST);
		message.append(BR_CONST + BR_CONST);
		message.append(FONT_COLOR_BLACK_CONST + EzioMobileDemoConstant.EMAIL_TERMINOLOGY + FONT_END_CONST);

		return message;
	}


	/**
	 * @param userInfoList users list informations
	 * @return message
	 */
	public String prepareEmailContentForForgetDetails(List<UserMasterDTO> userInfoList, String rootUrl) {

		// message contains HTML markups
		StringBuilder message = new StringBuilder(""+EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_1 + BR_CONST);
		message.append(BR_CONST);
		message.append(EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_2 + BR_CONST);
		message.append(BR_CONST);
		message.append(""+EzioMobileDemoConstant.EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_3_1 + userInfoList.size() + EzioMobileDemoConstant.EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_3_2 + "<br>");

		for (int i = 0; i < userInfoList.size(); i++) {
			message.append("Account n&#186;" + (i+1) + BR_CONST);
			message.append("<b>Username : "+userInfoList.get(i).getUserId()+"</b>" + BR_CONST);
			message.append(""+EzioMobileDemoConstant.EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_3_3+BR_CONST);
			message.append("RecoverToken : <b><a href=\""+ rootUrl + userInfoList.get(i).getRecoverToken()+"\">"+ rootUrl + userInfoList.get(i).getRecoverToken()+"</b>"+BR_CONST);
			message.append(BR_CONST);
			message.append(BR_CONST+BR_CONST);
		}

		message.append(EzioMobileDemoConstant.EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_4 + BR_CONST);
		message.append(BR_CONST);
		message.append(EzioMobileDemoConstant.EMAIL_MESSAGE_LINE_5 + BR_CONST);
		message.append(BR_CONST + BR_CONST);
		message.append(FONT_COLOR_BLACK_CONST + EzioMobileDemoConstant.EMAIL_TERMINOLOGY + FONT_END_CONST);

		return message.toString();
	}


	/**
	 * @param emailInfo email informations before sending
	 * @return isEmailSent
	 */
	public boolean sendMail(EzioDemoEmailInfo emailInfo) {

		boolean isMailSent;

		// Get system properties
		Properties props = System.getProperties();

		String password =  applicationPropertiesUtil.getSmtpPassword();

		// Setup mail server
		props.setProperty("mail.smtp.host", applicationPropertiesUtil.getSmtpHost());
		props.setProperty("mail.smtp.port", applicationPropertiesUtil.getSmtpPort());
		props.setProperty("mail.smtp.auth", applicationPropertiesUtil.getSmtpIsAuth());
		props.setProperty("mail.smtp.starttls.enable", applicationPropertiesUtil.getSmtStartTlsIsEnable());


		// Get the default Session object.
        final String smtpEmail = applicationPropertiesUtil.getSmtpEmail();

		/**
		 * If the password is not empty, than we need an authentification
		 * Otherwise it will create a simple session
 		 */

		Session session;

		if(null != password && !password.isEmpty()) {
			props.setProperty("mail.smtp.password", applicationPropertiesUtil.getSmtpPassword());
			session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(smtpEmail, applicationPropertiesUtil.getSmtpPassword());
					}
				});
		}else{
			session = Session.getInstance(props);
		}

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(smtpEmail));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailInfo.getToEmailAddress()));

			// Set Subject: header field
			message.setSubject(emailInfo.getEmailSubject());

			// set plain text message
			message.setContent(emailInfo.getEmailContent(), "text/html");

			// Send message
			Transport.send(message);
			logger.info("Email has been sent successfully....");
			isMailSent = true;
		}
		catch (MessagingException mex) {
			isMailSent = false;
			logger.error("Error while sending email: {}.", mex);
		}
		return isMailSent;
	}
}
