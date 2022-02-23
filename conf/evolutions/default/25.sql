# --- !Ups
ALTER TABLE d_form_col_validation
CHANGE COLUMN `id` `id` INT NOT NULL AUTO_INCREMENT ;

# --- !Downs
ALTER TABLE d_form_col_validation
CHANGE COLUMN `id` `id` INT NOT NULL ;
