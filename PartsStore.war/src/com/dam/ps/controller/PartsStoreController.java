/*
 * This software is the confidential and proprietary information of
 * Wipro. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license 
 * agreement you entered into with Wipro.
 *
 */

package com.dam.ps.controller;


import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dam.ps.service.IPartsStoreService;

import net.sf.json.JSONObject;


@Controller
public class PartsStoreController {

	/*
	 * Auto wiring Logger 
	 */
	private static final Logger _LOGGER = Logger.getLogger(PartsStoreController.class);
	
	/**
	 * Auto wiring queries source file
	 */
	@Autowired
	private Properties _sources;
	
	/*
	 * Auto wiring Service
	 */
	@Autowired
	private IPartsStoreService _partsStoreService;
	
	/**
	 * This method saves a car selection
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return carDetails JSONObject
	 * 
	 */
	@RequestMapping(value="/savemycar.pss")
	public @ResponseBody JSONObject saveMyCar(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-saveMyCar-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		HttpSession session = request.getSession();
		String selectedInstanceID = request.getParameter("instanceid");
		String selectedInstanceImage = request.getParameter("imagepath");
		String selectedVariant = request.getParameter("variant");
			session.setAttribute("selectedinstanceid", selectedInstanceID);
				_LOGGER.info("--------------------------"+selectedInstanceID);
			session.setAttribute("selectedinstanceimage", selectedInstanceImage);
				_LOGGER.info("--------------------------"+selectedInstanceImage);
			session.setAttribute("selectedvariant", selectedVariant);
				_LOGGER.info("--------------------------"+selectedVariant);
			result.put("status", "success");
			result.put("message", "Details Saved");
			result.put("data", selectedVariant+"@@"+selectedInstanceImage+"@@"+selectedInstanceID);
		_LOGGER.info("PartsStoreController-saveMyCar-ENDS"); 
		return result;
	}
	
	
	/**
	 * This method removes a car selection
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return carDetails JSONObject
	 * 
	 */
	
