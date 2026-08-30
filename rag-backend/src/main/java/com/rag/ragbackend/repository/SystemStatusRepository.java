package com.rag.ragbackend.repository;

import org.springframework.stereotype.Repository;

@Repository
public class SystemStatusRepository {

    public String getStatus() {
        return "UP";
    }
}
