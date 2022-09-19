# --- !Ups
ALTER TABLE `d_transfer_config_salesforce`
    ADD `api_url` VARCHAR(255) NOT NULL AFTER `transfer_config_id`;

# --- !Downs
ALTER TABLE `d_transfer_config_salesforce`
    DROP COLUMN `api_url` VARCHAR(255);
