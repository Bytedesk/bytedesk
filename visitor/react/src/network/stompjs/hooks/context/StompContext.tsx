import { createContext } from "react";
import { StompSessionProviderContext } from "../interfaces/StompSessionProviderContext";

const StompContext = createContext<StompSessionProviderContext | undefined>(
  undefined,
);

export default StompContext;
