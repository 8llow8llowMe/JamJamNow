import { Navigate } from "react-router-dom";
import { useCookies } from "react-cookie";

interface Props {
  children: React.ReactNode;
}

// TODO: 로그인 요청 모달 띄우기?

const PrivateRoute = ({ children }: Props) => {
  const [cookies] = useCookies(["accessToken"]);

  const isLoggedIn = !!cookies.accessToken;

  return isLoggedIn ? children : <Navigate to="/" replace />;
};

export default PrivateRoute;
