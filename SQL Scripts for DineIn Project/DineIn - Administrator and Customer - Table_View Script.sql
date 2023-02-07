--USE [master]
--GO

--DROP DATABASE IF EXISTS [DineIn];
--GO

--CREATE DATABASE [DineIn];
--GO

--==================================================================================================================================================================--

USE [DineIn]
GO

--DROP SCHEMA IF EXISTS [ADMINISTRATOR];
--GO

--CREATE SCHEMA [ADMINISTRATOR];
--GO

--==================================================================================================================================================================--

DROP TABLE [ADMINISTRATOR].[Tables];
GO

CREATE TABLE [ADMINISTRATOR].[Tables] (
	table_Id INT IDENTITY(1,1) NOT NULL,
	no_of_People_Allowed_Min INT NOT NULL,
	no_of_People_Allowed_Max INT NOT NULL,
	table_Status VARCHAR(50) NOT NULL,
	reserved_by VARCHAR(50) NULL DEFAULT NULL,
	mobileNo VARCHAR(50) NULL,
	date_entered DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--

DROP TABLE [ADMINISTRATOR].[FutureTableBookings];
GO

CREATE TABLE [ADMINISTRATOR].[FutureTableBookings] (
	table_Id INT NOT NULL,
	mobileNo VARCHAR(50) NOT NULL,
	time_slot DATETIME NOT NULL,
	date_entered DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--
DROP VIEW [Administrator].[ReserveTable];

CREATE VIEW [Administrator].[ReserveTable]
       AS
       SELECT * FROM [DineIn].[ADMINISTRATOR].[Tables]
       WHERE table_Status = 'Available'
       AND table_Id NOT IN (
       SELECT table_Id FROM [ADMINISTRATOR].[FutureTableBookings]
       WHERE DATEDIFF(mi, GETDATE(), time_slot) <= 60 
	);
GO

--==================================================================================================================================================================--

DROP TABLE [ADMINISTRATOR].[FoodItem_Categories];
GO

CREATE TABLE [ADMINISTRATOR].[FoodItem_Categories] (
	foodItemCategory_Id INT IDENTITY(1,1) NOT NULL,
	foodItemCategory_Name VARCHAR(100) NOT NULL,
	foodItemCategory_IsDeleted INT NOT NULL DEFAULT 0,
	date_entered DATETIME NOT NULL DEFAULT GETDATE(),
	date_updated DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--

DROP TABLE [DineIn].[ADMINISTRATOR].[FoodItems];
GO

CREATE TABLE [ADMINISTRATOR].[FoodItems] (
	foodItem_Id INT IDENTITY(1,1) NOT NULL,
	foodItem_Category VARCHAR(100) NOT NULL,
	foodItem_Name VARCHAR(100) NOT NULL,
	foodItem_Price NUMERIC(9,2) NOT NULL,
	foodItem_Status VARCHAR(100) NOT NULL,
	foodItem_Image TEXT NOT NULL,
	date_entered DATETIME NOT NULL DEFAULT GETDATE(),
	date_updated DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--

DROP TABLE [DineIn].[ADMINISTRATOR].[StaffDetails_Categories];
GO

CREATE TABLE [ADMINISTRATOR].[StaffDetails_Categories] (
	staffDetailsCategory_Id INT IDENTITY(1,1) NOT NULL,
	staffDetailsCategory_Name VARCHAR(100) NOT NULL,
	staffDetailsCategory_IsDeleted INT NOT NULL DEFAULT 0
);

--==================================================================================================================================================================--

DROP TABLE [ADMINISTRATOR].[Staff_Details];
GO

CREATE TABLE [ADMINISTRATOR].[Staff_Details] (
	staff_Id INT IDENTITY(1,1) NOT NULL,
	staff_Category VARCHAR(50) NOT NULL,
	staff_Name VARCHAR(50) NOT NULL,
	staff_MobileNo VARCHAR(10) NOT NULL,
	staff_Password VARCHAR(50) NOT NULL,
	staff_IsDeleted INT NOT NULL DEFAULT 0,
	date_entered DATETIME NOT NULL DEFAULT GETDATE(),
	date_updated DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--
DROP VIEW [ADMINISTRATOR].[Order_Payment_Details]
CREATE VIEW [ADMINISTRATOR].[Order_Payment_Details]
AS
	SELECT DISTINCT OD.mobileNo, OD.tableId, OD.order_Id, CAST(OD.date_updated AS VARCHAR(12)) 
	+ ' | ' + convert(varchar, cast(OD.date_updated as time), 100) AS date_updated, OD.order_Status, T.no_of_dishes, T.total_Amount,
	CASE WHEN OD.chef_Name IS NULL THEN '' ELSE OD.chef_Name END AS chef_Name, 
	CASE WHEN PD.payment_Status IS NULL THEN '' ELSE PD.payment_Status END AS payment_Status, 
	CASE WHEN PD.paid_Via IS NULL THEN '' ELSE PD.paid_Via END AS paid_Via, OD.date_updated AS date_updated_distinct 
	FROM [CUSTOMER].[OrderDetails] OD LEFT JOIN [CUSTOMER].[PaymentDetails] PD 
	ON OD.order_Id = PD.order_Id
	AND OD.mobileNo = PD.mobileNo
	AND OD.tableId = PD.tableId
	LEFT JOIN (SELECT OD.mobileNo, OD.tableId, OD.order_Id, 
		CAST(SUM(OD.price_Amount + OD.tax_Amount) AS NUMERIC(9,2)) AS total_Amount,
		COUNT(foodItem_Id) AS no_of_dishes
		FROM [CUSTOMER].[OrderDetails] OD
		GROUP BY OD.mobileNo, OD.tableId, OD.order_Id) AS T
	ON OD.order_Id = T.order_Id
	AND OD.mobileNo = T.mobileNo
	AND OD.tableId = T.tableId;
GO

--==================================================================================================================================================================--

--DROP SCHEMA IF EXISTS [CUSTOMER];
--GO

--CREATE SCHEMA [CUSTOMER];
--GO

--==================================================================================================================================================================--

DROP TABLE [DineIn].[CUSTOMER].[AddToCart];
GO

CREATE TABLE [CUSTOMER].[AddToCart] (
	mobileNo VARCHAR(50) NOT NULL,
	tableId INT NOT NULL,
	foodItem_Id INT NOT NULL,
	quantity INT NOT NULL,
	date_entered DATETIME NOT NULL DEFAULT GETDATE(),
	date_updated DATETIME NOT NULL DEFAULT GETDATE()
);

--==================================================================================================================================================================--

DROP SEQUENCE [CUSTOMER].[sequence_OrderDetails];
GO

CREATE SEQUENCE [CUSTOMER].[sequence_OrderDetails]
START WITH 1
INCREMENT BY 1
MINVALUE 1
MAXVALUE 100000
CYCLE;
GO

--==================================================================================================================================================================--

DROP TABLE [DineIn].[CUSTOMER].[OrderDetails];
GO

CREATE TABLE [CUSTOMER].[OrderDetails] (
	order_Id INT NOT NULL,
	mobileNo VARCHAR(50) NOT NULL,
	tableId INT NOT NULL,
	foodItem_Id INT NOT NULL,
	quantity INT NOT NULL,
	price_Amount NUMERIC(9,2) NOT NULL,
	tax_Amount AS (price_Amount * 5 / 100),
	order_Status VARCHAR(100) NOT NULL DEFAULT 'Pending',
	chef_Name VARCHAR(200) NULL,
	date_entered DATETIME NOT NULL DEFAULT GETDATE(),
	date_updated DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--

DROP TABLE [DineIn].[CUSTOMER].[PaymentDetails];
GO

CREATE TABLE [CUSTOMER].[PaymentDetails] (
	payment_Id INT IDENTITY(1,1) NOT NULL,
	order_Id INT NOT NULL,
	tableId INT NOT NULL,
	mobileNo VARCHAR(50) NOT NULL,
	noOfDishes VARCHAR(20) NOT NULL,
	total_Amount NUMERIC(9,2) NOT NULL,
	payment_Status VARCHAR(100) NOT NULL DEFAULT 'Pending',
	paid_Via VARCHAR(100) NOT NULL DEFAULT '',
	cardholder_Name VARCHAR(200) NOT NULL DEFAULT '',
	card_No VARCHAR(50) NOT NULL DEFAULT '',
	exp_Month VARCHAR(50) NOT NULL DEFAULT '',
	exp_Year VARCHAR(50) NOT NULL DEFAULT '',
	date_entered DATETIME NOT NULL DEFAULT GETDATE(),
	date_updated DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--

DROP TABLE [DineIn].[CUSTOMER].[FeedbackDetails];
GO

CREATE TABLE [DineIn].[CUSTOMER].[FeedbackDetails] (
	feedback_id INT IDENTITY(1,1) NOT NULL,
	order_Id INT NOT NULL,
	mobileNo VARCHAR(50) NOT NULL,
	quality_rating FLOAT NULL,
	quantity_rating FLOAT NULL,
	service_rating FLOAT NULL,
	price_rating FLOAT NULL,
	ambience_rating FLOAT NULL,
	date_entered DATETIME NOT NULL DEFAULT GETDATE(),
	date_updated DATETIME NOT NULL DEFAULT GETDATE()
);
GO

--==================================================================================================================================================================--
USE [DineIn]
SELECT * FROM [DineIn].[ADMINISTRATOR].[Tables];
SELECT * FROM [DineIn].[ADMINISTRATOR].[FutureTableBookings];
SELECT * FROM [DineIn].[ADMINISTRATOR].[FoodItem_Categories];
SELECT * FROM [DineIn].[ADMINISTRATOR].[FoodItems];
SELECT * FROM [DineIn].[ADMINISTRATOR].[StaffDetails_Categories];
SELECT * FROM [DineIn].[ADMINISTRATOR].[Staff_Details];
SELECT * FROM [DineIn].[CUSTOMER].[AddToCart];
SELECT * FROM [DineIn].[CUSTOMER].[OrderDetails];
SELECT * FROM [DineIn].[CUSTOMER].[PaymentDetails];
SELECT * FROM [DineIn].[CUSTOMER].[FeedbackDetails];

SELECT (NEXT VALUE FOR [CUSTOMER].[sequence_OrderDetails]);

--==================================================================================================================================================================--

INSERT INTO [ADMINISTRATOR].[StaffDetails_Categories] (staffDetailsCategory_Name) VALUES ('Admin'), ('Manager'), ('Chef');
INSERT INTO [DineIn].[ADMINISTRATOR].[Staff_Details] (staff_Category, staff_Name, staff_MobileNo, staff_Password) VALUES('Admin', 'Pallavi', '8286828682', 'P@123');
UPDATE [DineIn].[ADMINISTRATOR].[Tables] SET reserved_by = 'Customer', mobileNo = '8286736150' WHERE table_Id = '1';

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET mobileNo = '8020802080', date_entered = '2021-07-08 10:15:20.643', date_updated = '2021-07-08 11:15:20.643'
 WHERE order_Id = '1' and mobileNo = '8286736149' and tableId = '3';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET mobileNo = '8020802080', date_entered = '2021-07-08 10:15:20.643', date_updated = '2021-07-08 11:15:20.643'
 WHERE order_Id = '1' and mobileNo = '8286736149' and tableId = '3';
UPDATE [DineIn].[CUSTOMER].[FeedbackDetails] SET mobileNo = '8020802080', date_entered = '2021-07-08 10:15:20.643', date_updated = '2021-07-08 11:15:20.643'
 WHERE order_Id = '1' and mobileNo = '8286736149' ;

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET mobileNo = '8286736150', date_entered = '2021-07-08 12:15:20.643', date_updated = '2021-07-08 12:15:20.643'
 WHERE order_Id = '2' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET mobileNo = '8286736150', date_entered = '2021-07-08 12:15:20.643', date_updated = '2021-07-08 12:15:20.643'
 WHERE order_Id = '2' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[FeedbackDetails] SET mobileNo = '8286736150', date_entered = '2021-07-08 12:15:20.643', date_updated = '2021-07-08 12:15:20.643'
 WHERE order_Id = '2' and mobileNo = '8286736149' ;

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET mobileNo = '8655234586' WHERE order_Id = '3' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET mobileNo = '8655234586' WHERE order_Id = '3' and mobileNo = '8286736149';

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET mobileNo = '8655234586' WHERE order_Id = '4' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET mobileNo = '8655234586' WHERE order_Id = '4' and mobileNo = '8286736149';

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET  date_updated = '2021-07-13 13:15:20.643' WHERE order_Id IN (3,4);
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET  date_updated = '2021-07-13 13:15:20.643' WHERE order_Id IN (3,4);
UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET date_entered = '2021-07-13 13:10:20.643', date_updated = '2021-07-13 13:10:20.643' WHERE order_Id = '3';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET date_entered = '2021-07-13 13:10:20.643', date_updated = '2021-07-13 13:10:20.643' WHERE order_Id = '3';

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET mobileNo = '9820914526', date_entered = '2021-07-13 14:15:20.643', date_updated = '2021-07-13 14:15:20.643'
 WHERE order_Id = '5' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET mobileNo = '9820914526', date_entered = '2021-07-13 14:15:20.643', date_updated = '2021-07-13 14:15:20.643'
 WHERE order_Id = '5' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[FeedbackDetails] SET mobileNo = '9820914526', date_entered = '2021-07-13 14:15:20.643', date_updated = '2021-07-13 14:15:20.643'
 WHERE order_Id = '5' and mobileNo = '8286736149' ;

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET mobileNo = '9820914526', date_entered = '2021-07-22 10:30:20.643', date_updated = '2021-07-22 10:31:20.643'
 WHERE order_Id = '6' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET mobileNo = '9820914526', date_entered = '2021-07-22 10:30:20.643', date_updated = '2021-07-22 10:31:20.643'
 WHERE order_Id = '6' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[FeedbackDetails] SET mobileNo = '9820914526', date_entered = '2021-07-22 10:30:20.643', date_updated = '2021-07-22 10:31:20.643'
 WHERE order_Id = '6' and mobileNo = '8286736149' ;

UPDATE [DineIn].[CUSTOMER].[OrderDetails] SET mobileNo = '8286736150', date_entered = '2021-07-22 15:10:20.643', date_updated = '2021-07-22 15:10:20.643'
 WHERE order_Id = '7' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[PaymentDetails] SET mobileNo = '8286736150', date_entered = '2021-07-22 15:10:20.643', date_updated = '2021-07-22 15:10:20.643'
 WHERE order_Id = '7' and mobileNo = '8286736149';
UPDATE [DineIn].[CUSTOMER].[FeedbackDetails] SET mobileNo = '8286736150', date_entered = '2021-07-22 15:10:20.643', date_updated = '2021-07-22 15:10:20.643'
 WHERE order_Id = '7' and mobileNo = '8286736149' ;






