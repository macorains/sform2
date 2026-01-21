# --- !Ups
create table d_apitoken
(
    id           varchar(255) not null
        primary key,
    linkedid     varchar(255) null,
    username     varchar(255) not null,
    password     varchar(255) not null,
    expiry       datetime     not null,
    created      datetime     not null,
    created_user varchar(45)  not null,
    user_group   varchar(30)  not null
);

create index idx_api_token_expiry
    on d_apitoken (expiry);

create index idx_api_token_username_password
    on d_apitoken (username, password);

create table d_authtoken
(
    id       varchar(50) charset utf8 not null,
    user_id  varchar(50) charset utf8 not null,
    expiry   datetime                 null,
    created  datetime                 null,
    modified datetime                 null
)
    collate = utf8mb4_bin;

create table d_form
(
    id             bigint auto_increment
        primary key,
    hashed_id      varchar(45) charset utf8  not null,
    form_index     int                       not null,
    name           varchar(255) charset utf8 not null,
    title          varchar(255) charset utf8 not null,
    status         int                       not null,
    cancel_url     varchar(255) charset utf8 not null,
    complete_url   varchar(255) charset utf8 not null,
    input_header   longtext charset utf8     null,
    confirm_header longtext charset utf8     null,
    complete_text  longtext charset utf8     null,
    close_text     longtext charset utf8     null,
    form_data      json                      null,
    user_group     varchar(30) charset utf8  null,
    created_user   varchar(45) charset utf8  null,
    modified_user  varchar(45) charset utf8  null,
    created        datetime                  null,
    modified       datetime                  null
)
    collate = utf8mb4_bin;

create table d_form_col
(
    id            bigint auto_increment,
    form_id       bigint                       not null,
    name          varchar(255) charset utf8mb4 not null,
    col_id        varchar(45) charset utf8mb4  not null,
    col_index     int                          not null,
    col_type      int                          not null,
    default_value varchar(255) charset utf8mb4 null,
    user_group    varchar(30) charset utf8mb4  not null,
    created_user  varchar(45) charset utf8mb4  not null,
    modified_user varchar(45) charset utf8mb4  not null,
    created       datetime                     not null,
    modified      datetime                     not null,
    primary key (id, form_id),
    constraint fk_d_form_col_form_id
        foreign key (form_id) references d_form (id)
            on update cascade on delete cascade
)
    collate = utf8mb4_bin;

create index idx_user_group
    on d_form_col (user_group);

create table d_form_col_select
(
    id            bigint auto_increment,
    form_col_id   bigint                       not null,
    form_id       bigint                       not null,
    select_index  int                          not null,
    select_name   varchar(128) charset utf8mb4 not null,
    select_value  varchar(45) charset utf8mb4  not null,
    is_default    tinyint(1)                   not null,
    edit_style    varchar(255) charset utf8mb4 null,
    view_style    varchar(255) charset utf8mb4 null,
    user_group    varchar(30) charset utf8mb4  not null,
    created_user  varchar(45) charset utf8mb4  not null,
    modified_user varchar(45) charset utf8mb4  not null,
    created       datetime                     not null,
    modified      datetime                     not null,
    primary key (id, form_col_id, form_id),
    constraint fk_d_form_col_select_form_col_id
        foreign key (form_col_id) references d_form_col (id)
            on update cascade on delete cascade,
    constraint fk_d_form_col_select_form_id
        foreign key (form_id) references d_form (id)
            on update cascade on delete cascade
)
    collate = utf8mb4_bin;

create index idx_user_group
    on d_form_col_select (user_group);

