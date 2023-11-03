package com.group2.fireshare.client.model;

public class FileItem {
    private String lname;
    private String pname;
    private boolean isCreatedByConsole = false;

    public FileItem(String lname, String pname) {
        this.lname = lname;
        this.pname = pname;
    }

    public FileItem(String lname, String pname, boolean isCreatedByConsole) {
        this.lname = lname;
        this.pname = pname;
        this.isCreatedByConsole = isCreatedByConsole;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public boolean isCreatedByConsole() {
        return isCreatedByConsole;
    }
}
