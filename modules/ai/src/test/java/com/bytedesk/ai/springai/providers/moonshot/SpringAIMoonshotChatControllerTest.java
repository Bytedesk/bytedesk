package com.bytedesk.ai.springai.providers.moonshot;

import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.AfterEach;

class SpringAIMoonshotChatControllerTest {

    private ExecutorService executorService;

    @AfterEach
    void tearDown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    // @Test
    // void chatSyncShouldReturnDisabledMessageWhenDebugIsOff() {
    //     BytedeskProperties properties = new BytedeskProperties();
    //     properties.setDebug(false);
    //     SpringAIMoonshotService service = new SpringAIMoonshotService();
    //     executorService = Executors.newSingleThreadExecutor();

    //     SpringAIMoonshotChatController controller = new SpringAIMoonshotChatController(properties, service, executorService);

    //     ResponseEntity<JsonResult<?>> response = controller.chatSync("hello moonshot");

    //     assertEquals(200, response.getStatusCode().value());
    //     assertEquals(500, response.getBody().getCode());
    //     assertEquals("Service is not available", response.getBody().getMessage());
    // }

    // @Test
    // void chatSyncShouldReturnProviderUnavailableWhenDebugIsOnButNoModelExists() {
    //     BytedeskProperties properties = new BytedeskProperties();
    //     properties.setDebug(true);
    //     SpringAIMoonshotService service = new SpringAIMoonshotService();
    //     executorService = Executors.newSingleThreadExecutor();

    //     SpringAIMoonshotChatController controller = new SpringAIMoonshotChatController(properties, service, executorService);

    //     ResponseEntity<JsonResult<?>> response = controller.chatSync("hello moonshot");

    //     assertEquals(200, response.getStatusCode().value());
    //     assertEquals(500, response.getBody().getCode());
    //     assertEquals("Moonshot service is not available", response.getBody().getMessage());
    // }

    // @Test
    // void chatStreamShouldReturnEmptyFluxWhenNoModelExists() {
    //     BytedeskProperties properties = new BytedeskProperties();
    //     properties.setDebug(true);
    //     SpringAIMoonshotService service = new SpringAIMoonshotService();
    //     executorService = Executors.newSingleThreadExecutor();

    //     SpringAIMoonshotChatController controller = new SpringAIMoonshotChatController(properties, service, executorService);

    //     Flux<?> response = controller.chatStream("hello moonshot");

    //     assertTrue(response.collectList().block().isEmpty());
    //     assertFalse(service.isServiceHealthy());
    // }
}