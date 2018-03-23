package cn.qx.springcloudribbon.ribbon.service;

import cn.qx.springcloudribbon.SpringCloudRibbonApplication;
import cn.qx.springcloudribbon.ribbon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RibbonService {
    @Autowired
    RestTemplate restTemplate;
    /**
     * @see findUserById 单个参数
     * @param id
     * @return
     */
    public User findUserById(Long id){
        return restTemplate.getForObject("http://demo-clent/"+ id ,User.class);
    }
    /**
     * @see findUserByIdAndName 多个参数,getForObject可以换位其它的方法，比如postForObject
     * @param id
     * @param name
     * @return
     */
    public User findUserByIdAndName(Long id,String name){
        return restTemplate.getForObject("http://demo-clent/findUserByIdAndName/"+ id + "/" + name ,User.class);
    }
}
