import React, { useState } from 'react'
import { resetPassword } from '../utils/UserRoutes'
import { useParams, useNavigate } from "react-router-dom";
import { Footer } from '../components';

function ResetPassword() {
  const navigate = useNavigate();
  const { token } = useParams();
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  
  const handleChangeNewPassword = (e) => {
    setNewPassword(e.target.value);
  }

  const handleChangeConfirmPassword = (e) => {
    setConfirmPassword(e.target.value);
  }

  const validatePassword = () => {
    if (newPassword.length < 3) {
      setErrorMessage("Password must be at least 3 characters long");
      return false;
    }
    
    if (newPassword !== confirmPassword) {
      setErrorMessage("Passwords do not match");
      return false;
    }
    
    return true;
  }

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");

    if (!validatePassword()) {
      return;
    }

    // Create the object structure expected by the backend
    const resetPasswordDTO = {
      resetToken: token,
      newPassword: newPassword
    };
    
    try {
      const result = await resetPassword(resetPasswordDTO);

      if (result === "Password reset failed" || result.includes("error")) {
        setErrorMessage(result);
      } else {
        setSuccessMessage("Password successfully reset");
        // Redirect to login page after 3 seconds
        setTimeout(() => {
          navigate("/login");
        }, 3000);
      }
    } catch (error) {
      setErrorMessage("An unexpected error occurred");
    }
  }

  return (
    <>
      <div className="container my-3 py-3">
        <h1 className="text-center">Reset Password</h1>
        <hr />
        {errorMessage !== "" && (
          <div className="alert alert-danger text-center" role="alert">
            {errorMessage}
          </div>
        )}
        {successMessage !== "" && (
          <div className="alert alert-success text-center" role="alert">
            {successMessage}
          </div>
        )}
        <div className="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
            <form>
              <div className="my-3">
                <label htmlFor="newPassword">New Password</label>
                <input
                  className="form-control"
                  id="newPassword"
                  placeholder="Enter your new password"
                  type="password"
                  onChange={handleChangeNewPassword}
                />
                <small className="form-text text-muted">
                  Password must be at least 3 characters long
                </small>
              </div>
              <div className="my-3">
                <label htmlFor="confirmPassword">Confirm Password</label>
                <input
                  className="form-control"
                  id="confirmPassword"
                  placeholder="Confirm your new password"
                  type="password"
                  onChange={handleChangeConfirmPassword}
                />
              </div>
              <div className="text-center">
                <button 
                  className="my-2 mx-auto btn btn-dark" 
                  onClick={handleResetPassword} 
                  type="submit" 
                  disabled={!newPassword || !confirmPassword}
                >
                  Reset Password
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
