package com.group2.fireshare.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FetchList {
    private static FetchList instance;
    private ObservableList<FetchItem> fetchList = FXCollections.observableArrayList();

    private FetchList(){}

    public static FetchList getInstance() {
        if (instance == null){
            synchronized (FetchList.class){
                if (instance == null){
                    instance = new FetchList();
                }
            }
        }
        return instance;
    }

    public synchronized ObservableList<FetchItem> getFetchList() {
        return fetchList;
    }

    public synchronized void addFetchItem(FetchItem fetchItem) {
        fetchList.add(fetchItem);
    }


    public synchronized void setCancelFetchItem(String name, String content) {
        if (fetchList.isEmpty())
            return;
        for (FetchItem fetchItem: fetchList) {
            if (fetchItem.getName().equals(name) && fetchItem.getStatus().equals("Fetching")) {
                fetchItem.setStatus(content);
                if (fetchItem.isCreatedByConsole()) {
                    ClientConsole.getInstance().addText(content);
                }
                return;
            }
        }
    }

    public synchronized FetchItem getFetchItemFetching(String name) {
        if (fetchList.isEmpty())
            return null;

        for (FetchItem fetchItem: fetchList) {
            if (fetchItem.getName().equals(name) && fetchItem.getStatus().equals("Fetching")) {
                return fetchItem;
            }
        }

        return null;
    }

    public synchronized boolean hasFetchItemStartWithStatus(String name, String status) {
        if (fetchList.isEmpty())
            return false;

        for (FetchItem fetchItem: fetchList) {
            if (fetchItem.getName().equals(name) && fetchItem.getStatus().toLowerCase().startsWith(status.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
