package com.gemalto.eziomobile.demo.common;

public class EzioMobileDemoConstant {

	// Default notification profile value
	public static final String DEFAULT_NOTIFICATION_PROFILE = "PUSH:452418129615";

	public static final String DEFAULT_NOTIFICATION_PROFILE_IDCLOUD = "AMARIS_PROD:452418129615";
	
	public static final String CHALLENGE_30000003 = "30000003";

	public static final String DEFAULT_ERROR_MSG = "Something went wrong!";
	
	public static final String NO_RECORD_FOUND_MSG = "NO_RECORD_FOUND";
	
	public static final String CURRENCY_USD = "USD"; 
	
	//DeviceMaster - TokenID
	public static final String DEPRECATED_TOKEN_ID = "DEPRECATED";
	
	//Authentication type
	public static final String AUTHENTICATION_TYPE_1FA = "01";
	public static final String AUTHENTICATION_TYPE_2FA = "02";
	
	//mobile response, status
	public static final String STATUS_NOK = "F0";
	public static final String STATUS_OK = "0F";
	
	//E-commerce
	public static final String EZIO_SHOP = "Ezio shop";
	
	//Exp date for DCV Physical Cards
	//Card Issuance
	public static final String EZIO_DCV_CARDS_EXP_DATE = "07/30";
	public static final String EZIO_CARD_ISSUANCE_STATIC_CVV = "888";
	public static final String EZIO_CARD_ISSUANCE_CARD_TYPE_VISA = "VISA";
	public static final String EZIO_CARD_ISSUANCE_CARD_TYPE_MASTER_CARD = "MASTERCARD";
	public static final String EZIO_CARD_ISSUANCE_CARD_TYPE_AMEX = "AMEX";
	
	public static final String EZIO_CARD_ISSUANCE_PREFIX_VISA = "4";
	public static final String EZIO_CARD_ISSUANCE_PREFIX_MASTER_CARD = "5";
	public static final String EZIO_CARD_ISSUANCE_PREFIX_AMEX = "37";
	
	public static final int EZIO_CARD_ISSUANCE_VISA_LENGTH = 16;
	public static final int EZIO_CARD_ISSUANCE_MASTER_CARD_LENGTH = 16;
	public static final int EZIO_CARD_ISSUANCE_AMEX_LENGTH = 15;
	
	//ATM
	public static final String CASH_WITHDRAWAL ="Cash Withdrawal";
	
	//New Beneficiary
	public static final String BENEFICIARY_AVAILABLE = "Beneficiary exists";
	public static final String BENEFICIARY_NOT_AVAILABLE = "Beneficiary does not exists";
	
	
	//PAN TYPE
	public static final String PAN_TYPE_VISA_PREFIX_4 = "4";
	public static final String PAN_TYPE_MASTERCARD_PREFIX_5 = "5";
	public static final String PAN_TYPE_MY_VISA = "My VISA";
	public static final String PAN_TYPE_MY_MASTERCARD = "My MASTERCARD";
	public static final String PAN_TYPE_VISA = "VISA";
	public static final String PAN_TYPE_MASTERCARD = "MASTERCARD";
	public static final String PAN_TYPE_GEMALTO_DCV = "GEMALTO DCV #";
	
	//Get Cards Operations
	public static final String CARDMANAGEMENT_GET_CARD_ECOMMERCE = "ECOMMERCE";
	public static final String CARDMANAGEMENT_GET_CARD_MY_WALLET = "MY_WALLET";
	
	//Account type
	public static final String ACCOUNT_TYPE_CARD = "Pay Credit Card";
	public static final String ACCOUNT_TYPE_SAVINGS = "Savings";
	
	//Account Summary JSON
	public static final String ACCOUNT_TYPE_PAY_CREDIT_CARD = "Pay credit card accounts";
	public static final String ACCOUNT_TYPE_CURRENT_OR_SAVINGS = "Current/Savings accounts";
	public static final String ACCOUNT_TYPE_CREDIT = "Credit";
	public static final String ACCOUNT_TYPE_DEBIT = "Debit";
	
