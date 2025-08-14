package kr.hhplus.be.server.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class RedisTestcontainersConfiguration {

	public static final GenericContainer<?> REDIS_CONTAINER;

	static {
		// Redis Container
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:8.2.0-alpine"))
			.withExposedPorts(6379);

		REDIS_CONTAINER.start();

		// Redis 설정
		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
	}

	@PreDestroy
	public void preDestroy() {
		if (REDIS_CONTAINER.isRunning()) {
			REDIS_CONTAINER.stop();
		}
	}
}