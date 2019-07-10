/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

public class XML
{

    public static final Character AMP = Character.valueOf('&');
    public static final Character APOS = Character.valueOf('\'');
    public static final Character BANG = Character.valueOf('!');
    public static final Character EQ = Character.valueOf('=');
    public static final Character GT = Character.valueOf('>');
    public static final Character LT = Character.valueOf('<');
    public static final Character QUEST = Character.valueOf('?');
    public static final Character QUOT = Character.valueOf('\"');
    public static final Character SLASH = Character.valueOf('/');

    /*
     * Unable to fully structure code
     */
    public static String escape(String string)
    {
        sb = new StringBuilder(string.length());
        i = 0;
        length = string.length();
        while (i < length)
        {
            c = string.charAt(i);
            switch (c)
            {
                case '&':
                {
                    sb.append("&amp;");
                    **break;
                }
                case '<':
                {
                    sb.append("&lt;");
                    **break;
                }
                case '>':
                {
                    sb.append("&gt;");
                    **break;
                }
                case '\"':
                {
                    sb.append("&quot;");
                    **break;
                }
                case '\'':
                {
                    sb.append("&apos;");
                    **break;
                }
            }
            sb.append(c);
            lbl29:
            // 6 sources:
            ++i;
        }
        return sb.toString();
    }

    public static void noSpace(String string) throws JSONException
    {
        int length = string.length();
        if (length == 0)
        {
            throw new JSONException("Empty string.");
        }
        int i = 0;
        while (i < length)
        {
            if (Character.isWhitespace(string.charAt(i)))
            {
                throw new JSONException("'" + string + "' contains a space character.");
            }
            ++i;
        }
    }

    private static boolean parse(XMLTokener x, JSONObject context, String name) throws JSONException
    {
        String string;
        JSONObject jsonobject = null;
        Object token = x.nextToken();
        if (token != BANG)
        {
            if (token == QUEST)
            {
                x.skipPast("?>");
                return false;
            }
            if (token == SLASH)
            {
                token = x.nextToken();
                if (name == null)
                {
                    throw x.syntaxError("Mismatched close tag " + token);
                }
                if (!token.equals(name))
                {
                    throw x.syntaxError("Mismatched " + name + " and " + token);
                }
                if (x.nextToken() == GT) return true;
                throw x.syntaxError("Misshaped close tag");
            }
            if (token instanceof Character)
            {
                throw x.syntaxError("Misshaped tag");
            }
        } else
        {
            char c = x.next();
            if (c == '-')
            {
                if (x.next() == '-')
                {
                    x.skipPast("-->");
                    return false;
                }
                x.back();
            } else if (c == '[')
            {
                token = x.nextToken();
                if (!"CDATA".equals(token)) throw x.syntaxError("Expected 'CDATA['");
                if (x.next() != '[') throw x.syntaxError("Expected 'CDATA['");
                String string2 = x.nextCDATA();
                if (string2.length() <= 0) return false;
                context.accumulate("content", string2);
                return false;
            }
            int i = 1;
            do
            {
                if ((token = x.nextMeta()) == null)
                {
                    throw x.syntaxError("Missing '>' after '<!'.");
                }
                if (token == LT)
                {
                    ++i;
                    continue;
                }
                if (token != GT) continue;
                --i;
            } while (i > 0);
            return false;
        }
        String tagName = (String) token;
        token = null;
        jsonobject = new JSONObject();
        do
        {
            if (token == null)
            {
                token = x.nextToken();
            }
            if (!(token instanceof String)) break;
            string = (String) token;
            token = x.nextToken();
            if (token == EQ)
            {
                token = x.nextToken();
                if (!(token instanceof String))
                {
                    throw x.syntaxError("Missing value");
                }
                jsonobject.accumulate(string, JSONObject.stringToValue((String) token));
                token = null;
                continue;
            }
            jsonobject.accumulate(string, "");
        } while (true);
        if (token == SLASH)
        {
            if (x.nextToken() != GT)
            {
                throw x.syntaxError("Misshaped tag");
            }
            if (jsonobject.length() > 0)
            {
                context.accumulate(tagName, jsonobject);
                return false;
            }
            context.accumulate(tagName, "");
            return false;
        }
        if (token != GT) throw x.syntaxError("Misshaped tag");
        do
        {
            if ((token = x.nextContent()) == null)
            {
                if (tagName == null) return false;
                throw x.syntaxError("Unclosed tag " + tagName);
            }
            if (token instanceof String)
            {
                string = (String) token;
                if (string.length() <= 0) continue;
                jsonobject.accumulate("content", JSONObject.stringToValue(string));
                continue;
            }
            if (token == LT && XML.parse(x, jsonobject, tagName)) break;
        } while (true);
        if (jsonobject.length() == 0)
        {
            context.accumulate(tagName, "");
            return false;
        }
        if (jsonobject.length() == 1 && jsonobject.opt("content") != null)
        {
            context.accumulate(tagName, jsonobject.opt("content"));
            return false;
        }
        context.accumulate(tagName, jsonobject);
        return false;
    }

