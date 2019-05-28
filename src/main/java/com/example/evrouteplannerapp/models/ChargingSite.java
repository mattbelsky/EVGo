package com.example.evrouteplannerapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargingSite {

    @JsonProperty("ID")
    int id;
    @JsonProperty("UUID")
    String uuid;
    @JsonProperty("DataProviderID")
    int dataProviderID;
    @JsonProperty("OperatorID")
    int operatorID;
    @JsonProperty("OperatorsReference")
    String operatorsReference;
    @JsonProperty("UsageTypeID")
    int usageTypeID;
    @JsonProperty("UsageCost")
    String usageCost;
    @JsonProperty("AddressInfo")
    AddressInfo addressInfo;
    @JsonProperty("NumberOfPoints")
    int numberOfPoints;
    @JsonProperty("GeneralComments")
    String generalComments;
    @JsonProperty("StatusTypeID")
    int statusTypeID;
    @JsonProperty("DateLastStatusUpdate")
    String dateLastStatusUpdate;
    @JsonProperty("DataQualityLevel")
    int dataQualityLevel;
    @JsonProperty("DateCreated")
    String dateCreated;
    @JsonProperty("SubmissionStatusTypeID")
    int submissionStatusTypeID;
    @JsonProperty("Connections")
    Connections[] connections;
    @JsonProperty("IsRecentlyVerified")
    boolean recentlyVerified;
    @JsonProperty("DateLastVerified")
    String dateLastVerified;

    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public int getDataProviderID() {
        return dataProviderID;
    }

    public int getOperatorID() {
        return operatorID;
    }

    public String getOperatorsReference() {
        return operatorsReference;
    }

    public int getUsageTypeID() {
        return usageTypeID;
    }

    public String getUsageCost() {
        return usageCost;
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public String getGeneralComments() {
        return generalComments;
    }

    public int getStatusTypeID() {
        return statusTypeID;
    }

    public String getDateLastStatusUpdate() {
        return dateLastStatusUpdate;
    }

    public int getDataQualityLevel() {
        return dataQualityLevel;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public int getSubmissionStatusTypeID() {
        return submissionStatusTypeID;
    }

    public Connections[] getConnections() {
        return connections;
    }

    public boolean isRecentlyVerified() {
        return recentlyVerified;
    }

    public String getDateLastVerified() {
        return dateLastVerified;
    }
}

