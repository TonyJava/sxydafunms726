package com.afunms.soa.rest.controllers;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/menus")
public class MenusController {
	
	@RequestMapping(value = "/top", method = RequestMethod.GET, produces = "application/json")
	public Map<String, Object> generateTopPageMenu(HttpServletRequest req,
			HttpServletResponse resp) {
		resp.setCharacterEncoding("UTF-8");

		Session session = SecurityUtils.getSubject().getSession();
		Map<String, Object> menu = new HashMap();
		menu.put("menuRoot", session.getAttribute("menuRoot"));
		menu.put("roleFunction", session.getAttribute("roleFunction"));
		return menu;
	}
}
