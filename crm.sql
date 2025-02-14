-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 14, 2025 at 06:13 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `crm`
--
CREATE DATABASE IF NOT EXISTS `crm` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `crm`;

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `assigned_user_id` bigint(20) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `birthday` bigint(20) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `job` varchar(255) DEFAULT NULL,
  `last_update` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `relationship_id` bigint(20) DEFAULT NULL,
  `source_id` bigint(20) DEFAULT NULL,
  `referrer_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `relationship_id` (`relationship_id`),
  KEY `code_2` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `relationships`
--

CREATE TABLE IF NOT EXISTS `relationships` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `color` varchar(255) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `last_update` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sources`
--

CREATE TABLE IF NOT EXISTS `sources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `last_update` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `code_2` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `birthday` bigint(20) NOT NULL,
  `gender` varchar(255) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  `sign` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_update` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `users_email` (`email`),
  KEY `users_username` (`username`) USING BTREE,
  KEY `users_username_email` (`username`,`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts` ADD FULLTEXT KEY `code` (`code`,`name`,`phone`);

--
-- Indexes for table `sources`
--
ALTER TABLE `sources` ADD FULLTEXT KEY `code` (`name`,`code`);

--
-- Indexes for table `users`
--
ALTER TABLE `users` ADD FULLTEXT KEY `search_username_name_fts` (`username`,`name`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
