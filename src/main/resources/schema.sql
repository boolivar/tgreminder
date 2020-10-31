create table USERS(ID bigint, NAME varchar(256), TIME_ZONE varchar(128));

create table REMINDERS(ID bigint, CHAT_INDEX integer, USER_ID bigint, CHAT_ID bigint, MESSAGE varchar(256), TIME timestamp, FLAGS varchar(128));

create sequence USERS_SEQ;

create sequence REMINDERS_SEQ;