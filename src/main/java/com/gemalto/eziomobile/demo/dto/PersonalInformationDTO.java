package com.gemalto.eziomobile.demo.dto;

public class PersonalInformationDTO {
	
	private String emailAddress;

	private String currPassword;

	private String newPassword;

	private String confirmPwd;

	private String updateField;

	private String username;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getCurrPassword() {
		return currPassword;
	}

	public void setCurrPassword(String currPassword) {
		this.currPassword = currPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPwd() {
		return confirmPwd;
	}

	public void setConfirmPwd(String confirmPwd) {
		this.confirmPwd = confirmPwd;
	}

	public String getUpdateField() {
		return updateField;
	}

	public void setUpdateField(String updateField) {
		this.updateField = updateField;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "UpdateMyDetailsDTO [emailAddress=" + emailAddress + ", currPassword=" + currPassword + ", newPassword="
				+ newPassword + ", confirmPwd=" + confirmPwd + ", updateField=" + updateField + ", username=" + username
				+ "]";
	}

}
