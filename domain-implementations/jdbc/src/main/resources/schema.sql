-- PHPMYADMIN SQL DUMP
-- VERSION 5.0.1
-- HTTPS://WWW.PHPMYADMIN.NET/
--
-- HOST: 127.0.0.1
-- GENERATION TIME: MAR 24, 2020 AT 06:55 PM
-- SERVER VERSION: 10.4.11-MARIADB
-- PHP VERSION: 7.4.3
DROP DATABASE IF EXISTS Shoe_Shop;

CREATE DATABASE Shoe_Shop;

USE Shoe_Shop;

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET TIME_ZONE = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES UTF8MB4 */;

CREATE TABLE INVENTORY (
                           ID_INVENTORY INT AUTO_INCREMENT PRIMARY KEY,
                           CAPACITY INT
);

CREATE TABLE ADDRESS (
                         ID_ADDRESS INT AUTO_INCREMENT PRIMARY KEY,
                         LOCATION VARCHAR(255)
);

CREATE TABLE MODEL (
                       ID_MODEL INT AUTO_INCREMENT PRIMARY KEY,
                       NAME VARCHAR(255),
                       BRAND VARCHAR(255)
);

CREATE TABLE SHOE_STORE (
                            ID_STORE INT AUTO_INCREMENT PRIMARY KEY,
                            NAME VARCHAR(255),
                            OWNER VARCHAR(255),
                            LOCATION VARCHAR(255),
                            ID_INVENTORY INT,
                            FOREIGN KEY (ID_INVENTORY) REFERENCES INVENTORY(ID_INVENTORY)
);

CREATE TABLE SUPPLIER (
                          ID_SUPPLIER INT AUTO_INCREMENT PRIMARY KEY,
                          NAME VARCHAR(255),
                          PHONE VARCHAR(15)
);

CREATE TABLE CLIENT (
                        ID_CLIENT INT AUTO_INCREMENT PRIMARY KEY,
                        DNI VARCHAR(20),
                        NAME VARCHAR(255),
                        PHONE VARCHAR(15),
                        ID_ADDRESS INT,
                        FOREIGN KEY (ID_ADDRESS) REFERENCES ADDRESS(ID_ADDRESS)
);

CREATE TABLE SHOE (
                      ID_SHOE INT PRIMARY KEY,
                      ID_MODEL INT,
                      ID_INVENTORY INT,
                      PRICE DECIMAL(10, 2),
                      COLOR VARCHAR(50),
                      SIZE VARCHAR(10),
                      FOREIGN KEY (ID_MODEL) REFERENCES MODEL(ID_MODEL),
                      FOREIGN KEY (ID_INVENTORY) REFERENCES INVENTORY(ID_INVENTORY)
);

CREATE TABLE LINK_STORE_SUPPLIER (
                                     ID_STORE INT,
                                     ID_SUPPLIER INT,
                                     PRIMARY KEY (ID_STORE, ID_SUPPLIER),
                                     FOREIGN KEY (ID_STORE) REFERENCES SHOE_STORE(ID_STORE),
                                     FOREIGN KEY (ID_SUPPLIER) REFERENCES SUPPLIER(ID_SUPPLIER)
);

CREATE TABLE LINK_CLIENT_STORE (
                                   ID_CLIENT INT,
                                   ID_STORE INT,
                                   PRIMARY KEY (ID_CLIENT, ID_STORE),
                                   FOREIGN KEY (ID_CLIENT) REFERENCES CLIENT(ID_CLIENT),
                                   FOREIGN KEY (ID_STORE) REFERENCES SHOE_STORE(ID_STORE)
);




/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;