#!/bin/sh

next_time=`psql -d $DATABASE_URL -t -c "select min(time) from REMINDERS where time > current_timestamp + interval '5 minutes' and time < current_timestamp + interval '25 minutes';"`

ec=$?

echo next time: $next_time

if [ $ec -eq 0 ] && [ "$next_time" != " " ]; then
  wget $APP_URL -qO -
fi
