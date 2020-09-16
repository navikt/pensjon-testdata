package no.nav.pensjon.testdata.consumer.brev;

import no.nav.pensjon.testdata.controller.BrevMetaData;

import java.util.Map;

public class BrevdataMapper {
    public static BrevMetaData mapBrev( Map<String, Object> metaData) {
        String kodeVerdi = (String)metaData.get("brevkodeIBrevsystem");
        String dekode = (String)metaData.get("dekode");

        return new BrevMetaData(kodeVerdi, dekode);
    }
}
