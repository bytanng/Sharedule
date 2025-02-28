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

    const successPrefix = "Profile successfully updated: ";

    const data = await response.text();

    if (!data.startsWith(successPrefix)) {
      throw new Error();
    }

    localStorage.setItem("token", data.slice(successPrefix.length));
  } catch (error) {
    return "Profile update failed";
  }
};

export const deleteAccount = async (token) => {
  try {
    const response = await fetch(`${API_URL}/user/delete`, {
      method: DELETE_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        Confirmation: "CONFIRM_DELETE",
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
