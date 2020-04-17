package no.nav.pensjon.testdata.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.nav.pensjon.testdata.repository.OracleRepository;
import no.nav.pensjon.testdata.repository.ScenarioRepository;
import no.nav.pensjon.testdata.repository.support.Component;
import no.nav.pensjon.testdata.repository.support.PrimaryKeySwapper;
import no.nav.pensjon.testdata.repository.support.TestScenario;
import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;
import no.nav.pensjon.testdata.service.support.ChangeStampTransformer;
import no.nav.pensjon.testdata.service.support.HandlebarTransformer;
import no.nav.pensjon.testdata.service.support.MoogService;
import no.nav.pensjon.testdata.service.support.SqlColumnValueExtractor;

@Service
public class TestdataService {

    @Autowired
    private OracleRepository oracleRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private MoogService moogService;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void createTestcase(String testCaseId, Map<String, String> handlebars) throws IOException, ScenarioValidationException {
        oracleRepository.alterSession();
        validationService.validate(handlebars);
        TestScenario scenario = scenarioRepository.init(testCaseId, handlebars);

        scenario.validate();

        scenario.getComponents()
                .stream()
                .forEach(component -> processComponent(component, handlebars));
    }

    private void processComponent(Component component, Map<String, String> handlebars) {
        component.getSql()
                .stream()
                .map(String::trim)
                .map(sql -> HandlebarTransformer.execute(sql, handlebars))
                .map(ChangeStampTransformer::execute)
                .peek(this::createPenOrgEnhetIfNotExists)
                .map(PrimaryKeySwapper::swapPrimaryKeysInSql)
                .map(this::removeTrailingSemicolon)
                .filter(this::removeOsOppdragslinjeStatus)
                .forEach(statement -> scenarioRepository.execute(component, statement));
    }

    private void createPenOrgEnhetIfNotExists(String query) {
        if (StringUtils.containsIgnoreCase(query, "PEN_ORG_ENHET_ID")){
            String orgEnhetId = SqlColumnValueExtractor.extract(query, "PEN_ORG_ENHET_ID")
               .orElseThrow(() -> new IllegalArgumentException("Could not find pen_org_enhet id in the query! " + query));

            if (!PrimaryKeySwapper.containsPrimaryKey(orgEnhetId)){
                scenarioRepository.insertPenOrgEnhetIfNotExists(orgEnhetId)
                        .ifPresent(primaryKey -> PrimaryKeySwapper.updatePrimaryKey(orgEnhetId, primaryKey));
            }
        }
    }

    public String removeTrailingSemicolon(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ';') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /*
     * Ser bort ifra SQL relatert til T_OS_OPPDRLINJE_S , T_OS_TRANSAKSJON og T_OS_KVITTERING, i et forsøk på å fjerne avhengigheter mot OS.
     * Tabellen gir PEN et bilde av hva som er kommunisert til OS tidligere.
     *
     * For syntetiske testdata har ikke noe blitt kommunisert til OS, og det kan derfor gi mening å ikke ta med dette innholdet.
     */
    private boolean removeOsOppdragslinjeStatus(String sql) {
        return !sql.contains("T_OS_OPPDRLINJE_S") && !sql.contains("T_OS_TRANSAKSJON") && !sql.contains("T_OS_KVITTERING");
    }

    /*
     * Henter testdata log fra Q2 støttedatabase (d26dbvl010.test.local)
     */
    public List<String> fetchTestdataLog(String fom, String tom, List<String> identer) throws SQLException, IOException {
        return moogService.execute(fom, tom, identer);
    }
}
