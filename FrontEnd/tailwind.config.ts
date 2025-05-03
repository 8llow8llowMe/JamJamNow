export default {
  darkMode: "class", // 또는 'media'
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          light: "#4f46e5",
          dark: "#818cf8",
        },
        background: {
          light: "#ffffff",
          dark: "#0f172a",
        },
        text: {
          light: "#111827",
          dark: "#e5e7eb",
        },
      },
    },
  },
  plugins: [],
};
