package com.group2.fireshare.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Repository {
    private static Repository instance;
    private ObservableList<FileItem> fileList = FXCollections.observableArrayList();

    private Repository(){}

    public static Repository getInstance() {
        if (instance == null){
            synchronized (Repository.class){
                if (instance == null){
                    instance = new Repository();
                }
            }
        }
        return instance;
    }

    public synchronized ObservableList<FileItem> getFileList() {
        return fileList;
    }

    public synchronized void addFile(FileItem fileItem) {
        fileList.add(fileItem);
    }

    public synchronized boolean hasFile(String pname) {
        if (fileList.isEmpty())
            return false;
        for (FileItem fileItem:fileList ) {
            if (fileItem.getPname().equals(pname)) {
                return true;
            }
        }
        return false;
    }

    public synchronized String getFilePath(String pname) {
        if (fileList.isEmpty())
            return "";
        for (FileItem fileItem:fileList ) {
            if (fileItem.getPname().equals(pname)) {
                return fileItem.getLname();
            }
        }
        return "";
    }

    public synchronized boolean isFileCreatedByConsole(String filename) {
        if (fileList.isEmpty())
            return false;
        for (FileItem fileItem:fileList ) {
            if (fileItem.getPname().equals(filename)) {
                return fileItem.isCreatedByConsole();
            }
        }
        return false;
    }

}
