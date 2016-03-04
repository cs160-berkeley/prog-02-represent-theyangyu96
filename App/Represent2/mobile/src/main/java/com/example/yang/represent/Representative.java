package com.example.yang.represent;

/**
 * Created by Yang on 3/1/2016.
 */
public class Representative {
    public String tweet, position, party, fullName, termEnd;
    public String webAdress, emailAdress;
    public int picAdress;
    public Representative(String name, String party, String position, String termEnd, String tweet, int picAdress, String webAdress, String emailAdress) {
        this.tweet = tweet;
        this.position = position;
        this.fullName = name;
        this.party = party;
        this.termEnd = termEnd;
        this.picAdress = picAdress;
        this.webAdress = webAdress;
        this.emailAdress = emailAdress;
    }
    public String getTweet() {return tweet;}
    public String getPosition(){return position;}
    public String getParty() {return party;}
    public String getFullName() {return fullName;}
    public int getPicAdress() {return picAdress;}
    public String getWebAdress() {return webAdress;}
    public String getEmailAdress() {return emailAdress;}
    public String getTermEnd() {return termEnd;}
}