    public static Object stringToValue(String string)
    {
        return JSONObject.stringToValue(string);
    }

    public static JSONObject toJSONObject(String string) throws JSONException
    {
        JSONObject jo = new JSONObject();
        XMLTokener x = new XMLTokener(string);
        while (x.more())
        {
            if (!x.skipPast("<")) return jo;
            XML.parse(x, jo, null);
        }
        return jo;
    }

    public static String toString(Object object) throws JSONException
    {
        return XML.toString(object, null);
    }

    /*
     * Unable to fully structure code
     */
    public static String toString(Object object, String tagName) throws JSONException
    {
        block23:
        {
            block21:
            {
                block22:
                {
                    sb = new StringBuilder();
                    if (object instanceof JSONObject) break block21;
                    if (object == null) break block22;
                    if (object.getClass().isArray())
                    {
                        object = new JSONArray(object);
                    }
                    if (object instanceof JSONArray) break block23;
                }
                v0 = string = object == null ? "null" : XML.escape(object.toString());
                if (tagName == null)
                {
                    v1 = "\"" + string + "\"";
                    return v1;
                }
                if (string.length() == 0)
                {
                    v1 = "<" + tagName + "/>";
                    return v1;
                }
                v1 = "<" + tagName + ">" + string + "</" + tagName + ">";
                return v1;
            }
            if (tagName != null)
            {
                sb.append('<');
                sb.append(tagName);
                sb.append('>');
            }
            jo = (JSONObject) object;
            keys = jo.keys();
            block0:
            do
            {
                if (!keys.hasNext())
                {
                    if (tagName == null) return sb.toString();
                    sb.append("</");
                    sb.append(tagName);
                    sb.append('>');
                    return sb.toString();
                }
                key = keys.next();
                value = jo.opt(key);
                if (value == null)
                {
                    value = "";
                } else if (value.getClass().isArray())
                {
                    value = new JSONArray(value);
                }
                v2 = string = value instanceof String != false ? (String) value : null;
                if ("content".equals(key))
                {
                    if (value instanceof JSONArray)
                    {
                        ja = (JSONArray) value;
                        i = 0;
                        var10_14 = ja.iterator();
                        do
                        {
                            if (!var10_14.hasNext()) continue block0;
                            val = var10_14.next();
                            if (i > 0)
                            {
                                sb.append('\n');
                            }
                            sb.append(XML.escape(val.toString()));
                            ++i;
                        } while (true);
                    }
                    sb.append(XML.escape(value.toString()));
                    continue;
                }
                if (!(value instanceof JSONArray))
                {
                    if ("".equals(value))
                    {
                        sb.append('<');
                        sb.append(key);
                        sb.append("/>");
                        continue;
                    }
                    sb.append(XML.toString(value, key));
                    continue;
                }
                ja = (JSONArray) value;
                i = ja.iterator();
                do
                {
                    if (!i.hasNext()) **break;
                    val = i.next();
                    if (val instanceof JSONArray)
                    {
                        sb.append('<');
                        sb.append(key);
                        sb.append('>');
                        sb.append(XML.toString(val));
                        sb.append("</");
                        sb.append(key);
                        sb.append('>');
                        continue;
                    }
                    sb.append(XML.toString(val, key));
                } while (true);
                break;
            } while (true);
        }
        ja = (JSONArray) object;
        i = ja.iterator();
        while (i.hasNext() != false)
        {
            val = i.next();
            sb.append(XML.toString(val, tagName == null ? "array" : tagName));
        }
        return sb.toString();
    }
}

