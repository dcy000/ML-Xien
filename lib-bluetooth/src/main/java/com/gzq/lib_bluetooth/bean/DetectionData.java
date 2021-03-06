package com.gzq.lib_bluetooth.bean;

import com.gzq.lib_core.utils.DeviceUtils;

import java.io.Serializable;

public class DetectionData implements Serializable {
    private Float bloodOxygen;
    private Float bloodSugar;
    private Float cholesterol;
    private String detectionType;
    private String ecg;
    private String eqid = DeviceUtils.getIMEI();
    private Integer heartRate;
    private Integer highPressure;
    private Integer lowPressure;
    private Integer offset;
    private Integer pulse;
    private Integer state;
    private Integer sugarTime;
    private Float temperAture;
    private String time = String.valueOf(System.currentTimeMillis());
    private Float uricAcid;
    private Integer userid;
    private Float weight;
    private String yz;
    private Integer zid;
    private String result;
    private String resultUrl;

    public DetectionData() {

    }

    public Float getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(Float bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public Float getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(Float bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public Float getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(Float cholesterol) {
        this.cholesterol = cholesterol;
    }

    public String getDetectionType() {
        return detectionType;
    }

    public void setDetectionType(String detectionType) {
        this.detectionType = detectionType;
    }

    public String getEcg() {
        return ecg;
    }

    public void setEcg(String ecg) {
        this.ecg = ecg;
    }

    public String getEqid() {
        return eqid;
    }

    public void setEqid(String eqid) {
        this.eqid = eqid;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getHighPressure() {
        return highPressure;
    }

    public void setHighPressure(Integer highPressure) {
        this.highPressure = highPressure;
    }

    public Integer getLowPressure() {
        return lowPressure;
    }

    public void setLowPressure(Integer lowPressure) {
        this.lowPressure = lowPressure;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getSugarTime() {
        return sugarTime;
    }

    public void setSugarTime(Integer sugarTime) {
        this.sugarTime = sugarTime;
    }

    public float getTemperAture() {
        return temperAture;
    }

    public void setTemperAture(Float temperAture) {
        this.temperAture = temperAture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getUricAcid() {
        return uricAcid;
    }

    public void setUricAcid(Float uricAcid) {
        this.uricAcid = uricAcid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getYz() {
        return yz;
    }

    public void setYz(String yz) {
        this.yz = yz;
    }

    public Integer getZid() {
        return zid;
    }

    public void setZid(Integer zid) {
        this.zid = zid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }
}
