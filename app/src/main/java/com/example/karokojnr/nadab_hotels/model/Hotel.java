package com.example.karokojnr.nadab_hotels.model;

import com.google.gson.annotations.SerializedName;
/*
* Hotel model
* Contains the hotel fields from API
* Consumed by
* */
public class Hotel {

    @SerializedName("_id")
    private String id;

    @SerializedName("businessName")
    private String businessName;

    @SerializedName("applicantName")
    private String applicantName;

    @SerializedName("payBillNo")
    private String payBillNo;

    @SerializedName("mobileNumber")
    private int mobileNumber;

    @SerializedName("city")
    private String city;

    @SerializedName("address")
    private String address;

    @SerializedName("businessEmail")
    private String businessEmail;

    @SerializedName("filename")
    private String filename;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("image")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @SerializedName("password")
    private String password;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getPayBillNo() {
        return payBillNo;
    }

    public void setPayBillNo(String payBillNo) {
        this.payBillNo = payBillNo;
    }

    public String getMobileNumber() {
        return "0"+mobileNumber;
    }

    public void setMobileNumber(int mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "businessName='" + businessName + '\'' +
                ", applicantName='" + applicantName + '\'' +
                ", payBillNo='" + payBillNo + '\'' +
                ", mobileNumber=" + mobileNumber +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", businessEmail='" + businessEmail + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", image='" + image + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
