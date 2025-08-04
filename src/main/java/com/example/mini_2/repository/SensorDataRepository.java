package com.example.mini_2.repository;


import com.example.mini_2.entity.SensorDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorDataEntity, Long> {
    // 기본 CRUD는 JpaRepository가 자동 제공함
}