create table d_form_col_validation
(
    id            bigint auto_increment,
    form_col_id   bigint                      not null,
    form_id       bigint                      not null,
    max_value     int                         null,
    min_value     int                         null,
    max_length    int                         null,
    min_length    int                         null,
    input_type    int                         not null,
    required      tinyint(1)                  null,
    user_group    varchar(30) charset utf8mb4 not null,
    created_user  varchar(45) charset utf8mb4 not null,
    modified_user varchar(45) charset utf8mb4 not null,
    created       varchar(45) charset utf8mb4 not null,
    modified      varchar(45) charset utf8mb4 not null,
    primary key (id, form_col_id, form_id),
    constraint fk_d_form_col_validation_form_col_id
        foreign key (form_col_id) references d_form_col (id)
            on update cascade on delete cascade,
    constraint fk_d_form_col_validation_form_id
        foreign key (form_id) references d_form (id)
            on update cascade on delete cascade
)
    collate = utf8mb4_bin;

create index idx_user_group
    on d_form_col_validation (user_group);

create table d_transfer_config
(
    id            bigint auto_increment
        primary key,
    type_code     varchar(45)  not null,
    config_index  int          not null,
    name          varchar(255) not null,
    status        int          not null,
    user_group    varchar(30)  not null,
    created_user  varchar(45)  not null,
    modified_user varchar(45)  not null,
    created       datetime     not null,
    modified      datetime     not null
);

create index idx_user_group
    on d_transfer_config (user_group);

create table d_form_transfer_task
(
    id                 bigint auto_increment
        primary key,
    transfer_config_id bigint       not null,
    form_id            bigint       not null,
    task_index         int          not null,
    name               varchar(255) null,
    user_group         varchar(30)  not null,
    created_user       varchar(45)  not null,
    modified_user      varchar(45)  not null,
    created            datetime     not null,
    modified           datetime     not null,
    constraint fk_d_form_transfer_task_form_id
        foreign key (form_id) references d_form (id)
            on update cascade on delete cascade,
    constraint fk_d_form_transfer_task_form_transfer_config_id
        foreign key (transfer_config_id) references d_transfer_config (id)
            on update cascade on delete cascade
);

create index idx_form_id
    on d_form_transfer_task (form_id);

create table d_form_transfer_task_condition
(
    id                    bigint auto_increment
        primary key,
    form_transfer_task_id bigint       not null,
    form_id               bigint       not null,
    form_col_id           bigint       not null,
    operator              varchar(10)  not null,
    cond_value            varchar(255) not null,
    user_group            varchar(30)  not null,
    created_user          varchar(45)  not null,
    modified_user         varchar(45)  not null,
    created               datetime     not null,
    modified              datetime     not null,
    constraint fk_d_form_transfer_task_condition_form_transfer_task_id
        foreign key (form_transfer_task_id) references d_form_transfer_task (id)
            on update cascade on delete cascade
);

create index idx_form_transfer_task_id
    on d_form_transfer_task_condition (form_transfer_task_id);

create table d_form_transfer_task_mail
(
    id                    bigint auto_increment
        primary key,
    form_transfer_task_id bigint       not null,
    from_address_id       bigint       not null,
    to_address            varchar(255) null,
    to_address_id         bigint       null,
    to_address_field      varchar(255) null,
    cc_address            varchar(255) null,
    cc_address_id         bigint       null,
    cc_address_field      varchar(255) null,
    bcc_address_id        bigint       null,
    replyto_address_id    bigint       null,
    subject               varchar(255) not null,
    body                  text         not null,
    user_group            varchar(30)  not null,
    created_user          varchar(45)  not null,
    modified_user         varchar(45)  not null,
    created               datetime     not null,
    modified              datetime     not null,
    constraint fk_d_form_transfer_task_mail_form_transfer_task_id
        foreign key (form_transfer_task_id) references d_form_transfer_task (id)
            on update cascade on delete cascade
);

create index idx_transfer_task_id
    on d_form_transfer_task_mail (form_transfer_task_id);

