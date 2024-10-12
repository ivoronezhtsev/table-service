package com.example.table_service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.example.table_service.repository.RecordRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb_${random.value}")
public class RecordLoadTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecordRepository recordRepository;

    @Test
    @Transactional
    public void create100KRecords() throws Exception {
        for (int i = 0; i < 100_000; i++) {
            String recordJson = String.format("{\"name\": \"Name %d\", \"description\": \"Description %d\"}", i, i);
            mockMvc.perform(post("/api/records")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(recordJson))
                    .andExpect(status().isOk());

        }

        long count = recordRepository.count();
        Assertions.assertEquals(100000, count, "В таблице должно быть 100 000 записей");
    }
}