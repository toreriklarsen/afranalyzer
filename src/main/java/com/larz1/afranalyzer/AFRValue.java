package com.larz1.afranalyzer;

public class AFRValue {
    // todo make private NBNBNBNB
    public double Time;
    public double ZX_RPM;
    public double ZX_TPS;
    public double ZX_GEAR;
    public double LLC_AFR;
    private boolean skip = false;

    public AFRValue() {
    }

    public AFRValue(AFRValue a) {
        this.Time = a.getTime();
        this.ZX_RPM = a.getZX_RPM();
        this.ZX_TPS = a.getZX_TPS();
        this.ZX_GEAR = a.getZX_GEAR();
        this.LLC_AFR = a.getLLC_AFR();
        this.skip = a.isSkip();
    }

    public AFRValue(double time, double ZX_RPM, double ZX_TPS, double ZX_GEAR, double LLC_AFR) {
        this.Time = time;
        this.ZX_RPM = ZX_RPM;
        this.ZX_TPS = ZX_TPS;
        this.ZX_GEAR = ZX_GEAR;
        this.LLC_AFR = LLC_AFR;
    }

    public AFRValue(double ZX_RPM, double ZX_TPS, double LLC_AFR) {
        this.ZX_RPM = ZX_RPM;
        this.ZX_TPS = ZX_TPS;
        this.LLC_AFR = LLC_AFR;
    }

    public double getTime() {
        return Time;
    }

    public void setTime(double time) {
        Time = time;
    }

    public double getZX_RPM() {
        return ZX_RPM;
    }

    public void setZX_RPM(double ZX_RPM) {
        this.ZX_RPM = ZX_RPM;
    }

    public double getZX_TPS() {
        return ZX_TPS;
    }

    public void setZX_TPS(double ZX_TPS) {
        this.ZX_TPS = ZX_TPS;
    }

    public double getZX_GEAR() {
        return ZX_GEAR;
    }

    public void setZX_GEAR(double ZX_GEAR) {
        this.ZX_GEAR = ZX_GEAR;
    }

    public double getLLC_AFR() {
        return LLC_AFR;
    }

    public void setLLC_AFR(double LLC_AFR) {
        this.LLC_AFR = LLC_AFR;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    @Override
    public String toString() {
        return "AFRValue{" +
                "Time=" + Time +
                ", ZX_RPM=" + ZX_RPM +
                ", ZX_TPS=" + ZX_TPS +
                ", ZX_GEAR=" + ZX_GEAR +
                ", LLC_AFR=" + LLC_AFR +
                ", skip=" + skip +
                '}' +  '\n';
    }
}
