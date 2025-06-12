package com.vissoft.vn.dbdocs.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("api/auth")
public interface AuthOperator {
    @GetMapping("test")
    Object test();
}