	public static final int ACCOUNT_BALANCE_DEFAULT_5000 = 5000;
	public static final int ACCOUNT_BALANCE_0 = 0;

	// Login methods
	public static final int AUTHMODE_USERNAME_AND_PASSWORD = 0x0;
	public static final int AUTHMODE_USERNAME_AND_OTP_TIME_BASED = 0x1;
	public static final int AUTHMODE_USERNAME_AND_OTP_EVENT_BASED = 0x2;

	// Login type
	public static final String AUTH_LOGIN_TYPE_PASSWORD = "LOGIN_PASSWORD";
	public static final String AUTH_LOGIN_TYPE_OTP = "LOGIN_OTP";

	// Invalid username or password or OTP msg
	public static final String INVALID_USERNAME_OR_PASSWORD_MSG = "INVALID_USERNAME_OR_PASSWORD";
	public static final String INVALID_USERNAME_OR_OTP_MSG = "INVALID_USERNAME_OR_OTP";

	public static final String VALID_USER = "Valid_USER";
	
	public static final String OTP_VALIDATION_SUCCESS = "OTP_VALIDATION_SUCCESS";
	public static final String OTP_VALIDATION_FAILED = "OTP_VALIDATION_FAILED";

	// User state in CAS
	public static final String CAS_USER_STATE_ACTIVE = "1";
	public static final String CAS_USER_STATE_REVOKED = "3";

	// OOBS : Device/token type
	public static final String DEVICE_MOBILE_TYPE_1 = "1";
	public static final String DEVICE_MOBILE_TYPE_3 = "3";
	public static final String DEVICE_MOBILE_TYPE_8 = "8";
	

	// OOBS : Transaction type
	public static final int OOBS_PENDING_TRANSACTION = 1;
	public static final int OOBS_NO_PENDING_TRANSACTION = 0;
	
	public static final String OOBS_PENDING_TRANSACTION_MSG = "PENDING_TRANSACTION";
	public static final String OOBS_NO_PENDING_TRANSACTION_MSG = "NO_PENDING_TRANSACTION";
	public static final String OOBS_NO_MOBILE_FOUND = "NO_MOBILE_FOUND_IN_OOBS";

	// OOBS : Message type : Transaction Verification/DispatchMessageRequest
	// Push notification msg type
	// Msgtype :
	// 01 (TV – Transaction Varification)- Login, Money transfer, Add
	// Beneficiary, 3DS
	// 02 (Dispatch) - P2P
	public static final String OOBS_MESSAGE_TYPE_TRANSACTION_VERIFICATION = "01";
	public static final String OOBS_MESSAGE_TYPE_DISPATCH = "02";

	// OOBS - Local DB - OOBSMaster table : Message Status
	public static final String OOBS_MESSAGE_STATUS_NO_ERROR_DETECTED = "00";
	public static final String OOBS_MESSAGE_STATUS_ERROR_DETECTED = "FF";

	// OOBS - Push Notification
	public static final String PUSH_NOTIFICATION_SENT = "NOTIFICATION_SENT";
	public static final String PUSH_NOTIFICATION_NOT_SENT = "NOTIFICATION_NOT_SENT";
	
	//Notification profile message
	public static final String OOBS_PUSH_NOTIFICATION_PROFILE_AVAILABLE = "NOTIFICATION_PROFILE_AVAILABLE";
	public static final String OOBS_PUSH_NOTIFICATION_PROFILE_NOT_AVAILABLE = "NOTIFICATION_PROFILE_NOT_AVAILABLE";
	
	//Card Management
	public static final String CARD_MANAGEMENT_ON = "ON";
	public static final String CARD_MANAGEMENT_OFF = "OFF";
	public static final String TRANSACTION_REJECTED = "TRANSACTION_REJECTED";

