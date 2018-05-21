package com.larz1.afranalyzer;

// Feilen ligger her, må huske unadjusted AFR, slik at den kab benyttes i EGO calc, må også endre i Service

public class LogValue {
    private int time;
    private int rpm;
    private double tps;
    private double gear;
    private double afr;
    private double ect;
    private double lonacc;
    private boolean skip = false;
    private boolean egoOffsetApplied = false;
    private int egoOffset;
    private double unadjustedAfr;
    private int lineNr = 0;


    public LogValue() {
    }

    public LogValue(int time, int rpm, double tps, double afr) {
        this.time = time;
        this.rpm = rpm;
        this.tps = tps;
        this.afr = afr;
    }

    public LogValue(LogValue a) {
        this.time = a.time;
        this.rpm = a.rpm;
        this.tps = a.tps;
        this.gear = a.gear;
        this.afr = a.afr;
        this.ect = a.ect;
        this.lonacc = a.lonacc;
        this.lineNr = a.lineNr;
        this.egoOffset = a.egoOffset;
        this.skip = a.skip;
        this.egoOffsetApplied = a.egoOffsetApplied;
        this.unadjustedAfr = a.unadjustedAfr;
    }

    public LogValue(int time, int rpm, double tps, double gear, double afr) {
        this.time = time;
        this.rpm = rpm;
        this.tps = tps;
        this.gear = gear;
        this.afr = afr;
    }

    public LogValue(int rpm, double tps, double afr) {
        this.rpm = rpm;
        this.tps = tps;
        this.afr = afr;
    }

    /**
     * Get time in millis
     *
     * @return
     */
    public int getTime() {
        return time;
    }

    /**
     * Set time int millis
     *
     * @param time
     */
    public void setTime(int time) {
        this.time = time;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public double getGear() {
        return gear;
    }

    public void setGear(double gear) {
        this.gear = gear;
    }

    public double getAfr() {
        return afr;
    }

    public void setAfr(double afr) {
        if (egoOffsetApplied) {
            this.unadjustedAfr = this.afr;
        }
        this.afr = afr;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isEgoOffsetApplied() {
        return egoOffsetApplied;
    }

    public void setEgoOffsetApplied(boolean egoOffsetApplied) {
        this.egoOffsetApplied = egoOffsetApplied;
    }

    public double getEct() {
        return ect;
    }

    public void setEct(double ZC_ECT) {
        this.ect = ZC_ECT;
    }

    public double getUnadjustedAfr() {
        if (egoOffsetApplied) {
            return unadjustedAfr;
        }

        return this.afr;

    }

    public int getLineNr() {
        return lineNr;
    }

    public void setLineNr(int lineNr) {
        this.lineNr = lineNr;
    }

    public int getEgoOffset() {
        return egoOffset;
    }

    public void setEgoOffset(int egoOffset) {
        this.egoOffset = egoOffset;
    }

    public double getLonacc() {
        return lonacc;
    }

    public void setLonacc(double lonacc) {
        this.lonacc = lonacc;
    }


    @Override
    public String toString() {
        return "LogValue{" +
                "time=" + time +
                ", rpm=" + rpm +
                ", tps=" + tps +
                ", gear=" + gear +
                ", afr=" + afr +
                ", ect=" + ect +
                ", lonacc=" + lonacc +
                ", skip=" + skip +
                ", egoOffsetApplied=" + egoOffsetApplied +
                ", egoOffset=" + egoOffset +
                ", unadjustedAfr=" + unadjustedAfr +
                ", lineNr=" + lineNr +
                '}';
    }
}
