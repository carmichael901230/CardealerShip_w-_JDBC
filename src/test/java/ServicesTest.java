import static com.cardealer.util.LoggerUtil.error;
import static com.cardealer.util.LoggerUtil.info;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import org.junit.*;

import com.cardealer.pojo.Cars;
import com.cardealer.pojo.Offers;
import com.cardealer.pojo.Users;
import com.cardealer.service.Services;

public class ServicesTest {
	private Services serve;
	private Connection conn;
	private Scanner cin;
	Properties prop;
	
	private static String URL;
	private static String USERNAME;
	private static String PASSWORD;
	private static final String PROPERTIES_FILE = "src/main/resources/database.properties";
	
	@Before
	public void setUp() throws Exception {
		serve = new Services();
		prop = new Properties();
		
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
		
		cin = new Scanner(System.in);
	}
	
	@Test
	public void createUserTest() {
		Users expected = new Users("username", "password", 0, "full name");
		Users actual = serve.createUserObj(conn, cin);
		assertEquals(expected.getUserid(), actual.getUserid());
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getPassword(), actual.getPassword());
	}
	
	@Test
	public void createCarTest() {
		Cars expected = new Cars("make", "model", "color", "2019", 20000);
		Cars actual = serve.createNewCar(cin);
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getMake(), actual.getMake());
		assertEquals(expected.getModel(), actual.getModel());
		assertEquals(expected.getColor(), actual.getColor());
		assertEquals(expected.getYear(), actual.getYear());
		assertEquals(expected.getPrice(), actual.getPrice(), 0.001);
	}
	
	@Test
	public void userDisplayTest() {
		Users test = new Users(1,"username", "password", 0, "full name");
		String expected = String.format("%10s|%20s", "username", "full name");
		assertEquals(expected, test.display());
	}
	
	@Test
	public void offerDisplayTest() {
		Cars test = new Cars("make", "model", "color", "2019", 20000.0);
		String expected = String.format("%10s|%10s|%10s|%10s|%10.2f", "make", "model", "color", "2019", 20000.0);
		assertEquals(expected, test.display());	
	}
}
