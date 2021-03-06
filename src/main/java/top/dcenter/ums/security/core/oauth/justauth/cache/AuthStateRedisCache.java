/*
 * MIT License
 * Copyright (c) 2020-2029 YongWu zheng (dcenter.top and gitee.com/pcore and github.com/ZeroOrInfinity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package top.dcenter.ums.security.core.oauth.justauth.cache;

import org.springframework.util.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.dcenter.ums.security.core.oauth.justauth.enums.CacheKeyStrategy;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;
import top.dcenter.ums.security.core.oauth.properties.JustAuthProperties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * auth state redis cache, 适用单机与分布式
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/6 19:22
 */
public class AuthStateRedisCache implements Auth2StateCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final Duration timeout;
    private final String cacheKeyPrefix;

    public AuthStateRedisCache(Auth2Properties auth2Properties, Object stringRedisTemplate) {
        this.stringRedisTemplate = (StringRedisTemplate) stringRedisTemplate;
        final JustAuthProperties justAuth = auth2Properties.getJustAuth();
        this.timeout = justAuth.getTimeout();
        this.cacheKeyPrefix = justAuth.getCacheKeyPrefix();
    }

    @Override
    public void cache(String key, String value) {
        stringRedisTemplate.opsForValue().set(parsingKey(key), value, this.timeout);
    }

    @Override
    public void cache(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(parsingKey(key), value, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(parsingKey(key));
    }

    @Override
    public boolean containsKey(String key) {
        return StringUtils.hasText(stringRedisTemplate.opsForValue().get(parsingKey(key)));
    }

    @Override
    public CacheKeyStrategy getCacheKeyStrategy() {
        return CacheKeyStrategy.UUID;
    }

    private String parsingKey(String key) {
        return this.cacheKeyPrefix + key;
    }
}