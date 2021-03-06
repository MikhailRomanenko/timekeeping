drop table employee if exists;
drop table position if exists;
drop table schedule if exists;
drop table schedule_item if exists;
drop table shop if exists;
drop table user if exists;
drop table user_shop if exists;

create table employee (id bigint generated by default as identity, active boolean not null, employment varchar(255), first_name varchar(255), last_name varchar(255), position_id integer, shop_id bigint, primary key (id));
create table position (id integer generated by default as identity, department varchar(255), name varchar(255), primary key (id));
create table schedule (id bigint generated by default as identity, schedule_date date, version integer not null, shop_id bigint, primary key (id));
create table schedule_item (employee_id bigint, schedule_id bigint, duration integer not null, start_time integer not null, work_type varchar(255), primary key (employee_id, schedule_id));
create table shop (id bigint generated by default as identity, city varchar(255), street varchar(255), name varchar(255), primary key (id));
create table user (id bigint generated by default as identity, login varchar(255), password varchar(255), roles varchar(255), primary key (id));
create table user_shop (user_id bigint not null, shop_id bigint not null);

alter table employee add constraint FK_6o1mvrxpyqmb7haarvayfcn5j foreign key (position_id) references position;
alter table employee add constraint FK_s1njjafqwe48e4uj1syra1s4 foreign key (shop_id) references shop;
alter table schedule add constraint FK_2vsifa56cpir861ao0lbr236q foreign key (shop_id) references shop;
alter table schedule_item add constraint FK_t1tcegpyygjf3yqnhiowua9qc foreign key (employee_id) references employee;
alter table schedule_item add constraint FK_6uo00usa9ah81wkibgob8b74g foreign key (schedule_id) references schedule on delete cascade on update cascade;
alter table user_shop add constraint FK_lj2mqxu3f2few43i002wsv0gy foreign key (shop_id) references shop;
alter table user_shop add constraint FK_1xf1rv9ld8ris5m8edto45hhe foreign key (user_id) references user;