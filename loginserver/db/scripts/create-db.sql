DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `login` VARCHAR(45) NOT NULL default '',
  `password` VARCHAR(45),
  `email` varchar(255) DEFAULT NULL,
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastactive` bigint(13) unsigned NOT NULL DEFAULT '0',
  `accessLevel` TINYINT NOT NULL DEFAULT 0,
  `lastIP` VARCHAR(32) NULL DEFAULT NULL,
  `lastServer` TINYINT DEFAULT 1,
  PRIMARY KEY (`login`)
);

DROP TABLE IF EXISTS `gameservers`;
CREATE TABLE `gameservers` (
  `server_id` int(11) NOT NULL DEFAULT '0',
  `hexid` varchar(50) NOT NULL DEFAULT '',
  `host` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`server_id`)
);