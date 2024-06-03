package com.example.ProjectStudent.Utils;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.temporal.ChronoUnit.MICROS;

public class DateTimeUtils {

    private static final AtomicReference<Instant> uniqueTimestampMicros = new AtomicReference<>(nowMicros());

    public static Instant uniqueTimestampMicros() {
        Instant previous;
        Instant current;
        do {
            previous = uniqueTimestampMicros.get();
            current = nowMicros();
            if (current.compareTo(previous) <= 0) {
                current = previous.plus(1, MICROS);
            }
        } while (!uniqueTimestampMicros.compareAndSet(previous, current));
        return current;
    }

    public static Instant nowMicros() {
        return Instant.now().truncatedTo(MICROS);
    }
}
