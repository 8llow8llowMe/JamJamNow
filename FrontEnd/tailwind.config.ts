export default {
  darkMode: "class", // 또는 'media'
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "var(--color-primary)",
        background: "var(--color-background)",
        text: "var(--color-text)",
      },
      fontSize: {
        "2xs": "0.625rem", // 10px
        "3xs": "0.5rem", // 8px
      },
      screens: {
        "2xs": "320px", // 예: iPhone SE
        xs: "480px", // 예: 작은 모바일
      },
    },
  },
  plugins: [],
};
