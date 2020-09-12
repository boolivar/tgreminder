create table USER(ID bigint, NAME varchar(256), TIME_ZONE varchar(128));

create table REMINDER(ID bigint, USER_ID bigint, MESSAGE varchar(256), TIME timestamp, FLAGS varchar(128));