package com.gemalto.eziomobile.demo.common;

public class CommonOperationsConstants {
	
	public static final String PREFIX_DISPLAY_CARD_PAD_EB = "GADB";
	public static final String PREFIX_DISPLAY_CARD_PAD = "GADF";
	public static final String PREFIX_FLEX = "GATB";
	public static final String PREFIX_SIGNER = "GATZ";
	public static final String PREFIX_PICO = "GATP";
	public static final String PREFIX_LAVA_TIME_BASED = "GALT";
	public static final String PREFIX_LAVA_EVENT_BASED = "GALH";
	public static final String PREFIX_DCV_MOBILE = "GADV";
	public static final String PREFIX_DCV_PHYSIQUE = "GAPC";
	public static final String PREFIX_MOBILE_LOGIN = "GAOC";
	public static final String PREFIX_MOBILE_OC = "GALO";
	public static final String PREFIX_QRTOKEN = "GAQT";
	
	public static final String PREFIX_DEVICE_MOBILE_LOGIN = "GALO";
	public static final String PREFIX_DEVICE_MOBILE_SIGNATURE_TRANSACTION = "GAOC";
	public static final String PREFIX_DEVICE_MOBILE_DCV = "GADV";
	public static final String PREFIX_DEVICE_RFU = "GAXX";
	public static final String PREFIX_DEVICE_FLEX = "GATB";
	public static final String PREFIX_DEVICE_QRTOKEN = "GAQT";
	public static final String PREFIX_DEVICE_SIGNER = "GATZ";
	public static final String PREFIX_DEVICE_PICO = "GATP";
	//public static final String PREFIX_DCV_PHYSIQUE = "GAPC";
	public static final String PREFIX_DEVICE_DISPLAY_CARD_PAD = "GADF";
	public static final String PREFIX_DEVICE_DISPLAY_CARD_PAD_EB = "GADB";
	public static final String PREFIX_DEVICE_EMV = "EMV";
	public static final String PREFIX_DEVICE_LAVA = "GALT";
	
	public static final int TOKEN_NAME_LEN = 12;
	public static final int HTTP_TOKEN_NOT_FOUND = 404;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int BAD_FORMAT = 0x1F;
	public static final int TOKEN_NOT_FOUND = 0x2F;
	public static final int TOKEN_ALREADY_ASSOCIATED = 0x3F;
	public static final int GENERAL_ERROR = 0x4F;
	public static final int TOKEN_BLOCKED_OR_REVOKED = 0x5F;
	public static final int ALL_OK_INITIALIZED = 0x8F;
	public static final int ALL_OK_ACTIVATED = 0x9F;
	
	public static final String STATE_INITIALIZED = "1";
	public static final String STATE_ACTIVED = "2";
	public static final String STATE_BLOCKED= "3";
	public static final String STATE_REVOKED= "4";
	
	public static final String FLEX_POLICY = "FlexPolicy";
	public static final String SIGNER_POLICY = "SignerPolicy";
	public static final String PICO_POLICY = "PicoPolicy";
	public static final String LAVA_POLICY = "LavaPolicy";
	public static final String DISPLAY_CARD_PAD_POLICY = "DisplayCardPadPolicyDF";
	public static final String DISPLAY_CARD_PAD_POLICY_EB = "DisplayCardPadPolicyEB";
	
	
	public static final int MULTI_SEED_DEVICE = 1;
	public static final int SINGLE_SEED_TOTP_DEVICE = 3;
	public static final int EMV_DEVICE = 4;
	public static final int DCVV_DEVICE = 7;
	public static final int EZIO_MOBILE_PROTECTOR = 8;
	
	
	public static final int UNLINK_TOKEN = 1;
	public static final int REVOKE_TOKEN = 2;
	public static final int DELETE_TOKEN = 4;
	
	public static final int INITIALIZED = 1;
	public static final int ACTIVE = 2;
	public static final int BLOCKED = 3;
	public static final int REVOKED = 4;
	public static final int LOCKED = 5;
	
	public static final int DEVICE_MOBILE_LOGIN = 1;
	public static final int DEVICE_MOBILE_SIGNATURE_TRANSACTION = 2;
	public static final int DEVICE_MOBILE_DCV = 4;
	public static final int DEVICE_FLEX = 16;
	public static final int DEVICE_SIGNER = 32;
	public static final int DEVICE_PICO = 64;
	public static final int DEVICE_DISPLAY_CARD_PAD = 128;
	public static final int DEVICE_DISPLAY_CARD_PAD_EB = 256;
	public static final int DEVICE_QRTOKEN = 512;
	
