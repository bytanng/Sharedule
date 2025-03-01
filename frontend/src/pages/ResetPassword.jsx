import React, { useState } from 'react'
import { resetPassword } from '../utils/UserRoutes'
import { useParams } from "react-router-dom";
import { Footer } from '../components';

function ResetPassword() {

  const { token } = useParams();
  const [newPassword, setNewPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  
  const handleChangeNewPassword = (e) => {
    var newPassword = e.target.value;

    if (newPassword != "") {
        setNewPassword(newPassword);
    }
  }

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");

    const resetPasswordObject = {
        resetToken: token,
        newPassword: newPassword
    }
    
    const result = await resetPassword(resetPasswordObject);

    if (result === "Password reset failed") {
        setErrorMessage(result);
      } else {
        setErrorMessage("");
        setSuccessMessage("Password successfully reset");
      }
  }

  return (
    <>
      <div className="container my-3 py-3">
        <h1 className="text-center">Reset Password</h1>
        <hr />
        {errorMessage != "" && (
          <div>
            {errorMessage}
          </div>
        )}
        {successMessage != "" && (
          <div>
            {successMessage}
          </div>
        )}
        <div class="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
            <form>
              <div class="my-3">
                <label for="display-4">New Password</label>
                <input
                  type="text"
                  class="form-control"
                  id="floatingInput"
                  onChange={handleChangeNewPassword}
                />
              </div>
              <div className="text-center">
                <button class="my-2 mx-auto btn btn-dark" onClick={handleResetPassword} type="submit" disabled={newPassword == null || newPassword == ""}>
                    Reset
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <Footer />
    </>
  )
}

export default ResetPassword
