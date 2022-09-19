# --- !Ups
ALTER TABLE `d_transfer_config_salesforce`
    ADD `sf_client_id` VARCHAR(255) NOT NULL AFTER `sf_password`;

ALTER TABLE `d_transfer_config_salesforce`
    ADD `sf_client_secret` VARCHAR(255) NOT NULL AFTER `sf_client_id`;

ALTER TABLE `d_transfer_config_salesforce`
    ADD `iv_user_name` VARCHAR(32) NOT NULL AFTER `sf_client_secret`;

ALTER TABLE `d_transfer_config_salesforce`
    ADD `iv_password` VARCHAR(32) NOT NULL AFTER `iv_user_name`;

ALTER TABLE `d_transfer_config_salesforce`
    ADD `iv_client_id` VARCHAR(32) NOT NULL AFTER `iv_password`;

ALTER TABLE `d_transfer_config_salesforce`
    ADD `iv_client_secret` VARCHAR(32) NOT NULL AFTER `iv_client_id`;

ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `sf_security_token`;

# --- !Downs
ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `sf_client_id`;

ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `sf_client_secret`;

ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `iv_user_name`;

ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `iv_password`;

ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `iv_client_id`;

ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `iv_client_secret`;

ALTER TABLE `d_transfer_config_salesforce`
    ADD `sf_security_token` VARCHAR(255) NOT NULL AFTER `sf_user_name`,
