/*
 * This software is the confidential and proprietary information of
 * Wipro. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license 
 * agreement you entered into with Wipro.
 *
 */

package com.dam.ps.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.dam.ps.util.JSONMapper;

@Service("_partsStoreDAO")
public class PartsStoreDAO implements IPartsStoreDAO{
	/*
	 * private static logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(PartsStoreDAO.class);

	/**
	 * private jdbcTemplate
	 */
	private JdbcTemplate jdbcTemplate;

	/**
	 * setter method for data source injection
	 */
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
	 * Auto wiring queries
	 */
	@Autowired
	private Properties _queries;
	
	/**
	 * Autowiring Sources
	 */
	@Autowired
	private Properties _sources; 
	
	/**
	 * This method authenticates a user
	 * @param userName String
	 * @param password String
	 * @param sessionID String
	 * @return result JSONObject
	 */
	public JSONObject login(String userName, String password, String sessionID) {
		_LOGGER.info("PartsStoreDAO-login-STARTS");
		JSONObject result = new JSONObject();
		try {
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		JSONObject user = jdbcTemplate.queryForObject(_queries.getProperty("authenticate.user"), new JSONMapper(),userName.toLowerCase(), password);
		_LOGGER.info("SESSION ID::"+sessionID);
		
		// GET ALL THE PRODUCTS WITH THE OLD SESSION ID
		List<JSONObject> productList = jdbcTemplate.query(_queries.getProperty("transfer.cart.get.products"), new JSONMapper(), sessionID);
		// TRANSFER THE PRODUCTS FROM OLD SESSION ID TO USER ID
		updateUserID(productList,sessionID,user.getString("USER_ID"));
		
		JSONObject itemsInCart = jdbcTemplate.queryForObject(_queries.getProperty("items.in.cart"), new JSONMapper(), user.getString("USER_ID"));
		user.accumulate("CART_ITEMS", itemsInCart.getString("ITEMS_IN_CART"));
		result.put("status", "success");
		result.put("message", "N/A");
		
		if(userName.toLowerCase().equals("brand_manager")){
			user.accumulate("brandmanager", "true");
		} else {
			user.accumulate("brandmanager", "false");
		}
		result.put("data", user);
		}catch(Exception e){
			e.printStackTrace();
			result.put("message", "login failed");
		}
		_LOGGER.info("PartsStoreDAO-login-ENDS");
		return result;
	}
	
	/**
	 * This method updates the cart after login
	 * @param productIDs List<JSONObject>
	 * @param userID String
	 * @return count int
	 */
	private void updateUserID(List<JSONObject> productIDs,String sessionID, String userID) {
		_LOGGER.info("PartsStoreDAO-updateUserID-STARTS");
			for(JSONObject js : productIDs) {
				try {
					jdbcTemplate.queryForObject(_queries.getProperty("transfer.cart.get.individual.product.status"), new JSONMapper(),js.getString("PRODUCT_DETAILS_ID"),userID );
					jdbcTemplate.update(_queries.getProperty("transfer.cart.remove.product"),sessionID,js.getString("PRODUCT_DETAILS_ID"));
				} catch(Exception e){
					jdbcTemplate.update(_queries.getProperty("transfer.cart.after.login"), userID, sessionID,js.getString("PRODUCT_DETAILS_ID"));
				}
			}
		 _LOGGER.info("PartsStoreDAO-updateUserID-ENDS");
	}
	
