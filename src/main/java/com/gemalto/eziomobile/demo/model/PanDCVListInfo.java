package com.gemalto.eziomobile.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pandcvlistmaster")
public class PanDCVListInfo extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "pan")
	private String panNo;

	@Column(name = "psn")
	private String psnValue;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getPsnValue() {
		return psnValue;
	}

	public void setPsnValue(String psnValue) {
		this.psnValue = psnValue;
	}

	@Override
	public String toString() {
		return "PanDCVListInfo [id=" + id + ", panNo=" + panNo + ", psnValue=" + psnValue + "]";
	}

}
