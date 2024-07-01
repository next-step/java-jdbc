package com.interface21.webmvc.servlet.mvc.tobe.support;

import com.interface21.web.bind.annotation.PathVariable;
import com.interface21.web.bind.annotation.RequestMapping;
import com.interface21.web.bind.annotation.RequestParam;

public class MockArgumentResolverController {

    @RequestMapping("/requestParam")
    String mockRequestParamMethod(@RequestParam(value = "user") String id) {
        return id;
    }

    @RequestMapping("/requestParam")
    String mockRequestParamMethod(@RequestParam int id) {
        return String.valueOf(id);
    }

    @RequestMapping("/pathVariable/{name}")
    String mockPathVariableMethod(@PathVariable(value = "name") String userName) {
        return userName;
    }

    @RequestMapping("/pathVariable/user/{id}")
    String mockPathVariableMethod(@PathVariable int id) {
        return String.valueOf(id);
    }

    @RequestMapping("/user")
    String mockUser(MockUser user) {
        return user.toString();
    }

}
