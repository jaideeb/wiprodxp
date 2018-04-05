/*
 * This software is the confidential and proprietary information of
 * Wipro. You shall not disclose such Confidential Information and 
 * shall use it only in accordance with the terms of the license 
 * agreement you entered into with Wipro.
 *
 */

package com.dam.ps.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dam.ps.dao.IPartsStoreDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Service("_partsStoreService")
public class PartsStoreService implements IPartsStoreService {

	/*
	 * private static logger
	 */
	private static final Logger _LOGGER = Logger.getLogger(PartsStoreService.class);

	/**
	 * Auto wiring GnGDAO
	 */
	@Autowired
	private IPartsStoreDAO _partsStoreDAO;

	/**
	 * Auto wiring queries source file
	 */
	@Autowired
	private Properties _sources;

	/**
	 * Auto wiring queries
	 */
	@Autowired
	private Properties _queries;
	
	/*
	 * Stop Words
	 */
	String[] arr = {"car", "a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the", "car"};
	
	/**
	 * This method authenticates a user
	 * @param userName String
	 * @param password String
	 * @param sessionID String
	 * @return result JSONObject
	 */
	public JSONObject login(String userName, String password, String sessionID){
		_LOGGER.info("PartsStoreService-login-START");
		 JSONObject result = _partsStoreDAO.login(userName, password,sessionID);
		 _LOGGER.info("PartsStoreService-login-END");
		 return result;
	}
	
	/**
	 * This method pulls car information from store
	 * @return result JSONObject
	 */
	public JSONObject getMyCar(){
		_LOGGER.info("PartsStoreService-showProductMenu-START");
		 JSONObject result = _partsStoreDAO.getMyCar();
		 _LOGGER.info("PartsStoreService-showProductMenu-END");
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
		_LOGGER.info("PartsStoreService-addToCart-START");
		 JSONObject result = _partsStoreDAO.addToCart(userID, productDetailsID, productName, productImagePath, quantity, price);
		 _LOGGER.info("PartsStoreService-addToCart-END");
		 return result;
	}
	
