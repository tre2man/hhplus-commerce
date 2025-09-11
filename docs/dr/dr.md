# 장애 분석 및 대응 문서

## 1. 목적

커머스 플랫폼의 핵심 API인 POST /order와 POST /coupon/create에 대한 가상 장애 상황을 분석하고, 실제 부하 테스트 결과를 바탕으로 장애 대응 전략을 수립합니다.

## 2. 배경

- **대상 시스템**: HH Plus 2주차 커머스 서비스
- **핵심 API**: POST /order (주문 처리), POST /coupon/create (쿠폰 생성)
- **환경**: M1 MacBook Pro, 16GB RAM, Docker Compose
- **부하 테스트 도구**: k6

## 3. 문제 정의

### 3.1 기존 부하 테스트 결과 분석

#### 주문 API (POST /order)
- **테스트 조건**: RPS 100, ramping-arrival-rate
- **결과**:
    - p95 응답시간: 218ms (목표: 500ms 이하) ✅
    - 에러율: 0.00% (목표: 5% 이하) ✅
    - **현재 상태**: 양호

#### 쿠폰 발급 API (POST /coupon/issue)
- **테스트 조건**: RPS 1000, constant-arrival-rate, 10,000개 제한
- **결과**:
    - p95 응답시간: 2.87s (목표: 1s 이하) ❌
    - 에러율: 71.57% (발급 제한 에러를 제외하면 0%) (목표: 5% 이하) ❌
    - **현재 상태**: 부적합

### 3.2 핵심 장애 포인트 식별

#### POST /order API 장애 포인트
1. **Kafka 브로커 장애**: 이벤트 발행 실패로 주문 처리 중단
2. **데이터베이스 트랜잭션 장애**: Order, OrderPayment, OrderProduct 저장 중 부분 실패
3. **보상 트랜잭션 실패**: 롤백 과정에서 데이터 불일치 발생
4. **동시성 문제**: 동일 주문 ID에 대한 동시 처리

#### POST /coupon/create API 장애 포인트
1. **Redis 클러스터 장애**: CouponIssueCount, CouponIssueUser 저장 실패
2. **다중 저장소 트랜잭션 실패**: 4개 repository 간 데이터 불일치
3. **대량 트래픽 처리 한계**: 선착순 쿠폰 발급 시 p95 2.87초
4. **메모리 부족**: 대량 쿠폰 생성 시 리소스 고갈

## 4. 장애 발생 가능성

#### 주문 API 병목점
- **동기적 트랜잭션**: OrderPayment, OrderProduct 순차 처리
- **보상 로직 부재**: kafka 장애 시 대체 로직 미비

#### 쿠폰 API 병목점
- **성능 저하**: 지속적인 쿠폰 발급 요청이 올 시 쿠폰 발급 로직에 병목이 생깁니다.

## 5. 코드 개선안

### 5.1 POST /order API 개선안

#### 단기 개선안
1. **Circuit Breaker 패턴**
   ```java
   @Component
   public class OrderCircuitBreaker {
       @CircuitBreaker(name = "kafka", fallbackMethod = "fallbackOrder")
       public void createOrderEvent(OrderCommand command) {
           eventPublisher.publish(event, KafkaTopic.ORDER_REQUEST);
       }
       
       public void fallbackOrder(OrderCommand command, Exception ex) {
           // DB 직접 저장 또는 로컬 큐 활용
           orderService.createOrder(command);
       }
   }
   ```

2. **재시도 메커니즘**
   ```java
   @Retryable(value = {DataAccessException.class}, maxAttempts = 3)
   public Long createOrder(OrderCommand orderCommand) {
       // 기존 로직
   }
   ```

#### 중기 개선안
1. 보상 트랜잭션 실패 시 DLQ 활용, DLQ 일부 재시도 실패 시 관리자 알림
2. 쿠폰 발급 컨슈머를 다른 프로세스에 위임하여 API 서버와 분리, 장애 격리 및 가용성을 확보합니다.

### 6.2 POST /coupon/create API 개선안

#### 단기 개선안
1. **Redis Cluster 고가용성**
   ```yaml
   # docker-compose.yml
   redis-cluster:
     image: redis:7-alpine
     deploy:
       replicas: 3
     networks:
       - redis-net
   ```

### 6. 인프라 개선안

#### 수직 확장
- CPU: 2코어 → 4코어
- Memory: 2GB → 4GB

#### 수평 확장
- ALB 설정: 가용성 보장과 부하 분산을 위한 ALB 도입

## 7. 회고

- 장애 분석을 통해 시스템의 취약점을 파악했습니다.
- 단기, 중기, 장기 개선안을 수립할 수 있었습니다.
- 지속적인 모니터링과 알림 설정으로 사용자보다 개발자가 빠르게 문제를 인지할 수 있는 계획을 세웠습니다.