import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Footer, Navbar } from "../components";
import { login } from "../utils/UserRoutes";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    const user = { username, password };

    setLoading(true);
    setErrorMessage("");

    const result = await login(user);

    setLoading(false);

    if (result === "Login failed") {
      setErrorMessage("Invalid username or password");
    } else {
      // Store username in localStorage for navbar display
      localStorage.setItem('username', username);
      
      // Trigger a storage event so other components (like Navbar) can react
      window.dispatchEvent(new Event('storage'));
      
      // Navigate to homepage
      navigate('/');
    }
  };

  return (
    <>
      <Navbar />
      <div className="container my-3 py-3">
        <h1 className="text-center">Login</h1>
        <hr />
        <div className="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
            <form onSubmit={handleLogin}>
              <div className="my-3">
                <label for="display-4">Username</label>
                <input
                  type="text"
                  className="form-control"
                  id="floatingUsername"
                  placeholder="Username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                />
              </div>
              <div className="my-3">
                <label for="floatingPassword display-4">Password</label>
                <input
                  type="password"
                  className="form-control"
                  id="floatingPassword"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>
              {errorMessage && <p className="text-danger">{errorMessage}</p>}
              <div className="my-3">
                <p>
                  New Here?{" "}
                  <Link
                    to="/register"
                    className="text-decoration-underline text-info"
                  >
                    Register
                  </Link>{" "}
                </p>
                <p>
                  Forget Your Password?{" "}
                  <Link
                    to="/forgetpassword"
                    className="text-decoration-underline text-info"
                  >
                    Reset Here
                  </Link>{" "}
                </p>
              </div>
              <div className="text-center">
                <button
                  className="my-2 mx-auto btn btn-dark"
                  type="submit"
                  disabled={loading}
                >
                  {loading ? "Logging in..." : "Login"}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default Login;
