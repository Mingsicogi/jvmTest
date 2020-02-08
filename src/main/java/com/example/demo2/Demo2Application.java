package com.example.demo2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootApplication
@Slf4j
public class Demo2Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);
        long heapSize = Runtime.getRuntime().totalMemory();
        log.info("Heap Size : " + heapSize);
        log.info("Heap Size(M) : " + heapSize / (1024 * 1024) + " MB");
    }

    @RestController
    @RequestMapping("/arrayList/notAllocateSize")
    class ArrayListTest {

        @RequestMapping("")
        public ResponseEntity<String> test(Integer testSize) {
            log.info("=== Start notAllocateSize test controller ===");

            List<Integer> list = new ArrayList<>();
            Stream.iterate(1, i -> i + 1).limit(testSize).forEach(i -> list.add(1));

            log.info("notAllocateSize || testSize : " + testSize + ", ArrayList Size : " + list.size() + ", Success!");
            return ResponseEntity.ok("notAllocateSize || testSize : " + testSize + ", ArrayList Size : " + list.size() + ", Success!");
        }
    }

    @RestController
    @RequestMapping("/arrayList/allocateSize")
    class ArrayListTest2 {
        @RequestMapping("")
        public ResponseEntity<String> test(Integer testSize) {
            log.info("=== Start allocateSize test controller ===");

            List<Integer> list = new ArrayList<>(testSize);
            Stream.iterate(1, i -> i + 1).limit(testSize).forEach(i -> list.add(1));

            log.info("notAllocateSize || testSize : " + testSize + ", ArrayList Size : " + list.size() + ", Success!");
            return ResponseEntity.ok("allocateSize || testSize : " + testSize + ", ArrayList Size : " + list.size() + " Success!");
        }
    }

    @RestController
    @RequestMapping("/arrayList/listAggregation")
    class ArrayListTest3 {
        @RequestMapping("/badCase")
        public ResponseEntity<String> test(Integer testSize) {
            log.info("=== Start bad case listAggregation test controller ===");
            int basis = 100;

            List<Integer> result = new ArrayList<>();
            List<Integer> temp = new ArrayList<>(100);
            AtomicInteger counter = new AtomicInteger(1);
            AtomicInteger arrCopyCount = new AtomicInteger(1);
            for(int i = 0; i < testSize; i++) {
                temp.add(i);
                if(counter.getAndIncrement() == basis) {
                    result.addAll(temp);
                    temp.clear(); // init temp list
                    counter.set(1); // init counter
//                    log.info("### array copy {} ###", arrCopyCount.getAndIncrement());
                }
            }

            log.info("bad case listAggregation || testSize : " + testSize + ", Aggregation ArrayList Size : " + result.size() + ", Success!");
            return ResponseEntity.ok("bad case listAggregation || testSize : " + testSize + ", Aggregation ArrayList Size : " + result.size() + " Success!");
        }
    }

    @RestController
    @RequestMapping("/arrayList/listAggregation")
    class ArrayListTest4 {
        @RequestMapping("/betterCase")
        public ResponseEntity<String> test(Integer testSize) {
            log.info("=== Start better case listAggregation test controller ===");

            int basis = 100;

            List<Integer> result = new ArrayList<>(testSize);
            List<Integer> temp = new ArrayList<>(100);
            AtomicInteger counter = new AtomicInteger(1);
            AtomicInteger arrAddCount = new AtomicInteger(1);
            for(int i = 0; i < testSize; i++) {
                temp.add(i);
                if(counter.getAndIncrement() == basis) {
                    fillData(temp, result);
                    temp.clear();
                    counter.set(1);
//                    log.info("### temp array add in result array {} ###", arrAddCount.getAndIncrement());
                }
            }

            log.info("better case listAggregation || testSize : " + testSize + ", Aggregation ArrayList Size : " + result.size() + ", Success!");
            return ResponseEntity.ok("better case listAggregation || testSize : " + testSize + ", Aggregation ArrayList Size : " + result.size() + " Success!");
        }
        private void fillData(List<Integer> source, List<Integer> target) {
            source.forEach(target::add);
        }
    }
}
