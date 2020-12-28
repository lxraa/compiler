package com.lxraa.compiler.domain;

import java.util.Map;
import java.util.Set;

public class BaseGrammer {
    public static char[] NULL = new char[]{'ε'};
    public static char[] END = new char[]{'$'};
    public char[] START;

    private Map<char[], Set<char[]>> sentences;

    public BaseGrammer(char[] start){
        this.START = start;
    }

    public void addSentence(){

    }

}
