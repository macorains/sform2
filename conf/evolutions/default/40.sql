# --- !Ups
ALTER TABLE d_form_col ADD CONSTRAINT fk_d_form_col_form_id FOREIGN KEY (form_id) REFERENCES d_form (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_col_select ADD CONSTRAINT fk_d_form_col_select_form_id FOREIGN KEY (form_id) REFERENCES d_form (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_col_select ADD CONSTRAINT fk_d_form_col_select_form_col_id FOREIGN KEY (form_col_id) REFERENCES d_form_col (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_col_validation ADD CONSTRAINT fk_d_form_col_validation_form_id FOREIGN KEY (form_id) REFERENCES d_form (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_col_validation ADD CONSTRAINT fk_d_form_col_validation_form_col_id FOREIGN KEY (form_col_id) REFERENCES d_form_col (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_transfer_task ADD CONSTRAINT fk_d_form_transfer_task_form_id FOREIGN KEY (form_id) REFERENCES d_form (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_transfer_task ADD CONSTRAINT fk_d_form_transfer_task_form_transfer_config_id FOREIGN KEY (transfer_config_id) REFERENCES d_transfer_config (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_transfer_task_condition ADD CONSTRAINT fk_d_form_transfer_task_condition_form_transfer_task_id FOREIGN KEY (form_transfer_task_id) REFERENCES d_form_transfer_task (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_transfer_task_mail ADD CONSTRAINT fk_d_form_transfer_task_mail_form_transfer_task_id FOREIGN KEY (form_transfer_task_id) REFERENCES d_form_transfer_task (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_transfer_task_salesforce ADD CONSTRAINT fk_d_form_transfer_task_salesforce_form_transfer_task_id FOREIGN KEY (form_transfer_task_id) REFERENCES d_form_transfer_task (id) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE d_form_transfer_task_salesforce_field ADD CONSTRAINT fk_d_form_t__task_s__field_form_t__task_s__id FOREIGN KEY (form_transfer_task_salesforce_id) REFERENCES  d_form_transfer_task_salesforce (id) ON DELETE CASCADE ON UPDATE CASCADE ;

# --- !Downs
ALTER TABLE d_form_col DROP FOREIGN KEY fk_d_form_col_form_id ;
ALTER TABLE d_form_col_select DROP FOREIGN KEY fk_d_form_col_select_form_id ;
ALTER TABLE d_form_col_select DROP FOREIGN KEY fk_d_form_col_select_form_col_id ;
ALTER TABLE d_form_col_validation DROP FOREIGN KEY fk_d_form_col_validation_form_id ;
ALTER TABLE d_form_col_validation DROP FOREIGN KEY fk_d_form_col_validation_form_col_id ;
ALTER TABLE d_form_transfer_task DROP FOREIGN KEY fk_d_form_transfer_task_form_id ;
ALTER TABLE d_form_transfer_task DROP FOREIGN KEY fk_d_form_transfer_task_form_transfer_config_id ;
ALTER TABLE d_form_transfer_task_condition DROP FOREIGN KEY fk_d_form_transfer_task_condition_form_transfer_task_id ;
ALTER TABLE d_form_transfer_task_mail DROP FOREIGN KEY fk_d_form_transfer_task_mail_form_transfer_task_id ;
ALTER TABLE d_form_transfer_task_salesforce DROP FOREIGN KEY fk_d_form_transfer_task_salesforce_form_transfer_task_id ;
ALTER TABLE d_form_transfer_task_salesforce_field DROP FOREIGN KEY  fk_d_form_t__task_s__field_form_t__task_s__id ;

