import React, { useState } from "react";
import { Footer, Navbar } from "../components";
import { Link } from "react-router-dom";
import { register } from "../utils/UserRoutes";

const Register = () => {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const validateForm = () => {
    // Reset error message
    setErrorMessage("");

    // Check if passwords match
    if (password !== confirmPassword) {
      setErrorMessage("Passwords do not match");
      return false;
    }

    // Check password length
    if (password.length < 3) {
      setErrorMessage("Password must be at least 3 characters long");
      return false;
    }

    return true;
  };

  const handleRegister = async (e) => {
    e.preventDefault();

    // Validate form before submission
    if (!validateForm()) {
      return;
    }

    const user = { username, email, password };

    setLoading(true);
    setErrorMessage("");
    setSuccessMessage("");

    const result = await register(user);

    setLoading(false);

    if (!result.startsWith("User successfully registered")) {
      setErrorMessage(result);
    } else {
      setSuccessMessage(
        "Your Registration has been successful, click here to login"
      );
      // Reset form fields after successful registration
      setUsername("");
      setEmail("");
      setPassword("");
      setConfirmPassword("");
    }
  };

  return (
    <>
      <Navbar />
      <div className="container my-3 py-3">
        <h1 className="text-center">Register</h1>
        <hr />
        <div className="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
            {successMessage ? (
              <div className="alert alert-success text-center">
                {successMessage.split("click here")[0]}
                <br />
                <Link to="/login" className="text-decoration-underline fw-bold">
                  Click Here to Login
                </Link>
              </div>
            ) : (
              <form onSubmit={handleRegister}>
                <div className="form my-3">
                  <label htmlFor="Name">Username</label>
                  <input
                    type="text"
                    className="form-control"
                    id="Name"
                    placeholder="Enter Your Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                  />
                </div>
                <div className="form my-3">
                  <label htmlFor="Email">Email address</label>
                  <input
                    type="email"
                    className="form-control"
                    id="Email"
                    placeholder="name@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                <div className="form my-3">
                  <label htmlFor="Password">Password</label>
                  <input
                    type="password"
                    className="form-control"
                    id="Password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                  <small className="form-text text-muted">
                    Password must be at least 3 characters long
                  </small>
                </div>
                <div className="form my-3">
                  <label htmlFor="ConfirmPassword">Confirm Password</label>
                  <input
                    type="password"
                    className="form-control"
                    id="ConfirmPassword"
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                  />
                </div>
                {errorMessage && (
                  <div className="alert alert-danger" role="alert">
                    {errorMessage}
                  </div>
                )}
                <div className="my-3">
                  <p>
                    Already has an account?{" "}
                    <Link
                      to="/login"
                      className="text-decoration-underline text-info"
                    >
                      Login
                    </Link>{" "}
                  </p>
                </div>
                <div className="text-center">
                  <button
                    className="my-2 mx-auto btn btn-dark"
                    type="submit"
                    disabled={
                      loading ||
                      !username ||
                      !email ||
                      !password ||
                      !confirmPassword
                    }
                  >
                    {loading ? "Registering..." : "Register"}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default Register;
