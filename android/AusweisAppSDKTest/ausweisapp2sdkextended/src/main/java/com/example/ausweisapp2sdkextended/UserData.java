package com.example.ausweisapp2sdkextended;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserData {
    private String address;
    private String birthName;
    private String familyName;
    private String givenNames;
    private String placeOfBirth;
    private String dateOfBirth;
    private String doctoralDegree;
    private String artisticName;
    private String pseudonym;
    private String validUntil;
    private String nationality;
    private String issuingCountry;
    private String documentType;
    private String residencePermitI;
    private String residencePermitII;
    private String communityID;
    private String addressVerification;
    private String ageVerification;

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

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
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

    public String getResidencePermitI() {
        return residencePermitI;
    }

    public void setResidencePermitI(String residencePermitI) {
        this.residencePermitI = residencePermitI;
    }

    public String getResidencePermitII() {
        return residencePermitII;
    }

    public void setResidencePermitII(String residencePermitII) {
        this.residencePermitII = residencePermitII;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getAddressVerification() {
        return addressVerification;
    }

    public void setAddressVerification(String addressVerification) {
        this.addressVerification = addressVerification;
    }

    public String getAgeVerification() {
        return ageVerification;
    }

    public void setAgeVerification(String ageVerification) {
        this.ageVerification = ageVerification;
    }

    public byte[] uuid() {
        StringBuilder sb = new StringBuilder();
        sb.append(address);
        sb.append('\n');
        sb.append(birthName);
        sb.append('\n');
        sb.append(familyName);
        sb.append('\n');
        sb.append(givenNames);
        sb.append('\n');
        sb.append(placeOfBirth);
        sb.append('\n');
        sb.append(dateOfBirth);
        sb.append('\n');
        sb.append(doctoralDegree);
        sb.append('\n');
        sb.append(artisticName);
        sb.append('\n');
        sb.append(pseudonym);
        sb.append('\n');
        sb.append(nationality);
        sb.append('\n');
        sb.append(issuingCountry);
        sb.append('\n');
        sb.append(documentType);
        sb.append('\n');
        sb.append(residencePermitI);
        sb.append('\n');
        sb.append(residencePermitII);
        sb.append('\n');
        sb.append(communityID);

        try {
            return MessageDigest.getInstance("SHA-256").digest(sb.toString().getBytes());
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
                ", pseudonym='" + pseudonym + '\'' +
                ", validUntil='" + validUntil + '\'' +
                ", nationality='" + nationality + '\'' +
                ", issuingCountry='" + issuingCountry + '\'' +
                ", documentType='" + documentType + '\'' +
                ", residencePermitI='" + residencePermitI + '\'' +
                ", residencePermitII='" + residencePermitII + '\'' +
                ", communityID='" + communityID + '\'' +
                ", addressVerification='" + addressVerification + '\'' +
                ", ageVerification='" + ageVerification + '\'' +
                '}';
    }
}
