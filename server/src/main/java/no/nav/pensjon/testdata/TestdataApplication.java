package no.nav.pensjon.testdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class
})
public class TestdataApplication  {

	public static void main(String[] args) {
		SpringApplication.run(TestdataApplication.class, args);
	}
}