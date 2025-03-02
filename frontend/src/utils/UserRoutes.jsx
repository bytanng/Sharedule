import {
  API_URL,
  POST_METHOD,
  PUT_METHOD,
  GET_METHOD,
  DELETE_METHOD,
} from "./Constants";

export const register = async (user) => {
  try {
    const response = await fetch(`${API_URL}/register`, {
      method: POST_METHOD,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user),
      credentials: "include",
    });

    const data = await response.text();

    if (!data.includes("User successfully registered")) {
      throw new Error(data);
    }

    return data;
  } catch (error) {
    return "Registration failed: " + error.message;
  }
};

export const login = async (user) => {
  try {
    const response = await fetch(`${API_URL}/login`, {
      method: POST_METHOD,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(user),
      credentials: "include",
    });

    const data = await response.text();

    if (data.startsWith("Authentication error: ")) {
      throw new Error();
    }

    localStorage.setItem("token", data);
    return data;
  } catch (error) {
    return "Login failed";
  }
};

export const logout = async (token) => {
  try {
    const response = await fetch(`${API_URL}/logout`, {
      method: POST_METHOD,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    
    // Trigger storage event for components to react
    window.dispatchEvent(new Event('storage'));
    
    return await response.text();
  } catch (error) {
    return "Logout failed";
  }
};

export const updateProfile = async (token, profile) => {
  try {
    const response = await fetch(`${API_URL}/user/profile`, {
      method: PUT_METHOD,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(profile),
    });

    const data = await response.text();
    console.log("Profile update response:", data); // Debug log to see exact response
    const successPrefix = "Profile successfully updated: ";

    if (data.startsWith(successPrefix)) {
      // Success case - update token and return success
      localStorage.setItem("token", data.slice(successPrefix.length));
      return "Profile updated successfully";
    } else {
      // Handle specific error messages from backend
      if (data.includes("Username is already taken")) {
        return "Username is already taken";
      } else if (data.includes("Email is already taken") || data.includes("This email is already registered")) {
        return "Email is already in use";
      } else if (data.includes("Invalid email format") || data.includes("Email domain not supported")) {
        return data; // Return the exact error message for email validation issues
      } else {
        // Generic error for other cases
        console.error("Unhandled profile update error:", data);
        return "Profile update failed: " + data;
      }
    }
  } catch (error) {
    console.error("Profile update error:", error);
    return "Profile update failed";
  }
};

export const deleteAccount = async (token,confirmation) => {
  try {
    const response = await fetch(`${API_URL}/user/delete`, {
      method: DELETE_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        Confirmation: confirmation,
      },
    });
    const data = await response.text();
    localStorage.removeItem("token");
    return data;
  } catch (error) {
    return "Account deletion failed";
  }
};

export const getAllUsers = async () => {
  try {
    const response = await fetch(`${API_URL}/users`, {
      method: GET_METHOD,
      headers: { "Content-Type": "application/json" },
    });
    return await response.json();
  } catch (error) {
    return "Failed to fetch users";
  }
};

export const getUser = async (token) => {
  try {
    const response = await fetch(`${API_URL}/profile`, {
      method: GET_METHOD,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      credentials: "include",
    });

    return await response.json();
  } catch (error) {
    return "Failed to fetch user";
  }
};

export const uploadImage = async (file) => {
  try {
    const formData = new FormData();
    formData.append("file", file); // Attach the file to FormData

    const response = await fetch(`${API_URL}/file/upload`, {
      method: "POST",
      headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: formData,
      credentials: "include",
  });

  if (!response.ok) {
      throw new Error(`Failed to upload image: ${await response.text()}`);
  }

    return await response.text(); // Backend returns the S3 URL as plain text
  } catch (error) {
    console.error("Upload Error:", error);
    return "Failed to upload image";
  }
};

export const requestPasswordReset = async (email) => {
  try {
    const response = await fetch(`${API_URL}/user/reset-password`, {
      method: POST_METHOD,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email }),
    });
    return await response.text();
  } catch (error) {
    return "Password reset request failed";
  }
};

export const resetPassword = async (resetPasswordDTO) => {
  try {
    const response = await fetch(`${API_URL}/user/reset-password`, {
      method: PUT_METHOD,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(resetPasswordDTO),
    });
    return await response.text();
  } catch (error) {
    return "Password reset failed";
  }
};
