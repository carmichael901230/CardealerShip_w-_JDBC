package com.cardealer.pojo;

public class Cars {
	private int id;
	private String make;
	private String model;
	private String color;
	private String year;
	private double price;
	private int owner;		// FK to user.id
	

	public Cars() {
		this.id = -1;
		this.make = null;
		this.model = null;
		this.color = null;
		this.year = null;
		this.price = Double.MAX_VALUE;
		this.owner = 0;
	}
	public Cars(String make, String model, String color, String year, double price) {
		this.id = -1;
		this.make = make;
		this.model = model;
		this.color = color;
		this.year = year;
		this.price = price;
		this.owner = -1;
	}
	public Cars(String make, String model, String color, String year, double price, int owner) {
		super();
		this.id = -1;
		this.make = make;
		this.model = model;
		this.color = color;
		this.year = year;
		this.price = price;
		this.owner = owner;
	}
	public Cars(int id, String make, String model, String color, String year, double price, int owner) {
		super();
		this.id = id;
		this.make = make;
		this.model = model;
		this.color = color;
		this.year = year;
		this.price = price;
		this.owner = owner;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getOwner() {
		return owner;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	@Override
	public String toString() {
		return "Cars [make=" + make + ", model=" + model + ", color=" + color + ", year=" + year
				+ ", price=" + price + ", owner=" + owner + "]";
	}
	
	public String display() {
		return String.format("%10s|%10s|%10s|%10s|%10.2f", make, model, color, year, price);
	}
}
