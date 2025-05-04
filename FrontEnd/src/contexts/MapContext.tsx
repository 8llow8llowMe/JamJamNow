// src/contexts/MapContext.tsx
import { createContext, useContext, useState, useEffect } from "react";
import { loadKakaoMap } from "@src/libs/kakaoMapLoader";

type MapContextType = {
  map: kakao.maps.Map | null;
  setMap: (map: kakao.maps.Map) => void;
  isLoaded: boolean;
};

const MapContext = createContext<MapContextType | undefined>(undefined);

export const MapProvider = ({ children }: { children: React.ReactNode }) => {
  const [map, setMap] = useState<kakao.maps.Map | null>(null);
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    loadKakaoMap().then(() => {
      setIsLoaded(true);
    });
  }, []);

  return (
    <MapContext.Provider value={{ map, setMap, isLoaded }}>
      {children}
    </MapContext.Provider>
  );
};

export const useMap = () => {
  const context = useContext(MapContext);
  if (!context) throw new Error("useMap must be used within a MapProvider");
  return context;
};
