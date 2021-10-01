import java.io.*;
import java.sql.*;
import java.util.*;

//credosoft
public class App {

	private static String url = "jdbc:postgresql://localhost:5432/postgres";
	private static String user = "sm564";
	private static String password = "sean123";
	private static Scanner scanner;
	private static FileWriter writer;
	private static ArrayList<String> recorded_car_models = new ArrayList<>(); 

	private static ArrayList<String> features = new ArrayList<>();

	/**
	 * Used to establish a connection the DB. My app calls this for every single insert which is inefficient - batches of inserts would speed up the process massively for larger inputs and DBs.
	 * @return
	 */
	public static Connection connect() {
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to the PostgreSQL server successfully");
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return conn;
	}



/**
 * Start point
 */
	public static void populateDatabase() {
		populateCarModels();
		populateCarParts();
		populateFeatures();
	};

	/**
	 * Reads in the models from cars.txt, selects 10 random models and writes them to the DB. Updates cars.txt to remove the models already present in DB.
	 */
	public static void populateCarModels() {
		ArrayList<String> models = new ArrayList<>();
		try {
			File file = new File(System.getProperty("user.dir")+ File.separator +"src"+File.separator+"cars.txt"); //open a scanner on the cars.txt data file
			scanner = new Scanner(file);
			if(file.length() == 0) {
				System.out.println("Cars.txt is empty - exiting pogram");
				System.exit(0);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find cars.txt in the project /src folder");
			e.printStackTrace();
		}

		scanner.useDelimiter("\n");
		while(scanner.hasNext()) {
			String car = scanner.next(); 
			if(car.toCharArray()[car.length()-1] == '\n' || car.toCharArray()[car.length()-1] == '\r' ) 
				car = car.substring(0, car.length()-1);
			models.add(car);
		} scanner.close();


		for(int i=0;i< 10;i++) { //add 10 cars to db
			int index = (int) (Math.random() * models.size()); //get a random valid index for adding car models to the table, then get a random year between 1900 and 2021
			int year = 1900 + ((int) (Math.random()* 122));
			Car car = new Car(models.get(index), year);

			insertCarModel(car); // add car and year as a row to car_models table
			recorded_car_models.add(models.get(index)); //maintain the list of cars for populating the parts table later
			models.remove(index); //remove model so we don't add duplicates
		}

		//write back to cars.txt, this will shrink by 10 cars each run of the application as we add 10 unique cars to the database
		try {
			writer = new FileWriter(new File(System.getProperty("user.dir")+ File.separator +"src"+File.separator+"cars.txt"));
			String output = "";
			for(String car : models) {
				output+= car+"\n";
			}
			if(output.length()>1) output = output.substring(0, output.length()-1); //remove trailing whitespace
			writer.write(output);
			writer.close();
		} 
		catch (IOException e) {e.printStackTrace();}
		System.out.println("Finished inserting car_model values.");
	}

	/**
	 * Reads in data from parts.txt, assigns 11 random parts to 8 of the car models we just added to the DB, 12 to a single car model and none to another.
	 */
	private static void populateCarParts() {
		ArrayList<String> parts = new ArrayList<>();
		ArrayList<String> models = new ArrayList<>();
		models.addAll(recorded_car_models); //make  a local copy of the arrayList
		Map<String, String> partType = getPartTypeMap();
		models.remove(models.size()-1); //remove the last entry, this way we will have some car in the db with no associated parts as desired in the spec


		try {
			File file = new File(System.getProperty("user.dir") + File.separator +"src"+ File.separator + "parts.txt");
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find parts.txt in the project src folder");
			e.printStackTrace();
		}
		scanner.useDelimiter("\n");
		while(scanner.hasNext()) {
			String part = scanner.next();
			if(part.toCharArray()[part.length()-1] == '\n' || part.toCharArray()[part.length()-1] == '\r' ) 
				part = part.substring(0, part.length()-1);
			parts.add(part);
		} scanner.close();
		


		for(int i =0; i< models.size()-1;i++) { //associate 11 parts with 8 of the cars, 12 with 1 of the cars and 0 with 1 of the cars 

			for(int j =0; j < 11;j++) { //change to loop 11 times with random access index
				int partIndex = (int) (Math.random() * parts.size()); //pick a part at random to associate with the car
				String partName = parts.get(partIndex);
				insertCarPart(new Part(partName, partType.get(partName), models.get(i)));
			}
		}

		for(int j =0; j<12;j++) { //choose 12 random parts for the last car
			int partIndex = (int) (Math.random() * parts.size());
			String partName = parts.get(partIndex);
			insertCarPart(new Part(partName, partType.get(partName), models.get(models.size()-1)));
		} System.out.println("Finished inserting car_parts values.");



	}
	/**
	 * Reads in features from text file, randomly associates 5 to each car we inserted into the DB
	 */
	public static void populateFeatures() {
		
		
		try {
			File file = new File(System.getProperty("user.dir") + File.separator +"src"+ File.separator + "features.txt");
			scanner = new Scanner(file);
		}catch (FileNotFoundException e) {
			System.out.println("Couldn't find features.txt in the project src folder");
			e.printStackTrace();
		}
		scanner.useDelimiter("\n");
		while(scanner.hasNext()) {
			String feature = scanner.next();
			if(feature.toCharArray()[feature.length()-1] == '\n' || feature.toCharArray()[feature.length()-1] == '\r' ) 
				feature = feature.substring(0, feature.length()-1);
			features.add(feature);
		} scanner.close();
		
		//associate 5 random features to each car model that was added to the db during the app's current life-cycle
		// we only want to add a given feature for a given car once, so set up local copy
		for(int i =0; i <10 ;i++) {
			ArrayList<String> localFeatures = new ArrayList<>();
			localFeatures.addAll(features);
			
			String model = recorded_car_models.get(i);
			for(int j =0; j< 5; j++) {
				int featureIndex = (int) (Math.random() * localFeatures.size()); //pick a feature at random from the list we read in
				Feature feature = new Feature(localFeatures.get(featureIndex), model);
				insertCarFeature(feature); //associate with given car model, remove feature from temporary list and repeat
				localFeatures.remove(featureIndex); //means we don't allocate the same feature to a car model twice
			}
		}System.out.println("Finished inserting Features values");
	}

	/**
	 * 
	 * @return Predefined mapping for parts to their part type
	 */
	public static Map<String, String> getPartTypeMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Door", "Safety"); map.put("Radio", "Entertainment");
		map.put("Bonnet", "Structural"); map.put("Wheel", "Control");
		map.put("Lock", "Security"); map.put("Keys", "Security");
		map.put("Tyre", "Structural"); map.put("Axel", "Structural");
		map.put("Suspension", "Structural"); map.put("Tank", "Fuel");
		map.put("Exhaust", "Structural"); map.put("Headlights", "Lighting");
		map.put("Gearstick", "Control"); map.put("Brakes", "Control");
		map.put("Airbag", "Safety"); map.put("Wing-mirror", "Safety");
		map.put("Bluetooth", "Entertainment"); map.put("Speedometer", "Control");
		map.put("Camera", "Safety"); map.put("Rear-sensor", "Safety");
		map.put("Seat", "Structural"); map.put("Seatbelt", "Safety");
		return map;
	}

