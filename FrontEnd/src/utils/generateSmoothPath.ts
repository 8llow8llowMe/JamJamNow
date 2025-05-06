/** 입력받은 데이터를 path의 d 속성값으로 반환하는 함수
 *
 *  data는 20개 필요합니다.
 */
const generateSmoothPath = (
  data: number[],
  width: number,
  height: number
): string => {
  if (data.length !== 20) throw new Error("Data must be 20 points");

  const round2 = (num: number) => Math.round(num * 100) / 100;
  const xGap = width / (data.length - 1);

  const points = data.map((value, index) => {
    const x = round2(xGap * index);
    const y = round2(height * (1 - value / 100));
    return { x, y };
  });

  const path = [`M ${points[0].x},${points[0].y}`];

  for (let i = 0; i < points.length - 1; i++) {
    const p0 = points[i - 1] || points[i];
    const p1 = points[i];
    const p2 = points[i + 1] || points[i];
    const p3 = points[i + 2] || p2;

    const control1 = {
      x: round2(p1.x + (p2.x - p0.x) / 6),
      y: round2(p1.y + (p2.y - p0.y) / 6),
    };

    const control2 = {
      x: round2(p2.x - (p3.x - p1.x) / 6),
      y: round2(p2.y - (p3.y - p1.y) / 6),
    };

    path.push(
      `C ${control1.x},${control1.y} ${control2.x},${control2.y} ${p2.x},${p2.y}`
    );
  }

  return path.join(" ");
};

export default generateSmoothPath;
