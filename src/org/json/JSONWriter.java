/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

import java.io.IOException;
import java.io.Writer;

public class JSONWriter
{

    private static final int maxdepth = 200;
    private final JSONObject[] stack = new JSONObject[200];
    protected char mode = (char) 105;
    protected Writer writer;
    private boolean comma = false;
    private int top = 0;

    public JSONWriter(Writer w)
    {
        this.writer = w;
    }

    private JSONWriter append(String string) throws JSONException
    {
        if (string == null)
        {
            throw new JSONException("Null pointer");
        }
        if (this.mode != 'o')
        {
            if (this.mode != 'a') throw new JSONException("Value out of sequence.");
        }
        try
        {
            if (this.comma && this.mode == 'a')
            {
                this.writer.write(44);
            }
            this.writer.write(string);
        }
        catch (IOException e)
        {
            throw new JSONException(e);
        }
        if (this.mode == 'o')
        {
            this.mode = (char) 107;
        }
        this.comma = true;
        return this;
    }

    public JSONWriter array() throws JSONException
    {
        if (this.mode != 'i' && this.mode != 'o')
        {
            if (this.mode != 'a') throw new JSONException("Misplaced array.");
        }
        this.push(null);
        this.append("[");
        this.comma = false;
        return this;
    }

    private JSONWriter end(char mode, char c) throws JSONException
    {
        if (this.mode != mode)
        {
            String string;
            if (mode == 'a')
            {
                string = "Misplaced endArray.";
                throw new JSONException(string);
            }
            string = "Misplaced endObject.";
            throw new JSONException(string);
        }
        this.pop(mode);
        try
        {
            this.writer.write(c);
        }
        catch (IOException e)
        {
            throw new JSONException(e);
        }
        this.comma = true;
        return this;
    }

    public JSONWriter endArray() throws JSONException
    {
        return this.end('a', ']');
    }

    public JSONWriter endObject() throws JSONException
    {
        return this.end('k', '}');
    }

    public JSONWriter key(String string) throws JSONException
    {
        if (string == null)
        {
            throw new JSONException("Null key.");
        }
        if (this.mode != 'k') throw new JSONException("Misplaced key.");
        try
        {
            this.stack[this.top - 1].putOnce(string, Boolean.TRUE);
            if (this.comma)
            {
                this.writer.write(44);
            }
            this.writer.write(JSONObject.quote(string));
            this.writer.write(58);
            this.comma = false;
            this.mode = (char) 111;
            return this;
        }
        catch (IOException e)
        {
            throw new JSONException(e);
        }
    }

    public JSONWriter object() throws JSONException
    {
        if (this.mode == 'i')
        {
            this.mode = (char) 111;
        }
        if (this.mode != 'o')
        {
            if (this.mode != 'a') throw new JSONException("Misplaced object.");
        }
        this.append("{");
        this.push(new JSONObject());
        this.comma = false;
        return this;
    }

    private void pop(char c) throws JSONException
    {
        char m;
        if (this.top <= 0)
        {
            throw new JSONException("Nesting error.");
        }
        char c2 = m = this.stack[this.top - 1] == null ? (char) 'a' : 'k';
        if (m != c)
        {
            throw new JSONException("Nesting error.");
        }
        --this.top;
        this.mode = (char) (this.top == 0 ? 100 : (this.stack[this.top - 1] == null ? 97 : 107));
    }

    private void push(JSONObject jo) throws JSONException
    {
        if (this.top >= 200)
        {
            throw new JSONException("Nesting too deep.");
        }
        this.stack[this.top] = jo;
        this.mode = (char) (jo == null ? 97 : 107);
        ++this.top;
    }

    public JSONWriter value(boolean b) throws JSONException
    {
        String string;
        if (b)
        {
            string = "true";
            return this.append(string);
        }
        string = "false";
        return this.append(string);
    }

    public JSONWriter value(double d) throws JSONException
    {
        return this.value(new Double(d));
    }

    public JSONWriter value(long l) throws JSONException
    {
        return this.append(Long.toString(l));
    }

    public JSONWriter value(Object object) throws JSONException
    {
        return this.append(JSONObject.valueToString(object));
    }
}