	public static final int CARD_PAN_LEN = 16;
	public static final int CARD_PSN_LEN = 2;
	public static final int TOKEN_LEN =12;
	public static final int INVALID_PAN_NUMBER = 0x1F;
	public static final int BAD_LENGTH = 0x2F;
	public static final int CARD_ALREADY_ASSOCIATED = 0x3F;
	public static final int ALL_OK = 0x5F;

	public static final String HAS_MOBILE = "mobile";
	public static final String HAS_DCV_CARDS = "dcvcards";
	public static final String HAS_PHYSICAL_TOKENS = "physicalTokens";
	public static final String HAS_RM = "riskManagement";
	public static final String HAS_DEMODATA= "demoData";
	
	public static final String PSN = "00";
	public static final String FIELD_TYPE_PASSWORD = "Update_Password";
	public static final String FIELD_TYPE_EMAIL = "Update_Email";
	
	public final static String PRIVATE_KEY =  
			 "-----BEGIN RSA PRIVATE KEY-----\n"
			 + "MIICWgIBAAKBgE5IcRwZZgUg/p1iMg4rBlNq3ShcTvHeHY9ACdUrd1dRgKgZejS8\n"
			 + "Br1v575WDDxMC+yKCBBsHl5R7FaN/HExzzAXRsSyvtofytDo2OL+DaFhMqGdGZ4S\n"
			 + "7D4JX1afAl54mww9/02AKx6+HOEkj8HLLtfpwpuT3tBB3a2RWVa9J91PAgMBAAEC\n"
			 + "gYA0pBYblSC7djKBVtTeHiSE16mcSGZ10q0B9UgnZrE7sHzZjThWWdIWdTfbEDXu\n"
			 + "Hh0ulKtTj8DkICbTFWuaGqp9pvA2VBVuXO/yO/RZ2WQ88SfkEOXGcHKySqdh8TKh\n"
			 + "sHOdNg1YVY3bB7SNqXOAz059OvxazQVqMBg3r8yQYSUc4QJBAJx857lmDtUhrpTZ\n"
			 + "F4BVAdux4s2eyZS7Ta2+Dyf4WBR26bUwh8K8uUkDQUxfU1sJzCJnfXWSh+sm0hFI\n"
			 + "iOkZoIcCQQCAEFdsvK6a3mUwibwxHMRvXbLf07vijStAs57OCsleJn7LP0QgK6Xw\n"
			 + "cByP+i+dkCXsCTZ8E4WvGcvrq0NAf/b5AkAJxBnKDj56uqFklK/MyIFXLqDS6Ef4\n"
			 + "SkAsyaG0guEsVcd8EU1Hr/N4RQW8OG7BTbk5pG/F+KEW4dNWVMqGkZzdAkALkG40\n"
			 + "lX024upETcu+q5hZbh6e86G9vX2wZAftFzBD3joI5HekaAyd/6G0pqTyF3g1LkfL\n"
			 + "QIAL5cpyR47VHjkRAkAx3nzDRRSGduXEBlBXAtJeFEn271VlL9JgD22OhFrgfFye\n"
			 + "NEjfIkfBa5rnyZbdBD7oFySuMuQeMjZMvWI3hHYZ\n"
			 + "-----END RSA PRIVATE KEY-----";
	
	public final static String GAH_TENANT = "bankonline";

	public static final String USER_ID_LABEL = "userId : ";

	public static final String FROM_ACCOUNT_NO_PARAM = "fromAccountNo";
	public static final String USER_ID_PARAM = "userId";
	public static final String AMOUNT_PARAM = "amount";
	public static final String STATUS_PARAM = "status";
	public static final String TO_ACCOUNT_NO_PARAM = "toAccountNo";
	public static final String SENDER_S_ACCOUNT_HAS_BEEN_UPDATED_MESSAGE = "Sender's Account has been updated!";