	/**
	 * Writes to the car_models table in our DB
	 * @param car
	 * 
	 */
	public static long insertCarModel(Car car) {
		String SQL = "INSERT INTO car_models VALUES(?, ?)";
		long id = 0;

		try(Connection conn = connect();
				PreparedStatement pstmnt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)){

			pstmnt.setString(1,  car.getModel());
			pstmnt.setInt(2, car.getYear());

			int affectedRows = pstmnt.executeUpdate();

			if(affectedRows > 0) {
				System.out.println(String.format("Inserted (\'%s\', \'%s\') into Car_Models",car.getModel(), car.getYear()));
				try(ResultSet rs = pstmnt.getGeneratedKeys()){
					if(rs.next())
						id = rs.getLong(1);
				} catch(SQLException e) {
					//System.out.println(e.getMessage());
				}

			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return id;
	}

	/**
	 * Writes to the car_parts table in our DB
	 * @param part
	 *
	 */
	public static long insertCarPart(Part part) {
		String SQL = "INSERT INTO car_parts(partname, type, model) VALUES ( ?, ?, ?)";
		long id = 0;

		try(Connection conn = connect();
				PreparedStatement pstmnt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)){
			pstmnt.setString(1, part.getName()); pstmnt.setString(2, part.getType()); pstmnt.setString(3, part.getModel());

			int affectedRows = pstmnt.executeUpdate();

			if(affectedRows > 0) {
				System.out.println(String.format("Inserted (\'%s\', \'%s\', \'%s\') into Car_Parts", part.getName(), part.getType(), part.getModel()));
				try(ResultSet rs = pstmnt.getGeneratedKeys()){
					if(rs.next())
						id = rs.getLong(1);
				} catch(SQLException e) {
					//System.out.println(e.getMessage());
				}
			} else {
				System.out.println(String.format("-- No rows affected by %s, %s, %s)--",part.getName(), part.getType(), part.getModel()));
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return id;
	}

	/**
	 * Writes to the features table in our DB
	 * @param feature
	 * @return
	 */
	public static long insertCarFeature(Feature feature){
		String SQL = "INSERT INTO Features VALUES ( ?, ?)";
		long id = 0;
		
		try(Connection conn = connect();
				PreparedStatement pstmnt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)){
			pstmnt.setString(1, feature.getFeaturename()); pstmnt.setString(2, feature.getCarModel());

			int affectedRows = pstmnt.executeUpdate();

			if(affectedRows > 0) {
				System.out.println(String.format("Inserted (\'%s\', \'%s\') into Features", feature.getFeaturename(),feature.getCarModel()));
				try(ResultSet rs = pstmnt.getGeneratedKeys()){
					if(rs.next())
						id = rs.getLong(1);
				} catch(SQLException e) {
					//System.out.println(e.getMessage());
				}
			} else {
				System.out.println(String.format("-- No rows affected by %s, %s)--",feature.getFeaturename(),feature.getCarModel()));
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return id;
	}

	public static void main(String[] args) {
		if(args.length <2 || args.length > 3) {
			System.out.println("Please provide arguments in this order: "
					+ "URL to the database (e.g. jdbc:postgresql://localhost:5432/postgres), "
					+ " USER name, PASSWORD, if your database requires one. Check README.txt for more information"); return;
		} 
		url = args[0];
		user = args[1];
		if(args.length == 3)
			password = args[2]; 

		populateDatabase();

	}
}
