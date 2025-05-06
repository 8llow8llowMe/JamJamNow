import { createContext } from "react";
import { MapContextType } from "@src/types/map";

export const MapContext = createContext<MapContextType | undefined>(undefined);
