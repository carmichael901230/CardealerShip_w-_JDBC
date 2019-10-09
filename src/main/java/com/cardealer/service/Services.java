package com.cardealer.service;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import com.cardealer.pojo.Cars;
import com.cardealer.pojo.Offers;
import com.cardealer.pojo.Payments;
import com.cardealer.pojo.Users;
import static com.cardealer.util.LoggerUtil.*;


public class Services {
	
	// user register 
	// 1. read user input and create Users object
	// 2. insert new Users object to db
	// 3. retrieve new user from db and return (user.id is handled by db)
	public Users registerUser(Connection conn, Scanner cin) {
		Users newUser = createUserObj(conn, cin);
		if (newUser == null)
			return null;
		if (insertUser(newUser, conn)) {
			String query = "SELECT * FROM cardealership.users "
					+ "WHERE userid = ? AND password = ? AND fullname = ?;";
			PreparedStatement stmt = null;
			try {
				stmt = conn.prepareStatement(query);
				stmt.setString(1, newUser.getUserid());
				stmt.setString(2, newUser.getPassword());
				stmt.setString(3, newUser.getFullName());
				ResultSet res = stmt.executeQuery();
				if  (res.next()) {
					newUser.setId(res.getInt("id"));
				}
			} catch (SQLException e) {
				error("Create User: fail to retrieve new user info from db");
				e.printStackTrace();
			}
			if (newUser.getId() != -1) 
				return newUser;
		}
			
		return null;
	}
	
