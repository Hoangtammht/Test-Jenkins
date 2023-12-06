CREATE DATABASE EParking;
USE EParking;
CREATE TABLE Method (
    methodID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    methodName varchar(255) NOT NULL
);

CREATE TABLE Date (
    dateOfWeekID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    dateOfWeek varchar(255) NOT NULL
);

CREATE TABLE SpecialDate (
    specialDateID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    startSpecialDate TIMESTAMP,
    endSpecialDate TIMESTAMP
);

CREATE TABLE Parking (
    parkingID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userID int NOT NULL,
    methodID int NOT NULL,
    parkingName nvarchar(256),
    description nvarchar(3000),
    images nvarchar(3000),
    address nvarchar(1000),
	latitude decimal(10,6),
    longitude decimal(10,6),
    pricing int,
    park int,
    status int
);

CREATE TABLE ParkingDate (
    dateOfWeekID int NOT NULL,
    parkingID int NOT NULL,
    offerDate double
);

CREATE TABLE ParkingSpecialDate (
    specialDateID int NOT NULL,
    parkingID int NOT NULL,
    offerSpecialDate double
);

CREATE TABLE Role (
    roleID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    roleName varchar(50) NOT NULL
);

CREATE TABLE UserRole (
    roleID int NOT NULL,
    userID int NOT NULL
);

CREATE TABLE User (
	userID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    phoneNumber varchar(10) NOT NULL unique,
    password varchar(200),
    fullName nvarchar(256),
    email varchar(100),
    identifyCard varchar(20),
    balance DOUBLE
);

CREATE TABLE CarDetail (
    carID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userID int NOT NULL,
    licensePlate varchar(20)
);

CREATE TABLE PaymentDetail (
    paymentID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    reserveID int NOT NULL unique,
    paymentName nvarchar(100),
    paymentDateTime timestamp
);

CREATE TABLE ReservationStatus (
    statusID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    statusName nvarchar(100)
);

CREATE TABLE Reservation (
    reserveID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userID int NOT NULL,
    parkingID int NOT NULL,
	statusID int NOT NULL,
    carID int NOT NULL,
    startDateTime timestamp,
    endDatetime timestamp,
    totalPrice int
);

CREATE TABLE Cart(
	userID int NOT NULL,
    reserveID int NOT NULL unique
);

-- FK_Parking
ALTER TABLE Parking ADD CONSTRAINT FK_SupplierParking FOREIGN KEY (userID) REFERENCES User(userID);
ALTER TABLE Parking ADD CONSTRAINT FK_MethodParking FOREIGN KEY (methodID) REFERENCES Method(methodID);

-- FK_ParkingDate
ALTER TABLE ParkingDate ADD CONSTRAINT FK_ParkingDate FOREIGN KEY (parkingID) REFERENCES Parking(parkingID);
ALTER TABLE ParkingDate ADD CONSTRAINT FK_DateParking FOREIGN KEY (dateOfWeekID) REFERENCES Date(dateOfWeekID);

-- FK_ParkingSpecialDate
ALTER TABLE ParkingSpecialDate ADD CONSTRAINT FK_ParkingSpecialDate FOREIGN KEY (parkingID) REFERENCES Parking(parkingID);
ALTER TABLE ParkingSpecialDate ADD CONSTRAINT FK_SpecialDateParking FOREIGN KEY (specialDateID) REFERENCES SpecialDate(specialDateID);

-- FK_CarDetail
ALTER TABLE CarDetail ADD CONSTRAINT FK_CustomerCar FOREIGN KEY (userID) REFERENCES User(userID);

-- FK_Reservation
ALTER TABLE Reservation ADD CONSTRAINT FK_CustomerReservation FOREIGN KEY (userID) REFERENCES User(userID);
ALTER TABLE Reservation ADD CONSTRAINT FK_ParkingReservation FOREIGN KEY (parkingID) REFERENCES Parking(parkingID);
ALTER TABLE Reservation ADD CONSTRAINT FK_StatusReservation FOREIGN KEY (statusID) REFERENCES ReservationStatus(statusID);
ALTER TABLE Reservation ADD CONSTRAINT FK_CarReservation FOREIGN KEY (carID) REFERENCES CarDetail(carID);

