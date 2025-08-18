package kr.hhplus.be.server.aop.lock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class CustomSpringELParserTest {
    static class TestOrderCommand {
        private final List<Long> productIds;
        TestOrderCommand(List<Long> productIds) { this.productIds = productIds; }
        public List<Long> getProductIds() { return productIds; }
    }

    @Test
    @DisplayName("SpEL이 List를 반환하면 문자열 리스트로 변환한다 (#orderCommand.getProductIds())")
    void 성공_문자열_리스트() {
        String[] names = {"userId", "orderCommand"};
        Object[] args = {7L, new TestOrderCommand(List.of(1L, 3L, 2L))};

        List<String> keys = CustomSpringELParser.getMultipleKeys(
                names, args, "#orderCommand.getProductIds()"
        );

        Assertions.assertEquals(List.of("1", "3", "2"), keys);
    }

    @Test
    @DisplayName("SpEL이 String을 반환하면 단일 원소 리스트로 변환한다")
    void 성공_문자_단일_원소() {
        String[] names = {"a", "b"};
        Object[] args = {"foo", "bar"};

        List<String> keys = CustomSpringELParser.getMultipleKeys(
                names, args, "#a + ':' + #b"
        );

        Assertions.assertEquals(List.of("foo:bar"), keys);
    }

    @Test
    @DisplayName("SpEL이 null을 반환하면 빈 리스트를 반환한다")
    void 성공_null_빈리스트() {
        List<String> keys = CustomSpringELParser.getMultipleKeys(
                new String[]{}, new Object[]{}, "null"
        );
        Assertions.assertTrue(keys.isEmpty());
    }

    @Test
    @DisplayName("SpEL이 숫자 등 비지원 타입을 반환하면 빈 리스트를 반환한다")
    void unsupportedType_returnsEmptyList() {
        // "123"은 Integer로 평가됨
        List<String> keys = CustomSpringELParser.getMultipleKeys(
                new String[]{}, new Object[]{}, "123"
        );
        Assertions.assertTrue(keys.isEmpty());
    }

    @Test
    @DisplayName("리터럴 리스트 {'x','y'}는 문자열 리스트로 반환된다")
    void literalList_returnsStringList() {
        List<String> keys = CustomSpringELParser.getMultipleKeys(
                new String[]{}, new Object[]{}, "{'x','y'}"
        );
        Assertions.assertEquals(List.of("x", "y"), keys);
    }
}
