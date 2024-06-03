drop table if exists limits;
drop table if exists payments;
drop table if exists users;

--//-- Создание таблицы Пользователей(клиентов)
create table users (
   id serial primary key,
   username varchar (255) not null unique
);

--//-- Создание таблицы Платежей
create table payments (
   id serial primary key,
   idUser int not null references users on delete cascade,
   sumPay numeric not null,
   datePay timestamp not null unique
);

--//-- Создание таблицы Лимитов
create table limits (
   id serial primary key,
   idUser int not null references users on delete cascade,
   sumDaylimit numeric null,
   dateinstall timestamp,
   sumBaselimit numeric null
);

--//-- Добавление пользователей(клиентов - 100)
insert into users(username)
select 'User' || f :: text
from generate_series(1, 100) f;

--//-- Добавление некоторых дефолтных лимитов.
--//-- Остальные, при отсутствии будут добавлены автоматически по требованию
insert into limits(idUser, sumDaylimit, dateinstall, sumBaselimit)
	values ((select id from users where username = 'User1'), 10000.00, current_timestamp, 10000.00);
insert into limits(idUser, sumDaylimit, dateinstall, sumBaselimit)
	values ((select id from users where username = 'User2'), 10000.00, current_timestamp, 10000.00);
insert into limits(idUser, sumDaylimit, dateinstall, sumBaselimit)
	values ((select id from users where username = 'User3'), 10000.00, current_timestamp, 10000.00);