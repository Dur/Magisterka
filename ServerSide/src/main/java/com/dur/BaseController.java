package com.dur;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//   http://localhost:8080/ServerSide/http/jsp/welcome

@Controller
@RequestMapping("/jsp")
public class BaseController {
 
	@RequestMapping(value="/welcome", method = RequestMethod.GET)
	public String welcome(ModelMap model) {
 
		model.addAttribute("message", "welcome()");
 
		//Spring uses InternalResourceViewResolver and return back index.jsp
		return "index";
 
	}
 
	@RequestMapping(value="/welcome/{name}", method = RequestMethod.GET)
	public String welcomeName(@PathVariable String name, ModelMap model) {
 
		model.addAttribute("message", "Dziala " + name);
		return "index";
 
	}
	
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String defaultPage(ModelMap model) {
 
		model.addAttribute("message", "Strona testowa");
 
		//Spring uses InternalResourceViewResolver and return back index.jsp
		return "index";
	}
}
