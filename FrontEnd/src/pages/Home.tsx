import { LocationIcon } from "@src/assets/icon";
import MapView from "@src/components/map/MapView";
import { MapProvider } from "@src/contexts/MapProvider";
import { useState } from "react";

const Home = () => {
  // TODO: store 추가 시 store에 저장
  const [latLngData, setLatLngData] = useState({
    lngNE: 127.22560147553521,
    latNE: 37.668539165787855,
    lngSW: 126.75076041880565,
    latSW: 37.46984057457333,
  });

  // TODO: store 추가 시 함수 분리 후 store에 저장
  const handleCurrentLocation = () => {
    if (!navigator.geolocation) {
      alert("현재 위치를 지원하지 않는 브라우저입니다.");
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;

        // 현재 위치를 중심으로 네모 영역을 만드는 예시 (±0.01 정도)
        setLatLngData({
          latNE: lat + 0.01,
          lngNE: lng + 0.01,
          latSW: lat - 0.01,
          lngSW: lng - 0.01,
        });
      },
      () => {
        alert("위치 정보를 불러올 수 없습니다. 위치 접근을 허용해주세요.");
      }
    );
  };

  return (
    <div className="flex md:flex-row flex-col-reverse h-full p-5 md:space-x-5 space-x-0">
      <div className="md:w-1/2 md:h-full h-1/2 bg-blue-100">left container</div>
      <div className="md:w-1/2 md:h-full h-1/2 md:mb-0 mb-5">
        <div className="relative flex w-full h-full justify-center items-center border-2 border-[#dbdbdb] rounded-[20px]">
          <MapProvider>
            <MapView latLngData={latLngData} />
          </MapProvider>
          <div
            title="현재 위치"
            className="absolute z-10 p-1 bottom-2 right-2 bg-[#f9f9f9] rounded-md cursor-pointer active:bg-[#efefef]"
            onClick={handleCurrentLocation}
          >
            <LocationIcon />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
