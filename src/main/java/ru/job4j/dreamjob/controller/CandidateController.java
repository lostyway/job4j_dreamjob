package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;

@ThreadSafe
@Controller
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateService;
    private final CityService cityService;

    public CandidateController(CandidateService candidateService, CityService cityService) {
        this.candidateService = candidateService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file, Model model) {
        try {
            Candidate candidateToGet = candidateService.save(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (candidateToGet == null) {
                throw new RuntimeException("Failed to save candidate");
            }
            return "redirect:/candidates";
        } catch (Exception exception) {
            model.addAttribute("message", "Произошла ошибка при создании вакансии");
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var cand = candidateService.findById(id);
        if (cand.isEmpty()) {
            model.addAttribute("message", "Кандидат с таким id не был найден");
            return "errors/404";
        }
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("candidate", cand.get());
        return "candidates/one";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, @RequestParam MultipartFile file, Model model) {
        try {
            boolean isUpdated = candidateService.update(candidate, new FileDto(file.getOriginalFilename(), file.getBytes()));
            if (!isUpdated) {
                model.addAttribute("message", "Кандидат с таким id не был найден");
                return "errors/404";
            }
            return "redirect:/candidates";
        } catch (IOException e) {
            model.addAttribute("message", "Произошла проблема при чтении файла");
            return "errors/404";
        } catch (Exception exception) {
            model.addAttribute("message", "Произошла ошибка при добавлении резюме");
            return "errors/404";
        }
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
