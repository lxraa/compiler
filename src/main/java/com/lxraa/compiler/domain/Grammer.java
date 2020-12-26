package com.lxraa.compiler.domain;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Grammer {
    private Map<String, Set<String>> sentences = new HashMap<>();
    // 空
    public static String NULL = "ε";



    /**
     * 给文法添加句子
     * @param left
     * @param right
     */
    public void addSentence(String left,String right){
        if(null == this.sentences.get(left)){
            this.sentences.put(left,new HashSet<>());
            this.sentences.get(left).add(right);
            return;
        }
        this.sentences.get(left).add(right);

    }

    /**
     * 获取所有句子
     * @return
     */
    public Map<String, Set<String>> getSentences(){
        return this.sentences;
    }


    /**
     *  是否为终结符，小写为终结符
     * @param token
     * @return
     */
    public static Boolean isTerminal(String token){
        return Character.isLowerCase(token.charAt(0)) || token.equals(NULL);
    }

    /**
     * 是否为非终结符，大写为非终结符
     * @param token
     * @return
     */
    public static Boolean isNonTerminal(String token){
        return Character.isUpperCase(token.charAt(0));
    }

    public static Boolean isTerminal(char token){
        char[] terminal = new char[]{'+','*','(',')','^'};
        Boolean f = false;
        for(char t:terminal){
            if(t == token){
                f = true;
            }
        }
        return Character.isLowerCase(token) || NULL.charAt(0) == token || f;
    }

    public static Boolean isNonTerminal(char token){
        return Character.isUpperCase(token);
    }

}