	/**
	 * This method updates the shipping information for the products
	 * @param updateDetails JSONArray
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject updateShippingInfo(String updateDetails,String userID){
		_LOGGER.info("PartsStoreService-updateShippingInfo-START");
		String price = null;
		String quantity = null;
		String productDetailsID = null;
		String str2 = null;
		int count = 0;
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
	    	JSONArray jasArr = new JSONArray();
			StringTokenizer st1 = new StringTokenizer(updateDetails,"@@");
				while(st1.hasMoreTokens()) {
					str2 = st1.nextToken();
						StringTokenizer st2 = new StringTokenizer(str2,"**");
							while(st2.hasMoreTokens()){
								JSONObject jo = new JSONObject();
									price = st2.nextToken();
									quantity = st2.nextToken();
									productDetailsID = st2.nextToken();
								jo.accumulate("QUANTITY", price);
								jo.accumulate("SHIPPING_ID", quantity);
								jo.accumulate("PRODUCT_DETAILS_ID", productDetailsID);
								jasArr.add(jo);
							}
			}
			count = _partsStoreDAO.updateShippingInfo(jasArr, userID);
	  if(count > 0){
			result.put("status", "success");
			result.put("message", "updated cart with shipping info");
		}
		_LOGGER.info("PartsStoreService-updateShippingInfo-END");
		return result;
	}
	
	/**
	 * This method updates the cart
	 * @param updateDetails String
	 * @param userID String
	 * @param operation String
	 * @return JSONObject
	 */
	public JSONObject updateCart(String updateDetails,String userID, String operation){
		_LOGGER.info("PartsStoreService-updateCart-START");
		String price = null;
		String quantity = null;
		String productDetailsID = null;
		String str2 = null;
		int count = 0;
		JSONObject result = new JSONObject();
		result.accumulate("status", "failed");
		result.accumulate("message", "N/A");
		result.accumulate("data", "N/A");
		if(operation.equals("clear")){
			count = _partsStoreDAO.clearCart(userID);
	    } else if(operation.equals("update")) {
	    	JSONArray jasArr = new JSONArray();
			StringTokenizer st1 = new StringTokenizer(updateDetails,"@@");
				while(st1.hasMoreTokens()) {
					str2 = st1.nextToken();
						StringTokenizer st2 = new StringTokenizer(str2,"**");
							while(st2.hasMoreTokens()){
								JSONObject jo = new JSONObject();
									price = st2.nextToken();
									quantity = st2.nextToken();
									productDetailsID = st2.nextToken();
								jo.accumulate("PRICE", price);
								jo.accumulate("QUANTITY", quantity);
								jo.accumulate("PRODUCT_DETAILS_ID", productDetailsID);
								jasArr.add(jo);
							}
			}
			count = _partsStoreDAO.updateCartInfo(jasArr, userID);
	    } else if(operation.equals("delete")){
	    	JSONArray jasArr = new JSONArray();
	    	StringTokenizer st2 = new StringTokenizer(updateDetails,"**");
			while(st2.hasMoreTokens()){
				JSONObject jo = new JSONObject();
				productDetailsID = st2.nextToken();
				jo.accumulate("PRODUCT_DETAILS_ID", productDetailsID);
				jasArr.add(jo);
			}
	    	count = _partsStoreDAO.deleteProductsFromCart(jasArr, userID);
	    } else {
	    	count = 0;
	    }
		if(count > 0){
			result.put("status", "success");
			result.put("message", "updated cart");
		}
		_LOGGER.info("PartsStoreService-updateCart-END");
		return result;
	}

	
	/**
	 * This method fetches the product info
	 * @param productID String
	 * @return result JSONObject
	 */
	public JSONObject productInfo(String productID){
		_LOGGER.info("PartsStoreService-productInfo-START");
		 JSONObject result = _partsStoreDAO.productInfo(productID);
		 _LOGGER.info("PartsStoreService-productInfo-END");
		 return result;
	}
	
	/**
	 * This method pulls cart details
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject pullCartDetails(String userID){
		_LOGGER.info("PartsStoreService-pullCartDetails-START");
		 JSONObject result = _partsStoreDAO.pullCartDetails(userID);
		 _LOGGER.info("PartsStoreService-pullCartDetails-END");
		 return result;
	}
	
	/**
	 * This method displays the product menu
	 * @param instanceID String
	 * @return result JSONObject
	 */
	public JSONObject showProductMenu(String instanceID) {
		_LOGGER.info("PartsStoreService-showProductMenu-START");
		 JSONObject result = _partsStoreDAO.showProductMenu(instanceID);
		 List<JSONObject> data =getUniqueClassCategory(result);
		 result.put("data", data);
		_LOGGER.info("PartsStoreService-showProductMenu-END");
		return result;
	}
	
	/**
	 * This method shows the confirmation page, just before placing the order
	 * @param userID String
	 * @param deliveryAddressID String
	 * @return JSONObject
	 */
	public JSONObject showConfirmation(String userID, String deliveryAddressID){
		_LOGGER.info("PartsStoreService-showConfirmation-START");
		 JSONObject confirmationDetails = _partsStoreDAO.showConfirmation(userID, deliveryAddressID);
		_LOGGER.info("PartsStoreService-showConfirmation-END");
		return confirmationDetails;
		
	}
	
	/**
	 * This method generates the order
	 * @param orderDetails JSONObject
	 * @return JSONObject
	 */
	@Transactional(rollbackFor=Exception.class)
	public JSONObject generateOrder(JSONObject orderDetails){
		_LOGGER.info("PartsStoreService-generateOrder-START");
		 JSONObject confirmationDetails = _partsStoreDAO.generateOrder(orderDetails);
		_LOGGER.info("PartsStoreService-generateOrder-END");
		return confirmationDetails;
	}
	
