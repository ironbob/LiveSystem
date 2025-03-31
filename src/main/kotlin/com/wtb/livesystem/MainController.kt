package com.wtb.livesystem

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {

    @GetMapping("/")
    fun index(): String {
        return "forward:/index.html"
    }
}
