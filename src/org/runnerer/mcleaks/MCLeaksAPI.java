package org.runnerer.mcleaks;

import okhttp3.OkHttpClient;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface MCLeaksAPI
{

    static Builder builder()
    {
        return new Builder();
    }

    void checkAccount(String username, Consumer<Boolean> callback, Consumer<Throwable> errorHandler);

    Result checkAccount(String username);

    Optional<Boolean> getCachedCheck(String username);

    void checkAccount(UUID uuid, Consumer<Boolean> callback, Consumer<Throwable> errorHandler);

    Result checkAccount(UUID uuid);

    Optional<Boolean> getCachedCheck(UUID uuid);

    void shutdown();

    class Result
    {

        private boolean isBot;
        private Throwable error;

        Result(boolean isMCLeaks)
        {
            this.isBot = isMCLeaks;
        }

        Result(Throwable error)
        {
            this.error = error;
        }

        public boolean isBot()
        {
            return this.isBot;
        }

        public Throwable getError()
        {
            return this.error;
        }

        public boolean hasError()
        {
            return this.error != null;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Result result = (Result) o;

            if (isBot != result.isBot) return false;
            return error != null ? error.equals(result.error) : result.error == null;
        }

        @Override
        public int hashCode()
        {
            int result = (isBot ? 1 : 0);
            result = 31 * result + (error != null ? error.hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            return "Result{" + "isMCLeaks=" + isBot +
                    ", error=" + error +
                    '}';
        }
    }

    class Builder
    {

        private int threadCount = 3;
        private long expireAfter = 5;
        private TimeUnit unit = TimeUnit.MINUTES;
        private boolean testing, noCache;
        private String userAgent = "MCLeaksApiClient";
        private String apiKey;
        private OkHttpClient okHttpClient;

        public Builder okHttpClient(OkHttpClient okHttpClient)
        {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Builder threadCount(int threadCount)
        {
            this.threadCount = threadCount;
            return this;
        }

        public Builder expireAfter(long expireAfter, TimeUnit unit)
        {
            this.expireAfter = expireAfter;
            this.unit = unit;
            return this;
        }

        public Builder testing()
        {
            this.testing = true;
            return this;
        }

        public Builder userAgent(String userAgent)
        {
            this.userAgent = userAgent;
            return this;
        }

        public Builder apiKey(String apiKey)
        {
            this.apiKey = apiKey;
            return this;
        }

        public Builder nocache()
        {
            this.noCache = true;
            return this;
        }

        public MCLeaksAPI build()
        {
            if (okHttpClient == null) okHttpClient = new OkHttpClient.Builder().build();
            if (this.noCache) return new MCLeaksDetection(threadCount, testing, userAgent, apiKey, okHttpClient);
            return new MCLeaksDetection(threadCount, expireAfter, unit, testing, userAgent, apiKey, okHttpClient);
        }
    }
}