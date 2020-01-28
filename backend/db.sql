-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 03, 2018 at 05:45 PM
-- Server version: 5.7.23
-- PHP Version: 7.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `db_tree_store`
--

-- --------------------------------------------------------

--
-- Table structure for table `goods`
--

CREATE TABLE `goods` (
                       `id` int(10) NOT NULL,
                       `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
                       `detail` text COLLATE utf8_unicode_ci NOT NULL,
                       `price` int(10) NOT NULL,
                       `picture` varchar(255) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `goods`
--

INSERT INTO `goods` (`id`, `name`, `detail`, `price`, `picture`) VALUES
(2, 'ต้นไม้จำลอง 1', 'เนื้อหาจำลองแบบเรียบๆ ที่ใช้กันในธุรกิจงานพิมพ์หรืองานเรียงพิมพ์', 76, 'tree-1.jpg'),
(3, 'ต้นไม้จำลอง 2', 'เนื้อหาจำลองแบบเรียบๆ ที่ใช้กันในธุรกิจงานพิมพ์หรืองานเรียงพิมพ์', 25, 'tree-2.jpg'),
(4, 'ต้นไม้จำลอง 3', 'เนื้อหาจำลองแบบเรียบๆ ที่ใช้กันในธุรกิจงานพิมพ์หรืองานเรียงพิมพ์', 60, 'tree-3.jpg'),
(5, 'ต้นไม้จำลอง 4', 'เนื้อหาจำลองแบบเรียบๆ ที่ใช้กันในธุรกิจงานพิมพ์หรืองานเรียงพิมพ์', 125, 'tree-4.jpg'),
(6, 'ต้นไม้จำลอง 5', 'เนื้อหาจำลองแบบเรียบๆ ที่ใช้กันในธุรกิจงานพิมพ์หรืองานเรียงพิมพ์', 35, 'tree-5.webp'),
(7, 'ต้นไม้จำลอง 6', 'เนื้อหาจำลองแบบเรียบๆ ที่ใช้กันในธุรกิจงานพิมพ์หรืองานเรียงพิมพ์', 90, 'tree-6.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
                         `id` int(10) NOT NULL,
                         `goods_id` int(10) NOT NULL,
                         `user_id` int(10) NOT NULL,
                         `amount` int(10) NOT NULL,
                         `picture` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
                         `date_time` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
                         `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`id`, `goods_id`, `user_id`, `amount`, `picture`, `date_time`, `status`) VALUES
(9, 2, 1, 13, 'IMG_20181130_0448498684378652281789837.jpg', '20/12', 1),
(10, 6, 1, 4, 'line_1543773841804.jpg', '4444', 2),
(13, 6, 999, 6, 'line_1543773841804.jpg', 'xxxx', 2);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                       `id` int(11) NOT NULL,
                       `username` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
                       `password` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
                       `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
                       `picture` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
                       `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `name`, `picture`, `status`) VALUES
(999, 'admin', '1234', 'วิสิฐ ภูศรี', '1.jpg', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `goods`
--
ALTER TABLE `goods`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `goods`
--
ALTER TABLE `goods`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1000;
