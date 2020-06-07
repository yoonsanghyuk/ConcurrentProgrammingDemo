package com.example.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;
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

    @Async("threadPoolTaskExecutor")
    @GetMapping("api/hello4")
    public ListenableFuture<String> hello4(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("hello4");
        return new AsyncResult<>("hello4");
    }

    @GetMapping("/api/hello5")
    public DeferredResult<String> persons(){
        DeferredResult<String> result = new DeferredResult<>();

        mvcTaskExecutor.submit(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("hello5");
            result.setResult("hello5");
        });
        return result;
    }

    @GetMapping("/api/hello6")
    public ResponseBodyEmitter hello6(){
        final ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        mvcTaskExecutor.execute(()->{
            List<String> list = Arrays.asList("hello", "world", "!", "!");
            try {
                for(String str : list){
                    Thread.sleep(1000);
                    emitter.send(str);
                    log.info("emitter : " + str);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/api/hello7")
    public SseEmitter hello7(){
        final SseEmitter emitter = new SseEmitter();
        mvcTaskExecutor.execute(()->{
            List<String> list = Arrays.asList("hello", "world", "!", "!");
            try {
                for(String str : list){
                    Thread.sleep(1000);
                    emitter.send(str);
                    log.info("emitter : " + str);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}
