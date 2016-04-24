package com.dur;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dur.dao.TestDao;


//    http://localhost:8080/ServerSide/http/rest/hello.json
	
@Controller
@RequestMapping(value = "/rest")
public class RestController {

   @RequestMapping("/hello")
   public TestDao greeting(@RequestParam(value="name", required=false, defaultValue="World") String name) {
   	TestDao dao = new TestDao();
   	dao.setName(name);
   	dao.setValue("Durka");
   	return dao;
   }

}
