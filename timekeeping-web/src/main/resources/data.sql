insert into position values(1, 'Managenement', 'Manager');
insert into position values(2, 'Managenement', 'Admin');
insert into position values(3, 'Sales', 'Saler');
insert into position values(4, 'Sales', 'Sr. Saler');
insert into position values(5, 'Stock', 'Stockmen');

insert into shop values(1, 'City1', 'Street1', 'Shop1');
insert into shop values(2, 'City2', 'Street2', 'Shop2');
insert into shop values(3, 'City3', 'Street3', 'Shop3');

insert into employee values(1, true, 'FULL_TIME', 'Name1', 'Surname1', 1, 1);
insert into employee values(2, true, 'FULL_TIME', 'Name2', 'Surname2', 2, 1);
insert into employee values(3, true, 'PART_TIME', 'Name3', 'Surname3', 3, 1);
insert into employee values(4, false, 'FULL_TIME', 'Name4', 'Surname4', 3, 1);
insert into employee values(5, true, 'PART_TIME', 'Name5', 'Surname5', 3, 2);
insert into employee values(6, true, 'PART_TIME', 'Name6', 'Surname6', 3, 2);

insert into schedule values(1, DATE '2016-02-29', 0, 1);
insert into schedule values(2, DATE '2016-03-01', 1, 1);
insert into schedule values(3, DATE '2016-03-02', 0, 1);

insert into schedule_item values(1, 1, 240, 600, 'WORK');
insert into schedule_item values(2, 1, 720, 600, 'WORK');
insert into schedule_item values(3, 1, 900, 600, 'WORK');

insert into schedule_item values(1, 2, 240, 720, 'WORK');
insert into schedule_item values(2, 2, 780, 720, 'WORK');
insert into schedule_item values(3, 2, 900, 720, 'WORK');

insert into schedule_item values(1, 3, 240, 780, 'WORK');
insert into schedule_item values(2, 3, 840, 780, 'WORK');
insert into schedule_item values(3, 3, 900, 780, 'WORK');

insert into user values(1, 'user1', 'password', 'ROLE_USER');
insert into user values(2, 'user2', 'password', 'ROLE_USER');

insert into user_shop values(1, 1);
insert into user_shop values(1, 2);
insert into user_shop values(2, 2);