-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ec_test_1_f
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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cart_id` (`cart_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (1,2,1,1),(2,2,3,1);
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  `slug` varchar(120) DEFAULT NULL,
  `icon` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Laptops','Portable computers for work and play','laptops','bi-laptop'),(2,'Desktop PCs','Ready-to-use desktop computers','desktop-pcs','bi-pc-display'),(3,'Gaming PCs','High-performance gaming systems','gaming-pcs','bi-controller'),(4,'CPUs','Processors from Intel and AMD','cpus','bi-cpu'),(5,'Graphics Cards','NVIDIA and AMD GPUs','graphics-cards','bi-gpu-card'),(6,'RAM','System memory modules','ram','bi-memory'),(7,'SSD','Solid state storage drives','ssd','bi-device-ssd'),(8,'HDD','Hard disk drives','hdd','bi-hdd'),(9,'Motherboards','Motherboard platforms','motherboards','bi-motherboard'),(10,'Power Supplies','PSU units','power-supplies','bi-lightning-charge'),(11,'PC Cases','Computer cases and chassis','pc-cases','bi-box'),(12,'Monitors','Displays and gaming monitors','monitors','bi-display'),(13,'Keyboards','Mechanical and membrane keyboards','keyboards','bi-keyboard'),(14,'Mice','Gaming and office mice','mice','bi-mouse'),(15,'Headsets','Gaming and office headsets','headsets','bi-headphones'),(16,'Speakers','Desktop speakers','speakers','bi-speaker'),(17,'Networking','Routers, switches, and adapters','networking','bi-router'),(18,'Accessories','Cables, stands, and more','accessories','bi-usb-symbol');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int DEFAULT NULL,
  `product_name` varchar(200) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,'NVIDIA GeForce RTX 5070',599.00,1),(2,1,3,'AMD Ryzen 7 7800X3D',449.00,1),(3,2,1,'NVIDIA GeForce RTX 5070',599.00,1),(4,2,3,'AMD Ryzen 7 7800X3D',449.00,1);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `shipping_cost` decimal(10,2) DEFAULT NULL,
  `shipping_address` text NOT NULL,
  `customer_name` varchar(120) NOT NULL,
  `customer_email` varchar(120) NOT NULL,
  `customer_phone` varchar(40) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `payment_status` varchar(30) NOT NULL,
  `stripe_session_id` varchar(255) DEFAULT NULL,
  `stripe_payment_intent_id` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `ix_orders_stripe_payment_intent_id` (`stripe_payment_intent_id`),
  KEY `ix_orders_stripe_session_id` (`stripe_session_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,2,1053.00,5.00,'Sangkat Boeng Salang, Phnom Penh, Cambodia','Demo Customer','customer@techstore.com','010411311','Cancelled','Failed','cs_test_b1tV96twLWs0527cfhakmmVVdIY1Jt0XyziXP7F0AjOxu5lNoYubqUbcyY',NULL,'2026-09-10 06:59:08'),(2,2,1053.00,5.00,'Sangkat Boeng Salang, Phnom Penh, Cambodia','Chou Yamata','customer@techstore.com','010411311','Pending','Pending','cs_test_b1Bwgl03lxQAzFqnkIgjlWUKMc3UfclJnLDdt1ssP4miOeWWfmke5fASQb',NULL,'2026-09-10 07:03:57');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `description` text,
  `brand` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `discount_price` decimal(10,2) DEFAULT NULL,
  `stock_quantity` int NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `specifications` text,
  `rating` float DEFAULT NULL,
  `sales_count` int DEFAULT NULL,
  `is_featured` tinyint(1) DEFAULT NULL,
  `category_id` int NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'NVIDIA GeForce RTX 5070','Next-generation graphics performance for gaming and content creation.','NVIDIA',649.00,599.00,25,'rtx5070.jpg','CUDA Cores: High\nMemory: 12GB GDDR7\nInterface: PCIe 5.0\nOutputs: HDMI 2.1, DisplayPort 2.1',4.8,120,1,5,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(2,'Intel Core i7-14700K','High-performance desktop processor for creators and gamers.','Intel',419.00,389.00,40,'intel-i7.jpg','Cores: 20 (8P+12E)\nThreads: 28\nMax Turbo: 5.6 GHz\nSocket: LGA 1700',4.7,95,1,4,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(3,'AMD Ryzen 7 7800X3D','Gaming-focused CPU with 3D V-Cache technology.','AMD',449.00,NULL,35,'ryzen7.jpg','Cores: 8\nThreads: 16\nCache: 96MB\nSocket: AM5',4.9,140,1,4,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(4,'32GB DDR5 RAM Kit (2x16GB)','Fast DDR5 dual-channel memory kit for modern platforms.','Corsair',149.00,129.00,60,'ddr5-ram.jpg','Capacity: 32GB (2x16GB)\nSpeed: 6000 MT/s\nLatency: CL30\nRGB: Yes',4.6,200,1,6,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(5,'Samsung 990 PRO 2TB SSD','Ultra-fast NVMe SSD for gaming and professional workloads.','Samsung',199.00,179.00,50,'samsung-990.jpg','Capacity: 2TB\nInterface: PCIe 4.0 NVMe\nRead: up to 7450 MB/s\nWrite: up to 6900 MB/s',4.8,175,1,7,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(6,'ASUS TUF Gaming 27\" Monitor','27-inch IPS gaming monitor with high refresh rate.','ASUS',329.00,299.00,30,'asus-monitor.jpg','Size: 27 inch\nResolution: 2560x1440\nRefresh: 165Hz\nPanel: IPS',4.5,88,1,12,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(7,'Logitech G Pro X Superlight','Ultra-lightweight wireless gaming mouse.','Logitech',159.00,139.00,45,'logitech-mouse.jpg','Weight: <63g\nSensor: HERO 25K\nBattery: up to 70 hours\nConnectivity: LIGHTSPEED',4.7,210,0,14,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(8,'Mechanical Gaming Keyboard','RGB mechanical keyboard with tactile switches.','Razer',149.00,119.00,55,'mech-keyboard.jpg','Switches: Tactile\nBacklight: RGB\nLayout: Full-size\nCable: Detachable USB-C',4.6,160,0,13,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(9,'HyperX Cloud III Gaming Headset','Comfortable gaming headset with clear mic and spatial audio.','HyperX',99.00,79.00,70,'headset.jpg','Drivers: 53mm\nConnection: 3.5mm / USB\nMic: Noise-cancelling\nCompatible: PC, console',4.5,130,0,15,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(10,'850W Gold Modular Power Supply','Reliable fully modular 850W PSU with 80+ Gold efficiency.','Seasonic',149.00,NULL,40,'psu-850.jpg','Wattage: 850W\nEfficiency: 80+ Gold\nModular: Fully\nFan: 135mm',4.8,90,0,10,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(11,'ASUS ROG Strix G16 Laptop','Powerful gaming laptop with high-refresh display.','ASUS',1499.00,1399.00,15,'gaming-laptop.jpg','CPU: Intel Core i7\nGPU: RTX 4070\nRAM: 16GB\nStorage: 1TB SSD',4.6,55,1,1,'2026-09-09 12:08:35','2026-09-09 12:31:04'),(12,'TechStore Phantom Gaming PC','Prebuilt gaming PC optimized for 1440p and 4K gaming.','TechStore',1899.00,1749.00,10,'gaming-pc.jpg','CPU: Ryzen 7\nGPU: RTX 5070\nRAM: 32GB DDR5\nSSD: 2TB NVMe',4.9,42,1,3,'2026-09-09 12:08:35','2026-09-09 12:31:04');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `email` varchar(120) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `phone` varchar(40) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix_users_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Admin','admin@techstore.com','scrypt:32768:8:1$RbN1MAMmslPuqIEF$0cc8b6d3f89ddf13db06647af831e6e9a7bfde57805100f9df2c9b43ca0441c4441ba820833e3877a5136cb3fae1245fb037aa0efdd659f57a8fb501e7877db8','admin',1,NULL,'2026-09-09 12:08:35'),(2,'Demo Customer','customer@techstore.com','scrypt:32768:8:1$0ADyCYRovAr1GD44$fdc01be1bd2b450ec41d09b0fea76b8093fa7ce677e50aaf209ff0bd3dbd5479be4bcc05b23fa8112e546a5f27ac418a1a7ae11529da10b3db6ac7bc992d1e61','customer',1,NULL,'2026-09-09 12:08:35');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-10 19:10:39