	/**
	 * This method displays the product menu
	 * @param instanceID String
	 * @return result JSONObject
	 */
	public JSONObject showProductMenu(String instanceID) {
		_LOGGER.info("PartsStoreDAO-showProductMenu-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		List<JSONObject> productMenu = null;
		String query = null;
		if(instanceID == null) {
			query = _queries.getProperty("get.product.menu").replace("RUNTIME_PARAM", "");
			productMenu = jdbcTemplate.query(query, new JSONMapper());
		} else {
			query = _queries.getProperty("get.product.menu").replace("RUNTIME_PARAM", " WHERE INSTANCE_ID = ? ");;
			productMenu = jdbcTemplate.query(query, new JSONMapper(), instanceID);
		}
		_LOGGER.info("QUERY::"+query);
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", productMenu);
		_LOGGER.info("PartsStoreDAO-showProductMenu-ENDS");
		return result;
	}
	
	/**
	 * This method pulls car information from store
	 * @return result JSONObject
	 */
	public JSONObject getMyCar() {
		_LOGGER.info("PartsStoreDAO-getMyCar-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		List<JSONObject> car = jdbcTemplate.query(_queries.getProperty("get.my.car"), new JSONMapper());
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", car);
		_LOGGER.info("PartsStoreDAO-getMyCar-ENDS");
		return result;
	}
	
	/**
	 * This method pulls car information from store
	 * @param instanceID String
	 * @param searchText String
	 * @param categoryName String
	 * @return result JSONObject
	 */
	public JSONObject search(String query) {
		_LOGGER.info("PartsStoreDAO-search-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		List<JSONObject> searchResults = jdbcTemplate.query(query, new JSONMapper());
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", searchResults);
		_LOGGER.info("PartsStoreDAO-search-ENDS");
		return result;
	}
	
	/**
	 * This method gets all properties
	 * @param productIDs String
	 * @return result JSONObject
	 */
	public JSONObject getAllProperties(String productIDs){
		_LOGGER.info("PartsStoreDAO-getAllProperties-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		List<JSONObject> allProperties = jdbcTemplate.query(_queries.getProperty("get.all.properties").replace("@",productIDs), new JSONMapper());
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", allProperties);
		_LOGGER.info("PartsStoreDAO-getAllProperties-ENDS");
		return result;
	}
	
	/**
	 * This method fetches the product info
	 * @param productID String
	 * @return result JSONObject
	 */
	public JSONObject productInfo(String productID){
		_LOGGER.info("PartsStoreDAO-productInfo-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		try {
		JSONObject productInfo = jdbcTemplate.queryForObject(_queries.getProperty("get.product.info.main"), new JSONMapper(),productID);
		List<JSONObject> allProperties = jdbcTemplate.query(_queries.getProperty("get.product.info.properties"), new JSONMapper(),productID);
		productInfo.accumulate("properties", allProperties);
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", productInfo);
		}catch(Exception e){
			result.accumulate("message", "An error occurred");
		}
		_LOGGER.info("PartsStoreDAO-productInfo-ENDS");
		return result;
	}
	
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
	public JSONObject addToCart(String userID, String productDetailsID, String productName, String productImagePath, String quantity, String price){
		_LOGGER.info("PartsStoreDAO-addToCart-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		try {
			int addToCartResult = jdbcTemplate.update(_queries.getProperty("add.to.cart"), userID, productDetailsID, productName,productImagePath,quantity,price);
			if(addToCartResult == 1){
				result.put("status", "success");
					
			}
		}catch(Exception e){
			if(e.getMessage().contains("ORA-00001")){
				result.put("message", "product already in cart");
			}
		}
		
		_LOGGER.info("PartsStoreDAO-addToCart-ENDS");
		return result;
	}
	
	/**
	 * This method updates the cart
	 * @param updateDetails JSONArray
	 * @param userID String
	 * @return count int[]
	 */
	public int updateCartInfo(final JSONArray updateDetails,final String userID) {
		_LOGGER.info("PartsStoreDAO-updateCart-STARTS");
			 int[] count=jdbcTemplate.batchUpdate(_queries.getProperty("update.cart"), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setString(1, updateDetails.getJSONObject(i).getString("QUANTITY"));
						ps.setString(2, updateDetails.getJSONObject(i).getString("PRICE"));
						ps.setString(3, updateDetails.getJSONObject(i).getString("PRODUCT_DETAILS_ID"));
						ps.setString(4, userID);
				}
				@Override
				public int getBatchSize() {
					return updateDetails.size();
				}
			});
		 _LOGGER.info("PartsStoreDAO-updateCart-ENDS");
		return count.length;
	}
	
	/**
	 * This method updates the shipping information for the products
	 * @param updateDetails JSONArray
	 * @param userID String
	 * @return int
	 */
	public int updateShippingInfo(final JSONArray updateDetails,final String userID){
		_LOGGER.info("PartsStoreDAO-updateShippingInfo-STARTS");
		 int[] count=jdbcTemplate.batchUpdate(_queries.getProperty("update.shipping.info"), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
					ps.setString(1, updateDetails.getJSONObject(i).getString("QUANTITY"));
					ps.setString(2, updateDetails.getJSONObject(i).getString("SHIPPING_ID"));
					ps.setString(3, updateDetails.getJSONObject(i).getString("PRODUCT_DETAILS_ID"));
					ps.setString(4, userID);
			}
			@Override
			public int getBatchSize() {
				return updateDetails.size();
			}
		});
	 _LOGGER.info("PartsStoreDAO-updateShippingInfo-ENDS");
	return count.length;
		
	}
	
