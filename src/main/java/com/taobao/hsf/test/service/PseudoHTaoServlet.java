package com.taobao.hsf.test.service;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class PseudoHTaoServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
		WebApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		PseudoHTaoService pseudoHTaoService = (PseudoHTaoService)context.getBean("pseudoHTaoService");
		
		PrintWriter out = response.getWriter();
		out.println(pseudoHTaoService.queryInfo(request.getParameter("name")));
		return;
	}
	

}
