import SemiCircleProgress from "@src/assets/icon/SemiCircleProgress";

const SemiCircleCard = () => {
  return (
    <div className="relative flex flex-col justify-between w-[250px] h-[210px] p-4 rounded-2xl shadow-md bg-white">
      <div className="flex justify-between items-center py-2">
        <h2 className="font-semibold text-lg">스트레스 지수</h2>
      </div>
      {/* TOFO: 글자 위치 수정 */}
      <SemiCircleProgress progress={0.5} />
      <div className="flex justify-center text-center font-semibold text-[36px] z-10">
        {20}점
      </div>
    </div>
  );
};

export default SemiCircleCard;
