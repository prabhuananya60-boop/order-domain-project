package com.example.order.domain.events;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
}
