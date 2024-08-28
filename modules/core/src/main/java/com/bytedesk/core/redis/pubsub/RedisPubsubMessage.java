package com.bytedesk.core.redis.pubsub;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
// import lombok.extern.slf4j.Slf4j;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisPubsubMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private String content;
}
