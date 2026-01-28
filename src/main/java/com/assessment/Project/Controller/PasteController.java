package com.assessment.Project.Controller;

import com.assessment.Project.DTO.CreatePasteRequest;
import com.assessment.Project.Model.Paste;
import com.assessment.Project.Service.PasteService;
import com.assessment.Project.Util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PasteController {

    private final PasteService pasteService;

    public PasteController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    // ---------------- CREATE PASTE ----------------
    @PostMapping("/pastes")
    public ResponseEntity<?> createPaste(
            @Valid @RequestBody CreatePasteRequest req,
            HttpServletRequest request) {

        try {
            if (req.getContent() == null || req.getContent().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "content must be a non-empty string"));
            }

            if (req.getTtl_seconds() != null && req.getTtl_seconds() < 1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid ttl_seconds"));
            }

            if (req.getMax_views() != null && req.getMax_views() < 1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid max_views"));
            }

            String id = UUID.randomUUID().toString().substring(0, 8);
            long now = System.currentTimeMillis(); // TEMP: avoid TimeUtil issue

            Long expiresAt = req.getTtl_seconds() == null
                    ? null
                    : now + req.getTtl_seconds() * 1000L;

            Paste paste = new Paste(
                    id,
                    req.getContent(),
                    expiresAt,
                    req.getMax_views(),
                    0
            );

            pasteService.save(paste);

            String baseUrl = request.getRequestURL()
                    .toString()
                    .replace(request.getRequestURI(), "");

            return ResponseEntity.ok(Map.of(
                    "id", id,
                    "url", baseUrl + "/p/" + id
            ));

        } catch (Exception e) {
            e.printStackTrace(); // 👈 THIS IS KEY
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ---------------- FETCH PASTE (API) ----------------
    @GetMapping("/pastes/{id}")
    public ResponseEntity<?> getPaste(
            @PathVariable String id,
            HttpServletRequest request) {

        Paste paste = pasteService.find(id);
        if (paste == null) return notFound();

        long now = TimeUtil.now(request);

        if (paste.getExpiresAt() != null && now >= paste.getExpiresAt())
            return notFound();

        if (paste.getMaxViews() != null &&
                paste.getViews() >= paste.getMaxViews())
            return notFound();

        pasteService.incrementViews(paste);

        Integer remainingViews = paste.getMaxViews() == null
                ? null
                : paste.getMaxViews() - paste.getViews();

        String expiresAtIso = paste.getExpiresAt() == null
                ? null
                : Instant.ofEpochMilli(paste.getExpiresAt()).toString();

        Map<String, Object> response = new HashMap<>();
        response.put("content", paste.getContent());
        response.put("remaining_views", remainingViews);
        response.put("expires_at", expiresAtIso);

        return ResponseEntity.ok(response);
    }

    // ---------------- 404 HANDLER ----------------
    private ResponseEntity<?> notFound() {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Not found");
        return ResponseEntity.status(404).body(error);
    }
}
