package cn.qx.springcloudribbon.ribbon.controller;

import cn.qx.springcloudribbon.ribbon.entity.User;
import cn.qx.springcloudribbon.ribbon.service.RibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RibbonController {
    @Autowired
    RibbonService ribbonService;
    @RequestMapping("/ribbon/{id}")
    public User findUser(@PathVariable Long id){
        System.out.println("----------------------------------进入ribbon------findUser---------------------------------------------");
        return ribbonService.findUserById(id);
    }

    @GetMapping("/ribbon/{id}/{name}")
    public User findUserByIdAndName(@PathVariable Long id,@PathVariable String name){
        System.out.println("----------------------------------进入ribbon---findUserByIdAndName---------------------------------------");
        return ribbonService.findUserByIdAndName(id,name);
    }
}
