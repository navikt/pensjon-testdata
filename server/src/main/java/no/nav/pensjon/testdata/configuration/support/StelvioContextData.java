package no.nav.pensjon.testdata.configuration.support;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
        name = "StelvioContext",
        namespace = "http://www.nav.no/StelvioContextPropagation"
)
public class StelvioContextData  {
    private String applicationId;
    private String correlationId;
    private String languageId;
    private String userId;

    public StelvioContextData() {
    }

    @XmlElement(
            name = "applicationId"
    )
    public String getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @XmlElement(
            name = "correlationId"
    )
    public String getCorrelationId() {
        return this.correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @XmlElement(
            name = "languageId"
    )
    public String getLanguageId() {
        return this.languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    @XmlElement(
            name = "userId"
    )
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
