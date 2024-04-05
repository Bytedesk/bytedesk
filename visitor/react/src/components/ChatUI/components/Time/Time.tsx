import React from "react";
import formatDate, { IDate } from "./parser";
import { useLocale } from "../ConfigProvider";

export interface TimeProps {
  date: IDate;
}

export const Time = ({ date }: TimeProps) => {
  const { trans } = useLocale("Time");

  return (
    <time className="Time" dateTime={new Date(date).toJSON()}>
      {formatDate(date, trans())}
    </time>
  );
};
