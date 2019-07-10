/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JSONObject
{

    public static final Object NULL = new Null();
    private final Map<String, Object> map = new HashMap<String, Object>();

    public JSONObject()
    {
    }

    public JSONObject(JSONObject jo, String[] names)
    {
        this();
        int i = 0;
        while (i < names.length)
        {
            try
            {
                this.putOnce(names[i], jo.opt(names[i]));
            }
            catch (Exception ignore)
            {
                // empty catch block
            }
            ++i;
        }
    }

    public JSONObject(JSONTokener x) throws JSONException
    {
        this();
        if (x.nextClean() != '{')
        {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        block8:
        do
        {
            char c = x.nextClean();
            switch (c)
            {
                default:
                {
                    x.back();
                    String key = x.nextValue().toString();
                    c = x.nextClean();
                    if (c != ':')
                    {
                        throw x.syntaxError("Expected a ':' after a key");
                    }
                    this.putOnce(key, x.nextValue());
                    switch (x.nextClean())
                    {
                        case ',':
                        case ';':
                        {
                            if (x.nextClean() == '}')
                            {
                                return;
                            }
                            x.back();
                            continue block8;
                        }
                        case '}':
                        {
                            return;
                        }
                    }
                    throw x.syntaxError("Expected a ',' or '}'");
                }
                case '\u0000':
                {
                    throw x.syntaxError("A JSONObject text must end with '}'");
                }
                case '}':
            }
            break;
        } while (true);
    }

    public JSONObject(Map<?, ?> map)
    {
        if (map == null) return;
        Iterator<Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<?, ?> e = iterator.next();
            Object value = e.getValue();
            if (value == null) continue;
            this.map.put(String.valueOf(e.getKey()), JSONObject.wrap(value));
        }
    }

    public JSONObject(Object bean)
    {
        this();
        this.populateMap(bean);
    }

    public JSONObject(Object object, String[] names)
    {
        this();
        Class<?> c = object.getClass();
        int i = 0;
        while (i < names.length)
        {
            String name = names[i];
            try
            {
                this.putOpt(name, c.getField(name).get(object));
            }
            catch (Exception ignore)
            {
                // empty catch block
            }
            ++i;
        }
    }

    public JSONObject(String source) throws JSONException
    {
        this(new JSONTokener(source));
    }

    public JSONObject(String baseName, Locale locale) throws JSONException
    {
        this();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            if (key == null) continue;
            String[] path = key.split("\\.");
            int last = path.length - 1;
            JSONObject target = this;
            for (int i = 0; i < last; ++i)
            {
                String segment = path[i];
                JSONObject nextTarget = target.optJSONObject(segment);
                if (nextTarget == null)
                {
                    nextTarget = new JSONObject();
                    target.put(segment, nextTarget);
                }
                target = nextTarget;
            }
            target.put(path[last], bundle.getString(key));
        }
    }

    public static String doubleToString(double d)
    {
        if (Double.isInfinite(d)) return "null";
        if (Double.isNaN(d))
        {
            return "null";
        }
        String string = Double.toString(d);
        if (string.indexOf(46) <= 0) return string;
        if (string.indexOf(101) >= 0) return string;
        if (string.indexOf(69) >= 0) return string;
        do
        {
            if (!string.endsWith("0"))
            {
                if (!string.endsWith(".")) return string;
                return string.substring(0, string.length() - 1);
            }
            string = string.substring(0, string.length() - 1);
        } while (true);
    }

    public static String[] getNames(JSONObject jo)
    {
        int length = jo.length();
        if (length == 0)
        {
            return null;
        }
        Iterator<String> iterator = jo.keys();
        String[] names = new String[length];
        int i = 0;
        while (iterator.hasNext())
        {
            names[i] = iterator.next();
            ++i;
        }
        return names;
    }

    public static String[] getNames(Object object)
    {
        if (object == null)
        {
            return null;
        }
        Class<?> klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0)
        {
            return null;
        }
        String[] names = new String[length];
        int i = 0;
        while (i < length)
        {
            names[i] = fields[i].getName();
            ++i;
        }
        return names;
    }

    public static String numberToString(Number number) throws JSONException
    {
        if (number == null)
        {
            throw new JSONException("Null pointer");
        }
        JSONObject.testValidity(number);
        String string = number.toString();
        if (string.indexOf(46) <= 0) return string;
        if (string.indexOf(101) >= 0) return string;
        if (string.indexOf(69) >= 0) return string;
        do
        {
            if (!string.endsWith("0"))
            {
                if (!string.endsWith(".")) return string;
                return string.substring(0, string.length() - 1);
            }
            string = string.substring(0, string.length() - 1);
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String quote(String string)
    {
        StringWriter sw = new StringWriter();
        StringBuffer stringBuffer = sw.getBuffer();
        synchronized (stringBuffer)
        {
            try
            {
                return JSONObject.quote(string, sw).toString();
            }
            catch (IOException ignored)
            {
                return "";
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    public static Writer quote(String string, Writer w) throws IOException
    {
        if (string == null || string.length() == 0)
        {
            w.write("\"\"");
            return w;
        }
        c = '\u0000';
        len = string.length();
        w.write(34);
        i = 0;
        do
        {
            if (i >= len)
            {
                w.write(34);
                return w;
            }
            b = c;
            c = string.charAt(i);
            switch (c)
            {
                case '\"':
                case '\\':
                {
                    w.write(92);
                    w.write(c);
                    **break;
                }
                case '/':
                {
                    if (b == '<')
                    {
                        w.write(92);
                    }
                    w.write(c);
                    **break;
                }
                case '\b':
                {
                    w.write("\\b");
                    **break;
                }
                case '\t':
                {
                    w.write("\\t");
                    **break;
                }
                case '\n':
                {
                    w.write("\\n");
                    **break;
                }
                case '\f':
                {
                    w.write("\\f");
                    **break;
                }
                case '\r':
                {
                    w.write("\\r");
                    **break;
                }
            }
            if (c < ' ' || c >= '?' && c < '\u00a0' || c >= '\u2000' && c < '\u2100')
            {
                w.write("\\u");
                hhhh = Integer.toHexString(c);
                w.write("0000", 0, 4 - hhhh.length());
                w.write(hhhh);
                **break;
            }
            w.write(c);
            lbl46:
            // 9 sources:
            ++i;
        } while (true);
    }

    public static Object stringToValue(String string)
    {
        if (string.equals(""))
        {
            return string;
        }
        if (string.equalsIgnoreCase("true"))
        {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false"))
        {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null"))
        {
            return NULL;
        }
        char initial = string.charAt(0);
        if (initial < '0' || initial > '9')
        {
            if (initial != '-') return string;
        }
        try
        {
            if (string.indexOf(46) <= -1 && string.indexOf(101) <= -1 && string.indexOf(69) <= -1 && !"-0".equals(string))
            {
                Long myLong = new Long(string);
                if (!string.equals(myLong.toString())) return string;
                if (myLong != (long) myLong.intValue()) return myLong;
                return myLong.intValue();
            }
            Double d = Double.valueOf(string);
            if (d.isInfinite()) return string;
            if (d.isNaN()) return string;
            return d;
        }
        catch (Exception ignore)
        {
            // empty catch block
        }
        return string;
    }

    public static void testValidity(Object o) throws JSONException
    {
        if (o == null) return;
        if (o instanceof Double)
        {
            if (((Double) o).isInfinite()) throw new JSONException("JSON does not allow non-finite numbers.");
            if (!((Double) o).isNaN()) return;
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
        if (!(o instanceof Float)) return;
        if (((Float) o).isInfinite()) throw new JSONException("JSON does not allow non-finite numbers.");
        if (!((Float) o).isNaN()) return;
        throw new JSONException("JSON does not allow non-finite numbers.");
    }

    public static String valueToString(Object value) throws JSONException
    {
        if (value == null) return "null";
        if (value.equals(null))
        {
            return "null";
        }
        if (value instanceof JSONString)
        {
            String object;
            try
            {
                object = ((JSONString) value).toJSONString();
            }
            catch (Exception e)
            {
                throw new JSONException(e);
            }
            if (!(object instanceof String)) throw new JSONException("Bad value from toJSONString: " + object);
            return object;
        }
        if (value instanceof Number)
        {
            return JSONObject.numberToString((Number) value);
        }
        if (value instanceof Boolean) return value.toString();
        if (value instanceof JSONObject) return value.toString();
        if (value instanceof JSONArray)
        {
            return value.toString();
        }
        if (value instanceof Map)
        {
            Map map = (Map) value;
            return new JSONObject(map).toString();
        }
        if (value instanceof Collection)
        {
            Collection coll = (Collection) value;
            return new JSONArray(coll).toString();
        }
        if (!value.getClass().isArray()) return JSONObject.quote(value.toString());
        return new JSONArray(value).toString();
    }

    public static Object wrap(Object object)
    {
        try
        {
            if (object == null)
            {
                return NULL;
            }
            if (object instanceof JSONObject) return object;
            if (object instanceof JSONArray) return object;
            if (NULL.equals(object)) return object;
            if (object instanceof JSONString) return object;
            if (object instanceof Byte) return object;
            if (object instanceof Character) return object;
            if (object instanceof Short) return object;
            if (object instanceof Integer) return object;
            if (object instanceof Long) return object;
            if (object instanceof Boolean) return object;
            if (object instanceof Float) return object;
            if (object instanceof Double) return object;
            if (object instanceof String) return object;
            if (object instanceof BigInteger) return object;
            if (object instanceof BigDecimal)
            {
                return object;
            }
            if (object instanceof Collection)
            {
                Collection coll = (Collection) object;
                return new JSONArray(coll);
            }
            if (object.getClass().isArray())
            {
                return new JSONArray(object);
            }
            if (object instanceof Map)
            {
                Map map = (Map) object;
                return new JSONObject(map);
            }
            Package objectPackage = object.getClass().getPackage();
            String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
            if (objectPackageName.startsWith("java.")) return object.toString();
            if (objectPackageName.startsWith("javax.")) return object.toString();
            if (object.getClass().getClassLoader() != null) return new JSONObject(object);
            return object.toString();
        }
        catch (Exception exception)
        {
            return null;
        }
    }

    static final Writer writeValue(Writer writer, Object value, int indentFactor, int indent) throws JSONException, IOException
    {
        String o;
        if (value == null || value.equals(null))
        {
            writer.write("null");
            return writer;
        }
        if (value instanceof JSONObject)
        {
            ((JSONObject) value).write(writer, indentFactor, indent);
            return writer;
        }
        if (value instanceof JSONArray)
        {
            ((JSONArray) value).write(writer, indentFactor, indent);
            return writer;
        }
        if (value instanceof Map)
        {
            Map map = (Map) value;
            new JSONObject(map).write(writer, indentFactor, indent);
            return writer;
        }
        if (value instanceof Collection)
        {
            Collection coll = (Collection) value;
            new JSONArray(coll).write(writer, indentFactor, indent);
            return writer;
        }
        if (value.getClass().isArray())
        {
            new JSONArray(value).write(writer, indentFactor, indent);
            return writer;
        }
        if (value instanceof Number)
        {
            writer.write(JSONObject.numberToString((Number) value));
            return writer;
        }
        if (value instanceof Boolean)
        {
            writer.write(value.toString());
            return writer;
        }
        if (!(value instanceof JSONString))
        {
            JSONObject.quote(value.toString(), writer);
            return writer;
        }
        try
        {
            o = ((JSONString) value).toJSONString();
        }
        catch (Exception e)
        {
            throw new JSONException(e);
        }
        writer.write(o != null ? o.toString() : JSONObject.quote(value.toString()));
        return writer;
    }

    static final void indent(Writer writer, int indent) throws IOException
    {
        int i = 0;
        while (i < indent)
        {
            writer.write(32);
            ++i;
        }
    }

    public JSONObject accumulate(String key, Object value) throws JSONException
    {
        JSONObject.testValidity(value);
        Object object = this.opt(key);
        if (object == null)
        {
            this.put(key, value instanceof JSONArray ? new JSONArray().put(value) : value);
            return this;
        }
        if (object instanceof JSONArray)
        {
            ((JSONArray) object).put(value);
            return this;
        }
        this.put(key, new JSONArray().put(object).put(value));
        return this;
    }

    public JSONObject append(String key, Object value) throws JSONException
    {
        JSONObject.testValidity(value);
        Object object = this.opt(key);
        if (object == null)
        {
            this.put(key, new JSONArray().put(value));
            return this;
        }
        if (!(object instanceof JSONArray)) throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
        this.put(key, ((JSONArray) object).put(value));
        return this;
    }

    public Object get(String key) throws JSONException
    {
        if (key == null)
        {
            throw new JSONException("Null key.");
        }
        Object object = this.opt(key);
        if (object != null) return object;
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] not found.");
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws JSONException
    {
        E val = this.optEnum(clazz, key);
        if (val != null) return val;
        throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not an enum of type " + JSONObject.quote(clazz.getSimpleName()) + ".");
    }

    public boolean getBoolean(String key) throws JSONException
    {
        Object object = this.get(key);
        if (object.equals(Boolean.FALSE)) return false;
        if (object instanceof String && ((String) object).equalsIgnoreCase("false"))
        {
            return false;
        }
        if (object.equals(Boolean.TRUE)) return true;
        if (!(object instanceof String))
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a Boolean.");
        if (!((String) object).equalsIgnoreCase("true"))
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a Boolean.");
        return true;
    }

    public BigInteger getBigInteger(String key) throws JSONException
    {
        Object object = this.get(key);
        try
        {
            return new BigInteger(object.toString());
        }
        catch (Exception e)
        {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] could not be converted to BigInteger.");
        }
    }

    public BigDecimal getBigDecimal(String key) throws JSONException
    {
        Object object = this.get(key);
        try
        {
            return new BigDecimal(object.toString());
        }
        catch (Exception e)
        {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] could not be converted to BigDecimal.");
        }
    }

    public double getDouble(String key) throws JSONException
    {
        Object object = this.get(key);
        try
        {
            double d;
            if (object instanceof Number)
            {
                d = ((Number) object).doubleValue();
                return d;
            }
            d = Double.parseDouble((String) object);
            return d;
        }
        catch (Exception e)
        {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a number.");
        }
    }

    public int getInt(String key) throws JSONException
    {
        Object object = this.get(key);
        try
        {
            int n;
            if (object instanceof Number)
            {
                n = ((Number) object).intValue();
                return n;
            }
            n = Integer.parseInt((String) object);
            return n;
        }
        catch (Exception e)
        {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not an int.");
        }
    }

    public JSONArray getJSONArray(String key) throws JSONException
    {
        Object object = this.get(key);
        if (!(object instanceof JSONArray))
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a JSONArray.");
        return (JSONArray) object;
    }

    public JSONObject getJSONObject(String key) throws JSONException
    {
        Object object = this.get(key);
        if (!(object instanceof JSONObject))
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a JSONObject.");
        return (JSONObject) object;
    }

    public long getLong(String key) throws JSONException
    {
        Object object = this.get(key);
        try
        {
            long l;
            if (object instanceof Number)
            {
                l = ((Number) object).longValue();
                return l;
            }
            l = Long.parseLong((String) object);
            return l;
        }
        catch (Exception e)
        {
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] is not a long.");
        }
    }

    public String getString(String key) throws JSONException
    {
        Object object = this.get(key);
        if (!(object instanceof String))
            throw new JSONException("JSONObject[" + JSONObject.quote(key) + "] not a string.");
        return (String) object;
    }

    public boolean has(String key)
    {
        return this.map.containsKey(key);
    }

    public JSONObject increment(String key) throws JSONException
    {
        Object value = this.opt(key);
        if (value == null)
        {
            this.put(key, 1);
            return this;
        }
        if (value instanceof BigInteger)
        {
            this.put(key, ((BigInteger) value).add(BigInteger.ONE));
            return this;
        }
        if (value instanceof BigDecimal)
        {
            this.put(key, ((BigDecimal) value).add(BigDecimal.ONE));
            return this;
        }
        if (value instanceof Integer)
        {
            this.put(key, (Integer) value + 1);
            return this;
        }
        if (value instanceof Long)
        {
            this.put(key, (Long) value + 1L);
            return this;
        }
        if (value instanceof Double)
        {
            this.put(key, (Double) value + 1.0);
            return this;
        }
        if (!(value instanceof Float)) throw new JSONException("Unable to increment [" + JSONObject.quote(key) + "].");
        this.put(key, ((Float) value).floatValue() + 1.0f);
        return this;
    }

    public boolean isNull(String key)
    {
        return NULL.equals(this.opt(key));
    }

    public Iterator<String> keys()
    {
        return this.keySet().iterator();
    }

    public Set<String> keySet()
    {
        return this.map.keySet();
    }

    public int length()
    {
        return this.map.size();
    }

    public JSONArray names()
    {
        JSONArray ja = new JSONArray();
        Iterator<String> keys = this.keys();
        while (keys.hasNext())
        {
            ja.put(keys.next());
        }
        if (ja.length() == 0)
        {
            return null;
        }
        JSONArray jSONArray = ja;
        return jSONArray;
    }

    public Object opt(String key)
    {
        if (key == null)
        {
            return null;
        }
        Object object = this.map.get(key);
        return object;
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key)
    {
        return this.optEnum(clazz, key, null);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue)
    {
        try
        {
            Object val = this.opt(key);
            if (NULL.equals(val))
            {
                return defaultValue;
            }
            if (!clazz.isAssignableFrom(val.getClass())) return Enum.valueOf(clazz, val.toString());
            Enum myE = (Enum) val;
            return (E) myE;
        }
        catch (IllegalArgumentException e)
        {
            return defaultValue;
        }
        catch (NullPointerException e)
        {
            return defaultValue;
        }
    }

    public boolean optBoolean(String key)
    {
        return this.optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue)
    {
        try
        {
            return this.getBoolean(key);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public double optDouble(String key)
    {
        return this.optDouble(key, Double.NaN);
    }

    public BigInteger optBigInteger(String key, BigInteger defaultValue)
    {
        try
        {
            return this.getBigInteger(key);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue)
    {
        try
        {
            return this.getBigDecimal(key);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public double optDouble(String key, double defaultValue)
    {
        try
        {
            return this.getDouble(key);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public int optInt(String key)
    {
        return this.optInt(key, 0);
    }

    public int optInt(String key, int defaultValue)
    {
        try
        {
            return this.getInt(key);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public JSONArray optJSONArray(String key)
    {
        Object o = this.opt(key);
        if (!(o instanceof JSONArray)) return null;
        JSONArray jSONArray = (JSONArray) o;
        return jSONArray;
    }

    public JSONObject optJSONObject(String key)
    {
        Object object = this.opt(key);
        if (!(object instanceof JSONObject)) return null;
        JSONObject jSONObject = (JSONObject) object;
        return jSONObject;
    }

    public long optLong(String key)
    {
        return this.optLong(key, 0L);
    }

    public long optLong(String key, long defaultValue)
    {
        try
        {
            return this.getLong(key);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public String optString(String key)
    {
        return this.optString(key, "");
    }

    public String optString(String key, String defaultValue)
    {
        String string;
        Object object = this.opt(key);
        if (NULL.equals(object))
        {
            string = defaultValue;
            return string;
        }
        string = object.toString();
        return string;
    }

    private void populateMap(Object bean)
    {
        Class<?> klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        int i = 0;
        while (i < methods.length)
        {
            try
            {
                Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers()))
                {
                    String name = method.getName();
                    String key = "";
                    if (name.startsWith("get"))
                    {
                        key = "getClass".equals(name) || "getDeclaringClass".equals(name) ? "" : name.substring(3);
                    } else if (name.startsWith("is"))
                    {
                        key = name.substring(2);
                    }
                    if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0)
                    {
                        if (key.length() == 1)
                        {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1)))
                        {
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        }
                        Object result = method.invoke(bean, null);
                        if (result != null)
                        {
                            this.map.put(key, JSONObject.wrap(result));
                        }
                    }
                }
            }
            catch (Exception ignore)
            {
                // empty catch block
            }
            ++i;
        }
    }

    public JSONObject put(String key, boolean value) throws JSONException
    {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONObject put(String key, Collection<?> value) throws JSONException
    {
        this.put(key, new JSONArray(value));
        return this;
    }

    public JSONObject put(String key, double value) throws JSONException
    {
        this.put(key, new Double(value));
        return this;
    }

    public JSONObject put(String key, int value) throws JSONException
    {
        this.put(key, new Integer(value));
        return this;
    }

    public JSONObject put(String key, long value) throws JSONException
    {
        this.put(key, new Long(value));
        return this;
    }

    public JSONObject put(String key, Map<?, ?> value) throws JSONException
    {
        this.put(key, new JSONObject(value));
        return this;
    }

    public JSONObject put(String key, Object value) throws JSONException
    {
        if (key == null)
        {
            throw new NullPointerException("Null key.");
        }
        if (value != null)
        {
            JSONObject.testValidity(value);
            this.map.put(key, value);
            return this;
        }
        this.remove(key);
        return this;
    }

    public JSONObject putOnce(String key, Object value) throws JSONException
    {
        if (key == null) return this;
        if (value == null) return this;
        if (this.opt(key) != null)
        {
            throw new JSONException("Duplicate key \"" + key + "\"");
        }
        this.put(key, value);
        return this;
    }

    public JSONObject putOpt(String key, Object value) throws JSONException
    {
        if (key == null) return this;
        if (value == null) return this;
        this.put(key, value);
        return this;
    }

    public Object remove(String key)
    {
        return this.map.remove(key);
    }

    public boolean similar(Object other)
    {
        try
        {
            Object valueOther;
            Object valueThis;
            if (!(other instanceof JSONObject))
            {
                return false;
            }
            Set<String> set = this.keySet();
            if (!set.equals(((JSONObject) other).keySet()))
            {
                return false;
            }
            Iterator<String> iterator = set.iterator();
            do
            {
                if (!iterator.hasNext()) return true;
                String name = iterator.next();
                valueThis = this.get(name);
                valueOther = ((JSONObject) other).get(name);
            } while (!(valueThis instanceof JSONObject ? !((JSONObject) valueThis).similar(valueOther) : (valueThis instanceof JSONArray ? !((JSONArray) valueThis).similar(valueOther) : !valueThis.equals(valueOther))));
            return false;
        }
        catch (Throwable exception)
        {
            return false;
        }
    }

    public JSONArray toJSONArray(JSONArray names) throws JSONException
    {
        if (names == null) return null;
        if (names.length() == 0)
        {
            return null;
        }
        JSONArray ja = new JSONArray();
        int i = 0;
        while (i < names.length())
        {
            ja.put(this.opt(names.getString(i)));
            ++i;
        }
        return ja;
    }

    public String toString()
    {
        try
        {
            return this.toString(0);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString(int indentFactor) throws JSONException
    {
        StringWriter w = new StringWriter();
        StringBuffer stringBuffer = w.getBuffer();
        synchronized (stringBuffer)
        {
            return this.write(w, indentFactor, 0).toString();
        }
    }

    public Writer write(Writer writer) throws JSONException
    {
        return this.write(writer, 0, 0);
    }

    public Writer write(Writer writer, int indentFactor, int indent) throws JSONException
    {
        try
        {
            boolean commanate = false;
            int length = this.length();
            Iterator<String> keys = this.keys();
            writer.write(123);
            if (length == 1)
            {
                String key = keys.next();
                writer.write(JSONObject.quote(key.toString()));
                writer.write(58);
                if (indentFactor > 0)
                {
                    writer.write(32);
                }
                JSONObject.writeValue(writer, this.map.get(key), indentFactor, indent);
            } else if (length != 0)
            {
                int newindent = indent + indentFactor;
                while (keys.hasNext())
                {
                    String key = keys.next();
                    if (commanate)
                    {
                        writer.write(44);
                    }
                    if (indentFactor > 0)
                    {
                        writer.write(10);
                    }
                    JSONObject.indent(writer, newindent);
                    writer.write(JSONObject.quote(key.toString()));
                    writer.write(58);
                    if (indentFactor > 0)
                    {
                        writer.write(32);
                    }
                    JSONObject.writeValue(writer, this.map.get(key), indentFactor, newindent);
                    commanate = true;
                }
                if (indentFactor > 0)
                {
                    writer.write(10);
                }
                JSONObject.indent(writer, indent);
            }
            writer.write(125);
            return writer;
        }
        catch (IOException exception)
        {
            throw new JSONException(exception);
        }
    }

    private static final class Null
    {

        private Null()
        {
        }

        protected final Object clone()
        {
            return this;
        }

        public boolean equals(Object object)
        {
            if (object == null) return true;
            if (object == this) return true;
            return false;
        }

        public String toString()
        {
            return "null";
        }
    }

}

