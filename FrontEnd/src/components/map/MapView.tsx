import { useMap } from "@src/hooks/useMap";
import { useEffect, useRef } from "react";

export default function MapView() {
  const mapRef = useRef<HTMLDivElement>(null);
  const { setMap, isLoaded } = useMap();

  useEffect(() => {
    if (!isLoaded || !mapRef.current) return;

    const center = new window.kakao.maps.LatLng(37.5665, 126.978);
    const options = {
      center,
      level: 3,
    };

    const mapInstance = new window.kakao.maps.Map(mapRef.current, options);
    setMap(mapInstance);
  }, [isLoaded, setMap]);

  return (
    <div ref={mapRef} className="w-full h-full rounded-[20px]" id="kakao-map" />
  );
}
