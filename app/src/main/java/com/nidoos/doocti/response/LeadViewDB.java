package com.nidoos.doocti.response;

public class LeadViewDB {
    private Integer ID;
    private String LEADID;
    private String NAME;
    private String EMAIL;
    private String CALLSTATUS;

    public LeadViewDB(Integer ID, String LEADID, String NAME, String EMAIL, String CALLSTATUS) {
        this.ID = ID;
        this.LEADID = LEADID;
        this.NAME = NAME;
        this.EMAIL = EMAIL;
        this.CALLSTATUS = CALLSTATUS;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getLEADID() {
        return LEADID;
    }

    public void setLEADID(String TITLE) {
        this.LEADID = TITLE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getCALLSTATUS() {
        return CALLSTATUS;
    }

    public void setCALLSTATUS(String CALLSTATUS) {
        this.CALLSTATUS = CALLSTATUS;
    }

}
