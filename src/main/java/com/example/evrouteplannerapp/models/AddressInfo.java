package com.example.evrouteplannerapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressInfo {

    @JsonProperty("ID")
    int id;
    @JsonProperty("Title")
    String title;
    @JsonProperty("AddressLine1")
    String addressLine1;
    @JsonProperty("AddressLine2")
    String addressLine2;
    @JsonProperty("Town")
    String town;
    @JsonProperty("StateOrProvince")
    String stateOrProvince;
    @JsonProperty("Postcode")
    String postcode;
    @JsonProperty("CountryID")
    int countryID;
    @JsonProperty("Latitude")
    double latitude;
    @JsonProperty("Longitude")
    double longitude;
    @JsonProperty("ContactTelephone1")
    String contactTelephone1;
    @JsonProperty("ContactTelephone2")
    String contactTelephone2;
    @JsonProperty("ContactEmail")
    String contactEmail;
    @JsonProperty("AccessComments")
    String accessComments;
    @JsonProperty("RelatedURL")
    String relatedURL;
    @JsonProperty("DistanceUnit")
    int distanceUnit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getContactTelephone1() {
        return contactTelephone1;
    }

    public void setContactTelephone1(String contactTelephone1) {
        this.contactTelephone1 = contactTelephone1;
    }

    public String getContactTelephone2() {
        return contactTelephone2;
    }

    public void setContactTelephone2(String contactTelephone2) {
        this.contactTelephone2 = contactTelephone2;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAccessComments() {
        return accessComments;
    }

    public void setAccessComments(String accessComments) {
        this.accessComments = accessComments;
    }

    public String getRelatedURL() {
        return relatedURL;
    }

    public void setRelatedURL(String relatedURL) {
        this.relatedURL = relatedURL;
    }

    public int getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(int distanceUnit) {
        this.distanceUnit = distanceUnit;
    }
}
