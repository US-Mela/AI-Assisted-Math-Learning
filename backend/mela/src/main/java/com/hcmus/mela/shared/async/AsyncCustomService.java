package com.hcmus.mela.shared.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
public class AsyncCustomService {

    @Autowired
    @Qualifier("customExecutor")
    private Executor customExecutor;

    public <T> CompletableFuture<T> runAsync(Supplier<T> supplier, T fallback) {
        return CompletableFuture.supplyAsync(supplier, customExecutor)
                .completeOnTimeout(fallback, 3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Async error: {}", ex.getMessage());
                    return fallback;
                });
    }

}