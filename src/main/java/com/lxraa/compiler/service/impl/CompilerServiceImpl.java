package com.lxraa.compiler.service.impl;

import com.lxraa.compiler.domain.Grammer;
import com.lxraa.compiler.service.CompilerService;
import org.springframework.beans.factory.config.MapFactoryBean;
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
    private void initFollow(Map<String,Set<String>> follow,Set<String> terminal,Set<String> nonTerminal){
        for(String token : nonTerminal){
            follow.put(token,new HashSet<>());
            if(Grammer.START == token){
                follow.get(token).add(Grammer.END);
            }
        }
    }

    private Boolean updateFollowBySentence(Map<String,Set<String>> follow, String left, String right, Map<String,Set<String>> first){
        //左部最后一个字符
        String leftToken = left.substring(left.length() - 1);
        //右部最后一个字符
        String lastToken = right.substring(right.length() - 1);
        Boolean isChange = false;
        for(int i = 0;i < right.length() - 1;i++){
            String objToken = right.substring(i,i+1);
            String nextToken = right.substring(i+1,i+2);
            // 终结符没有follow集
            if(Grammer.isTerminal(objToken)){
                continue;
            }
            if(Grammer.isNonTerminal(objToken)){
                // 此处nextToken不可能为Grammer.NULL，应在化简文法时就考虑这种情况
                if(Grammer.isTerminal(nextToken)){
                    follow.get(objToken).add(nextToken);
                    isChange = isChange || true;
                    continue;
                }
                // 若token的下一个字符为非终结符，则
                if(Grammer.isNonTerminal(nextToken)){
                    isChange = isChange || union(follow.get(objToken),first.get(nextToken),true);
                    if(follow.get(objToken).contains(Grammer.NULL)){
                        isChange = isChange || updateFollowBySentence(follow,left,right.substring(0,i) + right.substring(i+1),first);
                    }
                    continue;
                }
            }
        }
        //处理最后一个字符
        isChange = isChange || union(follow.get(lastToken),follow.get(leftToken),true);
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
        Boolean isChange = false;
        while(true){
            for(String k : sentences.keySet()){
                for(String v: sentences.get(k)){
                    isChange = isChange || updateFollowBySentence(follow,k,v,first);
                }
            }
            if(!isChange){
                break;
            }
        }

        return null;
    }
}
