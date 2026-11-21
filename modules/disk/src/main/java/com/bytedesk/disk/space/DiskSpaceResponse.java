package com.bytedesk.disk.space;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class DiskSpaceResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    /** SHARED / PERSONAL */
    private String type;

    private String description;

    private String rootUid;
}
