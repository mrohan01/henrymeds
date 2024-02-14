-- provider records
create table provider (
    id varchar(27) not null,
    name varchar(256) not null,
    primary key (id)
);

-- provider schedules
create table provider_schedule_timeslot (
    id varchar(27) not null,
    provider_id varchar(27) not null,
    timeslot_start timestamp with time zone not null,
    booked int not null default 0,
    primary key (id)
);
-- enables searching for schedules by provider
create index idx_provider_schedule_by_provider on provider_schedule_timeslot (provider_id);
-- enables searching by time slot range (for client lookup)
create index idx_provider_schedule_timeslot_start on provider_schedule_timeslot (timeslot_start);
-- disallows a provider to have more than one appointment slot for a given start time
create unique index idx_provider_schedule_timeslot on provider_schedule_timeslot (provider_id, timeslot_start);


-- client records
create table client (
    id varchar(27) not null,
    name varchar(256) not null,
    primary key (id)
);

-- appointments scheduled with providers by clients
create table appointment (
    id varchar(27) not null,
    provider_schedule_id varchar(27) not null,
    client_id varchar(27) not null,
    scheduled_at timestamp with time zone not null,
    confirmed int not null default 0,
    confirmed_at timestamp with time zone,
    primary key (id)
);
-- enables searching for appointments by client ID
create index idx_appointments_by_client on appointment (client_id);
-- enables searching for appointments associated with a provider's schedule
create index idx_appointments_by_provider_schedule on appointment (provider_schedule_id);

-- notifications which will be or have been sent for appointments
create table appointment_notification (
    id varchar(27) not null,
    appointment_id varchar(27) not null,
    notification_timestamp timestamp with time zone not null,
    sent int not null default 0,
    primary key (id)
);

-- DATA
insert into provider(id, name)
    values ('2cIG1WRZ87LW1mvlg76L0xHhSpz', 'Dr. Spock');
insert into provider(id, name)
    values ('2cIG1dASclNWixEOeHm24oKOwMt', 'Dr. Freud');

insert into client(id, name)
    values('2cIG1YVDjOSjQVYotCRB0eKz6rB', 'Colonel Mustard');

--2cIG1YYCt3hAD8Bnybxw7nPUlUR
--2cIG1b4NYB3rvVzmKQIwwUHWUeG
--2cIG1WGyARezj2wnvh8wQO6nz2c
--2cIG1a18anuOyX8ED8LVxPdTQg0
--2cIG1W61wLDjOYsfGnXPEXQMbJO
--2cIG1clOm22syyXRqDgMfQDXS6r
--2cIG1cWad9tX6XAKQGSlUIFeIH3
--2cIG1c0PzDEnHYCDtJYbjYMeGvs
--2cIG1Y165wJ8PlXEN9ztBOgsAjN
--2cIG1a6JalzttLLMcM31Y2t3xVV
--2cIG1XNajTlN5mDFa5UTo7sQhdd
--2cIG1XMGB3TZFNjVvYJtrP3hh1u
--2cIG1ZdbtpPm8SFAmu0sMFLYfJn
--2cIG1bNjdjm6Zcn8fhareERFE2B
--2cIG1XEHcyyHkN9SDft3BKVOCC0
--2cIG1WYP5F477Bj70oEgK3WuYUr
--2cIG1Zg4X7z57lFWnR6JQebUhlM
--2cIG1WyfoTVAom4kZnS9WAK7bQk
--2cIG1ZzF9LmbJA2O0SZXZHtfgzR
--2cIG1Ztv8p5DTYeZGMHWnOXZDIY
--2cIG1aQpHzkyO7VO51BDu3mGwSv
--2cIG1bOrEJeIn5RdOkIaj4MUkHA
--2cIG1X3xMGI3Emg7pRoPJ6JceN0
--2cIG1d3wGJZWUCyt9J5tiJN8vP2
--2cIG1ZXkuyQnUjQqLEiBFtbwXQb
--2cIG1YKseKZNE40t7K8rwzLOiRl
--2cIG1dQdvdSUh9jG7M0r8vVaLQe
--2cIG1Y7T27SuEkBbO3a1iniYKvU
--2cIG1XIMpqzugtUZGBpfmC6zfk4
--2cIG1dHCC9f0DcmhgYi94fMkj9j
--2cIG1bMqdXOUwAwodwrcLDrFSkh
