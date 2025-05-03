// src/components/TabToggle.tsx
import React from "react";

type TabToggleProps = {
  tabs: string[];
  selected: string;
  onSelect: (tab: string) => void;
};

const TabToggle: React.FC<TabToggleProps> = ({ tabs, selected, onSelect }) => {
  return (
    <div className="flex p-1 bg-white rounded-full shadow-sm w-fit space-x-2 text-sm">
      {tabs.map((tab) => (
        <button
          key={tab}
          onClick={() => onSelect(tab)}
          className={`px-6 py-2 rounded-full font-semibold transition-all duration-200 hover:bg-[#EBF0F5]
            ${selected === tab ? "bg-[#E3FF04] text-black" : "text-black"}
          `}
        >
          {tab}
        </button>
      ))}
    </div>
  );
};

export default TabToggle;
