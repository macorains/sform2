# --- !Ups
CREATE TABLE `d_form_col` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `form_id` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `col_id` VARCHAR(45) NOT NULL,
  `col_index` INT NOT NULL,
  `col_type` INT NOT NULL,
  `default_value` VARCHAR(255) NULL,
  `user_group` varchar(30) not null,
  `created_user` VARCHAR(45) NOT NULL,
  `modified_user` VARCHAR(45) NOT NULL,
  `created` VARCHAR(45) NOT NULL,
  `modified` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`, `form_id`),
  INDEX `idx_user_group` (`user_group` ASC));

# --- !Downs
DROP TABLE `d_form_col`;
