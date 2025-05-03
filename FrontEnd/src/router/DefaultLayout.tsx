import Footer from "@src/components/common/Footer";
import Header from "@src/components/common/Header";
import { Outlet } from "react-router-dom";

export default function DefaultLayout() {
  return (
    <div>
      <div>
        <header>
          <Header />
        </header>
        <main>
          <Outlet />
        </main>
        <footer>
          <Footer />
        </footer>
      </div>
    </div>
  );
}
