package com.project.sudo;

import java.util.ArrayList;
public class TransactionHistory {
    // Contains all transaction history chain.
    public static ArrayList<Transaction> TransactionChain = new ArrayList<Transaction>();
    // ----------------------------------------------------------------------------------
    public static String data;
    public static boolean isValidChain(ArrayList<Transaction> trans){

        if(trans.size()!=0) {
            Transaction currentBlock;
            Transaction PreviousBlock;

            for (int i = 1; i < trans.size(); i++) {
                currentBlock = trans.get(i);
                PreviousBlock = trans.get(i - 1);
                if (!currentBlock.PrevHash.equals(PreviousBlock.Hash)) {
                    return false;
                }
            }
        }
        return  true;
    }
    public static ReturnTest sending (String TransID , String SenderID , String ReciverID ,String Amount , ArrayList<Transaction> transDB){
        TransactionChain = transDB;

        if(isValidChain(TransactionChain)){
            if(TransactionChain.size()==0){
                TransactionChain.add(new Transaction("0", "null", "null", "0", "0"));
                TransactionChain.add(new Transaction(TransID,SenderID,ReciverID,Amount,TransactionChain.get(TransactionChain.size()-1).Hash));
                data="Thank you, Your Transaction is Successful.";
            }
            else {
                // TransDB should be updated ! in DataBase.
                TransactionChain.add(new Transaction(TransID,SenderID,ReciverID,Amount,TransactionChain.get(TransactionChain.size()-1).Hash));
                data = "Thank you for Coming again,Your Transaction is Successful.";
            }
        }
        else{
            ArrayList<Transaction> Empty = new ArrayList<>();
            TransactionChain = Empty;
        }
        ReturnTest ret = new ReturnTest(TransactionChain,data);
        return ret;
    }
}