	/**
	 * This method fetches all available addresses for an user
	 * @param userID String
	 * @return JSONObject
	 */
	public JSONObject getAddressList(String userID) {
		_LOGGER.info("PartsStoreService-getAddressList-START");
		 JSONObject addressList = _partsStoreDAO.getAddressList(userID);
		_LOGGER.info("PartsStoreService-getAddressList-END");
		return addressList;
	}
	
	/**
	 * This method fetches all available shipping options
	 * @return JSONObject
	 */
	public JSONObject getShippingOptions() {
		_LOGGER.info("PartsStoreService-getShippingOptions-START");
		 JSONObject shippingOptions = _partsStoreDAO.getShippingOptions();
		_LOGGER.info("PartsStoreService-getShippingOptions-END");
		return shippingOptions;
	}
	
	/**
	 * This method fetches all the orders for a given user
	 * @param userID String
	 * @param orderID String
	 * @return JSONObject
	 */
	public JSONObject getMyOrders(String userID, String orderID){
		_LOGGER.info("PartsStoreService-getMyOrders-START");
		 JSONObject orderDetails = _partsStoreDAO.getMyOrders(userID, orderID);
		_LOGGER.info("PartsStoreService-getMyOrders-END");
		return orderDetails;
		
	}
	
	/**
	 * This method filters on search results
	 * @param key String
	 * @param value String
	 * @param low String
	 * @param high String
	 * @param query String
	 * @return result JSONObject
	 */
	public JSONObject filter(String key, String value, String low, String high, String query){
		_LOGGER.info("PartsStoreService-filter-START");
		String finalQuery = null;
		 if(key != null && value !=null){
			 finalQuery = "SELECT A.* FROM ("+
					 	  query+
					 	  ") A, PROPERTIES B WHERE A.PRODUCT_DETAILS_ID = B.PRODUCT_DETAILS_ID AND LOWER(B.KEY)='"+key.toLowerCase()+"' AND LOWER(B.VALUE) = '"+value.toLowerCase()+"'";
		 }else {
			 finalQuery = query.replace("ORDER BY PRODUCT_CATEGORY", " AND OUR_PRICE >= "+low+" AND OUR_PRICE <="+high+" ORDER BY PRODUCT_CATEGORY");
		 }
		 _LOGGER.info("FILTER QUERY::"+finalQuery);
		 JSONObject result = _partsStoreDAO.search(finalQuery);
		 result.accumulate("classcategory", "N/A");
		 result.accumulate("filter", "N/A");
		 	JSONObject priceRange = new JSONObject();
			 priceRange.accumulate("LOW_END", "N/A");
			 priceRange.accumulate("HIGH_END", "N/A");
		result.accumulate("pricefilter", priceRange);	
		_LOGGER.info("PartsStoreService-filter-END");
		return result;
	}
	
