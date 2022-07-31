# --- !Ups
alter table d_form_transfer_task_mail
    add to_address_id bigint null after to_address;

alter table d_form_transfer_task_mail
    add to_address_field varchar(255) null after to_address_id;

alter table d_form_transfer_task_mail
    add cc_address_id bigint null after cc_address;

alter table d_form_transfer_task_mail
    add cc_address_field varchar(255) null after cc_address_id;

# --- !Downs
alter table `d_form_transfer_task_mail`
drop column `to_address_id`;

alter table `d_form_transfer_task_mail`
drop column `to_address_field`;

alter table `d_form_transfer_task_mail`
drop column `cc_address_id`;

alter table `d_form_transfer_task_mail`
drop column `cc_address_field`;
