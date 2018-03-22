package com.larz1.afranalyzer;

public class LogValue {
    public double Time;
    public double ZX_RPM;
    public double ZX_TPS;
    public double ZX_GEAR;
    public double LLC_AFR;
    public double ZC_ECT;
    private boolean skip = false;
    private boolean egoOffsetApplied = false;


    public LogValue() {
    }

    public LogValue(int time, double ZX_RPM, double ZX_TPS, double LLC_AFR) {
        Time = time/1000.0;
        this.ZX_RPM = ZX_RPM;
        this.ZX_TPS = ZX_TPS;
        this.LLC_AFR = LLC_AFR;
    }

    public LogValue(LogValue a) {
        this.Time = a.getTime()/1000;
        this.ZX_RPM = a.getRpm();
        this.ZX_TPS = a.getTps();
        this.ZX_GEAR = a.getGear();
        this.LLC_AFR = a.getAfr();
        this.skip = a.isSkip();
    }

    public LogValue(int time, double ZX_RPM, double ZX_TPS, double ZX_GEAR, double LLC_AFR) {
        this.Time = time/1000;
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
     * @return
     */
    public int getTime() {
        return (int) (Time * 1000);
    }

    /**
     * Set time int millis
     * @param time
     */
    public void setTime(int time) {
        Time = time/1000;
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
        return ZC_ECT;
    }

    public void setEct(double ZC_ECT) {
        this.ZC_ECT = ZC_ECT;
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
