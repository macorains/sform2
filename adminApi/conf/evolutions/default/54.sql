# --- !Ups
drop table m_authinfo;
alter table d_authtoken add column created datetime after expiry;
alter table d_authtoken add column modified datetime after created;
alter table m_userinfo add column created datetime after deletable;
alter table m_userinfo add column modified datetime after created;
# --- !Downs
create table m_authinfo
(
    authinfo_id  int auto_increment primary key,
    provider_id  varchar(50) charset utf8  not null,
    provider_key varchar(50) charset utf8  not null,
    hasher       varchar(255) charset utf8 null,
    password     varchar(255) charset utf8 null,
    salt         varchar(255) charset utf8 null
)
    collate = utf8mb4_bin;
alter table d_authtoken drop column created;
alter table d_authtoken drop column modified;
alter table m_userinfo drop column created;
alter table m_userinfo drop column modified;



