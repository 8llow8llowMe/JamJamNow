import useDragLineChart from "@src/hooks/useDragLineChart";
import generateSmoothPath from "@src/utils/generateSmoothPath";
import { memo, useRef } from "react";

const CongestionCard = () => {
  const indicatorRef = useRef<SVGCircleElement | null>(null);
  const labelRef = useRef<SVGTextElement | null>(null);

  const indicator2Ref = useRef<SVGCircleElement | null>(null);
  const label2Ref = useRef<SVGTextElement | null>(null);

  const lineRef = useRef<SVGLineElement | null>(null);
  const currentHoverIndex = useRef<number | null>(null);

  const width = 570;
  const height = 250;
  const numderDeps = (width / 19) * 4;
  const data = [
    65, 70, 14, 40, 30, 20, 15, 10, 5, 10, 20, 30, 40, 60, 80, 90, 75, 60, 40,
    0,
  ];
  const data2 = [
    26, 75, 57, 86, 24, 67, 98, 66, 44, 3, 78, 46, 45, 89, 54, 34, 89, 54, 33,
    0,
  ];

  const pathD1 = generateSmoothPath(data, width, height - 25);
  const pathD2 = generateSmoothPath(data2, width, height - 25);

  const scrollRef = useDragLineChart();

  // TODO: hook으로 분리하면 되나요?
  const handleMouseMove = (e: React.MouseEvent<SVGSVGElement, MouseEvent>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;

    const xGap = width / (data.length - 1);
    const index = Math.round(mouseX / xGap);

    if (index < 0 || index >= data.length) return;
    if (index === currentHoverIndex.current) return;

    currentHoverIndex.current = index;

    const x = 6 + index * xGap;
    const y1 = 25 + (height - 25) * (1 - data[index] / 100);
    const y2 = 25 + (height - 25) * (1 - data2[index] / 100);

    // data1
    if (indicatorRef.current) {
      indicatorRef.current.setAttribute("cx", `${x}`);
      indicatorRef.current.setAttribute("cy", `${y1}`);
      indicatorRef.current.style.display = "block";
    }
    if (labelRef.current) {
      labelRef.current.setAttribute("x", `${x + 6}`);
      labelRef.current.setAttribute("y", `${y1 - 6}`);
      labelRef.current.textContent = `${data[index]}%`;
      labelRef.current.style.display = "block";
    }

    // data2
    if (indicator2Ref.current) {
      indicator2Ref.current.setAttribute("cx", `${x}`);
      indicator2Ref.current.setAttribute("cy", `${y2}`);
      indicator2Ref.current.style.display = "block";
    }
    if (label2Ref.current) {
      label2Ref.current.setAttribute("x", `${x + 6}`);
      label2Ref.current.setAttribute("y", `${y2 + 16}`);
      label2Ref.current.textContent = `${data2[index]}%`;
      label2Ref.current.style.display = "block";
    }

    // 공통 수직선
    if (lineRef.current) {
      lineRef.current.setAttribute("x1", `${x}`);
      lineRef.current.setAttribute("x2", `${x}`);
      lineRef.current.style.display = "block";
    }
  };

  const handleMouseLeave = () => {
    currentHoverIndex.current = null;

    [indicatorRef, indicator2Ref].forEach((ref) => {
      if (ref.current) ref.current.style.display = "none";
    });
    [labelRef, label2Ref].forEach((ref) => {
      if (ref.current) ref.current.style.display = "none";
    });
    if (lineRef.current) lineRef.current.style.display = "none";
  };

  return (
    <div className="flex flex-col justify-between w-full h-[400px] p-4 rounded-2xl shadow-md bg-white">
      <div className="flex justify-between items-end py-2">
        <h2 className="font-semibold text-lg">00동 시간별 혼잡도</h2>
        <span className="text-xs text-gray-500">25.05.06 기준</span>
      </div>

      {/* 그래프 영역 */}
      <div
        ref={scrollRef}
        className="relative w-full overflow-x-auto scrollbar-hide"
      >
        <div
          className={`relative min-w-[${width}px] h-[${height}px] cursor-move`}
        >
          <svg
            width={width}
            height={height}
            viewBox={`0 0 ${width} ${height}`}
            className="absolute top-0 left-0 z-0"
            onMouseMove={handleMouseMove}
            onMouseLeave={handleMouseLeave}
          >
            {/* 세로 점선 - 패턴 정의 */}
            <defs>
              <pattern
                id="dotLine"
                width={width / 19}
                height={height}
                patternUnits="userSpaceOnUse"
              >
                <line
                  x1="0"
                  y1="25"
                  x2="0"
                  y2={height}
                  stroke="#ddd"
                  strokeWidth="4"
                  strokeDasharray="10 10"
                />
              </pattern>
              <linearGradient
                id="fadeRightWhite"
                x1="0%"
                y1="0%"
                x2="100%"
                y2="0%"
              >
                <stop offset="0%" stop-color="white" stop-opacity="0" />
                <stop offset="100%" stop-color="white" stop-opacity="1" />
              </linearGradient>
            </defs>
            <g transform="translate(6, 0)">
              <rect width="100%" height={height} fill="url(#dotLine)" />
              <text
                x={45 + numderDeps * 0}
                y="15"
                fill="#333"
                fontSize={14}
                fontWeight={500}
              >
                8:00
              </text>
              <text
                x={45 + numderDeps * 1}
                y="15"
                fill="#333"
                fontSize={14}
                fontWeight={500}
              >
                12:00
              </text>
              <text
                x={45 + numderDeps * 2}
                y="15"
                fill="#333"
                fontSize={14}
                fontWeight={500}
              >
                16:00
              </text>
              <text
                x={45 + numderDeps * 3}
                y="15"
                fill="#333"
                fontSize={14}
                fontWeight={500}
              >
                20:00
              </text>
              <text
                x={45 + numderDeps * 4}
                y="15"
                fill="#333"
                fontSize={14}
                fontWeight={500}
              >
                24:00
              </text>
            </g>
            <g transform="translate(6, 25)">
              {/* 버스 혼잡도 (green line) */}
              <path d={pathD1} fill="none" stroke="#22c55e" strokeWidth="3" />

              {/* 지하철 혼잡도 (red line) */}
              <path d={pathD2} fill="none" stroke="#ef4444" strokeWidth="3" />
              <rect
                className=""
                x={width - 28}
                width={25}
                height={height - 25}
                fill="url(#fadeRightWhite)"
              />
            </g>
            {/* hover indicator - data1 */}
            <circle
              ref={indicatorRef}
              r={5}
              fill="#22c55e"
              style={{ display: "none" }}
            />
            <text
              ref={labelRef}
              fontSize={12}
              fill="#22c55e"
              style={{ display: "none", pointerEvents: "none" }}
            />

            {/* hover indicator - data2 */}
            <circle
              ref={indicator2Ref}
              r={5}
              fill="#ef4444"
              style={{ display: "none" }}
            />
            <text
              ref={label2Ref}
              fontSize={12}
              fill="#ef4444"
              style={{ display: "none", pointerEvents: "none" }}
            />

            {/* 공통 수직선 */}
            <line
              ref={lineRef}
              y1={0}
              y2={height}
              stroke="#aaa"
              strokeDasharray="4 4"
              style={{ display: "none" }}
            />
          </svg>
        </div>
      </div>

      {/* 범례 */}
      <div className="py-2 flex justify-start gap-4 text-white text-sm font-semibold items-center">
        <div className="px-2 py-1 rounded-md bg-green-400">버스 혼잡도</div>
        <div className="px-2 py-1 rounded-md bg-red-400">지하철 혼잡도</div>
      </div>
    </div>
  );
};

export default memo(CongestionCard);
