package kr.hhplus.be.server.domain.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> { }
