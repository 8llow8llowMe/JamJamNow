import { useState, useEffect, memo } from "react";
import { loadKakaoMap } from "@src/libs/kakaoMapLoader";
import { MapContext } from "@src/contexts/MapContext";

const MapProvider = ({ children }: { children: React.ReactNode }) => {
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

export default memo(MapProvider);
