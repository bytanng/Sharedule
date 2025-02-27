import React, { useState, useEffect } from "react";
import { Footer, Navbar } from "../components";
import { getUser } from "../utils/UserRoutes";

const Profile = () => {
  const [data, setData] = useState("");

  useEffect(() => {
    const handleProfile = async () => {
      const result = await getUser();
      setData(result);
    };

    handleProfile();
  }, []);

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
                disabled
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
