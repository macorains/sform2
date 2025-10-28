# --- !Ups
ALTER TABLE d_apitoken MODIFY COLUMN created_user VARCHAR(45) NOT NULL;

# --- !Downs
ALTER TABLE d_apitoken MODIFY COLUMN created_user VARCHAR(30) NOT NULL;

