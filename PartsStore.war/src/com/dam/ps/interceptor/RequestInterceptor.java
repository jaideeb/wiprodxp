/**
 * This software is the confidential and proprietary information of

 * Wipro. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license 
 * agreement you entered into with Wipro.
 *
 */

package com.dam.ps.interceptor;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * This class intercepts each and every request to the application. Checks for a
 * valid session and redirects as per the case
 */
public class RequestInterceptor extends HandlerInterceptorAdapter {
	
	/*
	 * private static logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(RequestInterceptor.class);
	
	/**
	 * This method checks for a valid session in the intercepted request
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @param handler Object
	 * @return boolean flag to indicate the execution result
	 * 
	 */
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler)
			throws ServletException, IOException {
		_LOGGER.info("RequestInterceptor-preHandle()-STARTS");
		response.setHeader("X-FRAME-OPTIONS", "DENY");
		/*boolean status = false;
		String receivedURL = request.getServletPath() + "?"+ request.getQueryString();
		HttpSession session = request.getSession();
		boolean hasValidSession = false;
		System.out.println("Here");
		if (session.getAttribute("user_id") != null) {
			if(session.getAttribute("role").equals("brand_manager") && receivedURL.toLowerCase().indexOf("brandmanager") >= 0){
				hasValidSession = true;
			} else if(!session.getAttribute("role").equals("brand_manager") && receivedURL.toLowerCase().indexOf("brandmanager")== -1 ){
				hasValidSession = true;	
			} else {
				hasValidSession = false;
				session.invalidate();
				Cookie[] cookies = request.getCookies();
			    // Delete all the cookies
			    if (cookies != null) {
			        for (int i = 0; i < cookies.length; i++) {
			        	if(cookies[i].getName().equals("username")){
			        		Cookie cookie = cookies[i];
				            cookies[i].setValue(null);
				            cookies[i].setMaxAge(0);
				            response.addCookie(cookie);
			        	}
			        }
			    }
			}
			
		} else {
			hasValidSession = false;
		}
		_LOGGER.info(receivedURL);
		if (hasValidSession) {
			status = true;
		} else {
			// THESE ARE ALL UNPROTECTED LINKS
			// MENTIONING THEM HERE IS A MUST
			if (
				receivedURL.indexOf("partsstoretest.ps") >= 0) {
				status = true;
			} else {
				String pageName = null;
				if(receivedURL.contains("brandmanager")){
					pageName = "/BrandManager";
				} else {
					pageName = "/";
				}
				reLogin(request, response,"Your session is invalid, please login again",pageName);
				status = false;
			}
		}*/
		_LOGGER.info("RequestInterceptor-preHandle()-ENDS");
		return true;
	}

	/**
	 * This method spits out the login page
	 * @param requestHttpServletRequest
	 * @param response HttpServletResponse
	 */
	private void reLogin(HttpServletRequest req, HttpServletResponse res, String message, String pageName) {
		try {
			try {
				req.getSession().invalidate();
				Cookie[] cookies = req.getCookies();
			    // Delete all the cookies
			    if (cookies != null) {
			        for (int i = 0; i < cookies.length; i++) {
			        	if(cookies[i].getName().equals("username")){
			        		Cookie cookie = cookies[i];
				            cookies[i].setValue(null);
				            cookies[i].setMaxAge(0);
				            res.addCookie(cookie);
			        	}
			        }
			    }
			} catch (Exception e) {
			}
			if(!res.isCommitted()) {
				RequestDispatcher rd= req.getRequestDispatcher(pageName);
				req.setAttribute("fail", message);
				rd.forward(req, res);
			} else {
				res.sendRedirect(pageName);
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
}