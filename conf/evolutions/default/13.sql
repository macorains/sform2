# --- !Ups
ALTER TABLE d_transfer_tasks
ADD form_id INTEGER GENERATED ALWAYS AS (json_unquote(json_extract(`CONFIG`,'$.formId'))) VIRTUAL AFTER STATUS;

# --- !Downs
ALTER TABLE d_transfer_tasks DROP form_id;