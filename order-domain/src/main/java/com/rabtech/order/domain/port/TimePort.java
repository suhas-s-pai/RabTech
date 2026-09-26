package com.rabtech.order.domain.port;

import java.time.Instant;

public interface TimePort {
    Instant now();
}
