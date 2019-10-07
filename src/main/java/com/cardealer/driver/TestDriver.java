package com.cardealer.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cardealer.service.Menu;

public class TestDriver {
	public static void main(String[] args) {
		Menu menu = new Menu();
//		menu.testRegister();	// true/false
		menu.testLogin();		// -1=>not login, 0=>customer login, 1=>employee login
//		menu.testAddCar();		// true/false
		menu.testShowCarLot();	// void
		
		
		
		
		
		
		
		
		
		
//		try {
//			Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//			System.out.println("Connected");
//			String sql = "SELECT * FROM cardealership.users";
//			Statement stmt = conn.createStatement();
//			ResultSet res = stmt.executeQuery(sql);
//			while(res.next()) {
//				System.out.println(res.getString(1) + " " + res.getString(2));
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
