# --- !Ups
ALTER TABLE d_form
ADD COLUMN user_group VARCHAR(30) AFTER form_data;
ALTER TABLE d_postdata
ADD COLUMN user_group VARCHAR(30) AFTER postdata;
ALTER TABLE d_postdata
ADD COLUMN created_user VARCHAR(45) AFTER user_group;
ALTER TABLE d_postdata
ADD COLUMN modified_user VARCHAR(45) AFTER created;
ALTER TABLE d_transfer_detail_log
ADD COLUMN user_group VARCHAR(30) AFTER message;
ALTER TABLE d_transfer_detail_log
ADD COLUMN created_user VARCHAR(45) AFTER user_group;
ALTER TABLE d_transfer_detail_log
ADD COLUMN modified_user VARCHAR(45) AFTER created;
ALTER TABLE d_transfer_log
ADD COLUMN user_group VARCHAR(30) AFTER result_data;
ALTER TABLE d_transfer_log
ADD COLUMN created_user VARCHAR(45) AFTER user_group;
ALTER TABLE d_transfer_log
ADD COLUMN modified_user VARCHAR(45) AFTER created;
ALTER TABLE d_transfer_tasks
ADD COLUMN user_group VARCHAR(30) AFTER config;
ALTER TABLE d_transfer_tasks
ADD COLUMN created_user VARCHAR(45) AFTER user_group;
ALTER TABLE d_transfer_tasks
ADD COLUMN modified_user VARCHAR(45) AFTER created;
ALTER TABLE m_transfers
ADD COLUMN user_group VARCHAR(30) AFTER config;
ALTER TABLE m_transfers
ADD COLUMN created_user VARCHAR(45) AFTER user_group;
ALTER TABLE m_transfers
ADD COLUMN modified_user VARCHAR(45) AFTER created;


# --- !Downs
ALTER TABLE d_form DROP COLUMN user_group;
ALTER TABLE d_postdata DROP COLUMN user_group;
ALTER TABLE d_postdata DROP COLUMN created_user;
ALTER TABLE d_postdata DROP COLUMN modified_user;
ALTER TABLE d_transfer_detail_log DROP COLUMN user_group;
ALTER TABLE d_transfer_detail_log DROP COLUMN created_user;
ALTER TABLE d_transfer_detail_log DROP COLUMN modified_user;
ALTER TABLE d_transfer_log DROP COLUMN user_group;
ALTER TABLE d_transfer_log DROP COLUMN created_user;
ALTER TABLE d_transfer_log DROP COLUMN modified_user;
ALTER TABLE d_transfer_tasks DROP COLUMN user_group;
ALTER TABLE d_transfer_tasks DROP COLUMN created_user;
ALTER TABLE d_transfer_tasks DROP COLUMN modified_user;
ALTER TABLE m_transfers DROP COLUMN user_group;
ALTER TABLE m_transfers DROP COLUMN created_user;
ALTER TABLE m_transfers DROP COLUMN modified_user;
