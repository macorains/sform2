# --- !Ups
ALTER TABLE d_form
ADD COLUMN form_index INT NOT NULL AFTER hashed_id,
ADD COLUMN name VARCHAR(255) NOT NULL AFTER form_index,
ADD COLUMN title VARCHAR(255) NOT NULL AFTER name,
ADD COLUMN status INT NOT NULL AFTER title,
ADD COLUMN cancel_url VARCHAR(255) NOT NULL AFTER status,
ADD COLUMN complete_url VARCHAR(255) NOT NULL AFTER cancel_url,
ADD COLUMN input_header LONGTEXT NULL AFTER complete_url,
ADD COLUMN confirm_header LONGTEXT NULL AFTER input_header,
ADD COLUMN complete_text LONGTEXT NULL AFTER confirm_header,
ADD COLUMN close_text LONGTEXT NULL AFTER complete_text;

# --- !Downs
ALTER TABLE d_form
DROP COLUMN form_index,
DROP COLUMN name,
DROP COLUMN title,
DROP COLUMN status,
DROP COLUMN cancel_url,
DROP COLUMN complete_url,
DROP COLUMN input_header,
DROP COLUMN confirm_header,
DROP COLUMN complete_text,
DROP COLUMN close_text
