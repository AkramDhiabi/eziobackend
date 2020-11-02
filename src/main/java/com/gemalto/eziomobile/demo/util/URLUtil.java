package com.gemalto.eziomobile.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import com.gemalto.mno.qrip.*;


//@Configuration
@ComponentScan(basePackages = { "com.gemalto.*" })
@PropertySource("classpath:application-${backend_config}.properties")
@Component
public class URLUtil {
	
	@Value("${backend_config}")
	private String backendConfig;

	@Value("${CAS_USERNAME}")
	private String CAS_USERNAME;

	@Value("${CAS_PASSWORD}")
	private String CAS_PASSWORD;
	
	@Value("${hostURL}")
	private String hostURL;

	@Value("${isP2PEnabled}")
	private String isP2PEnabled;

	@Value("${oobsQueueURL}")
	private String oobsQueueURL;

	@Value("${pushNotificationURL}")
	private String pushNotificationURL;

	@Value("${casServerURL}")
	private String casServerURL;

	@Value("${casServerAuth}")
	private String casServerAuthenticationURL;
	
	@Value("${getUserFromCASServer}")
	private String findUserFromCASServerURL;

	@Value("${uploadBatchProvisioningFileURL}")
	private String uploadBatchProvisioningFileURL;

	@Value("${launchProvisioningURL}")
	private String launchProvisioningURL;

	@Value("${provisioningStatusURL}")
	private String provisioningStatusURL;

	@Value("${linkDevicesURL}")
	private String linkDevicesURL;

	@Value("${validateOTPURL}")
	private String validateOTPURL;

	@Value("${getListOfTokensURL}")
	private String getListOfTokensURL;

	@Value("${getDeviceByDeviceIdURL}")
	private String getDeviceByDeviceIdURL;
	
	@Value("${verifyTxCallbackURL}")
	private String verifyTxCallbackURL;
	
	@Value("${mobileEnrollmentEPSURL}")
	private String mobileEnrollmentEPSURL;
	
	@Value("${oobsMobileEnrollmentURL}")
	private String oobsMobileEnrollmentURL;
	
	@Value("${getAndUpdateTokenCount}")
	private String getAndUpdateTokenCount;
	
	@Value("${emvCardCreatationURL}")
	private String emvCardCreatationURL;
	
	@Value("${emvCardLinkURL}")
	private String emvCardLinkURL;
	
	@Value("${emvCardActivationURL}")
	private String emvCardActivationURL;

	@Value("${getPANfromDCVdeviceToBeReworkedURL}")
	private String getPANfromDCVdeviceToBeReworkedURL;
	
	@Value("${getDeviceStateURL}")
	private String getDeviceStateURL;
	
	@Value("${dcvValidationURL}")
	private String dcvValidationURL;
	
	@Value("${rootURL}")
	private String rootURL;

	@Value("${frontURL}")
	private String frontURL;
	
	@Value("${qrtoken_encryptionmode}")
	private String qrtoken_encryptionmode;
	
	@Value("${qrtoken_algo}")
	private String qrtoken_algo;
	
	@Value("${qrtoken_randommode}")
	private String qrtoken_randommode;
	
	@Value("${qrtoken_randomvalue}")
	private String qrtoken_randomvalue;
	
	@Value("${qrtoken_PrimKeyTDES}")
	private String qrtoken_PrimKeyTDES;
	
	@Value("${qrtoken_PrimKeyAES128}")
	private String qrtoken_PrimKeyAES128;
	
	@Value("${qrtoken_PrimKeyAES256}")
	private String qrtoken_PrimKeyAES256;
	
	@Value("${qrtoken_CustKeyTDES}")
	private String qrtoken_CustKeyTDES;
	
	@Value("${qrtoken_CustKeyAES128}")
	private String qrtoken_CustKeyAES128;
	
	@Value("${qrtoken_CustKeyAES256}")
	private String qrtoken_CustKeyAES256;
	
	@Value("${qrtoken_seedDpuk}")
	private String qrtoken_seedDpuk;
	
	@Value("${qrtoken_seedOTP}")
	private String qrtoken_seedOTP;
	
	@Value("${qrtoken_qrcodeversion}")
	private String qrtoken_qrcodeversion;
	
