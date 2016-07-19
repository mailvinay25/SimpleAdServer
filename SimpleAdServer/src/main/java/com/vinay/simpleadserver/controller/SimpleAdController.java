/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinay.simpleadserver.controller;

import com.vinay.simpleadserver.cache.AdServerInMemoryCache;
import com.vinay.simpleadserver.model.Advertisement;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SimpleAdController is the main controller for Simple Ad server
 *
 * @author vchaitankar
 */
@Controller
@RequestMapping("/")
public class SimpleAdController {

    @Autowired
    private ServletContext servletContext;

    private int partnerId = 0;

    /**
     * @return This method returns the home page for the application
     */
    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    /**
     * @param model Map to hold information about model
     * @return This method returns the create ad campaign page for the
     * application
     */
    @RequestMapping("/createAd")
    public String createAdReqHandler(ModelMap model) {
        partnerId++;
        model.addAttribute("partnerId", partnerId);
        return "createAd";
    }

    /**
     * @return This method returns the retrieve ad campaign page for the
     * application
     */
    @RequestMapping(value = "/retrieveAd", method = RequestMethod.GET)
    public String retrieveAd() {
        return "retrieveAd";
    }

    /**
     * @return This method returns the create ad campaign success page for the
     * application
     */
    @RequestMapping(value = "/adCreateSuccess", method = RequestMethod.GET)
    public String adCreateSuccess() {
        return "adCreateSuccess";
    }

    /**
     * @return This method returns the error campaign page for the application
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error() {
        return "error";
    }

    /**
     * @param errorMsg Error message to be displayed in the error map
     * @param model Map to hold information about model
     * @return This method returns the error page for the application
     */
    @RequestMapping(value = "/error/{errorMsg}", method = RequestMethod.GET)
    public String errorMessage(@PathVariable("errorMsg") String errorMsg, ModelMap model) {
        model.addAttribute("errorMsg", errorMsg);
        return "error";
    }

    /**
     *
     * @param ad Takes Ad campaign JSON as POST data to the method
     * @return This method returns success/failure json for creating new ad
     * campaign
     */
    @RequestMapping(value = "/ad", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> createAd(@RequestBody Advertisement ad) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        String resultJson;
        if (ad != null) {
            AdServerInMemoryCache<String, String> cache = (AdServerInMemoryCache<String, String>) servletContext.getAttribute("cache");
            String partnerIdStr = String.valueOf(ad.getPartner_id());
            String value = cache.get(partnerIdStr);
            if (value == null) {
                cache.put(partnerIdStr, ad.getAd_content(), ad.getDuration());
                resultJson = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
                return new ResponseEntity<>(resultJson, responseHeaders, HttpStatus.CREATED);

            } else {
                resultJson = "{\"message\":\"Active ad campaigns already exist for the specified partner, Only one active campaign can exist for a given partner\"}";
            }
        } else {
            resultJson = "{\"message\":\"Invalid ad campaigns request\"}";
        }
        return new ResponseEntity<>(resultJson, responseHeaders, HttpStatus.CREATED);
    }

    /**
     * @param id Partner Id for which the ad campaign has to be retrieved
     * @return Returns ad campaign in JSON format for the associated Partner Id.
     */
    @RequestMapping(value = "/ad/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getAd(@PathVariable("id") long id) {
        AdServerInMemoryCache<String, String> cache = (AdServerInMemoryCache<String, String>) servletContext.getAttribute("cache");
        String adContent = cache.get(String.valueOf(id));
        if (adContent != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("partner_id", id);
                obj.put("ad_content", adContent);
                obj.put("status", "active");
                return obj.toJSONString();
            } catch (Exception e) {
                return "{\"message\":\"Exception while processing request, Contact Administrator\"}";
            }
        } else {
            return "{\"message\":\"no active ad campaigns exist for the specified partner\"}";
        }
    }

    /**
     *
     * @return This method returns list of all the active ad campaigns as JSON
     */
    @RequestMapping(value = "/getAllAds", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getAllAds() {
        AdServerInMemoryCache<String, String> cache = (AdServerInMemoryCache<String, String>) servletContext.getAttribute("cache");
        Map<String, String> map = cache.getAll();
        Set keys = map.keySet();
        Iterator it = keys.iterator();
        JSONArray jsonArray = new JSONArray();
        String key;
        String value;
        JSONObject obj;
        while (it.hasNext()) {
            key = (String) it.next();
            value = map.get(key);
            obj = new JSONObject();
            obj.put("partner_id", key);
            obj.put("ad_content", value);
            obj.put("status", "active");
            jsonArray.add(obj);
        }
        return jsonArray.toJSONString();
    }
}