	private boolean insertUser(Users user, Connection conn) {
		String query = "call cardealership.insert_user(?, ?, ?, ?);";
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, user.getUserid());
			stmt.setString(2, user.getPassword());
			stmt.setInt(3, user.getRole());
			stmt.setString(4, user.getFullName());
			stmt.execute();
			info("Create User: successfully created new "+user.getUserid()+" via stored procedure");
			return true;
		} catch (SQLException e) {
			error("Create User: fail to write new user into DB");
			e.printStackTrace();
			return false;
		}
	}
	
	public Users createUserObj(Connection conn, Scanner cin) {
		String userId = null;
		String password = null;
		String fullName = null;
		boolean readsuccess = false;
		// getting user info from user input
		try {
			System.out.println("Creating new User");
			System.out.print("Enter UserID: ");
			userId = cin.nextLine();
			System.out.print("Enter PassWord: ");
			password = cin.nextLine();
			System.out.print("Enter Your FullName: ");
			fullName = cin.nextLine();
			info("Create User: successfully created new user object");
			readsuccess = true;
		}catch(Exception e) {
			error("Create User: fail to read user input from console");
			return null;
		}
		
		// create user object
		if (readsuccess) {
			Users tempUser = new Users(userId, password, 0, fullName);
			return tempUser;
		}
		return null;
	}
	
	// user login
	// 1. read read user input create temp Users object
	// 2. select users from db where username and password match
	// 3. return login user obj
	public Users login(Connection conn, Scanner cin) {
		Users loginUser = loginUserObj(cin);
		if (loginUser == null) 
			return null;
		return lookupLoginUser(loginUser, conn);
	}
	
	private Users loginUserObj(Scanner cin) {
		String userId;
		String password;
		try {
			System.out.println("Login in");
			System.out.print("Enter UserID: ");
			userId = cin.nextLine();
			System.out.print("Enter Password: ");
			password = cin.nextLine();
			Users tempUser = new Users(userId, password);
			return tempUser;
		} catch (Exception e) {
			error("Login: fail to get user input from console");
		}
		return null;
	}
	private Users lookupLoginUser(Users user, Connection conn) {
		String query = "SELECT * FROM cardealership.users "
				+ "WHERE userid = ? AND password = ?;";
		Users dbUser = null;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, user.getUserid());
			stmt.setString(2, user.getPassword());
			ResultSet res = stmt.executeQuery();
			if (res.next()) {
				info("Login: successfully log in as "+user.getUserid());
				dbUser = new Users(res.getInt("id"), res.getString("userid"), res.getString("password"), res.getInt("usertype"), res.getString("fullname"));
				return dbUser;
			}
			else {
				System.out.println("Incorrect username or password, please try again");
			}
		} catch (SQLException e) {
			error("Login: fail to login as "+user.getUserid() + " due to invalid username or password");
			e.printStackTrace();
		}
		return null;
	}
	
	// add car
	// 1. prompt employee to enter car info, and create Cars obj
	// 2. insert new car into db
	public boolean addCar(Connection conn, Scanner cin) {
		Cars tempCar = null;
		tempCar = createNewCar(cin);
		if (tempCar == null)
			return false;
		return insertCar(tempCar, conn);
	}
	
	public Cars createNewCar(Scanner cin) {
		String make = null;
		String model = null;
		String color = null;
		String year = null;
		double price = 0.0;
		// prompt employee to enter car info
		try {
			System.out.println("Adding new Car to Lot");
			System.out.print("Enter Brand: ");
			make = cin.nextLine();
			System.out.print("Enter Model: ");
			model = cin.nextLine();
			System.out.print("Enter Color: ");
			color = cin.nextLine();
			System.out.print("Enter Model Year: ");
			year = cin.nextLine();
			System.out.print("Enter Price: ");
			price = Double.parseDouble(cin.nextLine());
			Cars tempCar = new Cars(make, model, color, year, price);
			info("Add Car: successfully got user input and created new Car object");
			return tempCar;
		} catch (Exception e) {
			error("Add Car: fail to get user input of car info");
			return null;
		} 
	}
	
	private boolean insertCar(Cars car, Connection conn) {
		String query = "INSERT INTO cardealership.cars (make, model, color, yearmade, ownerofcar, price)"
				+ "VALUES (?, ?, ?, ?, -1, ?);";
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, car.getMake());
			stmt.setString(2, car.getModel());
			stmt.setString(3, car.getColor());
			stmt.setString(4, car.getYear());
			stmt.setDouble(5, car.getPrice());
			System.out.println(stmt.toString());
			int res = stmt.executeUpdate();
			if (res > 0) {
				info("Add Car: successfully add new " + car.toString() + " to lot");
				return true;
			}
		} catch (SQLException e) {
			error("Add Car: fail to add new " + car.toString() + " to lot");
			e.printStackTrace();
		}
		return false;
	}
	
	// get car lot, return ArrayList with complete car infos
	// 1. access db, get all cars that owner=-1, return ArrayList<Cars>
	// 2. for each car in ArrayList, get its pendingList as ArrayList of user.ids
	// 3. get username for each user.id in the ArrayList, and construct a string of usernames
	// 4. display the carlot
	public ArrayList<Cars> getCarLot(Connection conn) {
		String query = "SELECT * FROM cardealership.cars "
				+ "WHERE ownerofcar = -1;";
		PreparedStatement stmt = null;
		ArrayList<Cars> carLot = new ArrayList<Cars>();
		int id = 0;
		String make = null;
		String model = null;
		String color = null;
		String yearmade = null;
		int ownerofcar = -2;
		double price = 0.0;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				id = res.getInt("id");
				make = res.getString("make");
				model = res.getString("model");
				color = res.getString("color");
				yearmade = res.getString("yearmade");
				ownerofcar = res.getInt("ownerofcar");
				price = res.getDouble("price");
				carLot.add(new Cars(id, make, model, color, yearmade, price, ownerofcar));
			}
			return carLot;
		} catch (SQLException e) {
			error("View Carlot: fail to get CarLot due to SQLException");
			e.printStackTrace();
		}
		return null;
	}
	
	//  display car lot to console, with car info and pendingList 
	public void displayCarLot(ArrayList<Cars> carLot, Connection conn)  {
		// display cars on lot
		String header = String.format("%3s|%10s|%10s|%10s|%10s|%10s|%-20s", "#", "Brand", "Model", "Color", "Year Made", "Price", "PendingList");
		System.out.println(header);
		System.out.println("--------------------------------------------------------------------------");
		if (carLot != null) {
			for (int i=0; i<carLot.size(); i++) {
				Cars curCar = carLot.get(i);
				System.out.print(String.format("%3s|", i+1));
				System.out.print(curCar.display());
				System.out.println("|"+getPendingListStr(curCar, conn));
			}
		}
		else {
			System.out.println("Car lot is empty, please come back later. We are working very hard to register new cars");
		}
	}
	
	// get the users in the pending list of a given car
	private String getPendingListStr(Cars car, Connection conn) {
		ArrayList<Integer> useridList = getPendingList(car, conn);
		if (useridList == null) {
			return "";
		}
		return pendingListStr(useridList, conn);
		
	}

	
	// given a Cars obj, return the ArrayList of uesrid in its pendingList
	private ArrayList<Integer> getPendingList(Cars car, Connection conn) {
		ArrayList<Integer> pendingUser = new ArrayList<Integer>();
		PreparedStatement stmt = null;
		String query = "SELECT userid FROM cardealership.offers "
					 + "WHERE carid = ? AND status = 0;";
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, car.getId());
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				pendingUser.add(res.getInt("userid"));
			}

			return pendingUser;
		} catch (SQLException e) {
			error("Show PendingList: fail to retrieve user.id from db");
			e.printStackTrace();
			return null;
		}
	}

	// receive user.id in the pendingList of a car, 
	// return String of usernames where usernames concatenate with each other
	private String pendingListStr(ArrayList<Integer> pendingList, Connection conn) {
		StringBuilder resStr = new StringBuilder();
		String query = "SELECT userid FROM cardealership.users "
				+ "WHERE id = ?;";
		PreparedStatement stmt = null;
		for (int i=0; i<pendingList.size(); i++) {
			try {
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, pendingList.get(i));
				ResultSet res = stmt.executeQuery();
				if (res.next()) {
					resStr.append(res.getString("userid")+", ");
				}
			} catch (SQLException e) {
				error("Show PendingList: fail to retrieve userid from db");
				e.printStackTrace();
				return null;
			}
			
		}
		return resStr.toString();
	}
	
	// current logged in user make off on car on lot
	// 1. get carLot
	// 2. prompt user to enter # of car, and retrieve the selected car
	// 3. add new record on offers table
	public boolean makeOffer(Users loginUser, Connection conn, Scanner cin) {
		boolean validChoice = false;
		while (!validChoice) {
			// display carlot
			ArrayList<Cars> carLot = getCarLot(conn);
			displayCarLot(carLot, conn);
			// prompt user to make choice
			System.out.print("Enter # of Car to Make an Offer: ");
			int choice = Integer.parseInt(cin.nextLine());
			// Retrieve chosen car
			Cars carChosen = null;
			try {
				carChosen = carLot.get(choice-1);
				validChoice = true;
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Invalid Car #, Please select Again");
				continue;
			}
			System.out.print("Enter Offer Price: ");
			double offerPrice = Double.parseDouble(cin.nextLine());
			Offers newOffer = new Offers(loginUser.getId(), carChosen.getId(), offerPrice);
			// modify offers table in db
			PreparedStatement stmt = null;
			String query = "INSERT INTO cardealership.offers (userid, carid, offerprice)"
					+ "VALUES (?, ?, ?);";

			try {
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, newOffer.getUserid());
				stmt.setInt(2, newOffer.getCarid());
				stmt.setDouble(3, newOffer.getOfferAmt());
				System.out.println(stmt.toString());
				int row = stmt.executeUpdate();
				if (row > 0) {
					info("Make Offer: successfully placed new "+newOffer.toString());
				}
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				error("Make Offer: fail to make offer due to SQL Error");
			}
		}
		return false;
	}
	
	// remove car from car lot
	// 1. display cars on carlot
	// 2. prompt employee to enter car #
	// 3. retrieve selected car
	// 4. update db
	public boolean removeCar(Connection conn, Scanner cin) {
		// display carlot
		ArrayList<Cars> carLot = getCarLot(conn);
		displayCarLot(carLot, conn);
		// prompt employee to make choice
		System.out.println("Enter # of Car");
		int carSelected = 0;
		try {
			carSelected = Integer.parseInt(cin.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid Car Number...");
			return false;
		}
		// retrieve selected car obj
		Cars carChosen = carLot.get(carSelected-1);
		// update db
		String query = "DELETE FROM cardealership.cars "
				+ "WHERE id = ? AND make = ? AND model = ?;";
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, carChosen.getId());
			stmt.setString(2, carChosen.getMake());
			stmt.setString(3, carChosen.getModel());
			int row = stmt.executeUpdate();
			if (row > 0) {
				info("Remove Car: Successfully removed "+carChosen.toString() + " from db");
				return true;
			}
			else {
				error("Remove Car: fail to remove "+carChosen.toString() + " from db");
				return false;
			}
		} catch (SQLException e) {
			error("Remove Car: fail to remove car from db due to SQL Error");
			e.printStackTrace();
		}
		return false;
	}

	// accept offer
	// 0. get current car lot
	// 1. prompt employee to chose a car #
	// 2. retrieve selected car
	// 3. prompt employee to chose a user #
	// 4. create offer obj
	// 4. update offer.status, update car.owner
	public boolean acceptOffer(Users loginUser, Connection conn, Scanner in) {
		// display car lot
		ArrayList<Cars> carLot = getCarLot(conn);
		displayCarLot(carLot, conn);
		// prompt employee to enter car #
		System.out.println("Enter # of Car");
		int choice = 0;
		try {
			choice = Integer.parseInt(in.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid Car Number, please try again");
			return false;
		}
		// retrieve selected car
		Cars carChosen = carLot.get(choice-1);
		// get pending list of the car
		ArrayList<Users> pendingUser = getOfferList(carChosen, conn);
		displayPendingList(pendingUser, carChosen, conn);
		// prompt employee to choose a user
		System.out.println("Enter # of Offer to Accpet: ");
		try {
			choice = Integer.parseInt(in.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid Offer Number, please try again");
			return false;
		}
		// got user obj
		Users offerUser = pendingUser.get(choice-1);
		Offers acceptedOffer = new Offers(offerUser.getId(), carChosen.getId());
		// update offer table
		String query = "UPDATE cardealership.offers "
				+ "	SET status = 1"
				+ "WHERE userid = ? AND carid = ? AND status = 0;";
		PreparedStatement stmt = null;
		int updateRow = 0;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, offerUser.getId());
			stmt.setInt(2, carChosen.getId());
			updateRow = stmt.executeUpdate();
			if (updateRow > 0) {
				info("Accept Offer: successfully updated offers table for carid: "+carChosen.getId()+", userid: "+offerUser.getId());
				query = "UPDATE cardealership.cars "
						+ "SET ownerofcar = ?"
						+"WHERE id = ?;";
				// update cars table
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, offerUser.getId());
				stmt.setInt(2, carChosen.getId());
				updateRow = stmt.executeUpdate();
				if(updateRow > 0) {
					info("Accept Offer: successfully updated cars table for carid :"+carChosen.getId()+", new owner is "+offerUser.getUserid());
					return true;
				}
				else {
					error("Accept Offer: fail to update cars table for carid :"+carChosen.getId());
				}
			}
			else {
				error("Accept Offer: fail to update offers table for carid: "+carChosen.getId()+", userid: "+offerUser.getId());
			}
		} catch (SQLException e) {
			error("Accept Offer: fail to accept offer due to SQL Error");
			e.printStackTrace();
		}
		return false;
	}
	
	// get complete info of users in a car's pending list
	private ArrayList<Users> getOfferList(Cars car, Connection conn) {
		// look into offers table 
		// get user.id in the pendingList
		ArrayList<Users> userList = new ArrayList<Users>();
		ArrayList<Integer> userIdList = new ArrayList<Integer>();
		String query = "SELECT * FROM cardealership.offers "
				+ "WHERE carid = ? AND status = 0;";
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, car.getId());
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				userIdList.add(res.getInt("userid"));
			}
			// retrieve real users obj from user table
			query = "SELECT * FROM cardealership.users "
					+ "WHERE id = ?;";
			try {
				stmt = conn.prepareStatement(query);
				for (int i=0; i<userIdList.size(); i++) {
					stmt.setInt(1, userIdList.get(i));
					ResultSet subRes = stmt.executeQuery();
					if (subRes.next()) {
						userList.add(new Users(subRes.getInt("id"), subRes.getString("userid"), subRes.getString("password"), subRes.getInt("usertype"), subRes.getString("fullname")));
					}
				}
				return userList;
			} catch (SQLException e) {
				error("Accept Offer: fail to retrieve user object of pending list for "+car.toString());
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			error("Accept Offer: fail to retrieve user.id of pending list for "+car.toString());
			e.printStackTrace();
		}
		return null;
	}
	
	// display pendingList
	private void displayPendingList(ArrayList<Users> pendingList, Cars carChosen, Connection conn) {
		String header = String.format("%3s|%10s|%20s|%10s", "#", "User ID", "Full Name", "Offer Price");
		System.out.println(header);
		System.out.println("----------------------------------------");
		
		String query = "SELECT offerprice FROM cardealership.offers "
				+ "WHERE userid = ? AND carid = ? AND status = 0;";
		PreparedStatement stmt = null;
		
		if (pendingList != null) {
			for (int i=0; i<pendingList.size(); i++) {
				System.out.print(String.format("%3s|", i+1));
				System.out.print(pendingList.get(i).display());
				// get offer price from offers table
				try {
					stmt = conn.prepareStatement(query);
					stmt.setInt(1, pendingList.get(i).getId());
					stmt.setInt(2, carChosen.getId());
					ResultSet res = stmt.executeQuery();
					if (res.next()) {
						System.out.println(String.format("|%10.2f", res.getDouble("offerprice")));
					}
				} catch (SQLException e) {
					error("Show PendingList: fail to load pendinglist from db");
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("Pending List is empty. No customer has made offer to this car");
		}
	}

	// display cars that are owned by given user
	// 1. look into cars table, find all car.ownerofcar = user.id
	// 2. look into offers table, find offerprice for each car
	// 3. look into payments table, find all payment made on each car
	public void displayCustCarList(Users user, Connection conn) {
		String header = String.format("%3s|%10s|%10s|%10s|%12s|%10s", "#", "Brand", "Model", "Color", "Offer Price", "Paid Amt");
		System.out.println(header);
		System.out.println("----------------------------------------------------------");
		PreparedStatement stmt = null;
		ResultSet res = null;
		String query = "SELECT * FROM cardealership.cars "
				+ "WHERE ownerofcar = ?;";
		int i = 1;
		try {
			// get all cars of the user
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, user.getId());
			res = stmt.executeQuery();
			boolean hasCar = false;
			while (res.next()) {
				hasCar = true;
				int carid = res.getInt("id");
				// display car info
				System.out.print(String.format("%3s|%10s|%10s|%10s|", i++, res.getString("make"), res.getString("model"), res.getString("color")));
				// get and display offer price from offers table
				query = "SELECT * FROM cardealership.offers "
						+ "WHERE carid = ? AND userid = ? AND status = 1;";
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, carid);
				stmt.setInt(2, user.getId());
				ResultSet offerRes = stmt.executeQuery();
				if (offerRes.next()) {
					System.out.print(String.format("%12.2f|", offerRes.getDouble("offerprice")));
				}
				// get paid amount for the car
				double paid = 0.0;
				query = "SELECT * FROM cardealership.payments "
						+ "WHERE carid = ?;";
				stmt = conn.prepareStatement(query);
				stmt.setInt(1, carid);
				ResultSet payRes = stmt.executeQuery();
				while (payRes.next()) {
					paid += payRes.getDouble("amount");
				}
				System.out.println(String.format("%10.2f", paid));
			}
			
			if (!hasCar) {
				System.out.println("Bought List is empty, you don't own any car yet");
			}
		} catch (SQLException e) {
			error("View CustCars: fail to load cars belong to "+user.toString());
			e.printStackTrace();
		}
		
		
	}

	// employee reject an offer
	// 1. display car lot
	// 2. prompt empolyee to enter car # to remove from lot
	// 3. dispaly pending list of the car
	// 4. prompt employee to enter customer # to reject
	// 5. update offers table where offer.status = -1
	public boolean rejectOffer(Connection conn, Scanner in) {
		// display car lot
		ArrayList<Cars> carLot = getCarLot(conn);
		displayCarLot(carLot, conn);
		// prompt employee to select a car 
		System.out.print("Enter # of Car: ");
		int carNum = 0;
		try {
			carNum = Integer.parseInt(in.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid Offer Number, please try again");
			return false;
		}
		Cars selectedCar = carLot.get(carNum-1);
		// display pending list
		ArrayList<Users> offerList = getOfferList(selectedCar, conn);
		displayPendingList(offerList, selectedCar, conn);
		// prompt employee to enter customer number to reject offer
		System.out.println("Enter # of User to Reject the Offer: ");
		int userNum = 0;
		try {
			userNum = Integer.parseInt(in.nextLine());
		} catch (Exception e) {
			System.out.println("Invalid User Number, please try again");
		}
		Users selectedUser = offerList.get(userNum-1);
		// update offer table
		String query = "UPDATE cardealership.offers "
				+ "SET status = -1"
				+ "WHERE carid = ? AND userid = ?  AND status = 0;";
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, selectedCar.getId());
			stmt.setInt(2, selectedUser.getId());
			int row = stmt.executeUpdate();
			if (row > 0) {
				info("Reject Offer: successfully rejected offer, carid="+selectedCar.getId()+", userid="+selectedUser.getId());
				return true;
			}
			else {
				error("Reject Offer: fail to update offer in db");
			}
		} catch (SQLException e) {
			error("Reject Offer: fail to reject offer due to SQL Error");
			e.printStackTrace();
		}
		
		return false;
	}
	public ArrayList<Cars> getCustCarList(Users user, Connection conn) {
		ArrayList<Cars> carList = new ArrayList<Cars>();
		PreparedStatement stmt = null;
		ResultSet res = null;
		String query = "SELECT * FROM cardealership.cars "
				+ "WHERE ownerofcar = ?;";
		try {
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, user.getId());
			res = stmt.executeQuery();
			while (res.next()) {
				carList.add(new Cars(res.getInt("id"), res.getString("make"), res.getString("model"), res.getString("color"), res.getString("yearmade"), res.getDouble("price"), res.getInt("ownerofcar")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return carList;
	}

	// make payment on user owned cars
	// 1. get user owned car list, and show car list with monthly payment
	// 2. prompt user to enter car # to make payment
	// 3. look into offers table and get offerprice
	// 4. confirm payment
	// 5. create payment on payments table
	public boolean makePayment(Users user, Connection conn, Scanner in) {
		// get car list and display
		ArrayList<Cars> ownedCar = getCustCarList(user, conn);
		displayCustCarList(user, conn);
		// prompt user to enter car #
		System.out.println("Enter Car #: ");
		int choice = 0;
		boolean validEnter = false;
		while (!validEnter) {
			try {
				// get selected car
				choice = Integer.parseInt(in.nextLine());
				validEnter = true;
				Cars carSelect = ownedCar.get(choice-1);
				double offerPrice = 0.0;
				// look into offer and get offer price
				String query = "SELECT * FROM cardealership.offers "
						+ "WHERE userid = ? AND carid = ? AND status = 1;";
				PreparedStatement stmt = null;
				try {
					// get remaining payment from payments table,
					// if remaining payment < monthly pay, then nextpay = remaining payment
					String payQuery = "SELECT * FROM cardealership.payments "
							+ "WHERE userid = ? AND carid = ?;";
					double paidAmt = 0.0;
					try {
						PreparedStatement payStmt = conn.prepareStatement(payQuery);
						payStmt.setInt(1, user.getId());
						payStmt.setInt(2, carSelect.getId());
						ResultSet payRes = payStmt.executeQuery();
						while (payRes.next()) {
							paidAmt += payRes.getDouble("amount");
						}
					} catch(SQLException e) {
						error("Make Payments: fail to retrieve payment history from db");
						e.printStackTrace();
					}
					stmt = conn.prepareStatement(query);
					stmt.setInt(1, user.getId());
					stmt.setInt(2, carSelect.getId());
					ResultSet res = stmt.executeQuery();
					if (res.next()) {
						validEnter = false;
						offerPrice = res.getDouble("offerprice");
						double nextPay = offerPrice/12;
						// if nextPay is less then remaining payment, then just pay remaining payment
						if (nextPay > offerPrice - paidAmt) {
							nextPay = offerPrice - paidAmt;
						}
						System.out.println("Confirm Your Payment");
						System.out.println(String.format("Next Payment Amount: %10.2f", nextPay));
						System.out.println("1. Confirm");
						System.out.println("2. Cancel");
						while (!validEnter) {
							try {
								choice = Integer.parseInt(in.nextLine());
								validEnter = true;
								// confirm payment, insert new payment into payments table
								if (choice == 1) {
									Payments newPay = new Payments(user.getId(), carSelect.getId(), nextPay);
									query = "INSERT INTO cardealership.payments (userid, carid, amount)"
											+ " VALUES (?, ?, ?);";
									try {
										stmt = conn.prepareStatement(query);
										stmt.setInt(1, newPay.getUserid());
										stmt.setInt(2, newPay.getCarid());
										stmt.setDouble(3, nextPay);
										int row = stmt.executeUpdate();
										if(row > 0) {
											info("Make Payment: successfully placed new "+newPay.toString());
											return true;
										}
									} catch (SQLException e) {
										error("Make Payment: fail to create new payment in db due to SQL Error");
										return false;
									}
								}
								else {
									System.out.println("Payment Cancelled");
									return true;
								}
							} catch(NumberFormatException e) {
								System.out.println("Invalide choice, please try again");
								continue;
							}
						}
					}
				} catch (SQLException e) {
					error("Make Payment: fail to make payment due to SQL Error");
					e.printStackTrace();
				}
			} catch (Exception e) {
				System.out.println("Invalid Car #, please try again");
				continue;
			}
		}
		return false;
	}
	
	// view payment
	// 1. look into payments table, get carid and userid pair
	// 2. use userid and look into users table and get user info
	// 3. use carid and look into cars table and get car info
	// 4. print them formated
	public void viewTransaction(Connection conn) {
		String query = "SELECT * FROM cardealership.payments;";
		PreparedStatement stmt = null;
		String header = String.format("%3s|%10s|%10s|%10s|%10s|%10s|%10s|%10s|%10s|%10s", "#", "User ID", "Username", "Car ID", "Car Make", "Car Model", "Year Made", "Color", "Amount", "Date");
		System.out.println(header);
		int i = 1;
		boolean hasValue = false;
		int carId = -1;
		int userId = -1;
		try {
			stmt = conn.prepareStatement(query);
			ResultSet res = stmt.executeQuery();
			while (res.next()) {
				hasValue = true;
				// get line number
				System.out.print(String.format("%3d", i++));
				// get user info
				userId = res.getInt("userid");
				String userQuery = "SELECT * FROM cardealership.users "
						+ "WHERE id = ?;";
				try {
					PreparedStatement userStmt = conn.prepareStatement(userQuery);
					userStmt.setInt(1, userId);
					ResultSet userRes = userStmt.executeQuery();
					while (userRes.next()) {
						System.out.print(String.format("|%10s|%10s", userRes.getInt("id"), userRes.getString("userid")));
					}
				} catch(SQLException e) {
					error("View Payments: fail to retrieve user info from db");
				}
				// get car info
				carId = res.getInt("carid");
				String carQuery = "SELECT * FROM cardealership.cars "
						+ "WHERE id = ?;";
				try {
					PreparedStatement carStmt = conn.prepareStatement(carQuery);
					carStmt.setInt(1, carId);
					ResultSet carRes = carStmt.executeQuery();
					while (carRes.next()) {
						System.out.print(String.format("|%10s|%10s|%10s|%10s|%10s", carRes.getInt("id"), carRes.getString("make"), carRes.getString("model"), carRes.getString("yearmade"), carRes.getString("color")));
					}
				} catch (SQLException e) {
					error("View Payments: fail to retrieve car info from db");
				}
				
				// get payment amount and date
				System.out.println(String.format("|%10s|%10s", res.getDouble("amount"), res.getString("date")));
			}
			if (!hasValue) {
				System.out.println("No Payment has been made at this point");
			}
		} catch (SQLException e) {
			error("View Payments: fail to retrieve payments from db due to SQL Error");
			e.printStackTrace();
		}
		
		
	}
}
