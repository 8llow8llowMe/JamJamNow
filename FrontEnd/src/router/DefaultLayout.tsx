import Footer from "@src/components/common/Footer";
import Header from "@src/components/common/Header";
import { Outlet } from "react-router-dom";

export default function DefaultLayout() {
  return (
    // <div className={isDark ? "theme-dark" : "theme-default"}>
    <div className="mx-auto max-w-[1280px] justify-center">
      <header className="mx-auto max-w-[1280px] h-[80px] justify-center fixed top-0 left-0 right-0 bg-[#f2f7fa] z-10">
        <Header />
      </header>
      <main className="pt-[80px] h-[calc(100vh-100px)] overflow-auto">
        <Outlet />
      </main>
      <footer className="h-[100px]">
        <Footer />
      </footer>
    </div>
  );
}
