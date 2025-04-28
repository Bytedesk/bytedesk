package com.bytedesk.kbase.llm.qa;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaResponseSimple implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;
    private String question;
    private String answer;
}
