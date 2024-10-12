package com.example.table_service.controller;

import com.example.table_service.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.table_service.entity.Record;
import java.util.Optional;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private RecordRepository recordRepository;

    // Получить запись по id
    @GetMapping("/{id}")
    public ResponseEntity<Record> getRecordById(@PathVariable Long id) {
        Optional<Record> record = recordRepository.findById(id);
        return record.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Добавить новую запись
    @PostMapping
    public ResponseEntity<Record> createRecord(@RequestBody Record record) {
        Record savedRecord = recordRepository.save(record);
        return ResponseEntity.ok(savedRecord);
    }

    // Обновить запись по id
    @PutMapping("/{id}")
    public ResponseEntity<Record> updateRecord(@PathVariable Long id, @RequestBody Record newRecordData) {
        Optional<Record> recordOptional = recordRepository.findById(id);
        if (recordOptional.isPresent()) {
            Record recordToUpdate = recordOptional.get();
            recordToUpdate.setName(newRecordData.getName());
            recordToUpdate.setDescription(newRecordData.getDescription());
            Record updatedRecord = recordRepository.save(recordToUpdate);
            return ResponseEntity.ok(updatedRecord);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Удалить запись по id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        Optional<Record> recordOptional = recordRepository.findById(id);
        if (recordOptional.isPresent()) {
            recordRepository.delete(recordOptional.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
