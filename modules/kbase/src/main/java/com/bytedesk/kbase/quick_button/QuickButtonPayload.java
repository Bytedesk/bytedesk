/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-11
 * @Description: Payload for quick button actions
 */
package com.bytedesk.kbase.quick_button;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickButtonPayload implements Serializable {

    private static final long serialVersionUID = 1L;

    /** FAQ reference uid when type = FAQ */
    private String faqUid;

    /** Snapshot of the FAQ question to simplify client rendering */
    private String faqQuestion;

    /** FAQ fallback answer snippet */
    private String faqAnswer;

    /** External link when type = URL */
    private String url;

    /** Optional target hint: _blank/_self */
    private String urlTarget;

    /** Serialized schema when type = FORM */
    private String formSchema;

    /** Asset url when type = IMAGE */
    private String imageUrl;

    /** Phone number when type = PHONE */
    private String phoneNumber;

    /** Email address when type = EMAIL */
    private String emailAddress;

    /** Email subject template */
    private String emailSubject;

    /** Email body template */
    private String emailBody;

    /** Custom code for client side extensions */
    private String customCode;
}