	@Value("${qrtoken_hashMode}")
	private String qrtoken_hashMode;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * @return CAS username
	 */
	public String getCASUsername() {
		return CAS_USERNAME;
	}

	/**
	 * @return CAS password
	 */
	public String getCASPassword() {
		return CAS_PASSWORD;
	}
	
	/**
	 * @return hostURL of application for ex: For example, www.example.com.
	 */
	public String getHostURL() {
		return hostURL;
	}

	/**
	 * @return p2p value
	 */
	public String isP2PEnabled() {
		return isP2PEnabled;
	}

	/**
	 * @return OOBS queue URL
	 */
	public String oobsQueueURL() {
		return oobsQueueURL;
	}

	/**
	 * @return Push Notification URL
	 */
	public String pushNotificationURL() {
		return pushNotificationURL;
	}

	/**
	 * @return CAS Server URL
	 */
	public String casServerURL() {
		return casServerURL;
	}

	/**
	 * @return CAS authentication URL
	 */
	public String casServerAuthenticationURL() {
		return casServerAuthenticationURL;
	}

	/**
	 * @return URL to upload batch provisioning file
	 */
	public String uploadBatchProvisioningFileURL() {
		return uploadBatchProvisioningFileURL;
	}

	/**
	 * @return URL to trigger/start provisioning
	 */
	public String launchProvisioningURL() {
		return launchProvisioningURL;
	}

	/**
	 * @return URL to get provisioning status
	 */
	public String provisioningStatusURL() {
		return provisioningStatusURL;
	}

	/**
	 * @return URL to link devices
	 */
	public String linkDevicesURL() {
		return linkDevicesURL;
	}

	/**
	 * @return URL to validate OTP
	 */
	public String validateOTPURL() {
		return validateOTPURL;
	}

	/**
	 * @return URL to get list of tokens of user
	 */
	public String getListOfTokensURL() {
		return getListOfTokensURL;
	}

	/**
	 * @return URL to get device data by deviceID
	 */
	public String getDeviceByDeviceIdURL() {
		return getDeviceByDeviceIdURL;
	}
	
	/**
	 * @return URL to find user on CAS server
	 */
	public String findUserFromCASServerURL() {
		return findUserFromCASServerURL;
	}
	
	/**
	 * @return Callback URL
	 */
	public String verifyTxCallbackURL() {
		return verifyTxCallbackURL;
	}
	
	/**
	 * @return EPS, mobile enrollment/registration URL
	 */
	public String mobileEnrollmentEPSURL() {
		return mobileEnrollmentEPSURL;
	}
	
	/**
	 * @return Mobile registration, OOBS enrollment URL
	 */
	public String oobsMobileEnrollmentURL() {
		return oobsMobileEnrollmentURL;
	}
	
	/**
	 * @return EPS DB update through EzioMobileDemo (old) web app
	 */
	public String getAndUpdateTokenCount() {
		return getAndUpdateTokenCount;
	}
	
	/**
	 * @return
	 */
	public String emvCardCreatationURL() {
		return emvCardCreatationURL;
	}
	
	/**
	 * @return
	 */
	public String emvCardLinkURL() {
		return emvCardLinkURL;
	}
	
	/**
	 * @return
	 */
	public String emvCardActivationURL() {
		return emvCardActivationURL;
	}
	
	/**
	 * @return
	 */
	public String getPANfromDCVdeviceToBeReworkedURL() {
		return getPANfromDCVdeviceToBeReworkedURL;
	}
	
	/**
	 * @return
	 */
	public String getDeviceStateURL() {
		return getDeviceStateURL;
	}
	
	/**
	 * @return URL to validate DCV transaction
	 */
	public String dcvValidationURL() {
		return dcvValidationURL;
	}
	
	/**
	 * @return Root URL to prepare user accountActivationLink
	 */
	public String getRootURL() {
		return rootURL;
	}

	/**
	 * @return Front URL
	 */
	public String getFrontURL() {
		return frontURL;
	}
	
	/**
	 * @return will read this property from application.properties and return the value
	 */
	public String getBackendConfiguration() {
		return backendConfig;
	}

