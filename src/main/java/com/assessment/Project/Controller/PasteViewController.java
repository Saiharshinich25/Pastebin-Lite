package com.assessment.Project.Controller;

import com.assessment.Project.Model.Paste;
import com.assessment.Project.Service.PasteService;
import com.assessment.Project.Util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.server.ResponseStatusException;

@Controller
public class PasteViewController {

    private final PasteService pasteService;

    public PasteViewController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    @GetMapping("/p/{id}")
    public String viewPaste(
            @PathVariable String id,
            HttpServletRequest request,
            Model model) {

        Paste paste = pasteService.find(id);
        System.out.println("HTML VIEW ID: " + id);
        System.out.println("PASTE FOUND: " + paste);

        if (paste == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        long now = TimeUtil.now(request);

        if (paste.getExpiresAt() != null && now >= paste.getExpiresAt())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (paste.getMaxViews() != null &&
                paste.getViews() >= paste.getMaxViews())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        pasteService.incrementViews(paste);
        model.addAttribute("content", paste.getContent());
        return "paste";
    }
}