import { Routes, Route } from "react-router-dom";
import DefaultLayout from "@src/router/DefaultLayout";
import Home from "@src/pages/Home";
import PrivateRoute from "@src/router/PrivateRoute";
import DashBoard from "@src/pages/DashBoard";
import Analysis from "@src/pages/Analysis";
import Profile from "@src/pages/Profile";

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
        <Route path="/dashboard" element={<DashBoard />} />
        <Route path="/analysis" element={<Analysis />} />
        <Route path="/profile" element={<Profile />} />
      </Route>
      {/* 404 redirect page */}
      {/* <Route path="*" element={<Navigate to="/" />} /> */}
    </Routes>
  );
}

export default Router;
