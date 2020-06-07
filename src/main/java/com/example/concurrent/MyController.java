package com.example.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RestController("/")
@Slf4j
public class MyController {
    @Autowired
    ThreadPoolTaskExecutor mvcTaskExecutor;

    @GetMapping
    public String hello(){
        System.out.println("hello");
        return "hello";
    }

    @GetMapping("/api/hello1")
    public String hello1(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("hello1");
        return "hello1";
    }

    @GetMapping("/api/hello2")
    public Callable<String> hello2(){
        return () -> {
            Thread.sleep(1000);
            log.info("hello2");
            return "hello2";
        };
    }

    @GetMapping("/api/hello3")
    public CompletableFuture<String> hello3(){
        return CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("hello3");
            return "hello3";
        }, this.mvcTaskExecutor);
    }
}