	/**
	 * This method deletes products from cart
	 * @param updateDetails JSONArray
	 * @param userID String
	 * @return count int[]
	 */
	public int deleteProductsFromCart(final JSONArray updateDetails,final String userID) {
		_LOGGER.info("PartsStoreDAO-deleteProductsFromCart-STARTS");
			 int[] count=jdbcTemplate.batchUpdate(_queries.getProperty("delete.from.cart"), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setString(1, updateDetails.getJSONObject(i).getString("PRODUCT_DETAILS_ID"));
						ps.setString(2, userID);
				}
				@Override
				public int getBatchSize() {
					return updateDetails.size();
				}
			});
		 _LOGGER.info("PartsStoreDAO-deleteProductsFromCart-ENDS");
		return count.length;
	}
	/**
	 * This method clears all products in a cart
	 * @param userID String
	 * @return count int
	 */
	public int clearCart(final String userID){
		_LOGGER.info("PartsStoreDAO-clearCart-STARTS");
			int clearCart = jdbcTemplate.update(_queries.getProperty("clear.cart"), userID);
		 _LOGGER.info("PartsStoreDAO-clearCart-ENDS");
		 return clearCart;
	}
	
	/**
	 * This method pulls cart details
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject pullCartDetails(String userID){
		_LOGGER.info("PartsStoreDAO-pullCartDetails-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		List<JSONObject> cartDetails = jdbcTemplate.query(_queries.getProperty("pull.cart.details"),new JSONMapper(), userID);
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", cartDetails);
		_LOGGER.info("PartsStoreDAO-pullCartDetails-ENDS");
		return result;
	}
	
	/**
	 * This method fetches all available addresses for an user
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject getAddressList(String userID){
		_LOGGER.info("PartsStoreDAO-getAddressList-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		List<JSONObject> addressList = jdbcTemplate.query(_queries.getProperty("get.all.addresses"),new JSONMapper(), userID);
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", addressList);
		_LOGGER.info("PartsStoreDAO-getAddressList-ENDS");
		return result;
	}
	
	/**
	 * This method fetches all available shipping options
	 * @return JSONObject
	 */
	public JSONObject getShippingOptions(){
		_LOGGER.info("PartsStoreDAO-getShippingOptions-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		List<JSONObject> shippingInfo = jdbcTemplate.query(_queries.getProperty("get.shipping.info"),new JSONMapper());
		result.put("status", "success");
		result.put("message", "N/A");
		result.put("data", shippingInfo);
		_LOGGER.info("PartsStoreDAO-getShippingOptions-ENDS");
		return result;
	}
	
	/**
	 * This method shows the confirmation page, just before placing the order
	 * @param userID String
	 * @param deliveryAddressID String
	 * @return JSONObject
	 */
	public JSONObject showConfirmation(String userID, String deliveryAddressID){
		_LOGGER.info("PartsStoreDAO-showConfirmation-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		JSONObject js = new JSONObject();
		// GET CART DETAILS
		List<JSONObject> cartDetails = jdbcTemplate.query(_queries.getProperty("pull.cart.details"),new JSONMapper(), userID);
		js.accumulate("cartdetails", cartDetails);
		// GET THE DELIVERY ADDRESS
		JSONObject specificDeliveryAddress = jdbcTemplate.queryForObject(_queries.getProperty("get.specific.address"), new JSONMapper(),userID, deliveryAddressID);
		js.accumulate("selecteddeliveryaddress", specificDeliveryAddress);
		result.put("data", js);
		result.put("status", "success");
		_LOGGER.info("PartsStoreDAO-showConfirmation-ENDS");
		return result;
	}
	
	/**
	 * This method generates the order
	 * @param orderDetails JSONObject
	 * @return JSONObject
	 */
	public JSONObject generateOrder(JSONObject orderDetails){
		_LOGGER.info("PartsStoreDAO-generateOrder-STARTS");
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		
		// GENERATE THE ORDER ID
		JSONObject sequenceID = jdbcTemplate.queryForObject(_queries.getProperty("generate.order.id"), new JSONMapper());
		String generatedOrderID = "PRTSSTR00"+sequenceID.getString("NEXT_NUM");
		jdbcTemplate.update(_queries.getProperty("insert.into.orders"), generatedOrderID,
																	    orderDetails.getString("userid"), 
																	    orderDetails.getString("addressid"),
																	    "N/A",
																	    orderDetails.getString("ordertotal"),
																	    orderDetails.getString("specialinstructions"),
																	    orderDetails.getString("orderplacedate"),
																	    orderDetails.getString("paymentmode"),
																	    orderDetails.getString("cardno")
																		);
		// GET CART DETAILS
		List<JSONObject> cartDetails = jdbcTemplate.query(_queries.getProperty("pull.cart.details"),new JSONMapper(), orderDetails.getString("userid"));
		_LOGGER.info("CART DETAILS::"+cartDetails);
		// INSERT IN PRODUCT DETAILS
		insertInOrderDetails(cartDetails,generatedOrderID);
		// DELETE ALL THE ITEMS IN THE CART
		clearCart(orderDetails.getString("userid"));
		result.put("status", "success");
		JSONObject js = new JSONObject();
		js.accumulate("orderid", generatedOrderID);
		result.put("data", js);
		_LOGGER.info("PartsStoreDAO-generateOrder-ENDS");
		return result;
	}
	
	/**
	 * This method inserts products in product details
	 * @param insertDetails List<JSONObject>
	 * @param orderID String
	 * @return int
	 */
	private int insertInOrderDetails(final List<JSONObject> insertDetails,final String orderID){
		_LOGGER.info("PartsStoreDAO-insertInOrderDetails-STARTS");
		 int[] count=jdbcTemplate.batchUpdate(_queries.getProperty("insert.into.order.details"), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				JSONObject js = (JSONObject)insertDetails.get(i);
					ps.setString(1, orderID);
					_LOGGER.info("PRODUCT DETAILS ID::"+js.getString("PRODUCT_DETAILS_ID"));
					_LOGGER.info("PRODUCT_NAME::"+js.getString("PRODUCT_NAME"));
					_LOGGER.info("PRODUCT_IMAGE_PATH::"+js.getString("PRODUCT_IMAGE_PATH"));
					_LOGGER.info("QUANTITY::"+js.getString("QUANTITY"));
					_LOGGER.info("PRICE::"+js.getString("PRICE"));
					_LOGGER.info("SHIPPING_ID::"+js.getString("SHIPPING_ID"));
					_LOGGER.info("----------------------------");
					ps.setString(2, js.getString("PRODUCT_DETAILS_ID"));
					ps.setString(3, js.getString("PRODUCT_NAME"));
					ps.setString(4, js.getString("PRODUCT_IMAGE_PATH"));
					ps.setString(5, js.getString("QUANTITY"));
					ps.setString(6, js.getString("PRICE"));
					ps.setString(7, js.getString("SHIPPING_ID"));
			}
			@Override
			public int getBatchSize() {
				return insertDetails.size();
			}
		});
	 _LOGGER.info("PartsStoreDAO-insertInOrderDetails-ENDS");
	return count.length;
		
	}
	
	/**
	 * This method fetches all the orders for a given user
	 * @param userID String
	 * @param sentOrderID String
	 * @return JSONObject
	 */
	public JSONObject getMyOrders(String userID, String sentOrderID){
		_LOGGER.info("PartsStoreDAO-generateOrder-STARTS");
		List<JSONObject> ordersAndAddress = null;
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		if(sentOrderID == null) {
			ordersAndAddress = jdbcTemplate.query(_queries.getProperty("fetch.order.address"),new JSONMapper(), userID);
		}else {
			ordersAndAddress = jdbcTemplate.query(_queries.getProperty("fetch.order.address") +" AND A.ORDER_ID = ?",new JSONMapper(), userID, sentOrderID);
		}
		List<JSONObject> orderDetails = jdbcTemplate.query(_queries.getProperty("fecth.order.details"),new JSONMapper(), userID);
		List<JSONObject> data = new ArrayList<JSONObject>();
		String orderID = null;
		for(JSONObject js1 : ordersAndAddress) {
			orderID = js1.getString("ORDER_ID");
			List<JSONObject> inner = new ArrayList<JSONObject>();
				inner: for(JSONObject js2 : orderDetails) {
					if(orderID.equals(js2.getString("ORDER_ID"))) {
						inner.add(js2);
					} else {
						continue inner;
					}
				}
			js1.accumulate("orderdetails",inner);
			data.add(js1);
		}
		result.put("data", data);
		result.put("status", "success");
		_LOGGER.info("PartsStoreDAO-generateOrder-ENDS");
		return result;
	}
	
}
