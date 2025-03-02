import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Footer, Navbar } from "../components";
import { getUser,uploadImage,updateProfile,deleteAccount } from "../utils/UserRoutes";
import DeleteModal from "../components/DeleteModal";

const Profile = () => {
  const [data, setData] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    displayPicture:null
  });
  const [imageUrl, setImageUrl] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isModalOpen, setModalOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [originalFormData, setOriginalFormData] = useState({});
  const [hasChanges, setHasChanges] = useState(false);

  const navigate = useNavigate();
  
  useEffect(() => {
    const handleProfile = async () => {
      const result = await getUser(localStorage.getItem("token"));
      setData(result);
      setFormData(result);
      setOriginalFormData(result);
      setSelectedFile(result.displayPicture);
      setImageUrl(result.displayPicture);
    };

    handleProfile();
  }, []);

  // Check if form data has changed
  useEffect(() => {
    if (isEditing) {
      const changed = 
        formData.username !== originalFormData.username ||
        formData.email !== originalFormData.email ||
        formData.displayPicture !== originalFormData.displayPicture;
      
      setHasChanges(changed);
    }
  }, [formData, originalFormData, isEditing]);

  const handleEdit = () => {
    setIsEditing(true);
    setErrorMessage("");
    setSuccessMessage("");
  };

  const handleChange = async (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrorMessage("");
  };

  const validateForm = () => {
    // Username validation
    if (formData.username.length < 6 || formData.username.length > 20) {
      setErrorMessage("Username must be between 6 and 20 characters");
      return false;
    }

    // Email validation
    const validDomains = ["gmail.com", "outlook.com", "hotmail.com", "yahoo.com"];
    const emailParts = formData.email.split('@');
    
    if (emailParts.length !== 2 || !validDomains.includes(emailParts[1].toLowerCase())) {
      setErrorMessage("Email must be from gmail.com, outlook.com, hotmail.com, or yahoo.com");
      return false;
    }

    return true;
  };

  const handleSubmit = async () => {
    // Clear previous messages
    setErrorMessage("");
    setSuccessMessage("");

    // Check if there are any changes
    if (!hasChanges) {
      setErrorMessage("No changes detected");
      return;
    }

    // Validate form
    if (!validateForm()) {
      return;
    }

    try {
      const result = await updateProfile(localStorage.getItem("token"), formData);
      
      if (result === "Profile updated successfully") {
        const freshUser = await getUser(localStorage.getItem("token"));
        setData(freshUser);
        setOriginalFormData(freshUser);
        setIsEditing(false);
        setSuccessMessage("Profile updated successfully!");
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          setSuccessMessage("");
        }, 3000);
      } else {
        // Display specific error message from the backend
        setErrorMessage(result);
      }
    } catch (error) {
      setErrorMessage("Failed to update profile. Please try again.");
    }
  };

  
  const handleUploadImage = async (event) => {
    const file = event.target.files[0];
    if (!file) {
        console.error("No file selected");
        return;
    }

    setSelectedFile(file); // Store file temporarily
    setImageUrl(URL.createObjectURL(file)); // Show preview immediately

    try {
        const uploadedImageUrl = await uploadImage(file);
        setImageUrl(uploadedImageUrl);
        setFormData((prevFormData) => ({
            ...prevFormData,
            displayPicture: uploadedImageUrl
        }));
    } catch (error) {
        console.error("Error uploading image:", error);
    }
  };


  const handleDeleteClick = () => {
    setModalOpen(true);
  };

  const handleConfirmDelete = () => {
    deleteAccount(localStorage.getItem("token"), "CONFIRM_DELETE");    
    setModalOpen(false);
    alert("Account deleted")
    navigate("/");
  }
  return (
    <>
      <Navbar />
      <div className="container my-3 py-3">
        <h1 className="text-center">My Profile</h1>
        <hr />
        {successMessage && (
          <div className="alert alert-success text-center" role="alert">
            {successMessage}
          </div>
        )}
        {errorMessage && (
          <div className="alert alert-danger text-center" role="alert">
            {errorMessage}
          </div>
        )}
        <div className="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
            <div className="form my-3">
                {/* Display the uploaded image */}
                {selectedFile && (
                    <img 
                        src={imageUrl} 
                        className="img-thumbnail rounded mx-auto my-2 d-block" 
                        alt="Profile"
                    />
                )}
                {/* File Input (Hidden) */}
                <input
                    type="file"
                    id="fileInput"
                    name="displayPicture"
                    accept="image/*"
                    style={{ display: "none" }}
                    onChange={handleUploadImage}
                />
                {/* Upload Button */}
                <label 
                    className={`btn btn-secondary col-5 d-block align-items-center ${!isEditing ? 'disabled' : ''}`}
                    htmlFor={isEditing ? "fileInput" : ""}
                    style={{ cursor: isEditing ? 'pointer' : 'not-allowed' }}
                >
                    Change Image
                </label>
            </div>
            <div className="my-3">
              <label for="display-4">Username</label>
              {isEditing ? (
                <input
                  type="text"
                  className="form-control"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                />
              ) : (
                <p>{data.username}</p>
              )}
            </div>
            <div className="my-3">
              <label for="floatingPassword display-4">Email</label>
              {isEditing ? (
                <input
                  type="email"
                  className="form-control"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                />
              ) : (
                <p>{data.email}</p>
              )}
            </div>
            <div className="my-3">
              <Link 
                to="/forgetpassword" 
                className="text-decoration-underline text-info"
              >
                Change Password
              </Link>
            </div>
            <div className="text-center">
              {isEditing ? (
                <button
                  className="my-2 mx-auto btn btn-primary"
                  onClick={handleSubmit}
                >
                  Submit
                </button>
              ) : (
                <button
                  className="my-2 mx-auto btn btn-dark"
                  onClick={handleEdit}
                >
                  Edit Profile
                </button>
              )}
              <button
                className="my-2 ms-2 btn btn-danger"
                onClick={handleDeleteClick}
              >
                Delete Account
              </button>
            </div>
          </div>
        </div>
      </div>
      <DeleteModal 
        isOpen={isModalOpen} 
        onClose={() => setModalOpen(false)} 
        onConfirm={handleConfirmDelete} 
      />
      <Footer />
    </>
  );
};

export default Profile;
