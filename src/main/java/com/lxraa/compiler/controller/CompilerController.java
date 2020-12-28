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

//        //E->TE'
//        grammer.addSentence("E","TG");
//        //E'->+E|ε
//        grammer.addSentence("G","+E");
//        grammer.addSentence("G","ε");
//        //T->FT'
//        grammer.addSentence("T","FH");
//        //T'->T|ε
//        grammer.addSentence("H","T");
//        grammer.addSentence("H","ε");
//        //F->PF'
//        grammer.addSentence("F","PH");
//        //F'->*F'|ε
//        grammer.addSentence("H","*H");
//        grammer.addSentence("H","ε");
//        //P->(E)|a|b|^
//        grammer.addSentence("P","(E)");
//        grammer.addSentence("P","a");
//        grammer.addSentence("P","b");
//        grammer.addSentence("P","^");

        Map<String, Set<String>> first = compilerService.getFirstSet(grammer);
        return new ResponseEntity<Object>(first,HttpStatus.OK);
    }

    @GetMapping("/getFollow")
    public ResponseEntity<Object> getFollow(){
//        Grammer grammer = new Grammer();
        // S→AB
//        grammer.addSentence("S","AB");
//        // S→bC
//        grammer.addSentence("S","bC");
//        // A→ε
//        grammer.addSentence("A","ε");
//        // A→b
//        grammer.addSentence("A","b");
//        // B→ε
//        grammer.addSentence("B","ε");
//        // B→aD
//        grammer.addSentence("B","aD");
//        // C→AD
//        grammer.addSentence("C","AD");
//        // C→b
//        grammer.addSentence("C","b");
//        // D→aS
//        grammer.addSentence("D","aS");
//        // D→c
//        grammer.addSentence("D","c");


        Grammer grammer2 = new Grammer();
        // E→TE’    E'= D
        grammer2.addSentence("E","TD");
        // E’→+TE’ | ε   E' = D
        grammer2.addSentence("D","+TD");
        grammer2.addSentence("D","ε");
        // T→FT’  T'= Y
        grammer2.addSentence("T","FY");
        // T’→*FT’ | ε  T' = Y
        grammer2.addSentence("Y","*FY");
        grammer2.addSentence("Y","ε");
        // F→（E）| id id = i
        grammer2.addSentence("F","(E)");
        grammer2.addSentence("F","i");


        Map<String,Set<String>> follow = compilerService.getFollowSet(grammer2);
        return new ResponseEntity<Object>(follow,HttpStatus.OK);
    }
}