	/**
	 * This method pulls car information from store
	 * @param instanceID String
	 * @param searchText String
	 * @param categoryName String
	 * @return result JSONObject
	 */
	public JSONObject search(String instanceID, String searchText, String categoryName){
		_LOGGER.info("PartsStoreService-search-START-----------------------------------------------------"+searchText);
		JSONObject queryParams = null;
		 JSONObject result = null;
		try {
		queryParams = formQuery(instanceID,searchText,categoryName);
		}catch(Exception e){
			 result = new JSONObject();
			 
			result.put("status", "success");
			result.put("message", "N/A");
			result.put("data", new ArrayList<JSONObject>());
			 
			 result.accumulate("classcategory", "N/A");
			 result.accumulate("filter", "N/A");
			 result.accumulate("searchtext", searchText);
				 JSONObject priceRange = new JSONObject();
				 priceRange.accumulate("LOW_END", "N/A");
				 priceRange.accumulate("HIGH_END", "N/A");
			 result.accumulate("pricefilter", priceRange);	
			 return result;
		}
		
		
		String query = queryParams.getString("query");
		 result = _partsStoreDAO.search(query);
		 List<JSONObject> leftFilter = getUniqueClassCategory(result);
		 _LOGGER.info("LEFT FILTER::"+leftFilter +" "+leftFilter.size());
		 
		 if(leftFilter.size() == 0) {
			 result.accumulate("classcategory", "N/A");
			 result.accumulate("filter", "N/A");
			 result.accumulate("searchtext", searchText);
				 JSONObject priceRange = new JSONObject();
				 priceRange.accumulate("LOW_END", "N/A");
				 priceRange.accumulate("HIGH_END", "N/A");
			 result.accumulate("pricefilter", priceRange);	
			 return result;
		 }
		 result.accumulate("query", query);
		 
		 if(leftFilter.size() > 1) {
			 result.accumulate("classcategory", leftFilter);
			 result.accumulate("filter", "N/A");
		 } else {
			 result.accumulate("classcategory", "N/A");
			 List<JSONObject> filterData = getFilterData(result);
			 result.accumulate("filter", filterData);
		 }
		 JSONObject js = getPriceFilter(result);
		 result.accumulate("pricefilter", js);
		 String searchKeyword = categoryName;
		 if(categoryName==null){
			//searchKeyword = searchText;
			 searchKeyword = getSearchText(queryParams,result,searchText);
		 }
		 result.accumulate("searchtext", searchKeyword);
		_LOGGER.info("PartsStoreService-search-END");
		return result;
	}
	
	/**
	 * This method fetches filter data
	 * @param searchResults JSONObject
	 * @return priceRange JSONObject
	 */
	private JSONObject getPriceFilter(JSONObject searchResults){
		double low = 99999.99;
		double high = 0.0;
		String price = null;
		double bottlePrice = 0.0d;
		JSONArray data = searchResults.getJSONArray("data");
		for (int i = 0; i < data.size(); i++) {
			JSONObject js = (JSONObject) data.get(i);
			price = js.getString("OUR_PRICE");
			_LOGGER.info(price+" ID: "+js.getString("PRODUCT_DETAILS_ID"));
			bottlePrice = Double
					.parseDouble(price.substring(0, price.length()));
			if (bottlePrice < low) {
				low = bottlePrice;
			}
			if (bottlePrice > high) {
				high = bottlePrice;
			}
		}
		JSONObject priceRange = new JSONObject();
		if (data.size() > 0) {
			priceRange.accumulate("LOW_END", low);
			priceRange.accumulate("HIGH_END", high);
		} else {
			priceRange.accumulate("LOW_END", "N/A");
			priceRange.accumulate("HIGH_END", "N/A");
		}
		return priceRange;
	}
	
	
	/**
	 * This method fetches filter data
	 * @param result JSONObject
	 * @return data List<JSONObject>
	 */
	private List<JSONObject> getFilterData(JSONObject result){
		JSONArray searchResults = result.getJSONArray("data");
		StringBuffer stbuff = new StringBuffer("(");
			for(int i=0;i<searchResults.size();i++){
				JSONObject js = (JSONObject)searchResults.get(i);
				stbuff.append(js.getString("PRODUCT_DETAILS_ID")).append(",");
			}
		String finalProducts = stbuff.substring(0, stbuff.length()-1)+")";
		JSONObject propertiesData = _partsStoreDAO.getAllProperties(finalProducts);
		List<JSONObject> filterData = getUniqueClassCategory(propertiesData);
		return filterData;
	}
	
