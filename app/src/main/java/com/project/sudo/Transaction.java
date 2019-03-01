package com.project.sudo;

import java.util.*;
public class Transaction {
    public String Hash;
    public String PrevHash;
    private String TransID;
    private String SenderID;
    private String ReceiverID;
    private String Amount;
    private long TimeStamp;
    public Transaction(String TransID ,String SenderID , String RevceiverID ,String Amount ,String PrevHash){
        this.TransID = TransID;
        this.SenderID=SenderID;
        this.ReceiverID=RevceiverID;
        this.Amount = Amount;
        this.TimeStamp = new Date().getTime();
        this.PrevHash = PrevHash;
        this.Hash = calculateHash();

    }
    public String calculateHash(){
        String data = TransID+SenderID+ReceiverID+Amount+TimeStamp+PrevHash;
        String calHash = Encryption.applySha256(data);
        return  calHash;
    }
}
