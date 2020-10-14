package no.nav.pensjon.testdata.consumer.brev;

import no.nav.pensjon.testdata.controller.BrevMetaData;

import java.util.Map;

public class BrevdataMapper {
    public static BrevMetaData mapBrev(String batchBrevCode, Map<String, Object> metaData) {
        String kodeVerdi = batchBrevCode; //(String)metaData.get("brevkodeIBrevsystem");
        String dekode = (String)metaData.get("dekode");

        return new BrevMetaData(kodeVerdi, dekode);
    }
}
