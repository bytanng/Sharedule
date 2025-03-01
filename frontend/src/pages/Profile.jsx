import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
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

  const navigate = useNavigate();
  
  useEffect(() => {
    const handleProfile = async () => {
      const result = await getUser(localStorage.getItem("token"));
      setData(result);
      setFormData(result);
      setSelectedFile(result.displayPicture);
      setImageUrl(result.displayPicture);
    };

    
    
    handleProfile();
  }, []);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleChange = async (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    await updateProfile(localStorage.getItem("token"), formData);
    const freshUser = await getUser(localStorage.getItem("token"));
    setData(freshUser);
    setIsEditing(false);
    alert("Profile updated");

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
                    className="btn btn-secondary col-5 d-block align-items-center" 
                    htmlFor="fileInput"
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
