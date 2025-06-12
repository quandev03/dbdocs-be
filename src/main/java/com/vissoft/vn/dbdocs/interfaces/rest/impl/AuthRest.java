package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.vissoft.vn.dbdocs.interfaces.rest.AuthOperator;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRest implements AuthOperator {
    @Override
    public Object test() {
        return "AuthRest test";
    }
}
