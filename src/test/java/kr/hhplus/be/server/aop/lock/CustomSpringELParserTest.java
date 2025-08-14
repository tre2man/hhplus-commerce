package kr.hhplus.be.server.aop.lock;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CustomSpringELParserTest {
    @Test
    void 성공_1개_파라미터_파싱() {
        String[] parameterNames = {"userId"};
        Object[] args = {1L};
        String key = "#userId";

        Object result = CustomSpringELParser.getDynamicValue(parameterNames, args, key);

        assertThat(result).isEqualTo(1L);
    }

    @Test
    void 성공_2개_이상의_파라미터_파싱() {
        String[] parameterNames = {"userId", "orderId"};
        Object[] args = {1L, 100L};
        String key = "#userId + '-' + #orderId";

        Object result = CustomSpringELParser.getDynamicValue(parameterNames, args, key);

        assertThat(result).isEqualTo("1-100");
    }

    @Test
    void 실패_파라미터_이름_불일치() {
        String[] parameterNames = {"userId"};
        Object[] args = {1L, 100L};
        String key = "#userId + '-' + #orderId";

        Object result = CustomSpringELParser.getDynamicValue(parameterNames, args, key);

        assertThat(result).isEqualTo("1-null");
    }

    @Test
    void 실패_파라미터_없음() {
        String[] parameterNames = {};
        Object[] args = {};
        String key = "#userId + '-' + #orderId";

        Object result = CustomSpringELParser.getDynamicValue(parameterNames, args, key);

        assertThat(result).isEqualTo("null-null");
    }
}
