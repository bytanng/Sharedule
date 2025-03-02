import React, {useState} from "react";
import { Link } from "react-router-dom";
import { Footer, Navbar } from "../components";
import { requestPasswordReset } from "../utils/UserRoutes";
import { EMAIL_REGEX } from "../utils/Constants";

const ForgetPassword = () => {

  const [email, setEmail] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const handleChangeEmail = (e) => {
    setEmail(e.target.value);
  } 

  const validateEmail = (email) => {
    if (!EMAIL_REGEX.test(email)) {
      return false;
    }

    // Check for specific domains
    const validDomains = ["gmail.com", "outlook.com", "hotmail.com", "yahoo.com"];
    const emailParts = email.split('@');
    
    if (emailParts.length !== 2 || !validDomains.includes(emailParts[1].toLowerCase())) {
      return false;
    }

    return true;
  };

  const handleReset = async (e) => {
    setErrorMessage("");
    setSuccessMessage("");
    e.preventDefault();

    if (!validateEmail(email)) {
      setErrorMessage("Please enter a valid email from gmail.com, outlook.com, hotmail.com, or yahoo.com");
      return;
    }

    const result = await requestPasswordReset(email);

    if (result === "Password reset request failed") {
      setErrorMessage(result);
    } else {
      setErrorMessage("");
      setSuccessMessage("If your account exists, a reset link will be sent to your email");
    }
  }

  return (
    <>
      <Navbar />
      <div className="container my-3 py-3">
        <h1 className="text-center">Reset Password</h1>
        <hr />
        {successMessage !== "" && (
          <div className="alert alert-success text-center" role="alert">
            {successMessage}
          </div>
        )}
        {errorMessage !== "" && (
          <div className="alert alert-danger text-center" role="alert">
            {errorMessage}
          </div>
        )}
        <div className="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
            <form>
              <div className="my-3">
                <label htmlFor="floatingInput">Email address</label>
                <input
                  type="email"
                  className="form-control"
                  id="floatingInput"
                  placeholder="name@example.com"
                  onChange={handleChangeEmail}
                />
                <small className="form-text text-muted">
                  Please enter the email you used for your account.
                </small>
              </div>
              <div className="text-center">
                <button className="my-2 mx-auto btn btn-dark" onClick={handleReset} type="submit" disabled={email == null || email == ""}>
                    Send Email
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

export default ForgetPassword;