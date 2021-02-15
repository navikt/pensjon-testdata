package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.consumer.opptjening.support.LagreInntektPoppRequest;
import no.nav.pensjon.testdata.consumer.opptjening.support.POPPInternalFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public interface RetryLayer {
    @Retryable(maxAttempts=3,value= POPPInternalFailureException.class)
    Boolean lagreInntekt(LagreInntektPoppRequest body, HttpHeaders httpHeaders);
}
