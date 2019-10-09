package com.cardealer.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import com.cardealer.pojo.Cars;
import com.cardealer.pojo.Users;
import static com.cardealer.util.LoggerUtil.*;

public class Menu {
	private static String URL;
	private static String USERNAME;
	private static String PASSWORD;
	private static final String PROPERTIES_FILE = "src/main/resources/database.properties";
	
	private Connection conn;
	private Scanner in;
	private Services serve;
	
	private Users curUser;
	private int loggedIn;
	private int validEnter;
	
	public Menu() {
		Properties prop = new Properties();
		
		try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
			
			prop.load(fis);
			URL = prop.getProperty("url");
			USERNAME = prop.getProperty("user");
			PASSWORD = prop.getProperty("password");
			
		} catch (FileNotFoundException e) {
			error("Util: fail to open file ["+PROPERTIES_FILE+"] to retrieve db info");
			e.printStackTrace();
		} catch (IOException e) {
			error("Util: fail to get db info from ["+PROPERTIES_FILE+"]");
			e.printStackTrace();
		}
		
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			info("Util: successfully connected to db");
		} catch (SQLException e) {
			e.printStackTrace();
			error("Connect DB: fail to connect to db");
		}
		serve = new Services();
		in = new Scanner(System.in);
		loggedIn = 0;
		curUser = null;
		validEnter = 1;
	}
	
	public void run() {
		int c = 0;
		
		loginLabel:
		while (loggedIn == 0) {
			if (curUser == null) {
				Menu.lineSplitter();
				Menu.displayTitle();
				System.out.println("1. Login");
				System.out.println("2. Register");
				if (validEnter == 0) {
					System.out.println("Invalid input, Choose again...");
				}
				System.out.print("Enter # Choice: ");
				try {
					c = Integer.parseInt(in.nextLine());
				} catch (Exception e) {
					validEnter = 0;
					continue;
				}
			}
			// login
			if (c == 1) {
				if (curUser == null) {
					curUser = serve.login(conn, in);
				}
				
				// successfully login
				else {
					// login as customer
					while (curUser.getRole() == 0) {
						c = 0;
						loggedIn = 1;
						Menu.lineSplitter();
						Menu.displayTitle();
						System.out.println("1. View Cars on Lot");
						System.out.println("2. Make an Offer");
						System.out.println("3. View My Cars");
						System.out.println("4. Make Payment");
						System.out.println("0. Logout");
						if (this.validEnter == 0) {
							System.out.println("Invalid input, Choose again...");
						}
						System.out.print("Enter # Choice: ");
						try {
							c = Integer.parseInt(in.nextLine());
						} catch (NumberFormatException e) {
							System.out.println("Invalid input, Choose again...");
						}
						// view car on lot
						if (c == 1) {
							ArrayList<Cars> carLot = serve.getCarLot(conn);
							serve.displayCarLot(carLot, conn);
							
						}
						// make an offer
						else if (c == 2) {
							if (serve.makeOffer(curUser, conn, in)) {
								System.out.println("Offer has been placed");
							}
						}
						// view my cars
						else if (c == 3) {
							serve.displayCustCarList(curUser, conn);
						}
						else if (c == 4) {
							if (serve.makePayment(curUser, conn, in)) {
								System.out.println("Payment has been placed");
							}
						}
						// logout
						else if (c == 0) {
							this.curUser = null;
							this.loggedIn = 0;
							continue loginLabel;
						}
						else {
							validEnter = 0;
						}
					}
					// login as employee
					while (curUser.getRole() == 1) {
						this.loggedIn = 2;
						Menu.lineSplitter();
						Menu.displayTitle();
						System.out.println("1. Add Car to Lot");
						System.out.println("2. Remove a Car");
						System.out.println("3. Accept an Offer");
						System.out.println("4. Reject an Offer");
						System.out.println("5. View Payments");
						System.out.println("0. Logout");
						if (this.validEnter == 0) {
							System.out.println("Invalid input, Choose again...");
						}
						System.out.println("Enter # Choice: ");
						c = Integer.parseInt(in.nextLine());
						// add car to lot
						if (c == 1) {
							validEnter = 1;
							Menu.lineSplitter();
							Menu.displayTitle();
							if (serve.addCar(conn, in)) {
								System.out.println("New Car is Added to Lot");
							}
							else {
								System.out.println("Fail to Add Car to Lot, please try again");
							}
						}
						// remove a car from lot
						else if (c == 2) {
							validEnter = 1;
							Menu.lineSplitter();
							Menu.displayTitle();
							if (serve.removeCar(conn, in)) {
								System.out.println("Car is deleted");
							}
							else {
								System.out.println("Fail to delete the car, please try again");
							}
						}
//						// accept an offer
						else if (c == 3) {
							validEnter = 1;
							Menu.lineSplitter();
							Menu.displayTitle();
							if (serve.acceptOffer(curUser, conn, in)) {
								System.out.println("Offer is accepted");
							} else {
								System.out.println("Fail to reject the offer, please try again");
							}
							
						}
//						// reject offer
						else if (c == 4) {
							validEnter = 1;
							Menu.lineSplitter();
							Menu.displayTitle();
							if (serve.rejectOffer(conn, in)) {
								System.out.println("Offer is rejected");
							}
							else {
								System.out.println("Fail to reject offer, please try again");
							}
						}
//						// view payment
						else if (c == 5) {
							validEnter = 1;
							Menu.lineSplitter();
							Menu.displayTitle();
							serve.viewTransaction(conn);
							
						}
						// log out
						else if (c == 0) {
							validEnter = 1;
							this.curUser = null;
							this.loggedIn = 0;
							continue loginLabel;
						}
	
						else {
							validEnter = 0;
						}
					}
				}
			}
			// choose to register
			else if (c == 2) {
				curUser = serve.registerUser(conn, in);
				if (curUser != null) {
					System.out.println("New user created");
					this.loggedIn = 0;
					c = 1;
					continue loginLabel;
				}
				else {
					System.out.println("Failed to create new user, please try again");
				}
				
			}
		}
	}
	
	
//	display title of car dealer
	public static void displayTitle() {
		System.out.println("*******************************************");
		System.out.println("*               Car Dealer                *");
		System.out.println("*******************************************");
	}
	
//	splitter
	public static void lineSplitter() {
		System.out.println();
		System.out.println("===========================================");
		System.out.println();
	}
	
	public void testRegister() {
		
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			in = new Scanner(System.in);
			serve.registerUser(conn, in);
			
		} catch (SQLException e) {
			error("Util: fail to connect to DB");
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			in.close();
		}
	}
	
	public Users testLogin() {
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			in = new Scanner(System.in);
			info("Util: Database connected");
			return serve.login(conn, in);
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			in.close();
		}
	}
	
	public void testAddCar() {
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			in = new Scanner(System.in);
			info("Util: Database connected");
			serve.addCar(conn, in);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			in.close();
		}
	}
	
	public void testShowCarLot() {
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			in = new Scanner(System.in);
			info("Util: Database connected");
			ArrayList<Cars> carLot = serve.getCarLot(conn);
			serve.displayCarLot(carLot, conn);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			in.close();
		}
	}
	
}
