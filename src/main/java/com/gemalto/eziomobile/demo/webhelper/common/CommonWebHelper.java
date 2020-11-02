package com.gemalto.eziomobile.demo.webhelper.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.codec.binary.Base64;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gemalto.eziomobile.demo.common.CommonOperationsConstants;
import com.gemalto.eziomobile.demo.common.EzioDemoIDCloudConstant;
import com.gemalto.eziomobile.demo.common.EzioMobileDemoConstant;
import com.gemalto.eziomobile.demo.exception.ServiceException;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;
import com.gemalto.eziomobile.demo.service.master.MasterService;
import com.gemalto.eziomobile.demo.service.qrtokenmaster.QRTokenmasterService;
import com.gemalto.eziomobile.demo.service.usermaster.UsermasterService;
import com.gemalto.eziomobile.demo.util.URLUtil;
import com.gemalto.eziomobile.demo.webhelper.login.LoginWebHelper;
import com.gemalto.eziomobile.webhelper.cas.CASWebHelper;

import com.gemalto.mno.qrip.*;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

@Component
public class CommonWebHelper {

    private static final LoggerUtil logger = new LoggerUtil(CommonWebHelper.class);
    public static final String AUTHORISATION_TOKEN = "bWJfZ3RvZXppb2RlbW9fYWRtaW46RUE5VUxHY0twME5xeXd1Rlg3WWRiNS9NMVcvbXBveVY=";
    public static final String NIMBUS_AUTHORISATION_TOKEN = "ZGVmYXVsdDpkZWZhdWx0";
    public static final String B_LABEL = "Basic ";


    private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;

