// import MapView from "@src/components/map/MapView";

const Home = () => {
  return (
    <div className="flex h-full p-5 space-x-5">
      <div className="w-1/2 bg-blue-100">left container</div>
      <div className="w-1/2">
        <div className="flex w-full h-full justify-center items-center border-2 border-[#333] rounded-[20px]">
          {/* <MapView /> */}
          KakaoMap
        </div>
      </div>
    </div>
  );
};

export default Home;
