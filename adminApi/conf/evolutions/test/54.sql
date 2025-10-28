# --- !Ups
drop table m_authinfo;

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


