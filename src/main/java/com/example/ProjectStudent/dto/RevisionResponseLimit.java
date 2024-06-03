package com.example.ProjectStudent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisionResponseLimit<T> {
    private Instant revision;
    private T content;

    public static <T> RevisionResponseLimit<T> of(Instant revision, T content) {
        return new RevisionResponseLimit<>(revision, content);
    }
}