create table d_form_transfer_task_salesforce
(
    id                    bigint auto_increment
        primary key,
    form_transfer_task_id bigint       not null,
    object_name           varchar(255) not null,
    user_group            varchar(30)  not null,
    created_user          varchar(45)  not null,
    modified_user         varchar(45)  not null,
    created               datetime     not null,
    modified              datetime     not null,
    constraint fk_d_form_transfer_task_salesforce_form_transfer_task_id
        foreign key (form_transfer_task_id) references d_form_transfer_task (id)
            on update cascade on delete cascade
);

create index idx_transfer_task_id
    on d_form_transfer_task_salesforce (form_transfer_task_id);

create table d_form_transfer_task_salesforce_field
(
    id                               bigint auto_increment
        primary key,
    form_transfer_task_salesforce_id bigint       not null,
    form_column_id                   varchar(45)  not null,
    field_name                       varchar(255) null,
    user_group                       varchar(30)  not null,
    created_user                     varchar(45)  not null,
    modified_user                    varchar(45)  not null,
    created                          datetime     not null,
    modified                         datetime     not null,
    constraint fk_d_form_t__task_s__field_form_t__task_s__id
        foreign key (form_transfer_task_salesforce_id) references d_form_transfer_task_salesforce (id)
            on update cascade on delete cascade
);

create index idx_transfer_task_id
    on d_form_transfer_task_salesforce_field (form_transfer_task_salesforce_id);

create table d_postdata
(
    postdata_id    int auto_increment
        primary key,
    form_hashed_id varchar(45) charset utf8 not null,
    postdata       json                     null,
    user_group     varchar(30) charset utf8 null,
    created_user   varchar(45) charset utf8 null,
    created        datetime                 null,
    modified_user  varchar(45) charset utf8 null,
    modified       datetime                 null
)
    collate = utf8mb4_bin;

create table d_transfer_config_mail
(
    id                 bigint auto_increment
        primary key,
    transfer_config_id bigint      not null,
    use_cc             tinyint(1)  not null,
    use_bcc            tinyint(1)  not null,
    use_replyto        tinyint(1)  not null,
    user_group         varchar(30) not null,
    created_user       varchar(45) not null,
    modified_user      varchar(45) not null,
    created            datetime    not null,
    modified           datetime    not null
);

create index idx_transfer_config_id
    on d_transfer_config_mail (transfer_config_id);

create table d_transfer_config_mail_address
(
    id                      bigint auto_increment
        primary key,
    transfer_config_mail_id bigint       not null,
    address_index           int          not null,
    name                    varchar(255) not null,
    address                 varchar(255) not null,
    user_group              varchar(30)  not null,
    created_user            varchar(45)  not null,
    modified_user           varchar(45)  not null,
    created                 datetime     not null,
    modified                datetime     not null
);

create index idx_transfer_config_mail_id
    on d_transfer_config_mail_address (transfer_config_mail_id);

create table d_transfer_config_salesforce
(
    id                 bigint auto_increment
        primary key,
    transfer_config_id bigint       not null,
    sf_domain          varchar(255) not null,
    api_version        varchar(10)  not null,
    sf_user_name       varchar(255) not null,
    sf_password        varchar(255) not null,
    sf_client_id       varchar(255) not null,
    sf_client_secret   varchar(255) not null,
    iv_user_name       varchar(32)  not null,
    iv_password        varchar(32)  not null,
    iv_client_id       varchar(32)  not null,
    iv_client_secret   varchar(32)  not null,
    user_group         varchar(30)  not null,
    created_user       varchar(45)  not null,
    modified_user      varchar(45)  not null,
    created            datetime     not null,
    modified           datetime     not null
);

create index idx_transfer_config_id
    on d_transfer_config_salesforce (transfer_config_id);

create table d_transfer_config_salesforce_object
(
    id                            bigint auto_increment
        primary key,
    transfer_config_salesforce_id bigint       not null,
    name                          varchar(255) not null,
    label                         varchar(255) not null,
    active                        tinyint(1)   not null,
    user_group                    varchar(30)  not null,
    created_user                  varchar(45)  not null,
    modified_user                 varchar(45)  not null,
    created                       datetime     not null,
    modified                      datetime     not null
);

