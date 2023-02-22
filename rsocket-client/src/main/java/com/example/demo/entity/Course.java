package com.example.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Course {

    private UUID courseId = UUID.randomUUID();
    private long created = Instant.now().getEpochSecond();
    private String courseName;

    public Course(String courseName) {
        this.courseName = courseName;
    }
}