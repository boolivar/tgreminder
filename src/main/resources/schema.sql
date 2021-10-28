create table USERS(ID bigint PRIMARY KEY, NAME varchar(256), TIME_ZONE varchar(128));

create table CHATS(ID bigint PRIMARY KEY, CHAT_INDEX integer);

create table REMINDERS(ID bigint PRIMARY KEY, CHAT_INDEX integer, USER_ID bigint, CHAT_ID bigint, MESSAGE varchar(256), TIME timestamp, FLAGS varchar(128), UNIQUE (CHAT_ID, CHAT_INDEX));

create sequence USERS_SEQ;

create sequence REMINDERS_SEQ;