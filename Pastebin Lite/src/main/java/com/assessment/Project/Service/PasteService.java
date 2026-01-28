package com.assessment.Project.Service;

import com.assessment.Project.Model.Paste;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasteService {

    private final Map<String, Paste> store = new ConcurrentHashMap<>();

    public void save(Paste paste) {
        store.put(paste.getId(), paste);
    }

    public Paste find(String id) {
        System.out.println("STORE KEYS: " + store.keySet());
        return store.get(id);
    }

    public void incrementViews(Paste paste) {
        paste.setViews(paste.getViews() + 1);
    }
}
