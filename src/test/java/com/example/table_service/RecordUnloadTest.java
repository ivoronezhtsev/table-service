package com.example.table_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.table_service.entity.Record;
import com.example.table_service.repository.RecordRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RecordUnloadTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecordRepository recordRepository;

    private final int TOTAL_CONNECTIONS = 100;
    private final int TOTAL_REQUESTS = 1000000;

    @BeforeEach
    public void addRecords() {
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            Record record = new Record();
            record.setName("Name " + i);
            record.setDescription("Description " + i);
            records.add(record);
        }
        recordRepository.saveAll(records);
    }

    @Test
    public void testConcurrentRequests() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_CONNECTIONS);
        List<Callable<Long>> tasks = new ArrayList<>();
        Random random = new Random();


        for (int i = 0; i < TOTAL_CONNECTIONS; i++) {
            tasks.add(() -> {
                long startTime = System.nanoTime();
                for (int j = 0; j < TOTAL_REQUESTS / TOTAL_CONNECTIONS; j++) {
                    long randomId = 1 + random.nextInt(TOTAL_REQUESTS);
                    MvcResult result = mockMvc.perform(get("/api/records/" + randomId))
                            .andExpect(status().isOk())
                            .andReturn();
                }
                long endTime = System.nanoTime();
                return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            });
        }

        List<Future<Long>> results = executorService.invokeAll(tasks);

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        List<Long> times = new ArrayList<>();
        for (Future<Long> future : results) {
            times.add(future.get());
        }

        printStatistics(times);
    }

    private void printStatistics(List<Long> times) {
        times.sort(Long::compareTo);

        long totalTime = times.stream().mapToLong(Long::longValue).sum();
        double median = times.get(times.size() / 2);
        double percentile95 = times.get((int) (times.size() * 0.95));
        double percentile99 = times.get((int) (times.size() * 0.99));

        System.out.println("Общее время выполнения (мс): " + totalTime);
        System.out.println("Медианное время (мс): " + median);
        System.out.println("95 процентиль (мс): " + percentile95);
        System.out.println("99 процентиль (мс): " + percentile99);
    }
}
