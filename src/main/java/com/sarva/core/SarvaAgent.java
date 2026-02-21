
package com.sarva.core;

import reactor.core.publisher.Flux;

public interface SarvaAgent {
    String getName();

    String handle(String query, ConversationMemory memory);

    Flux<String> handleStream(String query, ConversationMemory memory);
}