	public static final String STATUS_LABEL = ", status : ";
	public static final String UNABLE_TO_FETCH_LIST_OF_ACCOUNT_DETAILS_WITH_USER_ID_MESSAGE = "Unable to fetch list of account details with UserId: ";
	public static final String USER_ID_LABEL2 = ", userId : ";
	public static final String UNABLE_TO_UPDATE_RECEIVER_S_USER_ACCOUNT_BALANCE_WITH_MESSAFE = "Unable to update Receiver's (user) account balance with: ";
	public static final String AND_PAYEE_ACCOUNT_NO_LABEL = " And Payee Account No : ";

	public static final String DATA_STATUS_LABEL = "{\"data\":{\"status\":\"";

	public static final String DATA_STATUS_LABEL2 = "{\"data\":{\"status\":[\"";
	public static final String CLOSE_LABEL = "\"]}}";

	public static final String QRTOKEN_DEVICE_NAME_ATTR = "qrtokenDeviceName";
	public static final String TOKEN_DEVICE_INFO_LABEL = "tokenDeviceInfo: ";

	public static final String TOKEN_NAME_ATTR = "tokenName";
	public static final String AND_PAN_NO_MESSAGE = " AND PanNo : ";

	public static final String PAN_NO_PARAM = "panNo";
	public static final String CARD_STATUS_VALUE_PARAM = "cardStatusValue";

	public static final String CONTENT_TYPE_PROP = "Content-type";
	public static final String TEXT_XML_PROP_VALUE = "text/xml";
	public static final String COOKIE_PROP = "Cookie";
	public static final String XML_RESPONSE_ACTIVATE_DEVICES_MESSAGE = "XML response [Activate devices] : ";
	public static final String APPLICATION_X_WWW_PROP_VALUE = "application/x-www-form-urlencoded";

	public static final String ERROR_MESSAGE = "ERROR";
	public static final String AUTHORIZATION_PROP = "Authorization";
	public static final String ACCEPT = "Accept";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String POST = "POST";

	public static final String USER_AGENT = "User-Agent";
	public static final String MOZILLA_5_0 = "Mozilla/5.0";
	public static final String TEXT_XML = "text/xml";
	public static final String EN_US_EN_Q_0_5 = "en-US,en;q=0.5";
	public static final String COOKIE = "Cookie";
	public static final String GET = "GET";
	public static final String PUSHURL = "_pushurl: ";

	public static final String U_ID_PARAM = "uId";
	public static final String OTP_STATUS_PARAM = "otpStatus";
	public static final String CONTENT_LENGTH_PROP = "Content-Length";
	public static final String XXX_PROP_VALUE = "xxx";
	public static final String PAN_NUMBER_DIAMAND = "<PAN-NUMBER>";

	public static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String OATH_TOKEN_XML = "<oathToken xmlns=\"http://gemalto.com/ezio/mobile/eps/api\">";
	public static final String USER_ID_OPEN_XML = "<userId>";
	public static final String USER_ID_CLOSE_XML = "</userId>";
	public static final String OATH_ID_OPEN_XML = "<oathId>";
	public static final String OATH_ID_CLOSE_XML = "</oathId>";
	public static final String MANUFACTURER_ID_GA_XML = "<manufacturerId>GA</manufacturerId>";
	public static final String ACTIVATION_DATE_2013_XML = "<activationDate>2013-01-01T07:59:59+08:00</activationDate>";
	public static final String EXPIRATION_DATE_2024_XML = "<expirationDate>2024-01-01T07:59:59+08:00</expirationDate>";
	public static final String ACTIVATION_STATE_ACTIVE_XML = "<activationState>ACTIVE</activationState>";
	public static final String EXTERNAL_PROVISIONING_META_OPEN_XML = "<externalProvisioningMeta>";
	public static final String EXTERNAL_PROVISIONING_META_CLOSE_XML = "</externalProvisioningMeta>";
	public static final String PROPERTY_ENTRY_OPEN_XML = "<propertyEntry>";
	public static final String PROPERTY_ENTRY_CLOSE_XML = "</propertyEntry>";
	public static final String KEY_SAS_XML = "<key>sas.oathDeviceType</key>";
	public static final String VALUE_OTB_XML = "<value>OTB</value>";
	public static final String OATH_TOKEN_CLOSE_XML = "</oathToken>";
	public static final String PIN_OPEN_XML = "<pin>";
	public static final String PIN_CLOSE_XML = "</pin>";
	public static final String APPLICATION_XML_PROP_VALUE = "application/xml";
	public static final String ACCEPT_PROP = "Accept";
	public static final String REGISTRATION_CODE_ELEMENT = "registrationCode";
	public static final String TOKEN_ID_ELEMENT = "tokenId";
	public static final String DATA_REGCODE_LABEL = "{\"data\":{\"regcode\":[\"";
	public static final String PIN_LABEL = "\"],\"pin\":[\"";
	public static final String TOKENID_LABEL = "\"],\"tokenid\":[\"";
	public static final String END_LABEL = "\"]";

