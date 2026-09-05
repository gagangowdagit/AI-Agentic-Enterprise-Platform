package com.rag.ragbackend.repository;

import com.rag.ragbackend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByProjectId(String projectId);
}