package no.nav.pensjon.testdata;

import no.nav.pensjon.testdata.repository.ScenarioRepository;
import no.nav.pensjon.testdata.repository.support.TestScenario;
import org.junit.Test;

import java.io.IOException;

public class TestdataApplicationTests {



    @Test
    public void testSomething() throws IOException {
        ScenarioRepository repo = new ScenarioRepository();

        TestScenario scenario = repo.getTestScenario("AP2016 gradert tidlig uttak - løpende");

        System.out.print("foo");
    }



}
