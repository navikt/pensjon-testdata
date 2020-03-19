#!/usr/bin/env bash
export TMPFIL1=/tmp/altertable1.sql
export TMPFIL2=/tmp/altertable2.sql

# Kjører sqlplus med spool til utfil
sqlplus PEN/CQGjqIdxrYX0zQ7VJQDQ@pen_logmnr << EOF
set linesize 32767
set trimspool on
set trimout on
set wrap off
set termout off
set echo off
set pagesize 0
set heading off
set feedback off
spool $TMPFIL1
@/home/oracle/moog/setup_log.sql
spool off
exit
EOF

cat $TMPFIL1|grep 'alter table' > $TMPFIL2

# Kjører sqlplus med spool til utfil
sqlplus PEN/CQGjqIdxrYX0zQ7VJQDQ@pen_logmnr << EOF
@$TMPFIL2
exit
EOF

# Avslutter normalt
exit 0