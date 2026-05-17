package com.bytedesk.call.xml_curl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

class XmlCurlServiceTest {

    @Test
    void handleDialplanShouldUseProviderEvenWhenDemoDialplanFlagIsDisabled() {
        XmlCurlDialplanProvider provider = (context, destinationNumber, params) -> Optional.of(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document type=\"freeswitch/xml\"><section name=\"dialplan\"/></document>");

        XmlCurlService service = new XmlCurlService(
            staticProvider(provider),
            emptyProvider(),
            emptyProvider());

        byte[] result = service.handleDialplan(Map.of(
            "Caller-Context", "default",
            "Caller-Destination-Number", "013311156272"));

        String xml = new String(result, StandardCharsets.UTF_8);
        assertTrue(xml.contains("<section name=\"dialplan\"/>"));
    }

    @Test
    void handleDialplanShouldReturnNotFoundWhenNoProviderMatchesAndDemoDialplanDisabled() {
        XmlCurlService service = new XmlCurlService(
            emptyProvider(),
            emptyProvider(),
            emptyProvider());

        byte[] result = service.handleDialplan(Map.of(
            "Caller-Context", "default",
            "Caller-Destination-Number", "5003"));

        String xml = new String(result, StandardCharsets.UTF_8);
        assertTrue(xml.contains("status=\"not found\""));
    }

    private static <T> ObjectProvider<T> emptyProvider() {
        return new SimpleObjectProvider<>();
    }

    private static <T> ObjectProvider<T> staticProvider(T bean) {
        return new SimpleObjectProvider<>(bean);
    }

    private static final class SimpleObjectProvider<T> implements ObjectProvider<T> {

        private final T bean;

        private SimpleObjectProvider() {
            this.bean = null;
        }

        private SimpleObjectProvider(T bean) {
            this.bean = bean;
        }

        @Override
        public T getObject(Object... args) {
            return bean;
        }

        @Override
        public T getIfAvailable() {
            return bean;
        }

        @Override
        public T getIfUnique() {
            return bean;
        }

        @Override
        public T getObject() {
            return bean;
        }

        @Override
        public java.util.stream.Stream<T> orderedStream() {
            return bean == null ? java.util.stream.Stream.empty() : java.util.stream.Stream.of(bean);
        }
    }
}