package com.lxraa.compiler.service;

import com.lxraa.compiler.domain.Grammer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CompilerService {
    // 计算first集
    Map<String, Set<String>> getFirstSet(Grammer grammer);
    // 计算follow集
    Map<String,Set<String>> getFollowSet(Grammer grammer);

    Map<List<String>,Set<String>> getSelectSet(Grammer grammer);
//    Map<String,Boolean> getEmpty(Grammer grammer);

    Boolean antlr();
}