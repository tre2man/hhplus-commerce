package kr.hhplus.be.server.aop.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MultiDistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(multiDistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, final MultiDistributedLock multiDistributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        
        // 키 표현식을 평가하여 여러 키를 생성
        List<String> keys = generateKeys(signature.getParameterNames(), joinPoint.getArgs(), multiDistributedLock);
        
        // 모든 락을 순서대로 획득 시도
        List<RLock> acquiredLocks = new ArrayList<>();
        try {
            for (String key : keys) {
                RLock rLock = redissonClient.getLock(REDISSON_LOCK_PREFIX + key);
                boolean available = rLock.tryLock(multiDistributedLock.waitTime(), multiDistributedLock.leaseTime(), multiDistributedLock.timeUnit());
                if (!available) {
                    // 하나라도 락을 획득하지 못하면 모든 락을 해제하고 false 반환
                    releaseAllLocks(acquiredLocks);
                    return false;
                }
                acquiredLocks.add(rLock);
            }
            
            // 모든 락을 획득했으면 비즈니스 로직 실행
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            releaseAllLocks(acquiredLocks);
            throw new InterruptedException();
        } finally {
            // 모든 락 해제
            releaseAllLocks(acquiredLocks);
        }
    }
    
    private List<String> generateKeys(String[] parameterNames, Object[] args, MultiDistributedLock multiDistributedLock) {
        if (multiDistributedLock.keyExpression().isEmpty()) {
            return List.of();
        }
        
        // SpEL 표현식을 사용하여 키 생성
        String expression = multiDistributedLock.keyExpression();
        List<String> keys = CustomSpringELParser.getMultipleKeys(parameterNames, args, expression);
        
        // 키가 비어있으면 빈 리스트 반환
        if (keys.isEmpty()) {
            return List.of();
        }
        
        // 키에 prefix 추가
        String prefix = multiDistributedLock.keyPrefix();
        if (!prefix.isEmpty()) {
            keys = keys.stream()
                .map(key -> prefix + "::" + key)
                .collect(Collectors.toList());
        }
        
        return keys;
    }
    
    private void releaseAllLocks(List<RLock> locks) {
        for (RLock lock : locks) {
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (IllegalMonitorStateException e) {
                log.error("Failed to unlock the lock", e);
            }
        }
    }
}
