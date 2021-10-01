CREATE TABLE Car_Models (
	Model varchar(50) NOT NULL,
	Year int NOT NULL,
	CONSTRAINT COMP_KEY PRIMARY KEY (Model, Year)
);

CREATE TABLE Car_Parts (
	ID SERIAL PRIMARY KEY,
	PartName varchar(50) NOT NULL,
	Type varchar(50) NOT NULL,
	Model varchar(50) NOT NULL	
);

CREATE TABLE Features (
	FeatureName varchar(50) NOT NULL,
	Model varchar(50) NOT NULL
);


CREATE VIEW Inventory_Summary as
SELECT model AS Car_model, type AS Part_Type, count(type) AS Quantity 
FROM car_parts 
GROUP BY type, model 
ORDER BY model ;