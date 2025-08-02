package edu.wust.qrz.controller;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.service.DictionaryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    @Resource
    DictionaryService dictionaryService;

    @GetMapping("/all")
    public Result getAllDictionary(){
        return dictionaryService.getAllDictionary();
    }

}
