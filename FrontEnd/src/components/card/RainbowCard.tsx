import { MoreIcon, RainbowGraphIcon } from "@src/assets/icon";

const RainbowCard = () => {
  const score = 80;
  return (
    <div className="relative flex flex-col justify-between w-[250px] h-[210px] p-4 rounded-2xl shadow-md bg-white">
      <div className="flex justify-between items-center py-2">
        <h2 className="font-semibold text-lg">스트레스 지수</h2>
        <div
          title="설명"
          className="cursor-pointer hover:bg-[#EBF0F5] active:bg-[#dee3e8] rounded-full p-1"
        >
          <MoreIcon />
        </div>
      </div>
      <div className="flex justify-center text-center font-semibold text-[36px] z-10">
        {score}점
      </div>
      <div className="absolute top-20">
        <RainbowGraphIcon score={score} />
      </div>
    </div>
  );
};

export default RainbowCard;
