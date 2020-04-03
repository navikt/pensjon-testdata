package no.nav.pensjon.testdata.repository;

import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.ComponentCode;

class ScenarioRepositoryTest {
    private ScenarioRepository scenarioRepository = new ScenarioRepository();

    @Test
    void insertPenOrgEnhetIfNotExists() {
        JdbcTemplateWrapper jdbcTemplateWrapper = Mockito.mock(JdbcTemplateWrapper.class);
        Mockito.when(jdbcTemplateWrapper.queryForString(Mockito.eq(ComponentCode.PEN), Mockito.anyString(), Mockito.argThat(new StringArrayMatcher(new Object[]{"111"}))))
                .thenThrow(EmptyResultDataAccessException.class);
        Mockito.when(jdbcTemplateWrapper.queryForList(Mockito.eq(ComponentCode.PEN), Mockito.anyString()))
                .thenReturn(Collections.singletonList(Collections.singletonMap("NEXTVAL", "777")));
        scenarioRepository.setJdbcTemplateWrapper(jdbcTemplateWrapper);

        Optional<String> s = scenarioRepository.insertPenOrgEnhetIfNotExists("111");

        Mockito.verify(jdbcTemplateWrapper, Mockito.times(1))
                .queryForString(Mockito.eq(ComponentCode.PEN), Mockito.anyString(), Mockito.any());
        Mockito.verify(jdbcTemplateWrapper, Mockito.times(1)).queryForList(Mockito.eq(ComponentCode.PEN), Mockito.anyString());
        Mockito.verify(jdbcTemplateWrapper, Mockito.times(1)).execute(Mockito.eq(ComponentCode.PEN), Mockito.anyString());

        Assert.assertTrue(s.isPresent());
        Assert.assertEquals("777", s.get());
    }

    @Test
    void insertPenOrgEnhetWhichExists() {
        JdbcTemplateWrapper jdbcTemplateWrapper = Mockito.mock(JdbcTemplateWrapper.class);
        scenarioRepository.setJdbcTemplateWrapper(jdbcTemplateWrapper);

        Optional<String> s = scenarioRepository.insertPenOrgEnhetIfNotExists("222");

        Mockito.verify(jdbcTemplateWrapper, Mockito.times(1))
                .queryForString(Mockito.eq(ComponentCode.PEN), Mockito.anyString(), Mockito.any());
        Mockito.verify(jdbcTemplateWrapper, Mockito.never()).queryForList(Mockito.eq(ComponentCode.PEN), Mockito.anyString());
        Mockito.verify(jdbcTemplateWrapper, Mockito.never()).execute(Mockito.eq(ComponentCode.PEN), Mockito.anyString());

        Assert.assertTrue(s.isEmpty());
    }

    private static class StringArrayMatcher implements ArgumentMatcher<Object[]> {
        private final Object[] expected;

        public StringArrayMatcher(Object[] expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object[] actual) {
            if (actual == null) {
                return false;
            }

            if (expected.length != actual.length){
                return false;
            }

            for (int i=0; i < expected.length; i++ ){
                if (!expected[i].equals(actual[i])){
                    return false;
                }
            }
            return true;
        }
    }

}