import { useEffect, useRef } from "react";

const useDragLineChart = () => {
  const scrollRef = useRef<HTMLDivElement>(null);
  const isDragging = useRef(false);
  const startX = useRef(0);
  const scrollLeft = useRef(0);

  const getClientX = (e: MouseEvent | TouchEvent) => {
    if ("touches" in e) {
      return e.touches[0].clientX;
    }
    return e.clientX; // MouseEvent의 clientX
  };

  // 마우스/터치 공통 시작
  const handleDragStart = (e: MouseEvent | TouchEvent) => {
    isDragging.current = true;
    const clientX = getClientX(e);
    startX.current = clientX - (scrollRef.current?.offsetLeft ?? 0);
    scrollLeft.current = scrollRef.current?.scrollLeft ?? 0;
    e.preventDefault();
  };

  // 마우스/터치 이동
  const handleDragMove = (e: MouseEvent | TouchEvent) => {
    if (!isDragging.current || !scrollRef.current) return;

    const clientX = getClientX(e);
    const walk = clientX - (scrollRef.current.offsetLeft + startX.current);
    const newScrollLeft = scrollLeft.current - walk;

    // 스크롤 범위 제한: 최소 0, 최대 스크롤 가능한 영역
    const maxScrollLeft =
      scrollRef.current.scrollWidth - scrollRef.current.offsetWidth;
    const limitedScrollLeft = Math.max(
      0,
      Math.min(maxScrollLeft, newScrollLeft)
    );

    scrollRef.current.scrollLeft = limitedScrollLeft;

    // preventDefault()를 사용해 기본 스크롤 동작을 막을 수도 있음
    e.preventDefault();
  };

  // 드래그 끝
  const handleDragEnd = () => {
    isDragging.current = false;
  };

  useEffect(() => {
    const scrollEl = scrollRef.current;
    if (!scrollEl) return;

    // 터치 이벤트 처리
    const onTouchStart = (e: TouchEvent) => handleDragStart(e);
    const onTouchMove = (e: TouchEvent) => handleDragMove(e);
    const onTouchEnd = () => handleDragEnd();

    scrollEl.addEventListener("touchstart", onTouchStart);
    scrollEl.addEventListener("touchmove", onTouchMove);
    scrollEl.addEventListener("touchend", onTouchEnd);

    // 마우스 이벤트 처리
    const onMouseDown = (e: MouseEvent) => handleDragStart(e);
    const onMouseMove = (e: MouseEvent) => handleDragMove(e);
    const onMouseUp = () => handleDragEnd();

    scrollEl.addEventListener("mousedown", onMouseDown);
    scrollEl.addEventListener("mousemove", onMouseMove);
    scrollEl.addEventListener("mouseup", onMouseUp);
    scrollEl.addEventListener("mouseleave", onMouseUp); // 마우스가 영역을 벗어날 때에도 드래그 종료 처리

    return () => {
      // 터치 이벤트 제거
      scrollEl.removeEventListener("touchstart", onTouchStart);
      scrollEl.removeEventListener("touchmove", onTouchMove);
      scrollEl.removeEventListener("touchend", onTouchEnd);

      // 마우스 이벤트 제거
      scrollEl.removeEventListener("mousedown", onMouseDown);
      scrollEl.removeEventListener("mousemove", onMouseMove);
      scrollEl.removeEventListener("mouseup", onMouseUp);
      scrollEl.removeEventListener("mouseleave", onMouseUp);
    };
  }, []);

  return scrollRef;
};

export default useDragLineChart;
