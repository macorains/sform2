# --- !Ups
CREATE TABLE `d_apitoken`
(
    `id` VARCHAR(255) NOT NULL,
    `username` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `expiry` DATETIME NOT NULL,
    `created` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_api_token_username_password` (`username` ASC, `password` ASC),
    INDEX `idx_api_token_expiry` (`expiry` ASC)
);

# --- !Downs
DROP TABLE `d_apitoken`;
