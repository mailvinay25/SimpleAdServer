/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinay.simpleadserver.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Class to configure Spring framework to scan components in the package
 * "com.vinay.simpleadserver"
 *
 * @author vchaitankar
 */
@Configuration
@ComponentScan(basePackages = "com.vinay.simpleadserver")
public class AppConfig {
}