	/**
	 * This method displays the product menu
	 * @param result JSONObject
	 * @return data List<JSONObject>
	 */
	private List<JSONObject> getUniqueClassCategory(JSONObject result){
		List<JSONObject> data = new ArrayList<JSONObject>();
		TreeMap<String,String> hm = new TreeMap<String,String>();
		JSONArray arr = result.getJSONArray("data");
			 for(int i=0;i<arr.size();i++){
				JSONObject js = (JSONObject)arr.get(i);
				if(hm.containsKey(js.getString("PRODUCT_CLASS"))) {
					// ADD TO VALUES	
					String str = hm.get(js.getString("PRODUCT_CLASS"));
					hm.put(js.getString("PRODUCT_CLASS"), str+"@"+js.getString("PRODUCT_CATEGORY"));
				} else {
					// ADD A NEW ENTRY	
					hm.put(js.getString("PRODUCT_CLASS"),js.getString("PRODUCT_CATEGORY"));
				}
			 }
			 
			 // LOOP THROUGH THE HASHMAP
		     Iterator it = hm.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			        JSONObject js = new JSONObject();
			        js.accumulate("FIRST_LEVEL", pair.getKey());
			        List<JSONObject> list = new ArrayList<JSONObject>();
			        StringTokenizer st = new StringTokenizer(pair.getValue().toString(),"@");
			        
			        JSONObject map = new JSONObject();
			        String str = null;
			        while(st.hasMoreTokens()){
				        	str = st.nextToken();
			            	if(map.containsKey(str)){
			            		int count = Integer.parseInt(map.getString(str));
			            		map.put(str, String.valueOf(++count));
			            	}else {
			            		map.put(str, "1");
			            	}
			 	        }
			        
			        Iterator<?> keys = map.keys();

			        while( keys.hasNext() ) {
			            String key = (String)keys.next();
			            JSONObject val = new JSONObject();
			        	val.accumulate("VALUE", key);
			        	val.accumulate("COUNT", map.get(key));
			        	list.add(val);
			        }
			        
				    js.accumulate("SECOND_LEVEL", list);
				    data.add(js);
			    }
		return data;
	}
	
	
	private String getSearchText(JSONObject queryParams,JSONObject result, String originalText){
		_LOGGER.info("PartsStoreService-getSearchText-START");
				
		String searchText = null;
		HashMap<String,String> hm = new HashMap<String,String>();
		JSONArray queryWords = queryParams.getJSONArray("querywords");
		JSONArray arr = result.getJSONArray("data");
		String category = null;
				for(int i=0; i<queryWords.size();i++) {
				String queryword = queryWords.getString(i);
	   inner:  for(int j=0; j<arr.size();j++) {
					JSONObject json = (JSONObject)arr.get(j);
					category = json.getString("PRODUCT_CATEGORY");
					if(category.toLowerCase().contains(queryword.toLowerCase())){
						if(hm.containsKey(queryword)) {
							int num = Integer.parseInt(hm.get(queryword));
							hm.put(queryword, String.valueOf(num+1));
						} else {
							hm.put(queryword, "1");
						}
					} else {
						continue inner;
					}
				}
				}
		// PARSE THE MAP TO FIND THE SCORE
		int lowIndex = 0;
		int highIndex = 0;
		int tempIndex = 0;
		Iterator it = hm.entrySet().iterator();
		int count = 0;
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        _LOGGER.info("KEY::"+pair.getKey()+"   VALUE::"+pair.getValue());
	        tempIndex = originalText.indexOf(pair.getKey().toString());
	        if(count == 0) {
	        	lowIndex = tempIndex;
	        	highIndex = tempIndex;
	        	_LOGGER.info("-------aaaa  "+count+"-----------::"+lowIndex+"            -------------------highIndex::"+highIndex);
	        	count = count +1;
	        	continue;
	        }
	        
	        if(tempIndex > highIndex ) {
	        	highIndex = tempIndex;
	        	_LOGGER.info("-----bbbbb   "+count+"-------------::"+lowIndex+"            -------------------highIndex::"+highIndex);
	        	count = count +1;
	        	continue;
	        }
	        if(tempIndex < lowIndex) {
	        	lowIndex = tempIndex;
	        	_LOGGER.info("----cccccc   "+count+"--------------::"+lowIndex+"            -------------------highIndex::"+highIndex);
	        	count = count +1;
	        	continue;
	        }
	       
	    }
	    
	    highIndex = highIndex+1;
	    
	    _LOGGER.info("------------------::"+originalText.substring(lowIndex, highIndex+1));
	    try {
	    while(originalText.charAt(highIndex) != ' '){
	    	highIndex++;
	    }
	    }catch(StringIndexOutOfBoundsException e) {
	    	// END OF LINE RECEIVED
	    }
	    _LOGGER.info("------------------::"+originalText.substring(lowIndex, highIndex));
	    searchText = originalText.substring(lowIndex, highIndex);
	    _LOGGER.info("PartsStoreService-getSearchText-START");
		return searchText;
	}
	
	/**
	 * This helper method forms the final query
	 * @param instanceID String
	 * @param searchText String
	 * @param categoryName String
	 * @return query String
	 */
	private JSONObject formQuery(String instanceID, String searchText, String categoryName){
		JSONObject result = new JSONObject();
		List<String> queryWords = new ArrayList<String>();
		String query = _queries.getProperty("search.for.product");
		String finalQuery = null;
		if(searchText != null){
			// PARSE THE STRING AND SEARCH IN THE CATEGORY COLUMN - STARTS
			String finalString = searchText.replaceAll("  ", " ");
			StringTokenizer stkn = new StringTokenizer(finalString," ");
			String token = null;
			StringBuffer partQuery = new StringBuffer();
			while(stkn.hasMoreTokens()) {
				token = stkn.nextToken();
				if(token.length() > 2){
					// USE STOP WORDS - STARTS
					if ( ArrayUtils.contains( arr, token.toLowerCase() ) ) {
						_LOGGER.info("STOPWORD::-----------------------------------------"+token);
					    continue;
					} else {
						if(token.endsWith("s")) {
							token = token.substring(0, token.length()-1);
						}
						queryWords.add(token);
						partQuery.append(" LOWER(PRODUCT_CATEGORY) LIKE '%"+token.trim().toLowerCase()+"%' OR");
					}
					// USE STOP WORDS - ENDSS
				}else {
					continue;
				}
			}
			_LOGGER.info("PART QUERY:::::"+partQuery);
		finalQuery = "("+ partQuery.toString().substring(0, partQuery.length()-2)+")";
		// PARSE THE STRING AND SEARCH IN THE CATEGORY COLUMN - ENDS
		}
		
		if(categoryName != null && searchText == null) {
			if(instanceID != null){
				query = query + " WHERE LOWER(PRODUCT_CATEGORY) = '"+categoryName.toLowerCase()+"' AND INSTANCE_ID = "+instanceID;
			} else {
				query = query + " WHERE LOWER(PRODUCT_CATEGORY) = '"+categoryName.toLowerCase()+"'";
			}
		}
		
		if(searchText != null && categoryName == null) {
			if(instanceID != null){
				query = query + " WHERE "+finalQuery+" AND INSTANCE_ID = "+instanceID;
			} else {
				query = query + " WHERE "+finalQuery;
			}
		}
		
		if(searchText != null && categoryName != null) {
			if(instanceID != null){
				query = query + " WHERE "+finalQuery+" AND LOWER(PRODUCT_CATEGORY) = '"+categoryName.trim().toLowerCase()+"' AND INSTANCE_ID = "+instanceID;
			} else {
				query = query + " WHERE "+finalQuery+" AND LOWER(PRODUCT_CATEGORY) = '"+categoryName.trim().toLowerCase()+"'";
			}
		}
		 query = query + " ORDER BY PRODUCT_CATEGORY";
		 _LOGGER.info("QUERY::"+query);
		 result.accumulate("query", query);
		 result.accumulate("querywords", queryWords);
		return result;
	}
	
}
 