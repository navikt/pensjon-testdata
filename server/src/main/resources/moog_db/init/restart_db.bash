#!/usr/bin/env bash

# Kjører sqlplus
sqlplus / as sysdba << EOF
shutdown abort;
startup mount;
alter database open;
alter pluggable database pen_logmnr open read write;
exit
EOF

# Avslutter normalt
exit 0