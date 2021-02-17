package com.technova.help;

/**
 * Created by N on 11/30/2017.
 */

public class ResponseAdapter {
    public String helperName;
    public String helperContact;
    public String comment;

    public ResponseAdapter(String helperName, String helperContact, String comment) {
        this.helperName = helperName;
        this.helperContact = helperContact;
        this.comment = comment;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getHelperContact() {
        return helperContact;
    }

    public void setHelperContact(String helperContact) {
        this.helperContact = helperContact;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ResponseAdapter() {
    }
}
