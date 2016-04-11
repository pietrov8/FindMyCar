-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Czas generowania: 11 Kwi 2016, 18:57
-- Wersja serwera: 5.6.21
-- Wersja PHP: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Baza danych: `FMC`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `uzytkownicy`
--

CREATE TABLE IF NOT EXISTS `uzytkownicy` (
`id` int(11) NOT NULL,
  `nazwa_uzytkownika` varchar(20) NOT NULL,
  `nick` varchar(20) NOT NULL,
  `haslo` varchar(32) NOT NULL,
  `email` varchar(40) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Zrzut danych tabeli `uzytkownicy`
--

INSERT INTO `uzytkownicy` (`id`, `nazwa_uzytkownika`, `nick`, `haslo`, `email`) VALUES
(2, 'Radek', 'Radek', '9f3a08745c23449a53fc05d68eda1e1b', 'radek@gmail.com'),
(3, 'Piotrek', 'Piotrek', 'd97cf1ea369bd3a052edd2286387de80', 'piotrek@gmail.com'),
(4, 'Micha', 'Micha', '916b5e380e76f7b10977c62242c6ff47', 'michal@gmail.com');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `znaczniki`
--

CREATE TABLE IF NOT EXISTS `znaczniki` (
  `id` int(11) NOT NULL,
  `nazwa` varchar(40) NOT NULL,
  `latitude` varchar(15) NOT NULL,
  `longitude` varchar(15) NOT NULL,
  `opis` varchar(40) NOT NULL,
  `data_utworzenia` datetime NOT NULL,
  `aktywny` tinyint(1) NOT NULL,
  `usuniety` tinyint(1) NOT NULL,
  `data_usuniecia` datetime NOT NULL,
  `id_wlasciciela` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Zrzut danych tabeli `znaczniki`
--

INSERT INTO `znaczniki` (`id`, `nazwa`, `latitude`, `longitude`, `opis`, `data_utworzenia`, `aktywny`, `usuniety`, `data_usuniecia`, `id_wlasciciela`) VALUES
(0, 'Samochod Radka', '51.175985', '22.5065135', 'Golf zostawiony pod domem', '2016-03-17 00:00:00', 1, 0, '0000-00-00 00:00:00', 2),
(1, 'Samochod Michala', '51.2699383', '22.561944', 'Vectra pod blokiem', '2016-02-10 17:20:00', 1, 0, '0000-00-00 00:00:00', 3),
(2, 'Samochod Piotrka', '51.2318427', '22.537969', 'Hania', '2016-03-29 12:00:00', 1, 0, '0000-00-00 00:00:00', 4),
(3, 'samochod Mariusza', '51.2449579', '22.5483803', 'Mercedes zaprakowany w pobli', '2016-03-01 00:00:00', 0, 0, '0000-00-00 00:00:00', 2),
(4, 'samochod Kasi', '51.2657834', '22.5685143', 'Opel zaprakowany w pobli', '2016-03-24 00:00:00', 1, 0, '0000-00-00 00:00:00', 3);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indexes for table `uzytkownicy`
--
ALTER TABLE `uzytkownicy`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `znaczniki`
--
ALTER TABLE `znaczniki`
 ADD PRIMARY KEY (`id`), ADD KEY `id_wlasciciela` (`id_wlasciciela`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT dla tabeli `uzytkownicy`
--
ALTER TABLE `uzytkownicy`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `uzytkownicy`
--
ALTER TABLE `uzytkownicy`
ADD CONSTRAINT `uzytkownicy_ibfk_1` FOREIGN KEY (`id`) REFERENCES `znaczniki` (`id_wlasciciela`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
