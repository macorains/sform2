# --- !Ups
alter table `d_apitoken`
    add column `linkedid` varchar(255) null after `id`;

# --- !Downs
alter table `d_apitoken`
    drop column `linkedid`;