	// Response code
	public static final int RESPONSE_CODE_200 = 200;
	public static final int RESPONSE_CODE_401 = 401;
	public static final int RESPONSE_CODE_NO_CONTENT_204 = 204;
	public static final int RESPONSE_CODE_EXPECTATION_FAILED_417 = 417;
	public static final int RESPONSE_CODE_CONFLICT_409 = 409;
	public static final int RESPONSE_CODE_BAD_REQUEST_400 = 400;
	public static final int RESPONSE_CODE_NOT_FOUND_404 = 404;
	public static final int RESPONSE_CODE_FORBIDDEN_403 = 403;
	public static final int RESPONSE_CODE_UNKNOWN_500 = 500;
	
	public static final String RESPONSE_CODE_OK = "OK";
	public static final String RESPONSE_CODE_NOT_OK = "NOT_OK";
	
	// String Response code
	public static final String STR_RESPONSE_CODE_200 = "200";
	public static final String STR_RESPONSE_CODE_401 = "401";
	
	// User status for Login
	public static final String EZIODEMO_USER_FOUND_IN_DB = "USER_FOUND_IN_DB";
	public static final String EZIODEMO_USER_FOUND_IN_DB_CAS = "USER_FOUND_IN_DB_CAS";
	public static final String EZIODEMO_USER_FOUND_IN_DB_CAS_OOBS = "USER_FOUND_IN_DB_CAS_OOBS";
	public static final String EZIODEMO_USER_NOT_FOUND = "USER_NOT_FOUND";

	// User state for Login : Response status
	public static final int EZIODEMO_USER_NOT_FOUND_0 = 0;
	public static final int EZIODEMO_USER_FOUND_IN_DB_1 = 1;
	public static final int EZIODEMO_USER_FOUND_IN_DB_CAS_2 = 2;
	public static final int EZIODEMO_USER_FOUND_IN_DB_CAS_OOBS_3 = 3;

	public static final int EZIO_STATUS_VALUE_0 = 0;
	public static final int EZIO_STATUS_VALUE_1 = 1;
	public static final int EZIO_STATUS_VALUE_2 = 2;

	// Ezio User Account type (Local DB)
	public static final int EZIO_ACCOUNT_TYPE_0 = 0;
	public static final int EZIO_ACCOUNT_TYPE_1 = 1;
	
	//Callback result response
	public static final String CALLBACK_RESULT_RESPONSE_NA = "NA";
	public static final String CALLBACK_RESULT_RESPONSE_VALID_OTP = "VALID_OTP";
	public static final String CALLBACK_RESULT_RESPONSE_INVALID_OTP = "INVALID_OTP";
	public static final String CALLBACK_RESULT_RESPONSE_INVALID_HASHED_DATA = "INVALID_HASHED_DATA";
	public static final String CALLBACK_RESULT_RESPONSE_TRANSACTION_REJECTED = "TRANSACTION_REJECTED"; 
	public static final String CALLBACK_RESULT_RESPONSE_TRANSACTION_VALIDATED_WITH_1FA = "TRANSACTION_VALIDATED_WITH_1FA";
	
	//Mobile Registration
	public static final String MOBILE_REGISTRATION_DONE = "MOBILE_REGISTRATION_DONE";
	public static final String MOBILE_REGISTRATION_MOBILE_NOT_REGISTERED_ON_OOBS = "MOBILE_NOT_REGISTERED_ON_OOBS";
	public static final String MOBILE_REGISTRATION_MOBILE_ENROLLMENT_ERROR = "MOBILE_ENROLLMENT_ERROR";
	
	//EMV card creation 
	public static final String EMV_CARD_CREATE_LINK_ACTIVATE = "EMV_CARD_CREATE_LINK_ACTIVATE";
	public static final String EMV_CARD_ERROR = "EMV_CARD_ERROR";
	public static final String EMV_CARD_PAN_NOT_FOUND_ERROR_OR_ACCOUNT_ALREADY_SETUP = "EMV_CARD_PAN_NOT_FOUND_ERROR_OR_ACCOUNT_ALREADY_SETUP";

