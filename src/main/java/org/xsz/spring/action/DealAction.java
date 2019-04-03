package org.xsz.spring.action;

import org.xsz.spring.Annotation.XSZAutowired;
import org.xsz.spring.Annotation.XSZController;
import org.xsz.spring.service.DealService;

@XSZController
public class DealAction {
    @XSZAutowired
    private DealService dealService;
}
