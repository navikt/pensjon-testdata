declare
    v_table_owner          VARCHAR2(50);
    v_parent_table         VARCHAR2(50);
    v_where_clause         VARCHAR2(50);
    last_result            NUMBER;

    PROCEDURE delete_cascade( table_owner          VARCHAR2
                            , parent_table         VARCHAR2
                            , where_clause         VARCHAR2 )
        IS
        child_cons       VARCHAR2(30);
        parent_cons      VARCHAR2(30);
        child_table      VARCHAR2(30);
        child_cols       VARCHAR(500);
        parent_cols      VARCHAR(500);
        delete_command   VARCHAR(10000);
        new_where_clause VARCHAR2(10000);

        CURSOR cons_cursor IS
            SELECT owner
                 ,      constraint_name
                 ,      r_constraint_name
                 ,      table_name
                 ,      delete_rule
            FROM   sum_all_constraints
            WHERE  constraint_type = 'R'
              AND    delete_rule = 'NO ACTION'
              AND    r_constraint_name IN ( SELECT  constraint_name
                                            FROM    sum_all_constraints
                                            WHERE   constraint_type IN ('P', 'U')
                                              AND     table_name = parent_table
                                              AND     owner = table_owner )
              AND NOT table_name = parent_table;

        CURSOR   columns_cursor
            IS
            SELECT   cc1.column_name AS child_col, cc2.column_name AS parent_col
            FROM     sum_all_cons_columns cc1, sum_all_cons_columns cc2
            WHERE    cc1.constraint_name = child_cons
              AND      cc1.table_name = child_table
              AND      cc2.constraint_name = parent_cons
              AND      cc1.position = cc2.position
            ORDER BY cc1.position;

    BEGIN
        FOR cons IN cons_cursor
            LOOP
                child_cons   := cons.constraint_name;
                parent_cons  := cons.r_constraint_name;
                child_table  := cons.table_name;
                child_cols   := '';
                parent_cols  := '';

                FOR cols IN columns_cursor
                    LOOP
                        IF child_cols IS NULL THEN
                            child_cols  := cols.child_col;
                        ELSE
                            child_cols  := child_cols || ', ' || cols.child_col;
                        END IF;

                        IF parent_cols IS NULL THEN
                            parent_cols  := cols.parent_col;
                        ELSE
                            parent_cols  := parent_cols || ', ' || cols.parent_col;
                        END IF;
                    END LOOP;

                new_where_clause  := ' where (' || child_cols || ') in (select ' || parent_cols || ' from ' || table_owner || '.' || parent_table || ' ' || where_clause || ')';

                EXECUTE IMMEDIATE 'SELECT count(1) FROM ' || child_table || new_where_clause into last_result;

                IF last_result > 0 THEN
                    delete_cascade( table_owner  => cons.owner
                        , parent_table => child_table
                        , where_clause => new_where_clause);
                END IF;
            END LOOP;
        delete_command  := 'delete from ' || table_owner || '.' || parent_table || ' ' || where_clause;

        DBMS_OUTPUT.put_line(delete_command || ';');
        --EXECUTE IMMEDIATE delete_command;
    END;

begin
    for r in ( select table_name, column_name
               from   sys.all_tab_columns
               where  owner = 'PEN'
                 and    UPPER(column_name) in ('FNR','FNR_FK')
        )
        loop
            v_table_owner   := 'PEN';
            v_parent_table  := r.table_name;

            -- Her kan vi bruke en where in mot tabell med fnr som skal slettes.
            v_where_clause  := 'where ' || r.column_name || '= ' || '''13048541317''';

            delete_cascade( table_owner  => v_table_owner
                , parent_table => v_parent_table
                , where_clause => v_where_clause);
        end loop;
end;


-----------------------------------
-----------------------------------
-----------------------------------

CREATE TABLE mock_all_constraints (
                                      owner               VARCHAR(5),
                                      constraint_name     VARCHAR(50),
                                      constraint_type     VARCHAR(1),
                                      table_name          VARCHAR(25),
                                      r_constraint_name   VARCHAR(10),
                                      delete_rule         VARCHAR(10)
);
CREATE TABLE mock_all_cons_columns (
                                       column_name         VARCHAR(25),
                                       constraint_name     VARCHAR(25),
                                       table_name          VARCHAR(25),
                                       position            NUMBER
);
CREATE VIEW sum_all_constraints AS
SELECT OWNER, CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME, R_CONSTRAINT_NAME, DELETE_RULE FROM mock_all_constraints
UNION
SELECT OWNER, CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME, R_CONSTRAINT_NAME, DELETE_RULE FROM all_constraints;
CREATE VIEW sum_all_cons_columns AS
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, POSITION FROM mock_all_cons_columns
UNION
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, POSITION FROM all_cons_columns;


INSERT INTO mock_all_constraints (OWNER, CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME, R_CONSTRAINT_NAME, DELETE_RULE) VALUES ('PEN', 'const_2', 'R', 'T_BEREGNING_RES', 'const_1','NO_ACTION');
--INSERT INTO mock_all_constraints (OWNER, CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME, R_CONSTRAINT_NAME, DELETE_RULE) VALUES ('PEN', 'const_2', 'P', 'T_PERSON_GRUNNLAG', 'n/a','n/a');
INSERT INTO mock_all_cons_columns (CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, POSITION) VALUES ('const_1', 'T_PEN_UNDER_UTBET', 'PEN_UNDER_UTBET_ID', 1);
INSERT INTO mock_all_cons_columns (CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, POSITION) VALUES ('const_2', 'T_BEREGNING_RES', 'PEN_UNDER_UTBET_ID', 1);


SELECT * FROM mock_all_constraints;
SELECT * FROM mock_all_cons_columns;

SELECT * FROM sum_all_constraints s
                  LEFT OUTER JOIN sum_all_cons_columns a on a.CONSTRAINT_NAME = s.constraint_name
                  LEFT OUTER JOIN sum_all_cons_columns b on b.CONSTRAINT_NAME = s.R_CONSTRAINT_NAME
where s.table_name in ('T_PEN_UNDER_UTBET')
  AND s.constraint_type in ('R','P','U');



TRUNCATE TABLE mock_all_constraints;
TRUNCATE TABLE mock_all_cons_columns;


-----------------------------------
-----------------------------------
-----------------------------------

declare
    v_parent_table         VARCHAR2(50);
    last_result            NUMBER;
begin
    for r in ( select table_name
               from   sys.ALL_TABLES
               where  owner = 'PEN'
                 and table_name not like 'T_K_%'
                 and table_name not in ('T_APPL_PARAMETER', 'T_BPEN068_AVVIK_PARAM', 'T_DB_VERSJON','T_DELINGSTALL','T_DOKUMENTMAL','T_FORH_TALL_KOMP','T_FORHOLDSTALL','T_JUSTERINGSBELOP','T_PEN_ORG_ENHET', 'T_REFERANSEBELOP','ERRORDEFINITION','schema_version')
        )
        loop
            v_parent_table  := r.table_name;
            EXECUTE IMMEDIATE 'SELECT count(1) FROM ' || v_parent_table INTO last_result;
            IF last_result > 0 THEN
                DBMS_OUTPUT.put_line(v_parent_table || ' ' || last_result);
            END IF;
        end loop;
end;
