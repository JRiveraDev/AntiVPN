/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

import java.util.Iterator;

public class JSONML
{

    private static Object parse(XMLTokener x, boolean arrayForm, JSONArray ja) throws JSONException
    {
        String closeTag = null;
        JSONArray newja = null;
        JSONObject newjo = null;
        String tagName = null;
        do
        {
            Object token;
            block27:
            {
                block33:
                {
                    block31:
                    {
                        block28:
                        {
                            block32:
                            {
                                block29:
                                {
                                    block30:
                                    {
                                        if (!x.more())
                                        {
                                            throw x.syntaxError("Bad XML");
                                        }
                                        token = x.nextContent();
                                        if (token != XML.LT) break block28;
                                        token = x.nextToken();
                                        if (!(token instanceof Character)) break block29;
                                        if (token == XML.SLASH)
                                        {
                                            token = x.nextToken();
                                            if (!(token instanceof String))
                                            {
                                                throw new JSONException("Expected a closing name instead of '" + token + "'.");
                                            }
                                            if (x.nextToken() == XML.GT) return token;
                                            throw x.syntaxError("Misshaped close tag");
                                        }
                                        if (token != XML.BANG) break block30;
                                        char c = x.next();
                                        if (c == '-')
                                        {
                                            if (x.next() == '-')
                                            {
                                                x.skipPast("-->");
                                                continue;
                                            }
                                            x.back();
                                            continue;
                                        }
                                        if (c == '[')
                                        {
                                            token = x.nextToken();
                                            if (!token.equals("CDATA")) throw x.syntaxError("Expected 'CDATA['");
                                            if (x.next() != '[') throw x.syntaxError("Expected 'CDATA['");
                                            if (ja == null) continue;
                                            ja.put(x.nextCDATA());
                                            continue;
                                        }
                                        break block31;
                                    }
                                    if (token != XML.QUEST) throw x.syntaxError("Misshaped tag");
                                    x.skipPast("?>");
                                    continue;
                                }
                                if (!(token instanceof String))
                                {
                                    throw x.syntaxError("Bad tagName '" + token + "'.");
                                }
                                tagName = (String) token;
                                newja = new JSONArray();
                                newjo = new JSONObject();
                                if (!arrayForm) break block32;
                                newja.put(tagName);
                                if (ja != null)
                                {
                                    ja.put(newja);
                                }
                                break block33;
                            }
                            newjo.put("tagName", tagName);
                            if (ja == null) break block33;
                            ja.put(newjo);
                            break block33;
                        }
                        if (ja == null) continue;
                        ja.put(token instanceof String ? JSONObject.stringToValue((String) token) : token);
                        continue;
                    }
                    int i = 1;
                    do
                    {
                        if ((token = x.nextMeta()) == null)
                        {
                            throw x.syntaxError("Missing '>' after '<!'.");
                        }
                        if (token == XML.LT)
                        {
                            ++i;
                            continue;
                        }
                        if (token != XML.GT) continue;
                        --i;
                    } while (i > 0);
                    continue;
                }
                token = null;
                do
                {
                    if (token == null)
                    {
                        token = x.nextToken();
                    }
                    if (token == null)
                    {
                        throw x.syntaxError("Misshaped tag");
                    }
                    if (!(token instanceof String))
                    {
                        if (arrayForm)
                        {
                            break;
                        }
                        break block27;
                    }
                    String attribute = (String) token;
                    if (!arrayForm)
                    {
                        if ("tagName".equals(attribute)) throw x.syntaxError("Reserved attribute.");
                        if ("childNode".equals(attribute))
                        {
                            throw x.syntaxError("Reserved attribute.");
                        }
                    }
                    if ((token = x.nextToken()) == XML.EQ)
                    {
                        token = x.nextToken();
                        if (!(token instanceof String))
                        {
                            throw x.syntaxError("Missing value");
                        }
                        newjo.accumulate(attribute, JSONObject.stringToValue((String) token));
                        token = null;
                        continue;
                    }
                    newjo.accumulate(attribute, "");
                } while (true);
                if (newjo.length() > 0)
                {
                    newja.put(newjo);
                }
            }
            if (token == XML.SLASH)
            {
                if (x.nextToken() != XML.GT)
                {
                    throw x.syntaxError("Misshaped tag");
                }
                if (ja != null) continue;
                if (!arrayForm) return newjo;
                return newja;
            }
            if (token != XML.GT)
            {
                throw x.syntaxError("Misshaped tag");
            }
            closeTag = (String) JSONML.parse(x, arrayForm, newja);
            if (closeTag == null) continue;
            if (!closeTag.equals(tagName))
            {
                throw x.syntaxError("Mismatched '" + tagName + "' and '" + closeTag + "'");
            }
            tagName = null;
            if (!arrayForm && newja.length() > 0)
            {
                newjo.put("childNodes", newja);
            }
            if (ja == null) break;
        } while (true);
        if (!arrayForm) return newjo;
        return newja;
    }

