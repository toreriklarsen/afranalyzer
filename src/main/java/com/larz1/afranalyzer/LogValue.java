package com.larz1.afranalyzer;

// Feilen ligger her, må huske unadjusted AFR, slik at den kab benyttes i EGO calc, må også endre i Service

public class LogValue {
    public int Time;
    public double ZX_RPM;
    public double ZX_TPS;
    public double ZX_GEAR;
    public double LLC_AFR;
    public double ZX_ECT;
    private boolean skip = false;
    private boolean egoOffsetApplied = false;
    private int egoOffset;
    private double unadjustedAfr;
    private int lineNr = 0;


    public LogValue() {
    }

    public LogValue(int time, double ZX_RPM, double ZX_TPS, double LLC_AFR) {
        Time = time;
        this.ZX_RPM = ZX_RPM;
        this.ZX_TPS = ZX_TPS;
        this.LLC_AFR = LLC_AFR;
    }

    public LogValue(LogValue a) {
        this.Time = a.Time;
        this.ZX_RPM = a.ZX_RPM;
        this.ZX_TPS = a.ZX_TPS;
        this.ZX_GEAR = a.ZX_GEAR;
        this.LLC_AFR = a.LLC_AFR;
        this.ZX_ECT = a.ZX_ECT;
        this.skip = a.isSkip();
        this.egoOffsetApplied = a.isEgoOffsetApplied();
        this.unadjustedAfr = a.getUnadjustedAfr();
    }

    public LogValue(int time, double ZX_RPM, double ZX_TPS, double ZX_GEAR, double LLC_AFR) {
        this.Time = time;
        this.ZX_RPM = ZX_RPM;
        this.ZX_TPS = ZX_TPS;
        this.ZX_GEAR = ZX_GEAR;
        this.LLC_AFR = LLC_AFR;
    }

    public LogValue(double ZX_RPM, double ZX_TPS, double LLC_AFR) {
        this.ZX_RPM = ZX_RPM;
        this.ZX_TPS = ZX_TPS;
        this.LLC_AFR = LLC_AFR;
    }

    /**
     * Get time in millis
     *
     * @return
     */
    public int getTime() {
        return Time;
    }

    /**
     * Set time int millis
     *
     * @param time
     */
    public void setTime(int time) {
        Time = time;
    }

    public double getRpm() {
        return ZX_RPM;
    }

    public void setRpm(double ZX_RPM) {
        this.ZX_RPM = ZX_RPM;
    }

    public double getTps() {
        return ZX_TPS;
    }

    public void setTps(double ZX_TPS) {
        this.ZX_TPS = ZX_TPS;
    }

    public double getGear() {
        return ZX_GEAR;
    }

    public void setGear(double ZX_GEAR) {
        this.ZX_GEAR = ZX_GEAR;
    }

    public double getAfr() {
        return LLC_AFR;
    }

    public void setAfr(double LLC_AFR) {
        if (egoOffsetApplied) {
            this.unadjustedAfr = this.LLC_AFR;
        }
        this.LLC_AFR = LLC_AFR;
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
        return ZX_ECT;
    }

    public void setEct(double ZC_ECT) {
        this.ZX_ECT = ZC_ECT;
    }

    public double getUnadjustedAfr() {
        if (egoOffsetApplied) {
            return unadjustedAfr;
        }

        return this.LLC_AFR;

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

    @Override
    public String toString() {
        return "LogValue{" +
                "Time=" + Time +
                ", ZX_RPM=" + ZX_RPM +
                ", ZX_TPS=" + ZX_TPS +
                ", ZX_GEAR=" + ZX_GEAR +
                ", LLC_AFR=" + LLC_AFR +
                ", skip=" + skip +
                '}' + '\n';
    }
}
