/* eslint-disable react/no-array-index-key */
import React from "react";
import clsx from "clsx";

export interface PriceProps extends React.HTMLAttributes<HTMLDivElement> {
  price: number;
  className?: string;
  locale?: string;
  currency?: string;
  original?: boolean;
}

const canFormat =
  "Intl" in window &&
  typeof Intl.NumberFormat.prototype.formatToParts === "function";

export const Price = React.forwardRef<HTMLDivElement, PriceProps>(
  (props, ref) => {
    const { className, price, currency, locale, original, ...other } = props;
    let parts: any[] | void = [];

    if (locale && currency && canFormat) {
      parts = new Intl.NumberFormat(locale, {
        style: "currency",
        currency,
      }).formatToParts(price);
    } else {
      parts = undefined;
    }

    if (!parts) {
      const decimal = ".";
      const [integer, fraction] = `${price}`.split(decimal);
      parts = [
        { type: "currency", value: currency },
        { type: "integer", value: integer },
        { type: "decimal", value: fraction && decimal },
        { type: "fraction", value: fraction },
      ];
    }

    return (
      <div
        className={clsx("Price", { "Price--original": original }, className)}
        ref={ref}
        {...other}
      >
        {parts.map((t, i) =>
          t.value ? (
            <span className={`Price-${t.type}`} key={i}>
              {t.value}
            </span>
          ) : null,
        )}
      </div>
    );
  },
);
