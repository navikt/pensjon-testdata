package no.nav.pensjon.testdata.consumer.omregning;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.controller.support.AutomatiskOmregningRequest;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import no.nav.tjeneste.domene.pensjon.behandleautomatiskomregning.v1.binding.BehandleAutomatiskOmregningV1;
import no.nav.tjeneste.domene.pensjon.behandleautomatiskomregning.v1.informasjon.SakTilOmregning;
import no.nav.tjeneste.domene.pensjon.behandleautomatiskomregning.v1.meldinger.AutomatiskOmregningAvYtelseRequest;
import no.nav.tjeneste.domene.pensjon.behandleautomatiskomregning.v1.meldinger.AutomatiskOmregningAvYtelseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

@Service
public class AutomatiskOmregning {

    Logger logger = LoggerFactory.getLogger(AutomatiskOmregning.class);
    @Autowired
    private BehandleAutomatiskOmregningV1 behandleAutomatiskOmregningV1SoapService;

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    public String automatiskOmregning(AutomatiskOmregningRequest request) throws DatatypeConfigurationException, JsonProcessingException {

        logger.info("Starter automatisk omregning");

        AutomatiskOmregningAvYtelseRequest soapRequest = new AutomatiskOmregningAvYtelseRequest();
        soapRequest.setStatus("IKKE_BEHANDLET");
        soapRequest.setFomDato(DatatypeFactory.newInstance().newXMLGregorianCalendar(request.getVirkFom().toString()));
        SakTilOmregning sakTilOmregning = new SakTilOmregning();
        sakTilOmregning.setPid(request.getFnr());
        sakTilOmregning.setSakId(request.getSakId());
        sakTilOmregning.setSakType(getSakType(request.getSakId()));
        soapRequest.getYtelseListe().add(sakTilOmregning);
        soapRequest.setBehandlingsparametere("brukppen015:true");

        AutomatiskOmregningAvYtelseResponse response = behandleAutomatiskOmregningV1SoapService.automatiskOmregningAvYtelse(soapRequest);

        ObjectMapper objectMapper = new ObjectMapper();

        logger.info("Omregning status: " + response.getStatus());
        logger.info("Omregning funksjonell feilmelding: " + response.getFunksjonellFeilmelding());
        logger.info("Omregning feilmelding: " + response.getFeilmelding());
        logger.info(objectMapper.writeValueAsString(response));

        StringBuilder sb = new StringBuilder();
        sb.append("Omregning ferdig, status: " +  response.getStatus());
        if (response.getFunksjonellFeilmelding() != null) {
            sb.append(" Funksjonell feilmelding: " + response.getFunksjonellFeilmelding());
        }
        if (response.getFeilmelding() != null) {
            sb.append(" Teknisk feilmelding: " + response.getFeilmelding());
        }
        return sb.toString();
    }

    private String getSakType(String sakId) {
       return (String) jdbcTemplateWrapper.queryForList(ComponentCode.PEN,"SELECT K_SAK_T FROM T_SAK WHERE SAK_ID='"+sakId+"'").get(0).get("K_SAK_T");
    }
}
