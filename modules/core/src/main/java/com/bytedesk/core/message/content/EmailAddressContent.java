/*
 * @Author: jackning
 * @Date: 2026-01-16
 * @Description: Content object for quick button EMAIL actions (mailto)
 */
package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EmailAddressContent extends BaseContent {

    private static final long serialVersionUID = 1L;

    private String emailAddress;

    private String emailSubject;

    private String emailBody;
}
