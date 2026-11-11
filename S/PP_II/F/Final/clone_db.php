<?php
$pass = 'REMOVED';
$src = 'final_wp_mmt';
$dst = 'final_pp_ii_mmt';
$mysqli = new mysqli('127.0.0.1', 'root', $pass);
if ($mysqli->connect_error) { die($mysqli->connect_error); }
$mysqli->set_charset('utf8mb4');

// Get tables from source (skip migrations)
$tables = [];
$r = $mysqli->query("SHOW FULL TABLES FROM `$src` WHERE Table_type = 'BASE TABLE'");
while ($row = $r->fetch_row()) {
    if ($row[0] === 'migrations') continue;
    $tables[] = $row[0];
}
echo "Tables: " . implode(', ', $tables) . "\n";

// Disable FK
$mysqli->query("SET FOREIGN_KEY_CHECKS=0");
$mysqli->query("DROP DATABASE IF EXISTS `$dst`");
$mysqli->query("CREATE DATABASE `$dst` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
$mysqli->select_db($dst);

foreach ($tables as $t) {
    $create = $mysqli->query("SHOW CREATE TABLE `$src`.`$t`")->fetch_assoc();
    $sql = $create['Create Table'];
    if (!$mysqli->query($sql)) {
        echo "CREATE FAIL $t: " . $mysqli->error . "\n";
        continue;
    }
    echo "Created $t\n";
    // Copy data
    if (!$mysqli->query("INSERT INTO `$dst`.`$t` SELECT * FROM `$src`.`$t`")) {
        echo "DATA FAIL $t: " . $mysqli->error . "\n";
    } else {
        $c = $mysqli->query("SELECT COUNT(*) c FROM `$dst`.`$t`")->fetch_assoc()['c'];
        echo "  rows=$c\n";
    }
}

// Extra tables for PP_II (error_log, activity_logs) like teacher
$mysqli->query("CREATE TABLE IF NOT EXISTS error_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  status_code INT NOT NULL,
  path VARCHAR(500) NOT NULL,
  method VARCHAR(10) NOT NULL,
  ip VARCHAR(64) NOT NULL,
  message TEXT NULL,
  created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

$mysqli->query("CREATE TABLE IF NOT EXISTS activity_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  action VARCHAR(50) NOT NULL,
  module VARCHAR(80) NULL,
  table_name VARCHAR(80) NULL,
  record_id BIGINT NULL,
  description TEXT NULL,
  old_data JSON NULL,
  new_data JSON NULL,
  ip VARCHAR(64) NULL,
  created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

$mysqli->query("SET FOREIGN_KEY_CHECKS=1");
echo "DONE\n";

// Sample users check
$r = $mysqli->query("SELECT id, email, role FROM `$dst`.users LIMIT 10");
while ($row = $r->fetch_assoc()) {
    echo "user {$row['id']} {$row['email']} {$row['role']}\n";
}