create index idx_transfer_config_salesforce_id
    on d_transfer_config_salesforce_object (transfer_config_salesforce_id);

create table d_transfer_config_salesforce_object_field
(
    id                                   bigint auto_increment
        primary key,
    transfer_config_salesforce_object_id bigint       not null,
    name                                 varchar(255) not null,
    label                                varchar(255) not null,
    field_type                           varchar(30)  not null,
    active                               tinyint(1)   not null,
    user_group                           varchar(30)  not null,
    created_user                         varchar(45)  not null,
    modified_user                        varchar(45)  not null,
    created                              datetime     not null,
    modified                             datetime     not null
);

create index idx_transfer_config_salesforce_object_id
    on d_transfer_config_salesforce_object_field (transfer_config_salesforce_object_id);

create table d_transfer_detail_log
(
    id                int auto_increment
        primary key,
    postdata_id       int                      null,
    transfer_type_id  int                      null,
    status            int                      null,
    postdata          json                     null,
    modified_postdata json                     null,
    result_code       int                      null,
    message           json                     null,
    user_group        varchar(30) charset utf8 null,
    created_user      varchar(45) charset utf8 null,
    created           datetime                 null,
    modified_user     varchar(45) charset utf8 null,
    modified          datetime                 null
)
    collate = utf8mb4_bin;

create table d_transfer_log
(
    id               int auto_increment
        primary key,
    transfer_id      int                      null,
    transfer_type_id int                      null,
    status           int                      null,
    transfer_data    json                     null,
    result_data      json                     null,
    user_group       varchar(30) charset utf8 null,
    created_user     varchar(45) charset utf8 null,
    created          datetime                 null,
    modified_user    varchar(45) charset utf8 null,
    modified         datetime                 null
)
    collate = utf8mb4_bin;

create table m_transfers
(
    id            int auto_increment
        primary key,
    type_code     varchar(45)               not null,
    name          varchar(128) charset utf8 not null,
    status        int                       not null,
    user_group    varchar(30) charset utf8  null,
    created_user  varchar(45) charset utf8  null,
    created       datetime                  not null,
    modified_user varchar(45) charset utf8  null,
    modified      datetime                  not null
)
    collate = utf8mb4_bin;

create table m_userinfo
(
    id                varchar(50) charset utf8  not null
        primary key,
    linkedid          varchar(255)              null,
    username          varchar(255)              not null,
    password          varchar(255)              not null,
    user_group        varchar(30) charset utf8  null,
    role              varchar(30) charset utf8  null,
    first_name        varchar(30) charset utf8  null,
    last_name         varchar(30) charset utf8  null,
    full_name         varchar(50) charset utf8  null,
    email             varchar(255) charset utf8 null,
    avatar_url        varchar(255) charset utf8 null,
    serializedprofile varchar(10000)            null,
    activated         tinyint                   null,
    deletable         tinyint                   null,
    created           datetime                  null,
    modified          datetime                  null
)
    collate = utf8mb4_bin;

# --- !Downs
drop table m_userinfo;
drop table m_transfers;
drop table d_transfer_detail_log;
drop table d_transfer_log;
drop table d_transfer_config_salesforce_object_field;
drop table d_transfer_config_salesforce_object;
drop table d_transfer_config_salesforce;
drop table d_transfer_config_mail_address;
drop table d_transfer_config_mail;
drop table d_postdata;
drop table d_form_transfer_task_salesforce_field;
drop table d_form_transfer_task_salesforce;
drop table d_form_transfer_task_mail;
drop table d_form_transfer_task_condition;
drop table d_form_transfer_task;
drop table d_transfer_config;
drop table d_form_col_validation;
drop table d_form_col_select;
drop table d_form_col;
drop table d_form;
drop table d_authtoken;
drop table d_apitoken;
