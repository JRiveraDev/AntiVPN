/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

public class CDL
{

    private static String getValue(JSONTokener x) throws JSONException
    {
        char c;
        while ((c = x.next()) == ' ' || c == '\t')
        {
        }
        switch (c)
        {
            case '\u0000':
            {
                return null;
            }
            case '\"':
            case '\'':
            {
                char q = c;
                StringBuffer sb = new StringBuffer();
                do
                {
                    if ((c = x.next()) == q)
                    {
                        return sb.toString();
                    }
                    if (c == '\u0000') throw x.syntaxError("Missing close quote '" + q + "'.");
                    if (c == '\n') throw x.syntaxError("Missing close quote '" + q + "'.");
                    if (c == '\r')
                    {
                        throw x.syntaxError("Missing close quote '" + q + "'.");
                    }
                    sb.append(c);
                } while (true);
            }
            case ',':
            {
                x.back();
                return "";
            }
        }
        x.back();
        return x.nextTo(',');
    }

    public static JSONArray rowToJSONArray(JSONTokener x) throws JSONException
    {
        JSONArray ja = new JSONArray();
        block0:
        do
        {
            String value = CDL.getValue(x);
            char c = x.next();
            if (value == null) return null;
            if (ja.length() == 0 && value.length() == 0 && c != ',')
            {
                return null;
            }
            ja.put(value);
            do
            {
                if (c == ',') continue block0;
                if (c != ' ')
                {
                    if (c == '\n') return ja;
                    if (c == '\r') return ja;
                    if (c != '\u0000') throw x.syntaxError("Bad character '" + c + "' (" + c + ").");
                    return ja;
                }
                c = x.next();
            } while (true);

        } while (true);
    }

    public static JSONObject rowToJSONObject(JSONArray names, JSONTokener x) throws JSONException
    {
        JSONArray ja = CDL.rowToJSONArray(x);
        if (ja == null) return null;
        JSONObject jSONObject = ja.toJSONObject(names);
        return jSONObject;
    }

    public static String rowToString(JSONArray ja)
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        do
        {
            Object object;
            if (i >= ja.length())
            {
                sb.append('\n');
                return sb.toString();
            }
            if (i > 0)
            {
                sb.append(',');
            }
            if ((object = ja.opt(i)) != null)
            {
                String string = object.toString();
                if (string.length() > 0 && (string.indexOf(44) >= 0 || string.indexOf(10) >= 0 || string.indexOf(13) >= 0 || string.indexOf(0) >= 0 || string.charAt(0) == '\"'))
                {
                    sb.append('\"');
                    int length = string.length();
                    for (int j = 0; j < length; ++j)
                    {
                        char c = string.charAt(j);
                        if (c < ' ' || c == '\"') continue;
                        sb.append(c);
                    }
                    sb.append('\"');
                } else
                {
                    sb.append(string);
                }
            }
            ++i;
        } while (true);
    }

    public static JSONArray toJSONArray(String string) throws JSONException
    {
        return CDL.toJSONArray(new JSONTokener(string));
    }

    public static JSONArray toJSONArray(JSONTokener x) throws JSONException
    {
        return CDL.toJSONArray(CDL.rowToJSONArray(x), x);
    }

    public static JSONArray toJSONArray(JSONArray names, String string) throws JSONException
    {
        return CDL.toJSONArray(names, new JSONTokener(string));
    }

    public static JSONArray toJSONArray(JSONArray names, JSONTokener x) throws JSONException
    {
        if (names == null) return null;
        if (names.length() == 0)
        {
            return null;
        }
        JSONArray ja = new JSONArray();
        do
        {
            JSONObject jo;
            if ((jo = CDL.rowToJSONObject(names, x)) == null)
            {
                if (ja.length() != 0) return ja;
                return null;
            }
            ja.put(jo);
        } while (true);
    }

    public static String toString(JSONArray ja) throws JSONException
    {
        JSONObject jo = ja.optJSONObject(0);
        if (jo == null) return null;
        JSONArray names = jo.names();
        if (names == null) return null;
        return CDL.rowToString(names) + CDL.toString(names, ja);
    }

    public static String toString(JSONArray names, JSONArray ja) throws JSONException
    {
        if (names == null) return null;
        if (names.length() == 0)
        {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < ja.length())
        {
            JSONObject jo = ja.optJSONObject(i);
            if (jo != null)
            {
                sb.append(CDL.rowToString(jo.toJSONArray(names)));
            }
            ++i;
        }
        return sb.toString();
    }
}

