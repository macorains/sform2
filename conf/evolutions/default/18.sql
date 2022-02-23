# --- !Ups
ALTER TABLE m_userinfo
ADD COLUMN role VARCHAR(30) AFTER user_group

# --- !Downs
ALTER TABLE m_userinfo DROP COLUMN ;role