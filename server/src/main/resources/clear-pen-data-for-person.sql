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

        IF parent_table <> 'T_PERSON' THEN
            EXECUTE IMMEDIATE delete_command;
        END IF;
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

            v_where_clause  := 'where ' || r.column_name || '= ' || '''{fnr}''';

            delete_cascade( table_owner  => v_table_owner
                , parent_table => v_parent_table
                , where_clause => v_where_clause);
        end loop;
end;

