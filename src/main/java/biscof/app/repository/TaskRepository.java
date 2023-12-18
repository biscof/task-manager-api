package biscof.app.repository;

import biscof.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>,
        QuerydslPredicateExecutor<Task> {

    Optional<Task> findTaskById(Long id);

    Optional<Task> findTaskByTitle(String title);

}
