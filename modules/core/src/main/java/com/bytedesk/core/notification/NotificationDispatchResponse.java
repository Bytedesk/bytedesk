package com.bytedesk.core.notification;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDispatchResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int sentCount;

    private String level;

    private String orgUid;

    private String deptUid;

    private String userUid;
}