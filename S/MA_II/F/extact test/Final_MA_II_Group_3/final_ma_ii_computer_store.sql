-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: final_ma_ii_computer_store
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
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `level` enum('ERROR','INFO','WARN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` text COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` VALUES (4,'admin','2026-10-19 07:41:50.688794','INFO','Modern Multiple Technology (MMT) catalog seeded from local uploads'),(5,'admin','2026-10-19 07:41:50.689797','WARN','Critical stock watch: MSI GeForce RTX 5070 Gaming X at 2 units'),(6,'admin','2026-10-19 07:41:50.690800','INFO','Flash promotions enabled for drones, Wi-Fi 7, and OLED displays'),(7,'mmt_admin','2026-10-21 11:15:12.938626','WARN','Order MMT-EAB06E95 status: PENDING → CANCELLED'),(8,'mmt_admin','2026-10-21 11:15:23.828334','INFO','Order MMT-188AC3EA status: PENDING → DELIVERED'),(9,'mmt_admin','2026-10-21 11:20:09.188820','INFO','Order MMT-FE676D39 status: PENDING → DELIVERED');
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
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
  `description` text COLLATE utf8mb4_unicode_ci,
  `logo_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `slug` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKoce3937d2f4mpfqrycbr0l93m` (`name`),
  UNIQUE KEY `UKpnhnc9urm6fro7oseu9vka70q` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brands`
--

LOCK TABLES `brands` WRITE;
/*!40000 ALTER TABLE `brands` DISABLE KEYS */;
INSERT INTO `brands` VALUES (1,_binary '','AMD products at MMT',NULL,'AMD','amd'),(2,_binary '','Anker products at MMT',NULL,'Anker','anker'),(3,_binary '','APC products at MMT',NULL,'APC','apc'),(4,_binary '','Apple products at MMT',NULL,'Apple','apple'),(5,_binary '','ASUS products at MMT',NULL,'ASUS','asus'),(6,_binary '','Corsair products at MMT',NULL,'Corsair','corsair'),(7,_binary '','Dell products at MMT',NULL,'Dell','dell'),(8,_binary '','DJI products at MMT',NULL,'DJI','dji'),(9,_binary '','Elgato products at MMT',NULL,'Elgato','elgato'),(10,_binary '','Gigabyte products at MMT',NULL,'Gigabyte','gigabyte'),(11,_binary '','GoPro products at MMT',NULL,'GoPro','gopro'),(12,_binary '','HyperX products at MMT',NULL,'HyperX','hyperx'),(13,_binary '','Intel products at MMT',NULL,'Intel','intel'),(14,_binary '','Lenovo products at MMT',NULL,'Lenovo','lenovo'),(15,_binary '','LG products at MMT',NULL,'LG','lg'),(16,_binary '','Microsoft products at MMT',NULL,'Microsoft','microsoft'),(17,_binary '','MSI products at MMT',NULL,'MSI','msi'),(18,_binary '','Samsung products at MMT',NULL,'Samsung','samsung'),(19,_binary '','Shure products at MMT',NULL,'Shure','shure'),(20,_binary '','Sony products at MMT',NULL,'Sony','sony'),(21,_binary '','Steam products at MMT',NULL,'Steam','steam'),(22,_binary '','Synology products at MMT',NULL,'Synology','synology'),(23,_binary '','TP-Link products at MMT',NULL,'TP-Link','tp-link');
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
  `quantity` int NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1vhvont0fdtramle6nmghntj7` (`user_id`,`product_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK709eickf3kc0dujx3ub9i7btf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
  `description` text COLLATE utf8mb4_unicode_ci,
  `icon_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `slug` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKoul14ho7bctbefv8jywp5v3i2` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (19,'Laptops, desktops, and workstations','laptop','Laptops & PCs','laptops-pcs',1),(20,'GPUs, CPUs, RAM, and boards','memory','Components','components',2),(21,'Gaming monitors and smart TVs','tv','Monitors & TVs','monitors-tvs',3),(22,'Routers, mesh, and switches','wifi','Networking','networking',4),(23,'Drones, action cams, and capture','videocam','Drones & Cameras','drones-cameras',5),(24,'Mics, headsets, and capture cards','mic','Audio & Streaming','audio-streaming',6),(25,'Consoles, controllers, and games','sports_esports','Gaming & Consoles','gaming-consoles',7),(26,'SSDs, HDDs, and NAS','sd_storage','Storage & NAS','storage-nas',8),(27,'Chargers, UPS, and power gear','cable','Accessories','accessories',9),(28,'Steam, Xbox, and PlayStation cards','card_giftcard','Gift Cards','gift-cards',10);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (2,'Lenovo Legion Pro 7i',1,1899.00,2,40),(3,'Lenovo Legion Pro 7i',2,1899.00,3,40),(4,'Steam Gift Card $50',1,50.00,4,67);
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
  `created_at` datetime(6) NOT NULL,
  `discount` decimal(12,2) NOT NULL,
  `discount_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `order_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `shipping` decimal(12,2) NOT NULL,
  `shipping_address` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('CANCELLED','DELIVERED','PAID','PENDING','PROCESSING','SHIPPED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `tax` decimal(12,2) NOT NULL,
  `total` decimal(12,2) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnthkiu7pgmnqnu86i2jyoe2v7` (`order_number`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (2,'2026-10-19 07:56:00.482409',50.00,'CYBER2025','MMT-FE676D39',0.00,'Phnom Penh','DELIVERED',1899.00,129.43,1978.43,5),(3,'2026-10-19 09:12:13.718447',0.00,NULL,'MMT-188AC3EA',0.00,'MMT Store, Phnom Penh, Cambodia','DELIVERED',3798.00,265.86,4063.86,4),(4,'2026-10-20 01:12:57.250421',0.00,NULL,'MMT-EAB06E95',15.00,'MMT Store, Phnom Penh, Cambodia','CANCELLED',50.00,3.50,68.50,5);
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
  `architecture` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bin_location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `brand` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cost_price` decimal(12,2) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `featured` bit(1) NOT NULL,
  `flash_drop` bit(1) NOT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `original_price` decimal(12,2) DEFAULT NULL,
  `price` decimal(12,2) NOT NULL,
  `rating` double DEFAULT NULL,
  `review_count` int DEFAULT NULL,
  `sku` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `specs_json` text COLLATE utf8mb4_unicode_ci,
  `stock` int NOT NULL,
  `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `warn_min` int DEFAULT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfhmd06dsmj6k0n90swsh8ie9g` (`sku`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (40,'Intel Core Ultra','B1-LAP','Lenovo',1550.00,'2026-10-19 07:41:50.499509','High-refresh gaming laptop for creators and esports.',_binary '',_binary '','/uploads/products/lenovo-legion-pro-7i.webp','Lenovo Legion Pro 7i',2099.00,1899.00,4.8,96,'MMT-LAP-LEGION7I','{\"Display\":\"16\\\" QHD+\",\"GPU\":\"RTX 4070\",\"RAM\":\"32GB\",\"Storage\":\"1TB SSD\"}',5,'laptop,gaming,lenovo','2026-10-19 09:12:13.739787',3,19),(41,'Intel Core Ultra','B1-LAP','Dell',1400.00,'2026-10-19 07:41:50.504145','Premium OLED ultrabook for design and productivity.',_binary '',_binary '\0','/uploads/products/dell-xps-15-oled.webp','Dell XPS 15 OLED',1899.00,1699.00,4.7,74,'MMT-LAP-XPS15','{\"Display\":\"15.6\\\" OLED\",\"RAM\":\"16GB\",\"Storage\":\"512GB SSD\",\"Weight\":\"1.86kg\"}',12,'laptop,dell,oled','2026-10-19 07:41:50.504145',4,19),(42,'Blackwell','A2-RIG','ASUS',1800.00,'2026-10-19 07:41:50.506160','Prebuilt ROG tower ready for 1440p / 4K gaming.',_binary '',_binary '','/uploads/products/asus-rog-strix-gaming-pc-rtx-5070.avif','ASUS ROG Strix Gaming PC RTX 5070',2499.00,2199.00,4.9,41,'MMT-PC-ROG-5070','{\"GPU\":\"RTX 5070\",\"CPU\":\"Ryzen 7\",\"RAM\":\"32GB DDR5\",\"Cooling\":\"AIO 360\"}',5,'desktop,rog,prebuilt','2026-10-19 07:41:50.506160',2,19),(43,'Blackwell','A1-GPU','Gigabyte',720.00,'2026-10-19 07:41:50.507346','Next-gen AORUS graphics for enthusiast gaming.',_binary '',_binary '','/uploads/products/gigabyte-aorus-rtx-5070-ti.png','Gigabyte AORUS RTX 5070 Ti',999.00,899.00,4.8,58,'MMT-GPU-5070TI','{\"VRAM\":\"16GB\",\"TDP\":\"300W\",\"Bus\":\"256-bit\",\"Cooling\":\"WINDFORCE\"}',7,'gpu,nvidia,aorus','2026-10-19 07:41:50.507346',3,20),(44,'Blackwell','A1-GPU','MSI',520.00,'2026-10-19 07:41:50.508854','TRI FROZR cooled RTX 5070 for high-FPS builds.',_binary '\0',_binary '','/uploads/products/msi-geforce-rtx-5070-gaming-x-12gb.jpg','MSI GeForce RTX 5070 Gaming X',699.00,649.00,4.7,33,'MMT-GPU-5070-MSI','{\"VRAM\":\"12GB\",\"TDP\":\"250W\",\"Fans\":\"3X\",\"Boost\":\"High OC\"}',2,'gpu,msi,gaming','2026-10-19 07:41:50.508854',5,20),(45,'Zen 5','A3-CPU','AMD',390.00,'2026-10-19 07:41:50.510376','3D V-Cache gaming CPU for max FPS.',_binary '',_binary '\0','/uploads/products/amd-ryzen-7-9800x3d.png','AMD Ryzen 7 9800X3D',519.00,479.00,4.9,210,'MMT-CPU-9800X3D','{\"Cores\":\"8\",\"Threads\":\"16\",\"Cache\":\"96MB\",\"Socket\":\"AM5\"}',15,'cpu,amd,gaming','2026-10-19 07:41:50.510376',5,20),(46,'Arrow Lake','A3-CPU','Intel',320.00,'2026-10-19 07:41:50.511382','Arrow Lake desktop CPU for creators and gamers.',_binary '\0',_binary '\0','/uploads/products/intel-core-ultra-7-265k.jpg','Intel Core Ultra 7 265K',429.00,399.00,4.6,88,'MMT-CPU-265K','{\"Cores\":\"20\",\"Socket\":\"LGA1851\",\"iGPU\":\"Yes\",\"TDP\":\"125W\"}',18,'cpu,intel,ultra','2026-10-19 07:41:50.511382',6,20),(47,'DDR5','A4-RAM','Corsair',95.00,'2026-10-19 07:41:50.512895','Fast DDR5 kit for gaming and content creation.',_binary '\0',_binary '\0','/uploads/products/corsair-vengeance-ddr5-32gb.jpg','Corsair Vengeance DDR5 32GB',149.00,129.00,4.8,156,'MMT-RAM-VEN-32','{\"Capacity\":\"32GB\",\"Speed\":\"6000MT/s\",\"Kit\":\"2x16GB\",\"RGB\":\"No\"}',40,'ram,ddr5,corsair','2026-10-19 07:41:50.512895',10,20),(48,'WOLED','C1-MON','ASUS',1050.00,'2026-10-19 07:41:50.514401','32\" WOLED gaming monitor with stunning contrast.',_binary '',_binary '','/uploads/products/ROG%20Swift%20OLED%20PG32UCDP%20Gaming%20Monitor%20WOLED.jpg','ROG Swift OLED PG32UCDP',1499.00,1299.00,4.9,67,'MMT-MON-PG32','{\"Size\":\"32\\\"\",\"Refresh\":\"240Hz\",\"Res\":\"4K\",\"Panel\":\"WOLED\"}',4,'monitor,oled,rog','2026-10-19 07:41:50.514401',2,21),(49,'Fast IPS','C1-MON','ASUS',340.00,'2026-10-19 07:41:50.516695','Fast IPS gaming monitor for competitive play.',_binary '\0',_binary '\0','/uploads/products/asus-rog-strix-27-monitor.webp','ASUS ROG Strix 27 Monitor',499.00,449.00,4.7,102,'MMT-MON-ROG27','{\"Size\":\"27\\\"\",\"Refresh\":\"180Hz\",\"Res\":\"QHD\",\"GSync\":\"Compatible\"}',11,'monitor,gaming,asus','2026-10-19 07:41:50.516695',4,21),(50,'NanoCell','C2-TV','LG',700.00,'2026-10-19 07:41:50.517826','Nanocell 4K smart TV for movies and console gaming.',_binary '',_binary '\0','/uploads/products/LG%20NANO80%2065%20(164cm)%204K%20SMART%20NANOCELL%20.webp','LG NANO80 65\" 4K Smart TV',1099.00,899.00,4.6,49,'MMT-TV-NANO80-65','{\"Size\":\"65\\\"\",\"Res\":\"4K\",\"HDR\":\"Yes\",\"Smart\":\"webOS\"}',6,'tv,lg,4k','2026-10-19 07:41:50.517826',2,21),(51,'Wi-Fi 6','D1-NET','TP-Link',65.00,'2026-10-19 07:41:50.518830','AX3000 Wi-Fi 6 router for modern homes.',_binary '\0',_binary '\0','/uploads/products/tp-link-archer-ax55.jpg','TP-Link Archer AX55',129.00,99.00,4.5,188,'MMT-NET-AX55','{\"WiFi\":\"AX3000\",\"Bands\":\"Dual\",\"Ports\":\"Gigabit\",\"VPN\":\"Yes\"}',25,'router,wifi6,tplink','2026-10-19 07:41:50.518830',8,22),(52,'Wi-Fi 7','D1-NET','TP-Link',270.00,'2026-10-19 07:41:50.520843','Next-gen Wi-Fi 7 router for multi-gig homes.',_binary '',_binary '','/uploads/products/BE9300%20Tri-Band%20Wi-Fi%207%20Router.jpg','BE9300 Tri-Band Wi-Fi 7 Router',399.00,349.00,4.7,36,'MMT-NET-BE9300','{\"WiFi\":\"BE9300\",\"Bands\":\"Tri\",\"Ports\":\"2.5G\",\"Mesh\":\"Compatible\"}',9,'router,wifi7,tplink','2026-10-19 07:41:50.520843',3,22),(53,'Wi-Fi 7 Mesh','D1-NET','TP-Link',390.00,'2026-10-19 07:41:50.521849','BE10000 whole-home Wi-Fi 7 mesh system.',_binary '',_binary '\0','/uploads/products/TP-Link%20Deco%20BE63%20BE10000%20Wireless%20.jpg','TP-Link Deco BE63 Mesh',559.00,499.00,4.8,28,'MMT-NET-DECO-BE63','{\"System\":\"Mesh\",\"WiFi\":\"BE10000\",\"Coverage\":\"Whole home\",\"App\":\"Deco\"}',7,'mesh,wifi7,deco','2026-10-19 07:41:50.521849',3,22),(54,'Mini Series','E1-DRN','DJI',620.00,'2026-10-19 07:41:50.522356','Compact cinematic drone with omnidirectional sensing.',_binary '',_binary '','/uploads/products/DJI%20Mini%204%20Pro.jpg','DJI Mini 4 Pro',859.00,759.00,4.9,143,'MMT-DRN-MINI4','{\"Camera\":\"4K/60fps\",\"Weight\":\"249g\",\"Obstacle\":\"Omni\",\"Flight\":\"34 min\"}',10,'drone,dji,camera','2026-10-19 07:41:50.522356',3,23),(55,'Mavic Series','E1-DRN','DJI',1550.00,'2026-10-19 07:41:50.523360','Pro aerial platform for creators and survey work.',_binary '',_binary '\0','/uploads/products/DJI%20Mavic%204%20Pro.jpg','DJI Mavic 4 Pro',2099.00,1899.00,4.8,52,'MMT-DRN-MAVIC4','{\"Camera\":\"Hasselblad\",\"Res\":\"5.1K\",\"Range\":\"Long\",\"Battery\":\"Intelligent\"}',4,'drone,pro,dji','2026-10-19 07:41:50.523360',2,23),(56,'HERO13','E2-CAM','GoPro',300.00,'2026-10-19 07:41:50.525385','Rugged action camera for sport and adventure.',_binary '\0',_binary '','/uploads/products/GoPro%20HERO13%20Black.jpg','GoPro HERO13 Black',449.00,399.00,4.7,201,'MMT-CAM-HERO13','{\"Video\":\"5.3K\",\"Stabilization\":\"HyperSmooth\",\"Waterproof\":\"Yes\",\"Mods\":\"Compatible\"}',16,'gopro,action,camera','2026-10-19 07:41:50.525385',5,23),(57,'Osmo Action','E2-CAM','DJI',260.00,'2026-10-19 07:41:50.526388','High-dynamic-range action cam with dual screens.',_binary '\0',_binary '\0','/uploads/products/DJI%20Osmo%20Action%205%20Pro.png','DJI Osmo Action 5 Pro',379.00,349.00,4.6,77,'MMT-CAM-OSMO5','{\"Video\":\"4K/120fps\",\"Screens\":\"Dual\",\"Color\":\"10-bit\",\"Battery\":\"Long\"}',14,'action,dji,camera','2026-10-19 07:41:50.526388',4,23),(58,'Dynamic','F1-AUD','Shure',300.00,'2026-10-19 07:41:50.526893','Broadcast microphone for podcasts and vocals.',_binary '',_binary '\0','/uploads/products/Shure%20SM7B.jpg','Shure SM7B',429.00,399.00,4.9,320,'MMT-AUD-SM7B','{\"Type\":\"Dynamic\",\"Pattern\":\"Cardioid\",\"Use\":\"Broadcast\",\"XLR\":\"Yes\"}',12,'mic,podcast,shure','2026-10-19 07:41:50.526893',4,24),(59,'Capture','F2-STR','Elgato',145.00,'2026-10-19 07:41:50.529361','USB capture card for 4K60 streaming and recording.',_binary '',_binary '','/uploads/products/Elgato%204K%20X.jpg','Elgato 4K X',229.00,199.00,4.8,165,'MMT-STR-4KX','{\"Capture\":\"4K60\",\"HDR\":\"Yes\",\"Interface\":\"USB-C\",\"Latency\":\"Ultra-low\"}',20,'capture,streaming,elgato','2026-10-19 07:41:50.529361',6,24),(60,'Gaming Audio','F1-AUD','HyperX',65.00,'2026-10-19 07:41:50.530368','Comfort gaming headset with clear mic.',_binary '\0',_binary '\0','/uploads/products/hyperx-cloud-iii.webp','HyperX Cloud III',119.00,99.00,4.6,240,'MMT-AUD-CLOUD3','{\"Type\":\"Wired\",\"Driver\":\"53mm\",\"Mic\":\"Detachable\",\"Compat\":\"PC/Console\"}',30,'headset,hyperx,gaming','2026-10-19 07:41:50.530368',8,24),(61,'Series X','G1-CON','Microsoft',420.00,'2026-10-19 07:41:50.531372','Flagship 4K console for next-gen gaming.',_binary '',_binary '\0','/uploads/products/Microsoft-Xbox-Series-X_b562417b-7584-4740-983b-422cb2832fb9.7ef4114105e8cbe37cc4125a3cb125f0.avif','Xbox Series X',549.00,499.00,4.8,410,'MMT-CON-XSX','{\"Res\":\"4K\",\"Storage\":\"1TB SSD\",\"RayTracing\":\"Yes\",\"FPS\":\"Up to 120\"}',9,'console,xbox,gaming','2026-10-19 07:41:50.531372',3,25),(62,'DualSense','G2-ACC','Sony',45.00,'2026-10-19 07:41:50.532370','PS5 DualSense with haptic feedback.',_binary '\0',_binary '\0','/uploads/products/dualsense-controller-product-thumbnail-01-en-14sep21.webp','DualSense Wireless Controller',79.00,69.00,4.7,500,'MMT-CON-DSENSE','{\"Platform\":\"PS5\",\"Haptics\":\"Yes\",\"Battery\":\"Rechargeable\",\"Color\":\"White\"}',35,'controller,ps5,sony','2026-10-19 07:41:50.532370',10,25),(63,'PCIe 4.0','H1-SSD','Samsung',140.00,'2026-10-19 07:41:50.532878','Flagship PCIe 4.0 NVMe SSD.',_binary '',_binary '\0','/uploads/products/samsung-990-pro-2tb.jpg','Samsung 990 PRO 2TB',229.00,189.00,4.9,290,'MMT-SSD-990P-2T','{\"Capacity\":\"2TB\",\"Interface\":\"NVMe\",\"SeqRead\":\"7450MB/s\",\"DRAM\":\"Yes\"}',22,'ssd,samsung,nvme','2026-10-19 07:41:50.532878',6,26),(64,'DSM','H2-NAS','Synology',470.00,'2026-10-19 07:41:50.535414','4-bay NAS for home lab and small business.',_binary '',_binary '\0','/uploads/products/Synology%20DiskStation%20DS923+.jpg','Synology DiskStation DS923+',649.00,599.00,4.8,91,'MMT-NAS-DS923','{\"Bays\":\"4\",\"RAM\":\"4GB\",\"LAN\":\"2.5GbE\",\"Expandable\":\"Yes\"}',8,'nas,synology,storage','2026-10-19 07:41:50.535414',3,26),(65,'GaN','I1-PWR','Anker',48.00,'2026-10-19 07:41:50.536423','Compact multi-port GaN charger for laptops and phones.',_binary '\0',_binary '','/uploads/products/Anker%20Prime%20100W%20GaN%20Charger.webp','Anker Prime 100W GaN Charger',99.00,79.00,4.7,180,'MMT-ACC-ANKER100','{\"Power\":\"100W\",\"Ports\":\"Multi\",\"Tech\":\"GaN\",\"Travel\":\"Yes\"}',40,'charger,anker,usb-c','2026-10-19 07:41:50.536423',10,27),(66,'UPS','I2-UPS','APC',185.00,'2026-10-19 07:41:50.537429','Battery backup UPS for PCs and networking gear.',_binary '\0',_binary '\0','/uploads/products/APC%20Back-UPS%20Pro%20BR1500MS2.jpg','APC Back-UPS Pro BR1500MS2',279.00,249.00,4.6,64,'MMT-ACC-UPS1500','{\"VA\":\"1500\",\"LCD\":\"Yes\",\"Outlets\":\"10\",\"AVR\":\"Yes\"}',10,'ups,apc,power','2026-10-19 07:41:50.537429',3,27),(67,'Digital','J1-GC','Steam',47.00,'2026-10-19 07:41:50.538438','Digital Steam wallet top-up card.',_binary '\0',_binary '','/uploads/products/steam-gift-card-50usd.jpg','Steam Gift Card $50',NULL,50.00,5,80,'MMT-GC-STEAM50','{\"Value\":\"$50\",\"Platform\":\"Steam\",\"Type\":\"Gift Card\",\"Region\":\"Global\"}',100,'gift,steam,digital','2026-10-21 11:15:12.943191',20,28),(68,'Digital','J1-GC','Microsoft',95.00,'2026-10-19 07:41:50.539447','Xbox / Microsoft Store digital credit.',_binary '\0',_binary '\0','/uploads/products/xbox-gift-card-100.jpg','Xbox Gift Card $100',NULL,100.00,5,55,'MMT-GC-XBOX100','{\"Value\":\"$100\",\"Platform\":\"Xbox\",\"Type\":\"Gift Card\",\"Region\":\"Global\"}',80,'gift,xbox,digital','2026-10-19 07:41:50.539447',15,28),(69,'H2 Audio','I3-AUD','Apple',180.00,'2026-10-19 07:41:50.540451','Active noise cancelling earbuds with USB-C case.',_binary '',_binary '\0','/uploads/products/airpod%20pro%203.webp','AirPods Pro 3',279.00,249.00,4.8,220,'MMT-ACC-APP-PRO3','{\"ANC\":\"Yes\",\"Chip\":\"H2\",\"Case\":\"USB-C\",\"Water\":\"IP54\"}',18,'earbuds,apple,audio','2026-10-19 07:41:50.540451',5,27);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `member_since` date DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','CUSTOMER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ban_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `banned` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (4,'/uploads/avatars/customers/mm.jpg','2026-10-19 07:41:50.684599','admin@mmt.store','MMT Admin','2026-10-19','$2a$10$v5yH1ax8.GqW9.TnhLmAWOjVm0XAONnSqq2sKeC2Y3FmWYTigCiWq','ADMIN','mmt_admin',NULL,_binary '\0'),(5,'/uploads/avatars/customers/intro-1641239073.jpg','2026-10-19 07:41:50.686621','customer@mmt.store','MMT Customer','2026-10-19','$2a$10$MPa1gAtPyldHa3L2disZeOAC.tjM1zAdh.T/cOT0S5K33SOF1S/7u','CUSTOMER','mmt_customer',NULL,_binary '\0'),(6,NULL,'2026-10-21 11:10:49.346818','chouyamata@gmail.com','Chou Yamata','2026-10-21','$2a$10$tY66eRgX6yy63Zo47EcDReB8aoNX.DwjZ4mxW.NIdns4.MfICHtF2','CUSTOMER','chouyamata',NULL,_binary '\0');
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
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtp53unkks741xiqi6m620i7mx` (`user_id`,`product_id`),
  KEY `FKqxj7lncd242b59fb78rqegyxj` (`product_id`),
  CONSTRAINT `FKmmj2k1i459yu449k3h1vx5abp` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqxj7lncd242b59fb78rqegyxj` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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

-- Dump completed on 2026-10-21 19:30:48
