# --- !Ups

ALTER TABLE d_transfer_config_mail RENAME TO d_transfer_config_ses_mail;
ALTER TABLE d_form_transfer_task_mail RENAME TO d_form_transfer_task_ses_mail;

# --- !Downs

ALTER TABLE d_transfer_config_ses_mail RENAME TO d_transfer_config_mail;
ALTER TABLE d_form_transfer_task_ses_mail RENAME TO d_form_transfer_task_mail;
