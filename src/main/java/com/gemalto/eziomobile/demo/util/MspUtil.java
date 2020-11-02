package com.gemalto.eziomobile.demo.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Random;

import com.gemalto.ezio.qrip.Data;
import com.gemalto.ezio.qrip.OcraData;
import com.gemalto.ezio.qrip.QrCode;
import com.gemalto.ezio.qrip.QrCode.QrCodeData;
import com.gemalto.ezio.qrip.QrCodeConfiguration;
import com.gemalto.ezio.qrip.QrEncryptorMsp;
import com.gemalto.ezio.qrip.QrEncryptorObfuscationKey;
import com.gemalto.ezio.qrip.QrSigner;
import com.gemalto.ezio.qrip.QrSignerDSA;
import com.gemalto.ezio.util.UserInput;
import com.gemalto.eziomobile.demo.logger.LoggerUtil;

import static com.gemalto.eziomobile.demo.common.CommonOperationsConstants.*;

public class MspUtil {
	
	private static final LoggerUtil logger = new LoggerUtil(MspUtil.class.getClass());

	private static QrEncryptorMsp programEncryptor;
	private static QrSigner programSigner;
	private static String companyName;
    private static byte[] random;

	
	@SuppressWarnings("serial")
    protected static class MspRandomMock extends Random {

        byte[] predefined;

        public MspRandomMock(byte[] predefined) {
            this.predefined = predefined.clone();
        }

        @Override
        public void nextBytes(byte[] bytes) {
            int copylen = predefined.length;
			logger.info("int copylen " + String.valueOf(copylen));
            if (bytes.length < copylen) {
				logger.info("len diff");
                copylen = bytes.length;
            }
			logger.info("int copylen " + String.valueOf(copylen));
			logger.info("bytes.length " + String.valueOf(bytes.length));
            System.arraycopy(predefined, 0, bytes, 0, copylen);
        }
    }
	
	public static String encryptQRData(String dataTobeEncrypted, int encryptionMode)
            throws IOException, InvalidKeyException, GeneralSecurityException {
  
        companyName = "Gemalto";
        random = getRandom();
		MspRandomMock rand = new MspRandomMock(random);
        programEncryptor = getInputEncryptionKey();
        programSigner = getInputSignatureKey();

        //String eid = "PUYQGHUMEAAJPUYL";
		String eid = "P";

        UserInput serverChallenge
                = new UserInput(dataTobeEncrypted, new int[0]);

		String passwordHash
                = "11";
		String session
                = "e2";
				
		Data data = OcraData.standardOCRA(new Byte((byte) 0), eid, serverChallenge,
				new UserInput(""), hexStringToByteArray(passwordHash),
                hexStringToByteArray(session));
				
		QrCodeConfiguration config = null;
				
		switch(encryptionMode){
			case QR_CODE_CLEAR:
				logger.info("config QR_CODE_CLEAR");
				config = QrCodeConfiguration.mspV1Config(rand, null, null);
				break;
			case QR_CODE_ENCRYPTED:
				logger.info("config QR_CODE_ENCRYPTED");
				config = QrCodeConfiguration.mspV1Config(rand, programEncryptor, null);
				break;
			case QR_CODE_ENCRYPTED_AND_DSA_SIGNED:
				logger.info("config QR_CODE_ENCRYPTED_AND_DSA_SIGNED");
				config = QrCodeConfiguration.mspV1Config(rand, programEncryptor, programSigner);
				break;
			default:
				return null;
		}

        QrCodeData qrCodeData
                = new QrCode(companyName).generateQrCode(config, data);
        logger.info("QR-Code HEX string: "+ qrCodeData.getQrCodeToString());
		return qrCodeData.getQrCodeToString();

    }
	
	public static byte[] getRandom() throws IOException {
        byte[] random = hexStringToByteArray("474a0adf");
        return random;
	}

    public static QrSigner getInputSignatureKey() throws IOException {

        byte[] p = getSignatureP();
        byte[] q = getSignatureQ();
        byte[] g = getSignatureG();
        byte[] privateKey = getSignaturePrivateKey();
    
        return new QrSignerDSA((int) 1, privateKey, p, g, q);
    }

    public static QrEncryptorMsp getInputEncryptionKey() throws IOException {

        byte[] masterKey = getObfuscationKey();

        return new QrEncryptorObfuscationKey((int) 1, masterKey);
    }
	
	 /**
     * Get obfuscation key, replace this value accordingly.
     */
    public static byte[] getObfuscationKey() {
        return hexStringToByteArray(ENCRYPTION_KEY);
    }

    /**
     * Get public key for signature, replace this value accordingly.
     */
    public static byte[] getSignaturePrivateKey() {
        return hexStringToByteArray(PRIVATE_SIGNING_KEY);
    }

    /**
     * Get P for signature, replace this value accordingly.
     */
    public static byte[] getSignatureP() {
        return hexStringToByteArray(P_CONST);
    }

    /**
     * Get Q for signature, replace this value accordingly.
     */
    public static byte[] getSignatureQ() {
        return hexStringToByteArray(Q_CONST);
    }

    /**
     * Get G for signature, replace this value accordingly.
     */
    public static byte[] getSignatureG() {
        return hexStringToByteArray(G_CONST);
    }
	
	    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
}
