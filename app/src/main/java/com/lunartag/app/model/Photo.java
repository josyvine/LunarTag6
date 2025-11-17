package com.lunartag.app.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

/**
 * A simple data model class (POJO) to represent a photo record.
 * This object structure will be used to save and retrieve data from the Firestore 'photos' collection.
 */
public class Photo {

    private String filePath;
    private Date assignedTimestamp;
    private Date captureTimestampReal;
    private double lat;
    private double lon;
    private double accuracyMeters;
    private String addressHuman;
    private String shiftStart;
    private String shiftEnd;
    private String watermarkName;
    private String companyName;
    private Date sendScheduledAt;
    private String status; // e.g., "PENDING", "SENT", "FAILED"

    @ServerTimestamp
    private Date createdAt;

    // A no-argument constructor is required for Firestore data mapping
    public Photo() {}

    // --- Getters and Setters for all fields ---

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getAssignedTimestamp() {
        return assignedTimestamp;
    }

    public void setAssignedTimestamp(Date assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    public Date getCaptureTimestampReal() {
        return captureTimestampReal;
    }

    public void setCaptureTimestampReal(Date captureTimestampReal) {
        this.captureTimestampReal = captureTimestampReal;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAccuracyMeters() {
        return accuracyMeters;
    }

    public void setAccuracyMeters(double accuracyMeters) {
        this.accuracyMeters = accuracyMeters;
    }

    public String getAddressHuman() {
        return addressHuman;
    }

    public void setAddressHuman(String addressHuman) {
        this.addressHuman = addressHuman;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(String shiftStart) {
        this.shiftStart = shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(String shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public String getWatermarkName() {
        return watermarkName;
    }

    public void setWatermarkName(String watermarkName) {
        this.watermarkName = watermarkName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getSendScheduledAt() {
        return sendScheduledAt;
    }

    public void setSendScheduledAt(Date sendScheduledAt) {
        this.sendScheduledAt = sendScheduledAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
          }
