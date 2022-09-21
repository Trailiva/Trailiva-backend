package com.trailiva.data.repository;

import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Tab;
import com.trailiva.data.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByDueDate(LocalDate dueDate);

    @Modifying
    @Query(nativeQuery = true, value = "update Task t set elapse = true where CURRENT_DATE > t.due_date and elapse = false")
    void updateDueTask();
}
