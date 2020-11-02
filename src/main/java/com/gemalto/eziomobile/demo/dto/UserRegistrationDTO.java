package com.gemalto.eziomobile.demo.dto;

public class UserRegistrationDTO {
	
	private String emailAddress;

	private String firstName;

	private String lastName;

	private String companyName;

	private String country;

	private String username;

	private String password;

	private String activationKey;

	private int status;

	private String lastUpdate;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "UserRegistrationDTO [emailAddress=" + emailAddress + ", firstName=" + firstName + ", lastName="
				+ lastName + ", companyName=" + companyName + ", country=" + country + ", username=" + username
				+ ", password=" + password + ", activationKey=" + activationKey + ", status=" + status + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
