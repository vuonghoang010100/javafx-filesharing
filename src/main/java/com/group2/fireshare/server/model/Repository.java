package com.group2.fireshare.server.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Repository {
    private static Repository instance;
    private ObservableList<File> fileList = FXCollections.observableArrayList();

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

    public synchronized ObservableList<File> getFileList() {
        return fileList;
    }

    public synchronized void addFile(File file) {
        fileList.add(file);
    }

    public synchronized void removeFileByHostname(String hostname, int port) {
        if (fileList.isEmpty())
            return;
        for (File file: fileList) {
            if (file.getHostname().equals(hostname) && file.getPort() == port) {
                Platform.runLater(() -> {
                    fileList.remove(file);
                });
            }
        }
    }

}
