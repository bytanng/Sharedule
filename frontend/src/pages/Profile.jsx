import React, { useState } from 'react'
import { Footer, Navbar } from "../components";
import { Link } from 'react-router-dom';
import { updateProfile } from "../utils/UserRoutes"

const Profile = () => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [loading, setLoading] = useState(false);
    const [image, setImage] = useState(null);

    const handleUpdateProfile = async (e) => {
        e.preventDefault();

        const user = { username, email, password ,image};

        setLoading(true);
        
        let token = localStorage.getItem("token");
        console.log(token);
        const result = await updateProfile(user);
        
        console.log(result)
        setLoading(false);

    };

    const handleUploadImage = (event) => {
        const file = event.target.files[0]; // Get selected file
        if (file) {
          setImage(URL.createObjectURL(file)); // Create preview URL
        }
    };

    return (
        <>
            <Navbar />
            <div className="container my-3 py-3">
                <h1 className="text-center">My Profile</h1>
                <hr />
                <div class="row my-4 h-100">
                    <div className="col-md-4 col-lg-4 col-sm-8 mx-auto">
                        <form onSubmit = {handleUpdateProfile}>
                            <div className="form my-3">
                                <img src={image} class="img-thumbnail rounded mx-auto my-2 d-block" alt='DP'></img>
                                <input
                                    type="file"
                                    id="fileInput"
                                    accept="image/*"
                                    style={{ display: "none" }}
                                    onChange={handleUploadImage}
                                />
                                <label className="btn btn-secondary col-5 d-block align-items-center" htmlFor="fileInput">
                                    Upload Image
                                </label>
                            </div>
                            <div className="form my-3">
                                <label for="Name">Username</label>
                                <input
                                    type="text"
                                    class="form-control"
                                    id="Name"
                                    placeholder="Enter Your Username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                />
                            </div>
                            <div className="form my-3">
                                <label for="Email">Email address</label>
                                <input
                                    type="email"
                                    class="form-control"
                                    id="Email"
                                    placeholder="name@example.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                />
                            </div>
                            <div className="form  my-3">
                                <label for="Password">Password</label>
                                <input
                                    type="password"
                                    class="form-control"
                                    id="Password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                            </div>
                            {errorMessage && <p className="text-danger">{errorMessage}</p>}
                            <div className="text-center">
                                <button class="my-2 mx-auto btn btn-dark" type="submit" disabled={loading}>
                                {loading ? "Registering..." : "Update"}
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

export default Profile