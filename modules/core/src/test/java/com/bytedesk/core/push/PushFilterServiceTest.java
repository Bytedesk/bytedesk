package com.bytedesk.core.push;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PushFilterServiceTest {

    // @Mock
    // private StringRedisTemplate stringRedisTemplate;

    // private BytedeskProperties bytedeskProperties;

    // private PushFilterService pushFilterService;

    // @BeforeEach
    // void setUp() {
    //     bytedeskProperties = new BytedeskProperties();
    //     pushFilterService = new PushFilterService();
    //     ReflectionTestUtils.setField(pushFilterService, "stringRedisTemplate", stringRedisTemplate);
    //     ReflectionTestUtils.setField(pushFilterService, "bytedeskProperties", bytedeskProperties);
    // }

    // @Test
    // void canSendCodeReturnsTrueWithoutRedisCheckWhenDebugEnabled() {
    //     bytedeskProperties.setDebug(true);

    //     Boolean result = pushFilterService.canSendCode("127.0.0.1");

    //     assertThat(result).isTrue();
    //     verifyNoInteractions(stringRedisTemplate);
    // }

    // @Test
    // void canSendCodeChecksRedisWhenDebugDisabled() {
    //     bytedeskProperties.setDebug(false);
    //     String ip = "127.0.0.1";
    //     String key = RedisConsts.PUSH_CODE_IP_PREFIX + ip;
    //     when(stringRedisTemplate.hasKey(key)).thenReturn(Boolean.TRUE);

    //     Boolean result = pushFilterService.canSendCode(ip);

    //     assertThat(result).isFalse();
    //     verify(stringRedisTemplate).hasKey(key);
    // }
}