package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChoiceBlockOptions extends BlockOptions {
    private List<ChoiceItem> items;
    private boolean isMultipleChoice;
    private String buttonLabel;
    private boolean dynamicItems;
    private String dynamicVariableId;
    private String searchInputPlaceholder;
} 
