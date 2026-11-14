package com.bytedesk.remote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RemoteFrontendController {
    @GetMapping({"/remote", "/remote/", "/remote/chat", "/remote/assist", "/remote/auth/**"})
    public String frontend() {
        return "forward:/remote/index.html";
    }
}
