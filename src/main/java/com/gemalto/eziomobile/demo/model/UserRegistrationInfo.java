package com.gemalto.eziomobile.demo.model;

import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "userregistrationmaster")
public class UserRegistrationInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "emailaddress")
	private String emailaddress;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "companyname")
	private String companyname;

	@Column(name = "country")
	private String country;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "activationkey")
	private String activationkey;

	@Column(name = "status")
	private int status;

	@UpdateTimestamp
	@Temporal(TemporalType.DATE)
	@Column(name = "lastupdate")
	private Date lastupdate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
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

	public String getActivationkey() {
		return activationkey;
	}

	public void setActivationkey(String activationkey) {
		this.activationkey = activationkey;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}

	@Override
	public String toString() {
		return "UserRegistrationInfo [id=" + id + ", emailaddress=" + emailaddress + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", companyname=" + companyname + ", country=" + country + ", username="
				+ username + ", password=" + password + ", activationkey=" + activationkey + ", status=" + status
				+ ", lastupdate=" + lastupdate + "]";
	}

}
