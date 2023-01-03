package com.inspien.backupclientspring.controller;

import com.inspien.backupclientspring.exception.CustomException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "com.inspien.backupclientspring")
public class ErrorController {

    @ExceptionHandler(CustomException.class)
    public ModelAndView handle(CustomException e) {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("errorMsg", e.getErrorCode().getMessage());
        return modelAndView;
    }

}
