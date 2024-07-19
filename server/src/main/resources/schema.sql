drop table if exists users, requests, items, comments, bookings cascade;

create table users (
    id integer generated by default as identity not null primary key,
    name varchar(50) not null,
    email varchar(50) unique not null
);

create table requests (
    id integer generated by default as identity not null primary key,
    description varchar(512) not null,
    requestor_id integer references users(id) on delete cascade,
    created timestamp with time zone not null
);

create table items (
    id integer generated by default as identity not null primary key,
    name varchar(255) not null,
    description varchar(512) not null,
    available boolean not null,
    owner_id integer references users(id) on delete cascade,
    request_id integer references requests(id) on delete no action
);

create table comments (
    id integer generated by default as identity not null primary key,
    text varchar(512) not null,
    item_id integer references items(id) on delete cascade,
    author_id integer references users(id) on delete cascade,
    created timestamp with time zone not null
);

create table bookings (
    id integer generated by default as identity not null primary key,
    start_date timestamp with time zone not null,
    end_date timestamp with time zone not null,
    item_id integer references items(id) on delete cascade,
    booker_id integer references users(id) on delete cascade,
    status varchar(10) not null
);




