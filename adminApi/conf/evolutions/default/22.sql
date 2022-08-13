# --- !Ups
ALTER TABLE `d_form_col`
CHANGE COLUMN `created` `created` DATETIME NOT NULL ,
CHANGE COLUMN `modified` `modified` DATETIME NOT NULL ;

CREATE TABLE `d_form_col_select` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `form_col_id` INT NOT NULL,
  `form_id` INT NOT NULL,
  `select_index` INT NOT NULL,
  `select_name` VARCHAR(128) NOT NULL,
  `select_value` VARCHAR(45) NOT NULL,
  `is_default` TINYINT(1) NOT NULL,
  `edit_style` VARCHAR(255) NULL,
  `view_style` VARCHAR(255) NULL,
  `user_group` VARCHAR(30) NOT NULL,
  `created_user` VARCHAR(45) NOT NULL,
  `modified_user` VARCHAR(45) NOT NULL,
  `created` DATETIME NOT NULL,
  `modified` DATETIME NOT NULL,
  PRIMARY KEY (`id`, `form_col_id`, `form_id`),
  INDEX `idx_user_group` (`user_group` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

# --- !Downs
DROP TABLE `d_form_col_select`;

ALTER TABLE `d_form_col`
CHANGE COLUMN `created` `created` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `modified` `modified` VARCHAR(45) NOT NULL ;
