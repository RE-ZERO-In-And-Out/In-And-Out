import { Link } from "react-router-dom";
export default function Login() {
  return (
    <>
      <div>Login</div>
      <Link to="/signin">Signin</Link>
      <br />
      <Link to="/identify_email">IdentifyEmail</Link>
      <br />
      <Link to="/main">Login</Link>
    </>
  );
}
