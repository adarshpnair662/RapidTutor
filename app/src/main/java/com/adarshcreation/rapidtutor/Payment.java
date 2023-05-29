package com.adarshcreation.rapidtutor;

public class Payment {

    private String TransactionID,Sender,Receiver;
    private String Time;
    private String Amount,Name;

    public Payment() {
    }

    public Payment(String transactionID,String amount, String sender, String receiver,String  time, String name) {
    TransactionID = transactionID;
    Amount = amount;
    Sender = sender;
    Receiver = receiver;
    Time = time;
    Name = name;
    }

    public String getName() {
        return Name;
    }

    public String getAmount() {
        return Amount;
    }

    public String getSender() {
        return Sender;
    }

    public String getTime() {
        return Time;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }
}