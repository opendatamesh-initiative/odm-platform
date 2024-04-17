package org.opendatamesh.odm.cli.utils;

import java.util.Properties;

public final class InputManagerUtils {

    /**
     * Checks if the user has inserted the specific property value.
     * If it is not null than it is returned such value,
     * otherwise the properties are checked (that should be read from the properties file).
     * If even the property file hasn't a value for property, then the method returns null
     */
    public static String getPropertyValue(Properties properties, String propertyName, String overrideValue){
        if(overrideValue == null){
            if(properties == null || properties.getProperty(propertyName) == null)
                return null;
            else
                return properties.getProperty(propertyName);
        }
        else
            return overrideValue;
    }

    public static String getValueFromUser(String message, String defaultValue){
        boolean valueChecked = false;
        String input;
        do {
            System.out.print(message);
            input = System.console().readLine();
            if (input == null || input.isBlank()){
                if (defaultValue != null){
                    input = defaultValue;
                    valueChecked = true;
                }
                else
                    System.out.println("There isn't any default value. You must specify a value");
            }
            else
                valueChecked = true;
        } while (!valueChecked);
        return input;
    }

    public static String getValueFromUser(String message){
        return getValueFromUser(message, null);
    }
    
}
