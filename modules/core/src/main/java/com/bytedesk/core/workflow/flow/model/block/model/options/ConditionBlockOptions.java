package com.bytedesk.core.workflow.flow.model.block.model.options;

import java.util.List;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionBlockOptions extends BlockOptions {
    private List<ConditionItem> items;
    private String comparisons;
}
