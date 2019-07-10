/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

public class HTTPTokener
        extends JSONTokener
{

    public HTTPTokener(String string)
    {
        super(string);
    }

    public String nextToken() throws JSONException
    {
        char c;
        StringBuilder sb = new StringBuilder();
        while (Character.isWhitespace(c = this.next()))
        {
        }
        if (c == '\"' || c == '\'')
        {
            char q = c;
            do
            {
                if ((c = this.next()) < ' ')
                {
                    throw this.syntaxError("Unterminated string.");
                }
                if (c == q)
                {
                    return sb.toString();
                }
                sb.append(c);
            } while (true);
        }
        while (c != '\u0000')
        {
            if (Character.isWhitespace(c))
            {
                return sb.toString();
            }
            sb.append(c);
            c = this.next();
        }
        return sb.toString();
    }
}

