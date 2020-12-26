package com.lxraa.compiler.service;

import com.lxraa.compiler.domain.Grammer;

import java.util.Map;
import java.util.Set;

public interface CompilerService {
    // 计算first集
    Map<String, Set<String>> getFirstSet(Grammer grammer);
    Map<String,Set<String>> getFollowSet(Grammer grammer);
}