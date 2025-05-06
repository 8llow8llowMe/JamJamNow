import { useMap } from "@src/hooks/useMap";
import { useEffect, useRef } from "react";
// type
import { MapViewProps } from "@src/types/map";

const MapView = ({ latLngData }: MapViewProps) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const { map, setMap, isLoaded } = useMap();

  // 최초 1회 지도 생성
  useEffect(() => {
    if (!isLoaded || !mapRef.current || map) return;

    const centerLat = (latLngData.latNE + latLngData.latSW) / 2;
    const centerLng = (latLngData.lngNE + latLngData.lngSW) / 2;
    const center = new window.kakao.maps.LatLng(centerLat, centerLng);

    const options = {
      center,
      level: 5,
    };

    const mapInstance = new window.kakao.maps.Map(mapRef.current, options);
    setMap(mapInstance);
  }, [isLoaded, map, setMap, latLngData]);

  // latLngData가 바뀔 때마다 중심 좌표 이동
  useEffect(() => {
    if (!map) return;

    const centerLat = (latLngData.latNE + latLngData.latSW) / 2;
    const centerLng = (latLngData.lngNE + latLngData.lngSW) / 2;
    const newCenter = new window.kakao.maps.LatLng(centerLat, centerLng);

    map.panTo(newCenter);
  }, [latLngData, map]);

  return (
    <div ref={mapRef} className="w-full h-full rounded-[20px]" id="kakao-map" />
  );
};

export default MapView;
