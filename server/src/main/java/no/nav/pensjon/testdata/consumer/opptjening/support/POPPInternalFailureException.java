package no.nav.pensjon.testdata.consumer.opptjening.support;

public class POPPInternalFailureException extends RuntimeException {

    public POPPInternalFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
