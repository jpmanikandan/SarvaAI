package com.sarva.api;

import com.sarva.core.AgentRouter;
import com.sarva.core.ConversationMemory;
import jakarta.servlet.http.HttpSession;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final OpenAiAudioSpeechModel speechModel;
    private final AgentRouter agentRouter;

    public VoiceController(OpenAiAudioTranscriptionModel transcriptionModel,
            OpenAiAudioSpeechModel speechModel,
            AgentRouter agentRouter) {
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
        this.agentRouter = agentRouter;
    }

    @PostMapping("/transcribe")
    public String transcribe(@RequestParam("file") MultipartFile file) throws IOException {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(file.getResource());
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        return response.getResult().getOutput();
    }

    @PostMapping("/speak")
    public ResponseEntity<Resource> speak(@RequestBody SpeakRequest request) {
        byte[] speechBytes = speechModel.call(request.text());

        ByteArrayResource resource = new ByteArrayResource(speechBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.mp3")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }

    @PostMapping("/chat")
    public ResponseEntity<Resource> voiceChat(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "outputLanguage", required = false) String outputLanguage,
            HttpSession session) throws IOException {
        // 1. STT: Transcribe audio to text
        AudioTranscriptionPrompt transcriptionPrompt = new AudioTranscriptionPrompt(file.getResource());
        String userQuery = transcriptionModel.call(transcriptionPrompt).getResult().getOutput();
        System.out.println("STT Result: " + userQuery);

        // Get or create conversation memory
        ConversationMemory memory = (ConversationMemory) session.getAttribute("conversationMemory");
        if (memory == null) {
            memory = new ConversationMemory();
            session.setAttribute("conversationMemory", memory);
        }

        // 2. ROUTE: Process text through Sarva Core
        String agentResponse = agentRouter.route(userQuery, null, outputLanguage, memory);
        System.out.println("Agent Response: " + agentResponse);

        // 3. TTS: Convert agent response to audio
        byte[] speechBytes = speechModel.call(agentResponse);

        ByteArrayResource resource = new ByteArrayResource(speechBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=chat_response.mp3")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }

    public record SpeakRequest(String text) {
    }
}
