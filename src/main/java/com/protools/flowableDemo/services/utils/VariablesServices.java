package com.protools.flowableDemo.services.utils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@Service
public class VariablesServices {
    private static Logger logger =LogManager.getLogger(VariablesServices.class);
    private VariablesServices() {
    }
    public static Collection<String> stringToCollectionString(String string){
        logger.info("\t >> Converting String to Collection ... <<  ");
        Collection<String > response = new HashSet<String>() {
        };
        Gson gson = new Gson();
        try {
            String[] map = gson.fromJson(string,String[].class);
            response.addAll(Arrays.asList(map));
        } catch (Exception e) {
            logger.error("\t >> ERROR : String to Collection conversion failed");
        }
        return response;
    }

}
