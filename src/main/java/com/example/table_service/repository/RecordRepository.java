package com.example.table_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.table_service.entity.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {
}