package no.nav.pensjon.testdata.configuration.support;

public class NotConnectedToDatabaseException extends RuntimeException {

    public NotConnectedToDatabaseException(String message){
        super(message);
    }
}
