import React, { useState, useEffect } from "react";
import { Footer, Navbar } from "../components";
import { getUser, updateProfile } from "../utils/UserRoutes";

const Profile = () => {
  const [data, setData] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    username: "",
    email: "",
  });

  useEffect(() => {
    const handleProfile = async () => {
      const result = await getUser(localStorage.getItem("token"));
      setData(result);
      setFormData(result);
    };

    handleProfile();
  }, []);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    await updateProfile(localStorage.getItem("token"), formData);
    const freshUser = await getUser(localStorage.getItem("token"));
    setData(freshUser);
    setIsEditing(false);
  };

  return (
    <>
      <Navbar />
      <div className="container my-3 py-3">
        <h1 className="text-center">My Profile</h1>
        <hr />
        <div className="row my-4 h-100">
          <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
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
                  className="my-2 mx-auto btn btn-danger"
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
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default Profile;
