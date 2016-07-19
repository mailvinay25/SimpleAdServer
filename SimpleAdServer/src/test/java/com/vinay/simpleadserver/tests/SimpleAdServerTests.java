/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinay.simpleadserver.tests;

import com.google.gson.Gson;
import com.vinay.simpleadserver.cache.AdServerInMemoryCache;
import com.vinay.simpleadserver.config.AppConfig;
import com.vinay.simpleadserver.config.WebMvcConfig;
import com.vinay.simpleadserver.model.Advertisement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author vchaitankar SimpleAdServerTests is the main test class for simple ad
 * server. It contains all the test created for the simple ad server web
 * application
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {AppConfig.class, WebMvcConfig.class}, loader = AnnotationConfigWebContextLoader.class)

public class SimpleAdServerTests {

    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        // Setup Spring test in web mode
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        AdServerInMemoryCache<String, String> cache = new AdServerInMemoryCache<>(600, 1000);
        applicationContext.getServletContext().setAttribute("cache", cache);
    }

    /**
     * testCreateAd test is used to test if a ad campaign can be created.
     *
     * @throws Exception
     */
    @Test
    public void testCreateAd() throws Exception {
        Advertisement ad = new Advertisement();
        ad.setPartner_id(1);
        ad.setDuration(120);
        ad.setAd_content("This is a test ad");
        Gson gson = new Gson();
        String json = gson.toJson(ad);
        MvcResult mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        String testString = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);
    }

    /**
     * testRetrieveAd test is used to test retrieval of an already created ad
     * campaign
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveAd() throws Exception {
        testCreateAd();
        MvcResult result = this.mockMvc.perform(get("/ad/{partner_id}", 1)).andExpect(status().isOk()).andReturn();
        String resultStr = result.getResponse().getContentAsString();
        org.json.JSONObject obj1 = new org.json.JSONObject(resultStr);
        String partner_id = obj1.get("partner_id").toString();
        String ad_content = obj1.get("ad_content").toString();
        assertEquals(partner_id, "1");
        assertEquals(ad_content, "This is a test ad");
    }

    /**
     * testAdExpiry test is used to test the expiration of an ad campaign after
     * it has reached its duration.
     *
     * @throws Exception
     */
    @Test
    public void testAdExpiry() throws Exception {
        Advertisement ad = new Advertisement();
        ad.setPartner_id(1);
        ad.setDuration(4);
        ad.setAd_content("This is a test ad");
        Gson gson = new Gson();
        String json = gson.toJson(ad);
        MvcResult mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        String testString = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);
        Thread.sleep(5000);
        mvcResult = this.mockMvc.perform(get("/ad/{partner_id}", 1)).andExpect(status().isOk()).andReturn();
        result = mvcResult.getResponse().getContentAsString();
        testString = "{\"message\":\"no active ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);
    }

    /**
     * testCreateForExistingActiveAdByPartner test is used to test the
     * condition, 'Only one active campaign can exist for a given partner'
     *
     * @throws Exception
     */
    @Test
    public void testCreateForExistingActiveAdByPartner() throws Exception {
        testCreateAd();
        Advertisement ad = new Advertisement();
        ad.setPartner_id(1);
        ad.setDuration(120);
        ad.setAd_content("This is a test content for ad by partner with existing active ad campaign");
        Gson gson = new Gson();
        String json = gson.toJson(ad);
        MvcResult mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        String testString = "{\"message\":\"Active ad campaigns already exist for the specified partner, Only one active campaign can exist for a given partner\"}";
        assertEquals(result, testString);
    }

    /**
     * testRetrieveAllAds test is used to test if all active ad campaigns can be
     * retrieved by the application.
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveAllAds() throws Exception {
        testCreateMultipleAds();
        MvcResult mvcResult = this.mockMvc.perform(get("/getAllAds")).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        org.json.JSONArray jsonArray = new org.json.JSONArray(result);
        int size = jsonArray.length();
        assertEquals(size, 5);
    }

    /**
     * testRetrieveAllAdsWithExpiry test is used to test if all active ad
     * campaigns can be retrieved by the application while keeping track of
     * their expiration.
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveAllAdsWithExpiry() throws Exception {
        testCreateMultipleAds();
        MvcResult mvcResult = this.mockMvc.perform(get("/getAllAds")).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        org.json.JSONArray jsonArray = new org.json.JSONArray(result);
        int size = jsonArray.length();
        assertEquals(size, 5);
        Thread.sleep(21000);
        mvcResult = this.mockMvc.perform(get("/getAllAds")).andExpect(status().isOk()).andReturn();
        result = mvcResult.getResponse().getContentAsString();
        jsonArray = new org.json.JSONArray(result);
        size = jsonArray.length();
        assertEquals(size, 4);
        Thread.sleep(21000);
        mvcResult = this.mockMvc.perform(get("/getAllAds")).andExpect(status().isOk()).andReturn();
        result = mvcResult.getResponse().getContentAsString();
        jsonArray = new org.json.JSONArray(result);
        size = jsonArray.length();
        assertEquals(size, 3);
    }

    /**
     * testCreateMultipleAds method is used to create multiple ad campaigns for
     * different partners.
     *
     * @throws Exception
     */
    public void testCreateMultipleAds() throws Exception {
        Advertisement ad = new Advertisement();
        ad.setPartner_id(1);
        ad.setDuration(20);
        ad.setAd_content("This is a test ad for partner id 1");
        Gson gson = new Gson();
        String json = gson.toJson(ad);
        MvcResult mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        String testString = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);

        ad = new Advertisement();
        ad.setPartner_id(2);
        ad.setDuration(40);
        ad.setAd_content("This is a test ad for partner id 2");
        gson = new Gson();
        json = gson.toJson(ad);
        mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        result = mvcResult.getResponse().getContentAsString();
        testString = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);

        ad = new Advertisement();
        ad.setPartner_id(3);
        ad.setDuration(60);
        ad.setAd_content("This is a test ad for partner id 3");
        gson = new Gson();
        json = gson.toJson(ad);
        mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        result = mvcResult.getResponse().getContentAsString();
        testString = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);

        ad = new Advertisement();
        ad.setPartner_id(4);
        ad.setDuration(80);
        ad.setAd_content("This is a test ad for partner id 4");
        gson = new Gson();
        json = gson.toJson(ad);
        mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        result = mvcResult.getResponse().getContentAsString();
        testString = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);

        ad = new Advertisement();
        ad.setPartner_id(5);
        ad.setDuration(100);
        ad.setAd_content("This is a test ad for partner id 5");
        gson = new Gson();
        json = gson.toJson(ad);
        mvcResult = this.mockMvc.perform(post("/ad").content(json).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        result = mvcResult.getResponse().getContentAsString();
        testString = "{\"message\":\"Successfully created ad campaigns exist for the specified partner\"}";
        assertEquals(result, testString);
    }
}
