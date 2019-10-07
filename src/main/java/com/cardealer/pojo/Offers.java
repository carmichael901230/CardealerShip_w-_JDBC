package com.cardealer.pojo;

public class Offers {
	private int id;
	private int userid;
	private int carid;
	private double offerAmt;
	private int status; 		// 0=>pending, 1=>accepted, -1=>rejected
	
	
	public Offers() {
		this.id = 0;
		this.carid = 0;
		this.userid = 0;
		this.offerAmt = Double.MAX_VALUE;
		this.status = 0;
	}
	
	public Offers(int userid, int carid) {
		this.id = -1;
		this.userid = userid;
		this.carid = carid;
		this.offerAmt = Double.MAX_VALUE;
		this.status = 0;
	}

	public Offers(int userid, int carid, double offerAmt) {
		super();
		this.id = -1;
		this.userid = userid;
		this.carid = carid;
		this.offerAmt = offerAmt;
		this.status = 0;
	}
	public Offers(int id, int userid, int carid, double offerAmt, int status) {
		super();
		this.id = id;
		this.userid = userid;
		this.carid = carid;
		this.offerAmt = offerAmt;
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getCarid() {
		return carid;
	}
	public void setCarid(int carid) {
		this.carid = carid;
	}
	public double getOfferAmt() {
		return offerAmt;
	}
	public void setOfferAmt(double offerAmt) {
		this.offerAmt = offerAmt;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Offers [userid=" + userid + ", carid=" + carid + ", offerAmt=" + offerAmt
				+ ", status=" + status + "]";
	}
	
}
