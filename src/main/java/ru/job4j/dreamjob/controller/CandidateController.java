package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;

@Controller
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate) {
        candidateService.save(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var cand = candidateService.findById(id);
        if (cand.isEmpty()) {
            model.addAttribute("message", "Кандидат с таким id не был найден");
            return "errors/404";
        }
        model.addAttribute("candidate", cand.get());
        return "candidates/one";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute Candidate candidate) {
        boolean isUpdated = candidateService.update(candidate);
        if (!isUpdated) {
            model.addAttribute("message", "Кандидат с таким id не был найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    @GetMapping("delete/{id}")
    public String delete(Model model, @PathVariable int id) {
       boolean isDeleted = candidateService.deleteById(id);
       if (!isDeleted) {
           model.addAttribute("message", "Кандидат с таким id не был найден");
           return "errors/404";
       }
        return "redirect:/candidates";
    }
}
