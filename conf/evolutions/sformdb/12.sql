# --- !Ups
CREATE TABLE `D_TRANSFER_DETAIL_LOG` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `POSTDATA_ID` int(11),
  `TRANSFER_TYPE_ID` int(11),
  `STATUS` int(11),
  `POSTDATA` json,
  `MODIFIED_POSTDATA` json,
  `RESULT` int(11),
  `MESSAGE` json,
  `CREATED` datetime DEFAULT NULL,
  `MODIFIED` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
drop table D_TRANSFER_DETAIL_LOG