package no.nav.pensjon.testdata.consumer.opptjening.support;

import java.util.Date;

public class ChangeStamp {

    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;

    public ChangeStamp() {
        this.createdBy = "TESTDATA";
        this.updatedBy = "TESTDATA";
        Date now = new Date();
        this.createdDate = now;
        this.updatedDate = now;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
