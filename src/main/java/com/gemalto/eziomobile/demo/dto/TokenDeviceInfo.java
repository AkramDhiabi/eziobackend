package com.gemalto.eziomobile.demo.dto;

import java.util.List;

public class TokenDeviceInfo {

	private boolean isMobileSupported;
	private String qrcodeEncryptedData;
	private String challengeCode;
	private List<String> tokenAvailable;
	private int tokenDeviceCount;
	private String tokenqrcodeEncryptedData;
	private String tokenqrcodeEncryptedVersion;
	
	
	public String getTokenqrcodeEncryptedData() {
		return tokenqrcodeEncryptedData;
	}
	public void setTokenqrcodeEncryptedData(String tokenqrcodeEncryptedData) {
		this.tokenqrcodeEncryptedData = tokenqrcodeEncryptedData;
	}
	public String getTokenqrcodeEncryptedVersion() {
		return tokenqrcodeEncryptedVersion;
	}
	public void setTokenqrcodeEncryptedVersion(String tokenqrcodeEncryptedVersion) {
		this.tokenqrcodeEncryptedVersion = tokenqrcodeEncryptedVersion;
	}
	public boolean isMobileSupported() {
		return isMobileSupported;
	}
	public void setMobileSupported(boolean isMobileSupported) {
		this.isMobileSupported = isMobileSupported;
	}
	
	public String getQrcodeEncryptedData() {
		return qrcodeEncryptedData;
	}
	public void setQrcodeEncryptedData(String qrcodeEncryptedData) {
		this.qrcodeEncryptedData = qrcodeEncryptedData;
	}
	public String getChallengeCode() {
		return challengeCode;
	}
	public void setChallengeCode(String challengeCode) {
		this.challengeCode = challengeCode;
	}
	public List<String> getTokenAvailable() {
		return tokenAvailable;
	}
	public void setTokenAvailable(List<String> tokenList) {
		this.tokenAvailable = tokenList;
	}
	public int getTokenDeviceCount() {
		return tokenDeviceCount;
	}
	public void setTokenDeviceCount(int tokenDeviceCount) {
		this.tokenDeviceCount = tokenDeviceCount;
	}
	@Override
	public String toString() {
		return "TokenDeviceInfo [isMobileSupported=" + isMobileSupported + ", qrcodeEncryptedData="
				+ qrcodeEncryptedData + ", challengeCode=" + challengeCode + ", tokenAvailable=" + tokenAvailable
				+ ", tokenDeviceCount=" + tokenDeviceCount + ", tokenqrcodeEncryptedData=" + tokenqrcodeEncryptedData
				+ ", tokenqrcodeEncryptedVersion=" + tokenqrcodeEncryptedVersion + "]";
	}
	
}
