
package com.sarva.bhasha;

import com.sarva.core.ConversationMemory;

import com.sarva.core.SarvaAgent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class BhashaAgent implements SarvaAgent {

    @Override
    public String getName() {
        return "SarvaBhashaAgent";
    }

    @Override
    public String handle(String query, ConversationMemory memory) {
        return "Bhasha AI response (placeholder) for: " + query;
    }

    @Override
    public Flux<String> handleStream(String query, ConversationMemory memory) {
        return Flux.just("Bhasha AI response (placeholder) for: " + query);
    }
}
