declare global {
  interface Window {
    kakao: typeof kakao;
  }

  namespace kakao {
    namespace maps {
      class Map {
        constructor(container: HTMLElement, options: MapOptions);
        setCenter(position: LatLng): void;
        setLevel(level: number): void;
        addOverlayMapTypeId(mapTypeId: MapTypeId): void;
        removeOverlayMapTypeId(mapTypeId: MapTypeId): void;
        panTo(position: LatLng): void;
        getCenter(): LatLng;
        // ...필요에 따라 확장
      }

      class LatLng {
        constructor(latitude: number, longitude: number);
        getLat(): number;
        getLng(): number;
      }

      class Marker {
        constructor(options: MarkerOptions);
        setMap(map: Map | null): void;
        setPosition(position: LatLng): void;
      }

      class InfoWindow {
        constructor(options?: InfoWindowOptions);
        open(map: Map, marker: Marker): void;
        close(): void;
      }

      class MapTypeControl {
        constructor(): void;
      }

      class ZoomControl {
        constructor(): void;
      }

      interface MapOptions {
        center: LatLng;
        level?: number;
        mapTypeId?: MapTypeId;
      }

      interface MarkerOptions {
        position: LatLng;
        map?: Map;
        title?: string;
        image?: MarkerImage;
        clickable?: boolean;
      }

      interface InfoWindowOptions {
        content?: string | HTMLElement;
        removable?: boolean;
        position?: LatLng;
      }

      class MarkerImage {
        constructor(src: string, size: Size, options?: MarkerImageOptions);
      }

      interface MarkerImageOptions {
        alt?: string;
        coords?: string;
        shape?: string;
        offset?: Point;
      }

      class Size {
        constructor(width: number, height: number);
      }

      class Point {
        constructor(x: number, y: number);
      }

      type MapTypeId = "ROADMAP" | "HYBRID" | "SATELLITE" | "TRAFFIC" | string;

      function load(callback: () => void): void;
    }
  }
}

export {};
