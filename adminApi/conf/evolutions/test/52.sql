# --- !Ups
ALTER TABLE `d_transfer_config_salesforce`
    ADD `api_version` VARCHAR(10) NOT NULL AFTER `api_url`;
ALTER TABLE `d_transfer_config_salesforce`
    CHANGE `api_url` `sf_domain` VARCHAR(255) NOT NULL;

# --- !Downs
ALTER TABLE `d_transfer_config_salesforce`
    CHANGE `sf_domain` `api_url` VARCHAR(255) NOT NULL;
ALTER TABLE `d_transfer_config_salesforce`
DROP COLUMN `api_version`;
