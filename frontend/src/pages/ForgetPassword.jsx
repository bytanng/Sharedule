import React, {useState} from "react";
import { Link } from "react-router-dom";
import { Footer, Navbar } from "../components";
import { requestPasswordReset } from "../utils/UserRoutes";
import { EMAIL_REGEX } from "../utils/Constants";

const ForgetPassword = () => {

  const [email, setEmail] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");

  const handleChangeEmail = (e) => {
    setEmail(e.target.value);
  } 

  const handleReset = async (e) => {
    e.preventDefault();

    if (!EMAIL_REGEX.test(email)) {
      setErrorMessage("Please enter a valid email");
      return;
    }

    const result = await requestPasswordReset(email);

    if (result === "Password reset request failed") {
      setErrorMessage(result);
    } else {
      setErrorMessage("");
    }
  }

  return (
    <>
      <Navbar />
      <div className="container my-3 py-3">
        <h1 className="text-center">Reset Password</h1>
        <hr />
        {errorMessage != "" && (
          <div>
            {errorMessage}
          </div>
        )}
        <div class="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
            <form>
              <div class="my-3">
                <label for="display-4">Email address</label>
                <input
                  type="email"
                  class="form-control"
                  id="floatingInput"
                  placeholder="name@example.com"
                  onChange={handleChangeEmail}
                />
              </div>
              <div className="text-center">
                <button class="my-2 mx-auto btn btn-dark" onClick={handleReset} type="submit" disabled={email != null || email != ""}>
                    Reset
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