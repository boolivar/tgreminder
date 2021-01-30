#!/bin/sh

records_deleted=`psql -d $DATABASE_URL -t -c "DELETE FROM reminders WHERE time < (current_timestamp - interval '15 minutes');"`

echo records deleted: $records_deleted