package com.lxraa.compiler.controller;

import com.lxraa.compiler.domain.Grammer;
import com.lxraa.compiler.service.CompilerService;
import com.lxraa.compiler.service.impl.CompilerServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CompilerController {
    private final CompilerService compilerService;
    CompilerController(CompilerServiceImpl compilerService){
        this.compilerService = compilerService;
    }

    public ResponseEntity<Object> test(){

        return new ResponseEntity<Object>("1", HttpStatus.OK);
    }
    @GetMapping("/getFirst")
    public ResponseEntity<Object> getFirst(){
        Grammer grammer = new Grammer();
        // S→AB
        grammer.addSentence("S","AB");
        // S→bC
        grammer.addSentence("S","bC");
        // A→ε
        grammer.addSentence("A","ε");
        // A→b
        grammer.addSentence("A","b");
        // B→ε
        grammer.addSentence("B","ε");
        // B→aD
        grammer.addSentence("B","aD");
        // C→AD
        grammer.addSentence("C","AD");
        // C→b
        grammer.addSentence("C","b");
        // D→aS
        grammer.addSentence("D","aS");
        // D→c
        grammer.addSentence("D","c");

        Map<String, Set<String>> first = compilerService.getFirstSet(grammer);
        return new ResponseEntity<Object>(first,HttpStatus.OK);
    }

}
