package net.sharksystem.eID;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (address != null ? !address.equals(userData.address) : userData.address != null)
            return false;
        if (birthName != null ? !birthName.equals(userData.birthName) : userData.birthName != null)
            return false;
        if (familyName != null ? !familyName.equals(userData.familyName) : userData.familyName != null)
            return false;
        if (givenNames != null ? !givenNames.equals(userData.givenNames) : userData.givenNames != null)
            return false;
        if (placeOfBirth != null ? !placeOfBirth.equals(userData.placeOfBirth) : userData.placeOfBirth != null)
            return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(userData.dateOfBirth) : userData.dateOfBirth != null)
            return false;
        if (doctoralDegree != null ? !doctoralDegree.equals(userData.doctoralDegree) : userData.doctoralDegree != null)
            return false;
        if (artisticName != null ? !artisticName.equals(userData.artisticName) : userData.artisticName != null)
            return false;
        if (nationality != null ? !nationality.equals(userData.nationality) : userData.nationality != null)
            return false;
        if (issuingCountry != null ? !issuingCountry.equals(userData.issuingCountry) : userData.issuingCountry != null)
            return false;
        return documentType != null ? documentType.equals(userData.documentType) : userData.documentType == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (birthName != null ? birthName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (givenNames != null ? givenNames.hashCode() : 0);
        result = 31 * result + (placeOfBirth != null ? placeOfBirth.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (doctoralDegree != null ? doctoralDegree.hashCode() : 0);
        result = 31 * result + (artisticName != null ? artisticName.hashCode() : 0);
        result = 31 * result + (nationality != null ? nationality.hashCode() : 0);
        result = 31 * result + (issuingCountry != null ? issuingCountry.hashCode() : 0);
        result = 31 * result + (documentType != null ? documentType.hashCode() : 0);
        return result;
    }
}
