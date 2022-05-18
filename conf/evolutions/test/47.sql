# --- !Ups
ALTER TABLE `d_apitoken`
ADD COLUMN `created_user` VARCHAR(30) NOT NULL after `created`,
ADD COLUMN `user_group` VARCHAR(30) NOT NULL after `created_user` ;

# --- !Downs
ALTER TABLE `d_apitoken`
DROP COLUMN `user_group`,
DROP COLUMN `created_user` ;