	public static final String PWD_LABEL_CONST  = "Password : ";
	public static final String BR_CONST = "<br>";
	public static final String B_CONST = "<b>";
	public static final String B_END_CONST = "</b>";
	public static final String USERNAME_LABEL_CONST = "Username : ";
	public static final String FONT_COLOR_BLACK_CONST = "<font color=black>";
	public static final String FONT_END_CONST = "</font>";

	public static final String EXCEPTION_OCCURED_URL = "Exception Occured:: URL=";

	public static final String HAS_DEVICE = "hasDevice";

	public static final int AUTHMODE_USERNAME_AND_OTP_TIME_BASED = 0x1;
	public static final int AUTHMODE_USERNAME_AND_OTP_EVENT_BASED = 0x2;

	public static final String P_CONST = "b9bb690f27b7d6d813f36315e540eaffd35d84a964f9bd996c968ae754f4c806ec3fcde7a61f9916ff9392af08aba2699e48ed788b4b894f0916fbf6aeed0ac321546af1314e1c33c0f909fecf656401ed2ad2da04b86840705b4951185383ef9ec5d66fb32a33404d0c2034df431e77cf6c4c7470d186eb46faf909a3c549dbaa102a029bd8f6ac5859741eb68a43232783d61d512a0cf5822f79d46522abbb788a9bd64bd059b9f64f277e020b0e519f5bc9f31652f067df3e8341019d4b71840caae7f6f9878ca393aee6ac7f414fbf628ef037f40af703502b97e72e84d0bb6ed175e72b4ad18a25bd875d0647965d758668c5b5ea8ea7a87c5e156b5d591d431053e8d6a071b70f32c93bf0697dfb5da4f2610a9580bd70aac7fa4eb6dba40200934c9bb61a8d31e594e0fe0e451afeb461d2fdd496a2fba1b7aefba03e6b29051ba0d1eb8e7837c0ca8e56835cf0fd2ee0da618b00e664bd9cb1306263082168fc49c40acb98e6a2ea549bc55d94f02d3868029601e81b6690cc9f19bb";
	public static final String Q_CONST = "ece43b040a3767e93daf34eb4bda4eb073723278da56afee9e622a6b3247e541";
	public static final String G_CONST = "9a5fb7e4f25f51238f53fd22417fe573af3514b0b20df7c741afc3dcffe1653b2961cdfba6222d779932329ef7a97753f89b21333b98e168e398772c78633d145673ebc4005beed10478eb0784db26f0ebc38f07cc7ba70e69751fe1af3344675b8e438071a0d0f2932d1c1a7803862a5371608f1534a24e4b6744f8716a52fe58a73dc851fffe9f2b48733e15ff5b020be03322a405535be11e12448fdabc0687da17bbd2947d40c356df64fc6f40386ebbdd5fe8a4f4798ad9edd2c3722a61878746171c73dd35aee1b1df324be3d2bc9861ee22b5b34de9cb68cd65260fbf8566ed5fd403df1908250c83b69597b53048eda3bd031e93d906ad52e193b7071787b364951aa8b6c5275526648a2264a7a1e1782689fa79c0195c7b348d0db29d8987ac6998736490857823d8552c64df9a7243cde707dcc629e5cbeaf264035b10b9efb31d065ff00a705a1dde795062a547c538cb55a608220f6a3e58d25d51f458fc8d33f76c61e4fd56340d004d313f2022d3c79ce3e302cf6d98c99497";
	public static final String PRIVATE_SIGNING_KEY = "d834e3e49c9ed2aa2b65280920fe33e1f635972b64dadbd29197b6c629a91e17";
	public static final String ENCRYPTION_KEY = "00112233445566778899AABBCCDDEEFF";

	public static final int QR_CODE_CLEAR = 0;
	public static final int QR_CODE_ENCRYPTED = 1;
	public static final int QR_CODE_ENCRYPTED_AND_DSA_SIGNED = 2;