-- FK_Payment
ALTER TABLE PaymentDetail ADD CONSTRAINT FK_ReservationPayment FOREIGN KEY (reserveID) REFERENCES Reservation(reserveID);

-- FK-Role
ALTER TABLE UserRole ADD CONSTRAINT FK_UserRole FOREIGN KEY (userID) REFERENCES User(userID);
ALTER TABLE UserRole ADD CONSTRAINT FK_RoleUser FOREIGN KEY (roleID) REFERENCES Role(roleID);

-- FK-Cart
ALTER TABLE Cart ADD CONSTRAINT FK_User FOREIGN KEY (userID) REFERENCES User(userID);
ALTER TABLE Cart ADD CONSTRAINT FK_ReservationID FOREIGN KEY (reserveID) REFERENCES Reservation(reserveID);

-- Trigger for cart

DELIMITER //

CREATE TRIGGER alterTableCart AFTER UPDATE
ON Reservation FOR EACH ROW
BEGIN
	DECLARE cartCount INT;

	-- Kiểm tra xem userID và reserveID mới có tồn tại trong bảng Cart hay không
	SELECT COUNT(*) INTO cartCount
	FROM Cart
	WHERE userID = NEW.userID AND reserveID = NEW.reserveID AND NEW.statusID != 1;
    IF cartCount = 0 THEN
	IF NEW.statusID = 2 OR NEW.statusID = 3 THEN
		INSERT INTO Cart (userID, reserveID)
		VALUES (NEW.userID, NEW.reserveID);
	ELSE
		DELETE FROM Cart WHERE reserveID = NEW.reserveID;
	END IF;
    END IF;
END //

DELIMITER ;

INSERT INTO User(phoneNumber, password, fullName, email, identifyCard)
VALUES ("0123456789", "$2a$10$CovsRO2OYukwwURjLe1uGOYI1/z7jpjiKswqZfOK2.egrU2HGSzEi", "Admin Parking", "admin@gmail.com", "111111111111");
INSERT INTO User(phoneNumber, password, fullName, email, identifyCard)
VALUES ("0946219139", "$2a$10$CovsRO2OYukwwURjLe1uGOYI1/z7jpjiKswqZfOK2.egrU2HGSzEi", "Hoang Tam", "hoangtammht@gmail.com", "111111111111");

INSERT INTO Role(roleName)
VALUES ("ROLE_SUPPLIER");
INSERT INTO Role(roleName)
VALUES ("ROLE_CUSTOMER");

INSERT INTO UserRole(roleID, userID)
VALUES (1, 1);
INSERT INTO UserRole(roleID, userID)
VALUES (1, 2);
INSERT INTO UserRole(roleID, userID)
VALUES (2, 2);

INSERT INTO Method(methodName)
VALUES ("Hours");
INSERT INTO Method(methodName)
VALUES ("Slot");

INSERT INTO Date (dateOfWeek) VALUES ('Monday');
INSERT INTO Date (dateOfWeek) VALUES ('Tuesday');
INSERT INTO Date (dateOfWeek) VALUES ('Wednesday');
INSERT INTO Date (dateOfWeek) VALUES ('Thursday');
INSERT INTO Date (dateOfWeek) VALUES ('Friday');
INSERT INTO Date (dateOfWeek) VALUES ('Saturday');
INSERT INTO Date (dateOfWeek) VALUES ('Sunday');

INSERT INTO CarDetail (userID, licensePlate)
VALUES (2, '60A-000000');
INSERT INTO CarDetail (userID, licensePlate)
VALUES (2, '60A-111111');
INSERT INTO CarDetail (userID, licensePlate)
VALUES (2, '60A-222222');
INSERT INTO CarDetail (userID, licensePlate)
VALUES (2, '60A-333333');	
INSERT INTO CarDetail (userID, licensePlate)
VALUES (2, '60A-444444');
INSERT INTO CarDetail (userID, licensePlate)
VALUES (2, '60A-555555');

