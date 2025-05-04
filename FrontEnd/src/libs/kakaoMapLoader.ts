export const loadKakaoMap = (): Promise<typeof window.kakao> => {
  return new Promise((resolve, reject) => {
    if (window.kakao && window.kakao.maps) {
      resolve(window.kakao);
      return;
    }

    const script = document.createElement("script");
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_REACT_APP_KAKAOMAP_API_KEY}&autoload=false`;
    script.async = true;
    script.onload = () => {
      window.kakao.maps.load(() => {
        resolve(window.kakao);
      });
    };
    script.onerror = (err) => reject(err);

    document.head.appendChild(script);
  });
};
