CREATE DATABASE IF NOT EXISTS `sformtest`;
CREATE USER 'sformtest'@'%' identified by 'sformtest';
GRANT ALL ON sformtest.* TO 'sformtest'@'%';
