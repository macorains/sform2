# --- !Ups

CREATE TABLE `M_USER` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UID` varchar(45) NOT NULL,
  `NAME` varchar(45) DEFAULT NULL,
  `EMAIL` varchar(100) NOT NULL,
  `STATUS` int(11) NOT NULL,
  `PASSWORD` varchar(128) DEFAULT NULL,
  `CREATED` datetime DEFAULT NULL,
  `MODIFIED` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

# --- !Downs
  drop table dummy