	public static final String B_USE_CHALLENGE_LABEL = " -- bUseChallenge : ";
	public static final String TOKEN_TYPE_GATZ_OR_GATB_S_DATA_TO_SIGN_HEXA_LABEL = "TokenType : GATZ or GATB -- sDataToSignHexa : ";

	public static final String OPERATION_TYPE_MESSAGE = "Operation Type : ";

	public static final String USERNAME_LABEL = "','username':'";
	public static final String HASHEDDATA_LABEL = "','hasheddata':'";
	public static final String CLEARDATA_LABEL = "','cleardata':'";
	public static final String TITLE_GEMALTO_LABEL = "','title':'Gemalto',";
	public static final String TRANSECTIONID_LABEL = " 'transectionid':'";
	public static final String TYPE_LABEL = "{'type':'";
	public static final String ETIME_LABEL = "','etime':'";
	public static final String CHALLENGE_LABEL = "', 'challenge': '";
	public static final String SECURITY_LABEL = "', 'security':'";
	public static final String FROM_LABEL = "',  'from': '";
	public static final String TO = "', 'to': '";
	public static final String CLOSE_LABEL2= "'}";
	public static final String CARD_NUMBER_LABEL = "', 'CardNumber': '";
	public static final String EXP_DATE_LABEL = "', 'ExpDate': '";
	public static final String CVV_LABEL = "', 'CVV': '";
	public static final String AMOUNT_LABEL = "', 'amount': '";
	public static final String SEND_ONLINE_TRANSACTION_NOTIFICATION_LABEL = "----------- sendOnlineTransactionNotification --------------";
	public static final String PIN_CODE_ATTR = "pinCode";
	public static final String REG_CODE_ATTR = "regCode";
	public static final String PAYEE_NAME_ATTR = "payeeName";
	public static final String PAYEE_ACCOUNT_ATTR = "payeeAccount";
	public static final String CVV_ATTR = "cvv";
	public static final String EXP_DATE_ATTR = "expDate";

	public static final String OOBSMESSAGEMASTER_DAO_IMPL_MESSAGE_ID = "[OOBSMessagemasterDaoImpl] messageId : ";
	public static final String MESSAGE_ID_PARAM = "messageId";

	public static final String BASIC_PROP_VALUE = "Basic ZGVmYXVsdDpkZWZhdWx0";
	public static final String BASIC_TOKEN_PROP_VALUE = "Basic bWJfZ3RvZXppb2RlbW9fYWRtaW46RUE5VUxHY0twME5xeXd1Rlg3WWRiNS9NMVcvbXBveVY=";
	public static final String CONTENT_ELEMENT = "content";
	public static final String MSGTYPE_PROP = "msgtype";
	public static final String OPERATION_TYPE_PROP = "operationType";
	public static final String HASHEDDATA_PROP = "hasheddata";

	public static final String IS_BENEF_VALID_LABEL = "\",\"isbenefvalid\":\"";

	public static final String COUNT_PROP = "count";
	public static final String COUNT_LABEL = " count: ";
	public static final String UPDATE_COUNT_LABEL = "updateCount : ";

	public static final String RESULT_STATUS_ATTR = "resultStatus";
	public static final String ACTIVATION_ERROR_ATTR = "activation-error";

	public static final String XML_VERSION_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	public static final String NOTIFICATION_USER_MESSAGE_OPEN_STRING = "<notificationUserMessage>";
	public static final String XML_OPEN_MESSAGE = "<message>";
	public static final String XML_CLOSE_MESSAGE = "</message>";
	public static final String NOTIFICATION_USER_MESSAGE_CLOSE_STRING = "</notificationUserMessage>";
	public static final String VALIDITY_PERIOD_SECS_60_XML = "<validityPeriodSecs>60</validityPeriodSecs>";
	public static final String CALLBACK_URL_OPEN_XML = "<callbackUrl>";
	public static final String CALLBACK_URL_CLOSE_XML = "</callbackUrl>";
	public static final String CALLBACK_USER_XML = "<callbackUser></callbackUser>";
	public static final String CALLBACK_PASSWORD_XML = "<callbackPassword></callbackPassword>";

	public static final String AUTHORIZATION = "Authorization";
	public static final String BASIC_AUTH_EPS = "Basic bWJfZ3RvZXppb2RlbW9fYWRtaW46RUE5VUxHY0twME5xeXd1Rlg3WWRiNS9NMVcvbXBveVY=";
}
