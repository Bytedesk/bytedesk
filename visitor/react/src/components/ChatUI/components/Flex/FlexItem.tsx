import React from "react";
import clsx from "clsx";

export interface FlexItemProps extends React.HTMLAttributes<HTMLElement> {
  className?: string;
  flex?: string;
  alignSelf?:
    | "auto"
    | "flex-start"
    | "flex-end"
    | "center"
    | "baseline"
    | "stretch";
  order?: number;
}

export const FlexItem = React.forwardRef<HTMLDivElement, FlexItemProps>(
  (props, ref) => {
    const { className, flex, alignSelf, order, style, children, ...other } =
      props;
    return (
      <div
        className={clsx("FlexItem", className)}
        style={{
          ...style,
          flex,
          alignSelf,
          order,
        }}
        ref={ref}
        {...other}
      >
        {children}
      </div>
    );
  },
);
