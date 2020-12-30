package com.lxraa.compiler.service;

import com.lxraa.compiler.domain.Grammer;

import java.util.Map;
import java.util.Set;

public interface CompilerService {
    // 计算first集
    Map<String, Set<String>> getFirstSet(Grammer grammer);
    // 计算follow集
    Map<String,Set<String>> getFollowSet(Grammer grammer);

//    Map<String,Boolean> getEmpty(Grammer grammer);

    Boolean antlr();
}