INSERT INTO ReservationStatus (statusName) VALUES ('Present');
INSERT INTO ReservationStatus (statusName) VALUES ('Coming');
INSERT INTO ReservationStatus (statusName) VALUES ('Departed');

INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Khách sạn Sheraton Saigon', 'A convenient parking space', 'https://mrvu-fan.com/wp-content/uploads/2019/08/du-an-quat-tran-sheraton-sai-gon.png', '123 Example Street', 10.775667422143378, 106.70387789625266, 50000, 8, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Khách sạn Renaissance Riverside', 'A convenient parking space', 'https://cdn2.vietnambooking.com/wp-content/uploads/hotel_pro/hotel_346765/75192f35e6aaf258c274ce3bdb298787.jpg', '456 Example Avenue', 10.77464240228887, 106.70612635207664, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Khách sạn Hotel Grand Saigon', 'A convenient parking space', 'https://ticotravel.com.vn/wp-content/uploads/2022/03/saigon-grand-hotel-1-1.jpg', '456 Example Avenue', 10.774391924567361, 106.70532363858239, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Khách sạn Caravelle Saigon', 'A convenient parking space', 'https://saigontourist.com.vn/files/images/luu-tru/caravelle-saigon-hotel.jpg', '456 Example Avenue', 10.77699946262414, 106.70362352508788, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'Khách sạn LOTTE Saigon', 'A convenient parking space', 'https://www.lottehotel.com/content/dam/lotte-hotel/lotte/saigon/main/4361-02-2000-acc-LTHO.jpg.thumb.768.768.jpg', '456 Example Avenue', 10.778832301169551, 106.70680668275817, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Khách sạn Silverland Jolie', 'A convenient parking space', 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/238499125.jpg?k=9c030ce3aafb689ad4f1537b04e07e5a649f48bf7b93d97dce4d480c701ca0e0&o=&hp=1', '456 Example Avenue', 10.777636558242277, 106.70591899625275, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, "Nhat Ha L'Opera", 'A convenient parking space', 'https://pix10.agoda.net/hotelImages/289633/-1/84e7b9784153b4909bddfbe06500b573.jpg?ca=15&ce=1&s=768x1024', '456 Example Avenue', 10.778666284861046, 106.70455325392311, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'Vincom Center Đồng Khởi', 'A convenient parking space', 'https://vinhomecentralpark.com/wp-content/uploads/2021/06/Vincom-Dong-Khoi-Q1-1024x681.jpg', '456 Example Avenue', 10.778370032330068, 106.70185076237932, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'New World Sài Gòn', 'A convenient parking space', 'https://saigon.newworldhotels.com/wp-content/uploads/sites/18/2014/05/NWSGN-Driveway1.jpg', '456 Example Avenue', 10.77120828653991, 106.69469895207673, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Khách sạn PullMan', 'A convenient parking space', 'https://pix10.agoda.net/hotelImages/21648431/0/05101a9abe327741da996a59a697e14b.jpg?ca=17&ce=1&s=1024x768', '456 Example Avenue', 10.764545290278374, 106.69163380974705, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'Nhà khách Phương Nam', 'A convenient parking space', 'https://thienhatravel.vn/pic/New/images/hcm/nha-khach-phuong-nam.jpg', '456 Example Avenue', 10.76296629918281, 106.6863860989247, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'Nhà khách Xuân Thu', 'A convenient parking space', 'https://image-tc.galaxy.tf/wijpeg-1w03tpxgow8ozi13a1zgo9ueo/vincom-center-copy-orig_standard.jpg?crop=34%2C0%2C533%2C400', '456 Example Avenue', 10.760149062103876, 106.68875306608857, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'A IN HOTEL DEL LUNA', 'A convenient parking space', 'https://pix10.agoda.net/hotelImages/36114319/0/b87323b22ccf561ade150402e491e9cd.jpg?ce=0&s=1024x768', '456 Example Avenue', 10.765147849967287, 106.69507275392291, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Yellow House Saigon', 'A convenient parking space', 'https://media.tacdn.com/media/attractions-splice-spp-674x446/0a/78/a2/ca.jpg', '456 Example Avenue', 10.765234249772051, 106.69576280974695, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'Bitexco Tower', 'A convenient parking space', 'https://upload.wikimedia.org/wikipedia/commons/c/c4/DJI_0550-HDR-Pano_Bitexco_Financial_Tower.jpg', '456 Example Avenue', 10.771776721629644, 106.70436531657624, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'Takashimaya', 'A convenient parking space', 'https://cdn3.dhht.vn/wp-content/uploads/2023/02/takashimaya-co-nhung-thuong-hieu-nao-co-gi-an-choi-gi-bia.jpg', '456 Example Avenue', 10.773356663584561, 106.70057360974712, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'Tòa nhà Ree Tower', 'A convenient parking space', 'https://maisonoffice.vn/wp-content/uploads/2021/09/toa-nha-ree-tower-doan-van-bo.jpg', '456 Example Avenue', 10.764980069876904, 106.70245826741746, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'WeWork E. Town Central', 'A convenient parking space', 'https://5office.vn/wp-content/uploads/2021/05/wework-etown-central-doan-van-bo-quan-4-van-phong-tron-goi-5office.vn-bia.jpg', '456 Example Avenue', 10.765338171480833, 106.70297458275807, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'La Vela Saigon Hotel', 'A convenient parking space', 'https://reviewvilla.vn/wp-content/uploads/2022/04/La-Vela-Saigon-Hotel-1.jpg', '456 Example Avenue', 10.788934269152277, 106.68541715207702, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 1, 'Saigon Prince Hotel', 'A convenient parking space', 'https://pix10.agoda.net/hotelImages/109/10991/10991_16080819080045308387.jpg?ca=6&ce=1&s=1024x768', '456 Example Avenue', 10.772999604850733, 106.70401515207669, 80000, 10, 1);
INSERT INTO Parking (userID, methodID, parkingName, description, images, address, latitude, longitude, pricing, park, status)
VALUES (2, 2, 'UNIQLO Saigon Centre', 'A convenient parking space', 'https://lh5.googleusercontent.com/p/AF1QipPC-E4AKnHB_YIoL5tlhjj-9h5kQ9LPtnsFUYTb=w426-h240-k-no', '456 Example Avenue', 10.77395076501537, 106.70061297548841, 80000, 10, 1);

