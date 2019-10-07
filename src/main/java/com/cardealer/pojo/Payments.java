package com.cardealer.pojo;

import java.util.Calendar;

public class Payments {
	private int id;
	private int userid;
	private int carid;
	private double amount;
	private Calendar payDate;
	
	public Payments() {
		this.id = 0;
		this.userid = 0;
		this.carid = 0;
		this.amount = Double.MAX_VALUE;
		this.payDate = Calendar.getInstance();
	}


	public Payments(int userid, int carid, double amount) {
		this.carid = -1;
		this.userid = userid;
		this.carid = carid;
		this.amount = amount;
		this.payDate = Calendar.getInstance();
	}


	public Payments(int id, int userid, int carid, double amount, Calendar payDate) {
		super();
		this.id = id;
		this.userid = userid;
		this.carid = carid;
		this.amount = amount;
		this.payDate = payDate;
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


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public Calendar getPayDate() {
		return payDate;
	}


	public void setPayDate(Calendar payDate) {
		this.payDate = payDate;
	}


	@Override
	public String toString() {
		return "Payments [userid=" + userid + ", carid=" + carid + ", amount=" + amount+"]";
	}
	
	
}
