package com.cognizant.ormlearn.repository;

import com.cognizant.ormlearn.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
    
    @Query("SELECT a FROM Attempt a " +
           "left join fetch a.user u " +
           "left join fetch a.attemptQuestions aq " +
           "left join fetch aq.question q " +
           "left join fetch aq.attemptOptions ao " +
           "left join fetch ao.options o " +
           "WHERE u.id = :userId AND a.id = :attemptId")
    Attempt getAttempt(@Param("userId") int userId, @Param("attemptId") int attemptId);
}
