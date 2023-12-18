package biscof.app.repository;

import biscof.app.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>,
        QuerydslPredicateExecutor<Comment> {

    Optional<Comment> findCommentById(Long id);

    Optional<Comment> findCommentByTitle(String title);

}
