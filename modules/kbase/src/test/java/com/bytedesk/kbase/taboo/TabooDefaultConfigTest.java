package com.bytedesk.kbase.taboo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TabooDefaultConfig.class)
class TabooDefaultConfigTest {

    @Autowired
    private TabooService tabooService;

    @Test
    void registersDefaultTabooServiceBean() {
        assertThat(tabooService).isInstanceOf(TabooServiceImpl.class);
    }
}