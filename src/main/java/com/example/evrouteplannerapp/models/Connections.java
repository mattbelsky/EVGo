package com.example.evrouteplannerapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Connections {

    @JsonProperty("ID")
    int id;
    @JsonProperty("ConnectionTypeID")
    int connectionTypeID;
    @JsonProperty("StatusTypeID")
    int statusTypeID;
    @JsonProperty("LevelID")
    int levelID;
    @JsonProperty("PowerKW")
    int powerKW;
    @JsonProperty("CurrentTypeID")
    int currentTypeID;
    @JsonProperty("Quantity")
    int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConnectionTypeID() {
        return connectionTypeID;
    }

    public void setConnectionTypeID(int connectionTypeID) {
        this.connectionTypeID = connectionTypeID;
    }

    public int getStatusTypeID() {
        return statusTypeID;
    }

    public void setStatusTypeID(int statusTypeID) {
        this.statusTypeID = statusTypeID;
    }

    public int getLevelID() {
        return levelID;
    }

    public void setLevelID(int levelID) {
        this.levelID = levelID;
    }

    public int getPowerKW() {
        return powerKW;
    }

    public void setPowerKW(int powerKW) {
        this.powerKW = powerKW;
    }

    public int getCurrentTypeID() {
        return currentTypeID;
    }

    public void setCurrentTypeID(int currentTypeID) {
        this.currentTypeID = currentTypeID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
