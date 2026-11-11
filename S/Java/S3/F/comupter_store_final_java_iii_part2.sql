-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: comupter_store_final_java_iii
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `commune` varchar(80) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `default_address` bit(1) NOT NULL,
  `district` varchar(80) NOT NULL,
  `full_name` varchar(120) NOT NULL,
  `phone` varchar(30) NOT NULL,
  `province` varchar(80) NOT NULL,
  `street` varchar(250) NOT NULL,
  `user_id` bigint NOT NULL,
  `country` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id`),
  CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brands`
--

DROP TABLE IF EXISTS `brands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brands` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `logo_url` varchar(500) DEFAULT NULL,
  `name` varchar(80) NOT NULL,
  `slug` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKoce3937d2f4mpfqrycbr0l93m` (`name`),
  UNIQUE KEY `UKpnhnc9urm6fro7oseu9vka70q` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brands`
--

LOCK TABLES `brands` WRITE;
/*!40000 ALTER TABLE `brands` DISABLE KEYS */;
INSERT INTO `brands` VALUES (1,_binary '','/uploads/logo/asus.png','ASUS','asus'),(2,_binary '','/uploads/logo/msi.png','MSI','msi'),(3,_binary '','/uploads/logo/gigabyte.png','Gigabyte','gigabyte'),(4,_binary '','/uploads/logo/lenovo.png','Lenovo','lenovo'),(5,_binary '','/uploads/logo/dell.png','Dell','dell'),(6,_binary '','/uploads/logo/hp.png','HP','hp'),(7,_binary '','/uploads/logo/acer.png','Acer','acer'),(8,_binary '','/uploads/logo/intel.png','Intel','intel'),(9,_binary '','/uploads/logo/amd.png','AMD','amd'),(10,_binary '','/uploads/logo/nvidia.png','NVIDIA','nvidia'),(11,_binary '','/uploads/logo/samsung.webp','Samsung','samsung'),(12,_binary '','/uploads/logo/kingston.png','Kingston','kingston'),(13,_binary '','/uploads/logo/corsair.png','Corsair','corsair'),(14,_binary '','/uploads/logo/logitech.png','Logitech','logitech'),(15,_binary '','/uploads/logo/razer.png','Razer','razer'),(16,_binary '',NULL,'TestBrandXYZ','testbrandxyz'),(18,_binary '','/uploads/logo/TPLINK_Logo_2.png','TP-Link','tp-link'),(19,_binary '','/uploads/logo/LG_symbol.svg.webp','LG','lg'),(20,_binary '','/uploads/avatars/sellers/Apple Logo.png','Apple','apple'),(21,_binary '','/uploads/logo/dji.webp','DJI','dji'),(22,_binary '','/uploads/logo/GoPro.png','GoPro','gopro'),(23,_binary '','/uploads/logo/Sony-Logo.wine.png','Sony','sony'),(24,_binary '','/uploads/logo/Microsoft.png','Microsoft','microsoft'),(25,_binary '',NULL,'Nintendo','nintendo'),(26,_binary '','/uploads/logo/anker.webp','Anker','anker'),(27,_binary '','/uploads/logo/APC.jpg','APC','apc'),(28,_binary '',NULL,'Arduino','arduino'),(29,_binary '',NULL,'Synology','synology'),(30,_binary '','/uploads/logo/Seagate.png','Seagate','seagate'),(31,_binary '','/uploads/logo/WD.jpg','WD','wd'),(32,_binary '','/uploads/logo/CISCO.jpg','Cisco','cisco'),(33,_binary '','/uploads/logo/Elgato_logo.svg.webp','Elgato','elgato'),(34,_binary '','/uploads/logo/Shure.png','Shure','shure'),(35,_binary '','/uploads/logo/Rode-Logo.jpg','RODE','rode'),(36,_binary '','/uploads/logo/steam.png','Steam','steam'),(37,_binary '',NULL,'Generic','generic');
/*!40000 ALTER TABLE `brands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `quantity` int NOT NULL,
  `selected` bit(1) NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  KEY `FK709eickf3kc0dujx3ub9i7btf` (`user_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK709eickf3kc0dujx3ub9i7btf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `icon` varchar(50) DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `name` varchar(80) NOT NULL,
  `slug` varchar(80) NOT NULL,
  `sort_order` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`),
  UNIQUE KEY `UKoul14ho7bctbefv8jywp5v3i2` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,_binary '','?','/uploads/products/asus-rog-gaming-pc-starter.jpg','Computers','computers',1),(2,_binary '','?','/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif','Gaming PC','gaming-pc',2),(3,_binary '','?','/uploads/products/lenovo-legion-pro-7i.webp','Laptop','laptop',3),(4,_binary '','?','/uploads/products/amd-ryzen-7-9800x3d.png','CPU','cpu',4),(5,_binary '','?️','/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg','GPU','gpu',5),(6,_binary '','⬛','/uploads/products/asus-tuf-b850-plus-wifi.jpg','Motherboard','motherboard',6),(7,_binary '','?','/uploads/products/corsair-vengeance-ddr5-32gb.jpg','RAM','ram',7),(8,_binary '','?','/uploads/products/samsung-990-pro-2tb.jpg','SSD','ssd',8),(9,_binary '','?️','/uploads/products/Seagate IronWolf Pro.png','HDD','hdd',9),(10,_binary '','⚡','/uploads/products/corsair-rm850x-psu.avif','Power Supply','power-supply',10),(11,_binary '','?','/uploads/products/nzxt-h9-flow.webp','PC Case','pc-case',11),(12,_binary '','?️','/uploads/products/asus-rog-strix-27-monitor.webp','Monitor','monitor',12),(13,_binary '','⌨️','/uploads/products/razer-blackwidow-v4-pro.jpg','Keyboard','keyboard',13),(14,_binary '','?️','/uploads/products/logitech-g-pro-x-superlight-2.webp','Mouse','mouse',14),(15,_binary '','?','/uploads/products/hyperx-cloud-iii.webp','Headset','headset',15),(16,_binary '','❄️','/uploads/products/noctua-nh-d15-chromax.jpg','Cooling','cooling',16),(17,_binary '','?','/uploads/products/usb c.jpg','Accessories','accessories',17),(18,_binary '','?','/uploads/products/tp-link-archer-ax55.jpg','Networking','networking',18),(19,_binary '','?',NULL,'VR Headsets','vr-headsets',19),(20,_binary '','?','/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg','Wi-Fi Routers','wifi-routers',19),(21,_binary '','?','/uploads/products/Cisco Catalyst 9300 Series.webp','Network Switches','network-switches',20),(22,_binary '','?️','/uploads/products/Synology DiskStation DS923+.jpg','NAS & Storage','nas-storage',21),(23,_binary '','?️','/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif','Gaming Consoles','gaming-consoles',22),(24,_binary '','?','/uploads/products/Xbox Series X Wireless Controller (Black).jpg','Controllers','controllers',23),(25,_binary '','?','/uploads/products/steam-gift-card-50usd.jpg','Game Cards','game-cards',24),(26,_binary '','?','/uploads/products/camaera.jpg','Cameras','cameras',25),(27,_binary '','?','/uploads/products/GoPro HERO13 Black.jpg','Action Cams','action-cams',26),(28,_binary '','?','/uploads/products/DJI Mini 4 Pro.jpg','Drones','drones',27),(29,_binary '','?','/uploads/products/Elgato 4K X.jpg','Capture Cards','capture-cards',28),(30,_binary '','?','/uploads/products/indoor-security-cameras-023-preview-662bee34c6a12.avif','Security Cameras','security-cameras',29),(31,_binary '','?','/uploads/products/Anker Prime 100W GaN Charger.webp','Chargers','chargers',30),(32,_binary '','?','/uploads/products/APC Back-UPS Pro BR1500MS2.jpg','UPS','ups',31),(33,_binary '','?','/uploads/products/zyron-powaflex-10000mah-power-bank-built-in-cables-front.webp','Power Banks','power-banks',32),(34,_binary '','?','/uploads/products/usb c.jpg','Cables','cables',33),(35,_binary '','?','/uploads/products/Arduino UNO R4 WiFi.webp','Microcontrollers','microcontrollers',34),(36,_binary '','?️','/uploads/products/Shure SM7B.jpg','Audio','audio',35),(37,_binary '','?','/uploads/products/airpod pro 3.webp','Earbuds','earbuds',36),(38,_binary '','?','/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp','Smart TV','smart-tv',37),(39,_binary '','?️','/uploads/products/Dell PowerEdge R760.jpg','Servers','servers',38),(40,_binary '','?','/uploads/products/Lenovo ThinkStation PX.jpg','Workstations','workstations',39),(41,_binary '','?','/uploads/products/HPE Aruba Networking CX 6300.png','Enterprise Networking','enterprise-networking',40),(42,_binary '','x',NULL,'12345','12345',42);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commissions`
--

DROP TABLE IF EXISTS `commissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `commission_amount` decimal(12,2) NOT NULL,
  `commission_percent` decimal(5,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `sale_amount` decimal(12,2) NOT NULL,
  `seller_earnings` decimal(12,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `held_at` datetime(6) DEFAULT NULL,
  `payout_status` enum('DISPUTED','HELD','IN_PROTECTION','REFUNDED','RELEASED') NOT NULL,
  `protection_started_at` datetime(6) DEFAULT NULL,
  `refunded_at` datetime(6) DEFAULT NULL,
  `released_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8lrww534wk9n7try26jied988` (`order_id`),
  KEY `FKixfus24caeelwupt043s0ckkh` (`seller_id`),
  CONSTRAINT `FK8lrww534wk9n7try26jied988` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKixfus24caeelwupt043s0ckkh` FOREIGN KEY (`seller_id`) REFERENCES `sellers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commissions`
--

LOCK TABLES `commissions` WRITE;
/*!40000 ALTER TABLE `commissions` DISABLE KEYS */;
INSERT INTO `commissions` VALUES (1,45.54,3.00,'2026-10-09 08:34:32.229010',1518.00,1472.46,1,1,NULL,'RELEASED','2026-10-09 10:52:45.341487',NULL,'2026-10-09 10:52:58.268476'),(2,26.97,3.00,'2026-10-09 09:22:46.186885',899.00,872.03,2,2,'2026-10-09 09:22:46.186885','HELD',NULL,NULL,NULL),(3,83.88,3.00,'2026-10-19 12:46:05.746803',2796.00,2712.12,3,1,'2026-10-19 12:46:05.745795','RELEASED','2026-10-19 12:55:27.109248',NULL,'2026-10-19 12:56:31.236658'),(4,11.97,3.00,'2026-10-19 12:46:05.756052',399.00,387.03,4,2,'2026-10-19 12:46:05.756052','REFUNDED',NULL,'2026-10-19 12:51:03.798612',NULL);
/*!40000 ALTER TABLE `commissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `link` varchar(300) DEFAULT NULL,
  `message` varchar(500) NOT NULL,
  `read_flag` bit(1) NOT NULL,
  `title` varchar(150) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'2026-10-05 12:21:35.295311','/products','Discover computers and components from trusted Cambodian sellers.',_binary '\0','Welcome to ComputerHub!','SYSTEM',2),(2,'2026-10-05 12:21:35.296814','/deals','MSI RTX 5070 is 12% OFF — limited stock remaining!',_binary '\0','Flash Deal Alert','PROMO',2),(3,'2026-10-05 12:21:35.297818','/admin','ComputerHub marketplace has been seeded with sample data.',_binary '\0','Marketplace Ready','SYSTEM',1),(4,'2026-10-05 12:57:58.590126','/account','Your seller account \"TechZone Cambodia\" has been banned by the marketplace admin.',_binary '','Store Suspended','SELLER',3),(5,'2026-10-05 12:57:58.701483','/seller-dashboard','Your seller account \"TechZone Cambodia\" has been unbanned. You can sell again.',_binary '','Store Restored','SELLER',3),(6,'2026-10-09 08:34:32.231420','/orders/CH17915348722081','Your order CH17915348722081 has been placed successfully.',_binary '\0','Order Confirmed','ORDER',9),(7,'2026-10-09 09:22:46.189931','/orders/CH17915377661802','Order CH17915377661802: Payment successful. MME is holding your funds until delivery and buyer protection complete.',_binary '\0','Order Confirmed','ORDER',9),(8,'2026-10-09 09:22:46.190941','/seller-dashboard/orders','New order CH17915377661802 — funds are held by MME until delivery.',_binary '\0','New Order','ORDER',4),(9,'2026-10-09 10:06:49.379242','/orders/CH17915348722081','Admin confirmed order CH17915348722081. Your payment is held by MME until delivery and buyer protection finish.',_binary '\0','Payment Held by MME','ORDER',9),(10,'2026-10-09 10:06:49.396084','/seller-dashboard/orders','Order CH17915348722081 — payment is held. Please confirm you have stock, or decline if out of stock.',_binary '\0','Confirm Stock','ORDER',3),(11,'2026-10-09 10:18:46.050820','/orders/CH17915348722081','Seller confirmed stock for order CH17915348722081 and is preparing your package.',_binary '\0','Seller Confirmed','ORDER',9),(12,'2026-10-09 10:23:39.521728','/orders/CH17915348722081','Order CH17915348722081 has been shipped · Tracking: TRK1791541419520',_binary '\0','Order Update','ORDER',9),(13,'2026-10-09 10:23:59.903756','/orders/CH17915348722081','Order CH17915348722081 is out for delivery.',_binary '\0','Order Update','ORDER',9),(14,'2026-10-09 10:26:29.250147','/orders/CH17915348722081','Order CH17915348722081 is out for delivery.',_binary '\0','Order Update','ORDER',9),(15,'2026-10-09 10:26:44.338503','/orders/CH17915348722081','Order CH17915348722081 is out for delivery.',_binary '\0','Order Update','ORDER',9),(16,'2026-10-09 10:27:46.557524','/orders/CH17915348722081','Order CH17915348722081 is out for delivery.',_binary '\0','Order Update','ORDER',9),(17,'2026-10-09 10:28:34.998192','/orders/CH17915348722081','Order CH17915348722081 is out for delivery.',_binary '\0','Order Update','ORDER',9),(18,'2026-10-09 10:52:45.345062','/orders/CH17915348722081','Order CH17915348722081 delivered. Buyer protection is open for 3 day(s) — you can report missing, damaged, or incorrect items.',_binary '\0','Order Update','ORDER',9),(19,'2026-10-09 10:52:58.268476','/seller-dashboard/earnings','MME released $1472.46 for order CH17915348722081 (commission 3.00% deducted).',_binary '\0','Payout Released','PAYOUT',3),(20,'2026-10-09 10:52:58.270495','/orders/CH17915348722081','Order CH17915348722081 is complete. Thank you for shopping on MME.',_binary '\0','Order Complete','ORDER',9),(21,'2026-10-19 12:19:05.985109','/account','Your seller account \"DigiTech Store\" has been banned by the marketplace admin.',_binary '\0','Store Suspended','SELLER',5),(22,'2026-10-19 12:19:07.162971','/seller-dashboard','Your seller account \"DigiTech Store\" has been unbanned. You can sell again.',_binary '\0','Store Restored','SELLER',5),(23,'2026-10-19 12:46:05.749875','/orders/CH17924139657401','Order CH17924139657401: Payment successful. Waiting for MME admin to confirm and hold your funds.',_binary '\0','Order Confirmed','ORDER',2),(24,'2026-10-19 12:46:05.758570','/orders/CH17924139657522','Order CH17924139657522: Payment successful. Waiting for MME admin to confirm and hold your funds.',_binary '\0','Order Confirmed','ORDER',2),(25,'2026-10-19 12:50:47.198876','/orders/CH17924139657401','Admin confirmed order CH17924139657401. Your payment is held by MME until delivery and buyer protection finish.',_binary '\0','Payment Held by MME','ORDER',2),(26,'2026-10-19 12:50:47.199926','/seller-dashboard/orders','Order CH17924139657401 — payment is held. Please confirm you have stock, or decline if out of stock.',_binary '\0','Confirm Stock','ORDER',3),(27,'2026-10-19 12:51:03.799895','/orders/CH17924139657522','Order CH17924139657522 was refunded. Held funds were returned.',_binary '\0','Refund Issued','ORDER',2),(28,'2026-10-19 12:52:56.679255','/orders/CH17924139657401','Seller confirmed stock for order CH17924139657401 and is preparing your package.',_binary '\0','Seller Confirmed','ORDER',2),(29,'2026-10-19 12:53:20.276645','/orders/CH17924139657401','Order CH17924139657401 has been shipped · Tracking: TRK1792414400276',_binary '\0','Order Update','ORDER',2),(30,'2026-10-19 12:53:31.176518','/orders/CH17924139657401','Order CH17924139657401 is out for delivery.',_binary '\0','Order Update','ORDER',2),(31,'2026-10-19 12:55:27.110749','/orders/CH17924139657401','Order CH17924139657401 delivered. Buyer protection is open for 3 day(s) — you can report missing, damaged, or incorrect items.',_binary '\0','Order Update','ORDER',2),(32,'2026-10-19 12:56:31.236658','/seller-dashboard/earnings','MME released $2712.12 for order CH17924139657401 (commission 3.00% deducted).',_binary '\0','Payout Released','PAYOUT',3),(33,'2026-10-19 12:56:31.238986','/orders/CH17924139657401','Order CH17924139657401 is complete. Thank you for shopping on MME.',_binary '\0','Order Complete','ORDER',2);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `line_total` decimal(12,2) NOT NULL,
  `product_image` varchar(500) DEFAULT NULL,
  `product_name` varchar(250) NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1518.00,'/uploads/products/DJI Mini 4 Pro.jpg','DJI Mini 4 Pro Drone',2,759.00,1,38),(2,899.00,'/uploads/products/asus-rog-gaming-pc-starter.jpg','ASUS ROG Gaming PC Starter Build',1,899.00,2,19),(3,2796.00,'/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg','MSI GeForce RTX 5070 Gaming X 12GB',4,699.00,3,2),(4,399.00,'/uploads/products/intel-core-ultra-7-265k.jpg','Intel Core Ultra 7 265K',1,399.00,4,4);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `delivered_at` datetime(6) DEFAULT NULL,
  `delivery_type` enum('EXPRESS','STANDARD') DEFAULT NULL,
  `discount` decimal(12,2) DEFAULT NULL,
  `estimated_delivery` datetime(6) DEFAULT NULL,
  `order_number` varchar(40) NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_method` enum('CASH_ON_DELIVERY','CREDIT_CARD','QR_PAYMENT','STRIPE','VISA') DEFAULT NULL,
  `placed_at` datetime(6) DEFAULT NULL,
  `platform_fee` decimal(12,2) DEFAULT NULL,
  `recipient_name` varchar(120) DEFAULT NULL,
  `recipient_phone` varchar(30) DEFAULT NULL,
  `shipped_at` datetime(6) DEFAULT NULL,
  `shipping_address` varchar(400) DEFAULT NULL,
  `shipping_fee` decimal(12,2) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','DELIVERED','DISPUTED','FUNDS_HELD','OUT_FOR_DELIVERY','PAYMENT_CONFIRMED','PENDING_PAYMENT','REFUNDED','SELLER_PROCESSING','SHIPPED') NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `total` decimal(12,2) NOT NULL,
  `tracking_number` varchar(80) DEFAULT NULL,
  `seller_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `dispute_note` varchar(500) DEFAULT NULL,
  `dispute_opened_at` datetime(6) DEFAULT NULL,
  `dispute_reason` enum('DAMAGED','INCORRECT','MISSING','OTHER') DEFAULT NULL,
  `payout_released_at` datetime(6) DEFAULT NULL,
  `protection_ends_at` datetime(6) DEFAULT NULL,
  `admin_held_at` datetime(6) DEFAULT NULL,
  `cancel_reason` varchar(40) DEFAULT NULL,
  `seller_confirmed_at` datetime(6) DEFAULT NULL,
  `out_for_delivery_at` datetime(6) DEFAULT NULL,
  `shipping_country` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnthkiu7pgmnqnu86i2jyoe2v7` (`order_number`),
  KEY `FKbdhiwrh9a9c3gmlfvx8f0cvyh` (`seller_id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKbdhiwrh9a9c3gmlfvx8f0cvyh` FOREIGN KEY (`seller_id`) REFERENCES `sellers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,NULL,'2026-10-09 10:52:45.341487','STANDARD',0.00,'2026-10-12 08:34:32.209211','CH17915348722081','2026-10-09 08:34:32.209211','CREDIT_CARD','2026-10-09 08:34:32.210212',45.54,'Sothea Lim','012444555','2026-10-09 10:23:39.520713','247, Sangkat Boeng Salang, Phnom Penh, aas, Phnom Penh',2.00,'COMPLETED',1518.00,1520.00,'TRK1791541419520',1,9,NULL,NULL,NULL,'2026-10-09 10:52:58.268476','2026-10-12 10:52:45.341487','2026-10-09 10:06:49.372093',NULL,'2026-10-09 10:18:46.041726',NULL,'Cambodia'),(2,NULL,NULL,'STANDARD',0.00,'2026-10-12 09:22:46.181532','CH17915377661802','2026-10-09 09:22:46.181532','VISA','2026-10-09 09:22:46.182538',26.97,'Sothea Lim','012444555',NULL,'247, Sangkat Boeng Salang, Phnom Penh, aa, Phnom Penh',2.00,'PAYMENT_CONFIRMED',899.00,901.00,NULL,2,9,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cambodia'),(3,NULL,'2026-10-19 12:55:27.109248','STANDARD',0.00,'2026-10-22 12:46:05.741619','CH17924139657401','2026-10-19 12:46:05.741619','CREDIT_CARD','2026-10-19 12:46:05.741619',83.88,'Sokha Meas','012345678','2026-10-19 12:53:20.276645','247, Sangkat Boeng Salang, Phnom Penh, Phnom Penh, Phnom Penh, Cambodia',2.00,'COMPLETED',2796.00,2798.00,'TRK1792414400276',1,2,NULL,NULL,NULL,'2026-10-19 12:56:31.236658','2026-10-22 12:55:27.109248','2026-10-19 12:50:47.196302',NULL,'2026-10-19 12:52:56.677744','2026-10-19 12:53:31.175508','Cambodia'),(4,'2026-10-19 12:51:03.798612',NULL,'STANDARD',0.00,'2026-10-22 12:46:05.752447','CH17924139657522','2026-10-19 12:46:05.752447','CREDIT_CARD','2026-10-19 12:46:05.752447',11.97,'Sokha Meas','012345678',NULL,'247, Sangkat Boeng Salang, Phnom Penh, Phnom Penh, Phnom Penh, Cambodia',2.00,'REFUNDED',399.00,401.00,NULL,2,2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cambodia');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `color_options` varchar(100) DEFAULT NULL,
  `condition_type` enum('NEW','REFURBISHED','USED') DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `discount_percent` int DEFAULT NULL,
  `featured` bit(1) NOT NULL,
  `flash_deal` bit(1) NOT NULL,
  `flash_deal_ends_at` datetime(6) DEFAULT NULL,
  `gallery_urls` varchar(1000) DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `name` varchar(250) NOT NULL,
  `original_price` decimal(12,2) DEFAULT NULL,
  `price` decimal(12,2) NOT NULL,
  `ram_options` varchar(100) DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `review_count` int DEFAULT NULL,
  `sku` varchar(80) DEFAULT NULL,
  `slug` varchar(280) NOT NULL,
  `sold_count` int DEFAULT NULL,
  `specifications` text,
  `stock` int NOT NULL,
  `storage_options` varchar(100) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `warranty` varchar(80) DEFAULT NULL,
  `brand_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKostq1ec3toafnjok09y9l7dox` (`slug`),
  KEY `FKa3a4mpsfdf4d2y6r8ra3sc8mv` (`brand_id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  KEY `FKepbha8uixgrmnejm27n6e1kkd` (`seller_id`),
  CONSTRAINT `FKa3a4mpsfdf4d2y6r8ra3sc8mv` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`),
  CONSTRAINT `FKepbha8uixgrmnejm27n6e1kkd` FOREIGN KEY (`seller_id`) REFERENCES `sellers` (`id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,_binary '','Black,Silver','NEW','2026-10-05 12:21:35.259292','Complete gaming PC with Intel Core Ultra 7, RTX 5070 12GB, 32GB DDR5, 1TB NVMe SSD.',12,_binary '',_binary '','2026-10-06 06:21:35.259292','/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif','/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif','ASUS ROG Strix Gaming PC RTX 5070',1699.00,1499.00,'16GB,32GB,64GB',4.9,235,'SKU-ASUS-ROG-STR','asus-rog-strix-gaming-pc-rtx-5070',0,'CPU: Intel Core Ultra 7\nGPU: NVIDIA RTX 5070 12GB\nRAM: 32GB DDR5\nStorage: 1TB NVMe SSD\nMotherboard: ASUS ROG B850\nPSU: 850W 80+ Gold\nCase: ASUS ROG Strix\nWarranty: 2 Years',45,'512GB,1TB,2TB','2026-10-19 12:42:12.181121','2 Years',1,2,1),(2,_binary '','Black','NEW','2026-10-05 12:21:35.262298','High-performance MSI RTX 5070 graphics card with dual-fan cooling and RGB.',12,_binary '',_binary '','2026-10-06 06:21:35.262298','/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg','/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg','MSI GeForce RTX 5070 Gaming X 12GB',799.00,699.00,NULL,4.8,412,'SKU-MSI-GEFORCE-','msi-geforce-rtx-5070-gaming-x-12gb',4,'GPU: NVIDIA GeForce RTX 5070\nVRAM: 12GB GDDR7\nBoost Clock: 2.6 GHz\nInterface: PCIe 5.0\nOutputs: 3x DP, 1x HDMI\nWarranty: 3 Years',76,NULL,'2026-10-19 12:46:05.769095','3 Years',2,5,1),(3,_binary '','Black','NEW','2026-10-05 12:21:35.264295','Flagship AMD gaming CPU with 3D V-Cache technology for unmatched gaming performance.',9,_binary '',_binary '','2026-10-06 06:21:35.264295','/uploads/products/amd-ryzen-7-9800x3d.png','/uploads/products/amd-ryzen-7-9800x3d.png','AMD Ryzen 7 9800X3D',529.00,479.00,NULL,4.9,580,'SKU-AMD-RYZEN-7-','amd-ryzen-7-9800x3d',0,'Cores: 8\nThreads: 16\nBase Clock: 4.7 GHz\nBoost: 5.2 GHz\nCache: 96MB\nSocket: AM5\nTDP: 120W\nWarranty: 3 Years',120,NULL,'2026-10-19 12:42:12.181121','3 Years',9,4,1),(4,_binary '','Black','NEW','2026-10-05 12:21:35.266296','Latest Intel Core Ultra 7 processor for content creation and gaming.',11,_binary '',_binary '\0',NULL,'/uploads/products/intel-core-ultra-7-265k.jpg','/uploads/products/intel-core-ultra-7-265k.jpg','Intel Core Ultra 7 265K',449.00,399.00,NULL,4.7,198,'SKU-INTEL-CORE-U','intel-core-ultra-7-265k',1,'Cores: 20 (8P+12E)\nThreads: 20\nBoost: 5.5 GHz\nCache: 30MB\nSocket: LGA 1851\nTDP: 125W\nWarranty: 3 Years',89,NULL,'2026-10-19 12:46:05.769095','3 Years',8,4,2),(5,_binary '','Black','NEW','2026-10-05 12:21:35.267296','High-speed DDR5 memory kit optimized for AMD and Intel platforms.',20,_binary '',_binary '','2026-10-06 06:21:35.267296','/uploads/products/corsair-vengeance-ddr5-32gb.jpg','/uploads/products/corsair-vengeance-ddr5-32gb.jpg','Corsair Vengeance DDR5 32GB (2x16GB) 6000MHz',149.00,119.00,'8GB,16GB,32GB,64GB',4.8,890,'SKU-CORSAIR-VENG','corsair-vengeance-ddr5-32gb-2x16gb-6000mhz',0,'Capacity: 32GB (2x16GB)\nSpeed: 6000MHz\nType: DDR5\nLatency: CL30\nVoltage: 1.35V\nWarranty: Lifetime',200,NULL,'2026-10-19 12:42:12.181121','Lifetime',13,7,2),(6,_binary '',NULL,'NEW','2026-10-05 12:21:35.269295','Blazing-fast PCIe 4.0 NVMe SSD for gaming and professional workloads.',15,_binary '\0',_binary '','2026-10-06 06:21:35.269295','/uploads/products/samsung-990-pro-2tb.jpg','/uploads/products/samsung-990-pro-2tb.jpg','Samsung 990 Pro 2TB NVMe SSD',199.00,169.00,NULL,4.9,1200,'SKU-SAMSUNG-990-','samsung-990-pro-2tb-nvme-ssd',0,'Capacity: 2TB\nInterface: PCIe 4.0 x4 NVMe\nRead: 7450 MB/s\nWrite: 6900 MB/s\nForm Factor: M.2 2280\nWarranty: 5 Years',150,'512GB,1TB,2TB,4TB','2026-10-19 12:42:12.181121','5 Years',11,8,1),(7,_binary '','Black','NEW','2026-10-05 12:21:35.271296','Competitive gaming monitor with Fast IPS panel, 1ms response, and G-Sync Compatible.',12,_binary '',_binary '','2026-10-06 06:21:35.271296','/uploads/products/asus-rog-strix-27-monitor.webp','/uploads/products/asus-rog-strix-27-monitor.webp','ASUS ROG Strix 27\" 1440p 180Hz Monitor',399.00,349.00,NULL,4.7,340,'SKU-ASUS-ROG-STR','asus-rog-strix-27-1440p-180hz-monitor',0,'Size: 27 inch\nResolution: 2560x1440\nRefresh Rate: 180Hz\nPanel: Fast IPS\nResponse: 1ms\nHDR: HDR400\nWarranty: 3 Years',60,NULL,'2026-10-19 12:42:12.181121','3 Years',1,12,3),(8,_binary '','Black,Silver','NEW','2026-10-05 12:21:35.273296','Flagship gaming laptop with RTX 5080, Intel Core Ultra 9, and 240Hz Mini-LED display.',12,_binary '',_binary '\0',NULL,'/uploads/products/lenovo-legion-pro-7i.webp','/uploads/products/lenovo-legion-pro-7i.webp','Lenovo Legion Pro 7i Gaming Laptop',2499.00,2199.00,'16GB,32GB,64GB',4.8,156,'SKU-LENOVO-LEGIO','lenovo-legion-pro-7i-gaming-laptop',0,'CPU: Intel Core Ultra 9\nGPU: NVIDIA RTX 5080 16GB\nRAM: 32GB DDR5\nStorage: 1TB SSD\nDisplay: 16\" 2560x1600 240Hz Mini-LED\nWarranty: 1 Year',25,'512GB,1TB,2TB','2026-10-19 12:42:12.181121','1 Year',4,3,3),(9,_binary '','Black,White','NEW','2026-10-05 12:21:35.273296','Ultra-lightweight wireless gaming mouse used by pro esports athletes.',12,_binary '',_binary '\0',NULL,'/uploads/products/logitech-g-pro-x-superlight-2.webp','/uploads/products/logitech-g-pro-x-superlight-2.webp','Logitech G Pro X Superlight 2 Mouse',169.00,149.00,NULL,4.9,2100,'SKU-LOGITECH-G-P','logitech-g-pro-x-superlight-2-mouse',0,'Weight: 60g\nSensor: HERO 2\nDPI: 32000\nBattery: 95 hours\nConnectivity: Lightspeed Wireless\nWarranty: 2 Years',100,NULL,'2026-10-19 12:42:12.181121','2 Years',14,14,2),(10,_binary '','Black,White','NEW','2026-10-05 12:21:35.275805','Premium mechanical gaming keyboard with Razer Orange switches and command dial.',11,_binary '\0',_binary '\0',NULL,'/uploads/products/razer-blackwidow-v4-pro.jpg','/uploads/products/razer-blackwidow-v4-pro.jpg','Razer BlackWidow V4 Pro Keyboard',259.00,229.00,NULL,4.6,780,'SKU-RAZER-BLACKW','razer-blackwidow-v4-pro-keyboard',0,'Switches: Razer Orange Tactile\nLayout: Full-size\nConnectivity: Wired USB-C\nRGB: Per-key Chroma\nWrist Rest: Magnetic\nWarranty: 2 Years',70,NULL,'2026-10-19 12:42:12.181121','2 Years',15,13,2),(11,_binary '','Black','NEW','2026-10-05 12:21:35.276803','AMD AM5 ATX motherboard with WiFi 7, DDR5 support, and robust VRM.',11,_binary '',_binary '\0',NULL,'/uploads/products/asus-tuf-b850-plus-wifi.jpg','/uploads/products/asus-tuf-b850-plus-wifi.jpg','ASUS TUF Gaming B850-Plus WiFi Motherboard',259.00,229.00,NULL,4.7,210,'SKU-ASUS-TUF-GAM','asus-tuf-gaming-b850-plus-wifi-motherboard',0,'Socket: AM5\nChipset: B850\nForm Factor: ATX\nMemory: 4x DDR5\nPCIe: 5.0 x16\nWiFi: WiFi 7\nWarranty: 3 Years',55,NULL,'2026-10-19 12:42:12.181121','3 Years',1,6,1),(12,_binary '','Black','NEW','2026-10-05 12:21:35.278805','Fully modular 850W power supply with quiet operation and 10-year warranty.',16,_binary '\0',_binary '\0',NULL,'/uploads/products/corsair-rm850x-psu.avif','/uploads/products/corsair-rm850x-psu.avif','Corsair RM850x 850W 80+ Gold PSU',179.00,149.00,NULL,4.8,560,'SKU-CORSAIR-RM85','corsair-rm850x-850w-80-gold-psu',0,'Wattage: 850W\nEfficiency: 80+ Gold\nModular: Fully Modular\nFan: 135mm\nRails: Single +12V\nWarranty: 10 Years',85,NULL,'2026-10-19 12:42:12.181121','10 Years',13,10,2),(13,_binary '','Black,White','NEW','2026-10-05 12:21:35.279810','Premium mid-tower case with excellent airflow and dual-chamber design.',11,_binary '',_binary '\0',NULL,'/uploads/products/nzxt-h9-flow.webp','/uploads/products/nzxt-h9-flow.webp','NZXT H9 Flow Mid-Tower Case',179.00,159.00,NULL,4.6,320,'SKU-NZXT-H9-FLOW','nzxt-h9-flow-mid-tower-case',0,'Form Factor: Mid-Tower\nMotherboard: E-ATX / ATX / mATX\nGPU Clearance: 435mm\nRadiator: Up to 360mm\nFans Included: 4x 120mm\nWarranty: 2 Years',40,NULL,'2026-10-19 12:42:12.181121','2 Years',13,11,2),(14,_binary '','Black','NEW','2026-10-05 12:21:35.281308','Reliable DDR5 single stick for upgrades and budget builds.',21,_binary '\0',_binary '','2026-10-06 06:21:35.281308','/uploads/products/kingston-fury-beast-ddr5-16gb.webp','/uploads/products/kingston-fury-beast-ddr5-16gb.webp','Kingston Fury Beast DDR5 16GB 5600MHz',75.00,59.00,'8GB,16GB,32GB,64GB',4.5,450,'SKU-KINGSTON-FUR','kingston-fury-beast-ddr5-16gb-5600mhz',0,'Capacity: 16GB\nSpeed: 5600MHz\nType: DDR5\nLatency: CL36\nWarranty: Lifetime',180,NULL,'2026-10-19 12:42:12.181121','Lifetime',12,7,3),(15,_binary '','Black,White','NEW','2026-10-05 12:21:35.282310','Comfortable gaming headset with 53mm drivers and detachable noise-cancelling mic.',16,_binary '',_binary '\0',NULL,'/uploads/products/hyperx-cloud-iii.webp','/uploads/products/hyperx-cloud-iii.webp','HyperX Cloud III Gaming Headset',119.00,99.00,NULL,4.7,980,'SKU-HYPERX-CLOUD','hyperx-cloud-iii-gaming-headset',0,'Drivers: 53mm\nConnection: 3.5mm / USB\nMic: Detachable noise-cancelling\nSurround: Spatial Audio\nWarranty: 2 Years',95,NULL,'2026-10-19 12:42:12.181121','2 Years',12,15,3),(16,_binary '','Black,White','NEW','2026-10-05 12:21:35.283310','Legendary dual-tower air cooler in stealth black finish.',15,_binary '\0',_binary '\0',NULL,'/uploads/products/noctua-nh-d15-chromax.jpg','/uploads/products/noctua-nh-d15-chromax.jpg','Noctua NH-D15 Chromax Black Cooler',129.00,109.00,NULL,4.9,670,'SKU-NOCTUA-NH-D1','noctua-nh-d15-chromax-black-cooler',0,'Type: Dual Tower Air Cooler\nFans: 2x NF-A15\nHeight: 165mm\nTDP: 220W+\nCompatibility: Intel & AMD\nWarranty: 6 Years',50,NULL,'2026-10-19 12:42:12.181121','6 Years',13,16,1),(17,_binary '','Black','NEW','2026-10-05 12:21:35.285311','High-end AORUS RTX 5070 Ti with WINDFORCE cooling and RGB fusion.',10,_binary '',_binary '','2026-10-06 06:21:35.285311','/uploads/products/gigabyte-aorus-rtx-5070-ti.png','/uploads/products/gigabyte-aorus-rtx-5070-ti.png','Gigabyte AORUS RTX 5070 Ti 16GB',949.00,849.00,NULL,4.8,145,'SKU-GIGABYTE-AOR','gigabyte-aorus-rtx-5070-ti-16gb',0,'GPU: NVIDIA GeForce RTX 5070 Ti\nVRAM: 16GB GDDR7\nCooling: WINDFORCE 3X\nOutputs: 3x DP, 1x HDMI\nWarranty: 4 Years',35,NULL,'2026-10-19 12:42:12.181121','4 Years',3,5,1),(18,_binary '','Black,Silver','NEW','2026-10-05 12:21:35.286311','Premium creator laptop with stunning OLED display and Intel Core Ultra processors.',9,_binary '',_binary '\0',NULL,'/uploads/products/dell-xps-15-oled.webp','/uploads/products/dell-xps-15-oled.webp','Dell XPS 15 OLED Creator Laptop',2099.00,1899.00,'16GB,32GB,64GB',4.6,89,'SKU-DELL-XPS-15-','dell-xps-15-oled-creator-laptop',0,'CPU: Intel Core Ultra 7\nGPU: NVIDIA RTX 4050\nRAM: 32GB LPDDR5\nStorage: 1TB SSD\nDisplay: 15.6\" OLED 3.5K\nWarranty: 1 Year',20,'512GB,1TB,2TB','2026-10-19 12:42:12.181121','1 Year',5,3,3),(19,_binary '','Black,Silver','NEW','2026-10-05 12:21:35.288311','Affordable prebuilt gaming PC ready for 1080p and 1440p gaming.',10,_binary '',_binary '','2026-10-06 06:21:35.288311','/uploads/products/asus-rog-gaming-pc-starter.jpg','/uploads/products/asus-rog-gaming-pc-starter.jpg','ASUS ROG Gaming PC Starter Build',999.00,899.00,'16GB,32GB,64GB',4.5,210,'SKU-ASUS-ROG-GAM','asus-rog-gaming-pc-starter-build',1,'CPU: AMD Ryzen 5 7600\nGPU: RTX 4060 8GB\nRAM: 16GB DDR5\nStorage: 1TB SSD\nPSU: 650W Bronze\nWarranty: 1 Year',29,'512GB,1TB,2TB','2026-10-19 12:42:12.181121','1 Year',1,1,2),(20,_binary '','Black','NEW','2026-10-05 12:21:35.289311','Fast dual-band WiFi 6 router for home and small office networks.',20,_binary '\0',_binary '\0',NULL,'/uploads/products/tp-link-archer-ax55.jpg','/uploads/products/tp-link-archer-ax55.jpg','TP-Link Archer AX55 WiFi 6 Router',99.00,79.00,NULL,4.4,560,'SKU-TP-LINK-ARCH','tp-link-archer-ax55-wifi-6-router',0,'Standard: WiFi 6 (802.11ax)\nSpeed: AX3000\nBands: Dual-band\nPorts: 4x Gigabit LAN, 1x WAN\nWarranty: 2 Years',110,NULL,'2026-10-19 12:42:12.181121','2 Years',1,18,3),(21,_binary '','Black','NEW','2026-10-09 04:25:46.997598','TP-Link AX3000 Dual Band Wi-Fi 6 Router — available on MME from verified sellers.',20,_binary '\0',_binary '\0',NULL,'/uploads/products/AX3000 Dual Band Gigabit Wi-Fi 6 Router.jpg','/uploads/products/AX3000 Dual Band Gigabit Wi-Fi 6 Router.jpg','TP-Link AX3000 Dual Band Wi-Fi 6 Router',99.00,79.00,NULL,4.6,59,NULL,'tp-link-ax3000-dual-band-wi-fi-6-router',0,'Brand: TP-Link\nCategory: Wi-Fi Routers\nWarranty: 1 Year',53,NULL,'2026-10-19 12:42:12.181121',NULL,18,20,1),(22,_binary '','Black','NEW','2026-10-09 04:25:47.002025','TP-Link BE9300 Tri-Band Wi-Fi 7 Router — available on MME from verified sellers.',14,_binary '\0',_binary '\0',NULL,'/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg','/uploads/products/BE9300 Tri-Band Wi-Fi 7 Router.jpg','TP-Link BE9300 Tri-Band Wi-Fi 7 Router',349.00,299.00,NULL,4.3999999999999995,87,NULL,'tp-link-be9300-tri-band-wi-fi-7-router',0,'Brand: TP-Link\nCategory: Wi-Fi Routers\nWarranty: 1 Year',103,NULL,'2026-10-19 12:42:12.181121',NULL,18,20,2),(23,_binary '','Black','NEW','2026-10-09 04:25:47.006405','TP-Link Deco BE63 BE10000 Mesh — available on MME from verified sellers.',10,_binary '',_binary '\0',NULL,'/uploads/products/TP-Link Deco BE63 BE10000 Wireless .jpg','/uploads/products/TP-Link Deco BE63 BE10000 Wireless .jpg','TP-Link Deco BE63 BE10000 Mesh',499.00,449.00,NULL,4.3,105,NULL,'tp-link-deco-be63-be10000-mesh',0,'Brand: TP-Link\nCategory: Wi-Fi Routers\nWarranty: 1 Year',84,NULL,'2026-10-19 12:42:12.181121',NULL,18,20,2),(24,_binary '','Black','NEW','2026-10-09 04:25:47.009449','Cisco Catalyst 9300 Series Switch — available on MME from verified sellers.',14,_binary '\0',_binary '\0',NULL,'/uploads/products/Cisco Catalyst 9300 Series.webp','/uploads/products/Cisco Catalyst 9300 Series.webp','Cisco Catalyst 9300 Series Switch',2199.00,1899.00,NULL,4.7,7,NULL,'cisco-catalyst-9300-series-switch',0,'Brand: Cisco\nCategory: Enterprise Networking\nWarranty: 1 Year',103,NULL,'2026-10-19 12:42:12.181121',NULL,32,41,3),(25,_binary '','Black','NEW','2026-10-09 04:25:47.012985','HPE Aruba Networking CX 6300 — available on MME from verified sellers.',13,_binary '\0',_binary '',NULL,'/uploads/products/HPE Aruba Networking CX 6300.png','/uploads/products/HPE Aruba Networking CX 6300.png','HPE Aruba Networking CX 6300',1499.00,1299.00,NULL,4.7,28,NULL,'hpe-aruba-networking-cx-6300',0,'Brand: Generic\nCategory: Enterprise Networking\nWarranty: 1 Year',34,NULL,'2026-10-19 12:42:12.181121',NULL,37,41,3),(26,_binary '','Black','NEW','2026-10-09 04:25:47.018598','Synology DiskStation DS923+ — available on MME from verified sellers.',8,_binary '',_binary '\0',NULL,'/uploads/products/Synology DiskStation DS923+.jpg','/uploads/products/Synology DiskStation DS923+.jpg','Synology DiskStation DS923+',649.00,599.00,NULL,4.7,5,NULL,'synology-diskstation-ds923',0,'Brand: Synology\nCategory: NAS & Storage\nWarranty: 1 Year',79,NULL,'2026-10-19 12:42:12.181121',NULL,29,22,1),(27,_binary '','Black','NEW','2026-10-09 04:25:47.021598','Synology DiskStation DS1522+ — available on MME from verified sellers.',6,_binary '\0',_binary '\0',NULL,'/uploads/products/Synology DiskStation DS1522+.jpg','/uploads/products/Synology DiskStation DS1522+.jpg','Synology DiskStation DS1522+',899.00,849.00,NULL,4.3,19,NULL,'synology-diskstation-ds1522',0,'Brand: Synology\nCategory: NAS & Storage\nWarranty: 1 Year',91,NULL,'2026-10-19 12:42:12.181121',NULL,29,22,2),(28,_binary '',NULL,'NEW','2026-10-09 04:25:47.024599','Seagate IronWolf Pro HDD — available on MME from verified sellers.',11,_binary '',_binary '\0',NULL,'/uploads/products/Seagate IronWolf Pro.png','/uploads/products/Seagate IronWolf Pro.png','Seagate IronWolf Pro HDD',279.00,249.00,NULL,4.3999999999999995,35,NULL,'seagate-ironwolf-pro-hdd',0,'Brand: Seagate\nCategory: HDD\nWarranty: 1 Year',57,'512GB,1TB,2TB,4TB','2026-10-19 12:42:12.181121',NULL,30,9,1),(29,_binary '',NULL,'NEW','2026-10-09 04:25:47.027598','WD Red Plus NAS HDD — available on MME from verified sellers.',14,_binary '\0',_binary '',NULL,'/uploads/products/WD Red Plus.png','/uploads/products/WD Red Plus.png','WD Red Plus NAS HDD',219.00,189.00,NULL,4.6,4,NULL,'wd-red-plus-nas-hdd',0,'Brand: WD\nCategory: HDD\nWarranty: 1 Year',35,'512GB,1TB,2TB,4TB','2026-10-19 12:42:12.181121',NULL,31,9,2),(30,_binary '','Black','NEW','2026-10-09 04:25:47.029599','Xbox Series X Console — available on MME from verified sellers.',9,_binary '\0',_binary '\0',NULL,'/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif','/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif','Xbox Series X Console',549.00,499.00,NULL,4.8999999999999995,99,NULL,'xbox-series-x-console',0,'Brand: Microsoft\nCategory: Gaming Consoles\nWarranty: 1 Year',86,NULL,'2026-10-19 12:42:12.181121',NULL,24,23,1),(31,_binary '','Black','NEW','2026-10-09 04:25:47.033892','Xbox Series X Wireless Controller — available on MME from verified sellers.',14,_binary '\0',_binary '\0',NULL,'/uploads/products/Xbox Series X Wireless Controller (Black).jpg','/uploads/products/Xbox Series X Wireless Controller (Black).jpg','Xbox Series X Wireless Controller',69.00,59.00,NULL,4.3999999999999995,118,NULL,'xbox-series-x-wireless-controller',0,'Brand: Microsoft\nCategory: Controllers\nWarranty: 1 Year',25,NULL,'2026-10-19 12:42:12.181121',NULL,24,24,1),(32,_binary '','Black','NEW','2026-10-09 04:25:47.035911','PlayStation DualSense Controller — available on MME from verified sellers.',13,_binary '',_binary '\0',NULL,'/uploads/products/dualsense-controller-product-thumbnail-01-en-14sep21.webp','/uploads/products/dualsense-controller-product-thumbnail-01-en-14sep21.webp','PlayStation DualSense Controller',79.00,69.00,NULL,4.3,110,NULL,'playstation-dualsense-controller',0,'Brand: Sony\nCategory: Controllers\nWarranty: 1 Year',26,NULL,'2026-10-19 12:42:12.181121',NULL,23,24,2),(33,_binary '','Black','NEW','2026-10-09 04:25:47.043013','Steam Gift Card $50 — available on MME from verified sellers.',0,_binary '\0',_binary '\0',NULL,'/uploads/products/steam-gift-card-50usd.jpg','/uploads/products/steam-gift-card-50usd.jpg','Steam Gift Card $50',50.00,50.00,NULL,4.8,31,NULL,'steam-gift-card-50',0,'Brand: Steam\nCategory: Game Cards\nWarranty: 1 Year',88,NULL,'2026-10-19 12:42:12.181121',NULL,36,25,3),(34,_binary '','Black','NEW','2026-10-09 04:25:47.046649','Xbox Gift Card $100 — available on MME from verified sellers.',0,_binary '\0',_binary '\0',NULL,'/uploads/products/xbox-gift-card-100.jpg','/uploads/products/xbox-gift-card-100.jpg','Xbox Gift Card $100',100.00,100.00,NULL,4.8,22,NULL,'xbox-gift-card-100',0,'Brand: Microsoft\nCategory: Game Cards\nWarranty: 1 Year',85,NULL,'2026-10-19 12:42:12.181121',NULL,24,25,3),(35,_binary '','Black','NEW','2026-10-09 04:25:47.049675','PlayStation Store Digital Card — available on MME from verified sellers.',0,_binary '',_binary '\0',NULL,'/uploads/products/c4353_playstation_store_200_hong_kong_m6ns_euro_053122.jpg','/uploads/products/c4353_playstation_store_200_hong_kong_m6ns_euro_053122.jpg','PlayStation Store Digital Card',50.00,50.00,NULL,4.6,105,NULL,'playstation-store-digital-card',0,'Brand: Sony\nCategory: Game Cards\nWarranty: 1 Year',61,NULL,'2026-10-19 12:42:12.181121',NULL,23,25,2),(36,_binary '','Black','NEW','2026-10-09 04:25:47.053019','Call of Duty Black Ops 7 — available on MME from verified sellers.',13,_binary '\0',_binary '\0',NULL,'/uploads/products/call of duty black ops 7.jpg','/uploads/products/call of duty black ops 7.jpg','Call of Duty Black Ops 7',79.00,69.00,NULL,4.5,99,NULL,'call-of-duty-black-ops-7',0,'Brand: Generic\nCategory: Game Cards\nWarranty: 1 Year',75,NULL,'2026-10-19 12:42:12.181121',NULL,37,25,1),(37,_binary '','Black','NEW','2026-10-09 04:25:47.055532','Battlefield 5 Digital — available on MME from verified sellers.',26,_binary '\0',_binary '\0',NULL,'/uploads/products/battlefield 5.jpg','/uploads/products/battlefield 5.jpg','Battlefield 5 Digital',39.00,29.00,NULL,4.3999999999999995,71,NULL,'battlefield-5-digital',0,'Brand: Generic\nCategory: Game Cards\nWarranty: 1 Year',68,NULL,'2026-10-19 12:42:12.181121',NULL,37,25,2),(38,_binary '','Black','NEW','2026-10-09 04:25:47.059590','DJI Mini 4 Pro Drone — available on MME from verified sellers.',5,_binary '\0',_binary '\0',NULL,'/uploads/products/DJI Mini 4 Pro.jpg','/uploads/products/DJI Mini 4 Pro.jpg','DJI Mini 4 Pro Drone',799.00,759.00,NULL,4.8,21,NULL,'dji-mini-4-pro-drone',2,'Brand: DJI\nCategory: Drones\nWarranty: 1 Year',102,NULL,'2026-10-19 12:42:12.181121',NULL,21,28,1),(39,_binary '','Black','NEW','2026-10-09 04:25:47.063655','DJI Mavic 4 Pro — available on MME from verified sellers.',11,_binary '\0',_binary '',NULL,'/uploads/products/DJI Mavic 4 Pro.jpg','/uploads/products/DJI Mavic 4 Pro.jpg','DJI Mavic 4 Pro',1799.00,1599.00,NULL,4.7,104,NULL,'dji-mavic-4-pro',0,'Brand: DJI\nCategory: Drones\nWarranty: 1 Year',82,NULL,'2026-10-19 12:42:12.181121',NULL,21,28,2),(40,_binary '','Black','NEW','2026-10-09 04:25:47.066678','DJI Avata 2 — available on MME from verified sellers.',9,_binary '\0',_binary '\0',NULL,'/uploads/products/DJI Avata 2.webp','/uploads/products/DJI Avata 2.webp','DJI Avata 2',1099.00,999.00,NULL,4.8,66,NULL,'dji-avata-2',0,'Brand: DJI\nCategory: Drones\nWarranty: 1 Year',74,NULL,'2026-10-19 12:42:12.181121',NULL,21,28,3),(41,_binary '','Black','NEW','2026-10-09 04:25:47.069708','GoPro HERO13 Black — available on MME from verified sellers.',11,_binary '',_binary '\0',NULL,'/uploads/products/GoPro HERO13 Black.jpg','/uploads/products/GoPro HERO13 Black.jpg','GoPro HERO13 Black',449.00,399.00,NULL,4.3,50,NULL,'gopro-hero13-black',0,'Brand: GoPro\nCategory: Action Cams\nWarranty: 1 Year',96,NULL,'2026-10-19 12:42:12.181121',NULL,22,27,1),(42,_binary '','Black','NEW','2026-10-09 04:25:47.072222','DJI Osmo Action 5 Pro — available on MME from verified sellers.',13,_binary '\0',_binary '\0',NULL,'/uploads/products/DJI Osmo Action 5 Pro.png','/uploads/products/DJI Osmo Action 5 Pro.png','DJI Osmo Action 5 Pro',399.00,349.00,NULL,4.5,27,NULL,'dji-osmo-action-5-pro',0,'Brand: DJI\nCategory: Action Cams\nWarranty: 1 Year',79,NULL,'2026-10-19 12:42:12.181121',NULL,21,27,2),(43,_binary '','Black','NEW','2026-10-09 04:25:47.075248','Security Camera Bundle — available on MME from verified sellers.',19,_binary '\0',_binary '\0',NULL,'/uploads/products/indoor-security-cameras-023-preview-662bee34c6a12.avif','/uploads/products/indoor-security-cameras-023-preview-662bee34c6a12.avif','Security Camera Bundle',159.00,129.00,NULL,4.8,53,NULL,'security-camera-bundle',0,'Brand: Generic\nCategory: Security Cameras\nWarranty: 1 Year',84,NULL,'2026-10-19 12:42:12.181121',NULL,37,30,3),(44,_binary '','Black','NEW','2026-10-09 04:25:47.078921','Compact Digital Camera — available on MME from verified sellers.',10,_binary '\0',_binary '\0',NULL,'/uploads/products/camaera.jpg','/uploads/products/camaera.jpg','Compact Digital Camera',499.00,449.00,NULL,4.3999999999999995,58,NULL,'compact-digital-camera',0,'Brand: Generic\nCategory: Cameras\nWarranty: 1 Year',51,NULL,'2026-10-19 12:42:12.181121',NULL,37,26,1),(45,_binary '','Black','NEW','2026-10-09 04:25:47.083096','Elgato 4K X Capture Card — available on MME from verified sellers.',12,_binary '\0',_binary '\0',NULL,'/uploads/products/Elgato 4K X.jpg','/uploads/products/Elgato 4K X.jpg','Elgato 4K X Capture Card',259.00,229.00,NULL,4.8999999999999995,7,NULL,'elgato-4k-x-capture-card',0,'Brand: Elgato\nCategory: Capture Cards\nWarranty: 1 Year',84,NULL,'2026-10-19 12:42:12.181121',NULL,33,29,2),(46,_binary '','Black','NEW','2026-10-09 04:25:47.087235','Elgato Cam Link 4K — available on MME from verified sellers.',13,_binary '\0',_binary '',NULL,'/uploads/products/Elgato Cam Link 4K.jpg','/uploads/products/Elgato Cam Link 4K.jpg','Elgato Cam Link 4K',149.00,129.00,NULL,4.5,32,NULL,'elgato-cam-link-4k',0,'Brand: Elgato\nCategory: Capture Cards\nWarranty: 1 Year',97,NULL,'2026-10-19 12:42:12.181121',NULL,33,29,3),(47,_binary '','Black','NEW','2026-10-09 04:25:47.091275','AVerMedia Live Gamer ULTRA 2.1 — available on MME from verified sellers.',13,_binary '',_binary '\0',NULL,'/uploads/products/AVerMedia Live Gamer ULTRA 2.1.jpg','/uploads/products/AVerMedia Live Gamer ULTRA 2.1.jpg','AVerMedia Live Gamer ULTRA 2.1',229.00,199.00,NULL,4.8999999999999995,35,NULL,'avermedia-live-gamer-ultra-2-1',0,'Brand: Generic\nCategory: Capture Cards\nWarranty: 1 Year',32,NULL,'2026-10-19 12:42:12.181121',NULL,37,29,1),(48,_binary '','Black','NEW','2026-10-09 04:25:47.095645','Anker Prime 100W GaN Charger — available on MME from verified sellers.',20,_binary '\0',_binary '\0',NULL,'/uploads/products/Anker Prime 100W GaN Charger.webp','/uploads/products/Anker Prime 100W GaN Charger.webp','Anker Prime 100W GaN Charger',99.00,79.00,NULL,4.8,46,NULL,'anker-prime-100w-gan-charger',0,'Brand: Anker\nCategory: Chargers\nWarranty: 1 Year',73,NULL,'2026-10-19 12:42:12.181121',NULL,26,31,1),(49,_binary '','Black','NEW','2026-10-09 04:25:47.100819','20W USB-C Fast Charger — available on MME from verified sellers.',34,_binary '\0',_binary '\0',NULL,'/uploads/products/20W-USB-C-iPhone-12-Head.jpg','/uploads/products/20W-USB-C-iPhone-12-Head.jpg','20W USB-C Fast Charger',29.00,19.00,NULL,4.8,23,NULL,'20w-usb-c-fast-charger',0,'Brand: Apple\nCategory: Chargers\nWarranty: 1 Year',57,NULL,'2026-10-19 12:42:12.181121',NULL,20,31,2),(50,_binary '','Black','NEW','2026-10-09 04:25:47.104861','APC Back-UPS Pro BR1500MS2 — available on MME from verified sellers.',14,_binary '\0',_binary '\0',NULL,'/uploads/products/APC Back-UPS Pro BR1500MS2.jpg','/uploads/products/APC Back-UPS Pro BR1500MS2.jpg','APC Back-UPS Pro BR1500MS2',289.00,249.00,NULL,4.3999999999999995,99,NULL,'apc-back-ups-pro-br1500ms2',0,'Brand: APC\nCategory: UPS\nWarranty: 1 Year',49,NULL,'2026-10-19 12:42:12.181121',NULL,27,32,1),(51,_binary '','Black','NEW','2026-10-09 04:25:47.108291','CyberPower CP1500PFCLCD UPS — available on MME from verified sellers.',12,_binary '\0',_binary '\0',NULL,'/uploads/products/CyberPower CP1500PFCLCD.jpg','/uploads/products/CyberPower CP1500PFCLCD.jpg','CyberPower CP1500PFCLCD UPS',249.00,219.00,NULL,4.5,21,NULL,'cyberpower-cp1500pfclcd-ups',0,'Brand: Generic\nCategory: UPS\nWarranty: 1 Year',94,NULL,'2026-10-19 12:42:12.181121',NULL,37,32,2),(52,_binary '','Black','NEW','2026-10-09 04:25:47.111319','Zyron 10000mAh Power Bank — available on MME from verified sellers.',26,_binary '\0',_binary '\0',NULL,'/uploads/products/zyron-powaflex-10000mah-power-bank-built-in-cables-front.webp','/uploads/products/zyron-powaflex-10000mah-power-bank-built-in-cables-front.webp','Zyron 10000mAh Power Bank',39.00,29.00,NULL,4.3999999999999995,2,NULL,'zyron-10000mah-power-bank',0,'Brand: Generic\nCategory: Power Banks\nWarranty: 1 Year',27,NULL,'2026-10-19 12:42:12.181121',NULL,37,33,3),(53,_binary '','Black','NEW','2026-10-09 04:25:47.114841','USB-C Cable — available on MME from verified sellers.',33,_binary '\0',_binary '\0',NULL,'/uploads/products/usb c.jpg','/uploads/products/usb c.jpg','USB-C Cable',18.00,12.00,NULL,4.3999999999999995,87,NULL,'usb-c-cable',0,'Brand: Generic\nCategory: Cables\nWarranty: 1 Year',41,NULL,'2026-10-19 12:42:12.181121',NULL,37,34,1),(54,_binary '','Black','NEW','2026-10-09 04:25:47.121404','USB-B Printer Cable — available on MME from verified sellers.',36,_binary '\0',_binary '',NULL,'/uploads/products/usb b.jpg','/uploads/products/usb b.jpg','USB-B Printer Cable',14.00,9.00,NULL,4.3999999999999995,32,NULL,'usb-b-printer-cable',0,'Brand: Generic\nCategory: Cables\nWarranty: 1 Year',68,NULL,'2026-10-19 12:42:12.181121',NULL,37,34,2),(55,_binary '','Black','NEW','2026-10-09 04:25:47.126046','Belkin Surge Protector — available on MME from verified sellers.',29,_binary '\0',_binary '\0',NULL,'/uploads/products/Belkin Surge Protector.jpg','/uploads/products/Belkin Surge Protector.jpg','Belkin Surge Protector',34.00,24.00,NULL,4.8999999999999995,31,NULL,'belkin-surge-protector',0,'Brand: Generic\nCategory: Cables\nWarranty: 1 Year',28,NULL,'2026-10-19 12:42:12.181121',NULL,37,34,3),(56,_binary '','Black','NEW','2026-10-09 04:25:47.131530','Arduino UNO R4 WiFi — available on MME from verified sellers.',16,_binary '\0',_binary '\0',NULL,'/uploads/products/Arduino UNO R4 WiFi.webp','/uploads/products/Arduino UNO R4 WiFi.webp','Arduino UNO R4 WiFi',32.00,27.00,NULL,4.8,63,NULL,'arduino-uno-r4-wifi',0,'Brand: Arduino\nCategory: Microcontrollers\nWarranty: 1 Year',84,NULL,'2026-10-19 12:42:12.181121',NULL,28,35,1),(57,_binary '','Black','NEW','2026-10-09 04:25:47.136549','Arduino Nano ESP32 — available on MME from verified sellers.',21,_binary '\0',_binary '\0',NULL,'/uploads/products/Arduino Nano ESP32.webp','/uploads/products/Arduino Nano ESP32.webp','Arduino Nano ESP32',24.00,19.00,NULL,4.7,29,NULL,'arduino-nano-esp32',0,'Brand: Arduino\nCategory: Microcontrollers\nWarranty: 1 Year',85,NULL,'2026-10-19 12:42:12.181121',NULL,28,35,2),(58,_binary '','Black','NEW','2026-10-09 04:25:47.143102','Shure SM7B Microphone — available on MME from verified sellers.',11,_binary '',_binary '',NULL,'/uploads/products/Shure SM7B.jpg','/uploads/products/Shure SM7B.jpg','Shure SM7B Microphone',449.00,399.00,NULL,4.3,0,NULL,'shure-sm7b-microphone',0,'Brand: Shure\nCategory: Audio\nWarranty: 1 Year',72,NULL,'2026-10-19 12:42:12.181121',NULL,34,36,1),(59,_binary '','Black','NEW','2026-10-09 04:25:47.173341','Shure MV7+ Microphone — available on MME from verified sellers.',13,_binary '\0',_binary '',NULL,'/uploads/products/Shure MV7+.jpg','/uploads/products/Shure MV7+.jpg','Shure MV7+ Microphone',319.00,279.00,NULL,4.6,48,NULL,'shure-mv7-microphone',0,'Brand: Shure\nCategory: Audio\nWarranty: 1 Year',30,NULL,'2026-10-19 12:42:12.181121',NULL,34,36,2),(60,_binary '','Black','NEW','2026-10-09 04:25:47.185997','RODE NT1 5th Generation — available on MME from verified sellers.',11,_binary '\0',_binary '\0',NULL,'/uploads/products/RODE NT1 5th Generation.png','/uploads/products/RODE NT1 5th Generation.png','RODE NT1 5th Generation',279.00,249.00,NULL,4.8999999999999995,98,NULL,'rode-nt1-5th-generation',0,'Brand: RODE\nCategory: Audio\nWarranty: 1 Year',97,NULL,'2026-10-19 12:42:12.181121',NULL,35,36,3),(61,_binary '','Black','NEW','2026-10-09 04:25:47.191224','Audio-Technica AT2020 — available on MME from verified sellers.',17,_binary '',_binary '\0',NULL,'/uploads/products/Audio-Technica AT2020.jpg','/uploads/products/Audio-Technica AT2020.jpg','Audio-Technica AT2020',119.00,99.00,NULL,4.8,15,NULL,'audio-technica-at2020',0,'Brand: Generic\nCategory: Audio\nWarranty: 1 Year',55,NULL,'2026-10-19 12:42:12.181121',NULL,37,36,1),(62,_binary '','Black','NEW','2026-10-09 04:25:47.194683','AirPods Pro 3 — available on MME from verified sellers.',11,_binary '\0',_binary '',NULL,'/uploads/products/airpod pro 3.webp','/uploads/products/airpod pro 3.webp','AirPods Pro 3',279.00,249.00,NULL,4.8,16,NULL,'airpods-pro-3',0,'Brand: Apple\nCategory: Earbuds\nWarranty: 1 Year',54,NULL,'2026-10-19 12:42:12.181121',NULL,20,37,2),(63,_binary '','Black','NEW','2026-10-09 04:25:47.199122','AirPods Max — available on MME from verified sellers.',8,_binary '\0',_binary '\0',NULL,'/uploads/products/airpod max.jpg','/uploads/products/airpod max.jpg','AirPods Max',599.00,549.00,NULL,4.3,4,NULL,'airpods-max',0,'Brand: Apple\nCategory: Earbuds\nWarranty: 1 Year',40,NULL,'2026-10-19 12:42:12.181121',NULL,20,37,3),(64,_binary '','Black','NEW','2026-10-09 04:25:47.203363','LG NANO80 65\" 4K Smart TV — available on MME from verified sellers.',16,_binary '\0',_binary '\0',NULL,'/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp','/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp','LG NANO80 65\" 4K Smart TV',949.00,799.00,NULL,4.7,34,NULL,'lg-nano80-65-4k-smart-tv',0,'Brand: LG\nCategory: Smart TV\nWarranty: 1 Year',72,NULL,'2026-10-19 12:42:12.181121',NULL,19,38,1),(65,_binary '','Black','NEW','2026-10-09 04:25:47.207517','55\" 4K Ultra HD Smart TV — available on MME from verified sellers.',15,_binary '',_binary '\0',NULL,'/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp','/uploads/products/LG NANO80 65 (164cm) 4K SMART NANOCELL .webp','55\" 4K Ultra HD Smart TV',529.00,449.00,NULL,4.3,50,NULL,'55-4k-ultra-hd-smart-tv',0,'Brand: Generic\nCategory: Smart TV\nWarranty: 1 Year',93,NULL,'2026-10-19 12:42:12.181121',NULL,37,38,2),(66,_binary '','Black','NEW','2026-10-09 04:25:47.212579','Dell PowerEdge R760 Server — available on MME from verified sellers.',9,_binary '\0',_binary '\0',NULL,'/uploads/products/Dell PowerEdge R760.jpg','/uploads/products/Dell PowerEdge R760.jpg','Dell PowerEdge R760 Server',5499.00,4999.00,NULL,4.3,87,NULL,'dell-poweredge-r760-server',0,'Brand: Dell\nCategory: Servers\nWarranty: 1 Year',29,NULL,'2026-10-19 12:42:12.181121',NULL,5,39,1),(67,_binary '','Black,Silver','NEW','2026-10-09 04:25:47.215828','Lenovo ThinkStation PX — available on MME from verified sellers.',10,_binary '\0',_binary '\0',NULL,'/uploads/products/Lenovo ThinkStation PX.jpg','/uploads/products/Lenovo ThinkStation PX.jpg','Lenovo ThinkStation PX',3899.00,3499.00,'16GB,32GB,64GB',4.8,89,NULL,'lenovo-thinkstation-px',0,'Brand: Lenovo\nCategory: Workstations\nWarranty: 1 Year',73,'512GB,1TB,2TB','2026-10-19 12:42:12.181121',NULL,4,40,2),(68,_binary '','Black','NEW','2026-10-09 04:25:47.219574','MSI MAG 32CQ6PF Curved Monitor — available on MME from verified sellers.',13,_binary '\0',_binary '\0',NULL,'/uploads/products/MSI MAG 32CQ6PF 32 WQHD CURVED VA 180Hz.jpg','/uploads/products/MSI MAG 32CQ6PF 32 WQHD CURVED VA 180Hz.jpg','MSI MAG 32CQ6PF Curved Monitor',379.00,329.00,NULL,4.5,66,NULL,'msi-mag-32cq6pf-curved-monitor',0,'Brand: MSI\nCategory: Monitor\nWarranty: 1 Year',94,NULL,'2026-10-19 12:42:12.181121',NULL,2,12,3),(69,_binary '','Black','NEW','2026-10-09 04:25:47.223574','ROG Swift OLED PG32UCDP — available on MME from verified sellers.',13,_binary '',_binary '\0',NULL,'/uploads/products/ROG Swift OLED PG32UCDP Gaming Monitor WOLED.jpg','/uploads/products/ROG Swift OLED PG32UCDP Gaming Monitor WOLED.jpg','ROG Swift OLED PG32UCDP',1499.00,1299.00,NULL,4.3,55,NULL,'rog-swift-oled-pg32ucdp',0,'Brand: ASUS\nCategory: Monitor\nWarranty: 1 Year',76,NULL,'2026-10-19 12:42:12.181121',NULL,1,12,1),(70,_binary '','Black','NEW','2026-10-09 04:25:47.227083','MSI Optix G27C Gaming Monitor â available on MME from verified sellers.',17,_binary '\0',_binary '\0',NULL,'/uploads/products/Optix G27C MSI.webp','/uploads/products/Optix G27C MSI.webp','MSI Optix G27C Gaming Monitor',299.00,248.00,NULL,4.7,31,'','msi-optix-g27c-gaming-monitor',0,'Brand: MSI\r\nCategory: Monitor\r\nWarranty: 1 Year',34,NULL,'2026-10-19 12:42:12.181121','',2,12,2);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(2000) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `hidden` bit(1) NOT NULL,
  `image_urls` varchar(1000) DEFAULT NULL,
  `rating` int NOT NULL,
  `seller_responded_at` datetime(6) DEFAULT NULL,
  `seller_response` varchar(2000) DEFAULT NULL,
  `verified_purchase` bit(1) NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpl51cejpw4gy5swfar8br9ngi` (`product_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'Excellent build quality! Runs Cyberpunk at ultra settings with ray tracing. Fast delivery from TechZone.','2026-10-05 12:21:35.292316',_binary '\0',NULL,5,NULL,NULL,_binary '',1,2),(2,'RTX 5070 performs amazingly. Cool and quiet under load. Verified seller, highly recommend.','2026-10-05 12:21:35.294311',_binary '\0',NULL,5,'2026-10-03 12:21:35.294311','Thank you for your purchase! Enjoy your new GPU.',_binary '',2,2),(3,'Best gaming CPU on the market. 3D V-Cache makes a huge difference in FPS.','2026-10-05 12:21:35.295311',_binary '\0',NULL,5,NULL,NULL,_binary '',3,2);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seller_follows`
--

DROP TABLE IF EXISTS `seller_follows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seller_follows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `seller_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK5ebs5sg13685dx0vape1d4y2s` (`user_id`,`seller_id`),
  KEY `FKpacvhv7qgb05rsof9jb9f4tu9` (`seller_id`),
  CONSTRAINT `FKcncpau7c26olss7htfawyn176` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpacvhv7qgb05rsof9jb9f4tu9` FOREIGN KEY (`seller_id`) REFERENCES `sellers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seller_follows`
--

LOCK TABLES `seller_follows` WRITE;
/*!40000 ALTER TABLE `seller_follows` DISABLE KEYS */;
/*!40000 ALTER TABLE `seller_follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sellers`
--

DROP TABLE IF EXISTS `sellers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sellers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(200) DEFAULT NULL,
  `banner_url` varchar(500) DEFAULT NULL,
  `business_info` varchar(500) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `followers` int DEFAULT NULL,
  `id_document` varchar(100) DEFAULT NULL,
  `joined_at` datetime(6) DEFAULT NULL,
  `level` enum('NEW','TOP','TRUSTED','VERIFIED') NOT NULL,
  `location` varchar(100) DEFAULT NULL,
  `logo_url` varchar(500) DEFAULT NULL,
  `owner_name` varchar(100) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `rejection_reason` varchar(500) DEFAULT NULL,
  `response_rate` double DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED','SUSPENDED') NOT NULL,
  `store_name` varchar(150) NOT NULL,
  `total_products` int DEFAULT NULL,
  `total_sales` int DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `country` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4eudub0sj5cxxchae4gcnp7y5` (`user_id`),
  CONSTRAINT `FKjnqi0k1rlkb8h3fus7f5wfqd1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sellers`
--

LOCK TABLES `sellers` WRITE;
/*!40000 ALTER TABLE `sellers` DISABLE KEYS */;
INSERT INTO `sellers` VALUES (1,'Street 271, Boeng Keng Kang','/uploads/avatars/cover photos/photo_2026-10-09_09-47-07.jpg','Registered computer retail business','2026-10-05 12:21:35.224763','Premium PC components and gaming gear. Authorized reseller of ASUS, MSI, and NVIDIA products.',0,NULL,'2024-10-05 12:21:35.223757','TOP','Phnom Penh','/uploads/avatars/sellers/photo_2026-10-09_10-04-10.jpg','Vannak Chen','098765432',4.9,NULL,98.5,'APPROVED','TechZone Cambodia',26,6,3,'Cambodia'),(2,'Russian Blvd, Toul Kork','/uploads/avatars/cover photos/photo_2026-10-09_09-47-15.jpg','PC assembly and retail','2026-10-05 12:21:35.227276','Custom gaming PCs, laptops, and peripherals. Build your dream rig with us.',0,NULL,'2025-08-05 12:21:35.227276','TRUSTED','Phnom Penh','/uploads/avatars/sellers/photo_2026-10-09_10-04-14.jpg','Dara Kim','097111222',4.7,NULL,96,'APPROVED','PC Master',26,2,4,'Cambodia'),(3,'Siem Reap City Center','/uploads/avatars/cover photos/photo_2026-10-09_09-47-19.jpg','Electronics retail','2026-10-05 12:21:35.229277','Laptops, monitors, and office computing solutions at competitive prices.',0,NULL,'2026-02-05 12:21:35.229277','VERIFIED','Siem Reap','/uploads/avatars/sellers/photo_2026-10-09_10-04-17.jpg','Sreyneat Ly','096333444',4.5,NULL,94,'APPROVED','DigiTech Store',18,0,5,'Cambodia');
/*!40000 ALTER TABLE `sellers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar_url` varchar(500) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `full_name` varchar(120) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `role` enum('ADMIN','CUSTOMER','SELLER') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,'2026-10-05 12:21:34.907074','admin@computerhub.com',_binary '','Platform Admin','$2a$10$X7lIE5AEasHCrctJxCcLcesWinKatHQgPzJEEmBYsf.xst9qQ4ml.','012000000','ADMIN','2026-10-19 12:42:11.223668',NULL),(2,'/uploads/avatars/customers/mm.jpg','2026-10-05 12:21:35.004139','customer@computerhub.com',_binary '','Sokha Meas','$2a$10$TTZV32MLjTx33SWkeYZelukI.BBLuOeQvly3ruuEgj5mNJquuS2XK','012345678','CUSTOMER','2026-10-19 12:42:11.313552','/uploads/avatars/cover photos/photo_2026-10-09_09-47-07.jpg'),(3,'/uploads/avatars/sellers/photo_2026-10-09_09-48-44.jpg','2026-10-05 12:21:35.079063','techzone@computerhub.com',_binary '','Vannak Chea','$2a$10$ycjWTIeT8TaPa4hIvamNceQAbOs/Ea0a0F78s9D2/T0WnD8VqkmN2','098765432','SELLER','2026-10-19 12:42:11.763709','/uploads/avatars/cover photos/photo_2026-10-09_09-47-07.jpg'),(4,'/uploads/avatars/sellers/photo_2026-10-09_09-48-53.jpg','2026-10-05 12:21:35.150462','pcmaster@computerhub.com',_binary '','Dara Kim','$2a$10$KTNwIj5pcE/IMjd6YrPy4ufAuv5bxxnuPDn0gNd2bM3uF.L/XjnN2','097111222','SELLER','2026-10-19 12:42:11.837512','/uploads/avatars/cover photos/photo_2026-10-09_09-47-15.jpg'),(5,'/uploads/avatars/sellers/photo_2026-10-09_09-48-56.jpg','2026-10-05 12:21:35.222763','digitech@computerhub.com',_binary '','Sreyneat Ly','$2a$10$w42pxtpxIM7Yw8j15JSnreCxZmRjJMNMBb3tGDjtSvSD6e.AuNRYO','096333444','SELLER','2026-10-19 12:42:12.027581','/uploads/avatars/cover photos/photo_2026-10-09_09-48-27.jpg'),(6,'/uploads/avatars/customers/pckdv.jpg','2026-10-09 08:28:46.980704','rithy@computerhub.com',_binary '','Rithy Sok','$2a$10$sl8j2pzzOr5/kEm7PUIQ3eRP081p9nYpN0q5vGSCdlYlIpWSduari','012111222','CUSTOMER','2026-10-19 12:42:11.391769','/uploads/avatars/cover photos/photo_2026-10-09_09-47-15.jpg'),(7,'/uploads/avatars/customers/intro-1641239073.jpg','2026-10-09 08:28:47.084684','chenda@computerhub.com',_binary '','Chenda Phan','$2a$10$N35mScwFkEyyWZVzyeKO2uMtzON2bjzZ0AcOCHEQINecRIZEueL0e','012222333','CUSTOMER','2026-10-19 12:42:11.466788','/uploads/avatars/cover photos/photo_2026-10-09_09-47-19.jpg'),(8,'/uploads/avatars/customers/wolf-wall-street.jpg','2026-10-09 08:28:47.158241','piseth@computerhub.com',_binary '','Piseth Hor','$2a$10$FIshg0BW4itkzsUYkc/v1um.6PdfzVeS2mz9ngV00Q.NZkWPpcjqS','012333444','CUSTOMER','2026-10-19 12:42:11.540520','/uploads/avatars/cover photos/photo_2026-10-09_09-48-27.jpg'),(9,'/uploads/avatars/customers/lord-of-war.jpg','2026-10-09 08:28:47.232417','sothea@computerhub.com',_binary '','Sothea Lim','$2a$10$e9K1/0sM5Eeu0p3ASdDU7.v.1S2Oie4UCpqlo7YvwAvBBLkca.t/i','012444555','CUSTOMER','2026-10-19 12:42:11.616215','/uploads/avatars/cover photos/playstation cover.jpg'),(10,'/uploads/avatars/customers/movie-portrait.jpg','2026-10-09 08:28:47.303966','bopha@computerhub.com',_binary '','Bopha Keo','$2a$10$UX/3Ub/dnbLnC7243FviheR4cXBxZKY4OCRk8dOuMRjam94SUGzJC','012555666','CUSTOMER','2026-10-19 12:42:11.690378','/uploads/avatars/cover photos/photo_2026-10-09_09-48-23.jpg'),(11,'/uploads/avatars/customers/mm.jpg','2026-10-19 02:27:33.123749','chouyamata@gmail.com',_binary '','Chou Yamata','$2a$10$yc/S55QsujY5ErxmZN4qjeG07VRuCVDH2NNQ1921N5iBIDWVZVt0W','010411311','CUSTOMER','2026-10-19 12:14:26.631130','/uploads/avatars/cover photos/fdcfde4ac9cc40a7.jpg');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist_items`
--

DROP TABLE IF EXISTS `wishlist_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqxj7lncd242b59fb78rqegyxj` (`product_id`),
  KEY `FKmmj2k1i459yu449k3h1vx5abp` (`user_id`),
  CONSTRAINT `FKmmj2k1i459yu449k3h1vx5abp` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqxj7lncd242b59fb78rqegyxj` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist_items`
--

LOCK TABLES `wishlist_items` WRITE;
/*!40000 ALTER TABLE `wishlist_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `wishlist_items` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-24 16:38:50
