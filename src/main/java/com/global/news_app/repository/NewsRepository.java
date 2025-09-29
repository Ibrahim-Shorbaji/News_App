package com.global.news_app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.global.news_app.models.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.global.news_app.models.News;
import com.global.news_app.models.User;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {


    @Query(value = "SELECT * FROM news n WHERE n.deleted = 0", nativeQuery = true)
    List<News> findByIsDeletedFalse();

    @Query(value = "SELECT * FROM news n WHERE n.status = :status AND n.deleted = 0", nativeQuery = true)
    List<News> findByStatusAndIsDeletedFalse(@Param("status") String status);



    @Query(value = "SELECT * FROM news n WHERE n.publish_date < :date AND n.deleted = false", nativeQuery = true)
    List<News> findExpiredNews(@Param("date") LocalDate date);

}
