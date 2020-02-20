package no.nav.pensjon.testdata;

import org.junit.Test;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class TestdataApplicationTests {



    @Test
    public void testSomething() throws IOException {
        String fnr = "***REMOVED***";
        LocalDate fodselsdato = LocalDate.of(
                Integer.valueOf("19"  + fnr.substring(4,6)),
                Integer.valueOf(fnr.substring(2,4).replaceFirst("^0+", "")),
                Integer.valueOf(fnr.substring(0,2).replaceFirst("^0+", "")));
        Date foo = new Date(fodselsdato.toEpochDay());
         Date.valueOf(fodselsdato);
    }



}
