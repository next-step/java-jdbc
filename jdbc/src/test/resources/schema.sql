drop table if exists users ;

create table if not exists users (
    id bigint auto_increment,
    email varchar(100) not null,
    primary key(id)
);