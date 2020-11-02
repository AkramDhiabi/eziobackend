package com.gemalto.eziomobile.demo.dto;

public class DCVTokenDTO {

	private String dcvCardNo;

	private String dcvTokenId;

	public String getDcvCardNo() {
		return dcvCardNo;
	}

	public void setDcvCardNo(String dcvCardNo) {
		this.dcvCardNo = dcvCardNo;
	}

	public String getDcvTokenId() {
		return dcvTokenId;
	}

	public void setDcvTokenId(String dcvTokenId) {
		this.dcvTokenId = dcvTokenId;
	}

	@Override
	public String toString() {
		return "DCVTokenDTO [dcvCardNo=" + dcvCardNo + ", dcvTokenId=" + dcvTokenId + "]";
	}
}
