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
