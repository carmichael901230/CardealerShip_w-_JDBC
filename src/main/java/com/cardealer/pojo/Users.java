package com.cardealer.pojo;

public class Users {
	private int id;
	private String userid;
	private String password;
	private int role; 		// 0=>customer, 1=>employee
	private String fullName;
	
	
	public Users() {
		this.id = 0;
		this.userid = null;
		this.password = null;
		this.role = 0;
		this.fullName = null;
	}
	public Users(String userid, String password) {
		this.id = -1;
		this.userid = userid;
		this.password = password;
		this.role = -1;
		this.fullName = null;
	}
	public Users(String userid, String password, int role, String fullName) {
		super();
		this.id = -1;
		this.userid = userid;
		this.password = password;
		this.role = role;
		this.fullName = fullName;
	}
	
	public Users(int id, String userid, String password, int role, String fullName) {
		super();
		this.id = id;
		this.userid = userid;
		this.password = password;
		this.role = role;
		this.fullName = fullName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	@Override
	public String toString() {
		return "Users [id=" + id + ", userid=" + userid + ", password=" + password + ", role=" + role + ", fullName="
				+ fullName + "]";
	}
	public String display() {
		return String.format("%10s|%20s", userid, fullName);
	}
	
}
