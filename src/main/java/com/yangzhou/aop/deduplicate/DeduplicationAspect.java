package com.yangzhou.aop.deduplicate;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yangzhou.frame.domain.User;
import com.yangzhou.security.SecurityUtils;
import com.yangzhou.service.CacheService;

@Slf4j
@Aspect
@Component
public class DeduplicationAspect {

    private static final String CACHE_KEY = "deduplication";

    @Autowired
    private CacheService cacheService;


    @Pointcut("@annotation(deduplication)")
    public void deduplicateAop(Deduplication deduplication) {
    }

    /**
     * 去重检查，并占位
     *
     * @param point
     * @param deduplication
     */
    @Before("deduplicateAop(deduplication)")
    public void before(JoinPoint point, Deduplication deduplication) {
        String key = this.getDeduplicationKey(deduplication);
        synchronized (DeduplicationAspect.class){
            Object o = cacheService.get(key);
            if (o != null) {
                throw new UnsupportedOperationException("请勿重复调用！");
            }
            cacheService.set(key, point.getArgs());
            cacheService.expire(key, 60 * 5);
        }
    }

    /**
     * 去除占位
     *
     * @param point
     * @param deduplication
     */
    @AfterThrowing("deduplicateAop(deduplication)")
    public void afterThrowing(JoinPoint point, Deduplication deduplication) {
        this.deleteDeduplicationCache(deduplication);
    }

    /**
     * 去除占位
     *
     * @param result
     * @param deduplication
     */
    @AfterReturning(returning = "result", pointcut = "deduplicateAop(deduplication)")
    public void AfterReturning(Object result, Deduplication deduplication) {
        this.deleteDeduplicationCache(deduplication);
    }

    /**
     * 去除去重占位缓存
     *
     * @param deduplication
     */
    private void deleteDeduplicationCache(Deduplication deduplication) {
        String key = this.getDeduplicationKey(deduplication);
        cacheService.delete(key);
    }

    /**
     * 获取去重键值
     *
     * @param deduplication
     * @return
     */
    private String getDeduplicationKey(Deduplication deduplication) {
        User currentUser = SecurityUtils.getCurrentUser();
        String uniqueHandler = deduplication.value();
        return String.format("%s::%s::%s", CACHE_KEY, currentUser.getLogin(), uniqueHandler);
    }
}
