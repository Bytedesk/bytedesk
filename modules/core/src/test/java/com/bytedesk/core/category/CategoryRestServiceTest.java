package com.bytedesk.core.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.uid.UidUtils;

class CategoryRestServiceTest {

    @Test
    void shouldCollectSelfAndDescendantUids() {
        CategoryEntity grandchild = CategoryEntity.builder().uid("grandchild").build();
        CategoryEntity child = CategoryEntity.builder().uid("child").build();
        child.addChild(grandchild);

        CategoryEntity root = CategoryEntity.builder().uid("root").build();
        root.addChild(child);

        CategoryRestService service = new CategoryRestService(
                mock(CategoryRepository.class),
                new ModelMapper(),
                mock(UidUtils.class),
                mock(AuthService.class));

        assertThat(service.collectSelfAndDescendantUids(root))
                .containsExactly("root", "child", "grandchild");
    }

    @Test
    void shouldReturnEmptyWhenCategoryMissing() {
        CategoryRestService service = new CategoryRestService(
                mock(CategoryRepository.class),
                new ModelMapper(),
                mock(UidUtils.class),
                mock(AuthService.class));

        assertThat(service.collectSelfAndDescendantUids((CategoryEntity) null)).isEqualTo(List.of());
    }
}