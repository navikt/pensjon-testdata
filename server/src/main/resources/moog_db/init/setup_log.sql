select 'alter table '||owner||'.'||table_name||' add supplemental log data (primary key) columns;' as q
from sys.all_tables
where owner = 'PEN';