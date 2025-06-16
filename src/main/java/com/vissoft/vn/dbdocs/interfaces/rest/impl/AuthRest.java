package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import org.springframework.web.bind.annotation.RestController;

import com.vissoft.vn.dbdocs.interfaces.rest.AuthOperator;

@RestController
public class AuthRest implements AuthOperator {
    @Override
    public Object test() {
        return "AuthRest test";
    }
}
