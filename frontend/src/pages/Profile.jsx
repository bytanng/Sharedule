import React, { useState, useEffect } from "react";
import { Footer, Navbar } from "../components";
import { getUser,uploadImage,updateProfile } from "../utils/UserRoutes";

const Profile = () => {
  const [data, setData] = useState("");
  const [imageUrl, setImageUrl] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);

  

  useEffect(() => {
    const handleProfile = async () => {
      const result = await getUser();
      setData(result);
      setSelectedFile(result.displayPicture);
      setImageUrl(result.displayPicture);

    };
    
    handleProfile();
  }, []);

  const handleEditProfile = async () => {
    let fileUrl = imageUrl; // Keep previous image if no new file is uploaded

    // Only upload if user selected a new file
    if (selectedFile) {
        fileUrl = await uploadImage(selectedFile);
        setImageUrl(fileUrl);
    }

    const profileData = {
        username: data.username,
        email: data.email,
        displayPicture: fileUrl, // Send S3 URL
    };

    let token = localStorage.getItem("token");
    const response = await updateProfile(token, profileData);

    alert("Profile updated");
};

  
  const handleUploadImage = (event) => {
    const file = event.target.files[0];

    if (!file) {
        console.error("No file selected");
        return;
    }

    setSelectedFile(file); // Store file temporarily
    setImageUrl(URL.createObjectURL(file)); // Show preview
};

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
                    accept="image/*"
                    style={{ display: "none" }}
                    onChange={handleUploadImage} // Ensure function is correctly defined
                />

                {/* Upload Button */}
                <label 
                    className="btn btn-secondary col-5 d-block align-items-center" 
                    htmlFor="fileInput"
                >
                    Upload Image
                </label>
            </div>
            <div className="my-3">
              <label for="display-4">Username</label>
              <p>{data.username}</p>
            </div>
            <div className="my-3">
              <label for="floatingPassword display-4">Email</label>
              <p>{data.email}</p>
            </div>
            <div className="text-center">
              <button
                className="my-2 mx-auto btn btn-dark"
                type="submit"
                onClick={handleEditProfile}
              >
                Edit Profile
              </button>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default Profile;
