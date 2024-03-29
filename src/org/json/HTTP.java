/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

import java.util.Iterator;

public class HTTP
{

    public static final String CRLF = "\r\n";

    public static JSONObject toJSONObject(String string) throws JSONException
    {
        JSONObject jo = new JSONObject();
        HTTPTokener x = new HTTPTokener(string);
        String token = x.nextToken();
        if (token.toUpperCase().startsWith("HTTP"))
        {
            jo.put("HTTP-Version", token);
            jo.put("Status-Code", x.nextToken());
            jo.put("Reason-Phrase", x.nextTo('\u0000'));
            x.next();
        } else
        {
            jo.put("Method", token);
            jo.put("Request-URI", x.nextToken());
            jo.put("HTTP-Version", x.nextToken());
        }
        while (x.more())
        {
            String name = x.nextTo(':');
            x.next(':');
            jo.put(name, x.nextTo('\u0000'));
            x.next();
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException
    {
        Iterator<String> keys = jo.keys();
        StringBuilder sb = new StringBuilder();
        if (jo.has("Status-Code") && jo.has("Reason-Phrase"))
        {
            sb.append(jo.getString("HTTP-Version"));
            sb.append(' ');
            sb.append(jo.getString("Status-Code"));
            sb.append(' ');
            sb.append(jo.getString("Reason-Phrase"));
        } else
        {
            if (!jo.has("Method")) throw new JSONException("Not enough material for an HTTP header.");
            if (!jo.has("Request-URI")) throw new JSONException("Not enough material for an HTTP header.");
            sb.append(jo.getString("Method"));
            sb.append(' ');
            sb.append('\"');
            sb.append(jo.getString("Request-URI"));
            sb.append('\"');
            sb.append(' ');
            sb.append(jo.getString("HTTP-Version"));
        }
        sb.append(CRLF);
        do
        {
            if (!keys.hasNext())
            {
                sb.append(CRLF);
                return sb.toString();
            }
            String string = keys.next();
            if ("HTTP-Version".equals(string) || "Status-Code".equals(string) || "Reason-Phrase".equals(string) || "Method".equals(string) || "Request-URI".equals(string) || jo.isNull(string))
                continue;
            sb.append(string);
            sb.append(": ");
            sb.append(jo.getString(string));
            sb.append(CRLF);
        } while (true);
    }
}

