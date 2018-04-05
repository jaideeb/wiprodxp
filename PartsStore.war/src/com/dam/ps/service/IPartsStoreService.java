/*
 * This software is the confidential and proprietary information of
 * Wipro. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license 
 * agreement you entered into with Wipro.
 *
 */

package com.dam.ps.service;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public interface IPartsStoreService {
	
	/**
	 * This method authenticates a user
	 * @param userName String
	 * @param password String
	 * @param sessionID String
	 * @return result JSONObject
	 */
	public JSONObject login(String userName, String password, String sessionID);
	
	/**
	 * This method displays the product menu
	 * @param instanceID String
	 * @return result JSONObject
	 */
	public JSONObject showProductMenu(String instanceID);
	
	/**
	 * This method pulls car information from store
	 * @return result JSONObject
	 */
	public JSONObject getMyCar();
	
	/**
	 * This method pulls car information from store
	 * @param instanceID String
	 * @param searchText String
	 * @param categoryName String
	 * @return result JSONObject
	 */
	public JSONObject search(String instanceID, String searchText, String categoryName);
	
	/**
	 * This method fetches the product info
	 * @param productID String
	 * @return result JSONObject
	 */
	public JSONObject productInfo(String productID);
	
	/**
	 * This method fetches the product info
	 * @param userID String
	 * @param productDetailsID String
	 * @param productName String
	 * @param productImagePath String
	 * @param quantity String
	 * @param price String
	 * @return result JSONObject
	 */
	public JSONObject addToCart(String userID, String productDetailsID, String productName, String productImagePath, String quantity, String price);
	
	/**
	 * This method filters on search results
	 * @param key String
	 * @param value String
	 * @param low String
	 * @param high String
	 * @param query String
	 * @return result JSONObject
	 */
	public JSONObject filter(String key, String value, String low, String high, String query);
	
	/**
	 * This method updates the cart
	 * @param updateDetails String
	 * @param userID String
	 * @param operation String
	 * @return JSONObject
	 */
	public JSONObject updateCart(String updateDetails,String userID, String operation);
	
	/**
	 * This method pulls cart details
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject pullCartDetails(String userID);
	
	/**
	 * This method fetches all available addresses for an user
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject getAddressList(String userID);
	
	/**
	 * This method fetches all available shipping options
	 * @return JSONObject
	 */
	public JSONObject getShippingOptions();
	
	/**
	 * This method updates the shipping information for the products
	 * @param updateDetails JSONArray
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject updateShippingInfo(String updateDetails,String userID);
	
	/**
	 * This method shows the confirmation page, just before placing the order
	 * @param userID String
	 * @param deliveryAddressID String
	 * @return JSONObject
	 */
	public JSONObject showConfirmation(String userID, String deliveryAddressID);
	
	/**
	 * This method generates the order
	 * @param orderDetails JSONObject
	 * @return JSONObject
	 */
	public JSONObject generateOrder(JSONObject orderDetails);
	
	/**
	 * This method fetches all the orders for a given user
	 * @param userID String
	 * @param orderID String
	 * @return JSONObject
	 */
	public JSONObject getMyOrders(String userID, String orderID);
	
}
