# --- !Ups
ALTER TABLE D_FORM_COL ADD CONSTRAINT FK_D_FORM_COL_FORM_ID FOREIGN KEY (FORM_ID) REFERENCES D_FORM (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_COL_SELECT ADD CONSTRAINT FK_D_FORM_COL_SELECT_FORM_ID FOREIGN KEY (FORM_ID) REFERENCES D_FORM (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_COL_SELECT ADD CONSTRAINT FK_D_FORM_COL_SELECT_FORM_COL_ID FOREIGN KEY (FORM_COL_ID) REFERENCES D_FORM_COL (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_COL_VALIDATION ADD CONSTRAINT FK_D_FORM_COL_VALIDATION_FORM_ID FOREIGN KEY (FORM_ID) REFERENCES D_FORM (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_COL_VALIDATION ADD CONSTRAINT FK_D_FORM_COL_VALIDATION_FORM_COL_ID FOREIGN KEY (FORM_COL_ID) REFERENCES D_FORM_COL (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_TRANSFER_TASK ADD CONSTRAINT FK_D_FORM_TRANSFER_TASK_FORM_ID FOREIGN KEY (FORM_ID) REFERENCES D_FORM (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_TRANSFER_TASK ADD CONSTRAINT FK_D_FORM_TRANSFER_TASK_FORM_TRANSFER_CONFIG_ID FOREIGN KEY (TRANSFER_CONFIG_ID) REFERENCES D_TRANSFER_CONFIG (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_TRANSFER_TASK_CONDITION ADD CONSTRAINT FK_D_FORM_TRANSFER_TASK_CONDITION_FORM_TRANSFER_TASK_ID FOREIGN KEY (FORM_TRANSFER_TASK_ID) REFERENCES D_FORM_TRANSFER_TASK (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_TRANSFER_TASK_MAIL ADD CONSTRAINT FK_D_FORM_TRANSFER_TASK_MAIL_FORM_TRANSFER_TASK_ID FOREIGN KEY (FORM_TRANSFER_TASK_ID) REFERENCES D_FORM_TRANSFER_TASK (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE ADD CONSTRAINT FK_D_FORM_TRANSFER_TASK_SALESFORCE_FORM_TRANSFER_TASK_ID FOREIGN KEY (FORM_TRANSFER_TASK_ID) REFERENCES D_FORM_TRANSFER_TASK (ID) ON DELETE CASCADE ON UPDATE CASCADE ;
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE_FIELD ADD CONSTRAINT FK_D_FORM_T__TASK_S__FIELD_FORM_T__TASK_S__ID FOREIGN KEY (FORM_TRANSFER_TASK_SALESFORCE_ID) REFERENCES  D_FORM_TRANSFER_TASK_SALESFORCE (ID) ON DELETE CASCADE ON UPDATE CASCADE ;

# --- !Downs
ALTER TABLE D_FORM_COL DROP FOREIGN KEY FK_D_FORM_COL_FORM_ID ;
ALTER TABLE D_FORM_COL_SELECT DROP FOREIGN KEY FK_D_FORM_COL_SELECT_FORM_ID ;
ALTER TABLE D_FORM_COL_SELECT DROP FOREIGN KEY FK_D_FORM_COL_SELECT_FORM_COL_ID ;
ALTER TABLE D_FORM_COL_VALIDATION DROP FOREIGN KEY FK_D_FORM_COL_VALIDATION_FORM_ID ;
ALTER TABLE D_FORM_COL_VALIDATION DROP FOREIGN KEY FK_D_FORM_COL_VALIDATION_FORM_COL_ID ;
ALTER TABLE D_FORM_TRANSFER_TASK DROP FOREIGN KEY FK_D_FORM_TRANSFER_TASK_FORM_ID ;
ALTER TABLE D_FORM_TRANSFER_TASK DROP FOREIGN KEY FK_D_FORM_TRANSFER_TASK_FORM_TRANSFER_CONFIG_ID ;
ALTER TABLE D_FORM_TRANSFER_TASK_CONDITION DROP FOREIGN KEY FK_D_FORM_TRANSFER_TASK_CONDITION_FORM_TRANSFER_TASK_ID ;
ALTER TABLE D_FORM_TRANSFER_TASK_MAIL DROP FOREIGN KEY FK_D_FORM_TRANSFER_TASK_MAIL_FORM_TRANSFER_TASK_ID ;
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE DROP FOREIGN KEY FK_D_FORM_TRANSFER_TASK_SALESFORCE_FORM_TRANSFER_TASK_ID ;
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE_FIELD DROP FOREIGN KEY  FK_D_FORM_T__TASK_S__FIELD_FORM_T__TASK_S__ID ;