INSERT INTO Reservation (userID, parkingID, statusID, carID, startDateTime, endDatetime)
VALUES (2, 2, 1, 1, '2023-05-22 09:00:00', '2023-05-22 18:00:00');
INSERT INTO Reservation (userID, parkingID, statusID, carID, startDateTime, endDatetime)
VALUES (2, 2, 1, 1, '2023-05-23 10:30:00', '2023-05-23 17:30:00');
INSERT INTO Reservation (userID, parkingID, statusID, carID, startDateTime, endDatetime)
VALUES (2, 2, 2, 2, '2023-05-22 12:00:00', '2023-05-22 16:00:00');
INSERT INTO Reservation (userID, parkingID, statusID, carID, startDateTime, endDatetime)
VALUES (2, 2, 2, 2, '2023-05-21 12:00:00', '2023-05-21 16:00:00');
INSERT INTO Reservation (userID, parkingID, statusID, carID, startDateTime, endDatetime)
VALUES (2, 2, 3, 3, '2023-05-20 12:00:00', '2023-05-21 19:00:00');
INSERT INTO Reservation (userID, parkingID, statusID, carID, startDateTime, endDatetime)
VALUES (2, 2, 3, 3, '2023-05-20 12:00:00', '2023-05-21 20:00:00');
INSERT INTO Reservation (userID, parkingID, statusID, carID, startDateTime, endDatetime)
VALUES (1, 1, 1, 6, '2023-05-25 19:00:00', '2023-05-25 20:00:00');


