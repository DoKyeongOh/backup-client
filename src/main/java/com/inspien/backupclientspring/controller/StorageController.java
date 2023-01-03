package com.inspien.backupclientspring.controller;

import com.inspien.backupclientspring.domain.ClassifiedFilenames;
import com.inspien.backupclientspring.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/storage")
@AllArgsConstructor
public class StorageController {
    StorageService storageService;

    @PostMapping("")
    public ModelAndView createStorage(String rootDirPath) {
        storageService.createStorage(rootDirPath);
        ModelAndView modelAndView = new ModelAndView("storage-create-complete");
        modelAndView.addObject("errorMsg", "");
        return modelAndView;
    }

    @GetMapping("")
    public ModelAndView viewStorage(@RequestParam(name = "rootDirPath", required = false) String rootDirPath) {
        ClassifiedFilenames filenames = storageService.getClassifiedFilenames(rootDirPath);
        ModelAndView modelAndView = new ModelAndView("storage-view");
        modelAndView.addObject("filenames", filenames);
        modelAndView.addObject("rootDirPath", rootDirPath);
        modelAndView.addObject("errorMsg", "");
        return modelAndView;
    }

    @PutMapping("")
    public String updateStorage(String rootDirPath) {
        storageService.updateStorage(rootDirPath);
        return "storage-update-complete";
    }

    @GetMapping("/files")
    public String rollbackStorage(String rootDirPath) {
        storageService.rollbackStorage(rootDirPath);
        return "storage-rollback-complete";
    }

}