	//possible values ENCRYPTION_DFF_NO (0x10),ENCRYPTION_DFF_DYNAMIC_KEY (0x11),ENCRYPTION_DFF_CUSTOMER_KEY (0x12); 
	public String getQrtoken_encryptionmode() {
		return qrtoken_encryptionmode;
	}

	public QRtokenAlgo getQrtoken_algo() {
		final String ENCRYPTION_ALGO_AES256 = "ENCRYPTION_ALGO_AES256";
		final String ENCRYPTION_ALGO_AES128 = "ENCRYPTION_ALGO_AES128";
		final String ENCRYPTION_ALGO_TDES = "ENCRYPTION_ALGO_TDES";
		switch (qrtoken_algo) {
		case ENCRYPTION_ALGO_TDES:
			return QRtokenAlgo.ENCRYPTION_ALGO_TDES;
		case ENCRYPTION_ALGO_AES128:
			return QRtokenAlgo.ENCRYPTION_ALGO_AES128;
		case ENCRYPTION_ALGO_AES256:
			return QRtokenAlgo.ENCRYPTION_ALGO_AES256;
		default:
			return QRtokenAlgo.ENCRYPTION_ALGO_AES256;
		}
	}

	public QRTokenHash getQrtoken_hashMode() {

		final String SHA1 = "SHA1";
		final String SHA256 = "SHA256";
		final String SHA512 = "SHA512";
		switch (qrtoken_hashMode) {
		case SHA1:
			return QRTokenHash.SIGN_SHA1;
		case SHA256:
			return QRTokenHash.SIGN_SHA256;
		case SHA512:
			return QRTokenHash.SIGN_SHA512;
		default:
			return QRTokenHash.SIGN_SHA256;
		}
	}

	public QRTokenVersion getQrtoken_qrcodeversion() {
		final String QR_CODE_VERSION_AUTO = "QR_CODE_VERSION_AUTO";
		final String QR_CODE_VERSION_4 = "QR_CODE_VERSION_4";
		final String QR_CODE_VERSION_5 = "QR_CODE_VERSION_5";
		final String QR_CODE_VERSION_6 = "QR_CODE_VERSION_6";
		final String QR_CODE_VERSION_7 = "QR_CODE_VERSION_7";
		final String QR_CODE_VERSION_8 = "QR_CODE_VERSION_8";
		final String QR_CODE_VERSION_9 = "QR_CODE_VERSION_9";
		switch (qrtoken_qrcodeversion) {
		case QR_CODE_VERSION_AUTO:
			return QRTokenVersion.QR_CODE_VERSION_AUTO;

		case QR_CODE_VERSION_4:
			return QRTokenVersion.QR_CODE_VERSION_4;

		case QR_CODE_VERSION_5:
			return QRTokenVersion.QR_CODE_VERSION_5;

		case QR_CODE_VERSION_6:
			return QRTokenVersion.QR_CODE_VERSION_6;

		case QR_CODE_VERSION_7:
			return QRTokenVersion.QR_CODE_VERSION_7;

		case QR_CODE_VERSION_8:
			return QRTokenVersion.QR_CODE_VERSION_8;

		case QR_CODE_VERSION_9:
			return QRTokenVersion.QR_CODE_VERSION_9;

		default:
			return QRTokenVersion.QR_CODE_VERSION_AUTO;
		}

	}
	
	public String getQrtoken_randommode() {
		return qrtoken_randommode;
	}

	public String getQrtoken_PrimKeyTDES() {
		return qrtoken_PrimKeyTDES;
	}

	public String getQrtoken_PrimKeyAES128() {
		return qrtoken_PrimKeyAES128;
	}

	public String getQrtoken_PrimKeyAES256() {
		return qrtoken_PrimKeyAES256;
	}

	public String getQrtoken_CustKeyTDES() {
		return qrtoken_CustKeyTDES;
	}

	public String getQrtoken_CustKeyAES128() {
		return qrtoken_CustKeyAES128;
	}

	public String getQrtoken_CustKeyAES256() {
		return qrtoken_CustKeyAES256;
	}

	public String getQrtoken_randomvalue() {
		return qrtoken_randomvalue;
	}

	public String getQrtoken_seedDpuk() {
		return qrtoken_seedDpuk;
	}

	public String getQrtoken_seedOTP() {
		return qrtoken_seedOTP;
	}
	
	
}
