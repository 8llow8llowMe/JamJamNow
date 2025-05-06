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

  const xGap = width / (data.length - 1);

  const points = data.map((value, index) => {
    const x = xGap * index;
    const y = height * (1 - value / 100);
    return { x, y };
  });

  let d = `M ${points[0].x},${points[0].y}`;

  for (let i = 1; i < points.length - 1; i++) {
    // const prev = points[i - 1];
    const curr = points[i];
    const next = points[i + 1];

    // Quadratic smoothing method (중간점을 control point처럼 사용)
    const midX = (curr.x + next.x) / 2;
    const midY = (curr.y + next.y) / 2;

    d += ` Q ${curr.x},${curr.y} ${midX},${midY}`;
  }

  // 마지막 점은 직선으로 마무리
  const last = points[points.length - 1];
  d += ` T ${last.x},${last.y}`;

  return d;
};

export default generateSmoothPath;