	@RequestMapping(value="/removeCar.pss")
	public @ResponseBody JSONObject removeCar(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-removeCar-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "success");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		HttpSession session = request.getSession();
		session.removeAttribute("selectedinstanceid");
		session.removeAttribute("selectedinstanceimage");
		session.removeAttribute("selectedvariant");
		_LOGGER.info("PartsStoreController-removeCar-ENDS"); 
		return result;
	}
	
	/**
	 * This method authenticates a user
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return carDetails JSONObject
	 * 
	 */
	@RequestMapping(value="/login.pss")
	public @ResponseBody JSONObject login(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-login-STARTS");
		String userName = request.getParameter("userid");
		String password = request.getParameter("password");
		JSONObject userDetails = _partsStoreService.login(userName, password, request.getSession().getId());
		if(userDetails.containsKey("status") && userDetails.getString("status").equals("success")){
			JSONObject result = userDetails.getJSONObject("data");
			HttpSession session = request.getSession();
			session.setAttribute("userid", result.getString("USER_ID"));
			session.setAttribute("profilepic", result.getString("PROFILE_PIC"));
			session.setAttribute("fullname", result.getString("FULL_NAME"));
		}
		_LOGGER.info("PartsStoreController-login-ENDS"); 
		return userDetails;
	}
	
	/**
	 * This method fetches the available car models from store
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return carDetails JSONObject
	 * 
	 */
	@RequestMapping(value="/getmycar.pss")
	public @ResponseBody JSONObject getMyCar(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-showProductMenu-STARTS");
		JSONObject carDetails = null;
		HttpSession session = request.getSession(); 
		if(session.getAttribute("cardetails") == null) {
			carDetails = _partsStoreService.getMyCar();
			session.setAttribute("cardetails", carDetails);
		} else {
			carDetails = (JSONObject)session.getAttribute("cardetails");
		}
		_LOGGER.info("PartsStoreController-showProductMenu-ENDS"); 
		return carDetails;
	}
	
	/**
	 * This method searches for products. Typically used for AJAX searches
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	// PASS EITHER THE SEARCHTEXT OR THE CATEGORY NAME - NOT BOTH AT THE SAME TIME
	// SELECTED CAR WILL BE PICKED UP FROM SESSION
	@RequestMapping(value="/search.pss")
	public @ResponseBody JSONObject search(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-search-STARTS");
		HttpSession session = request.getSession();
		String instanceID = null;
		if(session.getAttribute("selectedinstanceid") != null) {
			instanceID = session.getAttribute("selectedinstanceid").toString();
		}
		String searchText = request.getParameter("searchtext");
		String categoryName = request.getParameter("categoryname");
		JSONObject searchResults = _partsStoreService.search(null, searchText, categoryName);
		if(searchResults.containsKey("query")) {
			String query = searchResults.getString("query");
			searchResults.remove("query");
			session.setAttribute("query", query);
		}
		_LOGGER.info("PartsStoreController-search-ENDS"); 
		return searchResults;
	}
	
	/**
	 * This method searches for products. Typically used for searches where search results will be displayed on a fresh page
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	// PASS EITHER THE SEARCHTEXT OR THE CATEGORY NAME - NOT BOTH AT THE SAME TIME
	// SELECTED CAR WILL BE PICKED UP FROM SESSION
	@RequestMapping(value="/searchpage.pss")
	public String searchPage(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-searchPage-STARTS");
		HttpSession session = request.getSession();
		String instanceID = null;
		if(session.getAttribute("selectedinstanceid") != null) {
			instanceID = session.getAttribute("selectedinstanceid").toString();
		}
		String searchText = request.getParameter("searchtext");
		String categoryName = request.getParameter("categoryname");
		JSONObject searchResults = _partsStoreService.search(null, searchText, categoryName);
		if(searchResults.containsKey("query")) {
			String query = searchResults.getString("query");
			searchResults.remove("query");
			session.setAttribute("query", query);
		}
		request.setAttribute("searchresult",StringEscapeUtils.escapeJavaScript(searchResults.toString()));
		_LOGGER.info("PartsStoreController-searchPage-ENDS"); 
		return "searchresult/searchresult";
	}
	
	/**
	 * This method searches for products. Typically used for searches where search results will be displayed on a fresh page
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/productinfo.pss")
	public String productInfo(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-productInfo-STARTS");
		String productID = request.getParameter("productid");
		JSONObject productInfo = _partsStoreService.productInfo(productID);
		_LOGGER.info("PRODUCT INFO::"+productInfo);
		request.setAttribute("productinfo",productInfo);
		_LOGGER.info("PartsStoreController-productInfo-ENDS"); 
		return "productinfo/productinfo";
	}
	
	/**
	 * This method fetches all delivery addresses for a given user
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/getdeliverydetails.pss")
	public @ResponseBody JSONObject getAddressList(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-getAddressList-STARTS");
		HttpSession session = request.getSession();
		String userID = null;
		JSONObject result = null;
		if(session.getAttribute("userid") == null) {
			result = new JSONObject();
			result.accumulate("status", "failed");
			result.accumulate("message", "user not logged in");
			result.accumulate("data", "N/A");
		} else {
			userID = session.getAttribute("userid").toString();
			result = _partsStoreService.getAddressList(userID);
		}
		_LOGGER.info("PartsStoreController-getAddressList-ENDS"); 
		return result;
	}
	
	/**
	 * This method updates the shipping information for products in cart
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/deliveryinformation.pss")
	public @ResponseBody JSONObject deliveryInformation(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-getAddressList-STARTS");
		HttpSession session = request.getSession();
		String userID = null;
		JSONObject result = null;
		if(session.getAttribute("userid") == null) {
			result = new JSONObject();
			result.accumulate("status", "failed");
			result.accumulate("message", "user not logged in");
			result.accumulate("data", "N/A");
		} else {
			userID = session.getAttribute("userid").toString();
			String detailString = request.getParameter("detailstring");
			//UPDATE SHIPPING INFO
			result = _partsStoreService.updateShippingInfo(detailString, userID);
			// STORE IN SESSION DELIVERY_ADDRESS_ID, SPECIAL_INSTRUCTION AND ORDER_TOTAL
			String specialInstructions = request.getParameter("specialinstructions");
			String orderTotal = request.getParameter("ordertotal");
			String deliveryAddressID = request.getParameter("deliveryaddressid");
			session.setAttribute("specialinstructions", specialInstructions);
			session.setAttribute("ordertotal", orderTotal);
			session.setAttribute("deliveryaddressid", deliveryAddressID);
		}
		_LOGGER.info("PartsStoreController-getAddressList-ENDS"); 
		return result;
	}
	
	/**
	 * This method fetches all available shipping information
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/getshippinginfo.pss")
	public @ResponseBody JSONObject getShippingInfo(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-getShippingInfo-STARTS");
		JSONObject shippingInfo = _partsStoreService.getShippingOptions();
		_LOGGER.info("PartsStoreController-getShippingInfo-ENDS"); 
		return shippingInfo;
	}
	
	/**
	 * This method displays the confirmation page
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/showconfirmation.pss")
	public @ResponseBody JSONObject showConfirmation(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-showConfirmation-STARTS");
		JSONObject result = null;
		String userID = null;
		String specialInstructions = null;
		String orderTotal = null;
		String deliveryAddressID = null;
		HttpSession session = request.getSession();
		if(session.getAttribute("userid") == null) {
			result = new JSONObject();
			result.accumulate("status", "failed");
			result.accumulate("message", "user not logged in");
			result.accumulate("data", "N/A");
		} else {
			userID = session.getAttribute("userid").toString();
			deliveryAddressID = session.getAttribute("deliveryaddressid").toString();
			specialInstructions = session.getAttribute("specialinstructions").toString();
			orderTotal = session.getAttribute("ordertotal").toString();
			result = _partsStoreService.showConfirmation(userID, deliveryAddressID);
				if(result.containsKey("status") && result.getString("status").equals("success")){
					JSONObject js = result.getJSONObject("data");
					js.accumulate("ordertotal", orderTotal);
					js.accumulate("specialinstructions", specialInstructions);
					result.put("data", js);
				}
		}
		_LOGGER.info("PartsStoreController-showConfirmation-ENDS"); 
		return result;
	}
	
	/**
	 * This method pulls cart details
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/pullcartdetails.pss")
	public @ResponseBody JSONObject pullCartDetails(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-pullCartDetails-STARTS");
		String userID = null;
		HttpSession session = request.getSession();
		if(session.getAttribute("userid") == null) {
			userID = session.getId();
		} else {
			userID = session.getAttribute("userid").toString();
		}
		JSONObject carDetails = _partsStoreService.pullCartDetails(userID);
		_LOGGER.info("PartsStoreController-pullCartDetails-ENDS"); 
		return carDetails;
	}
	
	/**
	 * This method searches for products. Typically used for searches where search results will be displayed on a fresh page
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/logoff.pss")
	public String logoff(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-logoff-STARTS");
		request.getSession().invalidate();
		_LOGGER.info("PartsStoreController-logoff-ENDS"); 
		return "forward:/showPage.pss?page=landing";
	}
	
	/**
	 * This method adds products to cart
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return status JSONObject
	 * 
	 */
	@RequestMapping(value="/addtocart.pss")
	public @ResponseBody JSONObject addToCart(HttpServletRequest request,HttpServletResponse response) {
		_LOGGER.info("PartsStoreController-addToCart-STARTS");
		HttpSession session = request.getSession();
		String userID = null;
		if(session.getAttribute("userid") == null) {
			userID = session.getId();
		} else {
			userID = session.getAttribute("userid").toString();
		}
		String productDetailsID = request.getParameter("productdetailsid");
		String productName = request.getParameter("productname");
		String productImagePath = request.getParameter("productimagepath");
		String quantity = request.getParameter("quantity");
		String price = request.getParameter("price");
		JSONObject status = _partsStoreService.addToCart(userID, productDetailsID, productName, productImagePath, quantity, price);
		_LOGGER.info("PartsStoreController-addToCart-ENDS"); 
		return status;
	}
	
	/**
	 * This method updates a cart - perform both update and delete operations
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return searchResult JSONObject
	 * 
	 */
	@RequestMapping(value="/modifycart.pss")
	public @ResponseBody JSONObject modifyCart(HttpServletRequest request,HttpServletResponse response) {
		_LOGGER.info("PartsStoreController-modifyCart-STARTS");
		HttpSession session = request.getSession();
		String userID = null;
		if(session.getAttribute("userid") == null) {
			userID = session.getId();
		} else {
			userID = session.getAttribute("userid").toString();
		}
		String detailString = request.getParameter("detailstring");
		String operation = request.getParameter("operation");
		JSONObject status = _partsStoreService.updateCart(detailString, userID, operation);
		_LOGGER.info("PartsStoreController-modifyCart-ENDS"); 
		return status;
	}
	
	
	/**
	 * This method displays the product menu
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return productMenu JSONObject
	 * 
	 */
	@RequestMapping(value="/filter.pss")
	public @ResponseBody JSONObject filter(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-filter-STARTS");
			HttpSession session = request.getSession();
			String query = session.getAttribute("query").toString();
			String key = request.getParameter("key");
			String value = request.getParameter("value");
			String low = request.getParameter("low");
			String high = request.getParameter("high");
			_LOGGER.info("SESION QUERY::"+query);
			JSONObject status = _partsStoreService.filter(key, value, low, high, query);
		_LOGGER.info("PartsStoreController-filter-ENDS"); 
		return status;
	}
	
	/**
	 * This method displays the product menu
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return productMenu JSONObject
	 * 
	 */
	@RequestMapping(value="/generateorder.pss")
	public @ResponseBody JSONObject generateOrder(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-generateOrder-STARTS");
			HttpSession session = request.getSession();
			JSONObject orderDetails = new JSONObject();
			/*orderDetails.accumulate("userid", "1");
			orderDetails.accumulate("addressid", "1");
			orderDetails.accumulate("specialinstructions", "Please get this order delivered on time");
			orderDetails.accumulate("ordertotal", "456.87");
			orderDetails.accumulate("orderplacedate", "03/14/2017 23:11");
			orderDetails.accumulate("paymentmode", "Card");
			orderDetails.accumulate("cardno", "4326");*/
			
			orderDetails.accumulate("userid", session.getAttribute("userid").toString());
			orderDetails.accumulate("addressid", session.getAttribute("deliveryaddressid").toString());
			orderDetails.accumulate("specialinstructions", session.getAttribute("specialinstructions").toString());
			orderDetails.accumulate("ordertotal", session.getAttribute("ordertotal").toString());
			orderDetails.accumulate("orderplacedate", request.getParameter("orderplacedate"));
			orderDetails.accumulate("paymentmode", session.getAttribute("paymentmode").toString());
			orderDetails.accumulate("cardno", session.getAttribute("cardno").toString());
			
			JSONObject status = _partsStoreService.generateOrder(orderDetails);
			if(status.containsKey("status") && status.getString("status").equals("success")){
				session.removeAttribute("addressid");
				session.removeAttribute("specialinstructions");
				session.removeAttribute("ordertotal");
				session.removeAttribute("query");
			}
		_LOGGER.info("PartsStoreController-generateOrder-ENDS"); 
		return status;
	}
	
	/**
	 * This method captures card details
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return productMenu JSONObject
	 * 
	 */
	@RequestMapping(value="/capturecarddetails.pss")
	public @ResponseBody JSONObject capturecarddetails(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-capturecarddetails-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		String paymentMode = request.getParameter("paymentmode");
		String cardNo = request.getParameter("cardno");
		HttpSession session = request.getSession();
		session.setAttribute("paymentmode", paymentMode);
		session.setAttribute("cardno", cardNo);
		result.put("status", "success");
		_LOGGER.info("PartsStoreController-capturecarddetails-ENDS"); 
		return result;
	}
	
	/**
	 * This method fetches the orders for a specific user 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return productMenu JSONObject
	 * 
	 */
	@RequestMapping(value="/getmyorders.pss")
	public @ResponseBody JSONObject getMyOrders(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-getMyOrders-STARTS");
		JSONObject result = new JSONObject();
		HttpSession session = request.getSession();
		String userID = null;
		if(session.getAttribute("userid") == null) {
			result = new JSONObject();
			result.accumulate("status", "failed");
			result.accumulate("message", "user not logged in");
			result.accumulate("data", "N/A");
		} else {
			userID = session.getAttribute("userid").toString();
			String orderID = request.getParameter("orderid");
			result = _partsStoreService.getMyOrders(userID,orderID);
		}
		_LOGGER.info("PartsStoreController-getMyOrders-ENDS"); 
		return result;
	}
	
	
	/**
	 * This method displays the product menu
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return productMenu JSONObject
	 * 
	 */
	@RequestMapping(value="/showproductmenu.pss")
	public @ResponseBody JSONObject showProductMenu(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-showProductMenu-STARTS");
		String instanceID = request.getParameter("instanceid");
		JSONObject productMenu = _partsStoreService.showProductMenu(instanceID);
		_LOGGER.info("PartsStoreController-showProductMenu-ENDS"); 
		return productMenu;
	}
	
	@RequestMapping(value="/showPage.pss")
	public String showPage(HttpServletRequest request,HttpServletResponse response){
		_LOGGER.info("PartsStoreController-showPage-STARTS");
		String page = request.getParameter("page").toString();
		if(page.equals("landing")){
			try{
				request.setAttribute("ProductCategories",StringEscapeUtils.escapeJavaScript(_partsStoreService.showProductMenu(null).toString()));	
				JSONObject carDetails = null;
				HttpSession session = request.getSession(); 
				if(session.getAttribute("cardetails") == null) {
					carDetails = _partsStoreService.getMyCar();
					session.setAttribute("cardetails", carDetails);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			page = "landing/landing";
		} else if(page.equals("search")){
			page = "searchresult/searchresult";
		} else if(page.equals("productinfo")){
			page = "productinfo/productinfo";
		} else if(page.equals("delivery")){
			page = "checkout/delivery";
		} else if(page.equals("payment")){
			page = "checkout/payment";
		} else if(page.equals("summary")){
			page = "checkout/summary";
		} else if(page.equals("order")){
			request.setAttribute("orderid", request.getParameter("orderid"));
			page = "order/confirmation";
		} else if(page.equals("cart")){
			page = "cart/cart";
		} else if(page.equals("myorders")){
			page = "order/myorders";
		} else if(page.equals("orderpage")){
			request.setAttribute("orderid", request.getParameter("orderid"));
			page = "order/order";
		} else if(page.equals("distributor")){
			page = "analytics/home";
		} else if(page.equals("dashboard")){
			page = "analytics/dashboard";
		} else if(page.equals("sales")){
			page = "analytics/sales";
		}
		_LOGGER.info("PartsStoreController-showPage-ENDS"); 
		return page;
	}
}
