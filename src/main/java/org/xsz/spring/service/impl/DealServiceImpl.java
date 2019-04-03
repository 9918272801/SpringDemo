package org.xsz.spring.service.impl;

import org.xsz.spring.Annotation.XSZService;
import org.xsz.spring.service.DealService;

@XSZService
public class DealServiceImpl implements DealService {
    @Override
    public String deal(String param) {
        return "hello " + param;
    }
}
