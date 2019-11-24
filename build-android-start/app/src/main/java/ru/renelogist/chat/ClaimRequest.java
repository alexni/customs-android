package ru.renelogist.chat;

import java.io.Serializable;

public class ClaimRequest implements Serializable {

    public String surname;
    public String name;
    public String secondName;
    public String phone;
    public String birthdate;
    public String passportPrefix;
    public String passportNumber;
    public String passportDate;
    public String operationType;
    public String operationTypes;
    public String trackNumber;
    public String trailerNumber;
    public String checkPoint;
    public String payer;
    public String carrier;
    public String comment;

    public ClaimRequest(){
    }

    public ClaimRequest(String surname, String name, String secondName, String phone, String birthdate, String passportPrefix, String passportNumber, String passportDate, String operationType, String trackNumber, String trailerNumber, String checkPoint, String payer, String carrier, String comment) {
        this.surname = surname;
        this.name = name;
        this.secondName = secondName;
        this.phone = phone;
        this.birthdate = birthdate;
        this.passportPrefix = passportPrefix;
        this.passportNumber = passportNumber;
        this.passportDate = passportDate;
        this.operationType = operationType;
        this.trackNumber = trackNumber;
        this.trailerNumber = trailerNumber;
        this.checkPoint = checkPoint;
        this.payer = payer;
        this.carrier = carrier;
        this.comment = comment;
    }

    public ClaimRequest(String surname, String name, String secondName, String phone,
            String birthdate, String passportPrefix, String passportNumber,
            String passportDate, String operationType, String operationTypes,
            String trackNumber, String trailerNumber, String checkPoint, String payer,
            String carrier, String comment) {
        this.surname = surname;
        this.name = name;
        this.secondName = secondName;
        this.phone = phone;
        this.birthdate = birthdate;
        this.passportPrefix = passportPrefix;
        this.passportNumber = passportNumber;
        this.passportDate = passportDate;
        this.operationType = operationType;
        this.operationTypes = operationTypes;
        this.trackNumber = trackNumber;
        this.trailerNumber = trailerNumber;
        this.checkPoint = checkPoint;
        this.payer = payer;
        this.carrier = carrier;
        this.comment = comment;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPassportPrefix() {
        return passportPrefix;
    }

    public void setPassportPrefix(String passportPrefix) {
        this.passportPrefix = passportPrefix;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportDate() {
        return passportDate;
    }

    public void setPassportDate(String passportDate) {
        this.passportDate = passportDate;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public void setTrailerNumber(String trailerNumber) {
        this.trailerNumber = trailerNumber;
    }

    public String getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(String checkPoint) {
        this.checkPoint = checkPoint;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(String operationTypes) {
        this.operationTypes = operationTypes;
    }

}