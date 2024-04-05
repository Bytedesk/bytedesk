import React from "react";
import clsx from "clsx";
import { Flex } from "../Flex";

export interface CardMediaProps extends React.HTMLAttributes<HTMLDivElement> {
  className?: string;
  aspectRatio?: "square" | "wide";
  color?: string;
  image?: string;
  children?: React.ReactNode;
}

export const CardMedia = React.forwardRef<HTMLDivElement, CardMediaProps>(
  (props, ref) => {
    const {
      className,
      aspectRatio = "square",
      color,
      image,
      children,
      ...other
    } = props;

    const bgStyle = {
      backgroundColor: color || undefined,
      backgroundImage:
        typeof image === "string" ? `url('${image}')` : undefined,
    };

    return (
      <div
        className={clsx(
          "CardMedia",
          {
            "CardMedia--wide": aspectRatio === "wide",
            "CardMedia--square": aspectRatio === "square",
          },
          className,
        )}
        style={bgStyle}
        {...other}
        ref={ref}
      >
        {children && (
          <Flex className="CardMedia-content" direction="column" center>
            {children}
          </Flex>
        )}
      </div>
    );
  },
);
