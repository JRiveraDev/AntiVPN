package org.json;

public class Cookie
{

    public static String escape(String string)
    {
        String s = string.trim();
        int length = s.length();
        StringBuilder sb = new StringBuilder(length);
        int i = 0;
        while (i < length)
        {
            char c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';')
            {
                sb.append('%');
                sb.append(Character.forDigit((char) (c >>> 4 & 15), 16));
                sb.append(Character.forDigit((char) (c & 15), 16));
            } else
            {
                sb.append(c);
            }
            ++i;
        }
        return sb.toString();
    }

    public static JSONObject toJSONObject(String string) throws JSONException
    {
        JSONObject jo = new JSONObject();
        JSONTokener x = new JSONTokener(string);
        jo.put("name", x.nextTo('='));
        x.next('=');
        jo.put("value", x.nextTo(';'));
        x.next();
        while (x.more())
        {
            Object value;
            String name = Cookie.unescape(x.nextTo("=;"));
            if (x.next() != '=')
            {
                if (!name.equals("secure")) throw x.syntaxError("Missing '=' in cookie parameter.");
                value = Boolean.TRUE;
            } else
            {
                value = Cookie.unescape(x.nextTo(';'));
                x.next();
            }
            jo.put(name, value);
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Cookie.escape(jo.getString("name")));
        sb.append("=");
        sb.append(Cookie.escape(jo.getString("value")));
        if (jo.has("expires"))
        {
            sb.append(";expires=");
            sb.append(jo.getString("expires"));
        }
        if (jo.has("domain"))
        {
            sb.append(";domain=");
            sb.append(Cookie.escape(jo.getString("domain")));
        }
        if (jo.has("path"))
        {
            sb.append(";path=");
            sb.append(Cookie.escape(jo.getString("path")));
        }
        if (!jo.optBoolean("secure")) return sb.toString();
        sb.append(";secure");
        return sb.toString();
    }

    public static String unescape(String string)
    {
        int length = string.length();
        StringBuilder sb = new StringBuilder(length);
        int i = 0;
        while (i < length)
        {
            char c = string.charAt(i);
            if (c == '+')
            {
                c = ' ';
            } else if (c == '%' && i + 2 < length)
            {
                int d = JSONTokener.dehexchar(string.charAt(i + 1));
                int e = JSONTokener.dehexchar(string.charAt(i + 2));
                if (d >= 0 && e >= 0)
                {
                    c = (char) (d * 16 + e);
                    i += 2;
                }
            }
            sb.append(c);
            ++i;
        }
        return sb.toString();
    }
}

