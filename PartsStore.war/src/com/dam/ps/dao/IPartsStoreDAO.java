/*
 * This software is the confidential and proprietary information of
 * Wipro. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license 
 * agreement you entered into with Wipro.
 *
 */

package com.dam.ps.dao;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Interface: Interface for all database access
 * @author WIPRO TECHNOLOGIES
 * @version 1.00
 * @since 02-MAR-2017 03:41:41 PM
 */
public interface IPartsStoreDAO {
	
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
	 * @param query String
	 * @return result JSONObject
	 */
	public JSONObject search(String query);
	
	/**
	 * This method gets all properties
	 * @param productIDs String
	 * @return result JSONObject
	 */
	public JSONObject getAllProperties(String productIDs);
	
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
	 * This method updates the cart
	 * @param updateDetails JSONArray
	 * @param userID String
	 * @return int
	 */
	public int updateCartInfo(final JSONArray updateDetails,final String userID);
	
	/**
	 * This method deletes products from cart
	 * @param updateDetails JSONArray
	 * @param userID String
	 * @return  int
	 */
	public int deleteProductsFromCart(final JSONArray updateDetails,final String userID);
	
	/**
	 * This method clears all products in a cart
	 * @param userID String
	 * @return count int[]
	 */
	public int clearCart(final String userID);
	
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
	 * @return int
	 */
	public int updateShippingInfo(final JSONArray updateDetails,final String userID);
	
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

