# --- !Ups
CREATE TABLE `d_form_col_validation` (
  `id` INT NOT NULL,
  `form_col_id` INT NOT NULL,
  `form_id` INT NOT NULL,
  `max_value` INT NULL,
  `min_value` INT NULL,
  `max_length` INT NULL,
  `min_length` INT NULL,
  `input_type` INT NOT NULL,
  `user_group` VARCHAR(30) NOT NULL,
  `created_user` VARCHAR(45) NOT NULL,
  `modified_user` VARCHAR(45) NOT NULL,
  `created` VARCHAR(45) NOT NULL,
  `modified` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`, `form_col_id`, `form_id`),
  INDEX `idx_user_group` (`user_group` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

# --- !Downs
DROP TABLE `d_form_col_validation`;
