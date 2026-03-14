package backend.moviefinder.streaming.controller;

import backend.moviefinder.streaming.dto.WatchmodeSourceDto;
import backend.moviefinder.streaming.service.StreamingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streaming")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @GetMapping("/{tmdbId}")
    public ResponseEntity<List<WatchmodeSourceDto>> getStreamingSources(@PathVariable Long tmdbId) {
        List<WatchmodeSourceDto> sources = streamingService.getStreamingSources(tmdbId);
        return ResponseEntity.ok(sources);
    }
}