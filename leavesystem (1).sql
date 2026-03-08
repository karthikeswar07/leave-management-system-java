-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 07, 2026 at 09:29 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `leavesystem`
--

-- --------------------------------------------------------

--
-- Table structure for table `holidays`
--

CREATE TABLE `holidays` (
  `id` int(11) NOT NULL,
  `holiday_date` date DEFAULT NULL,
  `holiday_name` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `holidays`
--

INSERT INTO `holidays` (`id`, `holiday_date`, `holiday_name`) VALUES
(1, '2026-01-10', 'Holiday1'),
(2, '2026-02-14', 'Holiday2'),
(3, '2026-03-18', 'Holiday3'),
(4, '2026-04-09', 'Holiday4'),
(5, '2026-05-21', 'Holiday5'),
(6, '2026-06-11', 'Holiday6'),
(7, '2026-07-19', 'Holiday7'),
(8, '2026-08-23', 'Holiday8'),
(9, '2026-09-12', 'Holiday9'),
(10, '2026-10-05', 'Holiday10');

-- --------------------------------------------------------

--
-- Table structure for table `leaves`
--

CREATE TABLE `leaves` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `from_date` date DEFAULT NULL,
  `to_date` date DEFAULT NULL,
  `reason` text DEFAULT NULL,
  `status` varchar(10) DEFAULT 'Pending',
  `reject_reason` varchar(255) DEFAULT NULL,
  `notified` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `leaves`
--

INSERT INTO `leaves` (`id`, `user_id`, `from_date`, `to_date`, `reason`, `status`, `reject_reason`, `notified`) VALUES
(1, 2, '2026-02-12', '2026-02-15', 'Brother\'s marriage', 'Approved', NULL, 0),
(2, 3, '2026-02-12', '2026-02-20', 'fever', 'Rejected', NULL, 0),
(3, 2, '2026-02-12', '2026-02-14', 'sick leave', 'Approved', NULL, 0),
(4, 3, '2026-02-12', '2026-02-14', 'fever', 'Approved', NULL, 0),
(5, 2, '2026-02-23', '2026-02-25', 'gradpa death', 'Rejected', NULL, 1),
(6, 2, '2026-03-04', '2026-03-06', 'fever', 'Approved', NULL, 0),
(7, 2, '2026-03-05', '2026-03-05', 'nrml', 'Rejected', 'many leaves', 1),
(8, 2, '2026-03-06', '2026-03-06', 'aappap', 'Cancelled', NULL, 0),
(9, 2, '2026-04-06', '2026-04-10', 'xyz', 'Approved', NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `role` varchar(10) DEFAULT NULL,
  `leave_balance` int(11) DEFAULT 10
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `leave_balance`) VALUES
(1, 'admin', 'admin', 'Admin', 10),
(2, 'employee', 'employee', 'Employee', -1),
(3, 'teja', '123', 'Employee', 7);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `holidays`
--
ALTER TABLE `holidays`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `leaves`
--
ALTER TABLE `leaves`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `holidays`
--
ALTER TABLE `holidays`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `leaves`
--
ALTER TABLE `leaves`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `leaves`
--
ALTER TABLE `leaves`
  ADD CONSTRAINT `leaves_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
