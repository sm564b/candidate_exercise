Please ensure you have executed the accompanying script.sql to set-up the database before executing the java app. Please also note that the assumed RDBMS is PostgreSQL.
Each execution of App.java will add 10 unique cars, 100 car parts and 50 features to the desired database. 
How to Use:
1. Start-up the DB, execute script.sql
2. Ensure cars.txt, parts.txt and features.txt are all present in the project src folder. Additionally postgresql-42.2.24.jar must be included as referenced library through the project properties (add [external] jar)
3. Run app.java with runtime arguments
	a. URL to PostgreSQL database ("jdbc:postgresql://<localhost>:<port>/<db-name>)
	b. USER who is accessing DB (usr)
	c. PASSWORD for the above user into your DB (if you require one)
4. You may execute app.java, adding data to the DB, up to 10 times before the source of unique car models runs out (as outlined below)
5. For any further use, please refill cars.txt (explained below) and DROP the three tables (car_models, car_parts and features) from your db before restarting from step 1 again.


The car, part and feature data written to the DB is located in the eponymous text files included (e.g. cars.txt).
The app randomly selects 10 of the 100 cars in cars.txt before writing them to the DB, it  then removes these 10 cars from the text file so that they will not be repeatedly added to the database.
Because of this, cars.txt will become empty after 10 executions. Should you wish to start from the beginning again, it is easiest to drop all three tables from the database, execute the sql script again and refill cars.txt.
To refill cars.txt, copy the contents of read-only file "all-cars.txt" and paste them into cars.txt. It is important that each car model is on its own line.

Basic class files are also included to represent rows in the car_models, car_parts and feature tables.