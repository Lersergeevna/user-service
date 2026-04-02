create table if not exists users (
    id bigserial primary key,
    name varchar(100) not null,
    email varchar(150) not null,
    age integer not null,
    created_at timestamp not null,
    constraint uk_users_email unique (email),
    constraint chk_users_age check (age between 1 and 130)
);
