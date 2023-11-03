package com.group2.fireshare.server.model;

import javafx.application.Platform;
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

    public synchronized void removeFileByHostname(String hostname, int port) {
        if (fileList.isEmpty())
            return;
        for (FileItem fileItem : fileList) {
            if (fileItem.getHostname().equals(hostname) && fileItem.getPort() == port) {
                // remove later to avoid error
                Platform.runLater(() -> {
                    fileList.remove(fileItem);
                });
            }
        }
    }

    public synchronized FileItem getFirstFileItemByName(String name) {
        // TODO: return List<FileItem> in case improve fetch method
        // Because FileItem doesn't have setter -> doesn't conflict in concurrency
        if (fileList.isEmpty())
            return null;
        for (FileItem fileItem : fileList) {
            if (fileItem.getFileName().equals(name)) {
                return fileItem;
            }
        }
        return null;
    }

}
