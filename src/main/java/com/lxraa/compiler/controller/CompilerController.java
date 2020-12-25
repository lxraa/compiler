package com.lxraa.compiler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CompilerController {

    public ResponseEntity<Object> test(){

        return new ResponseEntity<Object>("1", HttpStatus.OK);
    }
}
