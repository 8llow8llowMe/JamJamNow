import TabToggle from "@src/components/ui/TabToggle";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
const tabMap: Record<string, string> = {
  "/": "메인화면",
  "/dashboard": "대시보드",
  "/analysis": "운전자 분석",
  "/profile": "내 정보",
};
const reverseTabMap: Record<string, string> = Object.fromEntries(
  Object.entries(tabMap).map(([k, v]) => [v, k])
);
const Header = () => {
  // 다크모드 전환 함수
  // const toggleDarkMode = () => {
  //   document.documentElement.classList.toggle("dark");
  // };

  const location = useLocation();
  const navigate = useNavigate();

  const [selectedTab, setSelectedTab] = useState(
    tabMap[location.pathname] || "메인화면"
  );

  // 경로가 바뀌면 탭도 갱신
  useEffect(() => {
    setSelectedTab(tabMap[location.pathname] || "메인화면");
  }, [location.pathname]);

  const handleSelectTab = (tab: string) => {
    setSelectedTab(tab);
    const path = reverseTabMap[tab];
    if (path) navigate(path);
  };

  return (
    <div className="flex h-[80px] justify-between items-center px-10 border-b-2 border-b-[#333]">
      <div
        className="text-[32px] font-semibold cursor-pointer"
        onClick={() => navigate("/")}
      >
        JamJam
      </div>
      <div>
        <TabToggle
          tabs={["메인화면", "대시보드", "운전자 분석", "내 정보"]}
          selected={selectedTab}
          onSelect={handleSelectTab}
        />
      </div>
      <div className="rounded-full p-4 border bg-slate-100">My</div>
    </div>
  );
};

export default Header;
