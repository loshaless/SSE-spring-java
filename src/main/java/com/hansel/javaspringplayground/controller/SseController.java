package com.hansel.javaspringplayground.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/sse")
    public SseEmitter streamSseMvc() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/trigger")
    public void triggerEvent() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("message").data("Triggered at " + System.currentTimeMillis()));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}