	//money transfer
	public static final String ACCOUNT_TRANSFER_TYPE_DOMESTIC = "DOMESTIC_TRANSFER";
	public static final String ACCOUNT_TRANSFER_TYPE_EXTERNAL = "EXTERNAL_TRANSFER";

	public static final String RESPONSE_SUCCESS = "SUCCESS";
	public static final String RESPONSE_FAILURE = "FAILED";
	public static final String RESPONSE_FORBIDDEN = "FORBIDDEN";
	
	//Operations
	public static final String OPERATION_TYPE_LOGIN = "EzioDemoV2_Login";
	public static final String OPERATION_TYPE_NEW_BENEFICIARY = "EzioDemoV2_NewBeneficiary";
	public static final String OPERATION_TYPE_ECOMMERCE3DS = "EzioDemoV2_eCommerce3DS";
	public static final String OPERATION_TYPE_ECOMMERCE_DCV = "EzioDemoV2_eCommerceDCV";
	public static final String OPERATION_TYPE_MONEY_TRANSFER = "EzioDemoV2_MoneyTransfer";
	public static final String OPERATION_TYPE_CARD_ISSUANCE = "EzioDemoV2_CardIssuance";
	
	public static final String OPERATION_TYPE_P2P_02 = "02";
	public static final String OPERATION_TYPE_LOGIN_01 = "01";
	public static final String OPERATION_TYPE_NEW_BENEFICIARY_11 = "11";
	public static final String OPERATION_TYPE_MONEY_TRANSFER_12 = "12";
	public static final String OPERATION_TYPE_ECOMMERCE3DS_13 = "13";
	public static final String OPERATION_TYPE_CARD_ISSUANCE_14 = "14";
	public static final String OPERATION_TYPE_UNKNOWN_00 = "00";
	
	//Push notification message content
	public static final String NOTIFICATION_MSG_TYPE_LOGIN = "A payment to the Ezio Mobile Demo shop is pending your approval";
	public static final String NOTIFICATION_MSG_TYPE_NEW_BENEFICIARY = "You have an Add Beneficiary request.";
	public static final String NOTIFICATION_MSG_TYPE_MONEY_TRANSFER = "You have a Money transfer request.";
	public static final String NOTIFICATION_MSG_TYPE_ECOMMERCE3DS = "A payment to the Ezio shop is pending your approval.";
	public static final String NOTIFICATION_MSG_TYPE_CARD_ISSUANCE = "You have a new card request.";
	
	//DCV Activation error messages
	public static final String DCV_ACTIVATION_MSG_TYPE_BAD_LENGTH = "BAD_LENGTH";
	public static final String DCV_ACTIVATION_MSG_TYPE_INVALID_PAN_NUMBER = "INVALID_PAN_NUMBER";
	public static final String DCV_ACTIVATION_MSG_TYPE_CARD_ALREADY_ASSOCIATED = "CARD_ALREADY_ASSOCIATED";
	public static final String DCV_ACTIVATION_MSG_TYPE_UNKNOWN = "UNKNOWN";
	public static final String DCV_ACTIVATION_MSG_TYPE_GENERAL_ERROR = "GENERAL_ERROR";
	public static final String DCV_ACTIVATION_MSG_TYPE_ALL_OK = "ALL_OK";
	
	
	//Token management error messages
	public static final String TOKEN_MANAGMENT_MSG_TYPE_BAD_FORMAT = "BAD_FORMAT";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_TOKEN_NOT_FOUND = "TOKEN_NOT_FOUND";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_TOKEN_ALREADY_ASSOCIATED = "TOKEN_ALREADY_ASSOCIATED";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_GENERAL_ERROR = "GENERAL_ERROR";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_TOKEN_BLOCKED_OR_REVOKED = "TOKEN_BLOCKED_OR_REVOKED";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_INITIALIZED = "ALL_OK_INITIALIZED";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_INITIALIZED_OTP_VALIDATION_FAILED = "ALL_OK_INITIALIZED_OTP_VALIDATION_FAILED";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_ACTIVATED_OTP_VALIDATION_FAILED = "ALL_OK_ACTIVATED_OTP_VALIDATION_FAILED";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_ALL_OK_ACTIVATED = "ALL_OK_ACTIVATED";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_UNKNOWN = "UNKNOWN";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_LINK_OR_ACTIVATION_FAILED = "LINK_OR_ACTIVATION_FAILED";
	public static final String TOKEN_MANAGMENT_MSG_TYPE_LINK_DEVICE_TO_USER_FAILED = "LINK_DEVICE_TO_USER_FAILED";
	
