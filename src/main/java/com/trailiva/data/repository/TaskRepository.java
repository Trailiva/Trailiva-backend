package com.trailiva.data.repository;

import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Tab;
import com.trailiva.data.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsTaskByName(String name);
    List<Task> findByPriority(Priority priority);
    List<Task> findByTab(Tab tab);
    List<Task> findByDueDate(LocalDate dueDate);
}