    public CommonWebHelper() {
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Cannot initiate a new document builder: {}.", e);
        }
    }


    private static String headerJsession = "";


    private final static int BIT_RM = (int) 0x100;

    @Autowired
    private URLUtil urlUtil;

    @Autowired
    private MasterService masterService;

    @Autowired
    private UsermasterService userMasterService;

    @Autowired
    private LoginWebHelper loginWebHelper;

    @Autowired
    private QRTokenmasterService qrTokenmasterService;

    @Autowired
    CASWebHelper casWebHelper;

    /**
     * This method is to validate OTP for Token-Activation, login
     *
     * @param userId
     * @param otpValue
     * @return boolean value based on the result.
     * <p>
     * Type of Tx , userId, hashedData, OTP
     */
    public boolean validateOTP(String userId, String otpValue) {

        logger.info("validating OTP........");
        logger.info("validating OTP : " + otpValue + " and UserId : " + userId);

        int iCurrentAuthMode = 0;
        boolean isValidUser = false;

        iCurrentAuthMode = AUTHMODE_USERNAME_AND_OTP_TIME_BASED;
        logger.info("<------- [validateOTP] iCurrentAuthMode : " + iCurrentAuthMode + "-------->");

        isValidUser = loginWebHelper.checkLogin(userId, otpValue, iCurrentAuthMode);
        logger.info("[validateOTP] isValidUser : " + isValidUser);

        if (!isValidUser && iCurrentAuthMode == AUTHMODE_USERNAME_AND_OTP_TIME_BASED) {

            iCurrentAuthMode = AUTHMODE_USERNAME_AND_OTP_EVENT_BASED;
            logger.info("<------- [validateOTP] iCurrentAuthMode : " + iCurrentAuthMode + "-------->");

            isValidUser = loginWebHelper.checkLogin(userId, otpValue, iCurrentAuthMode);
        }
        return isValidUser;
    }

    /**
     * @param s
     * @return
     */
    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * @param dataToSignInHexa
     * @return
     */
    public String getSHA1(String dataToSignInHexa) {
        return DigestUtils.sha1Hex(hexStringToByteArray(dataToSignInHexa));
    }

    /**
     * @param ascii
     * @return
     */
    public String asciiToHex(String ascii) {
        StringBuilder hex = new StringBuilder();

        for (int i = 0; i < ascii.length(); i++) {
            hex.append(Integer.toHexString(ascii.charAt(i)));
        }
        return hex.toString();
    }

    /**
     * This method is to validate OTP from CAS server
     *
     * @param userId
     * @param otp
     * @param hashDataOrTxData
     * @return will be true - if OTP validated / false - if OTP is not validated
     */
    public boolean validateOTP(String userId, String otp, String hashDataOrTxData) {

        logger.info("Validating OTP....");

        String pushresponseData = "";
        String _pushurl = "";
        boolean isOTPVerfied = false;
        String pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<AuthenticationRequest><UserID>" + userId
                + "</UserID><OTP>" + otp + "</OTP><OcraData><ServerChallenge><Value>" + hashDataOrTxData
                + "</Value></ServerChallenge></OcraData></AuthenticationRequest>";
        try {
            logger.info("Validate OTP [pushXmlData] : " + pushXmlData);

           _pushurl = urlUtil.casServerURL();

            logger.info(CommonOperationsConstants.PUSHURL + _pushurl);
            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

            logger.info("making a POST call....");

            pushcon.setRequestMethod(POST);
            pushcon.setRequestProperty(CommonOperationsConstants.USER_AGENT, CommonOperationsConstants.MOZILLA_5_0);
            pushcon.setRequestProperty(CommonOperationsConstants.CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT_LANGUAGE, CommonOperationsConstants.EN_US_EN_Q_0_5);
            pushcon.setDoOutput(true);
            PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
            pushpw.write(pushXmlData);

            pushpw.flush();
            pushpw.close();
            // For POST only - END

            int pushresponseCode = pushcon.getResponseCode();
            pushresponseData = Integer.toString(pushresponseCode);

            logger.info("pushresponseCode: " + pushresponseCode);

            if (pushresponseCode == HttpURLConnection.HTTP_OK) {
                logger.info("Success!");
                logger.info("OTP validation success!");

                isOTPVerfied = true;
                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                logger.info("\n[OTP validate - Success] Response data: " + pushresponseData + "\n");
            } else {
                logger.info("Failed!");
                logger.info("OTP validation failed!");

                isOTPVerfied = false;
                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                logger.info("[OTP validate - Failed] Response data: " + pushresponseData);
            }

        } catch (Exception e) {
            isOTPVerfied = false;
            e.printStackTrace();
            pushresponseData = e.toString();
            logger.info("[OTP validate] Exception! : " + pushresponseData);
        }
        return isOTPVerfied;
    }

    /**
     * This method is used for validating OTP_OATH
     *
     * @param userId
     * @param otp
     * @param newtransactiondata
     * @return
     */
    public boolean validateOTP_OATH(String userId, String otp, String newtransactiondata) {

        logger.info("validate OTP from CAS");

        String pushresponseData = "";
        String _pushurl = "";
        boolean isOTPVerfied = false;
        String pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<AuthenticationRequest><UserID>" + userId
                + "</UserID><OTP>" + otp + "</OTP><OcraData><ServerChallenge><Value>" + newtransactiondata
                + "</Value></ServerChallenge></OcraData></AuthenticationRequest>";
        try {
            logger.info("Validate OTP pushXmlData: " + pushXmlData);

            // _pushurl = "http://10.10.84.139:80/saserver/master/api/auth/otb";
            _pushurl = urlUtil.casServerURL();
            _pushurl = _pushurl.replace("otb", "oath");

            logger.info(CommonOperationsConstants.PUSHURL + _pushurl);

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

            logger.info("CAS, making a server call.......... ");

            pushcon.setRequestMethod(POST);
            pushcon.setRequestProperty(CommonOperationsConstants.USER_AGENT, CommonOperationsConstants.MOZILLA_5_0);
            pushcon.setRequestProperty(CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT_LANGUAGE, CommonOperationsConstants.EN_US_EN_Q_0_5);

            pushcon.setDoOutput(true);
            PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
            pushpw.write(pushXmlData);

            pushpw.flush();
            pushpw.close();
            // For POST only - END

            int pushresponseCode = pushcon.getResponseCode();
            pushresponseData = Integer.toString(pushresponseCode);

            if (pushresponseCode == HttpURLConnection.HTTP_OK) {
                logger.info("Success... OTP validated ");
                logger.info("OK.........");

                isOTPVerfied = true;
                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                logger.info(" success response : " + pushresponseData);
            } else {
                logger.info(" NOT-OK response ");

                isOTPVerfied = false;
                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                logger.info("Error : " + pushresponseData);
            }

        } catch (Exception e) {
            isOTPVerfied = false;
            e.printStackTrace();
            pushresponseData = e.toString();
            logger.error("Exception occurred : " + pushresponseData);
        }
        return isOTPVerfied;
    }


    /**
     * This method fetches all the token devices from cas server for a user
     *
     * @param userId
     * @return
     */
    public Map<String, String> getDeviceStatus(String userId) {
        //boolean isUserAccountReset = false;
        String pushresponseData = "";
        String _pushurl = "";
        String casURL = "";
        int iDeviceRes = 0;

        Map<String, String> supportDeviceMap = new HashMap<String, String>();

        try {
            casURL = urlUtil.findUserFromCASServerURL();
            _pushurl = casURL + userId;
            logger.info("_pushURL : " + _pushurl);

            synchronized (headerJsession) {
                headerJsession = CASWebHelper.headerJsession;
            }

            logger.info(
                    "\n HeaderJsession of getDeviceStatus )...... "
                            + headerJsession);

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();

            pushcon.setRequestMethod(CommonOperationsConstants.GET);
            pushcon.setRequestProperty(CommonOperationsConstants.CONTENT_TYPE_PROP, "application/x-www-form-urlencoded");
            pushcon.setRequestProperty(CommonOperationsConstants.COOKIE, headerJsession);

            int pushresponseCode = pushcon.getResponseCode();
            pushresponseData = Integer.toString(pushresponseCode);


            if (pushresponseCode == HttpURLConnection.HTTP_OK) {

                logger.info("getDeviceStatus Success User (++++++  getDeviceStatus ++++++)");

                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()
                String pushinputLine = "";
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();

                pushresponseData = _pushresponse.toString();
                logger.info(
                        "pushResponseData (++++++  getDeviceStatus ++++++) : " + pushresponseData);

                InputSource is = new InputSource(new StringReader(pushresponseData));
                Document usersdoc = builder.parse(is);

                NodeList nList = usersdoc.getElementsByTagName("TokenName");
                String deviceCount = "00";
                for (int i = 0; i < nList.getLength(); i++) {
                    logger.info("\n" + nList.item(i).getChildNodes().item(0).getNodeValue() + " at position : [" + i + "] : " + nList.item(i).getChildNodes().item(0).getNodeValue().substring(0, 4));

                    switch (nList.item(i).getChildNodes().item(0).getNodeValue().substring(0, 4)) {
                        case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_LOGIN:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_MOBILE_LOGIN;
                            supportDeviceMap.put("loginDeviceOne", "GALO");
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_SIGNATURE_TRANSACTION:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_MOBILE_SIGNATURE_TRANSACTION;
                            supportDeviceMap.put("loginDeviceTwo", "GAOC");
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_DCV:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_MOBILE_DCV;
                            deviceCount = "01";
                            supportDeviceMap.put("dcvDevice", "GADV");
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_FLEX:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_FLEX;
                            deviceCount = "01";
                            supportDeviceMap.put("flexDevice", "GATB");
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_SIGNER:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_SIGNER;
                            deviceCount = "01";
                            supportDeviceMap.put("signerDevice", "GATZ");
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_QRTOKEN;
                            deviceCount = "01";
                            supportDeviceMap.put("qrtokenDevice", "GAQT");
                            supportDeviceMap.put("qrtokenDeviceName", nList.item(i).getChildNodes().item(0).getNodeValue());
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_PICO:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_PICO;
                            deviceCount = "01";
                            supportDeviceMap.put("picoDevice", "GATP");
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_DISPLAY_CARD_PAD;
                            deviceCount = "01";
                            supportDeviceMap.put("displaycardpadDevice", "GADF");
                            break;
                        case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD_EB:
                            iDeviceRes |= CommonOperationsConstants.DEVICE_DISPLAY_CARD_PAD_EB;
                            deviceCount = "01";
                            supportDeviceMap.put("displaycardpadDeviceEB", "GADB");
                            break;
                        default:
                            break;
                    }
                    logger.info("iDeviceRes at position [" + i + "] value : " + iDeviceRes);
                }        //1+2+4+16+32+64
                supportDeviceMap.put("tokenDeviceCount", deviceCount);
                logger.info("\n\n final map : " + supportDeviceMap.toString());
            } else {

                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
            }
        } catch (Exception e) {
            logger.error("iDeviceRes.......");
            e.printStackTrace();
            pushresponseData = e.toString();
        }
        return supportDeviceMap;
    }


    /**
     * @param data
     * @return
     */
    public static String Veoref(String data) {
        String sTableD = "0123456789" + "1234067895" + "2340178956" + "3401289567" + "4012395678" + "5987604321" + "6598710432" + "7659821043" + "8765932104" + "9876543210";
        String sTableP = "0123456789" + "1576283094" + "5803796142" + "8916043527" + "9453126870" + "4286573901" + "2793806415" + "7046913258";
        String sTableI = "0432156789";
        String sA = "0";
        String sP = "0";
        String sVeoref = "";
        int j = 0;
        int k = 0;

        try {
            for (int i = 1; i <= data.length(); i++) {
                j = data.length() - i;
                k = Integer.parseInt(data.substring(j, j + 1));
                k += i * 10;
                sP = sTableP.substring(k, k + 1);
                j = Integer.parseInt(sP) + Integer.parseInt(sA) * 10;
                sA = sTableD.substring(j, j + 1);
                logger.info("\n sA : " + sA + "  " + i);
            }
            j = Integer.parseInt(sA);
            sVeoref = sTableI.substring(j, j + 1);
        } catch (Exception e) {
            logger.error("exception occurred in CommonWebHelper - Veoref method");
            e.printStackTrace();
        }
        return sVeoref;
    }


    /**
     * This method generates the challenge code
     *
     * @param iMode
     * @param iNbDigits
     * @return
     */
    public String getChallenge(int iMode, int iNbDigits) {
        long iChallenge = 0;
        long i = 0;
        String sChallenge = "";

        try {
            i = (long) Math.pow(10, (iNbDigits - 1)) - 47;
            iChallenge = ThreadLocalRandom.current().nextLong(i);
            iChallenge = iChallenge - iChallenge % 47;
            sChallenge = "00000000" + Long.toString(iChallenge + iMode * 2);
            i = sChallenge.length() - iNbDigits + 1;
            sChallenge = sChallenge.substring((int) i);
            sChallenge = sChallenge + Veoref(sChallenge);
        } catch (Exception e) {
            logger.error("exception occurred in CommonWebHelper -  getChallenge method");
            e.printStackTrace();
        }
        return sChallenge;
    }

    /*
     ** RESET ACCOUNT SECTION
     */

    // PAY ATTANTION TO THE ORDER ORDER:
    // (1) UNLINK
    // (2) REVOKE
    // then (3) DELETE
    public boolean manageTokenAction(String sJSESSION, String sUserID, String sTokenName, int[] iArrAction) {
        int iAction = 0;
        boolean bExceuteRes = false;
        if (iArrAction == null)
            return false;
        for (int i = 0; i < iArrAction.length; i++) {
            iAction = iArrAction[i];
            //execute and return false in case of any error
            bExceuteRes = executeSingleTokenAction(sJSESSION, sUserID, sTokenName, iAction);

            if (!bExceuteRes) {
                //no need to go further, not normal
                return false;
            }
        }
        return true;
    }

    public String getSeedName(String sJSESSION, String sTokenName, String sTokenType) {
        String pushresponseData = "";
        String sAuthType = "Otb";
        String sSeedName = null;

        switch (Integer.parseInt(sTokenType)) {
            case CommonOperationsConstants.MULTI_SEED_DEVICE:
                //return Seed Name
                break;
            case CommonOperationsConstants.SINGLE_SEED_TOTP_DEVICE:
            case CommonOperationsConstants.EZIO_MOBILE_PROTECTOR:
            case CommonOperationsConstants.DCVV_DEVICE:
            case CommonOperationsConstants.EMV_DEVICE:
            default:
                return sTokenName;
        }

        if (sTokenName.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD_EB)) {
            sAuthType = "Oath";
        }

        try {

            urlUtil.getDeviceByDeviceIdURL();
            //String _pushurl = "http://10.10.84.139:80/saserver/master/api/devices/" + sTokenName;
            String _pushurl = urlUtil.getDeviceByDeviceIdURL() + sTokenName;

            logger.info("[getSeedName] URL: " + _pushurl);

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
            pushcon.setRequestMethod(CommonOperationsConstants.GET);
            pushcon.setRequestProperty(CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.COOKIE, sJSESSION);

            int pushresponseCode = pushcon.getResponseCode();
            pushresponseData = Integer.toString(pushresponseCode);

            if (pushresponseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                logger.info("pushresponseData : " + pushresponseData);

                InputSource is = new InputSource(new StringReader(pushresponseData));
                Document usersdoc = builder.parse(is);

                logger.info("XML response [Activate devices] : " + usersdoc.toString());

                // Just parse the response
                NodeList nList = usersdoc.getElementsByTagName(sAuthType + "Seed");
                Node nNode = nList.item(0);
                Element eElement = (Element) nNode;
                sSeedName = eElement.getElementsByTagName("SeedName").item(0).getTextContent();
                sSeedName = sSeedName.replace("#", "%23");
                logger.info("SeedName : " + sSeedName);

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            pushresponseData = e.toString();
            return null;
        }
        return sSeedName;
    }


    public boolean executeSingleTokenAction(String sJSESSION, String sUserID, String sTokenName, int iAction) {
        String pushresponseData = "";
        String pushXmlData = "";

        switch (iAction) {
            case CommonOperationsConstants.UNLINK_TOKEN:
                pushXmlData = "<TokenAction><Action>RemoveUser</Action><ActionData><UserID>" + sUserID + "</UserID></ActionData></TokenAction>";
                break;
            case CommonOperationsConstants.REVOKE_TOKEN:
                pushXmlData = "<TokenAction><Action>Revoke</Action></TokenAction>";
                break;
            case CommonOperationsConstants.DELETE_TOKEN:
                pushXmlData = "<TokenAction><Action>Delete</Action></TokenAction>";
                break;
            default:
                return false;
        }

        try {

            //String _pushurl = "http://10.10.84.139:80/saserver/master/api/devices/" + sTokenName;

            String _pushurl = urlUtil.getDeviceByDeviceIdURL() + sTokenName;
            logger.info("[executeSingleTokenAction] _pushurl : " + _pushurl);

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
            pushcon.setRequestMethod(POST);
            pushcon.setRequestProperty(CommonOperationsConstants.CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.COOKIE, sJSESSION);

            // For POST only - START
            pushcon.setDoInput(true);
            pushcon.setDoOutput(true);
            PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
            pushpw.write(pushXmlData);
            pushpw.flush();
            pushpw.close();
            // For POST only - END

            int pushresponseCode = pushcon.getResponseCode();

            if (pushresponseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String getPANfromDCVdeviceToBeReworked(String sJSESSIONID, String deviceID) {

        String sPan = "";
        String pushresponseData = "";

        try {

            String _pushurl = urlUtil.getPANfromDCVdeviceToBeReworkedURL();
            _pushurl = _pushurl.replace("<deviceID>", deviceID);

            logger.info("[getPANfromDCVdevice: authenticate.............] " + _pushurl);

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
            pushcon.setRequestMethod(CommonOperationsConstants.GET);
            pushcon.setRequestProperty(CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.COOKIE, sJSESSIONID);

            int pushresponseCode = pushcon.getResponseCode();

            if (pushresponseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();

                InputSource is = new InputSource(new StringReader(pushresponseData));
                Document usersdoc = builder.parse(is);

                logger.info("XML response [Activate devices] : " + usersdoc.toString());

                NodeList nList = usersdoc.getElementsByTagName("EmvSeed");
                Node nNode = nList.item(0);
                Element eElement = (Element) nNode;
                sPan = eElement.getElementsByTagName("FilteredPan").item(0).getTextContent();

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            pushresponseData = e.toString();
            return null;
        }
        return sPan;
    }


    /**
     * Method to get list of tokens
     *
     * @param sJSESSION
     * @param sUserID
     * @return
     */
    public Map<String, String> getTokenList(String sJSESSION, String sUserID) {

        String _pushurl = "";
        String pushresponseData = "";
        Map<String, String> hm = new HashMap<String, String>();

        try {
            _pushurl = urlUtil.findUserFromCASServerURL() + sUserID;

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
            pushcon.setRequestMethod(CommonOperationsConstants.GET);
            pushcon.setRequestProperty(CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.COOKIE, sJSESSION);

            int pushresponseCode = pushcon.getResponseCode();
            pushresponseData = Integer.toString(pushresponseCode);
            if (pushresponseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader pushin = new BufferedReader(new InputStreamReader(
                        pushcon.getInputStream())); //getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                // print result
                pushresponseData = _pushresponse.toString();
                InputSource is = new InputSource(new StringReader(pushresponseData));
                Document usersdoc = builder.parse(is);

                NodeList nList = usersdoc.getElementsByTagName("TokenName");
                NodeList nListType = usersdoc.getElementsByTagName("TokenType");

                for (int i = 0; i < nList.getLength(); i++) {
						/*
						Devices can be one of the following types:
						¦ 1 = MultiSeed Device
						¦ 2 = SingleSeed HOTP Device
						¦ 3 = SingleSeed TOTP Device
						¦ 4 = EMV Device
						¦ 5 = Virtual Device (specific device type for Lost PWD use case)
						¦ 6 = SMS Device
						¦ 7 = DCVV Device
						¦ 8 = Ezio Mobile Protector (mobile devices enrolled by EPS)
						¦ 11 = Get OTP API Device
						*/
                    // In the demo we have 7/3/4/1
                    hm.put(nList.item(i).getChildNodes().item(0).getNodeValue(), nListType.item(i).getChildNodes().item(0).getNodeValue());
                }
            } else {
                BufferedReader pushin = new BufferedReader(new InputStreamReader(
                        pushcon.getErrorStream()));
                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                // print result
                pushresponseData = _pushresponse.toString();

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return hm;
    }


    public String getDeviceState(String sJSESSION, String sTokenName, String sTokenType) {
        String _pushurl = "";
        String pushresponseData = "";
        String sSeedName = null;
        try {
            sSeedName = getSeedName(sJSESSION, sTokenName, sTokenType);
            _pushurl = urlUtil.getDeviceStateURL();
            logger.info("[getDeviceState] _pushurl : " + _pushurl);

            switch (Integer.parseInt(sTokenType)) {
                case CommonOperationsConstants.MULTI_SEED_DEVICE:
                    //get the first seed state => we can assume all seeds have the same state
                    if (sSeedName == null) {
                        return "-1";
                    }
                    _pushurl = _pushurl + sSeedName;
                    break;
                case CommonOperationsConstants.SINGLE_SEED_TOTP_DEVICE:
                case CommonOperationsConstants.EZIO_MOBILE_PROTECTOR:
                    _pushurl = _pushurl + sTokenName;
                    break;
                case CommonOperationsConstants.DCVV_DEVICE:
                    if (sTokenName.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DCV_PHYSIQUE)) {
                        String sPanNumber = getPANfromDCVdeviceToBeReworked(sJSESSION, sTokenName);
                        if (sPanNumber != null) {
                            _pushurl = _pushurl + sPanNumber + ":00";
                            _pushurl = _pushurl.replace("otb", "emv");
                        } else
                            return "-1";
                    } else {
                        _pushurl = _pushurl + sSeedName;
                    }
                    break;
                case CommonOperationsConstants.EMV_DEVICE:
                    return Integer.toString(CommonOperationsConstants.ACTIVE);
                default:
                    return "-1";
            }

            if (sTokenName.substring(0, 4).equals(CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD_EB)) {
                _pushurl = _pushurl.replace("otb", "oath");
            }

            logger.info("getDeviceState URL: " + _pushurl);

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
            pushcon.setRequestMethod(CommonOperationsConstants.GET);
            pushcon.setRequestProperty(CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.COOKIE, sJSESSION);
            //out.print(HeaderJsession);
            int pushresponseCode = pushcon.getResponseCode();
            logger.info("Response of fetch device status :: " + pushresponseCode);
            pushresponseData = Integer.toString(pushresponseCode);
            //pushresponseData = pushcon.getErrorStream();
            if (pushresponseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader pushin = new BufferedReader(new InputStreamReader(
                        pushcon.getInputStream())); //getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                // print result
                pushresponseData = _pushresponse.toString();
                InputSource is = new InputSource(new StringReader(pushresponseData));
                Document usersdoc = builder.parse(is);

                NodeList nList = usersdoc.getElementsByTagName("State");
                return nList.item(0).getChildNodes().item(0).getNodeValue();
            } else {
                BufferedReader pushin = new BufferedReader(new InputStreamReader(
                        pushcon.getErrorStream()));
                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();

                return "-1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    public int doJobSingleToken(String sJSESSION, String sUserID, String sTokenName, String sTokenState, boolean bHandleMobile, boolean bHandleDCVcardToken, boolean bHandlePhysicalToken) {
        switch (Integer.parseInt(sTokenState)) {
            case CommonOperationsConstants.ACTIVE:
            case CommonOperationsConstants.INITIALIZED:
            case CommonOperationsConstants.BLOCKED:
                return returnBlockedCommonOperationsStatus(sJSESSION, sUserID, sTokenName, bHandleMobile, bHandleDCVcardToken, bHandlePhysicalToken);
            case CommonOperationsConstants.REVOKED:
                return returnRevokedCommonOperationsStatus(sJSESSION, sUserID, sTokenName, bHandleMobile, bHandleDCVcardToken, bHandlePhysicalToken);
            case CommonOperationsConstants.LOCKED:
                // should never happen, counter very high!
                return CommonOperationsConstants.ALL_OK;
            default:
                return CommonOperationsConstants.GENERAL_ERROR;
        }
    }

    /**
     *
     * @param sJSESSION
     * @param sUserID
     * @param sTokenName
     * @param bHandleMobile
     * @param bHandleDCVcardToken
     * @param bHandlePhysicalToken
     * @return
     */
    private int returnRevokedCommonOperationsStatus(String sJSESSION, String sUserID, String sTokenName, boolean bHandleMobile, boolean bHandleDCVcardToken, boolean bHandlePhysicalToken) {
        if (((isMobileAssimilated(sTokenName) || isEMVAssimilated(sTokenName)) && bHandleMobile) || (isPhysicalDCVAssimilated(sTokenName) && bHandleDCVcardToken)) {
            if (manageTokenAction(sJSESSION, sUserID, sTokenName, new int[]{CommonOperationsConstants.UNLINK_TOKEN, CommonOperationsConstants.DELETE_TOKEN})) {
                return CommonOperationsConstants.ALL_OK;
            } else {
                return CommonOperationsConstants.GENERAL_ERROR;
            }
        } else if (isPhysicalTokenAssimilated(sTokenName) && bHandlePhysicalToken) {
            if (manageTokenAction(sJSESSION, sUserID, sTokenName, new int[]{CommonOperationsConstants.UNLINK_TOKEN})) {
                return CommonOperationsConstants.ALL_OK;
            } else {
                return CommonOperationsConstants.GENERAL_ERROR;
            }
        } else {
            //No action required
            return CommonOperationsConstants.ALL_OK;
        }
    }

    /**
     *
     * @param sJSESSION
     * @param sUserID
     * @param sTokenName
     * @param bHandleMobile
     * @param bHandleDCVcardToken
     * @param bHandlePhysicalToken
     * @return
     */
    private int returnBlockedCommonOperationsStatus(String sJSESSION, String sUserID, String sTokenName, boolean bHandleMobile, boolean bHandleDCVcardToken, boolean bHandlePhysicalToken) {
        if (((isMobileAssimilated(sTokenName) || isEMVAssimilated(sTokenName)) && bHandleMobile) || (isPhysicalDCVAssimilated(sTokenName) && bHandleDCVcardToken)) {
            if (manageTokenAction(sJSESSION, sUserID, sTokenName, new int[]{CommonOperationsConstants.UNLINK_TOKEN, CommonOperationsConstants.REVOKE_TOKEN, CommonOperationsConstants.DELETE_TOKEN})) {
                return CommonOperationsConstants.ALL_OK;
            } else {
                return CommonOperationsConstants.GENERAL_ERROR;
            }
        } else if (isPhysicalTokenAssimilated(sTokenName) && bHandlePhysicalToken) {
            if (manageTokenAction(sJSESSION, sUserID, sTokenName, new int[]{CommonOperationsConstants.UNLINK_TOKEN})) {
                return CommonOperationsConstants.ALL_OK;
            } else {
                return CommonOperationsConstants.GENERAL_ERROR;
            }
        } else {
            return CommonOperationsConstants.ALL_OK;
        }
    }


    public boolean isMobileAssimilated(String sTokenName) {
        switch (sTokenName.substring(0, 4)) {
            case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_LOGIN:
            case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_DCV:
            case CommonOperationsConstants.PREFIX_DEVICE_MOBILE_SIGNATURE_TRANSACTION:
                return true;
            default:
                return false;
        }
    }


    public boolean isPhysicalDCVAssimilated(String sTokenName) {
        switch (sTokenName.substring(0, 4)) {
            case CommonOperationsConstants.PREFIX_DCV_PHYSIQUE:
                return true;
            default:
                return false;
        }
    }


    public boolean isEMVAssimilated(String sTokenName) {
        switch (sTokenName.substring(0, 3)) {
            case CommonOperationsConstants.PREFIX_DEVICE_EMV:
                return true;
            default:
                return false;
        }
    }


    public boolean isPhysicalTokenAssimilated(String sTokenName) {
        switch (sTokenName.substring(0, 4)) {
            case CommonOperationsConstants.PREFIX_DEVICE_FLEX:
            case CommonOperationsConstants.PREFIX_DEVICE_SIGNER:
            case CommonOperationsConstants.PREFIX_DEVICE_PICO:
            case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD_EB:
            case CommonOperationsConstants.PREFIX_DEVICE_DISPLAY_CARD_PAD:
            case CommonOperationsConstants.PREFIX_DEVICE_LAVA:
            case CommonOperationsConstants.PREFIX_DEVICE_QRTOKEN:
                return true;
            default:
                return false;
        }
    }

    /**
     * * Mobile seeds:
     * - GALO/GAOC (tokenType=3)
     * - EMVxxxx(tokenType=4)/GADV(tokenType=7) for DCV
     * <p>
     * * Physical DCV seed:
     * - GAPC (tokenType=7)
     * <p>
     * * Physical tokens:
     * - GALT (tokenType=3)
     * - GATB/GATZ/GATP/GADB/GADF (tokenType=1)
     */

    public int performTokenAction(String sJSESSION, String sUserID, Map<String, String> mpToken, JSONObject resJSON_Obj) {

        if (sJSESSION == null || sJSESSION.isEmpty() || sUserID == null || sUserID.isEmpty() || mpToken == null || resJSON_Obj == null)
            return CommonOperationsConstants.GENERAL_ERROR;

        Iterator iter = null; // set.iterator();
        Set set = null; // map.entrySet();
        String sLocalJSESSION = sJSESSION;

        boolean bHandleMobile = false;
        boolean bHandleDCVcardToken = false;
        boolean bHandlePhysicalToken = false;

        try {
            logger.info("JSON to string: " + resJSON_Obj.toString());
            set = mpToken.entrySet();
            iter = set.iterator();

            if (resJSON_Obj.has(CommonOperationsConstants.HAS_MOBILE)) {
                bHandleMobile = resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_MOBILE);
                logger.info("HAS_MOBILE");
            }
            if (resJSON_Obj.has(CommonOperationsConstants.HAS_DCV_CARDS)) {
                bHandleDCVcardToken = resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_DCV_CARDS);
                logger.info("HAS_DCV_CARDS");
            }
            if (resJSON_Obj.has(CommonOperationsConstants.HAS_PHYSICAL_TOKENS)) {
                bHandlePhysicalToken = resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_PHYSICAL_TOKENS);
                logger.info("HAS_PHYSICAL_TOKENS");
            }

            //No need to continue
            if (!bHandleMobile && !bHandleDCVcardToken && !bHandlePhysicalToken) {
                return CommonOperationsConstants.ALL_OK;
            }

            String sTempTokenName = null;
            String sTempsTokenType = null;
            String sTempsTokenState = null;
            //Parse all tokens and do the required action
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

                sTempTokenName = (String) entry.getKey();
                sTempsTokenType = (String) entry.getValue();

                if (sTempTokenName == null || sTempsTokenType == null) {
                    return CommonOperationsConstants.GENERAL_ERROR;
                }

                logger.info("sTempTokenName: " + sTempTokenName);
                logger.info("sTempsTokenType: " + sTempsTokenType);

                sTempsTokenState = getDeviceState(sLocalJSESSION, sTempTokenName, sTempsTokenType);

                if (sTempsTokenState == null || sTempsTokenState.equals("-1")) {
                    return CommonOperationsConstants.GENERAL_ERROR;
                }

                logger.info("sTempsTokenState: " + sTempsTokenState);

                if (CommonOperationsConstants.ALL_OK != doJobSingleToken(sLocalJSESSION, sUserID, sTempTokenName, sTempsTokenState,
                        bHandleMobile, bHandleDCVcardToken, bHandlePhysicalToken)) {
                    return CommonOperationsConstants.GENERAL_ERROR;
                }
            }
            if (bHandleMobile) {
                if (!deleteUserInSecureMessenger(sUserID)) {
                    return CommonOperationsConstants.GENERAL_ERROR;
                }
            }
        } catch (Exception e) {
            System.err.println(CommonOperationsConstants.ERROR_MESSAGE + e);
            return CommonOperationsConstants.GENERAL_ERROR;
        }

        return CommonOperationsConstants.ALL_OK;
    }


    public boolean deleteUserInSecureMessenger(String userid) {
        String _pushurl = "";
        String pushresponseData = "";
        try {
            String backendConfiguration = urlUtil.getBackendConfiguration();
            logger.info("[deleteUserInSecureMessenger] backendConfiguration : " + backendConfiguration);
            //_pushurl = "http://10.10.84.139:8081/oobs-dispatcher/domains/default/users/"+userid;
            _pushurl = urlUtil.pushNotificationURL() + userid;
            logger.info("[deleteUserInSecureMessenger] pushurl : " + _pushurl);

            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
            switch (backendConfiguration) {
                case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_NIMBUS:
                    pushcon.setRequestMethod("DELETE");
                    pushcon.setRequestProperty(CommonOperationsConstants.AUTHORIZATION_PROP, B_LABEL + NIMBUS_AUTHORISATION_TOKEN);
                    break;
                case EzioDemoIDCloudConstant.IDCLOUD_BACKEND_CONFIGURATION_IDCLOUD:
                    pushcon.setRequestMethod("DELETE");
                    pushcon.setRequestProperty(CommonOperationsConstants.AUTHORIZATION_PROP, B_LABEL + AUTHORISATION_TOKEN);
                    break;
                default:
                    break;
            }
            int pushresponseCode = pushcon.getResponseCode();
            pushresponseData = Integer.toString(pushresponseCode);
            if (pushresponseCode == HttpURLConnection.HTTP_OK) { //success

                BufferedReader pushin = new BufferedReader(new InputStreamReader(
                        pushcon.getInputStream())); //getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                return true;
            } else if (pushresponseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                //most probably the userId has already been deleted
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            pushresponseData = e.toString();
            return false;
        }
    }

    /**
     * @param userId
     * @return
     * @throws SQLException
     */
    public int resetDemoDB(String userId) {

        int bStatus = CommonOperationsConstants.GENERAL_ERROR;
        int uId = 0; //userid
        try {

            logger.info("[resetDemoDB] userId : " + userId);
            uId = userMasterService.findUidByUserId(userId);

            logger.info("[resetDemoDB] uId for " + userId + " is : " + uId);

            masterService.deleteDeviceMasterData(uId);
            masterService.deleteAccountsByTypeAndUid(EzioMobileDemoConstant.EZIO_ACCOUNT_TYPE_1, uId);
            masterService.deleteCardManagementDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            masterService.deletePanMasterDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            masterService.deleteRiskPreferenceByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, String.valueOf(uId));
            masterService.deleteUserPreferenceByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            masterService.deleteTransactionsDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            masterService.deleteSignDataByStatusAndUid(EzioMobileDemoConstant.EZIO_STATUS_VALUE_1, uId);
            masterService.deleteCardIssuanceInfoByUserId(uId);
            qrTokenmasterService.deleteQRTokenDetailsByUserId(userId);
            masterService.resetAccountBalanceByUid(uId);

            masterService.createUserPreferenceMasterData(uId);
            masterService.createRiskPreferenceMasterData(uId);

            bStatus = CommonOperationsConstants.ALL_OK;

        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return bStatus;
    }

    //HTTP call to RM server, will be implemented
    public int resetRiskManagement(String userId) {

        int rmStatus = CommonOperationsConstants.GENERAL_ERROR;
        String tenantId = CommonOperationsConstants.GAH_TENANT;

        rmStatus = resetBehavioSecProfile(tenantId, userId);
        logger.info("print the value of rmstatus  : " + rmStatus);
        if (rmStatus == 1) {
            rmStatus = CommonOperationsConstants.ALL_OK;
        }
        return rmStatus;
    }

    public int resetBehavioSecProfile(String tenantId, String userId) {

        logger.info(" <------- Start reset BehavioSec profil ----------------->");

        logger.info("tenantId: " + tenantId);
        logger.info("userId :" + userId);

        String pushresponseData = "";
        String _pushurl = "";
        int res = 0;
        //action body
        String requestBodyJSON = "{\"userId\": \"" + userId + "\"}";

        try {

            String tokenId = generateJWT();
            logger.info("tokenId: " + tokenId);

            _pushurl = "https://demo-api.rnd.gemaltodigitalbankingidcloud.com/api/v1/tenants/" + tenantId + "/resetProfile";

            logger.info(CommonOperationsConstants.PUSHURL + _pushurl);

            URL pushobj = new URL(_pushurl);
            HttpsURLConnection pushcon = (HttpsURLConnection) pushobj.openConnection();

            pushcon.setRequestMethod(POST);
            pushcon.setRequestProperty(CommonOperationsConstants.AUTHORIZATION_PROP, "Bearer " + tokenId);
            pushcon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            pushcon.setDoOutput(true);
            pushcon.setDoInput(true);

            logger.info("pushcon: " + pushcon);

            OutputStream os = pushcon.getOutputStream();
            os.write(requestBodyJSON.getBytes("UTF-8"));
            os.close();

            int pushresponseCode = pushcon.getResponseCode();
            pushresponseData = Integer.toString(pushresponseCode);

            logger.info("pushresponseCode from resetBehavioSecProfile........ : " + pushresponseCode);
            logger.info("GET Response Code : " + pushresponseData);

            if (pushresponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();

                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                logger.info("\n success response for resetBehavioSecProfile: " + pushresponseData);
                return 1;
            } else if (pushresponseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                logger.info("\n success response for forbidden resetBehavioSecProfile: " + pushresponseData);
                return 1;
            } else {
                logger.info("\n NOT-OK response resetBehavioSecProfile");

                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(CommonOperationsConstants.ERROR_MESSAGE + e);
            return -1;
        }

    }

    public static String generateJWT() {
        BufferedReader bufferReader = new BufferedReader(new StringReader(CommonOperationsConstants.PRIVATE_KEY));
        return createJwt(bufferReader);
    }

    @SuppressWarnings("finally")
    public static String createJwt(BufferedReader bufferReader) {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        String token = "";
        StringBuilder pkcs8Lines = new StringBuilder();
        String line;
        byte[] pkcs8EncodedBytes;

        logger.info(" <------- " + " START generateJWT" + " ----------------->");

        try {
            // Read in the key into a String
            while ((line = bufferReader.readLine()) != null) {
                pkcs8Lines.append(line);
            }
            // Remove the "BEGIN" and "END" lines, as well as any whitespace
            String pkcs8Pem = pkcs8Lines.toString();
            pkcs8Pem = pkcs8Pem.replace("-----BEGIN RSA PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replace("-----END RSA PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

            logger.info(" <------- " + pkcs8Pem + " ----------------->");

            // Base64 decode the result
            pkcs8EncodedBytes = Base64.decodeBase64(pkcs8Pem);

            PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey unencryptedPrivateKey = kf.generatePrivate(kspec);

            RSAPrivateKey RSApk = (RSAPrivateKey) unencryptedPrivateKey;

            token = JWT.create()
                    .withIssuer("myIssuer")
                    .withAudience("audienceVal")
                    .withClaim("role", "tenant-oper")
                    .withClaim("tenantId", "bankonline")
                    .sign(Algorithm.RSA256(RSApk));
        } catch (Exception exception) {
            logger.info(CommonOperationsConstants.ERROR_MESSAGE + exception);
            logger.info(" <------- " + "JWTCreationException" + " ----------------->");
        } finally {
            logger.info(" <------- " + token + " ----------------->");
        }

        return token;
    }

    /**
     * * ServletContext context no need in new demo...
     * * sUserID
     * * the Items we are allowed to check because we support them
     **/
    public int resetMyAccount(String sUserID, JSONObject resJSON_Obj) {
        int iStatus = CommonOperationsConstants.ALL_OK;

        try {
            if ((resJSON_Obj.has(CommonOperationsConstants.HAS_MOBILE) && resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_MOBILE))
                    || (resJSON_Obj.has(CommonOperationsConstants.HAS_DCV_CARDS) && resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_DCV_CARDS))
                    || (resJSON_Obj.has(CommonOperationsConstants.HAS_PHYSICAL_TOKENS) && resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_PHYSICAL_TOKENS))) {

                logger.info("resetMyAccount..... 1");

                //authenticate
                synchronized (headerJsession) {
                    headerJsession = CASWebHelper.headerJsession;
                }
                if (headerJsession == null) {
                    logger.info("resetMyAccount..... 2");
                    return CommonOperationsConstants.GENERAL_ERROR;
                }
                logger.info("05 headerJsession..... " + headerJsession);

                Map<String, String> hm = new HashMap<String, String>();
                hm = getTokenList(headerJsession, sUserID);
                if (hm == null) {
                    logger.info("resetMyAccount..... 3");
                    return CommonOperationsConstants.GENERAL_ERROR;
                }
                if (hm.isEmpty()) {
                    logger.info("resetMyAccount..... 4");
                    //it is not normal to be able to get here without any token...
                    iStatus = CommonOperationsConstants.GENERAL_ERROR;
                } else {
                    logger.info("resetMyAccount..... 5");
                    iStatus = performTokenAction(headerJsession, sUserID, hm, resJSON_Obj);
                    if (iStatus == CommonOperationsConstants.GENERAL_ERROR) {
                        logger.info("resetMyAccount..... 6");
                        return CommonOperationsConstants.GENERAL_ERROR;
                    }
                }
            }
            if (resJSON_Obj.has(CommonOperationsConstants.HAS_RM) && resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_RM)) {
                logger.info("resetMyAccount..... 7");
                iStatus = resetRiskManagement(sUserID);
                if (iStatus == CommonOperationsConstants.GENERAL_ERROR) {
                    logger.info("resetMyAccount..... 8");
                    return CommonOperationsConstants.GENERAL_ERROR;
                }
            }
            if (resJSON_Obj.has(CommonOperationsConstants.HAS_DEMODATA) && resJSON_Obj.getBoolean(CommonOperationsConstants.HAS_DEMODATA)) {
                logger.info("resetMyAccount..... 9");
                iStatus = resetDemoDB(sUserID);
                if (iStatus == CommonOperationsConstants.GENERAL_ERROR) {
                    logger.info("resetMyAccount..... 10");
                    return CommonOperationsConstants.GENERAL_ERROR;
                }
            }
        } catch (Exception e) {
            logger.info("resetMyAccount..... 11");
            return CommonOperationsConstants.GENERAL_ERROR;
        }
        logger.info("resetMyAccount..... 12");
        return iStatus;
    }


    public JSONObject setDefaultJSONResetMyAccountOptions(String userid) {
        JSONObject resJSON_Obj = new JSONObject();
        try {
            resJSON_Obj.put("userId", userid);
            resJSON_Obj.put("mobile", false);
            resJSON_Obj.put("dcvcards", false);
            resJSON_Obj.put("physicalTokens", false);
            resJSON_Obj.put("riskManagement", false);
            resJSON_Obj.put("demoData", false);
            logger.info("replaceItemJSONResetMyAccountOptions 00 " + resJSON_Obj.toString());
        } catch (Exception e) {
            logger.info("replaceItemJSONResetMyAccountOptions 12");
            return null;
        }
        return resJSON_Obj;
    }


    public JSONObject replaceItemJSONResetMyAccountOptions(JSONObject resJSON_Obj, String item, boolean value) {
        JSONObject resJSON_Objtmp = resJSON_Obj;
        try {
            resJSON_Objtmp.remove(item);
            resJSON_Objtmp.put(item, value);
            logger.info("replaceItemJSONResetMyAccountOptions 00 " + resJSON_Objtmp.toString());
        } catch (Exception e) {
            logger.info("replaceItemJSONResetMyAccountOptions 12");
            return null;
        }
        return resJSON_Objtmp;
    }


    /**
     * Get JSON of user reset account options
     *
     * @param sUserID
     * @param sUserRole
     * @return
     */
    public JSONObject getResetMyAccountOptions(String sUserID, String sUserRole) {
        JSONObject resJSON_Obj = new JSONObject();
        try {
            Iterator iter = null; // set.iterator();
            Set set = null; // map.entrySet();
            synchronized (headerJsession) {
                headerJsession = CASWebHelper.headerJsession;
            }
            resJSON_Obj = setDefaultJSONResetMyAccountOptions(sUserID);

            if (!(headerJsession != null && !headerJsession.equals("") && resJSON_Obj != null)) {
               return resJSON_Obj;
            }

            Map<String, String> hm = new HashMap<String, String>();
            hm = getTokenList(headerJsession, sUserID);

            boolean bDoneMobile = false;
            boolean bDoneDCVcardToken = false;
            boolean bDonePhysicalToken = false;
            boolean bSupportRM = false;

            String sTempTokenName = null;

            set = hm.entrySet();
            iter = set.iterator();

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

                sTempTokenName = (String) entry.getKey();

                logger.info("getResetMyAccountOptions...... 3 " + sTempTokenName);

                if (sTempTokenName != null && !sTempTokenName.equals("")) {

                    if ((isMobileAssimilated(sTempTokenName) || isEMVAssimilated(sTempTokenName)) && !bDoneMobile) {
                        resJSON_Obj = replaceItemJSONResetMyAccountOptions(resJSON_Obj, "mobile", true);
                        logger.info("[getResetMyAccountOptions] resJSON_Obj - Mobile : " + resJSON_Obj.toString());
                        bDoneMobile = true;
                        logger.info("getResetMyAccountOptions...... 4");
                    } else if (isPhysicalDCVAssimilated(sTempTokenName) && !bDoneDCVcardToken) {
                        resJSON_Obj = replaceItemJSONResetMyAccountOptions(resJSON_Obj, "dcvcards", true);
                        logger.info("[getResetMyAccountOptions] resJSON_Obj - DCVcards : " + resJSON_Obj.toString());
                        bDoneDCVcardToken = true;
                        logger.info("getResetMyAccountOptions...... 5");
                    } else if (isPhysicalTokenAssimilated(sTempTokenName) && !bDonePhysicalToken) {
                        resJSON_Obj = replaceItemJSONResetMyAccountOptions(resJSON_Obj, "physicalTokens", true);
                        logger.info("[getResetMyAccountOptions] resJSON_Obj - PhysicalTokens : " + resJSON_Obj.toString());
                        bDonePhysicalToken = true;
                        logger.info("getResetMyAccountOptions...... 6");
                    }
                }
            }
            if (sUserRole.length() == 3) {
                if ((Integer.parseInt(sUserRole, 16) & (int) BIT_RM) == (int) BIT_RM) {
                    resJSON_Obj = replaceItemJSONResetMyAccountOptions(resJSON_Obj, "riskManagement", true);
                    logger.info("[getResetMyAccountOptions] resJSON_Obj - RiskManagement : " + resJSON_Obj.toString());
                    logger.info("getResetMyAccountOptions...... 7");
                    bSupportRM = true;
                }
            }
            if (bDonePhysicalToken || bDoneDCVcardToken || bDoneMobile || bSupportRM) {
                resJSON_Obj = replaceItemJSONResetMyAccountOptions(resJSON_Obj, "demoData", true);
                logger.info("[getResetMyAccountOptions] resJSON_Obj - DemoData : " + resJSON_Obj.toString());
                logger.info("getResetMyAccountOptions...... 8");
            }
            logger.info("getResetMyAccountOptions...... 9");
        } catch (Exception e) {
            logger.error("Unable to find user reset options!");
            e.printStackTrace();
        } finally {
            logger.info("Finally Block - [getResetMyAccountOptions] resJSON_Obj : " + resJSON_Obj.toString());
        }
        return resJSON_Obj;
    }


    /**
     * This method is used for creating map for permission based on the user role
     *
     * @param userRole
     * @return
     */
    public Map<String, Boolean> prepareUserPermissions(String userRole) {

        logger.info("preparePermissionMap..............");
        logger.info("userRole: " + userRole);
        String result = "";

        Map<String, Boolean> permissionMap = new HashMap<>();
        try {
            if (userRole != null && !userRole.equals("")) {

                userRole = userRole.toUpperCase();
                logger.info("USER ROLE : " + userRole);

                result = hexToBinary(userRole);
                logger.info("IT'S BINARY IS : " + result);
            }

            char GAH = result.charAt(3);
            logger.info("GAH: " + GAH);
            char userPreference = result.charAt(5);
            logger.info("userPreference: " + userPreference);
            char tokenPro = result.charAt(6);
            logger.info("tokenPro: " + tokenPro);
            char tokenActResync = result.charAt(7);
            logger.info("tokenActResync: " + tokenActResync);
            char cardIssuance = result.charAt(8);
            logger.info("cardIssuance: " + cardIssuance);
            char reportBackend = result.charAt(9);
            logger.info("reportBackend: " + reportBackend);
            char DCV = result.charAt(10);
            logger.info("DCV: " + DCV);
            char P2P = result.charAt(11);
            logger.info("P2P: " + P2P);
            //0001 0111 1110
            boolean isPermitted = false;

            if (GAH == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.GAH, isPermitted);

            if (userPreference == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.USER_PREFERENCE, isPermitted);

            if (tokenPro == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.TOKEN_PROVISION, isPermitted);

            if (tokenActResync == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.TOKEN_ACT_RESYNC, isPermitted);

            if (cardIssuance == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.CARD_ISSUANCE, isPermitted);

            if (reportBackend == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.REPORT_BACKEND, isPermitted);

            if (DCV == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.DCV, isPermitted);

            if (P2P == '1' ? (isPermitted = true) : (isPermitted = false)) ;
            permissionMap.put(EzioMobileDemoConstant.P2P, isPermitted);

            logger.info("permissionMap: " + permissionMap.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return permissionMap;
    }

    /**
     * This method is use to convert hexadecimal value to binary value
     *
     * @param userRole
     * @return binary string
     */
    private String hexToBinary(String userRole) {
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String[] binary = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};

        String result = "";

        for (int i = 0; i < userRole.length(); i++) {
            char temp = userRole.charAt(i);
            String temp2 = "" + temp + "";
            for (int j = 0; j < hex.length; j++) {
                if (temp2.equalsIgnoreCase(hex[j])) {
                    result = result + binary[j];
                }
            }
        }
        return result;
    }

    public byte[] getKey(QRtokenAlgo qrtokenAlgo, boolean isCustomkey) {
        String key = null;
        byte[] tokenkey = null;
        switch (qrtokenAlgo) {
            case ENCRYPTION_ALGO_TDES:
                if (isCustomkey) {
                    key = urlUtil.getQrtoken_CustKeyTDES();
                } else {
                    key = urlUtil.getQrtoken_PrimKeyTDES();
                }
                break;
            case ENCRYPTION_ALGO_AES128:
                if (isCustomkey) {
                    key = urlUtil.getQrtoken_CustKeyAES128();
                } else {
                    key = urlUtil.getQrtoken_PrimKeyAES128();
                }
                break;
            case ENCRYPTION_ALGO_AES256:
                if (isCustomkey) {
                    key = urlUtil.getQrtoken_CustKeyAES256();
                } else {
                    key = urlUtil.getQrtoken_PrimKeyAES256();
                }
                break;
            default:
                if (isCustomkey) {
                    key = urlUtil.getQrtoken_CustKeyAES256();
                } else {
                    key = urlUtil.getQrtoken_PrimKeyAES256();
                }
                break;
        }
        tokenkey = QRTokenUtil.hexStringToByteArray(key);
        return tokenkey;

    }

    public String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }


    //String challenge
    //String tokenName
    //String seedvalue
    //String JsessionId
    public byte[] getDPUKHexCASever(byte[] randomness, String tokenName, String seedvalue) {

        System.out.println("CAS server authentication request starts........");
        int target = 0;
        String pushXmlData = "";
        String _pushurl = "";
        String pushresponseData = "";
        String authenticateCasServerURL = "";
        String url = "";
        String challenge = "";
        String SeedName = null;

//	url = "http://10.10.84.139:80/saserver/master/api/devices/GAQT00000001?tokenType=1";
        //url = "http://10.10.84.139:80/saserver/master/api/devices/tokenName?tokenType=1";
        url = urlUtil.getDeviceByDeviceIdURL();
        logger.info("START DPUK.......");
        try {

            challenge = QRToken.getQRChallenge(randomness);


            String dpukURL = url + tokenName + "?tokenType=1";
            casWebHelper.authenticateCASever();

            synchronized (headerJsession) {
                headerJsession = CASWebHelper.headerJsession;
            }
            SeedName = tokenName + "#" + seedvalue;
            logger.info("\n HeaderJsession of getDeviceStatus )...... " + headerJsession);

            pushXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><TokenAction><Action>DynamicPinUnblockCode</Action><ActionData><Seed><Name>"
                    + SeedName + "</Name></Seed><Challenge>"
                    + challenge + "</Challenge></ActionData></TokenAction>";
            logger.info("START DPUK....... " + pushXmlData);


            _pushurl = dpukURL;
            URL pushobj = new URL(_pushurl);
            HttpURLConnection pushcon = (HttpURLConnection) pushobj.openConnection();
            pushcon.setRequestMethod(POST);
            pushcon.setRequestProperty(CONTENT_TYPE_PROP, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.ACCEPT, CommonOperationsConstants.TEXT_XML);
            pushcon.setRequestProperty(CommonOperationsConstants.COOKIE, headerJsession);

            // For POST only - START
            pushcon.setDoInput(true);
            pushcon.setDoOutput(true);
            PrintWriter pushpw = new PrintWriter(pushcon.getOutputStream());
            pushpw.write(pushXmlData);
            pushpw.flush();
            pushpw.close();
            // For POST only - END

            int pushresponseCode = pushcon.getResponseCode();
            if (pushresponseCode == HttpURLConnection.HTTP_OK) { // success

                logger.info("DPUK OK.......");


                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getInputStream())); // getErrorStream()

                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }

                pushin.close();
                pushresponseData = _pushresponse.toString();
                InputSource is = new InputSource(new StringReader(pushresponseData));
                Document usersdoc = builder.parse(is);

                NodeList _nList = usersdoc.getElementsByTagName("DynamicPinUnblockCodeResponse");

                Element err = (Element) _nList.item(0);


                String QRCodeAuth = err.getElementsByTagName("UnblockCode").item(0).getTextContent();

                BigInteger big = new BigInteger(QRCodeAuth);
                logger.info("DPUK " + big.toString(16));
                QRCodeAuth = big.toString(16);
                while (QRCodeAuth.length() != 8) {
                    QRCodeAuth = "0" + QRCodeAuth;
                }
                return QRTokenUtil.hexStringToByteArray(QRCodeAuth);

            } else {


                BufferedReader pushin = new BufferedReader(new InputStreamReader(pushcon.getErrorStream()));
                String pushinputLine;
                StringBuffer _pushresponse = new StringBuffer();
                while ((pushinputLine = pushin.readLine()) != null) {
                    _pushresponse.append(pushinputLine);
                }
                pushin.close();
                pushresponseData = _pushresponse.toString();

                return null;
            }
        } catch (Exception e) {
            logger.info("CAS server authentication error.......");
            e.printStackTrace();
            pushresponseData = e.toString();
        }
        return null;

    }

    public String padAmount(String amount) {
        //add cents
        amount += "00";
        //pad the rest with "0"
        while (amount.length() != 12) {
            amount = "0" + amount;
        }
        return amount;
    }

    public String getStringQRCodeVersion(QRTokenVersion qrTokenVersion) {
        switch (qrTokenVersion) {
            case QR_CODE_VERSION_AUTO:
                return "4";

            case QR_CODE_VERSION_4:
                return "4";

            case QR_CODE_VERSION_5:
                return "5";

            case QR_CODE_VERSION_6:
                return "6";

            case QR_CODE_VERSION_7:
                return "7";

            case QR_CODE_VERSION_8:
                return "8";

            case QR_CODE_VERSION_9:
                return "9";

            default:
                return "4";
        }

    }
}