	//Token Provisioning
	public static final String TOKEN_PROVISIONING_MSG_TYPE_PROVISIONING_FAILED = "Provisioning failed. Please make sure your provisioning or passphrase file is correct.";
	public static final String TOKEN_PROVISIONING_MSG_TYPE_FILE_SUCCESSFULLY_UPLOADED = "FILE_SUCCESSFULLY_UPLOADED";
	public static final String TOKEN_PROVISIONING_MSG_TYPE_FILE_COULD_NOT_UPLOAD = "FILE_COULD_NOT_UPLOAD";
	public static final String TOKEN_PROVISIONING_MSG_TYPE_NOT_WELL_FORMATTED = "NOT_WELL_FORMATTED";
	public static final String TOKEN_PROVISIONING_MSG_TYPE_NULL_EXCEPTION = "NULL_EXCEPTION";
	public static final String TOKEN_PROVISIONING_MSG_TYPE_GENERAL_EXCEPTION = "GENERAL_EXCEPTION";
	public static final String TOKEN_PROVISIONING_MSG_TYPE_IO_EXCEPTION = "IO_EXCEPTION";
	
	//Token Provisioning status
	public static final String TOKEN_PROVISIONING_STATUS_DONE = "DONE";
	public static final String TOKEN_PROVISIONING_STATUS_FAILED = "FAILED";
	public static final String TOKEN_PROVISIONING_STATUS_RUNNING = "RUNNING";
	public static final String TOKEN_PROVISIONING_STATUS_NOT_STARTED = "NOT_STARTED";
	
	//Token management operations
	public static final String OPERATION_TOKEN_ACTIVATION = "TOKEN_ACTIVATION";
	public static final String OPERATION_TOKEN_RESYNCHRONIZATION = "TOKEN_RESYNCHRONIZATION";
	
	//Token Resyncronization
	public static final String TOKEN_RESYNCHRONIZATION_MSG_TYPE_FAILED = "TOKEN_RESYNCHRONIZATION_FAILED";
	public static final String TOKEN_RESYNCHRONIZATION_MSG_TYPE_DONE = "TOKEN_RESYNCHRONIZATION_DONE";

	//Permissions
	public static final String GAH = "GAH";
	public static final String USER_PREFERENCE = "UserPreference";
	public static final String TOKEN_PROVISION = "TokenPro";
	public static final String TOKEN_ACT_RESYNC = "TokenActResync";
	public static final String CARD_ISSUANCE = "CardIssuance";
	public static final String REPORT_BACKEND = "ReportBackend";
	public static final String DCV = "DCV";
	public static final String P2P = "P2P";
	
	//Card Issuance Error
	public static final String CARD_ISSUANCE_MAX_CARD_LIMIT_REACHED = "MAX_CARD_LIMIT_REACHED";
	public static final String CARD_ISSUANCE_REQUEST_PENDING = "CARD_ISSUANCE_REQUEST_PENDING";
	
	//CardIssuance / PanMaster
	public static final int PAN_TYPE_GENERIC_PAN = 0;
	public static final int PAN_TYPE_NEW_PAN = 1;
	

