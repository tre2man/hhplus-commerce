package kr.hhplus.be.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hibernate.internal.util.collections.CollectionHelper.listOf;

@Component
public class DatabaseClean {
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public void execute() {
            List<String> tableNames = listOf(
                    "balance", "coupon", "issued_coupon", "`order`", "order_product", "product"
            );

            for (String tableName : tableNames) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            }
        }
}
