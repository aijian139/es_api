package com.yj.controller;

import com.yj.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class HelloController {

    @Autowired
    private ContentService contentService;

    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }

    @GetMapping("/parse/{keyword}")
    @ResponseBody
    public Boolean parse(@PathVariable String keyword) throws Exception {
        boolean f = contentService.parseContent(keyword);
        return f;
    }

    @GetMapping("/search/{keyword}/{pageNum}/{pageSize}")
    @ResponseBody
    public List<Map<String,Object>> search(@PathVariable String keyword, @PathVariable Integer pageNum, @PathVariable Integer pageSize) throws IOException {
        return contentService.searchHighLightContent(keyword, pageNum,pageSize);
    }

}