	//Group name
	public static final String GROUP_NAME_BLACK_LIST = "BLACK_LIST";

	//User role
	public static final String USER_ROLE_000 = "000";
	public static final String USER_ROLE_001 = "001";
	
	public static final String USER_ROLE_BLACK_LIST = "17E";
	public static final String USER_ROLE_NON_BLACK_LIST = "17F";
	
	//User Account Registration
	public static final String USER_REGISTRATION_INVALID_ACTIVATION_KEY_MSG = "INVALID_LINK";
	public static final String USER_REGISTRATION_USER_EXIST_MSG = "USERNAME_ALREADY_EXIST";
	public static final String USER_REGISTRATION_USER_AVAILABLE_MSG = "USERNAME_AVAILABLE";
	
	
	//Ezio Demo send email
	public static final String EMAIL_AMARIS_DEMO_FROM = "amaris.ovh@gmail.com";
	
	public static final String EMAIL_SUBJECT_SUPPORT_NEW_ACCOUNT = "New account creation";
	public static final String EMAIL_SUBJECT_SUPPORT_FORGET_USERNAME_PASSWORD = "Forgot username / forgot password";
	public static final String EMAIL_MESSAGE_LINE_1 = "Thanks for choosing Ezio Mobile Demo!";
	public static final String EMAIL_MESSAGE_LINE_2 = "Here are your login details:";
	public static final String EMAIL_MESSAGE_LINE_3 = "Just click on this link to activate your account:";
	public static final String EMAIL_MESSAGE_LINE_4 = "You are now ready to use the app.";
	public static final String EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_3_1 = "You have ";
	public static final String EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_3_2 = " account registered with this e-mail.";
	public static final String EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_3_3 = "Just click on this link to recover your account.";
	public static final String EMAIL_MESSAGE_FORGET_USER_DETAILS_LINE_4 = "You are now ready to use the app again!";
	public static final String EMAIL_MESSAGE_LINE_5 = "Any trouble? Reply to this email and let us know!";
	public static final String EMAIL_TERMINOLOGY = "The Gemalto Demo Center";
	
	//Forget Password / userName
	public static final String INVALID_USERID = "INVALID_USERID";

	//Batch Transfer
	public static final String NO_TOKEN_FOUND = "NO_TOKEN_FOUND";
	public static final String OK_AUTHENT = "OK_AUTHENT";
	public static final String ERROR_AUTHENT = "ERROR_AUTHENT";
    public static final String VERIFYTRANSACTIONCALLBACK = "verifytransactioncallback";
	public static final String DELIVERYCALLBACK = "deliverycallback";
	public static final String READNOTIFYCALLBACK = "readnotifycallback";
	public static final String REPLYCALLBACK = "replycallback";
	public static final String POSTCALLBACK = "postcallback";
	public static final String EXPIRECALLBACK = "expirecallback";
	public static final String ERRORREPORTCALLBACK = "errorreportcallback";
	public static final String CLIENTNOTIFICATIONPROFILEUPDATEDCALLBACK = "clientnotificationprofileupdatedcallback";
	public static final String USERMESSAGECALLBACK = "usermessagecallback";
	public static final String NOTIFICATIONCALLBACK = "notificationcallback";
	public static final String CLIENTREGISTEREDCALLBACK = "clientregisteredcallback";
	public static final String CLIENTUNREGISTERCALLBACK = "clientunregistercallback";
	public static final String SIGNTRANSACTIONCALLBACK = "signtransactioncallback";
	public static final String GATB = "GATB";
	public static final String GATZ = "GATZ";
	public static final String GADB = "GADB";
	public static final String GADF = "GADF";
	public static final String GAQT = "GAQT";
	public static final String GATP = "GATP";
	public static final String GAOC = "GAOC";
    public static final String PREPARED_TRANSACTION_OBJECT_LABEL = "Prepared Transaction Object : ";
	//
	public enum RoleId {
		ROLE_USER, ROLE_ADMIN
	}
}
