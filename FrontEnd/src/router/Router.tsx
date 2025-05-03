import { Routes, Route } from "react-router-dom";
import DefaultLayout from "@src/router/DefaultLayout";
import Home from "@src/pages/Home";
import PrivateRoute from "@src/router/PrivateRoute";

function Router() {
  return (
    <Routes>
      <Route element={<DefaultLayout />}>
        <Route path="/" element={<Home />} />
        <Route
          path="/map"
          element={
            <PrivateRoute>
              <Home />
            </PrivateRoute>
          }
        />
      </Route>
      {/* 404 redirect page */}
      {/* <Route path="*" element={<Navigate to="/" />} /> */}
    </Routes>
  );
}

export default Router;
