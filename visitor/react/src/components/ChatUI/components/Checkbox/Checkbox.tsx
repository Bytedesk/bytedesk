import React from "react";
import clsx from "clsx";

export type CheckboxValue = string | number | undefined;

export type CheckboxProps = React.InputHTMLAttributes<HTMLInputElement> & {
  value?: CheckboxValue;
  label?: CheckboxValue;
};

export const Checkbox = React.forwardRef<HTMLLabelElement, CheckboxProps>(
  (props, ref) => {
    const { className, label, checked, disabled, onChange, ...other } = props;
    return (
      <label
        className={clsx("Checkbox", className, {
          "Checkbox--checked": checked,
          "Checkbox--disabled": disabled,
        })}
        ref={ref}
      >
        <input
          type="checkbox"
          className="Checkbox-input"
          checked={checked}
          disabled={disabled}
          onChange={onChange}
          {...other}
        />
        {/* <span className="Checkbox-icon" /> */}
        <span className="Checkbox-text">{label}</span>
      </label>
    );
  },
);
