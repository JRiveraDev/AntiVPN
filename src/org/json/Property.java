/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

public class Property
{

    public static JSONObject toJSONObject(Properties properties) throws JSONException
    {
        JSONObject jo = new JSONObject();
        if (properties == null) return jo;
        if (properties.isEmpty()) return jo;
        Enumeration<?> enumProperties = properties.propertyNames();
        while (enumProperties.hasMoreElements())
        {
            String name = (String) enumProperties.nextElement();
            jo.put(name, properties.getProperty(name));
        }
        return jo;
    }

    public static Properties toProperties(JSONObject jo) throws JSONException
    {
        Properties properties = new Properties();
        if (jo == null) return properties;
        Iterator<String> keys = jo.keys();
        while (keys.hasNext())
        {
            String name = keys.next();
            properties.put(name, jo.getString(name));
        }
        return properties;
    }
}

