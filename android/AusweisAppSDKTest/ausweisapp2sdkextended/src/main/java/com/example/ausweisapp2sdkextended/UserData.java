package com.example.ausweisapp2sdkextended;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UserData {
    private String address;
    private String birthName;
    private String familyName;
    private String givenNames;
    private String placeOfBirth;
    private String dateOfBirth;
    private String doctoralDegree;
    private String artisticName;
    private String nationality;
    private String issuingCountry;
    private String documentType;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthName() {
        return birthName;
    }

    public void setBirthName(String birthName) {
        this.birthName = birthName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDoctoralDegree() {
        return doctoralDegree;
    }

    public void setDoctoralDegree(String doctoralDegree) {
        this.doctoralDegree = doctoralDegree;
    }

    public String getArtisticName() {
        return artisticName;
    }

    public void setArtisticName(String artisticName) {
        this.artisticName = artisticName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getIssuingCountry() {
        return issuingCountry;
    }

    public void setIssuingCountry(String issuingCountry) {
        this.issuingCountry = issuingCountry;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public byte[] uuid() {
        try {
            return MessageDigest.getInstance("SHA-256").digest(this.toString().getBytes());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "UserData{" +
                "address='" + address + '\'' +
                ", birthName='" + birthName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", givenNames='" + givenNames + '\'' +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", doctoralDegree='" + doctoralDegree + '\'' +
                ", artisticName='" + artisticName + '\'' +
                ", nationality='" + nationality + '\'' +
                ", issuingCountry='" + issuingCountry + '\'' +
                ", documentType='" + documentType + '\'' +
                '}';
    }
}
