import { API_URL, POST_METHOD, GET_METHOD } from "./Constants";

export const createItem = async (itemData) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("No authentication token found");
    }

    const response = await fetch(`${API_URL}/create-item`, {
      method: POST_METHOD,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(itemData),
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(errorData);
    }

    const data = await response.json();
    return { success: true, data };
  } catch (error) {
    return {
      success: false,
      error: error.message || "Failed to create item",
    };
  }
};

export const getUserItems = async () => {
  const token = localStorage.getItem("token"); // Retrieve token from localStorage

  if (!token) {
    console.error("No authentication token found");
    return null;
  }

  try {
    const response = await fetch(`${API_URL}/items`, {
      method: GET_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.status} - ${await response.text()}`);
    }

    return await response.json();
  } catch (error) {
    console.error("Failed to fetch user items:", error.message);
    return null;
  }
};

export const getItem = async (id) => {
  const token = localStorage.getItem("token");

  const response = await fetch(`${API_URL}/item/${id}`, {
    method: GET_METHOD,
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    credentials: "include",
  });

  return await response.json();
};

export const searchItems = async (query) => {
  const token = localStorage.getItem("token");

  try {
    const response = await fetch(`${API_URL}/items/search?query=${query}`, {
      method: GET_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();

    // Ensure it's always an array
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("Error fetching search results:", error);
    return []; // Return an empty array on error
  }
};

export const getAvailItems = async () => {
  try {
    const response = await fetch(`${API_URL}/products`, {
      method: GET_METHOD,
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.status} - ${await response.text()}`);
    }

    const data = await response.json();

    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("Failed to fetch user items:", error.message);
    return null;
  }
};

export const searchAvailItems = async (query) => {
  try {
    const response = await fetch(`${API_URL}/products/search?query=${query}`, {
      method: GET_METHOD,
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();

    // Ensure it's always an array
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("Error fetching search results:", error);
    return []; // Return an empty array on error
  }
};
