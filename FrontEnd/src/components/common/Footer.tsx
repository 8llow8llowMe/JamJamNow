import { memo } from "react";

const Footer = () => {
  return (
    <div className="text-sm text-gray-600 px-5 py-8">
      <div className="sm:flex-row sm:justify-between flex flex-col gap-2">
        <div className="space-y-2">
          <p className="font-semibold text-gray-800">
            JamJam - 우리 동네 교통 스트레스 지수 분석 대시보드
          </p>
          <div className="flex gap-3">
            <p>© 2025 팔로팔로미. All Rights Reserved.</p>
          </div>
        </div>
        <div className="space-y-2 flex flex-col">
          <a
            href="https://github.com/8llow8llowMe/JamJamNow"
            target="_blank"
            rel="noopener noreferrer"
            className="hover:underline"
          >
            Github ⭐️ 서비스 운영과 발전에 큰 도움이 됩니다.
          </a>
          <a href="mailto:nowdoboss@gmail.com" className="hover:underline">
            이메일 nowdoboss@gmail.com
          </a>
        </div>
      </div>
    </div>
  );
};

export default memo(Footer);
