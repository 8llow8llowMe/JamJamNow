// example
export type LatLngDataType = {
  lngNE: number;
  latNE: number;
  lngSW: number;
  latSW: number;
};

export type MapContextType = {
  map: kakao.maps.Map | null;
  setMap: (map: kakao.maps.Map) => void;
  isLoaded: boolean;
};
