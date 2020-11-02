package com.gemalto.eziomobile.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "userpreferencemaster")
public class UserPreferenceInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "user_uid")
	private int userId;

	@Column(name = "sec_mode")
	private int sec_mode;

	@Column(name = "sec_login")
	private String sec_login;

	@Column(name = "sec_txownacc")
	private String sec_txownacc;

	@Column(name = "sec_txother")
	private String sec_txother;

	@Column(name = "sec_addpayee")
	private String sec_addpayee;

	@Column(name = "status")
	private int status;

	@Column(name = "updated_date")
	private Date updated_date;

	@Column(name = "sec_ecommerce3ds")
	private String sec_ecommerce3ds;

	@Column(name = "p2p_notification")
	private String p2p_notification;

	@Column(name = "web_notification")
	private String web_notification;

	@Column(name = "mobile_banking")
	private String mobile_banking;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSec_mode() {
		return sec_mode;
	}

	public void setSec_mode(int sec_mode) {
		this.sec_mode = sec_mode;
	}

	public String getSec_login() {
		return sec_login;
	}

	public void setSec_login(String sec_login) {
		this.sec_login = sec_login;
	}

	public String getSec_txownacc() {
		return sec_txownacc;
	}

	public void setSec_txownacc(String sec_txownacc) {
		this.sec_txownacc = sec_txownacc;
	}

	public String getSec_txother() {
		return sec_txother;
	}

	public void setSec_txother(String sec_txother) {
		this.sec_txother = sec_txother;
	}

	public String getSec_addpayee() {
		return sec_addpayee;
	}

	public void setSec_addpayee(String sec_addpayee) {
		this.sec_addpayee = sec_addpayee;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public String getSec_ecommerce3ds() {
		return sec_ecommerce3ds;
	}

	public void setSec_ecommerce3ds(String sec_ecommerce3ds) {
		this.sec_ecommerce3ds = sec_ecommerce3ds;
	}

	public String getP2p_notification() {
		return p2p_notification;
	}

	public void setP2p_notification(String p2p_notification) {
		this.p2p_notification = p2p_notification;
	}

	public String getWeb_notification() {
		return web_notification;
	}

	public void setWeb_notification(String web_notification) {
		this.web_notification = web_notification;
	}

	public String getMobile_banking() {
		return mobile_banking;
	}

	public void setMobile_banking(String mobile_banking) {
		this.mobile_banking = mobile_banking;
	}

	@Override
	public String toString() {
		return "UserPreferenceInfo [id=" + id + ", userId=" + userId + ", sec_mode=" + sec_mode + ", sec_login="
				+ sec_login + ", sec_txownacc=" + sec_txownacc + ", sec_txother=" + sec_txother + ", sec_addpayee="
				+ sec_addpayee + ", status=" + status + ", updated_date=" + updated_date + ", sec_ecommerce3ds="
				+ sec_ecommerce3ds + ", p2p_notification=" + p2p_notification + ", web_notification=" + web_notification
				+ ", mobile_banking=" + mobile_banking + "]";
	}

}
