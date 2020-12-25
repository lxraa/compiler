package com.lxraa.compiler.domain;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Grammer {
    private Map<String,String> sentences = new HashMap<>();

    /**
     * 给文法添加句子
     * @param left
     * @param right
     */
    public void addSentence(String left,String right){
        this.sentences.put(left,right);
    }

    /**
     * 获取所有句子
     * @return
     */
    public Map<String,String> getSentences(){
        return this.sentences;
    }

    /**
     * 获取句子右部
     * @param left
     * @return
     */
    public String getRight(String left){
        return this.sentences.get(left);
    }
}
