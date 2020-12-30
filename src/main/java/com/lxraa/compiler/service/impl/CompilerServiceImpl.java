package com.lxraa.compiler.service.impl;

import com.lxraa.compiler.domain.Grammer;
import com.lxraa.compiler.service.CompilerService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompilerServiceImpl implements CompilerService {
    /**
     * 将set2中的集合合并到set1
     * flag为true时，去掉NULL；flag为false时不去掉NULL
     * @param set1
     * @param set2
     * @param flag
     * @return set1是否有新增元素
     */
    private Boolean union(Set<String> set1,Set<String> set2,Boolean flag){
        Boolean isChange = false;
        for(String token : set2){
            if(flag && token.equals(Grammer.NULL)){
                continue;
            }
            isChange = isChange || set1.add(token);
        }
        return isChange;
    }


    private void initFirst(Map<String,Set<String>> first,Set<String> nonTerminal,Set<String> terminal){
        //终结符的first集是它自己
        for(String token : terminal){
            if(null == first.get(token)){
                first.put(token,new HashSet<>());
            }
            first.get(token).add(token);
        }
        //非终结符只初始化数据结构，frist集求解需多轮迭代
        for(String token : nonTerminal) {
            if (null == first.get(token)) {
                first.put(token, new HashSet<>());
            }
        }
    }

    /**
     * 必须保证文法的左部第一个字符为非终结符，才能进行该函数的判断
     * 有3种情况
     * 1、右部的第一个字符为非空终结符，将该字符加入左部非终结符的first集
     * 2、右部的第一个字符为非终结符Y，则把first(Y)中所有的非空终结符加入first集中
     * @param first
     * @param left
     * @param right
     * @return
     */
    private Boolean updateFirstBySentence(Map<String,Set<String>> first,String left,String right) throws Exception {
        if(right.length() == 0){
            right = Grammer.NULL;
        }
        Boolean isChange = false;
        String objToken = left.substring(0,1);
        // 若右部第一个元素为终结符
        if(Grammer.isTerminal(right.charAt(0))){
            isChange = isChange || first.get(objToken).add(right.substring(0,1));
            return isChange;
        }
        // 若右部第一个元素为非终结符
        if(Grammer.isNonTerminal(right.charAt(0))){
            // 获取右部第一个非终结符的first集
            Set<String> ts = first.get(right.substring(0,1));
            isChange = isChange || union(first.get(objToken),ts,true);
            if(ts.contains(Grammer.NULL)){
                return isChange || updateFirstBySentence(first,left,right.substring(1));
            }
            return isChange;
        }
        throw new Exception("unknown token");
    }

    private Set<String> getTerminal(Grammer grammer){
        Set<String> terminal = new HashSet<>();
        Map<String,Set<String>> sentences = grammer.getSentences();
        for(String k : sentences.keySet()){
            for(int i = 0;i < k.length();i++){
                if(Grammer.isTerminal(k.charAt(i))){
                    terminal.add(k.substring(i,i+1));
                }
            }
            Set<String> vs = sentences.get(k);
            for(String v : vs){
                for(int i = 0;i < v.length();i++){
                    if(Grammer.isTerminal(v.charAt(i))){
                        terminal.add(v.substring(i,i+1));
                    }
                }
            }
        }
        return terminal;
    }

    private Set<String> getNonTerminal(Grammer grammer){
        Set<String> nonTerminal = new HashSet<>();
        Map<String,Set<String>> sentences = grammer.getSentences();
        for(String k : sentences.keySet()){
            for(int i = 0;i < k.length();i++){
                if(Grammer.isNonTerminal(k.charAt(i))){
                    nonTerminal.add(k.substring(i,i+1));
                }
            }
            Set<String> vs = sentences.get(k);
            for(String v : vs){
                for(int i = 0;i < v.length();i++){
                    if(Grammer.isNonTerminal(v.charAt(i))){
                        nonTerminal.add(v.substring(i,i+1));
                    }
                }
            }
        }
        return nonTerminal;
    }

    @Override
    public Map<String,Set<String>> getFirstSet(Grammer grammer) {
        Set<String> terminal = getTerminal(grammer);
        Set<String> nonTerminal = getNonTerminal(grammer);
        Map<String, Set<String>> sentences = grammer.getSentences();

        Map<String,Set<String>> first = new HashMap<>();
        // 初始化first集
        initFirst(first,nonTerminal,terminal);
        while(true){
            //
            Boolean isChange = false;
            // 遍历所有句子，更新first集
            for(String left : sentences.keySet()){
                for(String right : sentences.get(left)){
                    //left->right
                    try{
                        isChange = isChange || updateFirstBySentence(first,left,right);
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            if(!isChange){
                break;
            }
        }

        return first;
    }

    /**
     * 初始化follow集
     * @param follow
     * @param terminal
     * @param nonTerminal
     */
    private void initFollow(Map<String,Set<String>> follow,Set<String> terminal,Set<String> nonTerminal,Grammer grammer){
        for(String token : nonTerminal){
            follow.put(token,new HashSet<>());
            if(grammer.START == token){
                follow.get(token).add(Grammer.END);
            }
        }
    }

    private Set<String> getStrFirst(String s,Map<String,Set<String>> first){
        if(s.length() == 0){
            Set<String> tmpR = new HashSet<>();
            tmpR.add(Grammer.NULL);
            return tmpR;
        }
        String token = s.substring(0,1);
        Set<String> r = new HashSet<>();
        union(r, first.get(token),true);
        if(first.get(token).contains(Grammer.NULL)){
            union(r,getStrFirst(s.substring(1),first),true);
        }
        return r;
    }

    /**
     * 二型文法的左部必为一个非终结符
     * @param follow
     * @param left
     * @param right
     * @param first
     * @return
     */
    private Boolean updateFollowBySentence(Map<String,Set<String>> follow, String left, String right, Map<String,Set<String>> first){

        //右部最后一个字符
        String lastToken = right.substring(right.length() - 1);
        Boolean isChange = false;
        for(int i = 0;i < right.length() - 1;i++){
            String objToken = right.substring(i,i+1);
            // 终结符没有follow集
            if(Grammer.isTerminal(objToken)){
                continue;
            }
            if(Grammer.isNonTerminal(objToken)){
                String rightStr = right.substring(i+1);
                Set<String> tmpFirst = getStrFirst(rightStr,first);
                Boolean canBeNull = true;
                for(int j = 0;j <rightStr.length();j++){
                    canBeNull = canBeNull && first.get(rightStr.substring(j,j+1)).contains(Grammer.NULL);
                }
                if(canBeNull){
                    tmpFirst.add(Grammer.NULL);
                }

                isChange = isChange || union(follow.get(objToken),tmpFirst,true);
                if(tmpFirst.contains(Grammer.NULL)){
                    isChange = isChange || union(follow.get(objToken),follow.get(left),true);
                }
                // 此处nextToken不可能为Grammer.NULL，应在化简文法时就考虑这种情况
//                if(Grammer.isTerminal(nextToken)){
//                    isChange = isChange || follow.get(objToken).add(nextToken);
//                    continue;
//                }
//                // 若token的下一个字符为非终结符，则
//                if(Grammer.isNonTerminal(nextToken)){
//                    isChange = isChange || union(follow.get(objToken),first.get(nextToken),true);
//                    if(follow.get(objToken).contains(Grammer.NULL)){
//                        isChange = isChange || updateFollowBySentence(follow,left,right.substring(0,i) + right.substring(i+1),first);
//                    }
//                    continue;
//                }
//                isChange = isChange || union(follow.get(objToken),first.get(nextToken),true);
            }
        }
        //处理最后一个字符
        if(Grammer.isTerminal(lastToken)){
            return isChange;
        }
        isChange = isChange || union(follow.get(lastToken),follow.get(left),true);
        return isChange;

    }

    /**
     * 计算文法的follow集
     * @param grammer
     * @return
     */
    @Override
    public Map<String, Set<String>> getFollowSet(Grammer grammer) {
        Map<String,Set<String>> follow = new HashMap<>();
        Set<String> terminal = getTerminal(grammer);
        Set<String> nonTerminal = getNonTerminal(grammer);
        Map<String,Set<String>> first = this.getFirstSet(grammer);

        Map<String,Set<String>> sentences = grammer.getSentences();

        initFollow(follow,terminal,nonTerminal,grammer);
        while(true){
            Boolean isChange = false;
            for(String k : sentences.keySet()){
                for(String v: sentences.get(k)){
                    isChange = isChange || updateFollowBySentence(follow,k,v,first);
                }
            }
            if(!isChange){
                break;
            }
        }

        return follow;
    }

//    private void initEmpty(Map<String,Boolean> empty,Set<String> terminal,Set<String> nonTerminal,Grammer grammer){
//        for(String token : terminal){
//            if(Grammer.NULL.equals(token)){
//                empty.put(token,true);
//                continue;
//            }
//            empty.put(token,false);
//        }
//
//        for(String token : nonTerminal){
//            empty.put(token,false);
//        }
//    }

//    private Boolean updateEmptyBySentence(Map<String,Boolean> empty,String left,String right){
//        Boolean isChange = false;
//        if(Grammer.isNonTerminal(left)){
//            Boolean f = false;
//            for(int i = 0;i < right.length();i++){
//                String obj = right.substring(i,i+1);
//                f = f && empty.get(obj);
//            }
//
//            isChange = isChange || (empty.get(left) ^ f);
//            empty.replace(left,f);
//        }
//
//        return isChange;
//    }
//
//    @Override
//    public Map<String, Boolean> getEmpty(Grammer grammer) {
//        Set<String> nonTerminal = getNonTerminal(grammer);
//        Set<String> terminal = getTerminal(grammer);
//
//        Map<String,Boolean> empty = new HashMap<>();
//        initEmpty(empty,terminal,nonTerminal,grammer);
//
//        while(true){
//            Boolean isChange = false;
//            Map<String,Set<String>> sentences = grammer.getSentences();
//            for(String left : sentences.keySet()){
//                for(String right:sentences.get(left)){
//                    isChange = isChange || updateEmptyBySentence(empty,left,right);
//
//                }
//            }
//            if(!isChange){
//                break;
//            }
//        }
//
//        return empty;
//    }

    @Override
    public Boolean antlr(){
        return true;
    }

}
