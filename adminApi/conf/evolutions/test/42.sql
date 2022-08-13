# --- !Ups
CREATE TABLE `users`
(
  `id` varchar(255),
  `username` varchar(255),
  `password` varchar(255),
  `linkedid` varchar(255),
  `serializedprofile` varchar(10000)
);

ALTER TABLE `users`
	ADD PRIMARY KEY (`id`),
	ADD KEY `username` (`username`),
	ADD KEY `linkedid` (`linkedid`);

# --- !Downs
DROP TABLE `users`;