    public static JSONArray toJSONArray(String string) throws JSONException
    {
        return JSONML.toJSONArray(new XMLTokener(string));
    }

    public static JSONArray toJSONArray(XMLTokener x) throws JSONException
    {
        return (JSONArray) JSONML.parse(x, true, null);
    }

    public static JSONObject toJSONObject(XMLTokener x) throws JSONException
    {
        return (JSONObject) JSONML.parse(x, false, null);
    }

    public static JSONObject toJSONObject(String string) throws JSONException
    {
        return JSONML.toJSONObject(new XMLTokener(string));
    }

    public static String toString(JSONArray ja) throws JSONException
    {
        int length;
        int i;
        StringBuilder sb = new StringBuilder();
        String tagName = ja.getString(0);
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);
        Object object = ja.opt(1);
        if (object instanceof JSONObject)
        {
            i = 2;
            JSONObject jo = (JSONObject) object;
            Iterator<String> keys = jo.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                XML.noSpace(key);
                String value = jo.optString(key);
                if (value == null) continue;
                sb.append(' ');
                sb.append(XML.escape(key));
                sb.append('=');
                sb.append('\"');
                sb.append(XML.escape(value));
                sb.append('\"');
            }
        } else
        {
            i = 1;
        }
        if (i >= (length = ja.length()))
        {
            sb.append('/');
            sb.append('>');
            return sb.toString();
        }
        sb.append('>');
        do
        {
            object = ja.get(i);
            ++i;
            if (object == null) continue;
            if (object instanceof String)
            {
                sb.append(XML.escape(object.toString()));
                continue;
            }
            if (object instanceof JSONObject)
            {
                sb.append(JSONML.toString((JSONObject) object));
                continue;
            }
            if (object instanceof JSONArray)
            {
                sb.append(JSONML.toString((JSONArray) object));
                continue;
            }
            sb.append(object.toString());
        } while (i < length);
        sb.append('<');
        sb.append('/');
        sb.append(tagName);
        sb.append('>');
        return sb.toString();
    }

    public static String toString(JSONObject jo) throws JSONException
    {
        StringBuilder sb = new StringBuilder();
        String tagName = jo.optString("tagName");
        if (tagName == null)
        {
            return XML.escape(jo.toString());
        }
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);
        Iterator<String> keys = jo.keys();
        while (keys.hasNext())
        {
            String key = keys.next();
            if ("tagName".equals(key) || "childNodes".equals(key)) continue;
            XML.noSpace(key);
            String value = jo.optString(key);
            if (value == null) continue;
            sb.append(' ');
            sb.append(XML.escape(key));
            sb.append('=');
            sb.append('\"');
            sb.append(XML.escape(value));
            sb.append('\"');
        }
        JSONArray ja = jo.optJSONArray("childNodes");
        if (ja == null)
        {
            sb.append('/');
            sb.append('>');
            return sb.toString();
        }
        sb.append('>');
        int length = ja.length();
        int i = 0;
        do
        {
            if (i >= length)
            {
                sb.append('<');
                sb.append('/');
                sb.append(tagName);
                sb.append('>');
                return sb.toString();
            }
            Object object = ja.get(i);
            if (object != null)
            {
                if (object instanceof String)
                {
                    sb.append(XML.escape(object.toString()));
                } else if (object instanceof JSONObject)
                {
                    sb.append(JSONML.toString((JSONObject) object));
                } else if (object instanceof JSONArray)
                {
                    sb.append(JSONML.toString((JSONArray) object));
                } else
                {
                    sb.append(object.toString());
                }
            }
            ++i;
        } while (true);
    }
}

