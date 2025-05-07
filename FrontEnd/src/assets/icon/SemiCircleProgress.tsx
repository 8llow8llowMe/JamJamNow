type SemiCircleProgressProps = {
  progress: number; // 0부터 1 사이의 값 (예: 0.25 = 25%)
};

const SemiCircleProgress: React.FC<SemiCircleProgressProps> = ({
  progress,
}) => {
  const radius = 95;
  const strokeWidth = 20;
  const circumference = Math.PI * radius;

  // 진행률 계산 (0~1)
  const strokeDashoffset = circumference * (1 - progress);

  return (
    <svg width="300" height="150" viewBox="60 -10 240 120">
      {/* 배경 반원 */}
      <path
        d="M20,100 A80,80 0 0 1 220,100"
        fill="none"
        stroke="#E5EAF1"
        strokeWidth={strokeWidth}
        strokeLinecap="round"
      />

      {/* 진행 반원 */}
      <path
        d="M20,100 A80,80 0 0 1 220,100"
        fill="none"
        stroke="#4F46E5"
        strokeWidth={strokeWidth}
        strokeDasharray={circumference}
        strokeDashoffset={strokeDashoffset}
        strokeLinecap="round"
      />
    </svg>
  );
};

export default SemiCircleProgress;
